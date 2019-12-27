package org.shoonya.datatype.service

import cats.effect.Effect
import shoonya.model.TitlePrincipal
import shoonya.repository.Repository

object TitlePrincipalsService {
  /**
    * TitlePrincipals service
    * @param repository TitlePrincipals repository
    * @tparam F Effect type
    * @return TitlePrincipals service
    */
  def apply[F[_] : Effect](repository: Repository[F, TitlePrincipal]): Service[F, TitlePrincipal] =
    new Service[F, TitlePrincipal](repository)
}
