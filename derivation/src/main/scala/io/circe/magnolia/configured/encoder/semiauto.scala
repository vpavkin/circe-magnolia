package io.circe.magnolia.configured.encoder

import io.circe.Encoder
import io.circe.magnolia.MagnoliaEncoder
import io.circe.magnolia.configured.Configuration
import magnolia1.*
import scala.deriving.Mirror

object semiauto:
  inline def deriveConfiguredMagnoliaEncoder[T](using
      Configuration,
      Mirror.Of[T]
  ): Encoder[T] = MagnoliaEncoder.derived[T]
