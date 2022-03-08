name := "ErgoScript-Developers-Course"

version := "0.0.1"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "org.ergoplatform" %% "ergo-appkit" % "develop-d90135c5-SNAPSHOT",
  "org.slf4j" % "slf4j-jdk14" % "1.7.36",
)

resolvers ++= Seq(
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "SonaType" at "https://oss.sonatype.org/content/groups/public",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

assemblyJarName in assembly := s"ErgoScript-Developers-Course${version.value}.jar"
mainClass in assembly := Some("app.DevCourse-Main")
assemblyOutputPath in assembly := file(s"./ergoscript-dev-course${version.value}.jar/")