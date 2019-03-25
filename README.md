# circe-magnolia
### Codec derivation for [Circe](circe.io) using [Magnolia](http://magnolia.work/).

[![Build Status](https://img.shields.io/travis/circe/circe-magnolia/master.svg)](https://travis-ci.org/circe/circe-magnolia)
[![Coverage status](https://img.shields.io/codecov/c/github/circe/circe-magnolia/master.svg)](https://codecov.io/github/circe/circe-magnolia?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/io.circe/circe-magnolia-derivation_2.12.svg)](https://search.maven.org/artifact/io.circe/circe-magnolia-derivation_2.12)

This library provides facilities to derive JSON codec instances for Circe using Magnolia macros.

### ⚠️ Early development status warning
Although this project is extensively tested and seems to work fine, it's still at early development stages. 
It's not advised to use this in production without proper test coverage of related code.

There are still some things, that are different from circe-generic, including a critical issue with auto derivation.

See [Testing](#testing) and [Status](#status) for more details.

### Getting started

To play around with circe-magnolia, add it to your build:

```scala
libraryDependencies += "io.circe" %%% "circe-magnolia-derivation" % "0.4.0"
```

After that, as in `circe-generic`, you can use one of two derivation modes.

Note, that at the moment for both auto and semiauto modes you have to import encoder and decoder machinery separately (see examples below).

#### Auto

Works in the same way as `io.circe.generic.auto._` from `circe-generic`.

```
import io.circe.magnolia.derivation.decoder.auto._
import io.circe.magnolia.derivation.encoder.auto._

case class Foo(i: Int, s: String)

implicitly[Encoder[Foo]]
implicitly[Decoder[Foo]]

```

#### Semiauto

Works in the same way as `io.circe.generic.semiauto._` from `circe-generic`, but the method names differ so that you can theoretically use both in the same scope:

```
import io.circe.magnolia.derivation.decoder.semiauto._
import io.circe.magnolia.derivation.encoder.semiauto._

case class Foo(i: Int, s: String)

val encoder = deriveMagnoliaEncoder[Foo]
val decoder = deriveMagnoliaDecoder[Foo]

```

#### Configurable Semiauto and Auto derivations

Configuration is possible if you want to configure your Encoder/Decoder's output. This project provides the same configuration as `circe-generic-extras`.

For example, to generate Encoder/Decoder where the JSON keys are snake-cased:
```
import io.circe.magnolia.configured.decoder.semiauto._
import io.circe.magnolia.configured.encoder.semiauto._
import io.circe.magnolia.configured.Configuration

case class User(firstName: Int, lastName: String)

implicit val configuration: Configuration = Configuration.default.withSnakeCaseMemberNames

val encoder = deriveConfiguredMagnoliaEncoder[User]
val decoder = deriveConfiguredMagnoliaDecoder[User]
```

To avoid constantly needing to provide/import an implicit `Configuration` to derive Encoder/Decoders, you can hardcode the configuration by defining your own version of deriver. 
See [HardcodedDerivationSpec](https://github.com/circe/circe-magnolia/blob/master/tests/src/test/scala/io/circe/magnolia/configured/HardcodedDerivationSpec.scala) for an example of how to do this. (In fact, the default auto and semiauto derivers are hardcoded to use the default Configuration)

### Testing

To ensure `circe-magnolia` derivation and codecs work in the same way as in `circe-generic`, several test suites from original circe repository were adapted and added to this project. These tests validate the derivation semantics and also the lawfulness of derived codecs ([example](https://github.com/circe/circe-magnolia/blob/master/tests/src/test/scala/io/circe/magnolia/AutoDerivedSuite.scala)).

There's another set of tests, that validate the equivalence of JSON and decoding logic, produced by `circe-magnolia` and `circe-generic` ([example](https://github.com/circe/circe-magnolia/blob/master/tests/src/test/scala/io/circe/magnolia/AutoDerivedEquivalenceSuite.scala)).

Test suite is currently green, but couple of cases are worked around or ignored, and waiting to be fixed. See the issue tracker for outstanding issues.

### Status

Overall, **semiauto** derivation works pretty well. All laws are satisfied and compatibility tests are passing.

There is a subtle difference from circe-generic semiauto in what can and what can not be derived. Circe-magnolia deriver has more relaxed requirements on what has to be in scope, so you might not even notice this. Basically, circe-magnolia doesn't require any pre-defined codecs for intermediate types. 

In essense: current version of `circe-magnolia` semiauto is as powerful as `circe-generic`'s auto under the hood. It just doesn't provide any top-level implicits out of the box - you have to call `deriveMagnolia[Encoder|Decoder]` to start derivation process. See more [here](https://github.com/propensive/magnolia/issues/105)

**Auto** derivation also works, but there's a twist: at the moment, for default codecs (for example, `Encoder[List]`) to be picked up, they have to be imported explicitly.
Otherwise, deriver uses Magnolia to derive codecs for any types which are either case classes or sealed traits. These derived codecs then override the default ones.

This is definitely going to be fixed in future, but if you want to switch from circe-generic's auto to circe-magnolia's auto today, you would have to add additional imports to every place where derivation takes place:

```scala
// import all default codecs, defined in circe
import io.circe.Encoder._
import io.circe.Decoder._
// also import all codecs, defined in the companion objects of your own data definitions
import Foo._, Bar._
```

Another outstanding issue with auto is that it doesn't pick up default instances for tagged types.

### Further work

1) Facilitate [magnolia development](https://github.com/propensive/magnolia/issues/107) to make auto derivation work the same way as in `circe-generic`.
2) Add derivation of partial/patch codecs.
3) Configurable derivation should be very simple to do with Magnolia. Potentially this can provide huge flexibility to the `circe` users.

### Contributors

Circe-magnolia is currently developed and maintained by [Vladimir Pavkin](https://github.com/vpavkin).

I really welcome any kind of contributions, including test/bug reports and benchmarks.

I really appreciate all the people who contributed to the project:
* [Jon Pretty](https://github.com/propensive)
* [Artsiom Miklushou](https://github.com/mikla)

I also want to say "Thank you!" to

* [Jon Pretty](https://github.com/propensive) for active collaboration and improvements in Magnolia, that make this project progress.
* [Travis Brown](https://github.com/travisbrown) for his amazing Circe project.
