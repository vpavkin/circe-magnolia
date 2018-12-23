package io.circe.magnolia

import org.scalacheck.{Arbitrary, Gen}

package object configured {
  implicit val genConfiguration: Gen[Configuration] = {
    val genFunc = implicitly[Arbitrary[String => String]].arbitrary
    for {
      transformMemberNames <- genFunc
      transformConstructorNames <- genFunc
      useDefaults <- implicitly[Arbitrary[Boolean]].arbitrary
      discriminator <- implicitly[Arbitrary[Option[String]]].arbitrary
    } yield Configuration(
      transformMemberNames = transformMemberNames,
      transformConstructorNames = transformConstructorNames,
      useDefaults = useDefaults,
      discriminator = discriminator
    )
  }
}
