CREATE DATABASE  IF NOT EXISTS `barberbooker` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `barberbooker`;
-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: barberbooker
-- ------------------------------------------------------
-- Server version	8.0.32

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
-- Table structure for table `barber`
--

DROP TABLE IF EXISTS `barber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `barber` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `password` varchar(200) NOT NULL,
  `barbershopName` varchar(100) NOT NULL,
  `price` double NOT NULL,
  `phone` varchar(20) NOT NULL,
  `country` varchar(100) NOT NULL,
  `city` varchar(45) NOT NULL,
  `municipality` varchar(45) NOT NULL,
  `address` varchar(45) NOT NULL,
  `workingDays` varchar(45) NOT NULL,
  `workingHours` varchar(45) NOT NULL,
  `fcmToken` varchar(500) NOT NULL DEFAULT '',
  `googleAccessToken` varchar(500) NOT NULL DEFAULT '',
  `googleRefreshToken` varchar(500) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `barber`
--

LOCK TABLES `barber` WRITE;
/*!40000 ALTER TABLE `barber` DISABLE KEYS */;
INSERT INTO `barber` VALUES
(10,'cutandgo@gmail.com','faaa0562f5bc8e718a57e37e3931341d1d6f9a3cdb3ddbcaeac4dba9187335f3','Cut&Go',700,'062/444-222','Serbia','Belgrade','Karaburma','Marijane Gregoran 68','MON, TUE, WED, THU','10:00 - 20:00','','',''),
(11,'brotherhood@gmail.com','708696e356e08cb5da0bc57edbdbbdc160a05d137263caf5a65743cf7b2dc265','Brotherhood Barbershop',850,'062/123-4253','Serbia','Belgrade','Novi Beograd','Palmira Toljatija 5','MON, TUE, WED, THU, FRI, SAT','00:00 - 00:00','','',''),
(12,'ostro@gmail.com','e878f7734b6f947ed4d521e19ba83bb2517eb0e66a0de0369747f3c43e647234','Ostro Barbershop',600,'062/466-313','Serbia','Belgrade','Vracar','Kralja Milutina 30','MON, TUE, WED, THU, FRI, SAT, SUN','10:00 - 18:00','','',''),
(13,'makva@gmail.com','3ba6cc330e4d3027d03ecff2ff28528444bc12b008bd55fc8bbfada73580c648','Makva',675,'062/425-222','Serbia','Belgrade','Stari Grad','Vojvode Dobrnjca 42','MON, TUE, WED, THU, FRI','08:00 - 19:30','eScJh-k4QLCnfa6bN277js:APA91bHO7K2YMasohlXYq9-mywdLEK3uGc0u-qLfniJ5ne57mRppfGe61WTr1C0Bld_g6WINtVGzQ5b84f4ztNAtjaJ-Ap2unH_uTct807QlzZwgnbmHR3zPW9lGiA-La_yTpoazGjN9','',''),
(14,'benvenuto@gmail.com','f90cad0b6fa7f1fbe849b0918110a66e10264e9686942dcfea8be13cbec9f731','Studio Benvenuto',1000,'064/555-112','Serbia','Belgrade','Vracar','Trebinjska 9','MON, TUE, WED, THU, FRI','08:00 - 21:00','','',''),
(15,'erato@gmail.com','0e012d34f3cda78864c8119dd4cb625f4a1e417f4ca9c9311cde6a308f031e65','Studio Erato',750,'061/126-496','Serbia','Belgrade','Novi Beograd','Spanskih boraca 24','MON, TUE, WED','08:30 - 22:30','','',''),
(16,'bogdan@gmail.com','cc81be0425a980e929094d21f9caebacdd84f37c8bf1145108c0ec1a0551ab1d','Bogdan Team',1000,'065/124-7986','Serbia','Belgrade','Zvezdara','Bulevar kralja Aleksandra 432','MON, TUE, WED, THU, FRI, SAT, SUN','00:00 - 00:00','','',''),
(17,'izvor@gmail.com','763efa53815b6191a72b1b52a75d0a17b101291f0a23fd75ec8fb93e8d13ee65','Izvor',880,'064/555-222','Serbia','Belgrade','Vracar','Nevesinjska 8','MON, TUE, WED, THU, FRI, SAT','08:00 - 17:00','','',''),
(18,'sensa@gmail.com','2b146740e246c976549b88226e2f1169d2e6ae4e54654dd9ea86a778b134a0de','Sensa',680,'061/462-111','Serbia','Belgrade','Zvezdara','Suboticka 23','MON, TUE, WED, THU','09:00 - 20:00','','',''),
(19,'claire@gmail.com','8886508e03676b25c1511e8df94990ecb989bc30f4458e0d6f77f89493894f74','Claire',950,'063/456-214','Serbia','Belgrade','Vracar','Internacionalnih brigada 7','MON, TUE, WED, THU, FRI, SAT','08:00 - 20:00','','','');
/*!40000 ALTER TABLE `barber` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client`
--

DROP TABLE IF EXISTS `client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `client` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(200) NOT NULL,
  `password` varchar(200) NOT NULL,
  `name` varchar(200) NOT NULL,
  `surname` varchar(200) NOT NULL,
  `fcmToken` varchar(500) NOT NULL DEFAULT '',
  `googleAccessToken` varchar(500) NOT NULL DEFAULT '',
  `googleRefreshToken` varchar(500) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client`
