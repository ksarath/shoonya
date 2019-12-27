package org.shoonya.datatype

/**
  * config package represents the required application configuration
  */
package object config {

  /**
    * http server configuration
    * @param host host name for the http application
    * @param port port number for the http application
    */
  case class ServerConfig(host: String, port: Int)

  /**
    * Database server configuration
    * @param driver database driver
    * @param url database to connect
    * @param user username for database
    * @param password password for the database
    */
  case class DatabaseConfig(driver: String, url: String, user: String, password: String)
}
