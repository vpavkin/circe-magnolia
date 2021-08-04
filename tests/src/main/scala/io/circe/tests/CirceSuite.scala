package io.circe.tests

import cats.instances.AllInstances
import cats.kernel.Eq
import cats.syntax.{EitherOps, AllSyntax}
import io.circe.testing.{ArbitraryInstances, EqInstances}
import org.scalatest.flatspec.AnyFlatSpec
import org.typelevel.discipline.Laws
import org.scalacheck.Properties
import org.scalatestplus.scalacheck.Checkers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

/** An opinionated stack of traits to improve consistency and reduce boilerplate
  * in circe tests.
  */
trait CirceSuite
    extends AnyFlatSpec
    with ScalaCheckPropertyChecks
    with AllInstances
    with AllSyntax
    with ArbitraryInstances
    with Checkers
    with EqInstances:

  override def convertToEqualizer[T](left: T): Equalizer[T] =
    sys.error("Intentionally ambiguous implicit for Equalizer")
//
//  given prioritizedCatsSyntaxEither[A, B](
//      eab: Either[A, B]
//  ): EitherOps[A, B] = new EitherOps(eab)

  def checkLaws(name: String, ruleSet: Laws#RuleSet): Unit =
    ruleSet.all.properties.zipWithIndex.foreach {
      case ((id, prop), 0) => name should s"obey $id" in check(prop)
      case ((id, prop), _) => it should s"obey $id" in check(prop)
    }

  given eqSeq[A: Eq]: Eq[Seq[A]] = Eq.by((_: Seq[A]).toVector)(
    cats.kernel.instances.vector.catsKernelStdEqForVector[A]
  )
