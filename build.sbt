name := "spark-csv-file-comparator"
organization := "com.mohek.bigutils"
version in ThisBuild := "1.0-SNAPSHOT"

scalaVersion := "2.10.5"
coverageEnabled.in(ThisBuild ,Test, test) := true



// spark dependency
libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.0" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.6.0" % "provided"
libraryDependencies += "com.databricks" %% "spark-avro" % "2.0.1"

// slf4j
libraryDependencies += "org.clapper" %% "grizzled-slf4j" % "1.0.2"

// scopt for commandline options
libraryDependencies += "com.github.scopt" %% "scopt" % "3.3.0"

// test dependencies
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
libraryDependencies += "junit" % "junit" % "4.12" % "test"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3" % "test"

// XStream to JSON part of .tools
libraryDependencies += "com.thoughtworks.xstream" % "xstream" % "1.4.8"
libraryDependencies += "org.codehaus.jettison" % "jettison" % "1.3.7"




libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-core" % "2.1.1",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.1.1",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.1.1",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.1.1",
  "org.yaml" % "snakeyaml" % "1.10",
  "com.databricks" % "spark-csv_2.10" % "1.2.0",
  "org.scalaj" %% "scalaj-collection" % "1.5"
)

// Internal
resolvers += Resolver.mavenLocal
resolvers += "Local maven Repo" at Path.userHome.asFile.toURI.toURL+".m2/repository"
resolvers += "Local ivy2 Repo" at Path.userHome.asFile.toURI.toURL+".ivy2"
resolvers += "Repoo" at "https://repository.apache.org/content/repositories/releases/"
resolvers += "Typesafe repo" at "https://mvnrepository.com/artifact"

libraryDependencies ~= { _ map {
  case m if m.organization == "com.typesafe.play" =>
    m.exclude("commons-logging", "commons-logging").
      exclude("com.typesafe.play", "sbt-link")
  case m => m
}}


unmanagedJars in Compile := (baseDirectory.value ** "*.jar").classpath
unmanagedResourceDirectories in Compile +={ baseDirectory.value / "src/main/resources" }
