package org.shoonya.datatype.service

import cats.effect._
import cats.implicits._

import fs2.Stream

import io.circe._
import io.circe.syntax._

import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

import shoonya.repository._

/**
  * A generic service representing generic http routes such as get by id, get all
  * @param repository repository to connect for the queries / interactions
  * @tparam F Effect type
  * @tparam A Service type
  */
class Service[F[_] : Effect, A : Decoder : Encoder](repository: Repository[F, A]) extends Http4sDsl[F] {
  // http routes representing get by id and get all
  val routes = HttpRoutes.of[F]  {
    case _ @ GET -> Root / id  => repository.read(id).flatMap(okOr404)
    case _ @ GET -> Root => Ok(
      Stream("[")
        ++ repository.stream.map(_.asJson.noSpaces).intersperse(",")
        ++ Stream("]")
    )
  }

  // TODO: to be refactored and moved to a util package
  /**
    * Utility function for returning Ok or NotFound http response
    * @param result result or exception to be returned as an http response
    * @return Ok in case of success else NotFound
    */
  private def okOr404(result: Either[_, A]): F[Response[F]] =
    result match {
      case Left(_)    => NotFound()
      case Right(a)   => Ok(a.asJson)
    }
}
