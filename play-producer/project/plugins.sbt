// to enable the playframework
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.12")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "1.5.1")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.6.1")

resolvers += Resolver.url(
  "bintray-dnvriend-ivy-sbt-plugins",
  url("http://dl.bintray.com/dnvriend/sbt-plugins"))(
  Resolver.ivyStylePatterns)

addSbtPlugin("com.github.dnvriend" % "sbt-scaffold-play" % "0.0.3")