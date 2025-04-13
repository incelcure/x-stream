package modules

import tofu.logging.Logging
import services.XStreamService

final case class AppEnv[F[_]](
                               xStreamService: XStreamService[F],
                               logging: Logging[F]
                             )
