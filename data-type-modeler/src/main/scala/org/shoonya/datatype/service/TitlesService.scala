package org.shoonya.datatype.service

import cats.effect.Effect
import shoonya.model.Title
import shoonya.repository.Repository

object TitlesService {
  /**
    * Titles service
    * @param repository titles repository
    * @tparam F Effect type
    * @return Titles service
    */
  def apply[F[_] : Effect](repository: Repository[F, Title]): Service[F, Title] =
    new Service[F, Title](repository)
}
