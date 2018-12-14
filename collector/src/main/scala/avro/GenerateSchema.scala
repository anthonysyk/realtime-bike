package avro

import models.Station
import versatile.avro.CreateSchema

/**
  * No need
  */
object GenerateSchema extends App {

  CreateSchema.createSchemaFile[Station]

}
