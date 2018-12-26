package io.circe.magnolia.configured

import io.circe.tests.CirceSuite
import io.circe.tests.examples._
import io.circe.{Decoder, Encoder}
import io.circe.magnolia.tags.{Circe, Magnolia}
import io.circe.generic.extras.{Configuration => GeConfiguration}
import io.circe.magnolia.CodecEquivalenceTests
import io.circe.magnolia.configured.ConfiguredAutoDerivedEquivalenceSuite.{CirceCodecs, MagnoliaCodecs}
import io.circe.magnolia.AutoDerivedSuiteInputs._
import io.circe.magnolia.tags._

class ConfiguredAutoDerivedEquivalenceSuite extends CirceSuite {

  testWithConfiguration("with default configuration", Configuration.default)
  testWithConfiguration("with snake case configuration", Configuration.default.withSnakeCaseConstructorNames.withSnakeCaseMemberNames)
  testWithConfiguration("with useDefault = true", Configuration.default.copy(useDefaults = true))

  //TODOO: move
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
    checkLaws(s"Codec[OuterCaseClassExample] $postfix", CodecEquivalenceTests.useTagged[OuterCaseClassExample].codecEquivalence)
    checkLaws(s"Codec[RecursiveAdtExample] $postfix", CodecEquivalenceTests.useTagged[RecursiveAdtExample].codecEquivalence)
    checkLaws(s"Codec[RecursiveWithOptionExample] $postfix", CodecEquivalenceTests.useTagged[RecursiveWithOptionExample].codecEquivalence)
    checkLaws(s"Codec[WithTaggedMembers] $postfix", CodecEquivalenceTests.useTagged[WithTaggedMembers].codecEquivalence)
    checkLaws(s"Codec[Seq[WithSeqOfTagged]] $postfix", CodecEquivalenceTests.useTagged[Seq[WithSeqOfTagged]].codecEquivalence)
    checkLaws(s"Codec[RecursiveWithListExample] $postfix", CodecEquivalenceTests.useTagged[RecursiveWithListExample].codecEquivalence)
    checkLaws(s"Codec[ClassWithDefaults] $postfix", CodecEquivalenceTests.useTagged[ClassWithDefaults].codecEquivalence)
    checkLaws(s"Codec[ClassWithJsonKey] $postfix", CodecEquivalenceTests.useTagged[ClassWithJsonKey].codecEquivalence)
  }

}

object ConfiguredAutoDerivedEquivalenceSuite {
  class MagnoliaCodecs(config: Configuration) {
    implicit val configuration: Configuration = config

    import io.circe.tests.examples.Baz._

    import io.circe.magnolia.configured.decoder.auto._
    import io.circe.magnolia.configured.encoder.auto._

    implicit val magnoliaEncoder1: TaggedEncoder[Magnolia, AnyValInside] = tag[Magnolia](Encoder[AnyValInside])
    implicit val magnoliaEncoder3: TaggedEncoder[Magnolia, Qux[Int]] = tag[Magnolia](Encoder[Qux[Int]])
    implicit val magnoliaEncoder4: TaggedEncoder[Magnolia, Seq[Foo]] = tag[Magnolia](Encoder[Seq[Foo]])
    implicit val magnoliaEncoder5: TaggedEncoder[Magnolia, Baz] = tag[Magnolia](Encoder[Baz])
    implicit val magnoliaEncoder6: TaggedEncoder[Magnolia, Foo] = tag[Magnolia](Encoder[Foo])
    implicit val magnoliaEncoder7: TaggedEncoder[Magnolia, OuterCaseClassExample] = tag[Magnolia](Encoder[OuterCaseClassExample])
    implicit val magnoliaEncoder8: TaggedEncoder[Magnolia, RecursiveAdtExample] = tag[Magnolia](Encoder[RecursiveAdtExample])
    implicit val magnoliaEncoder9: TaggedEncoder[Magnolia, RecursiveWithOptionExample] = tag[Magnolia](Encoder[RecursiveWithOptionExample])
    implicit val magnoliaEncoder10: TaggedEncoder[Magnolia, WithTaggedMembers] = tag[Magnolia](Encoder[WithTaggedMembers])
    implicit val magnoliaEncoder11: TaggedEncoder[Magnolia, Seq[WithSeqOfTagged]] = tag[Magnolia](Encoder[Seq[WithSeqOfTagged]])
    implicit val magnoliaEncoder12: TaggedEncoder[Magnolia, RecursiveWithListExample] = tag[Magnolia](Encoder[RecursiveWithListExample])
    implicit val magnoliaEncoder13: TaggedEncoder[Magnolia, ClassWithDefaults] = tag[Magnolia](Encoder[ClassWithDefaults])
    implicit val magnoliaEncoder14: TaggedEncoder[Magnolia, ClassWithJsonKey] = tag[Magnolia](Encoder[ClassWithJsonKey])

