name := "PlatformSynAd"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.mongodb" % "mongo-java-driver" % "2.10.1",
  "net.vz.mongodb.jackson" %% "play-mongo-jackson-mapper" % "1.1.0",
  "com.typesafe" %% "play-plugins-mailer" % "2.1-RC2",
  "net.sf.barcode4j" % "barcode4j" % "2.1",
  "commons-io" % "commons-io" % "2.3",
  "org.powermock" % "powermock-core" % "1.5.5",
  "org.powermock" % "powermock-module-junit4" % "1.5.5",
  "org.powermock" % "powermock-api-mockito" % "1.5.5",
  "com.rosaloves" % "bitlyj" % "2.0.0",
  "com.amazonaws" % "aws-java-sdk" % "1.8.7"
) 

play.Project.playJavaSettings
