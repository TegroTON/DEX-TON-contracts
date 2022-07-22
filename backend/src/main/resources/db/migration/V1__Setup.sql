CREATE TABLE "exchange_pairs"
(
    "address"       BYTEA PRIMARY KEY,
    "left"          BYTEA,
    "right"         BYTEA       NOT NULL,
    "left_reserve"  NUMERIC(40) NOT NULL, /* up to 120 bits, that's like ~37 digits or so */
    "right_reserve" NUMERIC(40) NOT NULL,
    "discovered"    TIMESTAMPTZ NOT NULL,
    "updated"       TIMESTAMPTZ NOT NULL,
    UNIQUE ("left", "right") /* All exchange pairs must be unique */
);

CREATE TABLE "jettons"
(
    "address"      BYTEA PRIMARY KEY,
    "total_supply" NUMERIC(40) NOT NULL,
    "mintable"     BOOLEAN     NOT NULL,
    "admin"        BYTEA       NOT NULL,
    "name"         VARCHAR(255),
    "description"  TEXT,
    "symbol"       VARCHAR(12) UNIQUE, /* Usually these are 4-5 characters long.
                                          A dozen selected to accommodate rather long LP-tokens */
    "decimals"     INTEGER     NOT NULL,
    "image"        TEXT,
    "image_data"   BYTEA       NOT NULL,
    "discovered"   TIMESTAMPTZ NOT NULL,
    "updated"      TIMESTAMPTZ NOT NULL
);
