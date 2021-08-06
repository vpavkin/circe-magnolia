package io.circe.magnolia

import io.circe.magnolia.derivation.decoder.semiauto.*
import io.circe.magnolia.derivation.encoder.semiauto.*
import io.circe.testing.CodecTests
import io.circe.tests.CirceSuite
import io.circe.tests.examples.*
import io.circe.{Decoder, Encoder, Json}

class SemiautoDerivedSuite extends CirceSuite:
  import SemiautoDerivedSuiteInputs.*

  given decodeBox[A: Decoder]: Decoder[Box[A]] = deriveMagnoliaDecoder
  given encodeBox[A: Encoder]: Encoder[Box[A]] = deriveMagnoliaEncoder

  given decodeQux[A: Decoder]: Decoder[Qux[A]] = deriveMagnoliaDecoder
  given encodeQux[A: Encoder]: Encoder[Qux[A]] = deriveMagnoliaEncoder

  given decodeWub: Decoder[Wub] = deriveMagnoliaDecoder
  given encodeWub: Encoder[Wub] = deriveMagnoliaEncoder
  given decodeFoo: Decoder[Foo] = deriveMagnoliaDecoder
  given encodeFoo: Encoder[Foo] = deriveMagnoliaEncoder


  given decodeRecursiveAdtExample: Decoder[RecursiveAdtExample] =
    deriveMagnoliaDecoder
  given encodeRecursiveAdtExample: Encoder[RecursiveAdtExample] =
    deriveMagnoliaEncoder

  implicit lazy val decodeRecursiveWithOptionExample
      : Decoder[RecursiveWithOptionExample] =
    deriveMagnoliaDecoder
  implicit lazy val encodeRecursiveWithOptionExample
      : Encoder[RecursiveWithOptionExample] =
    deriveMagnoliaEncoder

  checkLaws("Codec[Tuple1[Int]]", CodecTests[Tuple1[Int]].unserializableCodec)
  checkLaws(
    "Codec[(Int, Int, Foo)]",
    CodecTests[(Int, Int, Foo)].unserializableCodec
  )
  checkLaws("Codec[Box[Int]]", CodecTests[Box[Int]].unserializableCodec)
  checkLaws("Codec[Qux[Int]]", CodecTests[Qux[Int]].unserializableCodec)
  checkLaws("Codec[Seq[Foo]]", CodecTests[Seq[Foo]].unserializableCodec)
  checkLaws("Codec[Baz]", CodecTests[Baz].unserializableCodec)
  checkLaws("Codec[Foo]", CodecTests[Foo].unserializableCodec)
  checkLaws(
    "Codec[RecursiveAdtExample]",
    CodecTests[RecursiveAdtExample].unserializableCodec
  )
  checkLaws(
    "Codec[RecursiveWithOptionExample]",
    CodecTests[RecursiveWithOptionExample].unserializableCodec
  )

  "A generically derived codec" should "not interfere with base instances" in forAll {
    (is: List[Int]) =>
      val json = Encoder[List[Int]].apply(is)

      assert(
        json === Json.fromValues(is.map(Json.fromInt)) && json
          .as[List[Int]] === Right(is)
      )
  }

  it should "not come from nowhere" in {
    assertTypeError("Decoder[OvergenerationExampleInner]")
    assertTypeError("Encoder[OvergenerationExampleInner]")

    assertTypeError("Decoder[OvergenerationExampleOuter0]")
    assertTypeError("Encoder[OvergenerationExampleOuter0]")
    assertTypeError("Decoder[OvergenerationExampleOuter1]")
    assertTypeError("Encoder[OvergenerationExampleOuter1]")
  }

  it should "require instances for all parts" in {
    assertTypeError("deriveMagnoliaDecoder[OvergenerationExampleInner0]")
    assertTypeError("deriveMagnoliaDecoder[OvergenerationExampleInner1]")
    assertTypeError("deriveMagnoliaEncoder[OvergenerationExampleInner0]")
    assertTypeError("deriveMagnoliaEncoder[OvergenerationExampleInner1]")
  }
