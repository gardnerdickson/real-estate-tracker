DROP TABLE IF EXISTS realtor_property_listing;

CREATE TABLE realtor_property_listing (
  realtor_property_listing_id BIGINT    AUTO_INCREMENT PRIMARY KEY,
  au_created_timestamp        TIMESTAMP DEFAULT current_timestamp,

  realtor_id                  BIGINT,
  mls_number                  VARCHAR(20),
  description                 CLOB,
  num_bathrooms               VARCHAR(20),
  num_bedrooms                VARCHAR(20),
  building_type               VARCHAR(20),
  price                       INT,
  address                     VARCHAR(100),
  longitude                   DECIMAL(10, 7),
  latitude                    DECIMAL(10, 7),
  postal_code                 VARCHAR(10)
);


DROP TABLE IF EXISTS sigma_sold_property;

CREATE TABLE sigma_sold_property (
  sigma_sold_property_id BIGINT    AUTO_INCREMENT PRIMARY KEY,
  au_created_timestamp   TIMESTAMP DEFAULT current_timestamp,

  sigma_id               VARCHAR(50),
  mls_number             VARCHAR(20),
  latitude               DECIMAL(10, 7),
  longitude              DECIMAL(10, 7),
  listedPrice            VARCHAR(20),
  soldPrice              INT,
);


DROP TABLE IF EXISTS mongo_sold_property;

CREATE TABLE mongo_sold_property (
  mongo_sold_property_id BIGINT    AUTO_INCREMENT PRIMARY KEY,
  au_created_timestamp   TIMESTAMP DEFAULT current_timestamp,

  mongo_id               VARCHAR(50),
  mls_number             VARCHAR(20),
  days_on_market         INT,
  date_listed            DATE,
  listed_price           INT,
  date_sold              DATE,
  sold_price             INT,
);
