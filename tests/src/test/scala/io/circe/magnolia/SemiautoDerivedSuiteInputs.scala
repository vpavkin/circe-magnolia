package io.circe.magnolia

import cats.kernel.Eq
import io.circe.generic.semiauto.*
import io.circe.generic.semiauto.*
import io.circe.{Decoder, Encoder}
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary

object SemiautoDerivedSuiteInputs:

  sealed trait RecursiveAdtExample
  case class BaseAdtExample(a: String) extends RecursiveAdtExample
  case class NestedAdtExample(r: RecursiveAdtExample)
      extends RecursiveAdtExample

  object RecursiveAdtExample:
    given eqRecursiveAdtExample: Eq[RecursiveAdtExample] =
      Eq.fromUniversalEquals

    private def atDepth(depth: Int): Gen[RecursiveAdtExample] =
      if depth < 3 then
        Gen.oneOf(
          Arbitrary.arbitrary[String].map(BaseAdtExample(_)),
          atDepth(depth + 1).map(NestedAdtExample(_))
        )
      else Arbitrary.arbitrary[String].map(BaseAdtExample(_))

    given arbitraryRecursiveAdtExample: Arbitrary[RecursiveAdtExample] =
      Arbitrary(atDepth(0))

  case class RecursiveWithOptionExample(o: Option[RecursiveWithOptionExample])

  object RecursiveWithOptionExample:
    given eqRecursiveWithOptionExample: Eq[RecursiveWithOptionExample] =
      Eq.fromUniversalEquals

    private def atDepth(depth: Int): Gen[RecursiveWithOptionExample] =
      if depth < 3 then
        Gen.option(atDepth(depth + 1)).map(RecursiveWithOptionExample(_))
      else Gen.const(RecursiveWithOptionExample(None))

    given arbitraryRecursiveWithOptionExample
        : Arbitrary[RecursiveWithOptionExample] =
      Arbitrary(atDepth(0))


  case class OvergenerationExampleInner(i: Int)
  case class OvergenerationExampleOuter0(i: OvergenerationExampleInner)
  case class OvergenerationExampleOuter1(oi: Option[OvergenerationExampleInner])
