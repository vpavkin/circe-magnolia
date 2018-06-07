# circe-magnolia
### Experimental [circe](https://circe.io) codec derivation using [magnolia](http://magnolia.work/).

[![Build Status](https://img.shields.io/travis/vpavkin/circe-magnolia/master.svg)](https://travis-ci.org/vpavkin/magnolia) 
[![Coverage status](https://img.shields.io/codecov/c/github/vpavkin/circe-magnolia/master.svg)](https://codecov.io/github/vpavkin/magnolia?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/ru.pavkin/circe-magnolia-derivation_2.12.svg)](https://github.com/vpavkin/circe-magnolia)

This library provides facilities to derive JSON codec instances for Circe using Magnolia macros.

### ⚠️ Experimental status warning.
This project is an experiment to investigate the feasibility of taken approach.

Currently `circe-magnolia` should not be used in production for two major reasons:
  - there are some obvious issues with how magnolia picks up default circe codecs. 
  	Many of them are in the process of being fixed: [here](https://github.com/propensive/magnolia/issues/87), [here](https://github.com/propensive/magnolia/issues/88) and [here](https://github.com/propensive/magnolia/issues/89)
  - behaviour semantics (emitted json and decoding logic) differ in some cases from `circe-generic`.
  
See [Testing](#testing) and [Status](#status) for more details.
  
### Getting started

To play around with circe-magnolia, add it to your build:

```scala
libraryDependencies += "ru.pavkin" %%% "circe-magnolia-derivation" % "0.0.1"
```

After that, as in `circe-generic`, you can use one of two dervation modes.

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

### Testing

To ensure `circe-magnolia` derivation and codecs work in the same way as in `circe-generic`, several test suites from original circe repository were adapted and added to this project. These tests validate the derivation semantics and also the lawfulness of derived codecs ([example](https://github.com/vpavkin/circe-magnolia/blob/master/tests/src/test/scala/io/circe/magnolia/AutoDerivedSuite.scala)). 

There's another set of tests, that validate the equivalence of JSON and decoding logic, produced by `circe-magnolia` and `circe-generic` ([example](https://github.com/vpavkin/circe-magnolia/blob/master/tests/src/test/scala/io/circe/magnolia/AutoDerivedEquivalenceSuite.scala)).


### Status

Below are problems, that are currently faced by the project to be a proper replacement for `circe-generic`.

#### Semiauto

Current test suite shows, that most of the time, codecs derived using `circe-magnolia` semiauto are equivalent to the ones provided by corresponding mode in `circe-generic`.

There's difference, though, in what can and can not be derived with either one.

`circe-generic`'s semiauto is strict in the sense, that auto derivation is used only to derive codec for specified type – it fails if there's no already existing implicit codec for at least one of underlying types.

Magnolia's macro seems to be falling back on itself during the derivation process even if there's no implicit around, signalling to do that. The difference can easily be observed by following example.

Say we have this ADT (test classes are taken from circe testing module):

```scala
sealed trait Foo
case class Bar(i: Int, s: String) extends Foo
case class Baz(xs: List[String])  extends Foo
```

Trying to directly derive codec for `Foo` using `circe-generic` will fail:

```scala
import io.circe.generic.semiauto._

deriveEncoder[Foo] // could not find Lazy implicit value of ...
deriveDecoder[Foo] // could not find Lazy implicit value of ...
```

But magnolia semiauto derives the codecs just fine:

```scala
import io.circe.magnolia.derivation.decoder.semiauto._
import io.circe.magnolia.derivation.encoder.semiauto._

val encoder = deriveMagnoliaEncoder[Foo]
val decoder = deriveMagnoliaDecoder[Foo]

/*
Outputs {"Baz": {"xs": ["a", "b", "c"] }}
*/
encoder(Baz(List("a", "b", "c")))

```

So when there's no codec around for some underlying type, magnolia derives it's own. Even though there's no implicit that should suggest that (see [semiauto implementation](https://github.com/vpavkin/circe-magnolia/blob/master/derivation/src/main/scala/io/circe/magnolia/derivation/encoder/semiauto.scala))

To wrap up: current version of `circe-magnolia` semiauto works more like `circe-generic`'s auto mode, except that it doesn't provide top-level implicits out of the box - you have to call `deriveMagnolia[Encoder|Decoder]` to start derivation process.

#### Auto

Auto mode needs more effort to make it work properly.
Several issues with Magnolia implicit prioritization are in progress already.

You can track the progress by the [failing tests on Travis](https://travis-ci.org/vpavkin/circe-magnolia). They show where `circe-magnolia` and `circe-generic` part ways.

Most of the problems are caused by two issues: 

1) If magnolia macro summoner is implicit, it overrides some already available codecs (e.g. ones in typeclass or target type companion objects). [Issue link](https://github.com/propensive/magnolia/issues/89)

2) When deriving an instance for a recusive type, magnolia is not currently able to "tie recursive knot" (recursively use the very same instance, that is being derived to derive other instances). This can be reproduced by a simple example [here](https://github.com/propensive/magnolia/issues/87).

### Further work

1) Fix all the inconsistencies between `circe-magnolia` and `circe-generic`
2) Add derivation of partial/patch codecs.
3) Configurable derivation should be very simple to do with magnolia. Potentially this can provide huge flexibility to the `circe` users.
