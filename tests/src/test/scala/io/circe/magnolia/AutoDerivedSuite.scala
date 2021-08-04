package io.circe.magnolia

import io.circe.magnolia.derivation.decoder.auto.given
import io.circe.magnolia.derivation.encoder.auto.given
import io.circe.testing.CodecTests
import io.circe.tests.CirceSuite
import io.circe.tests.examples.*
import io.circe.{Decoder, Encoder, Json}
import tags.*

class AutoDerivedSuite extends CirceSuite:
  import AutoDerivedSuiteInputs.*

  // TODO: All these imports are temporary workaround for https://github.com/propensive/magnolia/issues/89
  import Encoder.*
  import Decoder.*

  checkLaws("Codec[Tuple1[Int]]", CodecTests[Tuple1[Int]].unserializableCodec)
  checkLaws(
    "Codec[(Int, Int, Foo)]",
    CodecTests[(Int, Int, Foo)].unserializableCodec
  )
  checkLaws("Codec[Qux[Int]]", CodecTests[Qux[Int]].unserializableCodec)
  checkLaws("Codec[Seq[Foo]]", CodecTests[Seq[Foo]].unserializableCodec)
  checkLaws("Codec[Baz]", CodecTests[Baz].unserializableCodec)
  checkLaws("Codec[Foo]", CodecTests[Foo].unserializableCodec)
  checkLaws(
    "Codec[OuterCaseClassExample]",
    CodecTests[OuterCaseClassExample].unserializableCodec
  )

  "A generically derived codec" should "not interfere with base instances" in forAll {
    (is: List[Int]) =>
      val json = Encoder[List[Int]].apply(is)

      assert(
        json === Json.fromValues(is.map(Json.fromInt)) && json
          .as[List[Int]] === Right(is)
      )
  }

  it should "not be derived for Object" in {
    assertTypeError("Decoder[Object]")
    assertTypeError("Encoder[Object]")
  }

  it should "not be derived for AnyRef" in {
    assertTypeError("Decoder[AnyRef]")
    assertTypeError("Encoder[AnyRef]")
  }

  "Generic decoders" should "not interfere with defined decoders" in forAll {
    (xs: List[String]) =>
      val json = Json.obj(
        "SubtypeWithExplicitInstance" -> Json.fromValues(
          xs.map(Json.fromString)
        )
      )
      val ch = Decoder[Sealed].apply(json.hcursor)
      val res = ch === Right(SubtypeWithExplicitInstance(xs): Sealed)
      assert(res)
  }

  "Generic encoders" should "not interfere with defined encoders" in forAll {
    (xs: List[String]) =>
      val json = Json.obj(
        "SubtypeWithExplicitInstance" -> Json.fromValues(
          xs.map(Json.fromString)
        )
      )

      assert(
        Encoder[Sealed].apply(SubtypeWithExplicitInstance(xs): Sealed) === json
      )
  }
