package org.shoonya.datatype.http

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import fs2.Stream
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._
import org.shoonya.datatype.config.Config
import zio.{App, ZEnvDefinition, ZIO}

import scala.concurrent.duration._

/**
  * The main http server application
  */
object Server extends App with ZEnvDefinition {

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    ZIO.effectTotal(for {
      config <- ZIO.fromEither(Config.load())
    } yield 0))
  }

//  override def run(args: List[String]): IO[ExitCode] =
//    (for {
//      config                <- Stream.eval(Config.load())           // Loads application configuration
//      transactor            = Database.transactor(config.database)  // Creates transactor for db transactions
//      _                     <- Database.initialize(transactor, config.database).map(_ => println("db initialized"))
//
//
//      // Create repository for names table interactions and routes for Names service
//      namesRepository       = NamesRepository(transactor)
//      namesRoutes           = NamesService(namesRepository).routes
//
//
//      // Create repository for title_principals table interactions and routes for TitlePrincipals service
//      titlePrincipalsRepo   = TitlePrincipalsRepository(transactor)
//      titlePrincipalsRoutes = TitlePrincipalsService(titlePrincipalsRepo).routes
//
//
//      // Create repository for titles table interactions and routes for Titles service
//      titlesRepository      = TitlesRepository(transactor)
//      titlesRoutes          = TitlesService(titlesRepository).routes
//
//
//      // Create repository for actor with titles queries and routes for ActorWithTitles service
//      actorWithTitlesRepo   = ActorWithTitlesRepository(transactor)
//      actorWithTitlesRoutes = ActorWithTitlesService(actorWithTitlesRepo).routes
//
//
//      // Create routes for KevinBacon service
//      kevinBaconRoutes      = KevinBaconService(actorWithTitlesRepo, namesRepository).routes
//
//
//      // Create http application with all the requires routes
//      httpApp               = Router(
//                                "/names"              -> namesRoutes,
//                                "/title-principals"   -> titlePrincipalsRoutes,
//                                "/titles"             -> titlesRoutes,
//                                "/actor/titles"       -> actorWithTitlesRoutes,
//                                "/kevin-bacon"        -> kevinBaconRoutes
//                              ).orNotFound
//
//
//
//      // Run the http server / application
//      exitCode              <- BlazeServerBuilder[IO].
//                                withIdleTimeout(10.minutes).
//                                bindHttp(config.server.port, config.server.host).
//                                withHttpApp(httpApp).
//                                serve
//    } yield exitCode).compile.drain.as(ExitCode.Success)
}
