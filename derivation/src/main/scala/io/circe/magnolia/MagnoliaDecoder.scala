package io.circe.magnolia

import cats.syntax.either.*
import io.circe.{Decoder, DecodingFailure, HCursor}
import io.circe.Decoder.Result
import io.circe.magnolia.configured.Configuration
import magnolia1.*
import magnolia1.SealedTrait.Subtype
import scala.deriving.*

private[magnolia] object MagnoliaDecoder:
  self =>

  inline def derived[T](using c: Configuration, inline m: Mirror.Of[T]): Decoder[T] =
    val derivation = new Derivation[Decoder]:
      override def split[T](ctx: SealedTrait[Decoder, T]) = self.split[T](ctx)
      override def join[T](ctx: CaseClass[Decoder, T]): Decoder[T] =
        self.join[T](ctx)

    derivation.derived[T]

  def join[T](
      caseClass: CaseClass[Decoder, T]
  )(using configuration: Configuration): Decoder[T] =
    val paramJsonKeyLookup: Map[String, String] = caseClass.params.map { p =>
      val jsonKeyAnnotation = p.annotations.collectFirst { case ann: JsonKey =>
        ann
      }
      jsonKeyAnnotation match
        case Some(ann) => p.label -> ann.value
        case None      => p.label -> configuration.transformMemberNames(p.label)
    }.toMap

    if paramJsonKeyLookup.values.toList.distinct.length != caseClass.params.length then
      throw new DerivationError(
        "Duplicate key detected after applying transformation function for case class parameters"
      )

    if configuration.useDefaults then
      new Decoder[T]:
        override def apply(c: HCursor): Result[T] =
          caseClass
            .constructEither { p =>
              val key = paramJsonKeyLookup.getOrElse(
                p.label,
                throw new IllegalStateException(
                  "Looking up a parameter label should always yield a value. This is a bug"
                )
              )
              val keyCursor = c.downField(key)
              keyCursor.focus match
                case Some(_) => p.typeclass.tryDecode(keyCursor)
                case None =>
                  p.default.fold {
                    // Some decoders (in particular, the default Option[T] decoder) do special things when a key is missing,
                    // so we give them a chance to do their thing here.
                    p.typeclass.tryDecode(keyCursor)
                  }(x => Right(x))
            }
            .leftMap(_.head)
    else
      new Decoder[T]:
        def apply(c: HCursor): Result[T] =
          caseClass
            .constructEither { p =>
              p.typeclass.tryDecode(
                c.downField(
                  paramJsonKeyLookup.getOrElse(
                    p.label,
                    throw new IllegalStateException(
                      "Looking up a parameter label should always yield a value. This is a bug"
                    )
                  )
                )
              )
            }
            .leftMap(_.head)

  private[magnolia] def split[T](
      sealedTrait: SealedTrait[Decoder, T]
  )(using configuration: Configuration): Decoder[T] =

    val constructorLookup: Map[String, Subtype[Decoder, T, ?]] =
      sealedTrait.subtypes.map { s =>
        configuration.transformConstructorNames(s.typeInfo.short) -> s
      }.toMap

    if constructorLookup.size != sealedTrait.subtypes.length then
      throw new DerivationError(
        "Duplicate key detected after applying transformation function for case class parameters"
      )

    configuration.discriminator match
      case Some(discriminator) =>
        new DiscriminatedDecoder[T](discriminator, constructorLookup)
      case None => new NonDiscriminatedDecoder[T](constructorLookup)

  private[magnolia] class NonDiscriminatedDecoder[T](
      constructorLookup: Map[String, Subtype[Decoder, T, ?]]
  ) extends Decoder[T]:
    private val knownSubTypes =
      constructorLookup.keys.toSeq.sorted.mkString(",")

    override def apply(c: HCursor): Result[T] =
      c.keys match
        case Some(keys) if keys.size == 1 =>
          val key = keys.head
          for
            theSubtype <- Either.fromOption(
              constructorLookup.get(key),
              DecodingFailure(
                s"""Can't decode coproduct type: couldn't find matching subtype.
                       |JSON: ${c.value},
                       |Key: $key
                       |Known subtypes: $knownSubTypes\n""".stripMargin,
                c.history
              )
            )

            result <- c.get(key)(theSubtype.typeclass)
          yield result
        case _ =>
          Left(
            DecodingFailure(
              s"""Can't decode coproduct type: zero or several keys were found, while coproduct type requires exactly one.
                   |JSON: ${c.value},
                   |Keys: ${c.keys.map(_.mkString(","))}
                   |Known subtypes: $knownSubTypes\n""".stripMargin,
              c.history
            )
          )

  private[magnolia] class DiscriminatedDecoder[T](
      discriminator: String,
      constructorLookup: Map[String, Subtype[Decoder, T, ?]]
  ) extends Decoder[T]:
    val knownSubTypes = constructorLookup.keys.toSeq.sorted.mkString(",")

    override def apply(c: HCursor): Result[T] =
      c.downField(discriminator).as[String] match
        case Left(_) =>
          Left(
            DecodingFailure(
              s"""
             |Can't decode coproduct type: couldn't find discriminator or is not of type String.
             |JSON: ${c.value}
             |discriminator key: discriminator
              """.stripMargin,
              c.history
            )
          )
        case Right(ctorName) =>
          constructorLookup.get(ctorName) match
            case Some(subType) => subType.typeclass.apply(c)
            case None =>
              Left(
                DecodingFailure(
                  s"""
               |Can't decode coproduct type: constructor name not found in known constructor names
               |JSON: ${c.value}
               |Allowed discriminators: $knownSubTypes
              """.stripMargin,
                  c.history
                )
              )
