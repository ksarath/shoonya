package org.shoonya.datatype

import java.util.UUID

import cats.Id

package object model {
  // All the type aliases for the model classes

  /**
    * Represents the identifier for a model
    */
  type Identity = Id[UUID]
}
