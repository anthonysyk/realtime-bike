CREATE TYPE rtbike.position (
  lat double,
  lng double
);

CREATE TYPE rtbike.state ( start_date text,
    last_update text,
    bikes_taken int,
    bikes_droped int,
    availability int,
    counter int,
    counterStateChanged int
);

CREATE TABLE rtbike.stations (
    id UUID PRIMARY KEY,
    number int,
    name text,
    address text,
    position FROZEN<position>,
    banking boolean,
    bonus boolean,
    status text,
    contract_name text,
    bike_stands int,
    available_bike_stands int,
    available_bikes int,
    last_update timestamp,
    state FROZEN<state>
);
