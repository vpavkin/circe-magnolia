package io.circe.magnolia.derivation.encoder

import io.circe.Encoder
import io.circe.magnolia.MagnoliaEncoder
import io.circe.magnolia.configured.Configuration
import magnolia1.*

import scala.deriving.Mirror

object auto:

  inline given autoencoder[T](using inline m: Mirror.Of[T]): Encoder[T] =
    given Configuration = Configuration.default
    MagnoliaEncoder.derived[T]
