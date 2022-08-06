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
VALUES ('\xB5EE9C7201010101000300000120', -- addr_none
        5000000000000000000, -- Five billion toncoins in nanotons
        TRUE, -- There's inflation, new coins are being put into circulation
        '\xB5EE9C720101010100240000439FE66666666666666666666666666666666666666666666666666666666666666670', -- elector address, doesnt matter but has to be MsgAddressInt
        'Toncoin',
        'Native currency of The Open Network',
        'TON',
        9,
        'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNTYiIGhlaWdodD0iNTYiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGNpcmNsZSBjeD0iMjgiIGN5PSIyOCIgcj0iMjgiIGZpbGw9IiMwOEMiLz48cGF0aCBmaWxsLXJ1bGU9ImV2ZW5vZGQiIGNsaXAtcnVsZT0iZXZlbm9kZCIgZD0iTTIwLjIwOSAxOC41MDRoMTUuNzA0Yy41NTYgMCAxLjExMS4wODIgMS42OTEuMzUyLjY5Ni4zMjUgMS4wNjUuODM2IDEuMzIzIDEuMjEzYTMuNTI0IDMuNTI0IDAgMCAxIC41MTcgMS44NDNjMCAuNTk4LS4xNDIgMS4yNDgtLjQ2IDEuODE1bC0uMDEuMDE2LTkuOTIyIDE3LjA0M2ExLjIxNyAxLjIxNyAwIDAgMS0yLjEwOC0uMDA3bC05Ljc0LTE3LjAwN2ExLjcgMS43IDAgMCAxLS4wMDgtLjAxNGMtLjIyMy0uMzY3LS41NjgtLjkzNS0uNjI4LTEuNjY4YTMuMzIyIDMuMzIyIDAgMCAxIC40MzUtMS45MzYgMy4zMDYgMy4zMDYgMCAwIDEgMS40NjItMS4zNGMuNjU3LS4zMDcgMS4zMjMtLjMxIDEuNzQ0LS4zMVptNi41NzQgMi40MzVoLTYuNTc0Yy0uNDMyIDAtLjU5OC4wMjctLjcxNC4wOGEuODcuODcgMCAwIDAtLjM4NS4zNTQuODg4Ljg4OCAwIDAgMC0uMTE2LjUxN2MuMDA5LjEwNC4wNTEuMjIyLjMuNjMzbC4wMTUuMDI2IDcuNDc0IDEzLjA1di0xNC42NlptMi40MzQgMHYxNC43MjRsNy42NDctMTMuMTM1YTEuMzMgMS4zMyAwIDAgMCAuMTQ1LS42MTVjMC0uMTg4LS4wNC0uMzUtLjEyNi0uNTE5YTEuOTU3IDEuOTU3IDAgMCAwLS4xOTQtLjI0OC4zNTcuMzU3IDAgMCAwLS4xMTQtLjA4M2MtLjE4LS4wODQtLjM2NS0uMTI0LS42NjItLjEyNGgtNi42OTVaIiBmaWxsPSIjZmZmIi8+PC9zdmc+',
        now());

-- Information about all swaps done through any exchange pair
-- This is mostly meant as historical data, rows are inserted but only
CREATE TABLE swaps
(
    hash      BYTEA PRIMARY KEY,    -- Transaction hash
    lt        BIGINT      NOT NULL, -- Logical time
    src       BYTEA       NOT NULL, -- Source address
    dest      BYTEA       NOT NULL, -- Destination address
    amount    NUMERIC(80) NOT NULL, -- Amount that was sent
    timestamp TIMESTAMPTZ NOT NULL  -- When the swap was recorded
);

-- User's token wallets, including LP wallets
CREATE TABLE wallets
(
    address BYTEA PRIMARY KEY,
    balance NUMERIC(80) NOT NULL,
    owner   BYTEA       NOT NULL, -- Owner address
    master  BYTEA       NOT NULL, -- Jetton master contract address
    updated TIMESTAMPTZ NOT NULL
);

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
