package io.circe.magnolia.derivation.encoder

import io.circe.Encoder
import io.circe.magnolia.MagnoliaEncoder
import io.circe.magnolia.configured.Configuration
import magnolia1.{CaseClass, Magnolia, SealedTrait}

object semiauto {

  type Typeclass[T] = Encoder[T]

  def join[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
    MagnoliaEncoder.join(caseClass)(Configuration.default)

  def split[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    MagnoliaEncoder.split(sealedTrait)(Configuration.default)

  def deriveMagnoliaEncoder[T]: Typeclass[T] = macro Magnolia.gen[T]
}
