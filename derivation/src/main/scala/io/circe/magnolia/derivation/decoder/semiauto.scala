package io.circe.magnolia.derivation.decoder

import io.circe.Decoder
import io.circe.magnolia.MagnoliaDecoder
import io.circe.magnolia.configured.Configuration
import magnolia1.*
import scala.deriving.*

object semiauto:

  inline def deriveMagnoliaDecoder[T](using Mirror.Of[T]): Decoder[T] =
    given Configuration = Configuration.default
    MagnoliaDecoder.derived[T]
