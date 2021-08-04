package io.circe.magnolia

import cats.kernel.Eq
import cats.syntax.AllSyntax
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary

object AutoDerivedSuiteInputs extends AllSyntax:
  case class InnerCaseClassExample(a: String, b: String, c: String, d: String)
  case class OuterCaseClassExample(a: String, inner: InnerCaseClassExample)

  object InnerCaseClassExample:
    given arbitraryInnerCaseClassExample: Arbitrary[InnerCaseClassExample] =
      Arbitrary(
        for
          a <- Arbitrary.arbitrary[String]
          b <- Arbitrary.arbitrary[String]
          c <- Arbitrary.arbitrary[String]
          d <- Arbitrary.arbitrary[String]
        yield InnerCaseClassExample(a, b, c, d)
      )

  object OuterCaseClassExample:
    given eqOuterCaseClassExample: Eq[OuterCaseClassExample] =
      Eq.fromUniversalEquals

    given arbitraryOuterCaseClassExample: Arbitrary[OuterCaseClassExample] =
      Arbitrary(
        for
          a <- Arbitrary.arbitrary[String]
          i <- Arbitrary.arbitrary[InnerCaseClassExample]
        yield OuterCaseClassExample(a, i)
      )

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
        Gen
          .option(atDepth(depth + 1))
          .map(
            RecursiveWithOptionExample(_)
          )
      else Gen.const(RecursiveWithOptionExample(None))

    given arbitraryRecursiveWithOptionExample
        : Arbitrary[RecursiveWithOptionExample] =
      Arbitrary(atDepth(0))

  case class RecursiveWithListExample(o: List[RecursiveWithListExample])

  object RecursiveWithListExample:
    given eqRecursiveWithListExample: Eq[RecursiveWithListExample] =
      Eq.fromUniversalEquals

    private def atDepth(depth: Int): Gen[RecursiveWithListExample] =
      if depth < 2 then
        for
          size <- Gen.choose(0, 3)
          l <- Gen.listOfN(size, atDepth(depth + 1))
        yield RecursiveWithListExample(l)
      else Gen.const(RecursiveWithListExample(Nil))

    given arbitraryRecursiveWithListExample
        : Arbitrary[RecursiveWithListExample] = Arbitrary(atDepth(0))



  trait Tag1
  trait Tag2
  case class WithTaggedMembers(i: List[Int] @@ Tag1, s: String @@ Tag2)

  object WithTaggedMembers:
    given eqWithTaggedMembers: Eq[WithTaggedMembers] =
      Eq.fromUniversalEquals

    given arbitraryWithTaggedMembers: Arbitrary[WithTaggedMembers] =
      Arbitrary(
        for
          i <- Arbitrary.arbitrary[List[Int]]
          s <- Arbitrary.arbitrary[String]
        yield WithTaggedMembers(
          tag[Tag1](i),
          tag[Tag2](s)
        )
      )

  trait Tag
  case class WithSeqOfTagged(s: Vector[String @@ Tag])

  object WithSeqOfTagged:
    given eqSeqOfWithSeqOfTagged: Eq[Seq[WithSeqOfTagged]] =
      Eq.fromUniversalEquals

    given arbitraryWithSeqOfTagged: Arbitrary[WithSeqOfTagged] =
      Arbitrary(
        for s <- Arbitrary.arbitrary[Vector[String]]
        yield WithSeqOfTagged(s.map(tag[Tag](_)))
      )
