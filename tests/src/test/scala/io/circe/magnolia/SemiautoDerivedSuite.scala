package io.circe.magnolia

import io.circe.magnolia.derivation.decoder.semiauto._
import io.circe.magnolia.derivation.encoder.semiauto._
import io.circe.testing.CodecTests
import io.circe.tests.CirceSuite
import io.circe.tests.examples._
import io.circe.{Decoder, Encoder, Json}
import shapeless.test.illTyped

class SemiautoDerivedSuite extends CirceSuite {
  import SemiautoDerivedSuiteInputs._

  implicit def decodeBox[A: Decoder]: Decoder[Box[A]] = deriveMagnoliaDecoder
  implicit def encodeBox[A: Encoder]: Encoder[Box[A]] = deriveMagnoliaEncoder

  implicit def decodeQux[A: Decoder]: Decoder[Qux[A]] = deriveMagnoliaDecoder
  implicit def encodeQux[A: Encoder]: Encoder[Qux[A]] = deriveMagnoliaEncoder

  implicit val decodeWub: Decoder[Wub] = deriveMagnoliaDecoder
  implicit val encodeWub: Encoder[Wub] = deriveMagnoliaEncoder
  implicit val decodeFoo: Decoder[Foo] = deriveMagnoliaDecoder
  implicit val encodeFoo: Encoder[Foo] = deriveMagnoliaEncoder

  implicit val decodeAnyValInside: Decoder[AnyValInside] = deriveMagnoliaDecoder
  implicit val encodeAnyValInside: Encoder[AnyValInside] = deriveMagnoliaEncoder

  implicit val decodeRecursiveAdtExample: Decoder[RecursiveAdtExample] = deriveMagnoliaDecoder
  implicit val encodeRecursiveAdtExample: Encoder[RecursiveAdtExample] = deriveMagnoliaEncoder

  implicit lazy val decodeRecursiveWithOptionExample: Decoder[RecursiveWithOptionExample] =
    deriveMagnoliaDecoder
  implicit lazy val encodeRecursiveWithOptionExample: Encoder[RecursiveWithOptionExample] =
    deriveMagnoliaEncoder

  checkLaws("Codec[Tuple1[Int]]", CodecTests[Tuple1[Int]].unserializableCodec)
  checkLaws("Codec[(Int, Int, Foo)]", CodecTests[(Int, Int, Foo)].unserializableCodec)
  checkLaws("Codec[Box[Int]]", CodecTests[Box[Int]].unserializableCodec)
  checkLaws("Codec[Qux[Int]]", CodecTests[Qux[Int]].unserializableCodec)
  checkLaws("Codec[Seq[Foo]]", CodecTests[Seq[Foo]].unserializableCodec)
  checkLaws("Codec[Baz]", CodecTests[Baz].unserializableCodec)
  checkLaws("Codec[Foo]", CodecTests[Foo].unserializableCodec)
  checkLaws("Codec[RecursiveAdtExample]", CodecTests[RecursiveAdtExample].unserializableCodec)
  checkLaws("Codec[RecursiveWithOptionExample]", CodecTests[RecursiveWithOptionExample].unserializableCodec)
  checkLaws("Codec[AnyValInside]", CodecTests[AnyValInside].unserializableCodec)

  "A generically derived codec" should "not interfere with base instances" in forAll { (is: List[Int]) =>
    val json = Encoder[List[Int]].apply(is)

    assert(json === Json.fromValues(is.map(Json.fromInt)) && json.as[List[Int]] === Right(is))
  }

  it should "not come from nowhere" in {
    illTyped("Decoder[OvergenerationExampleInner]")
    illTyped("Encoder[OvergenerationExampleInner]")

    illTyped("Decoder[OvergenerationExampleOuter0]")
    illTyped("Encoder[OvergenerationExampleOuter0]")
    illTyped("Decoder[OvergenerationExampleOuter1]")
    illTyped("Encoder[OvergenerationExampleOuter1]")
  }

  it should "require instances for all parts" in {
    illTyped("deriveMagnoliaDecoder[OvergenerationExampleInner0]")
    illTyped("deriveMagnoliaDecoder[OvergenerationExampleInner1]")
    illTyped("deriveMagnoliaEncoder[OvergenerationExampleInner0]")
    illTyped("deriveMagnoliaEncoder[OvergenerationExampleInner1]")
  }
}
