package io.circe.magnolia

import cats.kernel.Eq
import io.circe.magnolia.derivation.decoder.semiauto._
import io.circe.magnolia.derivation.encoder.semiauto._
import io.circe.testing.CodecTests
import io.circe.tests.CirceSuite
import io.circe.tests.examples._
import io.circe.{Decoder, Encoder, Json}
import org.scalacheck.{Arbitrary, Gen}
import shapeless.test.illTyped

object SemiautoDerivedSuite {
  implicit def decodeBox[A: Decoder]: Decoder[Box[A]] = deriveMagnoliaDecoder
  implicit def encodeBox[A: Encoder]: Encoder[Box[A]] = deriveMagnoliaEncoder

  implicit def decodeQux[A: Decoder]: Decoder[Qux[A]] = deriveMagnoliaDecoder
  implicit def encodeQux[A: Encoder]: Encoder[Qux[A]] = deriveMagnoliaEncoder

  implicit val decodeWub: Decoder[Wub] = deriveMagnoliaDecoder
  implicit val encodeWub: Encoder[Wub] = deriveMagnoliaEncoder
  implicit val decodeFoo: Decoder[Foo] = deriveMagnoliaDecoder
  implicit val encodeFoo: Encoder[Foo] = deriveMagnoliaEncoder

  sealed trait RecursiveAdtExample
  case class BaseAdtExample(a: String) extends RecursiveAdtExample
  case class NestedAdtExample(r: RecursiveAdtExample) extends RecursiveAdtExample

  object RecursiveAdtExample {
    implicit val eqRecursiveAdtExample: Eq[RecursiveAdtExample] = Eq.fromUniversalEquals

    private def atDepth(depth: Int): Gen[RecursiveAdtExample] = if (depth < 3)
      Gen.oneOf(
        Arbitrary.arbitrary[String].map(BaseAdtExample(_)),
        atDepth(depth + 1).map(NestedAdtExample(_))
      ) else Arbitrary.arbitrary[String].map(BaseAdtExample(_))

    implicit val arbitraryRecursiveAdtExample: Arbitrary[RecursiveAdtExample] =
      Arbitrary(atDepth(0))

    implicit val decodeRecursiveAdtExample: Decoder[RecursiveAdtExample] = deriveMagnoliaDecoder
    implicit val encodeRecursiveAdtExample: Encoder[RecursiveAdtExample] = deriveMagnoliaEncoder
  }

  case class RecursiveWithOptionExample(o: Option[RecursiveWithOptionExample])

  object RecursiveWithOptionExample {
    implicit val eqRecursiveWithOptionExample: Eq[RecursiveWithOptionExample] =
      Eq.fromUniversalEquals

    private def atDepth(depth: Int): Gen[RecursiveWithOptionExample] = if (depth < 3)
      Arbitrary.arbitrary[Option[RecursiveWithOptionExample]].map(
        RecursiveWithOptionExample(_)
      ) else Gen.const(RecursiveWithOptionExample(None))

    implicit val arbitraryRecursiveWithOptionExample: Arbitrary[RecursiveWithOptionExample] =
      Arbitrary(atDepth(0))

    implicit val decodeRecursiveWithOptionExample: Decoder[RecursiveWithOptionExample] =
      deriveMagnoliaDecoder

    implicit val encodeRecursiveWithOptionExample: Encoder[RecursiveWithOptionExample] =
      deriveMagnoliaEncoder
  }

  case class OvergenerationExampleInner(i: Int)
  case class OvergenerationExampleOuter0(i: OvergenerationExampleInner)
  case class OvergenerationExampleOuter1(oi: Option[OvergenerationExampleInner])
}

class SemiautoDerivedSuite extends CirceSuite {
  import SemiautoDerivedSuite._

  checkLaws("Codec[Tuple1[Int]]", CodecTests[Tuple1[Int]].codec)
  checkLaws("Codec[(Int, Int, Foo)]", CodecTests[(Int, Int, Foo)].codec)
  checkLaws("Codec[Box[Int]]", CodecTests[Box[Int]].codec)
  checkLaws("Codec[Qux[Int]]", CodecTests[Qux[Int]].codec)
  checkLaws("Codec[Seq[Foo]]", CodecTests[Seq[Foo]].codec)
  checkLaws("Codec[Baz]", CodecTests[Baz].codec)
  checkLaws("Codec[Foo]", CodecTests[Foo].codec)
  checkLaws("Codec[RecursiveAdtExample]", CodecTests[RecursiveAdtExample].codec)
  checkLaws("Codec[RecursiveWithOptionExample]", CodecTests[RecursiveWithOptionExample].codec)

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
