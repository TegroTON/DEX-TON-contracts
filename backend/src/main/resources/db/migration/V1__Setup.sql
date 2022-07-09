CREATE TABLE "exchange_pairs"
(
    "address"        BYTEA PRIMARY KEY,
    "left"           BYTEA,
    "left_reserved"  NUMERIC(40) NOT NULL, /* up to 120 bits, that's like ~37 digits or so */
    "right"          BYTEA       NOT NULL,
    "right_reserved" NUMERIC(40) NOT NULL,
    "discovered"     TIMESTAMP   NOT NULL,
    "updated"        TIMESTAMP   NOT NULL
);

CREATE TABLE "jettons"
(
    "address"      BYTEA PRIMARY KEY,
    "total_supply" NUMERIC(40) NOT NULL,
    "mintable"     BOOLEAN     NOT NULL,
    "admin"        BYTEA       NOT NULL,
    "name"         VARCHAR(255),
    "description"  TEXT,
    "symbol"       VARCHAR(255),
    "decimals"     INTEGER     NOT NULL,
    "image"        TEXT,
    "image_data"   BYTEA       NOT NULL,
    "discovered"   TIMESTAMP   NOT NULL,
    "updated"      TIMESTAMP   NOT NULL
);
