package io.circe.magnolia.incompat


import io.circe.{Encoder, Json}
import io.circe.magnolia.CirceMagnoliaSuite
import io.circe.magnolia.derivation.encoder.auto._
import org.scalatest.OptionValues

case class MapContainer(theMap: Map[String, List[Int]])

/**
  * Something weird is going on here.
  * During derivation for the right side of the map,
  * Magnolia notices default list codec only after it partially derives it as coproduct. Ending JSON is very bizarre:
  * {{{
  *  {
  *   "theMap" : {
  *     "f" : {
  *       "::" : [1, 2, 3]
  *     }
  *   }
  * }
  * }}}
  * This can be fixed by explicitly importing Encoder companion into scope (see 2nd test).
  *
  * Seems like magnolia macro in some cases has higher priority than companion provided implicits,
  * that are not directly imported in scope
  */
class PriorityIssueTest extends CirceMagnoliaSuite with OptionValues {

  // todo: uncomment when https://github.com/propensive/magnolia/issues/89 is fixed
  "Circe Magnolia Encoder" should "use instances from companion even if they are not imported" ignore {
    val encoder = Encoder[MapContainer]
    val json = encoder(MapContainer(Map("f" -> List(1, 2, 3))))
    json.hcursor.downField("theMap").downField("f").focus.value shouldBe
      Json.arr(Json.fromInt(1), Json.fromInt(2), Json.fromInt(3))
  }

  "Circe Magnolia Encoder" should "use instances from companion if they are explicitly imported" in {
    import Encoder._

    val encoder = Encoder[MapContainer]
    val json = encoder(MapContainer(Map("f" -> List(1, 2, 3))))
    json.hcursor.downField("theMap").downField("f").focus.value shouldBe
      Json.arr(Json.fromInt(1), Json.fromInt(2), Json.fromInt(3))
  }
}

