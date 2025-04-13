package services

import cats.effect._
import tofu.logging.Logging
import cats.implicits._

class XStreamServiceImpl[F[_]: Async](log: Logging[F]) extends XStreamService[F] {
  override def run: F[Unit] =
    for {
      _ <- log.info("Сервис x-stream запущен")
      _ <- Async[F].delay(println("тут будет стриминг постов..."))
    } yield ()
}
