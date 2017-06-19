-- SmartCity Database Schema Version 0.0.1, 21/07/2016
--
-- Database: robotDB - 2016 UAntwerpen
-- ----------------------------------------------------
-- Server version   5.6.29

CREATE DATABASE  IF NOT EXISTS `robotDB` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `robotDB`;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bot`
--

DROP TABLE IF EXISTS `robots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `robots` (
  `idrobot` bigint(20) NOT NULL AUTO_INCREMENT,
  `jobID` bigint(20) DEFAULT NULL,
  `vertexProgress` int(11) DEFAULT NULL,
  `jobFrom` int(11) DEFAULT NULL,
  `jobTo` int(11) DEFAULT NULL,
  `workingMode` varchar(255) DEFAULT NULL,
  `vertexLocation` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`idrobot`),
  KEY `FK_g2k7qbjgq85d7hmmov6r4benu` (`vertexLocation`),
  CONSTRAINT `FK_g2k7qbjgq85d7hmmov6r4benu` FOREIGN KEY (`vertexLocation`) REFERENCES `vertices` (`idvertex`)
) ENGINE=InnoDB AUTO_INCREMENT=260 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `link`
--

DROP TABLE IF EXISTS `vertices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vertices` (
  `idvertex` bigint(20) NOT NULL AUTO_INCREMENT,
  `length` bigint(20) DEFAULT '1',
  `startDir` varchar(255) DEFAULT NULL,
  `startPoint` bigint(20) DEFAULT NULL,
  `stopDir` varchar(255) DEFAULT NULL,
  `stopPoint` bigint(20) DEFAULT NULL,
  `pid` bigint(20) DEFAULT NULL,
  `weight` int(11) DEFAULT '1',
  `trafficWeight` int(11) DEFAULT '1',
  `lockedBY` int(11) DEFAULT '1',
  PRIMARY KEY (`idvertex`),
  KEY `vertices_ibfk_1` (`startPoint`),
  KEY `vertices_ibfk_3` (`stopPoint`),
  KEY `FK_fraaqnos7yp6t7drynxqo0ov` (`pid`),
  CONSTRAINT `FK_fraaqnos7yp6t7drynxqo0ov` FOREIGN KEY (`pid`) REFERENCES `point` (`pid`),
  CONSTRAINT `startPoint` FOREIGN KEY (`startPoint`) REFERENCES `edges` (`idedge`),
  CONSTRAINT `stopPoint` FOREIGN KEY (`stopPoint`) REFERENCES `edges` (`idedge`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `point`
--

DROP TABLE IF EXISTS `edges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `edges` (
  `idedge` bigint(20) NOT NULL AUTO_INCREMENT,
  `rfid` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `lockedBy` int(11) DEFAULT '1',
  PRIMARY KEY (`idedge`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `trafficlight`
--

DROP TABLE IF EXISTS `tlights`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tlights` (
  `idtlight` bigint(20) NOT NULL AUTO_INCREMENT,
  `direction` varchar(255) DEFAULT NULL,
  `vertexLocation` bigint(20) DEFAULT NULL,
  `vertexProgress` bigint(20) DEFAULT NULL,
  `state` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`tlid`),
  KEY `fk_pointid` (`point_id`),
  CONSTRAINT `FK_36q0ntiwsex3ooj744c0t9py1` FOREIGN KEY (`point_id`) REFERENCES `point` (`pid`),
  CONSTRAINT `fk_pointid` FOREIGN KEY (`point_id`) REFERENCES `point` (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
