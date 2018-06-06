package io.circe.magnolia.derivation.encoder

import io.circe.Encoder
import io.circe.magnolia.MagnoliaEncoder
import magnolia.{CaseClass, Magnolia, SealedTrait}
import scala.language.experimental.macros

object auto {

  type Typeclass[T] = Encoder[T]

  private[magnolia] def combine[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
    MagnoliaEncoder.combine(caseClass)

  private[magnolia] def dispatch[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    MagnoliaEncoder.dispatch(sealedTrait)

  implicit def magnoliaEncoder[T]: Typeclass[T] = macro Magnolia.gen[T]
}
