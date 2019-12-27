package org.shoonya.datatype

import shoonya.model.Identity

package object repository {
  /**
    * type for repository name
    */
  type RepositoryName = String

  /**
    * Represents a result of read action, either a repository read error or the queried object
    * @tparam A type of the repository object
    */
  type Result[A] = Either[RepositoryException, A]

  /**
    * Represents a result of paginated read action, either a repository read error or the queried set of objects
    * @tparam A
    */
  type PaginatedResult[A] = Either[RepositoryException, Seq[A]]

  /**
    * type for query conditions such as where, groupby, orderby
    */
  trait Query

  /**
    * cursor to be used in a query
    * @param offset start index / offset for a query
    * @param limit limit / number of results to be queried
    */
  case class Cursor(offset: Long, limit: Int)

  /**
    * Represents a streaming action on a repository
    * @tparam F Effect type
    * @tparam A Repository type
    */
  trait StreamingRepository[F[_], A] {
    import fs2.Stream

    /**
      * Stream on the repository
      * @return repository stream
      */
    def stream: Stream[F, A]
  }

  /**
    * Represents read actions on a repository
    * @tparam F Effect type
    * @tparam A Repository type
    */
  trait ReadRepository[F[_], A] {

    /**
      * Represents a find by identifier action
      * @param id identifier to be queried
      * @return the result of read action, either error or the queried object
      */
    def read(id: Identity): F[Result[A]]
  }

  /**
    * Represents a paginated read action on a repository
    * @tparam F Effect type
    * @tparam A Repository type
    */
  trait PaginatedReadRepository[F[_], A] {

    /**
      * Represents a paginated read action
      * @param query represents the condition for this query
      * @return the result of read action, either error or the queried set of of objects
      */
    def read(query: Query, cursor: Cursor): F[PaginatedResult[A]]
  }

  /**
    * Represent a repository
    * @tparam F Effect type
    * @tparam A Repository type
    */
  trait Repository[F[_], A] {
    /**
      * Name of the repository
      */
    val name: RepositoryName
  }


  /**
    * trait representing a repository exception
    */
    sealed trait RepositoryException

  /**
    * Represents a missing record / entity in the repository
    * @param repositoryName name of the repository
    * @param msg a meaningful not found (customized) message for the repository
    * @param id unique identifier of the missing entity / record
    */
    case class NotFound(repositoryName: RepositoryName, msg: String, id: Option[Identity] = None) extends RepositoryException
}
