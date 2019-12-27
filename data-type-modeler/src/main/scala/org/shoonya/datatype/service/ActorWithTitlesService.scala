package org.shoonya.datatype.service

import cats.effect._
import cats.implicits._
import shoonya.model._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.{HttpRoutes, Response}
import org.http4s.dsl.Http4sDsl
import shoonya.repository._
import io.circe.{Decoder, Encoder}

/**
  * ActorWithTitles service, represents the http routes for actor with title queries from user
  * @param repository repository for actor with titles repository interactions
  * @tparam F Effect type
  */
class ActorWithTitlesService[F[_] : Effect](repository: ActorWithTitlesRepository[F]) extends Http4sDsl[F] {

  // single actor query parameter decoder from the url
  object ActorNameDecoderMater extends QueryParamDecoderMatcher[String]("actor")
  // multi query parameter decoder for actor from the url
  object ActorNamesDecoderMater extends OptionalMultiQueryParamDecoderMatcher[String]("actor")

  // http routes for the service
  val routes = HttpRoutes.of[F]  {
    case _ @ GET -> Root :? ActorNameDecoderMater(actorName) =>
      repository.getActorTitles(actorName).flatMap(result => okOr404(result))

    case _ @ GET -> Root / "typecastinfo" :? ActorNameDecoderMater(actorName) =>
      repository.getActorTitles(actorName).flatMap { // Get the sequence of persons along with titles matching given name

        case Right(names) =>
          val actorsTypeCastInfo = names.map {
            name =>
              val titlesAsActor = getTitlesAsActor(name)      // Get titles where the person played as an actor / actress
              val genresCount = genreCountMap(titlesAsActor)  // Get count per genre
              val isTypeCasted = genresCount.toList.
                sortBy(_._2)(Ordering.Int.reverse).           // Sort in descending order of the count and
                headOption.
                exists(_._2 > titlesAsActor.size / 2)         // check whether the first entry is greater than the half of total titles

              ActorTypeCastInfo(actorName, isTypeCasted, titlesAsActor.size, genresCount)   // Create the ActorTypeCastInfo
          }

          Ok(actorsTypeCastInfo.asJson)

        case Left(_) => NotFound()
      }

    case _ @ GET -> Root / "coincidence" :? ActorNamesDecoderMater(actors)  =>
      val coincidence = actors.map {                          // map all the given actor inputs
        _.map(repository.getActorTitles).sequence.map {       // Get the sequence of persons along with titles matching given name
            namesWithTitles =>
              val nameAndTitles = namesWithTitles.map {
                case Right(names) => names map (name => (name.name, getTitlesAsActor(name)))  // Get titles where the person played as an actor / actress
                case Left(_) => Seq.empty[(Name, Set[Title])]
              }

              // initial coincidence is the head (the first actor titles)
              val initialCoincidence: Seq[(Set[Name], Set[Title])] = nameAndTitles.head.map(nt => (Set(nt._1), nt._2))
              nameAndTitles.foldLeft(initialCoincidence) {
                case (result, namesWithTitles) =>
                    for {
                      (nameSet, titleSet) <- result
                      (name, nameTitleSet) <- namesWithTitles
                    } yield {
                      (nameSet + name, titleSet intersect nameTitleSet)  // take the intersection of titles with current actor
                    }
              }
          }

      }

      coincidence.toEither match {
        case Right(result) => result.flatMap(coincidence => Ok(coincidence.asJson))
        case Left(_) => BadRequest()
      }
  }

  /**
    * Get the titles where the person has played as actor / actress
    * @param nameWithTitles NameWithTitles - all the titles the person was part of along with person info and associations
    * @return the titles where the person played as an actor / actress
    */
  private def getTitlesAsActor(nameWithTitles: NameWithTitlePrincipalsAndTitles): Set[Title] = {
    val titlesMap = nameWithTitles.titles.map(t => (t.tconst, t)).toMap
    // Get the associations as an actor / actress
    val actorTitlePrincipals = nameWithTitles.titlePrincipals.filter(tp => List("actor", "actress").contains(tp.category))

    (for {
      actorTitlePrincipal <- actorTitlePrincipals               // Get a single actor / actress association
      actorTitle <- titlesMap.get(actorTitlePrincipal.tconst)   // Get the corresponding title
    } yield actorTitle).toSet
  }

  /**
    * Get the count per genre
    * @param titles all the titles (along with genres info)
    * @return count per genre
    */
  private def genreCountMap(titles: Set[Title]): GenreCountMap = {
    // Group the title per genre and take the count of titles per genre
    (for {
      title <- titles.toSeq
      genre <- title.genres.map(_.split(",").toSet).getOrElse(Set[String]()).toSeq
    } yield (genre, title)).groupBy(_._1).map(x => (x._1, x._2.length))
  }

  // TODO: to be refactored and moved to a util package
  /**
    * Utility function for returning Ok or NotFound http response
    * @param result result or exception to be returned as an http response
    * @return Ok in case of success else NotFound
    */
  private def okOr404[A : Decoder : Encoder](result: Either[_, A]): F[Response[F]] =
    result match {
      case Left(_)    => NotFound()
      case Right(a)   => Ok(a.asJson)
    }
}

object ActorWithTitlesService {
  def apply[F[_]: Effect](repository: ActorWithTitlesRepository[F]): ActorWithTitlesService[F] =
    new ActorWithTitlesService(repository)
}
