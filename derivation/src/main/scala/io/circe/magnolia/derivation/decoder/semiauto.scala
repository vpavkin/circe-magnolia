package io.circe.magnolia.derivation.decoder

import io.circe.Decoder
import io.circe.magnolia.MagnoliaDecoder
import io.circe.magnolia.configured.Configuration
import magnolia1.{CaseClass, Magnolia, SealedTrait}

object semiauto {

  type Typeclass[T] = Decoder[T]

  def join[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
    MagnoliaDecoder.join(caseClass)(Configuration.default)

  def split[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    MagnoliaDecoder.split(sealedTrait)(Configuration.default)

  def deriveMagnoliaDecoder[T]: Typeclass[T] = macro Magnolia.gen[T]
}
