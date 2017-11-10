CREATE DATABASE `oracle` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_turkish_ci */;
CREATE TABLE `oracle`.`current_currency` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `MARKETNAME` varchar(20) COLLATE utf8_turkish_ci DEFAULT NULL,
  `HIGH` float DEFAULT NULL,
  `LOW` float DEFAULT NULL,
  `LAST` float DEFAULT NULL,
  `BASEVOLUME` float DEFAULT NULL,
  `TIMESTAMP` timestamp NULL DEFAULT NULL,
  `BID` float DEFAULT NULL,
  `ASK` float DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

CREATE TABLE `oracle`.`minute_currency` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `HIGH` float DEFAULT NULL,
  `LOW` float DEFAULT NULL,
  `LAST` float DEFAULT NULL,
  `BASEVOLUME` float DEFAULT NULL,
  `TIMESTAMP` timestamp NULL DEFAULT NULL,
  `MARKETNAME` varchar(20) COLLATE utf8_turkish_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

CREATE TABLE `oracle`.`bollinger_predictions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `MARKETNAME` varchar(20) COLLATE utf8_turkish_ci DEFAULT NULL,
  `HIGH` float DEFAULT NULL,
  `MEAN` float DEFAULT NULL,
  `LOW` float DEFAULT NULL,
  `TIMESTAMP` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

CREATE TABLE `oracle`.`cci_predictions` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `MARKETNAME` VARCHAR(20) NULL,
  `CCI_RATE` FLOAT NULL,
  `TIMESTAMP` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`));
