package io.circe.magnolia.configured.encoder

import io.circe.Encoder
import io.circe.magnolia.MagnoliaEncoder
import io.circe.magnolia.configured.Configuration
import magnolia1.{CaseClass, Magnolia, SealedTrait}

object semiauto {

  type Typeclass[T] = Encoder[T]

  def join[T](caseClass: CaseClass[Typeclass, T])(implicit configuration: Configuration): Typeclass[T] =
    MagnoliaEncoder.join(caseClass)

  def split[T](sealedTrait: SealedTrait[Typeclass, T])(implicit configuration: Configuration): Typeclass[T] =
    MagnoliaEncoder.split(sealedTrait)

  def deriveConfiguredMagnoliaEncoder[T]: Typeclass[T] = macro Magnolia.gen[T]
}
