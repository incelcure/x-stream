package services

trait XStreamService[F[_]] {
  def run: F[Unit]
}
