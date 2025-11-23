-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: thuvienchuan
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `sachduocmuon`
--

DROP TABLE IF EXISTS `sachduocmuon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sachduocmuon` (
  `NgayMuon` date NOT NULL,
  `NgayTraThucTe` date DEFAULT NULL,
  `TrangThaiSach` varchar(20) NOT NULL,
  `IdSach` int NOT NULL,
  `IdPhieuMuon` int NOT NULL,
  PRIMARY KEY (`IdSach`,`IdPhieuMuon`),
  KEY `IdPhieuMuon` (`IdPhieuMuon`),
  CONSTRAINT `sachduocmuon_ibfk_1` FOREIGN KEY (`IdSach`) REFERENCES `sach` (`IdSach`),
  CONSTRAINT `sachduocmuon_ibfk_2` FOREIGN KEY (`IdPhieuMuon`) REFERENCES `phieumuon` (`IdPhieuMuon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sachduocmuon`
--

LOCK TABLES `sachduocmuon` WRITE;
/*!40000 ALTER TABLE `sachduocmuon` DISABLE KEYS */;
INSERT INTO `sachduocmuon` VALUES ('2023-10-01','2023-10-15','Da Tra',1,1),('2023-10-05',NULL,'Dang Muon',2,2),('2023-10-10','2023-10-20','Da Tra',3,3),('2023-10-12',NULL,'Dang Muon',4,4),('2023-10-15',NULL,'Qua Han',5,5);
/*!40000 ALTER TABLE `sachduocmuon` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-23 20:31:49
