package org.shoonya.datatype.repository

import cats.syntax.functor._
import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor._
import shoonya.model.{ApplicationEntity, Identity}

class ApplicationEntityRepository[F[_]: Sync](transactor: Transactor[F])
  extends Repository[F, ApplicationEntity] with StreamingRepository[F, ApplicationEntity] with ReadRepository[F, ApplicationEntity] {
  // Repository name
  override val name: RepositoryName = "Application-Entity-Repository"

  override def stream: fs2.Stream[F, ApplicationEntity] =
    sql"""
        SELECT
          id,
          application_id,
          name,
          entity
        FROM
          application_entity
      """
      .query[ApplicationEntity]
      .stream
      .transact(transactor)

  override def read(id: Identity): F[Result[ApplicationEntity]] =
    sql"""
        SELECT
          id,
          application_id,
          name,
          entity
        FROM
          application_entity
        WHERE
          id = ${id.toString}
        """
      .query[ApplicationEntity]
      .option                   //.to[List]
      .transact(transactor)
      .map {
        case Some(entity) => Right(entity)
        case None => Left(NotFound(name, "Application entity not found", Option(id)))
      }
}

object ApplicationEntityRepository {
  def apply[F[_]: Sync](transactor: Transactor[F]): ApplicationEntityRepository[F] =
    new ApplicationEntityRepository(transactor)
}
