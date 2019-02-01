package io.circe.magnolia.configured

import io.circe.Encoder
import io.circe.magnolia.MagnoliaEncoder
import io.circe.magnolia.configured.HardcodedDerivationSpec.{User, UserType}
import io.circe.parser.parse
import io.circe.tests.CirceSuite
import magnolia.{CaseClass, Magnolia, SealedTrait}

// An example of hardcoding a configuration. This means at when deriving Encoder/Decoder you no longer need to have
// a Configuratio in scope
class HardcodedDerivationSpec extends CirceSuite {
  "Hardcoded Encoder deriver" should "match the hardcoded configured behavior" in {
    assert(UserType.encoder(User("John", "Doe")) == parse(
      """
        {
          "type": "user",
          "first_name": "John",
          "last_name": "Doe"
        }
      """).right.get)
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

    def combine[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
      MagnoliaEncoder.combine(caseClass)(config)

    def dispatch[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
      MagnoliaEncoder.dispatch(sealedTrait)(config)

    def deriveEncoder[T]: Typeclass[T] = macro Magnolia.gen[T]
  }

}
