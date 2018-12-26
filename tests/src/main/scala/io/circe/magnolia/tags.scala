package io.circe.magnolia

import io.circe.{Decoder, Encoder}
import shapeless.tag.@@

object tags {
  sealed trait Magnolia
  sealed trait Circe

  final class TaggedDecoder[T, A](val inner: Decoder[A]) extends AnyVal {
    def toTagged: Decoder[A] @@ T = {
      shapeless.tag[T](inner)
    }
  }
  final class TaggedEncoder[T, A](val inner: Encoder[A]) extends AnyVal {
    def toTagged: Encoder[A] @@ T = {
      shapeless.tag[T](inner)
    }
  }

  final class PartialTagged[T] {
    def apply[A](decoder: Decoder[A]): TaggedDecoder[T, A] = new TaggedDecoder(decoder)
    def apply[A](encoder: Encoder[A]): TaggedEncoder[T, A] = new TaggedEncoder(encoder)
  }

  def tag[T]: PartialTagged[T] = new PartialTagged[T]

}
