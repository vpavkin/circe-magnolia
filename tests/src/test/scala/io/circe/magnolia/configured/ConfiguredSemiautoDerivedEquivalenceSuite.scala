package io.circe.magnolia.configured

import io.circe.{Decoder, Encoder}
import io.circe.tests.CirceSuite
import io.circe.tests.examples._
import io.circe.magnolia.tags.{Circe, Magnolia}
import io.circe.generic.extras.{Configuration => GeConfiguration}
import io.circe.magnolia.CodecEquivalenceTests
import io.circe.magnolia.SemiautoDerivedSuiteInputs._
import io.circe.magnolia.tags._

class ConfiguredSemiautoDerivedEquivalenceSuite extends CirceSuite {

  private class MagnoliaCodecs(config: Configuration) {
    implicit val configuration: Configuration = config

    import io.circe.magnolia.configured.decoder.semiauto.deriveConfiguredMagnoliaDecoder
    import io.circe.magnolia.configured.encoder.semiauto.deriveConfiguredMagnoliaEncoder

    implicit val magnoliaEncoder3: TaggedEncoder[Magnolia, Box[Int]] = mkTag[Magnolia](deriveConfiguredMagnoliaEncoder[Box[Int]])
    implicit val magnoliaEncoder4: TaggedEncoder[Magnolia, Qux[Int]] = mkTag[Magnolia](deriveConfiguredMagnoliaEncoder[Qux[Int]])
    implicit val magnoliaEncoder6: TaggedEncoder[Magnolia, Baz] = mkTag[Magnolia](deriveConfiguredMagnoliaEncoder[Baz])
    private implicit val encodeWub: Encoder[Wub] = deriveConfiguredMagnoliaEncoder[Wub]
    implicit val magnoliaEncoder11: TaggedEncoder[Magnolia, Wub] = mkTag[Magnolia](encodeWub)
    implicit val magnoliaEncoder10: TaggedEncoder[Magnolia, Bam] = mkTag[Magnolia](deriveConfiguredMagnoliaEncoder[Bam])
    private implicit val encodeFoo: Encoder[Foo] = deriveConfiguredMagnoliaEncoder[Foo]
    implicit val magnoliaEncoder7: TaggedEncoder[Magnolia, Foo] = mkTag[Magnolia](encodeFoo)
    implicit val magnoliaEncoder20: TaggedEncoder[Magnolia, Seq[Foo]] = mkTag[Magnolia](Encoder.encodeSeq(magnoliaEncoder7.inner))
    private implicit val encodeRecursiveAdtExample: Encoder[RecursiveAdtExample] = deriveConfiguredMagnoliaEncoder[RecursiveAdtExample]
    implicit val magnoliaEncoder8: TaggedEncoder[Magnolia, RecursiveAdtExample] = mkTag[Magnolia](encodeRecursiveAdtExample)
    private implicit  val encodeRecursiveWithOptionExample: Encoder[RecursiveWithOptionExample] = deriveConfiguredMagnoliaEncoder[RecursiveWithOptionExample]
    implicit val magnoliaEncoder9: TaggedEncoder[Magnolia, RecursiveWithOptionExample] = mkTag[Magnolia](encodeRecursiveWithOptionExample)
    implicit val magnoliaEncoder1: TaggedEncoder[Magnolia, AnyValInside] = mkTag[Magnolia](deriveConfiguredMagnoliaEncoder[AnyValInside])
    implicit val magnoliaEncoder13: TaggedEncoder[Magnolia, ClassWithDefaults] = mkTag[Magnolia](deriveConfiguredMagnoliaEncoder[ClassWithDefaults])
    implicit val magnoliaEncoder14: TaggedEncoder[Magnolia, ClassWithJsonKey] = mkTag[Magnolia](deriveConfiguredMagnoliaEncoder[ClassWithJsonKey])

