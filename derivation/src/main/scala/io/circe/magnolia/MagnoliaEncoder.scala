package io.circe.magnolia

import io.circe.magnolia.configured.Configuration
import io.circe.{Encoder, Json}
import magnolia._

private[magnolia] object MagnoliaEncoder {

  private[magnolia] def combine[T](caseClass: CaseClass[Encoder, T])(implicit config: Configuration): Encoder[T] = {
    val paramJsonKeyLookup = caseClass.parameters.map { p =>
      val jsonKeyAnnotation = p.annotations.collectFirst {
        case ann: JsonKey => ann
      }
      jsonKeyAnnotation match {
        case Some(ann) => p.label -> ann.value
        case None => p.label -> config.transformMemberNames(p.label)
      }
    }.toMap

    if (paramJsonKeyLookup.values.toList.distinct.size != caseClass.parameters.length) {
      throw new DerivationError(
        "Duplicate key detected after applying transformation function for case class parameters"
      )
    }

    new Encoder[T] {
      def apply(a: T): Json =
        Json.obj(caseClass.parameters.map { p =>
          val label = paramJsonKeyLookup.getOrElse(p.label, throw new IllegalStateException("Looking up a parameter label should always yield a value. This is a bug"))
          label -> p.typeclass(p.dereference(a))
        }: _*)
    }
  }

  private[magnolia] def dispatch[T](
    sealedTrait: SealedTrait[Encoder, T]
  )(implicit config: Configuration): Encoder[T] = {
    {
      val origTypeNames = sealedTrait.subtypes.map(_.typeName.short)
      val transformed = origTypeNames.map(config.transformConstructorNames).distinct
      if (transformed.length != origTypeNames.length) {
        throw new DerivationError(
          "Duplicate key detected after applying transformation function for " +
            "sealed trait child classes"
        )
      }
    }

    new Encoder[T] {
      def apply(a: T): Json = {
        sealedTrait.dispatch(a) { subtype =>
          val baseJson = subtype.typeclass(subtype.cast(a))
          val constructorName = config
            .transformConstructorNames(subtype.typeName.short)
          config.discriminator match {
            case Some(discriminator) => {
              val asObj = baseJson.asObject
              if (asObj.isEmpty) {
                println(a)
              }
              //TODOO: We are assuming object encoder. Can we enforce this?
              Json.fromJsonObject(asObj.get.add(discriminator, Json.fromString(constructorName)))
            }
            case None =>
              Json.obj(constructorName -> baseJson)
          }
        }
      }
    }
  }

}
