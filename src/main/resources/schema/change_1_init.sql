CREATE TABLE property_listing (
  property_listing_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
  au_created_timestamp TIMESTAMP,

  realtor_id           BIGINT,
  mls_number           VARCHAR(20),
  description          TEXT,
  num_bathrooms        VARCHAR(20),
  num_bedrooms         VARCHAR(20),
  building_type        CLOB,
  price                INT,
  address              VARCHAR(100),
  longitude            DECIMAL(10, 7),
  latitude             DECIMAL(10, 7),
  postal_code          VARCHAR(6)
)

