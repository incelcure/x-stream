package services

import cats.effect._
import tofu.logging.Logging
import cats.implicits._
import clients.XClient
import modules.KafkaProducer

class XStreamServiceImpl[F[_]: Async](
                                       log: Logging[F],
                                       x:   XClient[F],
                                       k:   KafkaProducer[F]
                                     ) extends XStreamService[F] {

  override def run: F[Unit] =
    x.streamTweets
      .evalTap(p => log.info(s"Tweet → ${p.text.take(60)}…"))
      .evalMap(k.send)
      .compile.drain
}

