package io.circe.magnolia.configured

import io.circe.magnolia.DerivationError
import io.circe._
import io.circe.magnolia.configured.ConfiguredSemiautoDerivedSuite.{DefaultConfig, KebabCase, Lenient, SnakeCaseAndDiscriminator, Strict, WithDefaultValue}
import io.circe.tests.CirceSuite
import io.circe.tests.examples.{Bar, ClassWithDefaults, ClassWithJsonKey, NonProfit, Organization, Public}
import org.scalatest.Inside
import io.circe.parser.parse
import io.circe.magnolia.configured.decoder.semiauto.deriveConfiguredMagnoliaDecoder
import io.circe.magnolia.configured.encoder.semiauto.deriveConfiguredMagnoliaEncoder

class ConfiguredSemiautoDerivedSuite extends CirceSuite with Inside {

  it should "have Snake case member and constructor names configuration" in {
    val obj = Public("X", "high")
    val json =
      parse("""
        {
          "type": "public",
          "name": "X",
          "tax_category": "high"
        }
      """)
    assert(SnakeCaseAndDiscriminator.encoder(obj).asRight[Throwable] == json)
    assert(json.flatMap(j => SnakeCaseAndDiscriminator.decoder(j.hcursor)) == Right(obj))
  }

  it should "have Kebab case member and constructor names configuration" in {
    val obj = NonProfit("X")
    val json =
      parse("""
        {
          "non-profit": {
            "org-name": "X"
          }
        }
      """)
    assert(KebabCase.encoder(obj).asRight[Throwable] == json)
    assert(json.flatMap(j => KebabCase.decoder(j.hcursor)) == Right(obj))
  }

