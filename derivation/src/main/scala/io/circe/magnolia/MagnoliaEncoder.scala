package io.circe.magnolia

import io.circe.magnolia.configured.Configuration
import io.circe.{Encoder, Json}
import magnolia._

private[magnolia] object MagnoliaEncoder {

  private[magnolia] def combine[T](
    caseClass: CaseClass[Encoder, T]
  )(implicit config: Configuration): Encoder[T] = {
    {
      val origParameterNames = caseClass.parameters.map(_.label)
      val transformed = origParameterNames.map(config.transformMemberNames).distinct
      if (transformed.length != origParameterNames.length) {
        throw new DerivationError("Duplicate key detected after applying transformation function for case class parameters")
      }
    }
    new Encoder[T] {
    def apply(a: T): Json = {
      Json.obj(caseClass.parameters.map { p =>
        val label = config.transformMemberNames(p.label)
        label -> p.typeclass(p.dereference(a))
      }: _*)
    }
  }}

  private[magnolia] def dispatch[T](
    sealedTrait: SealedTrait[Encoder, T]
  )(implicit config: Configuration): Encoder[T] = {
    {
      val origTypeNames = sealedTrait.subtypes.map(_.typeName.short)
      val transformed = origTypeNames.map(config.transformConstructorNames).distinct
      if (transformed.length != origTypeNames.length) {
        throw new DerivationError("Duplicate key detected after applying transformation function for " +
          "sealed trait child classes")
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
              Json.fromJsonObject(baseJson.asObject.get.add(discriminator, Json.fromString(constructorName)))
            }
            case None =>
              Json.obj(constructorName -> baseJson)
          }
        }
      }
    }
  }

}
