-- Add column to store logic time of the transaction for easy lookup afterwards
ALTER TABLE swaps
    ADD COLUMN lt BIGINT NOT NULL DEFAULT 0;