--

LOCK TABLES `client` WRITE;
/*!40000 ALTER TABLE `client` DISABLE KEYS */;
INSERT INTO `client` VALUES
(3,'sarenac.pavle@gmail.com','8ac4b6c44b92ee792a2a03b9a87b0dce356ac26c597689bebd4371669d079525','Pavle','Sarenac','eo_FVlWoTP-Ax6L3257Vo1:APA91bGoQNXquNVBIcwBqxS0hIgZmNEaLkSWpD-pnTsV8LsRM-KrFPKnN8crKOCnimHSskUbprA6UIgVfdvJt09rGfPXW9tNjRkTaBxHSn11G65SbACFXxHcv5m_ycRnMRNe0hD68bCd','',''),
(4,'ljubmajstorovic9@gmail.com','f3ac5f744af42f5d5fdda00f60282e7f0bb7dd668cd11d15c3156e16c5037130','Ljubica','Majstorovic','','',''),
(5,'posta.nikolan@gmail.com','e380b769e8ae1c6dc3f65951addd71d8592ba4c373769d8d5191e44a17edbd74','Nikola','Nikolic','','',''),
(6,'luka@gmail.com','68541b7da88970696c37c0bc52fa2b9eba389a5b69990a8d3d7e005d443180dc','Luka','Golijanin','','',''),
(7,'vlajko@gmail.com','6896eca8ecd6ed25d21dc2c9c022dae3a967d711f63b7eeec2a5cd0ad439ca69','Vladimir','Aleksic','','',''),
(8,'dovla@gmail.com','0778082328ed32effc33c4ebbeb6905dee611f65a0fa6c92aeddb07df44b26bf','Vladimir','Beljic','','',''),
(9,'saki@gmail.com','ef9e99c8aad3f92735535c07b4f071d78217e007a368023ee9ffc0f1bd1640df','Aleksandar','Sarac','','',''),
(10,'mateja@gmail.com','04c9ebbe5a18343d0926691d2b4a76f748830102c162e1baf93e17e379bd8a84','Mateja','Milicevic','','',''),
(11,'voja@gmail.com','c103746a44f1e888e551b1c5495feeba22d49bc4d039b30a6fa40633af082147','Vojin','Radosavljevic','','',''),
(12,'simpri@gmail.com','5e51355f84e51bbdff5b5b8cf1e728c1332e33cf8d9fd7506f7f18283996814c','Jovan','Simpraga','','','');
/*!40000 ALTER TABLE `client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `id` int NOT NULL AUTO_INCREMENT,
  `clientEmail` varchar(45) NOT NULL,
  `barberEmail` varchar(45) NOT NULL,
  `date` varchar(45) NOT NULL,
  `startTime` varchar(45) NOT NULL,
  `endTime` varchar(45) NOT NULL,
  `status` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=197 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES
(9,'sarenac.pavle@gmail.com','makva@gmail.com','23/11/2025','10:00','10:30','PENDING'),
(10,'sarenac.pavle@gmail.com','makva@gmail.com','24/11/2025','08:00','08:30','ACCEPTED'),
(11,'ljubmajstorovic9@gmail.com','makva@gmail.com','30/08/2025','08:30','09:00','REJECTED'),
(12,'posta.nikolan@gmail.com','makva@gmail.com','23/11/2025','09:00','09:30','ACCEPTED'),
(13,'luka@gmail.com','makva@gmail.com','23/11/2025','09:30','10:00','PENDING'),
(14,'vlajko@gmail.com','makva@gmail.com','27/11/2025','10:00','10:30','ACCEPTED'),
(15,'dovla@gmail.com','makva@gmail.com','23/11/2025','10:30','11:00','PENDING'),
(16,'saki@gmail.com','makva@gmail.com','23/11/2025','11:00','11:30','PENDING'),
(17,'mateja@gmail.com','makva@gmail.com','23/11/2025','11:30','12:00','PENDING'),
(18,'voja@gmail.com','makva@gmail.com','23/11/2025','12:00','12:30','PENDING'),
(19,'simpri@gmail.com','makva@gmail.com','28/11/2025','13:30','14:00','ACCEPTED'),
(20,'sarenac.pavle@gmail.com','makva@gmail.com','29/11/2025','08:00','08:30','PENDING'),
(21,'ljubmajstorovic9@gmail.com','makva@gmail.com','30/07/2025','08:30','09:00','REJECTED'),
(22,'posta.nikolan@gmail.com','makva@gmail.com','30/07/2025','09:00','09:30','REJECTED'),
(23,'luka@gmail.com','makva@gmail.com','30/07/2025','09:30','10:00','REJECTED'),
(24,'vlajko@gmail.com','makva@gmail.com','30/07/2025','10:00','10:30','REJECTED'),
(25,'dovla@gmail.com','makva@gmail.com','30/07/2025','10:30','11:00','REJECTED'),
(26,'saki@gmail.com','makva@gmail.com','30/07/2025','11:00','11:30','REJECTED'),
(27,'mateja@gmail.com','makva@gmail.com','30/07/2025','11:30','12:00','DONE_SUCCESS'),
(28,'voja@gmail.com','makva@gmail.com','30/07/2025','12:00','12:30','REJECTED'),
(29,'simpri@gmail.com','makva@gmail.com','30/07/2025','13:30','14:00','REJECTED'),
(30,'sarenac.pavle@gmail.com','makva@gmail.com','26/07/2025','08:00','08:30','DONE_SUCCESS'),
(31,'ljubmajstorovic9@gmail.com','makva@gmail.com','26/07/2025','08:30','09:00','DONE_SUCCESS'),
(32,'posta.nikolan@gmail.com','makva@gmail.com','26/07/2025','09:00','09:30','DONE_SUCCESS'),
(33,'luka@gmail.com','makva@gmail.com','26/07/2025','09:30','10:00','DONE_SUCCESS'),
(34,'vlajko@gmail.com','makva@gmail.com','26/07/2025','10:00','10:30','DONE_SUCCESS'),
(35,'dovla@gmail.com','makva@gmail.com','26/07/2025','10:30','11:00','DONE_SUCCESS'),
(36,'saki@gmail.com','makva@gmail.com','26/07/2025','11:00','11:30','DONE_SUCCESS'),
(37,'mateja@gmail.com','makva@gmail.com','01/08/2025','11:30','12:00','DONE_FAILURE'),
(38,'voja@gmail.com','makva@gmail.com','02/08/2025','00:00','00:30','DONE_FAILURE'),
(39,'simpri@gmail.com','makva@gmail.com','01/08/2025','23:30','00:00','DONE_FAILURE'),
(40,'sarenac.pavle@gmail.com','cutandgo@gmail.com','26/07/2023','08:00','08:30','DONE_SUCCESS'),
(41,'ljubmajstorovic9@gmail.com','cutandgo@gmail.com','26/07/2023','08:30','09:00','DONE_SUCCESS'),
(42,'posta.nikolan@gmail.com','cutandgo@gmail.com','26/07/2023','09:00','09:30','DONE_SUCCESS'),
(43,'luka@gmail.com','cutandgo@gmail.com','26/07/2023','09:30','10:00','DONE_SUCCESS'),
(44,'vlajko@gmail.com','cutandgo@gmail.com','26/07/2023','10:00','10:30','DONE_SUCCESS'),
(45,'dovla@gmail.com','cutandgo@gmail.com','26/07/2023','10:30','11:00','DONE_SUCCESS'),
(46,'saki@gmail.com','cutandgo@gmail.com','26/07/2023','11:00','11:30','DONE_SUCCESS'),
(47,'mateja@gmail.com','cutandgo@gmail.com','26/07/2023','11:30','12:00','DONE_SUCCESS'),
(48,'voja@gmail.com','cutandgo@gmail.com','26/07/2023','12:00','12:30','DONE_SUCCESS'),
(49,'simpri@gmail.com','cutandgo@gmail.com','26/07/2023','13:30','14:00','DONE_SUCCESS'),
(50,'sarenac.pavle@gmail.com','brotherhood@gmail.com','26/07/2023','08:00','08:30','DONE_SUCCESS'),
(51,'ljubmajstorovic9@gmail.com','brotherhood@gmail.com','26/07/2023','08:30','09:00','DONE_SUCCESS'),
(52,'posta.nikolan@gmail.com','brotherhood@gmail.com','26/07/2023','09:00','09:30','DONE_SUCCESS'),
(53,'luka@gmail.com','brotherhood@gmail.com','26/07/2023','09:30','10:00','DONE_SUCCESS'),
(54,'vlajko@gmail.com','brotherhood@gmail.com','26/07/2023','10:00','10:30','DONE_SUCCESS'),
(55,'dovla@gmail.com','brotherhood@gmail.com','26/07/2023','10:30','11:00','DONE_SUCCESS'),
(56,'saki@gmail.com','brotherhood@gmail.com','26/07/2023','11:00','11:30','DONE_SUCCESS'),
(57,'mateja@gmail.com','brotherhood@gmail.com','26/07/2023','11:30','12:00','DONE_SUCCESS'),
(58,'voja@gmail.com','brotherhood@gmail.com','26/07/2023','12:00','12:30','DONE_SUCCESS'),
(59,'simpri@gmail.com','brotherhood@gmail.com','26/07/2023','13:30','14:00','DONE_SUCCESS'),
(60,'sarenac.pavle@gmail.com','ostro@gmail.com','26/07/2023','08:00','08:30','DONE_SUCCESS'),
(61,'ljubmajstorovic9@gmail.com','ostro@gmail.com','26/07/2023','08:30','09:00','DONE_SUCCESS'),
(62,'posta.nikolan@gmail.com','ostro@gmail.com','26/07/2023','09:00','09:30','DONE_SUCCESS'),
(63,'luka@gmail.com','ostro@gmail.com','26/07/2023','09:30','10:00','DONE_SUCCESS'),
(64,'vlajko@gmail.com','ostro@gmail.com','26/07/2023','10:00','10:30','DONE_SUCCESS'),
(65,'dovla@gmail.com','ostro@gmail.com','26/07/2023','10:30','11:00','DONE_SUCCESS'),
(66,'saki@gmail.com','ostro@gmail.com','26/07/2023','11:00','11:30','DONE_SUCCESS'),
(67,'mateja@gmail.com','ostro@gmail.com','26/07/2023','11:30','12:00','DONE_SUCCESS'),
(68,'voja@gmail.com','ostro@gmail.com','26/07/2023','12:00','12:30','DONE_SUCCESS'),
(69,'simpri@gmail.com','ostro@gmail.com','26/07/2023','13:30','14:00','DONE_SUCCESS'),
(70,'sarenac.pavle@gmail.com','benvenuto@gmail.com','26/07/2023','08:00','08:30','DONE_SUCCESS'),
(71,'ljubmajstorovic9@gmail.com','benvenuto@gmail.com','26/07/2023','08:30','09:00','DONE_SUCCESS'),
(72,'posta.nikolan@gmail.com','benvenuto@gmail.com','26/07/2023','09:00','09:30','DONE_SUCCESS'),
(73,'luka@gmail.com','benvenuto@gmail.com','26/07/2023','09:30','10:00','DONE_SUCCESS'),
(74,'vlajko@gmail.com','benvenuto@gmail.com','26/07/2023','10:00','10:30','DONE_SUCCESS'),
(75,'dovla@gmail.com','benvenuto@gmail.com','26/07/2023','10:30','11:00','DONE_SUCCESS'),
(76,'saki@gmail.com','benvenuto@gmail.com','26/07/2023','11:00','11:30','DONE_SUCCESS'),
(77,'mateja@gmail.com','benvenuto@gmail.com','26/07/2023','11:30','12:00','DONE_SUCCESS'),
(78,'voja@gmail.com','benvenuto@gmail.com','26/07/2023','12:00','12:30','DONE_SUCCESS'),
(79,'simpri@gmail.com','benvenuto@gmail.com','26/07/2023','13:30','14:00','DONE_SUCCESS'),
(80,'sarenac.pavle@gmail.com','erato@gmail.com','26/07/2023','08:00','08:30','DONE_SUCCESS'),
(81,'ljubmajstorovic9@gmail.com','erato@gmail.com','26/07/2023','08:30','09:00','DONE_SUCCESS'),
(82,'posta.nikolan@gmail.com','erato@gmail.com','26/07/2023','09:00','09:30','DONE_SUCCESS'),
(83,'luka@gmail.com','erato@gmail.com','26/07/2023','09:30','10:00','DONE_SUCCESS'),
(84,'vlajko@gmail.com','erato@gmail.com','26/07/2023','10:00','10:30','DONE_SUCCESS'),
(85,'dovla@gmail.com','erato@gmail.com','26/07/2023','10:30','11:00','DONE_SUCCESS'),
(86,'saki@gmail.com','erato@gmail.com','26/07/2023','11:00','11:30','DONE_SUCCESS'),
(87,'mateja@gmail.com','erato@gmail.com','26/07/2023','11:30','12:00','DONE_SUCCESS'),
(88,'voja@gmail.com','erato@gmail.com','26/07/2023','12:00','12:30','DONE_SUCCESS'),
(89,'simpri@gmail.com','erato@gmail.com','26/07/2023','13:30','14:00','DONE_SUCCESS'),
(90,'sarenac.pavle@gmail.com','bogdan@gmail.com','26/07/2023','08:00','08:30','DONE_SUCCESS'),
(91,'ljubmajstorovic9@gmail.com','bogdan@gmail.com','26/07/2023','08:30','09:00','DONE_SUCCESS'),
(92,'posta.nikolan@gmail.com','bogdan@gmail.com','26/07/2023','09:00','09:30','DONE_SUCCESS'),
(93,'luka@gmail.com','bogdan@gmail.com','26/07/2023','09:30','10:00','DONE_SUCCESS'),
(94,'vlajko@gmail.com','bogdan@gmail.com','26/07/2023','10:00','10:30','DONE_SUCCESS'),
(95,'dovla@gmail.com','bogdan@gmail.com','26/07/2023','10:30','11:00','DONE_SUCCESS'),
(96,'saki@gmail.com','bogdan@gmail.com','26/07/2023','11:00','11:30','DONE_SUCCESS'),
(97,'mateja@gmail.com','bogdan@gmail.com','26/07/2023','11:30','12:00','DONE_SUCCESS'),
(98,'voja@gmail.com','bogdan@gmail.com','26/07/2023','12:00','12:30','DONE_SUCCESS'),
(99,'simpri@gmail.com','bogdan@gmail.com','26/07/2023','13:30','14:00','DONE_SUCCESS'),
(100,'sarenac.pavle@gmail.com','makva@gmail.com','26/07/2023','08:00','08:30','DONE_SUCCESS'),
(101,'ljubmajstorovic9@gmail.com','makva@gmail.com','26/07/2023','08:30','09:00','DONE_SUCCESS'),
(102,'posta.nikolan@gmail.com','makva@gmail.com','26/07/2023','09:00','09:30','DONE_SUCCESS'),
(103,'luka@gmail.com','makva@gmail.com','26/07/2023','09:30','10:00','DONE_SUCCESS'),
(104,'vlajko@gmail.com','makva@gmail.com','26/07/2023','10:00','10:30','DONE_SUCCESS'),
(105,'dovla@gmail.com','makva@gmail.com','26/07/2023','10:30','11:00','DONE_SUCCESS'),
(106,'saki@gmail.com','makva@gmail.com','26/07/2023','11:00','11:30','DONE_SUCCESS'),
(107,'mateja@gmail.com','makva@gmail.com','26/07/2023','11:30','12:00','DONE_SUCCESS'),
(108,'voja@gmail.com','makva@gmail.com','26/07/2023','12:00','12:30','DONE_SUCCESS'),
(109,'simpri@gmail.com','makva@gmail.com','26/07/2023','13:30','14:00','DONE_SUCCESS'),
(110,'sarenac.pavle@gmail.com','brotherhood@gmail.com','26/08/2023','08:00','08:30','DONE_SUCCESS'),
(111,'ljubmajstorovic9@gmail.com','brotherhood@gmail.com','26/08/2023','08:30','09:00','DONE_SUCCESS'),
(112,'posta.nikolan@gmail.com','brotherhood@gmail.com','26/08/2023','09:00','09:30','DONE_SUCCESS'),
(113,'luka@gmail.com','brotherhood@gmail.com','26/08/2023','09:30','10:00','DONE_SUCCESS'),
(114,'vlajko@gmail.com','brotherhood@gmail.com','26/08/2023','10:00','10:30','DONE_SUCCESS'),
(115,'dovla@gmail.com','brotherhood@gmail.com','26/08/2023','10:30','11:00','DONE_SUCCESS'),
(116,'saki@gmail.com','brotherhood@gmail.com','26/08/2023','11:00','11:30','DONE_SUCCESS'),
(117,'mateja@gmail.com','brotherhood@gmail.com','26/08/2023','11:30','12:00','DONE_SUCCESS'),
(118,'voja@gmail.com','brotherhood@gmail.com','26/08/2023','12:00','12:30','DONE_SUCCESS'),
(119,'simpri@gmail.com','brotherhood@gmail.com','26/08/2023','13:30','14:00','DONE_SUCCESS'),
(120,'sarenac.pavle@gmail.com','ostro@gmail.com','26/08/2023','08:00','08:30','DONE_SUCCESS'),
(121,'ljubmajstorovic9@gmail.com','ostro@gmail.com','26/08/2023','08:30','09:00','DONE_SUCCESS'),
(122,'posta.nikolan@gmail.com','ostro@gmail.com','26/08/2023','09:00','09:30','DONE_SUCCESS'),
(123,'luka@gmail.com','ostro@gmail.com','26/08/2023','09:30','10:00','DONE_SUCCESS'),
(124,'vlajko@gmail.com','ostro@gmail.com','26/08/2023','10:00','10:30','DONE_SUCCESS'),
(125,'dovla@gmail.com','ostro@gmail.com','26/08/2023','10:30','11:00','DONE_SUCCESS'),
(126,'saki@gmail.com','ostro@gmail.com','26/08/2023','11:00','11:30','DONE_SUCCESS'),
(127,'mateja@gmail.com','ostro@gmail.com','26/08/2023','11:30','12:00','DONE_SUCCESS'),
(128,'voja@gmail.com','ostro@gmail.com','26/08/2023','12:00','12:30','DONE_SUCCESS'),
(129,'simpri@gmail.com','ostro@gmail.com','26/08/2023','13:30','14:00','DONE_SUCCESS'),
(130,'sarenac.pavle@gmail.com','benvenuto@gmail.com','26/08/2023','08:00','08:30','DONE_SUCCESS'),
(131,'ljubmajstorovic9@gmail.com','benvenuto@gmail.com','26/08/2023','08:30','09:00','DONE_SUCCESS'),
(132,'posta.nikolan@gmail.com','benvenuto@gmail.com','26/08/2023','09:00','09:30','DONE_SUCCESS'),
(133,'luka@gmail.com','benvenuto@gmail.com','26/08/2023','09:30','10:00','DONE_SUCCESS'),
(134,'vlajko@gmail.com','benvenuto@gmail.com','26/08/2023','10:00','10:30','DONE_SUCCESS'),
(135,'dovla@gmail.com','benvenuto@gmail.com','26/08/2023','10:30','11:00','DONE_SUCCESS'),
(136,'saki@gmail.com','benvenuto@gmail.com','26/08/2023','11:00','11:30','DONE_SUCCESS'),
(137,'mateja@gmail.com','benvenuto@gmail.com','26/08/2023','11:30','12:00','DONE_SUCCESS'),
(138,'voja@gmail.com','benvenuto@gmail.com','26/08/2023','12:00','12:30','DONE_SUCCESS'),
(139,'simpri@gmail.com','benvenuto@gmail.com','26/08/2023','13:30','14:00','DONE_SUCCESS'),
(140,'sarenac.pavle@gmail.com','erato@gmail.com','26/08/2023','08:00','08:30','DONE_SUCCESS'),
(141,'ljubmajstorovic9@gmail.com','erato@gmail.com','26/08/2023','08:30','09:00','DONE_SUCCESS'),
(142,'posta.nikolan@gmail.com','erato@gmail.com','26/08/2023','09:00','09:30','DONE_SUCCESS'),
(143,'luka@gmail.com','erato@gmail.com','26/08/2023','09:30','10:00','DONE_SUCCESS'),
(144,'vlajko@gmail.com','erato@gmail.com','26/08/2023','10:00','10:30','DONE_SUCCESS'),
(145,'dovla@gmail.com','erato@gmail.com','26/08/2023','10:30','11:00','DONE_SUCCESS'),
(146,'saki@gmail.com','erato@gmail.com','26/08/2023','11:00','11:30','DONE_SUCCESS'),
(147,'mateja@gmail.com','erato@gmail.com','26/08/2023','11:30','12:00','DONE_SUCCESS'),
(148,'voja@gmail.com','erato@gmail.com','26/08/2023','12:00','12:30','DONE_SUCCESS'),
(149,'simpri@gmail.com','erato@gmail.com','26/08/2023','13:30','14:00','DONE_SUCCESS'),
(150,'sarenac.pavle@gmail.com','bogdan@gmail.com','26/08/2023','08:00','08:30','DONE_SUCCESS'),
(151,'ljubmajstorovic9@gmail.com','bogdan@gmail.com','26/08/2023','08:30','09:00','DONE_SUCCESS'),
(152,'posta.nikolan@gmail.com','bogdan@gmail.com','26/08/2023','09:00','09:30','DONE_SUCCESS'),
(153,'luka@gmail.com','bogdan@gmail.com','26/08/2023','09:30','10:00','DONE_SUCCESS'),
(154,'vlajko@gmail.com','bogdan@gmail.com','26/08/2023','10:00','10:30','DONE_SUCCESS'),
(155,'dovla@gmail.com','bogdan@gmail.com','26/08/2023','10:30','11:00','DONE_SUCCESS'),
(156,'saki@gmail.com','bogdan@gmail.com','26/08/2023','11:00','11:30','DONE_SUCCESS'),
(157,'mateja@gmail.com','bogdan@gmail.com','26/08/2023','11:30','12:00','DONE_SUCCESS'),
(158,'voja@gmail.com','bogdan@gmail.com','26/08/2023','12:00','12:30','DONE_SUCCESS'),
(159,'simpri@gmail.com','bogdan@gmail.com','26/08/2023','13:30','14:00','DONE_SUCCESS'),
(160,'sarenac.pavle@gmail.com','makva@gmail.com','26/08/2023','08:00','08:30','DONE_SUCCESS'),
(161,'ljubmajstorovic9@gmail.com','makva@gmail.com','26/08/2023','08:30','09:00','DONE_SUCCESS'),
(162,'posta.nikolan@gmail.com','makva@gmail.com','26/08/2023','09:00','09:30','DONE_SUCCESS'),
(163,'luka@gmail.com','makva@gmail.com','26/08/2023','09:30','10:00','DONE_SUCCESS'),
(164,'vlajko@gmail.com','makva@gmail.com','26/08/2023','10:00','10:30','DONE_SUCCESS'),
(165,'dovla@gmail.com','makva@gmail.com','26/08/2023','10:30','11:00','DONE_SUCCESS'),
(166,'saki@gmail.com','makva@gmail.com','26/08/2023','11:00','11:30','DONE_SUCCESS'),
(167,'mateja@gmail.com','makva@gmail.com','26/08/2023','11:30','12:00','DONE_SUCCESS'),
(168,'voja@gmail.com','makva@gmail.com','26/08/2023','12:00','12:30','DONE_SUCCESS'),
(169,'simpri@gmail.com','makva@gmail.com','26/08/2023','13:30','14:00','DONE_SUCCESS'),
(170,'sarenac.pavle@gmail.com','cutandgo@gmail.com','29/07/2024','08:00','08:30','DONE_SUCCESS'),
(171,'ljubmajstorovic9@gmail.com','cutandgo@gmail.com','29/07/2024','08:30','09:00','DONE_SUCCESS'),
(172,'posta.nikolan@gmail.com','cutandgo@gmail.com','29/07/2024','09:00','09:30','DONE_SUCCESS'),
(173,'luka@gmail.com','cutandgo@gmail.com','29/07/2024','09:30','10:00','DONE_SUCCESS'),
(174,'vlajko@gmail.com','cutandgo@gmail.com','29/07/2024','10:00','10:30','DONE_SUCCESS'),
(175,'dovla@gmail.com','cutandgo@gmail.com','29/07/2024','10:30','11:00','DONE_SUCCESS'),
(176,'saki@gmail.com','cutandgo@gmail.com','29/07/2024','11:00','11:30','DONE_SUCCESS'),
(177,'mateja@gmail.com','cutandgo@gmail.com','29/07/2024','11:30','12:00','DONE_SUCCESS'),
(178,'voja@gmail.com','cutandgo@gmail.com','29/07/2024','12:00','12:30','DONE_SUCCESS'),
(179,'simpri@gmail.com','cutandgo@gmail.com','29/07/2024','13:30','14:00','DONE_SUCCESS'),
(180,'sarenac.pavle@gmail.com','benvenuto@gmail.com','29/07/2024','08:00','08:30','DONE_SUCCESS'),
(181,'ljubmajstorovic9@gmail.com','benvenuto@gmail.com','29/07/2024','08:30','09:00','DONE_SUCCESS'),
(182,'posta.nikolan@gmail.com','benvenuto@gmail.com','29/07/2024','09:00','09:30','DONE_SUCCESS'),
(183,'luka@gmail.com','benvenuto@gmail.com','29/07/2024','09:30','10:00','DONE_SUCCESS'),
(184,'vlajko@gmail.com','benvenuto@gmail.com','29/07/2024','10:00','10:30','DONE_SUCCESS'),
(185,'dovla@gmail.com','benvenuto@gmail.com','29/07/2024','10:30','11:00','DONE_SUCCESS'),
(186,'saki@gmail.com','benvenuto@gmail.com','29/07/2024','11:00','11:30','DONE_SUCCESS'),
(187,'mateja@gmail.com','benvenuto@gmail.com','29/07/2024','11:30','12:00','DONE_SUCCESS'),
(188,'voja@gmail.com','benvenuto@gmail.com','29/07/2024','12:00','12:30','DONE_SUCCESS'),
(189,'simpri@gmail.com','benvenuto@gmail.com','29/07/2024','13:30','14:00','DONE_SUCCESS'),
(190,'sarenac.pavle@gmail.com','brotherhood@gmail.com','29/07/2024','08:00','08:30','DONE_SUCCESS'),
(191,'ljubmajstorovic9@gmail.com','brotherhood@gmail.com','29/07/2024','08:30','09:00','DONE_SUCCESS'),
(192,'posta.nikolan@gmail.com','brotherhood@gmail.com','29/07/2024','09:00','09:30','DONE_SUCCESS'),
(193,'luka@gmail.com','brotherhood@gmail.com','29/07/2024','09:30','10:00','DONE_SUCCESS'),
(194,'vlajko@gmail.com','brotherhood@gmail.com','29/07/2024','10:00','10:30','DONE_SUCCESS'),
(195,'dovla@gmail.com','brotherhood@gmail.com','29/07/2024','10:30','11:00','DONE_SUCCESS'),
(196,'saki@gmail.com','brotherhood@gmail.com','29/07/2024','11:00','11:30','DONE_SUCCESS');
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` int NOT NULL AUTO_INCREMENT,
  `clientEmail` varchar(45) NOT NULL,
  `barberEmail` varchar(45) NOT NULL,
  `grade` int NOT NULL,
  `text` varchar(500) NOT NULL,
  `date` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (3,'ljubmajstorovic9@gmail.com','makva@gmail.com',5,'A truly amazing barbershop! Everyone is so nice and welcoming, and I am more than satisfied with my haircut. You must come here!','29/07/2024'),(4,'posta.nikolan@gmail.com','makva@gmail.com',3,'It was good, but not amazing. There are better barbershops around.','29/07/2024'),(5,'luka@gmail.com','makva@gmail.com',4,'Good.','29/07/2024'),(6,'vlajko@gmail.com','makva@gmail.com',5,'Great!','29/07/2024'),(7,'dovla@gmail.com','makva@gmail.com',5,'','29/07/2024'),(8,'saki@gmail.com','makva@gmail.com',5,'','29/07/2024'),(9,'mateja@gmail.com','makva@gmail.com',5,'The barber is really friendly.','29/07/2024'),(10,'voja@gmail.com','makva@gmail.com',5,'The atmosphere is warm and welcoming.','29/07/2024'),(11,'simpri@gmail.com','makva@gmail.com',5,'Good experience.','29/07/2024'),(13,'ljubmajstorovic9@gmail.com','cutandgo@gmail.com',5,'A truly amazing barbershop! Everyone is so nice and welcoming, and I am more than satisfied with my haircut. You must come here!','29/07/2024'),(14,'posta.nikolan@gmail.com','cutandgo@gmail.com',3,'It was good, but not amazing. There are better barbershops around.','29/07/2024'),(15,'luka@gmail.com','cutandgo@gmail.com',3,'Good.','29/07/2024'),(16,'vlajko@gmail.com','cutandgo@gmail.com',2,'Great!','29/07/2024'),(17,'dovla@gmail.com','cutandgo@gmail.com',3,'','29/07/2024'),(18,'saki@gmail.com','cutandgo@gmail.com',2,'','29/07/2024'),(19,'mateja@gmail.com','cutandgo@gmail.com',2,'The barber is really friendly.','29/07/2024'),(20,'voja@gmail.com','cutandgo@gmail.com',4,'The atmosphere is warm and welcoming.','29/07/2024'),(21,'simpri@gmail.com','cutandgo@gmail.com',1,'Good experience.','29/07/2024'),(22,'sarenac.pavle@gmail.com','cutandgo@gmail.com',2,'','29/07/2024'),(23,'ljubmajstorovic9@gmail.com','brotherhood@gmail.com',5,'A truly amazing barbershop! Everyone is so nice and welcoming, and I am more than satisfied with my haircut. You must come here!','29/07/2024'),(24,'posta.nikolan@gmail.com','brotherhood@gmail.com',5,'It was good, but not amazing. There are better barbershops around.','29/07/2024'),(25,'luka@gmail.com','brotherhood@gmail.com',5,'Good.','29/07/2024'),(26,'vlajko@gmail.com','brotherhood@gmail.com',5,'Great!','29/07/2024'),(27,'dovla@gmail.com','brotherhood@gmail.com',5,'','29/07/2024'),(28,'saki@gmail.com','brotherhood@gmail.com',5,'','29/07/2024'),(29,'mateja@gmail.com','brotherhood@gmail.com',5,'The barber is really friendly.','29/07/2024'),(30,'voja@gmail.com','brotherhood@gmail.com',5,'The atmosphere is warm and welcoming.','29/07/2024'),(31,'simpri@gmail.com','brotherhood@gmail.com',5,'Good experience.','29/07/2024'),(32,'sarenac.pavle@gmail.com','brotherhood@gmail.com',5,'','29/07/2024'),(33,'ljubmajstorovic9@gmail.com','ostro@gmail.com',5,'A truly amazing barbershop! Everyone is so nice and welcoming, and I am more than satisfied with my haircut. You must come here!','29/07/2024'),(34,'posta.nikolan@gmail.com','ostro@gmail.com',4,'It was good, but not amazing. There are better barbershops around.','29/07/2024'),(35,'luka@gmail.com','ostro@gmail.com',4,'Good.','29/07/2024'),(36,'vlajko@gmail.com','ostro@gmail.com',4,'Great!','29/07/2024'),(37,'dovla@gmail.com','ostro@gmail.com',4,'','29/07/2024'),(38,'saki@gmail.com','ostro@gmail.com',4,'','29/07/2024'),(39,'mateja@gmail.com','ostro@gmail.com',4,'The barber is really friendly.','29/07/2024'),(40,'voja@gmail.com','ostro@gmail.com',4,'The atmosphere is warm and welcoming.','29/07/2024'),(41,'simpri@gmail.com','ostro@gmail.com',4,'Good experience.','29/07/2024'),(42,'sarenac.pavle@gmail.com','ostro@gmail.com',4,'','29/07/2024'),(43,'ljubmajstorovic9@gmail.com','benvenuto@gmail.com',5,'A truly amazing barbershop! Everyone is so nice and welcoming, and I am more than satisfied with my haircut. You must come here!','29/07/2024'),(44,'posta.nikolan@gmail.com','benvenuto@gmail.com',3,'It was good, but not amazing. There are better barbershops around.','29/07/2024'),(45,'luka@gmail.com','benvenuto@gmail.com',3,'Good.','29/07/2024'),(46,'vlajko@gmail.com','benvenuto@gmail.com',3,'Great!','29/07/2024'),(47,'dovla@gmail.com','benvenuto@gmail.com',3,'','29/07/2024'),(48,'saki@gmail.com','benvenuto@gmail.com',3,'','29/07/2024'),(49,'mateja@gmail.com','benvenuto@gmail.com',3,'The barber is really friendly.','29/07/2024'),(50,'voja@gmail.com','benvenuto@gmail.com',3,'The atmosphere is warm and welcoming.','29/07/2024'),(51,'simpri@gmail.com','benvenuto@gmail.com',3,'Good experience.','29/07/2024'),(52,'sarenac.pavle@gmail.com','benvenuto@gmail.com',3,'','29/07/2024'),(53,'ljubmajstorovic9@gmail.com','erato@gmail.com',5,'A truly amazing barbershop! Everyone is so nice and welcoming, and I am more than satisfied with my haircut. You must come here!','29/07/2024'),(54,'posta.nikolan@gmail.com','erato@gmail.com',2,'It was good, but not amazing. There are better barbershops around.','29/07/2024'),(55,'luka@gmail.com','erato@gmail.com',2,'Good.','29/07/2024'),(56,'vlajko@gmail.com','erato@gmail.com',2,'Great!','29/07/2024'),(57,'dovla@gmail.com','erato@gmail.com',2,'','29/07/2024'),(58,'saki@gmail.com','erato@gmail.com',2,'','29/07/2024'),(59,'mateja@gmail.com','erato@gmail.com',2,'The barber is really friendly.','29/07/2024'),(60,'voja@gmail.com','erato@gmail.com',2,'The atmosphere is warm and welcoming.','29/07/2024'),(61,'simpri@gmail.com','erato@gmail.com',2,'Good experience.','29/07/2024'),(62,'sarenac.pavle@gmail.com','erato@gmail.com',2,'','29/07/2024'),(63,'ljubmajstorovic9@gmail.com','bogdan@gmail.com',5,'A truly amazing barbershop! Everyone is so nice and welcoming, and I am more than satisfied with my haircut. You must come here!','29/07/2024'),(64,'posta.nikolan@gmail.com','bogdan@gmail.com',1,'It was good, but not amazing. There are better barbershops around.','29/07/2024'),(65,'luka@gmail.com','bogdan@gmail.com',1,'Good.','29/07/2024'),(66,'vlajko@gmail.com','bogdan@gmail.com',1,'Great!','29/07/2024'),(67,'dovla@gmail.com','bogdan@gmail.com',1,'','29/07/2024'),(68,'saki@gmail.com','bogdan@gmail.com',1,'','29/07/2024'),(69,'mateja@gmail.com','bogdan@gmail.com',1,'The barber is really friendly.','29/07/2024'),(70,'voja@gmail.com','bogdan@gmail.com',1,'The atmosphere is warm and welcoming.','29/07/2024'),(71,'simpri@gmail.com','bogdan@gmail.com',1,'Good experience.','29/07/2024'),(72,'sarenac.pavle@gmail.com','bogdan@gmail.com',1,'','29/07/2024'),(73,'sarenac.pavle@gmail.com','claire@gmail.com',4,'Recenzija','31/07/2024'),(85,'sarenac.pavle@gmail.com','makva@gmail.com',5,'','15/08/2024');
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jwtRefreshTokens`
--

DROP TABLE IF EXISTS `jwtrefreshtoken`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jwtrefreshtoken` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `tokenHash` CHAR(64) NOT NULL,
    `audience` VARCHAR(100) NOT NULL,
    `issuer` VARCHAR(100) NOT NULL,
    `subject` VARCHAR(100) NOT NULL,
    `userType` VARCHAR(100) NOT NULL,
    `issuedAt` DATETIME NOT NULL,
    `expiresAt` DATETIME NOT NULL,
    `isRevoked` BOOLEAN NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY (`tokenHash`),
    INDEX `idx_subject` (`subject`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-08-15 20:09:59