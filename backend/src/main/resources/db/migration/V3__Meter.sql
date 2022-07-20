CREATE TABLE measurements_metrics
(
    "id"         BIGSERIAL PRIMARY KEY,
    "name"       TEXT  NOT NULL,
    "dimensions" JSONB NOT NULL,
    UNIQUE ("name", "dimensions")
);

CREATE TABLE measurements_values
(
    "timestamp" TIMESTAMPTZ NOT NULL,
    "value"     FLOAT8      NOT NULL,
    "metric_id" BIGINT      NOT NULL REFERENCES measurements_metrics ("id"),
    "metadata"  JSON        NOT NULL
);

CREATE VIEW measurements AS
SELECT "name", "dimensions", "metadata", "timestamp", "value"
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
      AND dimensions = in_dims;
    IF NOT FOUND THEN
        INSERT INTO measurements_metrics
            ("name", "dimensions")
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
                          NEW.dimensions),
            NEW.metadata);

CREATE INDEX ON measurements_metrics USING GIN (dimensions);

CREATE INDEX ON measurements_values USING BTREE ("metric_id", "timestamp");
