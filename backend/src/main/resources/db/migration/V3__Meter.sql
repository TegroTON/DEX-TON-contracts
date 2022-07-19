CREATE TABLE "meter_metrics"
(
    "id"         BIGSERIAL PRIMARY KEY,
    "name"       TEXT  NOT NULL,
    "dimensions" JSONB NOT NULL,
    UNIQUE ("name", "dimensions")
);

CREATE TABLE "meter_values"
(
    "timestamp" TIMESTAMPTZ NOT NULL,
    "value"     FLOAT8      NOT NULL,
    "metric_id" BIGINT      NOT NULL REFERENCES "meter_metrics" ("id"),
    "metadata"  JSON        NOT NULL
);

CREATE VIEW "meter_measurements" AS
SELECT "timestamp", "value", "name", "dimensions", "metadata"
FROM "meter_values"
         INNER JOIN meter_metrics ON (metric_id = id);

CREATE FUNCTION create_metric(in_name TEXT, in_dims JSONB) RETURNS INT
    LANGUAGE plpgsql AS
$_$
DECLARE
    out_id INT;
BEGIN
    SELECT id
    INTO out_id
    FROM "meter_metrics" AS m
    WHERE m.name = in_name
      AND m.dimensions = in_dims;
    IF NOT FOUND THEN
        INSERT INTO "meter_metrics"
            ("name", "dimensions")
        VALUES (in_name, in_dims)
        RETURNING id into out_id;
    END IF;
    RETURN out_id;
END;
$_$;

CREATE RULE metrics_view_insert
    AS ON INSERT TO "meter_measurements"
    DO INSTEAD
    INSERT INTO "meter_values" (timestamp, value, metric_id, metadata)
    VALUES (NEW.TIMESTAMP,
            NEW.value,
            create_metric(NEW.name,
                          NEW.dimensions),
            NEW.metadata);

CREATE INDEX ON "meter_metrics" USING GIN (dimensions);

CREATE INDEX ON "meter_values" USING BTREE ("metric_id", "timestamp");
