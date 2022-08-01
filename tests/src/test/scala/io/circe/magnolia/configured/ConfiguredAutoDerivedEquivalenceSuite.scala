package io.circe.magnolia.configured

import io.circe.tests.CirceSuite
import io.circe.tests.examples._
import io.circe.{Decoder, Encoder}
import io.circe.magnolia.tags.{Circe, Magnolia}
import io.circe.generic.extras.{Configuration => GeConfiguration}
import io.circe.magnolia.CodecEquivalenceTests
import io.circe.magnolia.AutoDerivedSuiteInputs._
import io.circe.magnolia.tags._
import io.circe.parser.parse
import shapeless.tag.@@
import shapeless.tag

class ConfiguredAutoDerivedEquivalenceSuite extends CirceSuite {

  private class MagnoliaCodecs(config: Configuration) {
    implicit val configuration: Configuration = config

    import io.circe.tests.examples.Baz._

    import io.circe.magnolia.configured.decoder.auto._
    import io.circe.magnolia.configured.encoder.auto._

    private implicit val encodeIntTag1: Encoder[List[Int] @@ Tag1] = Encoder[List[Int]].narrow
    private implicit val encodeStringTag2: Encoder[String @@ Tag2] = Encoder[String].narrow
    private implicit val decodeIntTag1: Decoder[List[Int] @@ Tag1] = Decoder[List[Int]].map(tag[Tag1](_))
    private implicit val decodeStringTag2: Decoder[String @@ Tag2] = Decoder[String].map(tag[Tag2](_))

    private implicit val encodeStringTag: Encoder[String @@ Tag] = Encoder[String].narrow
    private implicit val decodeStringTag: Decoder[String @@ Tag] = Decoder[String].map(tag[Tag](_))

    implicit val magnoliaEncoder1: TaggedEncoder[Magnolia, AnyValInside] = mkTag[Magnolia](Encoder[AnyValInside])
    implicit val magnoliaEncoder3: TaggedEncoder[Magnolia, Qux[Int]] = mkTag[Magnolia](Encoder[Qux[Int]])
    implicit val magnoliaEncoder4: TaggedEncoder[Magnolia, Seq[Foo]] = mkTag[Magnolia](Encoder[Seq[Foo]])
    implicit val magnoliaEncoder5: TaggedEncoder[Magnolia, Baz] = mkTag[Magnolia](Encoder[Baz])
    implicit val magnoliaEncoder6: TaggedEncoder[Magnolia, Foo] = mkTag[Magnolia](Encoder[Foo])
    implicit val magnoliaEncoderSealed: TaggedEncoder[Magnolia, Sealed] = mkTag[Magnolia](Encoder[Sealed])
    implicit val magnoliaEncoder7: TaggedEncoder[Magnolia, OuterCaseClassExample] = mkTag[Magnolia](Encoder[OuterCaseClassExample])
    implicit val magnoliaEncoder8: TaggedEncoder[Magnolia, RecursiveAdtExample] = mkTag[Magnolia](Encoder[RecursiveAdtExample])
    implicit val magnoliaEncoder9: TaggedEncoder[Magnolia, RecursiveWithOptionExample] = mkTag[Magnolia](Encoder[RecursiveWithOptionExample])
    implicit val magnoliaEncoder10: TaggedEncoder[Magnolia, WithTaggedMembers] = mkTag[Magnolia](Encoder[WithTaggedMembers])
    implicit val magnoliaEncoder11: TaggedEncoder[Magnolia, Seq[WithSeqOfTagged]] = mkTag[Magnolia](Encoder[Seq[WithSeqOfTagged]])
    implicit val magnoliaEncoder12: TaggedEncoder[Magnolia, RecursiveWithListExample] = mkTag[Magnolia](Encoder[RecursiveWithListExample])
    implicit val magnoliaEncoder13: TaggedEncoder[Magnolia, ClassWithDefaults] = mkTag[Magnolia](Encoder[ClassWithDefaults])
    implicit val magnoliaEncoder14: TaggedEncoder[Magnolia, ClassWithJsonKey] = mkTag[Magnolia](Encoder[ClassWithJsonKey])

