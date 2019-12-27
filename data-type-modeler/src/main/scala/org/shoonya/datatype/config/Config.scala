package org.shoonya.datatype.config

import cats.{Monad, MonadError}
import pureconfig._
import pureconfig.error.ConfigReaderException
import pureconfig.generic.semiauto.deriveConvert

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

  /**
    * implicit converter for loading root Config using pureconfig
    */
  implicit val convert: ConfigConvert[Config] = deriveConvert

  /**
    * Loads the application configuration
    * @param file application configuration file path, by default application.conf
    * @return application configuration
    */
  def load(file: String = "application.conf"): ConfigReader.Result[Config] =
    ConfigSource.file(file).load[Config]
}