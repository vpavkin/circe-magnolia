package io.circe.magnolia.configured.decoder

import io.circe.Decoder
import io.circe.magnolia.MagnoliaDecoder
import io.circe.magnolia.configured.Configuration
import magnolia1.{CaseClass, Magnolia, SealedTrait}

object auto {

  type Typeclass[T] = Decoder[T]

  def join[T](caseClass: CaseClass[Typeclass, T])(implicit configuration: Configuration): Typeclass[T] =
    MagnoliaDecoder.join(caseClass)

  def split[T](sealedTrait: SealedTrait[Typeclass, T])(implicit configuration: Configuration): Typeclass[T] =
    MagnoliaDecoder.split(sealedTrait)

  implicit def magnoliaConfiguredDecoder[T]: Typeclass[T] = macro Magnolia.gen[T]
}