    implicit val magnoliaDecoder1: TaggedDecoder[Magnolia, AnyValInside] = mkTag[Magnolia](Decoder[AnyValInside])
    implicit val magnoliaDecoder3: TaggedDecoder[Magnolia, Qux[Int]] = mkTag[Magnolia](Decoder[Qux[Int]])
    implicit val magnoliaDecoder4: TaggedDecoder[Magnolia, Seq[Foo]] = mkTag[Magnolia](Decoder[Seq[Foo]])
    implicit val magnoliaDecoder5: TaggedDecoder[Magnolia, Baz] = mkTag[Magnolia](Decoder[Baz])
    implicit val magnoliaDecoder6: TaggedDecoder[Magnolia, Foo] = mkTag[Magnolia](Decoder[Foo])
    implicit val magnoliaDecoderSealed: TaggedDecoder[Magnolia, Sealed] = mkTag[Magnolia](Decoder[Sealed])
    implicit val magnoliaDecoder7: TaggedDecoder[Magnolia, OuterCaseClassExample] = mkTag[Magnolia](Decoder[OuterCaseClassExample])
    implicit val magnoliaDecoder8: TaggedDecoder[Magnolia, RecursiveAdtExample] = mkTag[Magnolia](Decoder[RecursiveAdtExample])
    implicit val magnoliaDecoder9: TaggedDecoder[Magnolia, RecursiveWithOptionExample] = mkTag[Magnolia](Decoder[RecursiveWithOptionExample])
    implicit val magnoliaDecoder10: TaggedDecoder[Magnolia, WithTaggedMembers] = mkTag[Magnolia](Decoder[WithTaggedMembers])
    implicit val magnoliaDecoder11: TaggedDecoder[Magnolia, Seq[WithSeqOfTagged]] = mkTag[Magnolia](Decoder[Seq[WithSeqOfTagged]])
    implicit val magnoliaDecoder12: TaggedDecoder[Magnolia, RecursiveWithListExample] = mkTag[Magnolia](Decoder[RecursiveWithListExample])
    implicit val magnoliaDecoder13: TaggedDecoder[Magnolia, ClassWithDefaults] = mkTag[Magnolia](Decoder[ClassWithDefaults])
    implicit val magnoliaDecoder14: TaggedDecoder[Magnolia, ClassWithJsonKey] = mkTag[Magnolia](Decoder[ClassWithJsonKey])

  }

  private class CirceCodecs(config: GeConfiguration) {

    implicit val configuration: GeConfiguration = config
    import io.circe.generic.extras.auto._

    private implicit val encodeIntTag1: Encoder[List[Int] @@ Tag1] = Encoder[List[Int]].narrow
    private implicit val encodeStringTag2: Encoder[String @@ Tag2] = Encoder[String].narrow
    private implicit val decodeIntTag1: Decoder[List[Int] @@ Tag1] = Decoder[List[Int]].map(tag[Tag1](_))
    private implicit val decodeStringTag2: Decoder[String @@ Tag2] = Decoder[String].map(tag[Tag2](_))

    private implicit val encodeStringTag: Encoder[String @@ Tag] = Encoder[String].narrow
    private implicit val decodeStringTag: Decoder[String @@ Tag] = Decoder[String].map(tag[Tag](_))

    implicit val circeEncoder1: TaggedEncoder[Circe, AnyValInside] = mkTag[Circe](Encoder[AnyValInside])
    implicit val circeEncoder3: TaggedEncoder[Circe, Qux[Int]] = mkTag[Circe](Encoder[Qux[Int]])
    implicit val circeEncoder4: TaggedEncoder[Circe, Seq[Foo]] = mkTag[Circe](Encoder[Seq[Foo]])
    implicit val circeEncoder5: TaggedEncoder[Circe, Baz] = mkTag[Circe](Encoder[Baz])
    implicit val circeEncoder6: TaggedEncoder[Circe, Foo] = mkTag[Circe](Encoder[Foo])
    implicit val circeEncoderSealed: TaggedEncoder[Circe, Sealed] = mkTag[Circe](Encoder[Sealed])
    implicit val circeEncoder7: TaggedEncoder[Circe, OuterCaseClassExample] = mkTag[Circe](Encoder[OuterCaseClassExample])
    implicit val circeEncoder8: TaggedEncoder[Circe, RecursiveAdtExample] = mkTag[Circe](Encoder[RecursiveAdtExample])
    implicit val circeEncoder9: TaggedEncoder[Circe, RecursiveWithOptionExample] = mkTag[Circe](Encoder[RecursiveWithOptionExample])
    implicit val circeEncoder10: TaggedEncoder[Circe, WithTaggedMembers] = mkTag[Circe](Encoder[WithTaggedMembers])
    implicit val circeEncoder11: TaggedEncoder[Circe, Seq[WithSeqOfTagged]] = mkTag[Circe](Encoder[Seq[WithSeqOfTagged]])
    implicit val circeEncoder12: TaggedEncoder[Circe, RecursiveWithListExample] = mkTag[Circe](Encoder[RecursiveWithListExample])
    implicit val circeEncoder13: TaggedEncoder[Circe, ClassWithDefaults] = mkTag[Circe](Encoder[ClassWithDefaults])
    implicit val circeEncoder14: TaggedEncoder[Circe, ClassWithJsonKey] = mkTag[Circe](Encoder[ClassWithJsonKey])

