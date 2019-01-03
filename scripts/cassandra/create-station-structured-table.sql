CREATE TABLE rtbike.station_structured_topic(
                year int,
                month int,
                day int,
                event_timestamp bigint,
                event_id varchar,
                source varchar,
                event_type varchar,
                readable_date varchar,
                event_raw varchar,
                offset bigint,
                PRIMARY KEY((year, event_type), event_timestamp, offset, event_id))
                WITH CLUSTERING ORDER BY (event_timestamp DESC);
