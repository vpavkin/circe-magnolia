package io.circe.tests

import cats.instances.AllInstances
import cats.kernel.Eq
import cats.syntax.{AllSyntax, EitherOps}
import io.circe.testing.{ArbitraryInstances, EqInstances}
import org.scalatest.FlatSpec
import org.scalatestplus.scalacheck.{Checkers, ScalaCheckDrivenPropertyChecks}
import org.typelevel.discipline.Laws

/**
  * An opinionated stack of traits to improve consistency and reduce boilerplate in circe tests.
  */
trait CirceSuite
    extends FlatSpec
    with ScalaCheckDrivenPropertyChecks
    with AllInstances
    with AllSyntax
    with ArbitraryInstances
    with Checkers
    with EqInstances {

  override def convertToEqualizer[T](left: T): Equalizer[T] =
    sys.error("Intentionally ambiguous implicit for Equalizer")

  implicit def prioritizedCatsSyntaxEither[A, B](eab: Either[A, B]): EitherOps[A, B] = new EitherOps(eab)

  def checkLaws(name: String, ruleSet: Laws#RuleSet): Unit =
    ruleSet.all.properties.zipWithIndex.foreach {
      case ((id, prop), 0) => name should s"obey $id" in check(prop)
      case ((id, prop), _) => it should s"obey $id" in check(prop)
    }

  implicit def eqSeq[A: Eq]: Eq[Seq[A]] = Eq.by((_: Seq[A]).toVector)(
    cats.kernel.instances.vector.catsKernelStdEqForVector[A]
  )
}
