package io.circe.magnolia.configured.encoder

import io.circe.Encoder
import io.circe.magnolia.MagnoliaEncoder
import io.circe.magnolia.configured.Configuration
import magnolia.{CaseClass, Magnolia, SealedTrait}

object semiauto {

  type Typeclass[T] = Encoder[T]

  def combine[T](caseClass: CaseClass[Typeclass, T])(implicit configuration: Configuration): Typeclass[T] =
    MagnoliaEncoder.combine(caseClass)

  def dispatch[T](sealedTrait: SealedTrait[Typeclass, T])(implicit configuration: Configuration): Typeclass[T] =
    MagnoliaEncoder.dispatch(sealedTrait)

  def deriveConfiguredMagnoliaEncoder[T]: Typeclass[T] = macro Magnolia.gen[T]
}
