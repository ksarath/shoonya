package org.shoonya.datatype.config

import cats.{Monad, MonadError}
import com.typesafe.config.ConfigFactory
import pureconfig.error.ConfigReaderException

/**
  * Application configuration
  * @param server the http server configuration
  * @param database database configuration
  */
case class Config(server: ServerConfig, database: DatabaseConfig)

/**
  * Utilities for the application configuration
  */
object Config {
  import pureconfig._
  import pureconfig.generic.auto._

  type MonadicError[F[_]] = MonadError[F, Throwable]

  /**
    * Loads the application configuration
    * @param file application configuration file path, by default application.conf
    * @return application configuratio
    */
  def load[F[_]: Monad : MonadicError](file: String = "src/main/resources/application.conf"): F[Config] =
    loadConfig[Config](ConfigFactory.load(file)) match {
      case Left(e) => implicitly[MonadicError[F]].raiseError[Config](new ConfigReaderException[Config](e))
      case Right(config) => implicitly[Monad[F]].pure(config)
    }
}