package io.circe.magnolia.derivation.encoder

import io.circe.Encoder
import io.circe.magnolia.MagnoliaEncoder
import io.circe.magnolia.configured.Configuration
import magnolia.{CaseClass, Magnolia, SealedTrait}
import scala.language.experimental.macros

object semiauto {

  type Typeclass[T] = Encoder[T]

  def combine[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
    MagnoliaEncoder.combine(caseClass)(Configuration.default)

  def dispatch[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    MagnoliaEncoder.dispatch(sealedTrait)(Configuration.default)

  def deriveMagnoliaEncoder[T]: Typeclass[T] = macro Magnolia.gen[T]
}
