package models

case class Contract(
                    name: String,
                    cities: Seq[String],
                    commercial_name: String,
                    country_code: String
                    )