/*
CREATE TABLE `subscribed_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `subscription_id` varchar(255) COLLATE utf8_unicode_ci NOT NULL UNIQUE,
  `email` varchar(255) COLLATE utf8_unicode_ci NOT NULL UNIQUE,
  `first_name` varchar(100) COLLATE utf8_unicode_ci NULL,
  `gender` varchar(55) COLLATE utf8_unicode_ci NULL,
  `date_of_birth` char(10) NOT NULL,
  `consent_flag` boolean NOT NULL,
  `newsletter_id` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `active_flag` boolean NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;*/

DROP TABLE IF EXISTS subscribed_user;

--CREATE TABLE subscribed_user (
--  id INT AUTO_INCREMENT  PRIMARY KEY,
--  first_name VARCHAR(250) NOT NULL,
--  last_name VARCHAR(250) NOT NULL,
--  career VARCHAR(250) DEFAULT NULL
--);


CREATE TABLE subscribed_user (
  `id` INT unsigned NOT NULL AUTO_INCREMENT,
  `subscription_id` varchar(255) NOT NULL UNIQUE,
  `email` varchar(255) NOT NULL UNIQUE,
  `first_name` varchar(100) NULL,
  `gender` varchar(55) NULL,
  `date_of_birth` char(10) NOT NULL,
  `consent_flag` boolean NOT NULL,
  `newsletter_id` varchar(255) NOT NULL,
  `active_flag` boolean NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

--INSERT INTO billionaires (first_name, last_name, career) VALUES
--  ('Aliko', 'Dangote', 'Billionaire Industrialist'),
--  ('Bill', 'Gates', 'Billionaire Tech Entrepreneur'),
--  ('Folrunsho', 'Alakija', 'Billionaire Oil Magnate');
