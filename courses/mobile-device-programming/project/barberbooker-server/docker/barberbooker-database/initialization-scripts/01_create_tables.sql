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