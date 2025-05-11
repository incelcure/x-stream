package modules

import cats.effect.Async
import domain.XPost
import fs2.kafka._
import cats.implicits._

trait KafkaProducer[F[_]] { def send(post: XPost): F[Unit] }

final class KafkaProducerImpl[F[_]: Async](
  cfg: ProducerSettings[F, String, String]
) extends KafkaProducer[F] {

  private val producerR = KafkaProducer.resource(cfg)

  override def send(post: XPost): F[Unit] = {
    producerR.use { producer =>
      for {
        fMeta <- producer.produceOne(ProducerRecord("x-stream-data", null, post.text))
        _     <- fMeta.void
      } yield ()
    }
  }
}
