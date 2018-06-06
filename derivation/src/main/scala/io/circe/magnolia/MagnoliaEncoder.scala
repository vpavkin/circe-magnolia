package io.circe.magnolia

import io.circe.{Encoder, Json}
import magnolia._

private[magnolia] object MagnoliaEncoder {

  private[magnolia] def combine[T](caseClass: CaseClass[Encoder, T]): Encoder[T] = new Encoder[T] {
    def apply(a: T): Json = {
      if (caseClass.isValueClass) {
        val p = caseClass.parameters.head
        p.typeclass(p.dereference(a))
      }
      else if (caseClass.isObject)
        Json.fromString(caseClass.typeName.short)
      else Json.obj(
        caseClass.parameters.map(p =>
          p.label -> p.typeclass(p.dereference(a))
        ): _*
      )
    }
  }

  private[magnolia] def dispatch[T](sealedTrait: SealedTrait[Encoder, T]): Encoder[T] = new Encoder[T] {
    def apply(a: T): Json =
      sealedTrait.dispatch(a) { subtype =>
        Json.obj(
          subtype.typeName.short -> subtype.typeclass(subtype.cast(a))
        )
      }
  }
}