    implicit val magnoliaDecoder3: TaggedDecoder[Magnolia, Box[Int]] = mkTag[Magnolia](deriveConfiguredMagnoliaDecoder[Box[Int]])
    implicit val magnoliaDecoder4: TaggedDecoder[Magnolia, Qux[Int]] = mkTag[Magnolia](deriveConfiguredMagnoliaDecoder[Qux[Int]])
    private implicit val decodeWub: Decoder[Wub] = deriveConfiguredMagnoliaDecoder[Wub]
    implicit val magnoliaDecoder11: TaggedDecoder[Magnolia, Wub] = mkTag[Magnolia](decodeWub)
    implicit val magnoliaDecoder10: TaggedDecoder[Magnolia, Bam] = mkTag[Magnolia](deriveConfiguredMagnoliaDecoder[Bam])
    implicit val magnoliaDecoder6: TaggedDecoder[Magnolia, Baz] = mkTag[Magnolia](deriveConfiguredMagnoliaDecoder[Baz])
    implicit val magnoliaDecoder7: TaggedDecoder[Magnolia, Foo] = mkTag[Magnolia](deriveConfiguredMagnoliaDecoder[Foo])
    implicit val magnoliaDecoder12: TaggedDecoder[Magnolia, Seq[Foo]] = mkTag[Magnolia](Decoder.decodeSeq(magnoliaDecoder7.inner))
    private implicit val decodeRecursiveAdtExample: Decoder[RecursiveAdtExample] = deriveConfiguredMagnoliaDecoder[RecursiveAdtExample]
    implicit val magnoliaDecoder8: TaggedDecoder[Magnolia, RecursiveAdtExample] = mkTag[Magnolia](decodeRecursiveAdtExample)
    private implicit  val decodeRecursiveWithOptionExample: Decoder[RecursiveWithOptionExample] = deriveConfiguredMagnoliaDecoder[RecursiveWithOptionExample]
    implicit val magnoliaDecoder9: TaggedDecoder[Magnolia, RecursiveWithOptionExample] = mkTag[Magnolia](decodeRecursiveWithOptionExample)
    implicit val magnoliaDecoder1: TaggedDecoder[Magnolia, AnyValInside] = mkTag[Magnolia](deriveConfiguredMagnoliaDecoder[AnyValInside])
    implicit val magnoliaDecoder13: TaggedDecoder[Magnolia, ClassWithDefaults] = mkTag[Magnolia](deriveConfiguredMagnoliaDecoder[ClassWithDefaults])
    implicit val magnoliaDecoder14: TaggedDecoder[Magnolia, ClassWithJsonKey] = mkTag[Magnolia](deriveConfiguredMagnoliaDecoder[ClassWithJsonKey])

  }

  private class CirceCodecs(config: GeConfiguration) {

    implicit val configuration: GeConfiguration = config
    import io.circe.generic.extras.semiauto.{deriveEncoder,deriveDecoder}

    implicit val circeEncoder1: TaggedEncoder[Circe, AnyValInside] = mkTag[Circe](deriveEncoder[AnyValInside])
    implicit val circeEncoder3: TaggedEncoder[Circe, Box[Int]] = mkTag[Circe](deriveEncoder[Box[Int]])
    implicit val circeEncoder4: TaggedEncoder[Circe, Qux[Int]] = mkTag[Circe](deriveEncoder[Qux[Int]])
    implicit val circeEncoder6: TaggedEncoder[Circe, Baz] = mkTag[Circe](deriveEncoder[Baz])
    private implicit val encodeFoo: Encoder[Foo] = deriveEncoder[Foo]
    implicit val circeEncoder7: TaggedEncoder[Circe, Foo] = mkTag[Circe](encodeFoo)
    implicit val circeEncoderSeqFoo: TaggedEncoder[Circe, Seq[Foo]] = mkTag[Circe](Encoder.encodeSeq(encodeFoo))
    private implicit val encodeRecursiveAdtExample: Encoder[RecursiveAdtExample] = deriveEncoder[RecursiveAdtExample]
    implicit val circeEncoder8: TaggedEncoder[Circe, RecursiveAdtExample] = mkTag[Circe](encodeRecursiveAdtExample)
    private implicit val encodeRecursiveWithOptionExample: Encoder[RecursiveWithOptionExample] = deriveEncoder[RecursiveWithOptionExample]
    implicit val circeEncoder9: TaggedEncoder[Circe, RecursiveWithOptionExample] = mkTag[Circe](encodeRecursiveWithOptionExample)
    implicit val circeEncoder13: TaggedEncoder[Circe, ClassWithDefaults] = mkTag[Circe](deriveEncoder[ClassWithDefaults])
    implicit val circeEncoder14: TaggedEncoder[Circe, ClassWithJsonKey] = mkTag[Circe](deriveEncoder[ClassWithJsonKey])