    implicit val magnoliaDecoder1: TaggedDecoder[Magnolia, AnyValInside] = tag[Magnolia](Decoder[AnyValInside])
    implicit val magnoliaDecoder3: TaggedDecoder[Magnolia, Qux[Int]] = tag[Magnolia](Decoder[Qux[Int]])
    implicit val magnoliaDecoder4: TaggedDecoder[Magnolia, Seq[Foo]] = tag[Magnolia](Decoder[Seq[Foo]])
    implicit val magnoliaDecoder5: TaggedDecoder[Magnolia, Baz] = tag[Magnolia](Decoder[Baz])
    implicit val magnoliaDecoder6: TaggedDecoder[Magnolia, Foo] = tag[Magnolia](Decoder[Foo])
    implicit val magnoliaDecoder7: TaggedDecoder[Magnolia, OuterCaseClassExample] = tag[Magnolia](Decoder[OuterCaseClassExample])
    implicit val magnoliaDecoder8: TaggedDecoder[Magnolia, RecursiveAdtExample] = tag[Magnolia](Decoder[RecursiveAdtExample])
    implicit val magnoliaDecoder9: TaggedDecoder[Magnolia, RecursiveWithOptionExample] = tag[Magnolia](Decoder[RecursiveWithOptionExample])
    implicit val magnoliaDecoder10: TaggedDecoder[Magnolia, WithTaggedMembers] = tag[Magnolia](Decoder[WithTaggedMembers])
    implicit val magnoliaDecoder11: TaggedDecoder[Magnolia, Seq[WithSeqOfTagged]] = tag[Magnolia](Decoder[Seq[WithSeqOfTagged]])
    implicit val magnoliaDecoder12: TaggedDecoder[Magnolia, RecursiveWithListExample] = tag[Magnolia](Decoder[RecursiveWithListExample])
    implicit val magnoliaDecoder13: TaggedDecoder[Magnolia, ClassWithDefaults] = tag[Magnolia](Decoder[ClassWithDefaults])
    implicit val magnoliaDecoder14: TaggedDecoder[Magnolia, ClassWithJsonKey] = tag[Magnolia](Decoder[ClassWithJsonKey])

  }

  class CirceCodecs(config: GeConfiguration) {

    implicit val configuration: GeConfiguration = config
    import io.circe.generic.extras.auto._

    implicit val circeEncoder1: TaggedEncoder[Circe, AnyValInside] = tag[Circe](Encoder[AnyValInside])
    implicit val circeEncoder3: TaggedEncoder[Circe, Qux[Int]] = tag[Circe](Encoder[Qux[Int]])
    implicit val circeEncoder4: TaggedEncoder[Circe, Seq[Foo]] = tag[Circe](Encoder[Seq[Foo]])
    implicit val circeEncoder5: TaggedEncoder[Circe, Baz] = tag[Circe](Encoder[Baz])
    implicit val circeEncoder6: TaggedEncoder[Circe, Foo] = tag[Circe](Encoder[Foo])
    implicit val circeEncoder7: TaggedEncoder[Circe, OuterCaseClassExample] = tag[Circe](Encoder[OuterCaseClassExample])
    implicit val circeEncoder8: TaggedEncoder[Circe, RecursiveAdtExample] = tag[Circe](Encoder[RecursiveAdtExample])
    implicit val circeEncoder9: TaggedEncoder[Circe, RecursiveWithOptionExample] = tag[Circe](Encoder[RecursiveWithOptionExample])
    implicit val circeEncoder10: TaggedEncoder[Circe, WithTaggedMembers] = tag[Circe](Encoder[WithTaggedMembers])
    implicit val circeEncoder11: TaggedEncoder[Circe, Seq[WithSeqOfTagged]] = tag[Circe](Encoder[Seq[WithSeqOfTagged]])
    implicit val circeEncoder12: TaggedEncoder[Circe, RecursiveWithListExample] = tag[Circe](Encoder[RecursiveWithListExample])
    implicit val circeEncoder13: TaggedEncoder[Circe, ClassWithDefaults] = tag[Circe](Encoder[ClassWithDefaults])
    implicit val circeEncoder14: TaggedEncoder[Circe, ClassWithJsonKey] = tag[Circe](Encoder[ClassWithJsonKey])

    implicit val circeDecoder1: TaggedDecoder[Circe, AnyValInside] = tag[Circe](Decoder[AnyValInside])
    implicit val circeDecoder3: TaggedDecoder[Circe, Qux[Int]] = tag[Circe](Decoder[Qux[Int]])
    implicit val circeDecoder4: TaggedDecoder[Circe, Seq[Foo]] = tag[Circe](Decoder[Seq[Foo]])
    implicit val circeDecoder5: TaggedDecoder[Circe, Baz] = tag[Circe](Decoder[Baz])
    implicit val circeDecoder6: TaggedDecoder[Circe, Foo] = tag[Circe](Decoder[Foo])
    implicit val circeDecoder7: TaggedDecoder[Circe, OuterCaseClassExample] = tag[Circe](Decoder[OuterCaseClassExample])
    implicit val circeDecoder8: TaggedDecoder[Circe, RecursiveAdtExample] = tag[Circe](Decoder[RecursiveAdtExample])
    implicit val circeDecoder9: TaggedDecoder[Circe, RecursiveWithOptionExample] = tag[Circe](Decoder[RecursiveWithOptionExample])
    implicit val circeDecoder10: TaggedDecoder[Circe, WithTaggedMembers] = tag[Circe](Decoder[WithTaggedMembers])
    implicit val circeDecoder11: TaggedDecoder[Circe, Seq[WithSeqOfTagged]] = tag[Circe](Decoder[Seq[WithSeqOfTagged]])
    implicit val circeDecoder12: TaggedDecoder[Circe, RecursiveWithListExample] = tag[Circe](Decoder[RecursiveWithListExample])
    implicit val circeDecoder13: TaggedDecoder[Circe, ClassWithDefaults] = tag[Circe](Decoder[ClassWithDefaults])
    implicit val circeDecoder14: TaggedDecoder[Circe, ClassWithJsonKey] = tag[Circe](Decoder[ClassWithJsonKey])
  }
}
