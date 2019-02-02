package io.circe.magnolia

import io.circe.tests.CirceSuite
import io.circe.tests.examples._
import io.circe.{Decoder, Encoder}
import shapeless.tag
import shapeless.tag.@@
import tags._

class AutoDerivedEquivalenceSuite extends CirceSuite {
  import AutoDerivedSuiteInputs._

  object magnolia {

    private implicit val encodeIntTag1: Encoder[List[Int] @@ Tag1] = Encoder[List[Int]].narrow
    private implicit val encodeStringTag2: Encoder[String @@ Tag2] = Encoder[String].narrow
    private implicit val decodeIntTag1: Decoder[List[Int] @@ Tag1] = Decoder[List[Int]].map(tag[Tag1](_))
    private implicit val decodeStringTag2: Decoder[String @@ Tag2] = Decoder[String].map(tag[Tag2](_))

    private implicit val encodeStringTag: Encoder[String @@ Tag] = Encoder[String].narrow
    private implicit val decodeStringTag: Decoder[String @@ Tag] = Decoder[String].map(tag[Tag](_))

    // TODO: This is a temporary workaround for https://github.com/propensive/magnolia/issues/89
    import Baz._

    import io.circe.magnolia.derivation.decoder.auto._
    import io.circe.magnolia.derivation.encoder.auto._

    implicit val magnoliaEncoder1 = tag[Magnolia](Encoder[AnyValInside])
    implicit val magnoliaEncoder3 = tag[Magnolia](Encoder[Qux[Int]])
    implicit val magnoliaEncoder4 = tag[Magnolia](Encoder[Seq[Foo]])
    implicit val magnoliaEncoder5 = tag[Magnolia](Encoder[Baz])
    implicit val magnoliaEncoderFoo = tag[Magnolia](Encoder[Foo])
    implicit val magnoliaEncoder7 = tag[Magnolia](Encoder[OuterCaseClassExample])
    implicit val magnoliaEncoder8 = tag[Magnolia](Encoder[RecursiveAdtExample])
    implicit val magnoliaEncoder9 = tag[Magnolia](Encoder[RecursiveWithOptionExample])
    implicit val magnoliaEncoder10 = tag[Magnolia](Encoder[WithTaggedMembers])
    implicit val magnoliaEncoder11 = tag[Magnolia](Encoder[Seq[WithSeqOfTagged]])
    implicit val magnoliaEncoder12 = tag[Magnolia](Encoder[RecursiveWithListExample])

    implicit val magnoliaDecoder1 = tag[Magnolia](Decoder[AnyValInside])
    implicit val magnoliaDecoder3 = tag[Magnolia](Decoder[Qux[Int]])
    implicit val magnoliaDecoder4 = tag[Magnolia](Decoder[Seq[Foo]])
    implicit val magnoliaDecoder5 = tag[Magnolia](Decoder[Baz])
    implicit val magnoliaDecoder6 = tag[Magnolia](Decoder[Foo])
    implicit val magnoliaDecoder7 = tag[Magnolia](Decoder[OuterCaseClassExample])
    implicit val magnoliaDecoder8 = tag[Magnolia](Decoder[RecursiveAdtExample])
    implicit val magnoliaDecoder9 = tag[Magnolia](Decoder[RecursiveWithOptionExample])
    implicit val magnoliaDecoder10 = tag[Magnolia](Decoder[WithTaggedMembers])
    implicit val magnoliaDecoder11 = tag[Magnolia](Decoder[Seq[WithSeqOfTagged]])
    implicit val magnoliaDecoder12 = tag[Magnolia](Decoder[RecursiveWithListExample])
  }

  object circe {

    private implicit val encodeIntTag1: Encoder[List[Int] @@ Tag1] = Encoder[List[Int]].narrow
    private implicit val encodeStringTag2: Encoder[String @@ Tag2] = Encoder[String].narrow
    private implicit val decodeIntTag1: Decoder[List[Int] @@ Tag1] = Decoder[List[Int]].map(tag[Tag1](_))
    private implicit val decodeStringTag2: Decoder[String @@ Tag2] = Decoder[String].map(tag[Tag2](_))

