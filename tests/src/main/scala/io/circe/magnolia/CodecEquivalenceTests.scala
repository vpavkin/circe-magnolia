package io.circe.magnolia

import cats.instances.either.*
import cats.kernel.Eq
import cats.laws.*
import cats.laws.discipline.*
import io.circe.magnolia.tags.{TaggedDecoder, TaggedEncoder}
import io.circe.{Decoder, Encoder, Json}
import org.scalacheck.{Arbitrary, Prop, Shrink}
import org.typelevel.discipline.Laws
import shapeless.tag.@@

trait CodecEquivalenceLaws[A]:
  def circeDecoder: Decoder[A] @@ tags.Circe
  def magnoliaDecoder: Decoder[A] @@ tags.Magnolia

  def circeEncoder: Encoder[A] @@ tags.Circe
  def magnoliaEncoder: Encoder[A] @@ tags.Magnolia

  def encoderEq(a: A): IsEq[Json] =
    circeEncoder(a) <-> magnoliaEncoder(a)

  def decoderEq(a: A): IsEq[Decoder.Result[A]] =
    val encoded = magnoliaEncoder(a)
    encoded.as(circeDecoder) <-> encoded.as(magnoliaDecoder)

object CodecEquivalenceLaws:

  def apply[A](implicit
      circeDecode: Decoder[A] @@ tags.Circe,
      magnoliaDecode: Decoder[A] @@ tags.Magnolia,
      circeEncode: Encoder[A] @@ tags.Circe,
      magnoliaEncode: Encoder[A] @@ tags.Magnolia
  ) = new CodecEquivalenceLaws[A]:

    override val circeDecoder = circeDecode
    override val magnoliaDecoder = magnoliaDecode
    override val circeEncoder = circeEncode
    override val magnoliaEncoder = magnoliaEncode

trait CodecEquivalenceTests[A] extends Laws:
  def laws: CodecEquivalenceLaws[A]

  def codecEquivalence(implicit
      arbitraryA: Arbitrary[A],
      shrinkA: Shrink[A],
      eqA: Eq[A]
  ): RuleSet = new DefaultRuleSet(
    name = "codec equality",
    parent = None,
    "encoder equivalence" -> Prop.forAll { (a: A) =>
      laws.encoderEq(a)
    },
    "decoder equivalence" -> Prop.forAll { (a: A) =>
      laws.decoderEq(a)
    }
  )

  // Use codecEquivalence if possible. Use only when only
  // derived Encoder can be equivalent and should be documented
  def encoderEquivalence(implicit
      arbitraryA: Arbitrary[A],
      shrinkA: Shrink[A]
  ): RuleSet = new DefaultRuleSet(
    name = "codec equality",
    parent = None,
    "encoder equivalence" -> Prop.forAll { (a: A) =>
      laws.encoderEq(a)
    }
  )

object CodecEquivalenceTests:
  def apply[A](implicit
      circeDecode: Decoder[A] @@ tags.Circe,
      magnoliaDecode: Decoder[A] @@ tags.Magnolia,
      circeEncode: Encoder[A] @@ tags.Circe,
      magnoliaEncode: Encoder[A] @@ tags.Magnolia
  ): CodecEquivalenceTests[A] = new CodecEquivalenceTests[A]:
    val laws: CodecEquivalenceLaws[A] = CodecEquivalenceLaws[A](
      circeDecode,
      magnoliaDecode,
      circeEncode,
      magnoliaEncode
    )

  def useTagged[A](implicit
      circeDecode: TaggedDecoder[tags.Circe, A],
      magnoliaDecode: TaggedDecoder[tags.Magnolia, A],
      circeEncode: TaggedEncoder[tags.Circe, A],
      magnoliaEncode: TaggedEncoder[tags.Magnolia, A]
  ): CodecEquivalenceTests[A] = new CodecEquivalenceTests[A]:
    val laws: CodecEquivalenceLaws[A] = CodecEquivalenceLaws[A](
      circeDecode.toTagged,
      magnoliaDecode.toTagged,
      circeEncode.toTagged,
      magnoliaEncode.toTagged
    )
