import io.circe.Encoder
import io.circe.magnolia.derivation.encoder.semiauto.deriveMagnoliaEncoder
import io.circe.magnolia.configured.encoder.semiauto.deriveConfiguredMagnoliaEncoder
import io.circe.magnolia.configured.Configuration

case class Foo(bar: Int, baz: String)

object Foo:
  given Configuration = Configuration(_.drop(2), identity, true, None)
  given encoder: Encoder[Foo] = deriveConfiguredMagnoliaEncoder[Foo]

@main def runtest = println(Encoder[Foo].apply(Foo(3, "a")))
