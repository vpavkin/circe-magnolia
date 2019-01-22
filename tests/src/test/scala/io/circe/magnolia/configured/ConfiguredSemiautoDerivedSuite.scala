package io.circe.magnolia.configured

import io.circe.magnolia.DerivationError
import io.circe.{CursorOp, Decoder, DecodingFailure, Encoder}
import io.circe.magnolia.configured.ConfiguredSemiautoDerivedSuite.{DefaultConfig, SnakeCaseAndDiscriminator, WithDefaultValue}
import io.circe.tests.CirceSuite
import io.circe.tests.examples.{Bar, ClassWithDefaults, ClassWithJsonKey, NonProfit, Organization, Public}
import org.scalatest.Inside
import io.circe.parser.parse
import io.circe.magnolia.configured.decoder.semiauto.deriveConfiguredMagnoliaDecoder
import io.circe.magnolia.configured.encoder.semiauto.deriveConfiguredMagnoliaEncoder

class ConfiguredSemiautoDerivedSuite extends CirceSuite with Inside {

  it should "Snake case member and constructor names" in {
    val obj = Public("X", "high")
    val json =
      parse("""
        {
          "type": "public",
          "name": "X",
          "tax_category": "high"
        }
      """).right.get
    assert(SnakeCaseAndDiscriminator.encoder(obj) == json)
    assert(SnakeCaseAndDiscriminator.decoder(json.hcursor) == Right(obj))
  }

  it should "return error message when discriminator key is missing" in {
    val input =
      parse("""
        {
          "name": "X",
          "tax_category": "high"
        }
      """).right.get
    inside(SnakeCaseAndDiscriminator.decoder(input.hcursor)) {
      case Left(e) => {
        assert(e.message.contains("couldn't find discriminator or is not of type String."))
      }
    }
  }

  it should "return error message when discriminator key is not a String" in {

    val input =
      parse("""
      {
        "type": 1,
        "name": "X",
        "tax_category": "high"
      }
    """).right.get

    inside(SnakeCaseAndDiscriminator.decoder(input.hcursor)) {
      case Left(e) => {
        assert(e.message.contains("couldn't find discriminator or is not of type String."))
      }
    }
  }

  it should "return error message when discriminator key is not a known constructor value" in {
    val input =
      parse("""
      {
        "type": "not_known",
        "name": "X",
        "tax_category": "high"
      }
    """).right.get

    inside(SnakeCaseAndDiscriminator.decoder(input.hcursor)) {
      case Left(e) => {
        assert(e.message.contains("constructor name not found in known constructor names"))
        assert(e.message.contains("non_profit,public"))
      }
    }
  }

  it should "return camel case keys/constructor without discriminator for the default Configuration" in {
    val obj = NonProfit("RSPCA")
    val expectedJson =
      parse("""
        {
          "NonProfit": {
            "orgName": "RSPCA"
          }
        }
      """).right.get

    assert(DefaultConfig.encoder(obj) == expectedJson)
    assert(DefaultConfig.decoder(expectedJson.hcursor) == Right(obj))

  }

  it should "use JsonKey annotated name when encoding and decoding, taking precedence over any other transformation" in {
    implicit val config = Configuration.default.withSnakeCaseMemberNames
    val encoder = deriveConfiguredMagnoliaEncoder[ClassWithJsonKey]
    val decoder = deriveConfiguredMagnoliaDecoder[ClassWithJsonKey]

    val jsonResult = parse(
      """
         {
           "Renamed": "value",
           "another_field": "another"
         }
      """).right.get

    val expected = ClassWithJsonKey("value", "another")
    assert(decoder.apply(jsonResult.hcursor) == Right(expected))
    assert(encoder.apply(expected) == jsonResult)
  }

  "Configuration#useDefaults" should "Use the parameter default value if key does not exist in JSON" in {
    assert(WithDefaultValue.decoder(parse("""{"required": "req"}""").right.get.hcursor) == Right(ClassWithDefaults(required = "req")))
  }