    implicit val circeDecoder1: TaggedDecoder[Circe, AnyValInside] = mkTag[Circe](Decoder[AnyValInside])
    implicit val circeDecoder3: TaggedDecoder[Circe, Qux[Int]] = mkTag[Circe](Decoder[Qux[Int]])
    implicit val circeDecoder4: TaggedDecoder[Circe, Seq[Foo]] = mkTag[Circe](Decoder[Seq[Foo]])
    implicit val circeDecoder5: TaggedDecoder[Circe, Baz] = mkTag[Circe](Decoder[Baz])
    implicit val circeDecoder6: TaggedDecoder[Circe, Foo] = mkTag[Circe](Decoder[Foo])
    implicit val circeDecoderSealed: TaggedDecoder[Circe, Sealed] = mkTag[Circe](Decoder[Sealed])
    implicit val circeDecoder7: TaggedDecoder[Circe, OuterCaseClassExample] = mkTag[Circe](Decoder[OuterCaseClassExample])
    implicit val circeDecoder8: TaggedDecoder[Circe, RecursiveAdtExample] = mkTag[Circe](Decoder[RecursiveAdtExample])
    implicit val circeDecoder9: TaggedDecoder[Circe, RecursiveWithOptionExample] = mkTag[Circe](Decoder[RecursiveWithOptionExample])
    implicit val circeDecoder10: TaggedDecoder[Circe, WithTaggedMembers] = mkTag[Circe](Decoder[WithTaggedMembers])
    implicit val circeDecoder11: TaggedDecoder[Circe, Seq[WithSeqOfTagged]] = mkTag[Circe](Decoder[Seq[WithSeqOfTagged]])
    implicit val circeDecoder12: TaggedDecoder[Circe, RecursiveWithListExample] = mkTag[Circe](Decoder[RecursiveWithListExample])
    implicit val circeDecoder13: TaggedDecoder[Circe, ClassWithDefaults] = mkTag[Circe](Decoder[ClassWithDefaults])
    implicit val circeDecoder14: TaggedDecoder[Circe, ClassWithJsonKey] = mkTag[Circe](Decoder[ClassWithJsonKey])
  }

  def testWithConfiguration(postfix: String, configuration: Configuration): Unit = {
    val magnoliaCodecs = new MagnoliaCodecs(configuration)
    val circeCodecs = new CirceCodecs(toGenericExtrasConfig(configuration))

    import magnoliaCodecs._
    import circeCodecs._

    checkLaws(s"Codec[AnyValInside] $postfix", CodecEquivalenceTests.useTagged[AnyValInside].codecEquivalence)
    checkLaws(s"Codec[Qux[Int]] $postfix", CodecEquivalenceTests.useTagged[Qux[Int]].codecEquivalence)
    checkLaws(s"Codec[Seq[Foo]] $postfix", CodecEquivalenceTests.useTagged[Seq[Foo]].codecEquivalence)
    checkLaws(s"Codec[Baz] $postfix", CodecEquivalenceTests.useTagged[Baz].codecEquivalence)
    checkLaws(s"Codec[Foo] $postfix", CodecEquivalenceTests.useTagged[Foo].codecEquivalence)
    checkLaws(s"Codec[Sealed] $postfix", CodecEquivalenceTests.useTagged[Sealed].encoderEquivalence)
    checkLaws(s"Codec[OuterCaseClassExample] $postfix", CodecEquivalenceTests.useTagged[OuterCaseClassExample].codecEquivalence)
    checkLaws(s"Codec[RecursiveAdtExample] $postfix", CodecEquivalenceTests.useTagged[RecursiveAdtExample].codecEquivalence)
    checkLaws(s"Codec[RecursiveWithOptionExample] $postfix", CodecEquivalenceTests.useTagged[RecursiveWithOptionExample].codecEquivalence)
    // TODO: disabled due to https://github.com/circe/circe-magnolia/issues/3
    // checkLaws(s"Codec[WithTaggedMembers] $postfix", CodecEquivalenceTests.useTagged[WithTaggedMembers].codecEquivalence)
    checkLaws(s"Codec[Seq[WithSeqOfTagged]] $postfix", CodecEquivalenceTests.useTagged[Seq[WithSeqOfTagged]].codecEquivalence)
    checkLaws(s"Codec[RecursiveWithListExample] $postfix", CodecEquivalenceTests.useTagged[RecursiveWithListExample].codecEquivalence)
    checkLaws(s"Codec[ClassWithDefaults] $postfix", CodecEquivalenceTests.useTagged[ClassWithDefaults].codecEquivalence)
    checkLaws(s"Codec[ClassWithJsonKey] $postfix", CodecEquivalenceTests.useTagged[ClassWithJsonKey].codecEquivalence)
  }

  testWithConfiguration("with default configuration", Configuration.default)
  testWithConfiguration("with snake case configuration", Configuration.default.withSnakeCaseConstructorNames.withSnakeCaseMemberNames)
  testWithConfiguration("with useDefault = true", Configuration.default.copy(useDefaults = true))
  testWithConfiguration("with discriminator", Configuration.default.copy(discriminator = Some("type")))
  testWithConfiguration("with strict", Configuration.default.copy(strictDecoding = true))

  "If a sealed trait subtype has explicit Encoder instance that doesn't encode to a JsonObject, the derived encoder" should
    s"wrap it with type constructor even when discriminator is specified by the configuration" in {
    import io.circe.magnolia.configured.encoder.auto._
    implicit val config: Configuration = Configuration.default.withDiscriminator("type")
    val magnoliaEncoder = Encoder[Sealed]
    val expected = parse(
      """
          {
            "SubtypeWithExplicitInstance": ["1"]
          }
        """)
    assert(magnoliaEncoder.apply(SubtypeWithExplicitInstance(List("1"))).asRight[Throwable] == expected)
  }
}

