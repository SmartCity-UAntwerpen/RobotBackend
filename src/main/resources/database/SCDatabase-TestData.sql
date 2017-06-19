-- SmartCity Database Test Data Version 0.0.1, 21/07/2016
--
-- Database: robotDB - 2016 UAntwerpen
-- ----------------------------------------------------
-- Server version   5.6.29

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
-- Test data for table `point`
--

DROP TABLE IF EXISTS robotDB.edges;
CREATE TABLE robotDB.edges (
  `ideges` bigint(20) NOT NULL AUTO_INCREMENT,
  `rfid` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `lockedBy` int(11) DEFAULT '1',
  PRIMARY KEY (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

--
-- Table attributes for Point
-- (Id, RFID, Type, Pointlock)
--
/*!40101 LOCK TABLES robotDB.point WRITE; */
/*!40000 ALTER TABLE robotDB.point DISABLE KEYS */;
INSERT INTO robotDB.edges VALUES
  -- Parking
  (1,'04 97 36 A2 7F 22 80','PARKING',0),
  (2,'04 41 70 92 1E 25 80','PARKING',0),
  (3,'04 70 39 32 06 27 80','PARKING',0),
  (4,'04 26 3E 92 1E 25 80','PARKING',0),
  (5,'04 3C 67 9A F6 1F 80','PARKING',0),
  (6,'04 18 25 9A 7F 22 80','PARKING',0),

  -- Crossings
  (7,'04 C5 88 8A C8 48 80','POINT',0),
  (8,'04 B3 88 8A C8 48 80','POINT',0),
  (9,'04 8D 88 8A C8 48 80','POINT',0),
  (10,'04 EC 88 8A C8 48 80','POINT',0),
  (11,'04 AA 88 8A C8 48 80','POINT',0),
  (12,'04 DA 88 8A C8 48 80','POINT',0),
  (13,'04 E3 88 8A C8 48 80','POINT',0),
  (14,'04 67 88 8A C8 48 80','POINT',0),
  (15,'04 7B 88 8A C8 48 80','POINT',0),
  (16,'04 BC 88 8A C8 48 80','POINT',0),
  (17,'04 96 88 8A C8 48 80','POINT',0),
  (18,'04 A1 88 8A C8 48 80','POINT',0),

  -- Traffic-Lights
  (19,'04 C4 FD 12 A9 34 80','TRAFFICLIGHT',0),  -- T1
  (20,'04 86 04 22 A9 34 84','TRAFFICLIGHT',0);  -- T2
/*!40000 ALTER TABLE robotDB.point ENABLE KEYS */;
/*!40101 UNLOCK TABLES; */

--
-- Test data for table `link`
--

DROP TABLE IF EXISTS robotDB.vertices;
CREATE TABLE robotDB.vertices (
  `idvertex` bigint(20) NOT NULL AUTO_INCREMENT,
  `length` bigint(20) DEFAULT '1',
  `startDir` varchar(255) DEFAULT NULL,
  `startPoint` bigint(20) DEFAULT NULL,
  `stopDir` varchar(255) DEFAULT NULL,
  `stopPoint` bigint(20) DEFAULT NULL,
  `weight` int(11) DEFAULT '1',
  PRIMARY KEY (`lid`),
  KEY `vertices_ibfk_1` (`startPoint`),
  KEY `vertices_ibfk_2` (`stopPoint`),
  CONSTRAINT `startPoint` FOREIGN KEY (`startPoint`) REFERENCES `edges` (`idedge`),
  CONSTRAINT `stopPoint` FOREIGN KEY (`stopPoint`) REFERENCES `edges` (`idedge`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8;

--
-- Table attributes for Link
-- (Id, length, startDirection, startPoint, stopDirection, stopPoint, weight)
--
/*!40101 LOCK TABLES robotDB.link WRITE; */
/*!40000 ALTER TABLE robotDB.link DISABLE KEYS */;
INSERT INTO robotDB.vertices VALUES
  (1,10,'E',1,'W',7,1), -- 1 -> 7
  (2,10,'W',7,'E',1,1), -- 7 -> 1

  (3,380,'S',2,'N',10,1), -- 2 -> 10
  (4,380,'N',10,'S',2,1), -- 10 -> 2

  (5,10,'E',3,'W',10,1), -- 3 -> 10
  (6,10,'W',10,'E',3,1), -- 10 -> 3

  (7,10,'W',4,'E',9,1), -- 4 -> 9
  (8,10,'E',9,'W',4,1), -- 9 -> 4

  (9,380,'N',5,'S',9,1), -- 5 -> 9
  (10,380,'S',9,'N',5,1), -- 9 -> 5

  (11,10,'W',6,'E',12,1), -- 6 -> 12
  (12,10,'E',12,'W',6,1), -- 12 -> 6

  (13,150,'E',7,'W',8,1), -- 7 -> 8
  (14,150,'W',8,'E',7,1), -- 8 -> 7

  (15,360,'N',7,'W',13,1), -- 7 -> 13
  (16,360,'W',13,'N',7,1), -- 13 -> 7

  (17,80,'S',7,'N',19,1), -- 7 -> T1 (19)

  (18,705,'E',8,'W',9,1), -- 8 -> 9

  (19,150,'N',8,'S',13,1), -- 8 -> 13
  (20,150,'S',13,'N',8,1), -- 13 -> 8

  (21,895,'N',9,'E',13,1), -- 9 -> 13

  (22,619,'S',10,'W',17,1), -- 10 -> 17

  (23,150,'E',11,'W',12,1), -- 11 -> 12
  (24,150,'W',12,'E',11,1), -- 12 -> 11

  (25,150,'W',11,'E',16,1), -- 11 -> 16
  (26,150,'E',16,'W',11,1), -- 16 -> 11

  (27,150,'S',11,'N',18,1), -- 11 -> 18
  (28,150,'N',18,'S',11,1), -- 18 -> 11

  (29,360,'S',12,'E',18,1), -- 12 -> 18
  (30,360,'E',18,'S',12,1), -- 18 -> 12

  (31,80,'N',12,'S',20,1), -- 12 -> T2 (20)

  (32,425,'N',14,'S',8,1), -- 14 -> 8

  (33,150,'E',14,'W',15,1), -- 14 -> 15
  (34,150,'W',15,'E',14,1), -- 15 -> 14

  (35,425,'S',15,'N',11,1), -- 15 -> 11

  (36,425,'W',16,'E',10,1), -- 16 -> 10

  (37,150,'S',16,'N',17,1), -- 16 -> 17
  (38,150,'N',17,'S',16,1), -- 17 -> 16

  (39,150,'E',17,'W',18,1), -- 17 -> 18
  (40,150,'W',18,'E',17,1), -- 18 -> 17

  (41,440,'S',19,'W',14,1), -- T1 (19) -> 14

  (42,440,'N',20,'E',15,1); -- T2 (20) -> 15
/*!40000 ALTER TABLE robotDB.link ENABLE KEYS */;
/*!40101 UNLOCK TABLES; */

--
-- Test data for table `bot`
--

DROP TABLE IF EXISTS robotDB.robots;
CREATE TABLE robotDB.robots (
  `idrobot` bigint(20) NOT NULL AUTO_INCREMENT,
  `jobID` bigint(20) DEFAULT NULL,
  `vertexProgress` int(11) DEFAULT NULL,
  `workingMode` varchar(255) DEFAULT NULL,
  `vertexLocation` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`idrobot`),
  KEY `FK_g2k7qbjgq85d7hmmov6r4benu` (`vertexLocation`),
  CONSTRAINT `FK_g2k7qbjgq85d7hmmov6r4benu` FOREIGN KEY (`vertexLocation`) REFERENCES `vertices` (`idvertex`)
) ENGINE=InnoDB AUTO_INCREMENT=260 DEFAULT CHARSET=utf8;

/*!40101 LOCK TABLES robotDB.bot WRITE; */
/*!40000 ALTER TABLE robotDB.bot DISABLE KEYS */;
/* INSERT INTO robotDB.bot VALUES */;
/*!40000 ALTER TABLE robotDB.bot ENABLE KEYS */;
/*!40101 UNLOCK TABLES; */

--
-- Test data for table `trafficlight`
--

DROP TABLE IF EXISTS robotDB.tlights;
CREATE TABLE robotDB.tlights (
  `idtlights` bigint(20) NOT NULL AUTO_INCREMENT,
  `direction` varchar(255) DEFAULT NULL,
  `vertexLocation` bigint(20) DEFAULT NULL,
  `vertexProgress` bigint(20) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idtlights`),
  KEY `tlights_ibfk_1` (`vertexLocation`),
 --CONSTRAINT `FK_36q0ntiwsex3ooj744c0t9py1` FOREIGN KEY (`point_id`) REFERENCES `point` (`pid`),
  CONSTRAINT `vertexLocation` FOREIGN KEY (`vertexLocation`) REFERENCES `vertices` (`idvertex`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*!40101 LOCK TABLES robotDB.trafficlight WRITE; */
/*!40000 ALTER TABLE robotDB.trafficlight DISABLE KEYS */;
INSERT INTO robotDB.trafficlight VALUES (1,'Z',5,'state');
/*!40000 ALTER TABLE robotDB.trafficlight ENABLE KEYS */;
/*!40101 UNLOCK TABLES; */
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
