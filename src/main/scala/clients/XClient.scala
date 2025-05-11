package clients

import cats.effect.{Async, Resource}
import cats.implicits.toFunctorOps
import fs2.Stream
import io.circe.{parser, Json}
import sttp.client3._
import sttp.client3.circe._
import sttp.model.Uri
import domain.XPost
import sttp.capabilities.fs2.Fs2Streams

trait XClient[F[_]] {
  def streamTweets: Stream[F, XPost]
}

final class XClientImpl[F[_]: Async](
  bearer: String,
  backendR: Resource[F, SttpBackend[F, Fs2Streams[F]]]
) extends XClient[F] {

  private val endpoint: Uri =
    uri"https://api.twitter.com/2/tweets/search/stream?tweet.fields=text&expansions=author_id"

  private val request =
    basicRequest
      .get(endpoint)
      .header("Authorization", s"Bearer $bearer")
      .response(
        asStreamUnsafe(Fs2Streams[F])
          .map(bytes => parser.parse(bytes.toString).toOption)
      )

  override def streamTweets: Stream[F, XPost] =
    for {
      backend  <- Stream.resource(backendR)
      response <- Stream.eval(request.send(backend))
      json <- response.body match {
        case Some(j) => Stream.emit(j)
        case None    => Stream.empty
      }
      post <- json.hcursor.downField("data").get[String]("text") match {
        case Right(text) => Stream.emit(XPost(text))
        case Left(_)     => Stream.empty
      }
    } yield post

}
