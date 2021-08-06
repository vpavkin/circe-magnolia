package io.circe.tests

import cats.instances.AllInstances
import cats.kernel.Eq
import cats.syntax.functor.given
import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import io.circe.testing.ArbitraryInstances
import org.scalacheck.{Arbitrary, Gen}
import io.circe.magnolia.JsonKey

package object examples extends AllInstances with ArbitraryInstances:
  val glossary: Json = Json.obj(
    "glossary" -> Json.obj(
      "title" -> Json.fromString("example glossary"),
      "GlossDiv" -> Json.obj(
        "title" -> Json.fromString("S"),
        "GlossList" -> Json.obj(
          "GlossEntry" -> Json.obj(
            "ID" -> Json.fromString("SGML"),
            "SortAs" -> Json.fromString("SGML"),
            "GlossTerm" -> Json.fromString(
              "Standard Generalized Markup Language"
            ),
            "Acronym" -> Json.fromString("SGML"),
            "Abbrev" -> Json.fromString("ISO 8879:1986"),
            "GlossDef" -> Json.obj(
              "para" -> Json.fromString(
                "A meta-markup language, used to create markup languages such as DocBook."
              ),
              "GlossSeeAlso" -> Json
                .arr(Json.fromString("GML"), Json.fromString("XML"))
            ),
            "GlossSee" -> Json.fromString("markup")
          )
        )
      )
    )
  )

