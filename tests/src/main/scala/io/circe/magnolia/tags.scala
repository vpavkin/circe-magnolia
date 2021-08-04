package io.circe.magnolia

import io.circe.{Decoder, Encoder}

trait Tagged[Tag]
type @@[A, Tag] = A & Tagged[Tag]
def tag[Tag]: [A] => A => A @@ Tag = [A] => (a: A) => a.asInstanceOf[A @@ Tag]

object tags:
  trait Magnolia
  trait Circe

  final class TaggedDecoder[T, A](val inner: Decoder[A]):
    def toTagged: Decoder[A] @@ T = tag[T](inner)
  final class TaggedEncoder[T, A](val inner: Encoder[A]):
    def toTagged: Encoder[A] @@ T = tag[T](inner)

  final class PartialTagged[T]:
    def apply[A](decoder: Decoder[A]): TaggedDecoder[T, A] = new TaggedDecoder(
      decoder
    )
    def apply[A](encoder: Encoder[A]): TaggedEncoder[T, A] = new TaggedEncoder(
      encoder
    )

  def mkTag[T]: PartialTagged[T] = new PartialTagged[T]
