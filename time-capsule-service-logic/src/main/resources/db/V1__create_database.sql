CREATE TABLE public.T_DISCOUNT
(
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(255)    UNIQUE      NOT NULL,
    description     TEXT                        NOT NULL,
    discount        NUMERIC(3, 2)               NOT NULL,
    start_date      DATE,
    expiration_date DATE

        CONSTRAINT percentage_check CHECK (discount >= 0 AND discount <= 1)
);