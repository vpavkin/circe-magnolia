package io.circe.magnolia

import cats.syntax.either._
import io.circe.{Decoder, DecodingFailure, HCursor}
import io.circe.Decoder.Result
import magnolia._
import mercator._

private[magnolia] object MagnoliaDecoder {

  private[magnolia] def combine[T](caseClass: CaseClass[Decoder, T]): Decoder[T] = new Decoder[T] {
    def apply(c: HCursor): Result[T] =
      caseClass.constructMonadic(p => c.downField(p.label).as[p.PType](p.typeclass))
  }

  private[magnolia] def dispatch[T](sealedTrait: SealedTrait[Decoder, T]): Decoder[T] = new Decoder[T] {
    lazy val knownSubTypes = sealedTrait.subtypes.map(_.typeName.short).mkString(",")
    
    def apply(c: HCursor): Result[T] = c.keys match {
      case Some(keys) if keys.size == 1 =>
        val key = keys.head
        for {
          theSubtype <- Either.fromOption(
            sealedTrait.subtypes.find(_.typeName.short == key),
            DecodingFailure(
              s"""Can't decode coproduct type: couldn't find matching subtype.
                 |JSON: ${c.value},
                 |Key: $key
                 |Known subtypes: $knownSubTypes\n""".stripMargin,
              c.history
            ))

          result <- c.get(key)(theSubtype.typeclass)
        } yield result
      case _ =>
        Left(DecodingFailure(
          s"""Can't decode coproduct type: zero or several keys were found, while coproduct type requires exactly one.
             |JSON: ${c.value},
             |Keys: ${c.keys.map(_.mkString(","))}
             |Known subtypes: $knownSubTypes\n""".stripMargin,
          c.history
        ))
    }
  }
}
