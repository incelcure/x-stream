package app

import cats.effect._
import tofu.logging._
import modules._
import services._
import clients.XClientImpl
import fs2.kafka.ProducerSettings
import sttp.capabilities.fs2.Fs2Streams
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.fs2.AsyncHttpClientFs2Backend

object XStreamApp extends IOApp {

  implicit val loggable: Loggable[Unit] = Loggable.empty

  override def run(args: List[String]): IO[ExitCode] = {
    val xClientR = AsyncHttpClientFs2Backend.resource[IO]().map { backend =>
      new XClientImpl[IO](
        sys.env("X_BEARER"),
        Resource.pure[IO, SttpBackend[IO, Fs2Streams[IO]]](backend)
      )
    }

    val program: Resource[IO, AppEnv[IO]] =
      for {
        logger  <- Resource.eval(Logs.sync[IO, IO].byName("x-stream"))
        xClient <- xClientR
        producer <- Resource.pure(
          new KafkaProducerImpl[IO](
            ProducerSettings[IO, String, String]
              .withBootstrapServers("localhost:29092")
          )
        )
        service <- Resource.pure(new XStreamServiceImpl[IO](logger, xClient, producer))
      } yield AppEnv(service, logger)

    program.use(_.xStreamService.run).as(ExitCode.Success)
  }

}
