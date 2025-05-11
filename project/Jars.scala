import sbt.*

object Jars {
  object cats {
    val effectVersion = "3.5.2"

    val effect  = "org.typelevel" %% "cats-effect" % effectVersion
  }

  object sttp {
    object client3 {
      val version = "3.9.1"

      val core  = "com.softwaremill.sttp.client3"      %% "core"                           % version
      val async = "com.softwaremill.sttp.client3"      %% "async-http-client-backend-cats" % version
      val asyncFs2 =   "com.softwaremill.sttp.client3" %% "async-http-client-backend-fs2"  % version
      val circe = "com.softwaremill.sttp.client3"      %% "circe"                          % version
    }
  }

  object circe {
    val version = "0.14.7"

    val core    = "io.circe" %% "circe-core"    % version
    val generic = "io.circe" %% "circe-generic" % version
    val parser  = "io.circe" %% "circe-parser"  % version
  }

  object fs2 {
    val version = "3.2.0"

    val kafka    = "com.github.fd4s" %% "fs2-kafka" % version
  }

  object tofu {
    val version = "0.13.6"

    val core       = "tf.tofu" %% "tofu-core-ce3"           % version
    val logging    = "tf.tofu" %% "tofu-logging-layout"     % version
    val derivation = "tf.tofu" %% "tofu-logging-derivation" % version
    val structured = "tf.tofu" %% "tofu-logging-structured" % version
    val kernel     = "tf.tofu" %% "tofu-kernel"             % version
  }

  object prometheus {
    val version = "0.16.0"

    val common  = "io.prometheus" % "simpleclient_common"  % version
    val hotspot = "io.prometheus" % "simpleclient_hotspot" % version
  }

  object testkit {
    object izumi {
      val version = "1.2.16"

      val docker    = "io.7mind.izumi" %% "distage-framework-docker"  % version
      val scalatest = "io.7mind.izumi" %% "distage-testkit-scalatest" % version
    }

    val scalatest = "org.scalatest" %% "scalatest" % "3.2.19"
  }
}