    implicit val circeDecoder1: TaggedDecoder[Circe, AnyValInside] = mkTag[Circe](deriveDecoder[AnyValInside])
    implicit val circeDecoder3: TaggedDecoder[Circe, Qux[Int]] = mkTag[Circe](deriveDecoder[Qux[Int]])
    implicit val circeDecoder5: TaggedDecoder[Circe, Baz] = mkTag[Circe](deriveDecoder[Baz])
    private implicit val decodeFoo: Decoder[Foo] = deriveDecoder[Foo]
    implicit val circeDecoder6: TaggedDecoder[Circe, Foo] = mkTag[Circe](decodeFoo)
    implicit val circeDecoder4: TaggedDecoder[Circe, Seq[Foo]] = mkTag[Circe](Decoder.decodeSeq(circeDecoder6.inner))
    private implicit val decodeRecursiveAdtExample: Decoder[RecursiveAdtExample] = deriveDecoder[RecursiveAdtExample]
    implicit val circeDecoder8: TaggedDecoder[Circe, RecursiveAdtExample] = mkTag[Circe](decodeRecursiveAdtExample)
    private implicit val decodeRecursiveWithOptionExample: Decoder[RecursiveWithOptionExample] = deriveDecoder[RecursiveWithOptionExample]
    implicit val circeDecoder9: TaggedDecoder[Circe, RecursiveWithOptionExample] = mkTag[Circe](decodeRecursiveWithOptionExample)
    implicit val circeDecoder13: TaggedDecoder[Circe, ClassWithDefaults] = mkTag[Circe](deriveDecoder[ClassWithDefaults])
    implicit val circeDecoder14: TaggedDecoder[Circe, ClassWithJsonKey] = mkTag[Circe](deriveDecoder[ClassWithJsonKey])
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
    checkLaws(s"Codec[RecursiveAdtExample] $postfix", CodecEquivalenceTests.useTagged[RecursiveAdtExample].codecEquivalence)
    checkLaws(s"Codec[RecursiveWithOptionExample] $postfix", CodecEquivalenceTests.useTagged[RecursiveWithOptionExample].codecEquivalence)
    // TODO: disabled due to https://github.com/circe/circe-magnolia/issues/3
    // checkLaws(s"Codec[WithTaggedMembers] $postfix", CodecEquivalenceTests.useTagged[WithTaggedMembers].codecEquivalence)
    checkLaws(s"Codec[ClassWithDefaults] $postfix", CodecEquivalenceTests.useTagged[ClassWithDefaults].codecEquivalence)
    checkLaws(s"Codec[ClassWithJsonKey] $postfix", CodecEquivalenceTests.useTagged[ClassWithJsonKey].codecEquivalence)
  }

  testWithConfiguration("with default configuration", Configuration.default)
  testWithConfiguration("with snake case configuration", Configuration.default.withSnakeCaseConstructorNames.withSnakeCaseMemberNames)
  testWithConfiguration("with useDefault = true", Configuration.default.copy(useDefaults = true))
  testWithConfiguration("with discriminator", Configuration.default.copy(discriminator = Some("type")))

}
