package clients

import cats.effect.{Async, Resource}
import fs2.Stream
import fs2.text
import io.circe.parser
import sttp.capabilities.fs2.Fs2Streams
import sttp.client3._
import sttp.model.Uri
import domain.XPost
import sttp.capabilities._

trait XClient[F[_]] {
  def streamTweets: Stream[F, XPost]
}

final class XClientImpl[F[_]: Async](
  bearer: String,
  backendR: Resource[F, SttpBackend[F, Fs2Streams[F]]]
) extends XClient[F] {

  private val baseUri: String = sys.env.getOrElse("X_BASE_URI", "localhost:1080")

  private val endpoint: Uri =
    uri"http://$baseUri/2/tweets/search/stream?tweet.fields=text&expansions=author_id"

  private val request: Request[Either[String, Stream[F, Byte]], Fs2Streams[F]] = // Важно!
    basicRequest
      .get(endpoint)
      .header("Authorization", s"Bearer $bearer")
      .response(asStreamUnsafe(Fs2Streams[F]))

  override def streamTweets: Stream[F, XPost] =
    Stream.resource(backendR).flatMap { backend =>
      Stream.eval(request.send(backend)).flatMap { response =>
        response.body match {
          case Left(error) =>
            Stream.raiseError[F](new Exception(s"Stream error: $error"))

          case Right(bytesStream) =>
            bytesStream
              .through(text.utf8.decode)
              .through(text.lines)
              .evalMap { line =>
                Async[F].fromEither(
                  parser
                    .parse(line)
                    .flatMap(_.hcursor.downField("data").get[String]("text")) // Извлекаем текст
                    .map(XPost)
                )
              }
              .handleErrorWith { e =>
                Stream.eval(Async[F].delay(println(s"Error processing tweet: ${e.getMessage}"))) >>
                Stream.empty
              }
        }
      }
    }
}
