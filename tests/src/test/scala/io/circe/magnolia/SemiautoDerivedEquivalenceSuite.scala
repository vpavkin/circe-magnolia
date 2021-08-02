package io.circe.magnolia

import io.circe.{Decoder, Encoder}
import io.circe.tests.CirceSuite
import io.circe.tests.examples.*
import shapeless.tag
import shapeless.tag.@@
import tags.*

class SemiautoDerivedEquivalenceSuite extends CirceSuite:
  import SemiautoDerivedSuiteInputs.*

  object magnolia:
    import io.circe.magnolia.derivation.decoder.semiauto.*
    import io.circe.magnolia.derivation.encoder.semiauto.*

    implicit val magnoliaEncoder3 =
      tag[Magnolia](deriveMagnoliaEncoder[Box[Int]])
    implicit val magnoliaEncoder4 =
      tag[Magnolia](deriveMagnoliaEncoder[Qux[Int]])
    implicit val magnoliaEncoder6 = tag[Magnolia](deriveMagnoliaEncoder[Baz])
    implicit val magnoliaEncoder11 = tag[Magnolia](deriveMagnoliaEncoder[Wub])
    implicit val magnoliaEncoder10 = tag[Magnolia](deriveMagnoliaEncoder[Bam])
    implicit val magnoliaEncoder7 = tag[Magnolia](deriveMagnoliaEncoder[Foo])
    implicit private val encodeRecursiveAdtExample
        : Encoder[RecursiveAdtExample] =
      deriveMagnoliaEncoder[RecursiveAdtExample]
    implicit val magnoliaEncoder8 = tag[Magnolia](encodeRecursiveAdtExample)
    implicit private lazy val encodeRecursiveWithOptionExample
        : Encoder[RecursiveWithOptionExample] =
      deriveMagnoliaEncoder[RecursiveWithOptionExample]
    implicit val magnoliaEncoder9 =
      tag[Magnolia](encodeRecursiveWithOptionExample)
    implicit val magnoliaEncoder1 =
      tag[Magnolia](deriveMagnoliaEncoder[AnyValInside])
    implicit val magnoliaEncoder5: Encoder[Seq[Foo]] @@ Magnolia =
      tag[Magnolia](Encoder.encodeSeq(magnoliaEncoder7))

    implicit val magnoliaDecoder3 =
      tag[Magnolia](deriveMagnoliaDecoder[Box[Int]])
    implicit val magnoliaDecoder4 =
      tag[Magnolia](deriveMagnoliaDecoder[Qux[Int]])
    implicit val magnoliaDecoder11 = tag[Magnolia](deriveMagnoliaDecoder[Wub])
    implicit val magnoliaDecoder10 = tag[Magnolia](deriveMagnoliaDecoder[Bam])
    implicit val magnoliaDecoder6 = tag[Magnolia](deriveMagnoliaDecoder[Baz])
    implicit val magnoliaDecoder7 = tag[Magnolia](deriveMagnoliaDecoder[Foo])
    implicit private val decoderREcursiveAdtExample
        : Decoder[RecursiveAdtExample] =
      deriveMagnoliaDecoder[RecursiveAdtExample]
    implicit val magnoliaDecoder8 = tag[Magnolia](decoderREcursiveAdtExample)
    implicit private lazy val decoderRecursiveWithOptionExample
        : Decoder[RecursiveWithOptionExample] =
      deriveMagnoliaDecoder[RecursiveWithOptionExample]
    implicit val magnoliaDecoder9 =
      tag[Magnolia](decoderRecursiveWithOptionExample)
    implicit val magnoliaDecoder1 =
      tag[Magnolia](deriveMagnoliaDecoder[AnyValInside])
    implicit val magnoliaDecoder5 =
      tag[Magnolia](Decoder.decodeSeq(magnoliaDecoder7))

  object circe:

    import io.circe.generic.semiauto.*

    implicit val enc1 = deriveEncoder[Wub]
    implicit val dec1 = deriveDecoder[Wub]

    implicit val enc2 = deriveEncoder[BaseAdtExample]
    implicit val dec2 = deriveDecoder[BaseAdtExample]

    implicit val circeEncoder3 = tag[Circe](deriveEncoder[Box[Int]])
    implicit val circeEncoder4 = tag[Circe](deriveEncoder[Qux[Int]])
    implicit val circeEncoder6 = tag[Circe](deriveEncoder[Baz])
    implicit val circeEncoder7 = tag[Circe](deriveEncoder[Foo])
    implicit private val encoderREcursiveAdtExample
        : Encoder[RecursiveAdtExample] = deriveEncoder[RecursiveAdtExample]
    implicit val circeEncoder8 = tag[Circe](encoderREcursiveAdtExample)
    implicit private val encoderRecursiveWithOptionExample
        : Encoder[RecursiveWithOptionExample] =
      deriveEncoder[RecursiveWithOptionExample]
    implicit val circeEncoder9 = tag[Circe](encoderRecursiveWithOptionExample)
    implicit val circeEncoder1 = tag[Circe](deriveEncoder[AnyValInside])
    implicit val circeEncoder5: Encoder[Seq[Foo]] @@ Circe =
      tag[Circe](Encoder.encodeSeq(circeEncoder7))

    implicit val circeDecoder3 = tag[Circe](deriveDecoder[Box[Int]])
    implicit val circeDecoder4 = tag[Circe](deriveDecoder[Qux[Int]])
    implicit val circeDecoder6 = tag[Circe](deriveDecoder[Baz])
    implicit val circeDecoder7 = tag[Circe](deriveDecoder[Foo]: Decoder[Foo])
    implicit private val decodeRecursiveAdtExample
        : Decoder[RecursiveAdtExample] = deriveDecoder[RecursiveAdtExample]
    implicit val circeDecoder8 = tag[Circe](decodeRecursiveAdtExample)
    implicit private val decodeRecursiveWithOptionExample
        : Decoder[RecursiveWithOptionExample] =
      deriveDecoder[RecursiveWithOptionExample]
    implicit val circeDecoder9 = tag[Circe](decodeRecursiveWithOptionExample)
    implicit val circeDecoder1 = tag[Circe](deriveDecoder[AnyValInside])
    implicit val circeDecoder5 = tag[Circe](Decoder.decodeSeq(circeDecoder7))

  import magnolia1.*
  import circe.*

  checkLaws("Codec[Box[Int]]", CodecEquivalenceTests[Box[Int]].codecEquivalence)
  checkLaws("Codec[Qux[Int]]", CodecEquivalenceTests[Qux[Int]].codecEquivalence)
  checkLaws("Codec[Baz]", CodecEquivalenceTests[Baz].codecEquivalence)
  checkLaws("Codec[Foo]", CodecEquivalenceTests[Foo].codecEquivalence)
  checkLaws(
    "Codec[RecursiveAdtExample]",
    CodecEquivalenceTests[RecursiveAdtExample].codecEquivalence
  )
  checkLaws(
    "Codec[RecursiveWithOptionExample]",
    CodecEquivalenceTests[RecursiveWithOptionExample].codecEquivalence
  )
  checkLaws(
    "Codec[AnyValInside]",
    CodecEquivalenceTests[AnyValInside].codecEquivalence
  )
  checkLaws("Codec[Seq[Foo]]", CodecEquivalenceTests[Seq[Foo]].codecEquivalence)
