package io.circe.tests.examples

import cats.kernel.Eq
import io.circe.{Decoder, Encoder}
import org.scalacheck.Arbitrary

case class OptionalString(value: String):
  def toOption: Option[String] = value match
    case ""    => None
    case other => Some(other)

object OptionalString:
  def fromOption(o: Option[String]): OptionalString =
    OptionalString(o.getOrElse(""))

  given decodeOptionalString: Decoder[OptionalString] =
    Decoder[Option[String]].map(fromOption)

  given encodeOptionalString: Encoder[OptionalString] =
    Encoder[Option[String]].contramap(_.toOption)

  given eqOptionalString: Eq[OptionalString] = Eq.fromUniversalEquals

  given arbitraryOptionalString: Arbitrary[OptionalString] =
    Arbitrary(Arbitrary.arbitrary[Option[String]].map(fromOption))

case class WrappedOptionalField(f: OptionalString)

object WrappedOptionalField:
  given decodeWrappedOptionalField: Decoder[WrappedOptionalField] =
    Decoder.forProduct1("f")(WrappedOptionalField.apply)

  given encodeWrappedOptionalField: Encoder[WrappedOptionalField] =
    Encoder.forProduct1("f")(_.f)

  given eqWrappedOptionalField: Eq[WrappedOptionalField] =
    Eq.fromUniversalEquals

  given arbitraryWrappedOptionalField: Arbitrary[WrappedOptionalField] =
    Arbitrary(Arbitrary.arbitrary[OptionalString].map(WrappedOptionalField(_)))
