package org.shoonya.datatype.model

import cats.syntax.either._
import doobie.util.meta.Meta
import io.circe.Json
import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec}
import io.circe.generic.semiauto._
import io.circe.jawn.parse
import org.postgresql.util.PGobject

case class ApplicationEntity(id: Identity, applicationId: Identity, name: String, entity: Entity)

object ApplicationEntity {
  implicit val aeEncoder = deriveEncoder[ApplicationEntity]
  implicit val aeDecoder = deriveDecoder[ApplicationEntity]
}

case class Field(id: String, name: String, fieldType: String, displayProperties: Json)

object Field {
  implicit val jsonMeta: Meta[Json] =
    Meta.Advanced.other[PGobject]("json").timap[Json](
      a => parse(a.getValue).leftMap[Json](e => throw e).merge)(
      a => {
        val o = new PGobject
        o.setType("json")
        o.setValue(a.noSpaces)
        o
      }
    )

  implicit val fieldEncoder = deriveEncoder[Field]
  implicit val fieldDecoder = deriveDecoder[Field]
}

case class PrimaryKey(id: String, name: String, fieldIds: Seq[String])

object PrimaryKey {
  implicit val pkEncoder = deriveEncoder[PrimaryKey]
  implicit val pkDecoder = deriveDecoder[PrimaryKey]
}

case class UniqueKey(id: String, name: String, fieldIds: Seq[String])

object UniqueKey {
  implicit val ukEncoder = deriveEncoder[UniqueKey]
  implicit val ukDecoder = deriveDecoder[UniqueKey]
}

case class ForeignKey(id: String, name: String, entityId: Identity, applicationId: Identity, fieldIds: Seq[(String, String)])

object ForeignKey {
  implicit val fkEncoder = deriveEncoder[ForeignKey]
  implicit val fkDecoder = deriveDecoder[ForeignKey]
}

case class Index(id: String, name: String, fieldIds: Seq[String])

object Index {
  implicit val indexEncoder = deriveEncoder[Index]
  implicit val indexDecoder = deriveDecoder[Index]
}

case class Entity(fields: Seq[Field], primaryKey: PrimaryKey, uniqueKey: Seq[UniqueKey], foreignKeys: Seq[ForeignKey], indices: Seq[Index])

object Entity {
  implicit val entityEncoder = deriveEncoder[Entity]
  implicit val entityDecoder = deriveDecoder[Entity]
}

@ConfiguredJsonCodec
case class Entity1(name: String)

object Entity1 {

  /**
    * This is an implicit configuration used for deserializing github contributor json to [[Entity1]]
    */
  implicit val customConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
}
