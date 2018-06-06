package io.circe.magnolia

import cats.kernel.Eq
import cats.syntax.AllSyntax
import io.circe.magnolia.derivation.decoder.auto._
import io.circe.magnolia.derivation.encoder.auto._
import io.circe.{Decoder, Encoder}
import org.scalacheck.{Arbitrary, Gen}

object AutoDerivedSuiteInputs extends AllSyntax {
  case class InnerCaseClassExample(a: String, b: String, c: String, d: String)
  case class OuterCaseClassExample(a: String, inner: InnerCaseClassExample)

  object InnerCaseClassExample {
    implicit val arbitraryInnerCaseClassExample: Arbitrary[InnerCaseClassExample] =
      Arbitrary(
        for {
          a <- Arbitrary.arbitrary[String]
          b <- Arbitrary.arbitrary[String]
          c <- Arbitrary.arbitrary[String]
          d <- Arbitrary.arbitrary[String]
        } yield InnerCaseClassExample(a, b, c, d)
      )
  }

  object OuterCaseClassExample {
    implicit val eqOuterCaseClassExample: Eq[OuterCaseClassExample] = Eq.fromUniversalEquals

    implicit val arbitraryOuterCaseClassExample: Arbitrary[OuterCaseClassExample] =
      Arbitrary(
        for {
          a <- Arbitrary.arbitrary[String]
          i <- Arbitrary.arbitrary[InnerCaseClassExample]
        } yield OuterCaseClassExample(a, i)
      )
  }

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
  }

  case class RecursiveWithOptionExample(o: Option[RecursiveWithOptionExample])

  object RecursiveWithOptionExample {
    implicit val eqRecursiveWithOptionExample: Eq[RecursiveWithOptionExample] =
      Eq.fromUniversalEquals

    private def atDepth(depth: Int): Gen[RecursiveWithOptionExample] = if (depth < 3)
      Gen.option(atDepth(depth + 1)).map(
        RecursiveWithOptionExample(_)
      ) else Gen.const(RecursiveWithOptionExample(None))

    implicit val arbitraryRecursiveWithOptionExample: Arbitrary[RecursiveWithOptionExample] =
      Arbitrary(atDepth(0))
  }

  case class RecursiveWithListExample(o: List[RecursiveWithListExample])

  object RecursiveWithListExample {
    implicit val eqRecursiveWithListExample: Eq[RecursiveWithListExample] =
      Eq.fromUniversalEquals

    private def atDepth(depth: Int): Gen[RecursiveWithListExample] =
      if (depth < 2) for {
        size <- Gen.choose(0, 3)
        l <- Gen.listOfN(size, atDepth(depth + 1))
      } yield RecursiveWithListExample(l)
      else Gen.const(RecursiveWithListExample(Nil))

    implicit val arbitraryRecursiveWithListExample: Arbitrary[RecursiveWithListExample] = Arbitrary(atDepth(0))
  }

  import shapeless.tag
  import shapeless.tag.@@

  trait Tag1
  trait Tag2
  case class WithTaggedMembers(i: List[Int] @@ Tag1, s: String @@ Tag2)

  implicit val encodeIntTag1: Encoder[List[Int] @@ Tag1] = Encoder[List[Int]].narrow
  implicit val encodeStringTag2: Encoder[String @@ Tag2] = Encoder[String].narrow
  implicit val decodeIntTag1: Decoder[List[Int] @@ Tag1] = Decoder[List[Int]].map(tag[Tag1](_))
  implicit val decodeStringTag2: Decoder[String @@ Tag2] = Decoder[String].map(tag[Tag2](_))

  object WithTaggedMembers {
    implicit val eqWithTaggedMembers: Eq[WithTaggedMembers] = Eq.fromUniversalEquals

    implicit val arbitraryWithTaggedMembers: Arbitrary[WithTaggedMembers] = Arbitrary(
      for {
        i <- Arbitrary.arbitrary[List[Int]]
        s <- Arbitrary.arbitrary[String]
      } yield WithTaggedMembers(tag[Tag1](i), tag[Tag2](s))
    )
  }

  trait Tag
  case class WithSeqOfTagged(s: Vector[String @@ Tag])

  implicit val encodeStringTag: Encoder[String @@ Tag] = Encoder[String].narrow
  implicit val decodeStringTag: Decoder[String @@ Tag] = Decoder[String].map(tag[Tag](_))

  object WithSeqOfTagged {
    implicit val eqSeqOfWithSeqOfTagged: Eq[Seq[WithSeqOfTagged]] = Eq.fromUniversalEquals

    implicit val arbitraryWithSeqOfTagged: Arbitrary[WithSeqOfTagged] = Arbitrary(
      for {
        s <- Arbitrary.arbitrary[Vector[String]]
      } yield WithSeqOfTagged(s.map(tag[Tag](_)))
    )
  }
}
