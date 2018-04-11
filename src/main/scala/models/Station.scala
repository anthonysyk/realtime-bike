package models

case class Station(
                    number: Int,
                    name: String,
                    address: String,
                    position: Position,
                    banking: Boolean,
                    bonus: Boolean,
                    status: String,
                    contract_name: String,
                    bike_stands: Int,
                    available_bike_stands: Int,
                    available_bikes: Int,
                    last_update: Long
                  )

case class Position(
                     lat: Double,
                     lng: Double
                   )