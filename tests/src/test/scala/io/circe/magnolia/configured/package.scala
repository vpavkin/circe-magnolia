package io.circe.magnolia

import io.circe.generic.extras.Configuration as GeConfiguration

package object configured:
  def toGenericExtrasConfig(c: Configuration): GeConfiguration =
    GeConfiguration(
      transformMemberNames = c.transformMemberNames,
      transformConstructorNames = c.transformConstructorNames,
      useDefaults = c.useDefaults,
      discriminator = c.discriminator
    )
