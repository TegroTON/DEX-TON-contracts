CREATE TABLE measurements_metrics
(
    "id"        BIGSERIAL PRIMARY KEY,
    "name"      TEXT  NOT NULL,
    "dimension" JSONB NOT NULL,
    UNIQUE ("name", dimension)
);

CREATE TABLE measurements_values
(
    "timestamp" TIMESTAMPTZ NOT NULL,
    "value"     FLOAT8      NOT NULL,
    "metric_id" BIGINT      NOT NULL REFERENCES measurements_metrics ("id"),
    "metadata"  JSON        NOT NULL
);

CREATE VIEW measurements AS
SELECT "name", dimension, "metadata", "timestamp", "value"
FROM measurements_values
         INNER JOIN measurements_metrics ON (metric_id = id);

CREATE FUNCTION create_metric(in_name TEXT, in_dims JSONB) RETURNS INT
    LANGUAGE plpgsql AS
$_$
DECLARE
    out_id INT;
BEGIN
    SELECT id
    INTO out_id
    FROM measurements_metrics
    WHERE name = in_name
      AND dimension = in_dims;
    IF NOT FOUND THEN
        INSERT INTO measurements_metrics
            ("name", dimension)
        VALUES (in_name, in_dims)
        RETURNING id into out_id;
    END IF;
    RETURN out_id;
END;
$_$;

CREATE RULE measurements_insert
    AS ON INSERT TO measurements
    DO INSTEAD
    INSERT INTO measurements_values (timestamp, value, metric_id, metadata)
    VALUES (NEW.timestamp,
            NEW.value,
            create_metric(NEW.name,
                          NEW.dimension),
            NEW.metadata);

CREATE INDEX ON measurements_metrics USING GIN (dimension);

CREATE INDEX ON measurements_values USING BTREE ("metric_id", "timestamp");
