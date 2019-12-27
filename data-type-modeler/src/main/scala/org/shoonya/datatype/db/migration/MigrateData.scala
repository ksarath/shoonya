package org.shoonya.datatype.db.migration

import cats.effect._
import cats.implicits._
import doobie.implicits._
import fs2._
import java.nio.file.Paths
import java.util.concurrent.Executors

import doobie.util.transactor.Transactor
import shoonya.config.Config
import shoonya.db.Database

import scala.concurrent.ExecutionContext

object MigrateData extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = ???
}
