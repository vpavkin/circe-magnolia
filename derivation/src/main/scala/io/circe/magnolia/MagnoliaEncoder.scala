package io.circe.magnolia

import io.circe.magnolia.configured.Configuration
import io.circe.{Encoder, Json}
import magnolia1.*

import scala.deriving.Mirror

private[magnolia] object MagnoliaEncoder:
  self =>

  inline def derived[T: Mirror.Of](using Configuration): Encoder[T] =
    val derivation = new Derivation[Encoder]:
      override def split[T](ctx: SealedTrait[Encoder, T]) = self.split[T](ctx)
      override def join[T](ctx: CaseClass[Encoder, T]): Encoder[T] =
        self.join[T](ctx)

    derivation.derived[T]

  inline def join[T](
      inline caseClass: CaseClass[Encoder, T]
  )(using config: Configuration): Encoder[T] =
    val paramJsonKeyLookup = caseClass.params.map { p =>
      val jsonKeyAnnotation = p.annotations.collectFirst { case ann: JsonKey =>
        ann
      }
      jsonKeyAnnotation match
        case Some(ann) => p.label -> ann.value
        case None      => p.label -> config.transformMemberNames(p.label)
    }.toMap

    if paramJsonKeyLookup.values.toList.distinct.size != caseClass.params.length then
      throw new DerivationError(
        "Duplicate key detected after applying transformation function for case class parameters"
      )

    new Encoder[T]:
      def apply(a: T): Json =
        Json.obj(caseClass.params.map { p =>
          val label = paramJsonKeyLookup.getOrElse(
            p.label,
            throw new IllegalStateException(
              "Looking up a parameter label should always yield a value. This is a bug"
            )
          )
          label -> p.typeclass(p.deref(a))
        }*)

  inline def split[T](
      inline
      sealedTrait: SealedTrait[Encoder, T]
  )(using config: Configuration): Encoder[T] =
    val origTypeNames = sealedTrait.subtypes.map(_.typeInfo.short)
    val transformed =
      origTypeNames.map(config.transformConstructorNames).distinct
    if transformed.length != origTypeNames.length then
      throw new DerivationError(
        "Duplicate key detected after applying transformation function for " +
          "sealed trait child classes"
      )

    new Encoder[T]:
      def apply(a: T): Json =
        sealedTrait.choose(a) { subtype =>
          val baseJson = subtype.typeclass(subtype.cast(a))
          val constructorName = config
            .transformConstructorNames(subtype.typeInfo.short)
          config.discriminator match
            case Some(discriminator) =>
              // Note: Here we handle the edge case where a subtype of a sealed trait has a custom encoder which does not encode
              // encode into a JSON object and thus we cannot insert the discriminator key. In this case we fallback
              // to the non-discriminator case for this subtype. This is same as the behavior of circe-generic-extras
              baseJson.asObject match
                case Some(jsObj) =>
                  Json.fromJsonObject(
                    jsObj.add(discriminator, Json.fromString(constructorName))
                  )
                case None => Json.obj(constructorName -> baseJson)
            case None =>
              Json.obj(constructorName -> baseJson)
        }
