package io.circe.magnolia.configured

import io.circe.Encoder
import io.circe.magnolia.MagnoliaEncoder
import io.circe.magnolia.configured.HardcodedDerivationSpec.{User, UserType}
import io.circe.parser.parse
import io.circe.tests.CirceSuite
import magnolia1.{CaseClass, Magnolia, SealedTrait}

// An example of hardcoding a configuration. This means at when deriving Encoder/Decoder you no longer need to provide
// a Configuration object
class HardcodedDerivationSpec extends CirceSuite {
  "Hardcoded Encoder deriver" should "match the hardcoded configured behavior" in {
    assert(UserType.encoder(User("John", "Doe")).asRight[Throwable] == parse(
      """
        {
          "type": "user",
          "first_name": "John",
          "last_name": "Doe"
        }
      """))
  }
}

object HardcodedDerivationSpec {

  sealed trait UserType
  final case class User(firstName: String, lastName: String) extends UserType

  final case class SuperUser(name: String) extends UserType

  object UserType {
    implicit val encoder: Encoder[UserType] = hardcodedConfiguration.deriveEncoder[UserType]
  }

  object hardcodedConfiguration {
    val config: Configuration =
      Configuration
        .default
        .withDiscriminator("type")
        .withSnakeCaseConstructorNames
        .withSnakeCaseMemberNames

    type Typeclass[T] = Encoder[T]

    def join[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
      MagnoliaEncoder.join(caseClass)(config)

    def split[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
      MagnoliaEncoder.split(sealedTrait)(config)

    def deriveEncoder[T]: Typeclass[T] = macro Magnolia.gen[T]
  }

}
