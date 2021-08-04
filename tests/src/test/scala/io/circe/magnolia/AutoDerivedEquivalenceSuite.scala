package io.circe.magnolia

import io.circe.tests.CirceSuite
import io.circe.tests.examples.*
import io.circe.{Decoder, Encoder}
import tags.*

//scalafmt:off
class AutoDerivedEquivalenceSuite extends CirceSuite:
  import AutoDerivedSuiteInputs.*

  object magnoliaInstances:

    given t1e: Encoder[List[Int] @@ Tag1] =
      Encoder[List[Int]].narrow
    given t2e: Encoder[String @@ Tag2] =
      Encoder[String].narrow
    given t1d: Decoder[List[Int] @@ Tag1] =
      Decoder[List[Int]].map(tag[Tag1](_))
    given t2d: Decoder[String @@ Tag2] =
      Decoder[String].map(tag[Tag2](_))

    given te: Encoder[String @@ Tag] =
      Encoder[String].narrow
    given td: Decoder[String @@ Tag] =
      Decoder[String].map(tag[Tag](_))

    // TODO: This is a temporary workaround for https://github.com/propensive/magnolia/issues/89
    import Baz.given
    import Baz.*

    import io.circe.magnolia.derivation.decoder.auto.given
    import io.circe.magnolia.derivation.encoder.auto.given

    given magnoliaEncoder3: (Encoder[Qux[Int]] @@ Magnolia) =
      tag[Magnolia](Encoder[Qux[Int]])
    given magnoliaEncoder4: (Encoder[Seq[Foo]] @@ Magnolia) =
      tag[Magnolia](Encoder[Seq[Foo]])
    given magnoliaEncoder5: (Encoder[Baz] @@ Magnolia) =
      tag[Magnolia](Encoder[Baz])
    given magnoliaEncoderFoo: (Encoder[Foo] @@ Magnolia) =
      tag[Magnolia](Encoder[Foo])
    given magnoliaEncoder7: (Encoder[OuterCaseClassExample] @@ Magnolia) =
      tag[Magnolia](Encoder[OuterCaseClassExample])
//    given magnoliaEncoder8: (Encoder[RecursiveAdtExample] @@ Magnolia) =
//      tag[Magnolia](Encoder[RecursiveAdtExample])
//    given magnoliaEncoder10: (Encoder[RecursiveWithOptionExample] @@ Magnolia) =
//      tag[Magnolia](Encoder[RecursiveWithOptionExample])
    given magnoliaEncoder11: (Encoder[WithTaggedMembers] @@ Magnolia) =
      tag[Magnolia](Encoder[WithTaggedMembers])
    given magnoliaEncoder12: (Encoder[Seq[WithSeqOfTagged]] @@ Magnolia) =
      tag[Magnolia](Encoder[Seq[WithSeqOfTagged]])
//    given magnoliaEncoder13: (Encoder[RecursiveWithListExample] @@ Magnolia) =
//      tag[Magnolia](Encoder[RecursiveWithListExample])

    given magnoliaDecoder3: (Decoder[Qux[Int]] @@ Magnolia) =
      tag[Magnolia](Decoder[Qux[Int]])
    given magnoliaDecoder4: (Decoder[Seq[Foo]] @@ Magnolia) =
      tag[Magnolia](Decoder[Seq[Foo]])
    given magnoliaDecoder5: (Decoder[Baz] @@ Magnolia) =
      tag[Magnolia](Decoder[Baz])
    given magnoliaDecoder6: (Decoder[Foo] @@ Magnolia) =
      tag[Magnolia](Decoder[Foo])
    given magnoliaDecoder7: (Decoder[OuterCaseClassExample] @@ Magnolia) =
      tag[Magnolia](Decoder[OuterCaseClassExample])
//    given magnoliaDecoder8: (Decoder[RecursiveAdtExample] @@ Magnolia) =
//      tag[Magnolia](Decoder[RecursiveAdtExample])
//    given magnoliaDecoder9: (Decoder[RecursiveWithOptionExample] @@ Magnolia) =
//      tag[Magnolia](Decoder[RecursiveWithOptionExample])
    given magnoliaDecoder10: (Decoder[WithTaggedMembers] @@ Magnolia) =
      tag[Magnolia](Decoder[WithTaggedMembers])
    given magnoliaDecoder11: (Decoder[Seq[WithSeqOfTagged]] @@ Magnolia) =
      tag[Magnolia](Decoder[Seq[WithSeqOfTagged]])
