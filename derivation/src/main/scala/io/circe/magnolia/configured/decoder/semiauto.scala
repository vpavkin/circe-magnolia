package io.circe.magnolia.configured.decoder

import io.circe.Decoder
import io.circe.magnolia.MagnoliaDecoder
import io.circe.magnolia.configured.Configuration
import magnolia.{CaseClass, Magnolia, SealedTrait}
import scala.language.experimental.macros

object semiauto {

  type Typeclass[T] = Decoder[T]

  def combine[T](caseClass: CaseClass[Typeclass, T])(implicit configuration: Configuration): Typeclass[T] =
    MagnoliaDecoder.combine(caseClass)

  def dispatch[T](sealedTrait: SealedTrait[Typeclass, T])(implicit configuration: Configuration): Typeclass[T] =
    MagnoliaDecoder.dispatch(sealedTrait)

  def deriveConfiguredMagnoliaDecoder[T]: Typeclass[T] = macro Magnolia.gen[T]
}