  it should "return error message when discriminator key is missing" in {
    val input =
      parse("""
        {
          "name": "X",
          "tax_category": "high"
        }
      """)
    inside(input.flatMap(i => SnakeCaseAndDiscriminator.decoder(i.hcursor))) {
      case Left(e: DecodingFailure) => {
        assert(e.message.contains("couldn't find discriminator or is not of type String."))
        assert(e.history.isEmpty)
      }
      case x => fail(x.toString)
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
    """)

    inside(input.flatMap(i => SnakeCaseAndDiscriminator.decoder(i.hcursor))) {
      case Left(e: DecodingFailure) => {
        assert(e.message.contains("couldn't find discriminator or is not of type String."))
        assert(e.history.isEmpty)
      }
      case x => fail(x.toString)
    }
  }

  it should "return error message when the type key is not found (non-descriminated Decoder) " in {
    val input = Json.obj("bad" -> Json.Null)

    inside(DefaultConfig.decoder(input.hcursor)) {
      case Left(e) => {
        assert(e.message.contains("Can't decode coproduct type: couldn't find matching subtype"))
        assert(e.history.isEmpty)
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
    """)

    inside(input.flatMap(i => SnakeCaseAndDiscriminator.decoder(i.hcursor))) {
      case Left(e: DecodingFailure) => {
        assert(e.message.contains("constructor name not found in known constructor names"))
        assert(e.message.contains("non_profit,public"))
      }
      case x => fail(x.toString)
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
      """)

    assert(DefaultConfig.encoder(obj).asRight[Throwable] == expectedJson)
    assert(expectedJson.flatMap(j => DefaultConfig.decoder(j.hcursor)) == Right(obj))

  }

  it should "use JsonKey annotated name when encoding and decoding, taking precedence over any other transformation" in {
    implicit val config = Configuration.default.withSnakeCaseMemberNames
    val encoder         = deriveConfiguredMagnoliaEncoder[ClassWithJsonKey]
    val decoder         = deriveConfiguredMagnoliaDecoder[ClassWithJsonKey]

    val jsonResult = parse("""
         {
           "Renamed": "value",
           "another_field": "another"
         }
      """)

    val expected = ClassWithJsonKey("value", "another")
    assert(jsonResult.flatMap(j => decoder.apply(j.hcursor)) == Right(expected))
    assert(encoder.apply(expected).asRight[Throwable] == jsonResult)
  }

  "Configuration#useDefaults" should "Use the parameter default value if key does not exist in JSON" in {
    assert(
      parse("""{"required": "req"}""").flatMap(j => WithDefaultValue.decoder(j.hcursor)) ==
        Right(ClassWithDefaults(required = "req", defaultOptNotSpecified = None))
    )
  }

  "Configuration#useDefaults" should "decode parameter if the key exists" in {
    val input =
      parse("""
      {
        "required": "req",
        "field": "provided",
        "defaultOptSome": "provided1",
        "defaultNone": "provided2",
        "defaultOptNotSpecified": "provided3"
      }
    """)
    val expected =
      ClassWithDefaults(
        required               = "req",
        field                  = "provided",
        defaultOptSome         = Some("provided1"),
        defaultNone            = Some("provided2"),
        defaultOptNotSpecified = Some("provided3"),
      )
    assert(input.flatMap(i => WithDefaultValue.decoder(i.hcursor)) == Right(expected))
  }

  "Configuration#useDefaults" should "Decode to None when key is found for a field of type Option[T], instead of using the default value" in {
    val input    = parse("""
      {
        "required": "req",
        "defaultOptSome": null
      }
    """)
    val expected = ClassWithDefaults(required = "req", defaultOptSome = None, defaultOptNotSpecified = None)
    assert(input.flatMap(i => WithDefaultValue.decoder(i.hcursor)) == Right(expected))
  }

  "Configuration#useDefaults" should "fail if key is missing and no default was provided for parameter" in {
    assert(parse("{}").flatMap(j => WithDefaultValue.decoder(j.hcursor)) == Left(
        DecodingFailure("Attempt to decode value on failed cursor", List(CursorOp.DownField("required")))
      )
    )
  }

  "Configuration#strictDecoding" should "Raise error when strict decoding enabled and extraneous key is found in JSON" in {
    val input = parse("""
      {
        "NonProfit": {
          "orgName": "RSPCA",
          "extraneous": true
        }
      }
    """)
    inside(input.flatMap(i => Strict.decoder(i.hcursor))) {
      case Left(e: DecodingFailure) => {
        assert(e.message.contains("Unexpected field"))
        assert(e.message.contains("extraneous"))
        assert(e.message.contains("orgName"))
      }
      case x => fail(x.toString)
    }
  }

  "Configuration#strictDecoding" should "Should not raise error when strict decoding is disabled and extraneous key is found in JSON" in {
    val input = parse("""
      {
        "NonProfit": {
          "orgName": "RSPCA",
          "extraneous": true
        }
      }
    """)
    val expected = NonProfit("RSPCA")
    assert(input.flatMap(i => Lenient.decoder(i.hcursor)) == Right(expected))
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
    implicit val config =
      Configuration.default.copy(transformConstructorNames = _ => "sameKey", discriminator = Some("type"))
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
      defaults.defaultGenericConfiguration.withSnakeCaseMemberNames.withSnakeCaseConstructorNames.withDiscriminator("type")

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

  object KebabCase {
    implicit val configuration: Configuration =
      Configuration.default.withKebabCaseConstructorNames.withKebabCaseMemberNames

    val encoder: Encoder[Organization] = deriveConfiguredMagnoliaEncoder[Organization]
    val decoder: Decoder[Organization] = deriveConfiguredMagnoliaDecoder[Organization]
  }

  object Strict {
    implicit val configuration: Configuration = Configuration.default.withStrictDecoding

    val encoder: Encoder[Organization] = deriveConfiguredMagnoliaEncoder[Organization]
    val decoder: Decoder[Organization] = deriveConfiguredMagnoliaDecoder[Organization]
  }

  object Lenient {
    implicit val configuration: Configuration = Configuration.default

    val encoder: Encoder[Organization] = deriveConfiguredMagnoliaEncoder[Organization]
    val decoder: Decoder[Organization] = deriveConfiguredMagnoliaDecoder[Organization]
  }

}
