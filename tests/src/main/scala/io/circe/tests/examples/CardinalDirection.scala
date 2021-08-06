package io.circe.tests.examples

import cats.kernel.Eq
import org.scalacheck.{Arbitrary, Gen}

sealed trait CardinalDirection
case object North extends CardinalDirection
case object South extends CardinalDirection
case object East extends CardinalDirection
case object West extends CardinalDirection

object CardinalDirection:
  given eqCardinalDirection: Eq[CardinalDirection] =
    Eq.fromUniversalEquals
  given arbitraryCardinalDirection: Arbitrary[CardinalDirection] =
    Arbitrary(
      Gen.oneOf(North, South, East, West)
    )

sealed trait ExtendedCardinalDirection
case object North2 extends ExtendedCardinalDirection
case object South2 extends ExtendedCardinalDirection
case object East2 extends ExtendedCardinalDirection
case object West2 extends ExtendedCardinalDirection
case class NotACardinalDirectionAtAll(x: String)
    extends ExtendedCardinalDirection

object ExtendedCardinalDirection:
  given eqExtendedCardinalDirection: Eq[ExtendedCardinalDirection] =
    Eq.fromUniversalEquals
  given arbitraryExtendedCardinalDirection
      : Arbitrary[ExtendedCardinalDirection] = Arbitrary(
    Gen.oneOf(
      Gen.const(North2),
      Gen.const(South2),
      Gen.const(East2),
      Gen.const(West2),
      Arbitrary.arbitrary[String].map(NotACardinalDirectionAtAll(_))
    )
  )
