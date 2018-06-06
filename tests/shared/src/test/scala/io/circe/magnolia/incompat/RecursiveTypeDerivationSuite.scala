package io.circe.magnolia.incompat

import io.circe.{Encoder, Json}
import io.circe.magnolia.CirceMagnoliaSuite
import org.scalatest.OptionValues

/**
  * Problem here is that we expect the same behaviour as shapeless Lazy provides -
  * the derived codec itself would be visible and used to construct the codec for List using circe std one.
  * Magnolia doesn't see it and derives coproduct codec for List instead, which doesn't look nice for json API:
  * {{{
  * {
  *   "::" : {
  *      "head" : {
  *        "field" : 2,
  *        "recursion" : {
  *           "Nil" : "Nil"
  *         }
  *       },
  *       "tl$access$1" : {
  *         "Nil" : "Nil"
  *       }
  *     }
  *   }
  * }
  * }}}
  */
class RecursiveTypeDerivationSuite extends CirceMagnoliaSuite with OptionValues {

  import io.circe.magnolia.derivation.encoder.auto._

  case class Recursive(field: Int, recursion: List[Recursive])

  "Magnolia encoder" should "use available encoders while descending into a recursive type" in {

    val encoder = Encoder[Recursive]
    val json = encoder(Recursive(1, List(Recursive(2, Nil), Recursive(3, Nil))))
    json.asObject.flatMap(_ ("recursion")).value shouldBe Json.arr(
      Json.obj("field" -> Json.fromInt(2), "recursion" -> Json.arr()),
      Json.obj("field" -> Json.fromInt(3), "recursion" -> Json.arr())
    )
  }
}
