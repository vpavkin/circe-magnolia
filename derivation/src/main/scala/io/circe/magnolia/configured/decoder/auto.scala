package io.circe.magnolia.configured.decoder

import io.circe.Decoder
import io.circe.magnolia.MagnoliaDecoder
import io.circe.magnolia.configured.Configuration
import magnolia1.*
import scala.deriving.*

object auto:
  inline given [T](using Configuration, Mirror.Of[T]): Decoder[T] =
    MagnoliaDecoder.derived[T]
