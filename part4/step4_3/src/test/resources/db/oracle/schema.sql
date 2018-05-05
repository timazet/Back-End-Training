-- DDL for DOG table
-- DROP TABLE DOG; -- Uncomment to make tests repeatable
CREATE TABLE DOG (
  ID         VARCHAR2(40) PRIMARY KEY,
  NAME       VARCHAR2(100) NOT NULL,
  BIRTH_DATE DATE,
  HEIGHT     INT           NOT NULL,
  WEIGHT     INT           NOT NULL
);