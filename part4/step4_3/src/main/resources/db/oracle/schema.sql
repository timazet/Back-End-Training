-- DDL for DOG table
-- Wireshark - tcp.port eq 49161 && ip.src_host eq 127.0.0.1
-- DROP TABLE DOG; -- Uncomment to make tests repeatable
CREATE TABLE DOG (
  ID         VARCHAR2(40) PRIMARY KEY,
  NAME       VARCHAR2(100) NOT NULL,
  BIRTH_DATE DATE,
  HEIGHT     INT          NOT NULL,
  WEIGHT     INT          NOT NULL
);