  "Configuration#useDefaults" should "decode parameter if the key exists" in {
    val input =
      parse("""
      {
        "required": "req",
        "field": "provided",
        "defaultOptSome": "provided1",
        "defaultNone": "provided2"
      }
    """).right.get
    val expected =
      ClassWithDefaults(required = "req", field = "provided", defaultOptSome = Some("provided1"), defaultNone = Some("provided2"))
    assert(WithDefaultValue.decoder(input.hcursor) == Right(expected))
  }

  "Configuration#useDefaults" should "Decode to None when key is found for a field of type Option[T], instead of using the default value" in {
    val input = parse("""
      {
        "required": "req",
        "defaultOptSome": null
      }
    """).right.get
    val expected = ClassWithDefaults(required = "req", defaultOptSome = None)
    assert(WithDefaultValue.decoder(input.hcursor) == Right(expected))
  }

  "Configuration#useDefaults" should "fail if key is missing and no default was provided for parameter" in {
    assert(WithDefaultValue.decoder(parse("{}").right.get.hcursor) == Left(DecodingFailure("Attempt to decode value on failed cursor", List(CursorOp.DownField("required")))))
  }

  "Encoder derivation" should "fail if transforming parameter names has collisions" in {
    implicit val config: Configuration = Configuration.default.copy(transformMemberNames = _ => "sameKey")

    try {
      deriveConfiguredMagnoliaEncoder[Bar]
      fail("Expected exception not thrown")
    } catch {
      case e: DerivationError => assert(e.getMessage.contains("Duplicate key detected"))
    }
  }

  "Encoder derivation" should "fail if transformed sealed trait subtype constructor name has collisions" in {
    implicit val config = Configuration.default.copy(transformConstructorNames = _ => "sameKey")
    try {
      deriveConfiguredMagnoliaEncoder[Organization]
      fail("Expected exception not thrown")
    } catch {
      case e: DerivationError => assert(e.getMessage.contains("Duplicate key detected"))
    }
  }

  "Decoder derivation" should "fail if transforming parameter names results in duplicate JSON keys" in {
    implicit val config = Configuration.default.copy(transformMemberNames = _ => "sameKey")

    try {
      deriveConfiguredMagnoliaDecoder[Bar]
      fail("Expected exception not thrown")
    } catch {
      case e: DerivationError => assert(e.getMessage.contains("Duplicate key detected"))
    }
  }

  "Decoder derivation" should "fail if transformed sealed trait subtype constructor name has collisions if configured to use a discriminator" in {
    implicit val config = Configuration.default.copy(transformConstructorNames = _ => "sameKey", discriminator = Some("type"))
    try {
      deriveConfiguredMagnoliaDecoder[Organization]
      fail("Expected exception not thrown")
    } catch {
      case e: DerivationError => assert(e.getMessage.contains("Duplicate key detected"))
    }
  }

  "Decoder derivation" should "fail if transformed sealed trait subtype constructor name has collisions if configured to NOT use a discriminator" in {
    implicit val config = Configuration.default.copy(transformConstructorNames = _ => "sameKey", discriminator = None)
    try {
      deriveConfiguredMagnoliaDecoder[Organization]
      fail("Expected exception not thrown")
    } catch {
      case e: DerivationError => assert(e.getMessage.contains("Duplicate key detected"))
    }
  }

}

object ConfiguredSemiautoDerivedSuite {

  object SnakeCaseAndDiscriminator {
    implicit val configuration: Configuration =
      Configuration.default.withSnakeCaseMemberNames.withSnakeCaseConstructorNames.withDiscriminator("type")

    val encoder: Encoder[Organization] = deriveConfiguredMagnoliaEncoder[Organization]
    val decoder: Decoder[Organization] = deriveConfiguredMagnoliaDecoder[Organization]
  }

  object DefaultConfig {
    implicit val configuration: Configuration = Configuration.default

    val encoder: Encoder[Organization] = deriveConfiguredMagnoliaEncoder[Organization]
    val decoder: Decoder[Organization] = deriveConfiguredMagnoliaDecoder[Organization]
  }

  object WithDefaultValue {
    implicit val configuration: Configuration = Configuration.default.withDefaults

    val encoder: Encoder[ClassWithDefaults] = deriveConfiguredMagnoliaEncoder[ClassWithDefaults]
    val decoder: Decoder[ClassWithDefaults] = deriveConfiguredMagnoliaDecoder[ClassWithDefaults]
  }

}
