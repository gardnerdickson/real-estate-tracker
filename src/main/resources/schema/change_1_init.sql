CREATE TABLE IF NOT EXISTS execution (
  au_execution_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  au_start_timestamp TIMESTAMP,
  au_end_timestamp   TIMESTAMP,
  au_status          VARCHAR(10), // STARTED, COMPLETE, FAILED

  date               DATE,
  minimum_price      INT,
  maximum_price      INT,
  minimum_latitude   DECIMAL(10, 7),
  maximum_latitude   DECIMAL(10, 7),
  minimum_longitude  DECIMAL(10, 7),
  maximum_longitude  DECIMAL(10, 7),
);


CREATE TABLE IF NOT EXISTS realtor_property_listing (
  realtor_property_listing_id BIGINT    AUTO_INCREMENT PRIMARY KEY,
  au_created_timestamp        TIMESTAMP DEFAULT current_timestamp,
  au_execution_id             BIGINT,

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
  postal_code                 VARCHAR(10),

  FOREIGN KEY (au_execution_id) REFERENCES execution
);


CREATE TABLE IF NOT EXISTS sigma_sold_property (
  sigma_sold_property_id BIGINT    AUTO_INCREMENT PRIMARY KEY,
  au_created_timestamp   TIMESTAMP DEFAULT current_timestamp,
  au_execution_id        BIGINT,

  sigma_id               VARCHAR(50),
  mls_number             VARCHAR(20),
  latitude               DECIMAL(10, 7),
  longitude              DECIMAL(10, 7),
  listed_price           VARCHAR(20),
  sold_price             INT,

  FOREIGN KEY (au_execution_id) REFERENCES execution
);


CREATE TABLE IF NOT EXISTS mongo_sold_property (
  mongo_sold_property_id BIGINT    AUTO_INCREMENT PRIMARY KEY,
  au_created_timestamp   TIMESTAMP DEFAULT current_timestamp,
  au_execution_id        BIGINT,

  mongo_id               VARCHAR(50),
  mls_number             VARCHAR(20),
  days_on_market         INT,
  date_listed            DATE,
  listed_price           INT,
  date_sold              DATE,
  sold_price             INT,

  FOREIGN KEY (au_execution_id) REFERENCES execution
);


