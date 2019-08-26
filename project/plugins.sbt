resolvers +=  Resolver.bintrayRepo("jetbrains", "sbt-plugins")

addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.5")
addSbtPlugin("com.dwijnand"      % "sbt-dynver"      % "4.0.0")
addSbtPlugin("com.dwijnand"      % "sbt-travisci"    % "1.1.3")
addSbtPlugin("com.geirsson"      % "sbt-scalafmt"    % "1.5.1")
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "5.2.0")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.11")
addSbtPlugin("org.wartremover"   % "sbt-wartremover" % "2.4.2")

addSbtPlugin("org.jetbrains" % "sbt-idea-plugin" % "2.4.0")
