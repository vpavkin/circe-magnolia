package io.circe.magnolia

import cats.kernel.Eq
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary

object SemiautoDerivedSuiteInputs {

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

    implicit val decodeRecursiveAdtExample: Decoder[RecursiveAdtExample] = deriveDecoder
    implicit val encodeRecursiveAdtExample: Encoder[RecursiveAdtExample] = deriveEncoder
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
      deriveDecoder

    implicit val encodeRecursiveWithOptionExample: Encoder[RecursiveWithOptionExample] =
      deriveEncoder
  }

  case class AnyInt(value: Int) extends AnyVal

  object AnyInt {
    implicit val encodeAnyInt: Encoder[AnyInt] = deriveEncoder
    implicit val decodeAnyInt: Decoder[AnyInt] = deriveDecoder
  }

  case class AnyValInside(v: AnyInt)

  object AnyValInside {
    implicit val eqAnyValInside: Eq[AnyValInside] = Eq.fromUniversalEquals

    implicit val arbitraryAnyValInside: Arbitrary[AnyValInside] =
      Arbitrary(arbitrary[Int].map(i => AnyValInside(AnyInt(i))))
  }

  case class OvergenerationExampleInner(i: Int)
  case class OvergenerationExampleOuter0(i: OvergenerationExampleInner)
  case class OvergenerationExampleOuter1(oi: Option[OvergenerationExampleInner])
}
