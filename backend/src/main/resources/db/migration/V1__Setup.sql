/* Database definitions for core functionality */

-- Exchange pair contracts
CREATE TABLE pairs
(
    address       BYTEA PRIMARY KEY,    -- Address of the exchange pair contract
    base          BYTEA       NOT NULL, -- Base token address
    quote         BYTEA       NOT NULL, -- Quote token address
    base_wallet   BYTEA       NOT NULL, -- Address of the token wallet holding base token
    quote_wallet  BYTEA       NOT NULL, -- Address of the token wallet holding quote token
    base_reserve  NUMERIC(80) NOT NULL, -- Amount of base token reserved, uint256, that's like ~78 digits or so
    quote_reserve NUMERIC(80) NOT NULL, -- Amount of quote token reserved
    updated       TIMESTAMPTZ NOT NULL, -- Last time this information was updated
    UNIQUE (base, quote) /* All pairs are unique. Note: This does not prevent from potentially adding a pair BBB/AAA
                            when AAA/BBB is already present. However, to achieve this, one would have to deploy two
                            separate exchange pair contracts with separate liquidity pools (token addresses are expected
                            to be hardcoded). But you wouldn't do that, right? */
);

-- Jettons and LP-tokens
CREATE TABLE tokens
(
    address     BYTEA PRIMARY KEY,    -- Jetton master address, for LP tokens this is the same as exchange pair
    supply      NUMERIC(80) NOT NULL,
    mintable    BOOLEAN     NOT NULL,
    admin       BYTEA       NOT NULL,
    /* While following fields are deemed optional per token data standard, they're mandatory here because:
       1. STFU
       2. We don't want no-name, description-less jettons that cannot be identified by their symbol (ticker)
     */
    name        TEXT        NOT NULL,
    description TEXT        NOT NULL,
    symbol      TEXT        NOT NULL, /* These are usually 4-5 characters long, but it is not enforced for
                                         robustness reasons,must be manually checked for being too long/inappropriate */
    decimals    INTEGER     NOT NULL,
    image       TEXT        NOT NULL, -- This also includes potential `image_data` field, simply encoded as data base64
    updated     TIMESTAMPTZ NOT NULL
);

-- Ensure all symbols are unique, not permitting two tokens aaa and AaA to be added to the db
CREATE UNIQUE INDEX ON tokens (UPPER(symbol));

-- Make sure that table at the very least contains native toncoin
INSERT INTO tokens
VALUES ('\xB5EE9C720101010100240000437F9999999999999999999999999999999999999999999999999999999999999999C0', -- Elector address as AddrStd
        5000000000000000000, -- Five billion toncoins in nanotons
        TRUE, -- There's inflation, new coins are being put into circulation
        '\xB5EE9C720101010100240000439FE66666666666666666666666666666666666666666666666666666666666666670', -- Same elector address but as MsgAddressInt
        'Toncoin',
        'Native currency of The Open Network',
        'TON',
        9,
        'data:image/png;base64,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',
        now());


-- Information about all swaps done through any exchange pair
-- This is mostly meant as historical data, rows are inserted but only
CREATE TABLE swaps
(
    address   BYTEA       NOT NULL, -- Beneficiary
    pair      BYTEA       NOT NULL, -- Exchange pair address
    token     BYTEA       NOT NULL, -- Address of the token that was sent to the beneficiary
    amount    NUMERIC(80) NOT NULL, -- Amount that was sent
    timestamp TIMESTAMPTZ NOT NULL  -- When the swap occurred
);

CREATE INDEX ON swaps (address, timestamp);


/* Extra definitions to support collection of different metrics.
   Metrics are stored in the same database for convenience and ease of deployment,
   PostgreSQL is plenty adequate for project's needs.

   Inspired by https://www.youtube.com/watch?v=atvgYJTBEF4 but I am too lazy to do it right, thus just one table
 */
CREATE TABLE metrics
(
    name      TEXT        NOT NULL,
    value     FLOAT8      NOT NULL,
    metadata  JSON        NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL
);

CREATE INDEX ON metrics (name, timestamp);