package examples:

  import io.circe.HCursor

  case class Box[A](a: A)

  object Box:
    given [A: Eq]: Eq[Box[A]] = Eq.by(_.a)
    given [A](using A: Arbitrary[A]): Arbitrary[Box[A]] =
      Arbitrary(A.arbitrary.map(Box(_)))

  case class Qux[A](i: Int, a: A, j: Int)

  object Qux:
    given [A: Eq]: Eq[Qux[A]] = Eq.by(q => (q.i, q.a, q.j))

    given [A](using A: Arbitrary[A]): Arbitrary[Qux[A]] =
      Arbitrary(
        for
          i <- Arbitrary.arbitrary[Int]
          a <- A.arbitrary
          j <- Arbitrary.arbitrary[Int]
        yield Qux(i, a, j)
      )

  case class Wub(x: Long)

  object Wub:
    given Eq[Wub] = Eq.by(_.x)
    given Arbitrary[Wub] = Arbitrary(
      Arbitrary.arbitrary[Long].map(Wub(_))
    )

    val decodeWub: Decoder[Wub] =
      Decoder[Long].prepare(_.downField("x")).map(Wub(_))
    val encodeWub: Encoder[Wub] =
      Encoder.instance(w => Json.obj("x" -> Json.fromLong(w.x)))

  sealed trait Foo
  case class Bar(i: Int, s: String) extends Foo
  case class Bam(w: Wub, d: Double) extends Foo

  object Bar:
    given Eq[Bar] = Eq.fromUniversalEquals
    given Arbitrary[Bar] = Arbitrary(
      for
        i <- Arbitrary.arbitrary[Int]
        s <- Arbitrary.arbitrary[String]
      yield Bar(i, s)
    )

    val decodeBar: Decoder[Bar] = Decoder.forProduct2("i", "s")(Bar.apply)
    val encodeBar: Encoder[Bar] = Encoder.forProduct2("i", "s") {
      case Bar(i, s) => (i, s)
    }

  case class Baz(xs: List[String])

  object Baz:
    given Eq[Baz] = Eq.fromUniversalEquals
    given Arbitrary[Baz] = Arbitrary(
      Arbitrary.arbitrary[List[String]].map(Baz.apply)
    )

    given Decoder[Baz] = Decoder[List[String]].map(Baz(_))
    given Encoder[Baz] = Encoder.instance { case Baz(xs) =>
      Json.fromValues(xs.map(Json.fromString))
    }

  object Bam:
    given Eq[Bam] = Eq.fromUniversalEquals
    given Arbitrary[Bam] = Arbitrary(
      for
        w <- Arbitrary.arbitrary[Wub]
        d <- Arbitrary.arbitrary[Double]
      yield Bam(w, d)
    )

    val decodeBam: Decoder[Bam] =
      Decoder.forProduct2("w", "d")(Bam.apply)(Wub.decodeWub, implicitly)
    val encodeBam: Encoder[Bam] =
      Encoder.forProduct2[Bam, Wub, Double]("w", "d") { case Bam(w, d) =>
        (w, d)
      }(Wub.encodeWub, implicitly)

  object Foo:
    given Eq[Foo] = Eq.fromUniversalEquals

    given Arbitrary[Foo] = Arbitrary(
      Gen.oneOf(
        Arbitrary.arbitrary[Bar],
        Arbitrary.arbitrary[Bam]
      )
    )

    val encodeFoo: Encoder[Foo] = Encoder.instance {
      case bar @ Bar(_, _) => Json.obj("Bar" -> Bar.encodeBar(bar))
      case bam @ Bam(_, _) => Json.obj("Bam" -> Bam.encodeBam(bam))
    }

    val decodeFoo: Decoder[Foo] = Decoder.instance { c =>
      c.keys.map(_.toVector) match
        case Some(Vector("Bar")) => c.get("Bar")(Bar.decodeBar.widen)
        case Some(Vector("Bam")) => c.get("Bam")(Bam.decodeBam.widen)
        case _                   => Left(DecodingFailure("Foo", c.history))
    }

  sealed trait Sealed
  final case class SubtypeWithExplicitInstance(xs: List[String]) extends Sealed
  final case class AnotherSubtype(i: Int) extends Sealed

  object Sealed:
    given Arbitrary[Sealed] = Arbitrary(
      Gen.oneOf(
        Arbitrary.arbitrary[SubtypeWithExplicitInstance],
        Arbitrary.arbitrary[AnotherSubtype]
      )
    )
    given Eq[Sealed] = Eq.fromUniversalEquals
    
    given Arbitrary[SubtypeWithExplicitInstance] = Arbitrary(
      for strs <- Arbitrary.arbitrary[List[String]]
        yield SubtypeWithExplicitInstance(strs)
    )
    
    given Arbitrary[AnotherSubtype] = Arbitrary(
      for i <- Arbitrary.arbitrary[Int]
        yield AnotherSubtype(i)
    )
    given Encoder[SubtypeWithExplicitInstance] =
      (a: SubtypeWithExplicitInstance) =>
        Json.fromValues(a.xs.map(Json.fromString))
        
    given Decoder[SubtypeWithExplicitInstance] = (a: HCursor) =>
      a.as[List[String]].map(SubtypeWithExplicitInstance(_))

   
    

  sealed trait Organization
  final case class Public(name: String, taxCategory: String)
      extends Organization
  final case class NonProfit(orgName: String) extends Organization

  final case class ClassWithDefaults(
      required: String,
      field: String = "defaultValue",
      defaultOptSome: Option[String] = Some("defaultOptSome"),
      defaultNone: Option[String] = None,
      defaultOptNotSpecified: Option[String]
  )

  object ClassWithDefaults:
    given Eq[ClassWithDefaults] = Eq.fromUniversalEquals
    given Arbitrary[ClassWithDefaults] = Arbitrary(
      for
        required <- Arbitrary.arbitrary[String]
        field <- Arbitrary.arbitrary[String]
        defaultOptSome <- Arbitrary.arbitrary[Option[String]]
        defaultNone <- Arbitrary.arbitrary[Option[String]]
        defaultOptNotSpecified <- Arbitrary.arbitrary[Option[String]]
      yield ClassWithDefaults(
        required = required,
        field = field,
        defaultOptSome = defaultOptSome,
        defaultNone = defaultNone,
        defaultOptNotSpecified = defaultOptNotSpecified
      )
    )

@main def runTest =
  import examples.*
  import io.circe.magnolia.derivation.encoder.auto.given
  val box = Box(3)
  val qux = Qux(3, "s", 4)
  val wub = Wub(4L)
  val foo: Foo = Bar(4, "l")

  import io.circe.syntax.given

  println(box.asJson.spaces2)
  println(qux.asJson.spaces2)
  println(wub.asJson.spaces2)
  println(foo.asJson.spaces2)
/*

{
  "a" : 3
}
{
  "i" : 3,
  "a" : "s",
  "j" : 4
}
{
  "x" : 4
}
{
  "Bar" : {
    "i" : 4,
    "s" : "l"
  }
}
 */
