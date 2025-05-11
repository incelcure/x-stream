import sbt.*

object Deps {
  import Jars.*

  object Core {
    protected def coreDependencies: Vector[ModuleID] = {
      Vector(
        cats.effect,
        sttp.client3.core,
        sttp.client3.async,
        sttp.client3.asyncFs2,
        sttp.client3.circe,
        circe.core,
        circe.generic,
        circe.parser,
        fs2.kafka,
        tofu.core,
        tofu.logging,
        tofu.derivation,
        tofu.structured,
        tofu.kernel,
        prometheus.common,
        prometheus.hotspot
      )
    }

    protected def testDependencies: Vector[ModuleID] = {
      Vector(
        testkit.scalatest,
        testkit.izumi.docker,
        testkit.izumi.scalatest
      )
    }

    def dependencies: Vector[ModuleID] = coreDependencies ++ testDependencies.map(_ % Test)
  }
}
