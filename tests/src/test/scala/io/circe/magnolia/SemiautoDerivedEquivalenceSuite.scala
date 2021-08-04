package io.circe.magnolia

import io.circe.{Decoder, Encoder}
import io.circe.tests.CirceSuite
import io.circe.tests.examples.*
import tags.*

class SemiautoDerivedEquivalenceSuite extends CirceSuite:
  import SemiautoDerivedSuiteInputs.*

  object semiautoMagnoliaInstances:
    import io.circe.magnolia.derivation.decoder.semiauto.*
    import io.circe.magnolia.derivation.encoder.semiauto.*

    given magnoliaEncoder3: (Encoder[Box[Int]] @@ Magnolia) =
      tag[Magnolia](deriveMagnoliaEncoder[Box[Int]])
    given magnoliaEncoder4: (Encoder[Qux[Int]] @@ Magnolia) =
      tag[Magnolia](deriveMagnoliaEncoder[Qux[Int]])
    given magnoliaEncoder6: (Encoder[Baz] @@ Magnolia) =
      tag[Magnolia](deriveMagnoliaEncoder[Baz])
    given magnoliaEncoder11: (Encoder[Wub] @@ Magnolia) =
      tag[Magnolia](deriveMagnoliaEncoder[Wub])
    given magnoliaEncoder10: (Encoder[Bam] @@ Magnolia) =
      tag[Magnolia](deriveMagnoliaEncoder[Bam])
    given magnoliaEncoder7: (Encoder[Foo] @@ Magnolia) =
      tag[Magnolia](deriveMagnoliaEncoder[Foo])

    implicit private val encodeRecursiveAdtExample
        : Encoder[RecursiveAdtExample] =
      deriveMagnoliaEncoder[RecursiveAdtExample]

    given magnoliaEncoder8: (Encoder[RecursiveAdtExample] @@ Magnolia) =
      tag[Magnolia](encodeRecursiveAdtExample)

    implicit private lazy val encodeRecursiveWithOptionExample
        : Encoder[RecursiveWithOptionExample] =
      deriveMagnoliaEncoder[RecursiveWithOptionExample]
    given magnoliaEncoder9: (Encoder[RecursiveWithOptionExample] @@ Magnolia) =
      tag[Magnolia](encodeRecursiveWithOptionExample)

    given magnoliaEncoder5: (Encoder[Seq[Foo]] @@ Magnolia) =
      tag[Magnolia](Encoder.encodeSeq(magnoliaEncoder7))

    given magnoliaDecoder3: (Decoder[Box[Int]] @@ Magnolia) =
      tag[Magnolia](deriveMagnoliaDecoder[Box[Int]])
    given magnoliaDecoder4: (Decoder[Qux[Int]] @@ Magnolia) =
      tag[Magnolia](deriveMagnoliaDecoder[Qux[Int]])
    given magnoliaDecoder11: (Decoder[Wub] @@ Magnolia) =
      tag[Magnolia](deriveMagnoliaDecoder[Wub])
    given magnoliaDecoder10: (Decoder[Bam] @@ Magnolia) =
      tag[Magnolia](deriveMagnoliaDecoder[Bam])
    given magnoliaDecoder6: (Decoder[Baz] @@ Magnolia) =
      tag[Magnolia](deriveMagnoliaDecoder[Baz])
    given magnoliaDecoder7: (Decoder[Foo] @@ Magnolia) =
      tag[Magnolia](deriveMagnoliaDecoder[Foo])
    implicit private val decoderREcursiveAdtExample
        : Decoder[RecursiveAdtExample] =
      deriveMagnoliaDecoder[RecursiveAdtExample]
    given magnoliaDecoder8: (Decoder[RecursiveAdtExample] @@ Magnolia) =
      tag[Magnolia](decoderREcursiveAdtExample)
    implicit private lazy val decoderRecursiveWithOptionExample
        : Decoder[RecursiveWithOptionExample] =
      deriveMagnoliaDecoder[RecursiveWithOptionExample]
    given magnoliaDecoder9: (Decoder[RecursiveWithOptionExample] @@ Magnolia) =
      tag[Magnolia](decoderRecursiveWithOptionExample)

    given magnoliaDecoder5: (Decoder[Seq[Foo]] @@ Magnolia) =
      tag[Magnolia](Decoder.decodeSeq(magnoliaDecoder7))

  object semiautoCirceInstances:

    import io.circe.generic.semiauto.*

    given enc1: Encoder[Wub] = deriveEncoder[Wub]
    given dec1: Decoder[Wub] = deriveDecoder[Wub]

    given enc2: Encoder[BaseAdtExample] = deriveEncoder[BaseAdtExample]
    given dec2: Decoder[BaseAdtExample] = deriveDecoder[BaseAdtExample]

    given circeEncoder3: (Encoder[Box[Int]] @@ Circe) =
      tag[Circe](deriveEncoder[Box[Int]])
    given circeEncoder4: (Encoder[Qux[Int]] @@ Circe) =
      tag[Circe](deriveEncoder[Qux[Int]])
    given circeEncoder6: (Encoder[Baz] @@ Circe) =
      tag[Circe](deriveEncoder[Baz])
    given circeEncoder7: (Encoder[Foo] @@ Circe) =
      tag[Circe](deriveEncoder[Foo])
    implicit private val encoderREcursiveAdtExample
        : Encoder[RecursiveAdtExample] = deriveEncoder[RecursiveAdtExample]
    given circeEncoder8: (Encoder[RecursiveAdtExample] @@ Circe) =
      tag[Circe](encoderREcursiveAdtExample)
    implicit private val encoderRecursiveWithOptionExample
        : Encoder[RecursiveWithOptionExample] =
      deriveEncoder[RecursiveWithOptionExample]
    given circeEncoder9: (Encoder[RecursiveWithOptionExample] @@ Circe) =
      tag[Circe](encoderRecursiveWithOptionExample)

    given circeEncoder5: (Encoder[Seq[Foo]] @@ Circe) =
      tag[Circe](Encoder.encodeSeq(circeEncoder7))

    given circeDecoder3: (Decoder[Box[Int]] @@ Circe) =
      tag[Circe](deriveDecoder[Box[Int]])

    given circeDecoder4: (Decoder[Qux[Int]] @@ Circe) =
      tag[Circe](deriveDecoder[Qux[Int]])

    given circeDecoder6: (Decoder[Baz] @@ Circe) =
      tag[Circe](deriveDecoder[Baz])
    given circeDecoder7: (Decoder[Foo] @@ Circe) =
      tag[Circe](deriveDecoder[Foo]: Decoder[Foo])
    implicit private val decodeRecursiveAdtExample
        : Decoder[RecursiveAdtExample] = deriveDecoder[RecursiveAdtExample]
    given circeDecoder8: (Decoder[RecursiveAdtExample] @@ Circe) =
      tag[Circe](decodeRecursiveAdtExample)
    implicit private val decodeRecursiveWithOptionExample
        : Decoder[RecursiveWithOptionExample] =
      deriveDecoder[RecursiveWithOptionExample]
    given circeDecoder9: (Decoder[RecursiveWithOptionExample] @@ Circe) =
      tag[Circe](decodeRecursiveWithOptionExample)

    given circeDecoder5: (Decoder[Seq[Foo]] @@ Circe) =
      tag[Circe](Decoder.decodeSeq(circeDecoder7))

  import semiautoMagnoliaInstances.given

  import semiautoCirceInstances.given

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

  checkLaws("Codec[Seq[Foo]]", CodecEquivalenceTests[Seq[Foo]].codecEquivalence)
