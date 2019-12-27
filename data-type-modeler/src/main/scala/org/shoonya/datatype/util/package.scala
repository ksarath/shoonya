package org.shoonya.datatype

import cats.{Applicative, Semigroupal}
import cats.effect._
import cats.implicits._
import fs2.Stream

import scala.collection.immutable.Queue
import scala.concurrent.ExecutionContext.Implicits.global

package object util {
  implicit val executionContext = global
}