    private implicit val encodeStringTag: Encoder[String @@ Tag] = Encoder[String].narrow
    private implicit val decodeStringTag: Decoder[String @@ Tag] = Decoder[String].map(tag[Tag](_))

    import io.circe.generic.auto._

    implicit val circeEncoder1 = tag[Circe](Encoder[AnyValInside])
    implicit val circeEncoder3 = tag[Circe](Encoder[Qux[Int]])
    implicit val circeEncoder4 = tag[Circe](Encoder[Seq[Foo]])
    implicit val circeEncoder5 = tag[Circe](Encoder[Baz])
    implicit val circeEncoder6 = tag[Circe](Encoder[Foo])
    implicit val circeEncoder7 = tag[Circe](Encoder[OuterCaseClassExample])
    implicit val circeEncoder8 = tag[Circe](Encoder[RecursiveAdtExample])
    implicit val circeEncoder9 = tag[Circe](Encoder[RecursiveWithOptionExample])
    implicit val circeEncoder10 = tag[Circe](Encoder[WithTaggedMembers])
    implicit val circeEncoder11 = tag[Circe](Encoder[Seq[WithSeqOfTagged]])
    implicit val circeEncoder12 = tag[Circe](Encoder[RecursiveWithListExample])


    implicit val circeDecoder1 = tag[Circe](Decoder[AnyValInside])
    implicit val circeDecoder3 = tag[Circe](Decoder[Qux[Int]])
    implicit val circeDecoder4 = tag[Circe](Decoder[Seq[Foo]])
    implicit val circeDecoder5 = tag[Circe](Decoder[Baz])
    implicit val circeDecoder6 = tag[Circe](Decoder[Foo])
    implicit val circeDecoderSealed = tag[Circe](Encoder[Sealed])
    implicit val circeDecoder7 = tag[Circe](Decoder[OuterCaseClassExample])
    implicit val circeDecoder8 = tag[Circe](Decoder[RecursiveAdtExample])
    implicit val circeDecoder9 = tag[Circe](Decoder[RecursiveWithOptionExample])
    implicit val circeDecoder10 = tag[Circe](Decoder[WithTaggedMembers])
    implicit val circeDecoder11 = tag[Circe](Decoder[Seq[WithSeqOfTagged]])
    implicit val circeDecoder12 = tag[Circe](Decoder[RecursiveWithListExample])
  }

  import magnolia._
  import circe._

  checkLaws("Codec[AnyValInside]", CodecEquivalenceTests[AnyValInside].codecEquivalence)
  checkLaws("Codec[Qux[Int]]", CodecEquivalenceTests[Qux[Int]].codecEquivalence)
  checkLaws("Codec[Seq[Foo]]", CodecEquivalenceTests[Seq[Foo]].codecEquivalence)
  checkLaws("Codec[Baz]", CodecEquivalenceTests[Baz].codecEquivalence)
  checkLaws("Codec[Foo]", CodecEquivalenceTests[Foo].codecEquivalence)
  checkLaws("Codec[Sealed]", CodecEquivalenceTests[Foo].codecEquivalence)
  checkLaws("Codec[OuterCaseClassExample]", CodecEquivalenceTests[OuterCaseClassExample].codecEquivalence)
  checkLaws("Codec[RecursiveAdtExample]", CodecEquivalenceTests[RecursiveAdtExample].codecEquivalence)
  checkLaws("Codec[RecursiveWithOptionExample]", CodecEquivalenceTests[RecursiveWithOptionExample].codecEquivalence)
  checkLaws("Codec[WithTaggedMembers]", CodecEquivalenceTests[WithTaggedMembers].codecEquivalence)
  checkLaws("Codec[Seq[WithSeqOfTagged]]", CodecEquivalenceTests[Seq[WithSeqOfTagged]].codecEquivalence)
  checkLaws("Codec[RecursiveWithListExample]", CodecEquivalenceTests[RecursiveWithListExample].codecEquivalence)

}
