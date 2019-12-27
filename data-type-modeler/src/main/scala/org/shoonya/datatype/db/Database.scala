package org.shoonya.datatype.db

import cats.effect._
import doobie.Transactor
import org.flywaydb.core.Flyway

import shoonya.config.DatabaseConfig

/**
  * Utility methods for database connection and queries
  */
object Database {
  /**
    * Builds a Transactor for database transactions
    * @param config database configuration
    * @return
    */
  def transactor[F[_]: Async : ContextShift](config: DatabaseConfig): Transactor.Aux[F, Unit] = Transactor.fromDriverManager[F](
    config.driver, config.url, config.user, config.password
  )

  /**
    * Initialize the database
    * @param transactor transactor
    * @param config database configuration
    * @tparam F Effect type
    * @return Nothing inside the Effect
    */
  def initialize[F[_] : Sync](transactor: Transactor[F], config: DatabaseConfig): F[Unit] =
    transactor.configure { _ =>
      Sync[F].delay {
        Flyway
          .configure()
          .dataSource(config.url, config.user, config.password)
          .load()
          .migrate()
      }
    }
}