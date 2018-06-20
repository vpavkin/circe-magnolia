package io.circe.magnolia.derivation.decoder

import io.circe.Decoder
import io.circe.magnolia.MagnoliaDecoder
import magnolia.{CaseClass, Magnolia, SealedTrait}
import scala.language.experimental.macros

object semiauto {

  type Typeclass[T] = Decoder[T]

  def combine[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
    MagnoliaDecoder.combine(caseClass)

  def dispatch[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    MagnoliaDecoder.dispatch(sealedTrait)

  def deriveMagnoliaDecoder[T]: Typeclass[T] = macro Magnolia.gen[T]
}
