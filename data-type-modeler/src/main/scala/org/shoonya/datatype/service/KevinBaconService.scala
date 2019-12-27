package org.shoonya.datatype.service

import cats.effect._
import cats.implicits._
import shoonya.model._
import shoonya.util.bfs
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import shoonya.repository._

/**
  * Kevin Bacon service, route for returning the Kevin Bacon number
  * @param repository ActorWithTitles repository
  * @param namesRepo Names repository
  * @tparam F Effect type
  */
class KevinBaconService[F[_] : Effect](repository: ActorWithTitlesRepository[F], namesRepo: NamesRepository[F]) extends Http4sDsl[F] {
  // actor query parameter decoder from url
  object ActorNameDecoderMater extends QueryParamDecoderMatcher[String]("actor")

  // http routes for kevin bacon service
  val routes = HttpRoutes.of[F] {
    case _@GET -> Root :? ActorNameDecoderMater(actorName) =>
      namesRepo.fromName(actorName).flatMap {
        case Right(names) =>
          (for {
            r <- names.filter(_.primaryProfession.exists(_.contains("actor"))).map(
                    name =>
                      bfs(name, getCoActors).flatMap(
                        _.filter(n => n._1.primaryName == "Kevin Bacon" && n._1.birthYear == Option(1958)).take(1).
                          compile.toList.map(r => r.headOption.map(_._2))
                      )
                  )
          } yield r).toList.sequence.flatMap(r => Ok(r.asJson))

        case Left(_) => NotFound()
      }
  }

  /**
    * Utility function to get the co-actors of a given actor / actress from the repository
    * @param name given actor
    * @return co-actors of the given actor / actress
    */
  private def getCoActors(name: Name): F[Seq[Name]] = {
    repository.getCoActors(name.nconst).map {
      case Right(value) => value.map(_.name)
      case Left(_) => Seq()
    }
  }

}

object KevinBaconService {
  def apply[F[_]: Effect](repository: ActorWithTitlesRepository[F], namesRepo: NamesRepository[F]): KevinBaconService[F] =
    new KevinBaconService(repository, namesRepo)
}
