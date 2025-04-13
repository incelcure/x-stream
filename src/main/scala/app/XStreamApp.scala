package app


import cats.effect._
import tofu.logging._
import modules._
import services._

object XStreamApp extends IOApp {

  implicit val loggable: Loggable[Unit] = Loggable.empty

  override def run(args: List[String]): IO[ExitCode] = {
    val app: Resource[IO, AppEnv[IO]] = for {
      logger <- Resource.eval(
        Logs.sync[IO, IO].byName("x-stream")
      )
      service = new XStreamServiceImpl[IO](logger)
    } yield AppEnv(service, logger)

    app.use(_.xStreamService.run).as(ExitCode.Success)
  }
}
