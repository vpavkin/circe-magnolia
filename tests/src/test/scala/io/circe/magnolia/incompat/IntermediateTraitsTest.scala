package io.circe.magnolia.incompat


import io.circe._
import io.circe.magnolia.CirceMagnoliaSuite
import org.scalatest.EitherValues

/**
  * This is another semantical difference from shapeless-based circe derivation.
  * Shapeless generic skips intermediate traits/abstract classes and forms a coproduct of only leaf types.
  * This makes perfect sense for many scenarios, JSON included.
  *
  * Magnolia, on the other hand, dispatches through all intermediate types.
  * For encoding it's not that bad, but for decoding it's a showstopper. See tests.
  */
class IntermediateTraitsTest extends CirceMagnoliaSuite with EitherValues {

  sealed trait T
  case class A(a: Int) extends T
  case class B(b: String) extends T
  sealed trait C extends T
  case class C1(c1: Int) extends C
  case class C2(c2: String) extends C


  import io.circe.magnolia.derivation.encoder.auto._
  import io.circe.magnolia.derivation.decoder.auto._

  val encoder = Encoder[T]
  val decoder = Decoder[T]

  // here JSON is deeper nested than when using circe-generic.
  // it's not that huge problem, until you try to decode a leaf, that is under an intermediate trait (next test)
  "Magnolia encoder" should "skip intermediate traits" in {
    val json = encoder(C1(5))
    json.hcursor.get[JsonObject]("C1").right.value
  }

  // when sending a message to JSON API we don't usually specify intermediate traits -
  // we just put the leaf type into the key.
  // Magnolia can't see the C1, because on the first dispatch it faces only A, B and C.
  "Magnolia decoder" should "skip intermediate traits" in {
    val json = Json.obj("C1" -> Json.obj("c1" -> Json.fromInt(2)))
    decoder(HCursor.fromJson(json)).right.value
  }
}