//    given magnoliaDecoder12: (Decoder[RecursiveWithListExample] @@ Magnolia) =
//      tag[Magnolia](Decoder[RecursiveWithListExample])

  object circeInstances:

    given t1codec: Encoder[List[Int] @@ Tag1] = Encoder[List[Int]].narrow
    given t1codec1: Encoder[String @@ Tag2] = Encoder[String].narrow
    given t1codec2: Decoder[List[Int] @@ Tag1] =
      Decoder[List[Int]].map(tag[Tag1](_))
    given t1codec3: Decoder[String @@ Tag2] = Decoder[String].map(tag[Tag2](_))
    given t1codec4: Encoder[String @@ Tag] = Encoder[String].narrow
    given t1codec5: Decoder[String @@ Tag] = Decoder[String].map(tag[Tag](_))

    import io.circe.generic.auto.*

    given circeEncoder3: (Encoder[Qux[Int]] @@ Circe) =
      tag[Circe](Encoder[Qux[Int]])
    given circeEncoder4: (Encoder[Seq[Foo]] @@ Circe) =
      tag[Circe](Encoder[Seq[Foo]])
    given circeEncoder5: (Encoder[Baz] @@ Circe) = tag[Circe](Encoder[Baz])
    given circeEncoder6: (Encoder[Foo] @@ Circe) = tag[Circe](Encoder[Foo])
    given circeEncoder7: (Encoder[OuterCaseClassExample] @@ Circe) =
      tag[Circe](Encoder[OuterCaseClassExample])
//    given circeEncoder8: (Encoder[RecursiveAdtExample] @@ Circe) =
//      tag[Circe](Encoder[RecursiveAdtExample])
//    given circeEncoder9: (Encoder[RecursiveWithOptionExample] @@ Circe) =
//      tag[Circe](Encoder[RecursiveWithOptionExample])
    given circeEncoder10: (Encoder[WithTaggedMembers] @@ Circe) =
      tag[Circe](Encoder[WithTaggedMembers])
    given circeEncoder11: (Encoder[Seq[WithSeqOfTagged]] @@ Circe) =
      tag[Circe](Encoder[Seq[WithSeqOfTagged]])
//    given circeEncoder12: (Encoder[RecursiveWithListExample] @@ Circe) =
//      tag[Circe](Encoder[RecursiveWithListExample])

    given circeDecoder3: (Decoder[Qux[Int]] @@ Circe) =
      tag[Circe](Decoder[Qux[Int]])
    given circeDecoder4: (Decoder[Seq[Foo]] @@ Circe) =
      tag[Circe](Decoder[Seq[Foo]])
    given circeDecoder5: (Decoder[Baz] @@ Circe) = tag[Circe](Decoder[Baz])
    given circeDecoder6: (Decoder[Foo] @@ Circe) = tag[Circe](Decoder[Foo])
    given circeDecoderSealed: (Encoder[Sealed] @@ Circe) =
      tag[Circe](Encoder[Sealed])
    given circeDecoder7: (Decoder[OuterCaseClassExample] @@ Circe) =
      tag[Circe](Decoder[OuterCaseClassExample])
//    given circeDecoder8: (Decoder[RecursiveAdtExample] @@ Circe) =
//      tag[Circe](Decoder[RecursiveAdtExample])
//    given circeDecoder9: (Decoder[RecursiveWithOptionExample] @@ Circe) =
//      tag[Circe](Decoder[RecursiveWithOptionExample])
    given circeDecoder10: (Decoder[WithTaggedMembers] @@ Circe) =
      tag[Circe](Decoder[WithTaggedMembers])
    given circeDecoder11: (Decoder[Seq[WithSeqOfTagged]] @@ Circe) =
      tag[Circe](Decoder[Seq[WithSeqOfTagged]])
//    given circeDecoder12: (Decoder[RecursiveWithListExample] @@ Circe) =
//      tag[Circe](Decoder[RecursiveWithListExample])

  import magnoliaInstances.given
  import circeInstances.given

  checkLaws("Codec[Qux[Int]]", CodecEquivalenceTests[Qux[Int]].codecEquivalence)
//  checkLaws("Codec[Seq[Foo]]", CodecEquivalenceTests[Seq[Foo]].codecEquivalence)
//  checkLaws("Codec[Baz]", CodecEquivalenceTests[Baz].codecEquivalence)
//  checkLaws("Codec[Foo]", CodecEquivalenceTests[Foo].codecEquivalence)
//  checkLaws("Codec[Sealed]", CodecEquivalenceTests[Foo].codecEquivalence)
//  checkLaws(
//    "Codec[OuterCaseClassExample]",
//    CodecEquivalenceTests[OuterCaseClassExample].codecEquivalence
//  )
//  checkLaws(
//    "Codec[RecursiveAdtExample]",
//    CodecEquivalenceTests[RecursiveAdtExample].codecEquivalence
//  )
//  checkLaws(
//    "Codec[RecursiveWithOptionExample]",
//    CodecEquivalenceTests[RecursiveWithOptionExample].codecEquivalence
//  )
//  checkLaws(
//    "Codec[WithTaggedMembers]",
//    CodecEquivalenceTests[WithTaggedMembers].codecEquivalence
//  )
//  checkLaws(
//    "Codec[Seq[WithSeqOfTagged]]",
//    CodecEquivalenceTests[Seq[WithSeqOfTagged]].codecEquivalence
//  )
//  checkLaws(
//    "Codec[RecursiveWithListExample]",
//    CodecEquivalenceTests[RecursiveWithListExample].codecEquivalence
//  )
