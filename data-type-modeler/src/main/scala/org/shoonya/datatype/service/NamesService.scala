package org.shoonya.datatype.service

import cats.effect.Effect
import shoonya.model.Name
import shoonya.repository.Repository

object NamesService {
  /**
    * Names service
    * @param repository names repository
    * @tparam F Effect type
    * @return Names service
    */
  def apply[F[_] : Effect](repository: Repository[F, Name]): Service[F, Name] =
    new Service[F, Name](repository)
}