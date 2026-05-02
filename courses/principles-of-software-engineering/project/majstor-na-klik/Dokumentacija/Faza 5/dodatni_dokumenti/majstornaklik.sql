-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Jun 05, 2023 at 02:46 PM
-- Server version: 8.0.31
-- PHP Version: 8.0.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `majstornaklik`
--
CREATE DATABASE IF NOT EXISTS `majstornaklik` DEFAULT CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci;
USE `majstornaklik`;

-- --------------------------------------------------------

--
-- Table structure for table `grad`
--

DROP TABLE IF EXISTS `grad`;
CREATE TABLE IF NOT EXISTS `grad` (
  `IdGra` int NOT NULL AUTO_INCREMENT,
  `Naziv` varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  PRIMARY KEY (`IdGra`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Dumping data for table `grad`
--

INSERT INTO `grad` (`IdGra`, `Naziv`) VALUES
(9, 'Beograd'),
(10, 'Bor'),
(11, 'Valjevo'),
(12, 'Vranje'),
(13, 'Vršac'),
(14, 'Zaječar'),
(15, 'Zrenjanin'),
(16, 'Jagodina'),
(17, 'Kikinda'),
(18, 'Kragujevac'),
(19, 'Kraljevo'),
(20, 'Kruševac'),
(21, 'Leskovac'),
(22, 'Loznica'),
(23, 'Niš'),
(24, 'Novi Pazar'),
(25, 'Novi Sad'),
(26, 'Pančevo'),
(27, 'Pirot'),
(28, 'Požarevac'),
(29, 'Priština'),
(30, 'Prokuplje'),
(31, 'Smederevo'),
(32, 'Sombor'),
(33, 'Sremska Mitrovica'),
(34, 'Subotica'),
(35, 'Užice'),
(36, 'Čačak'),
(37, 'Šabac');

-- --------------------------------------------------------

--
-- Table structure for table `majstor`
--

DROP TABLE IF EXISTS `majstor`;
CREATE TABLE IF NOT EXISTS `majstor` (
  `IdMaj` int NOT NULL,
  `BrojRecenzija` int DEFAULT NULL,
  `ProsecnaCena` decimal(10,2) DEFAULT NULL,
  `ProsecnaBrzina` decimal(10,2) DEFAULT NULL,
  `ProsecanKvalitet` decimal(10,2) DEFAULT NULL,
  `IdSpec` int NOT NULL,
  PRIMARY KEY (`IdMaj`),
  KEY `IdSpec` (`IdSpec`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Dumping data for table `majstor`
--

INSERT INTO `majstor` (`IdMaj`, `BrojRecenzija`, `ProsecnaCena`, `ProsecnaBrzina`, `ProsecanKvalitet`, `IdSpec`) VALUES
(26, 4, '3.75', '3.75', '3.50', 67),
(27, 3, '3.33', '3.67', '3.33', 90),
(28, 5, '4.60', '3.80', '4.40', 73),
(29, 13, '4.70', '4.46', '4.62', 30),
(30, 4, '4.25', '3.50', '2.75', 81),
(31, 3, '2.33', '1.67', '1.67', 70),
(32, 4, '3.00', '3.75', '3.25', 21),
(33, 4, '3.25', '4.00', '3.25', 57),
(34, 4, '3.00', '4.00', '3.50', 82),
(35, 3, '5.00', '4.67', '5.00', 33),
(36, 4, '3.25', '3.25', '3.00', 17),
(37, 3, '3.33', '2.00', '3.00', 58),
(38, 3, '2.33', '2.33', '2.67', 52),
(39, 1, '3.00', '2.00', '2.00', 29),
(40, 1, '5.00', '5.00', '5.00', 53),
(41, NULL, NULL, NULL, NULL, 81),
(42, NULL, NULL, NULL, NULL, 73),
(49, NULL, NULL, NULL, NULL, 29);

-- --------------------------------------------------------

--
-- Table structure for table `poruka`
--

DROP TABLE IF EXISTS `poruka`;
CREATE TABLE IF NOT EXISTS `poruka` (
  `IdP` int NOT NULL AUTO_INCREMENT,
  `Tekst` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `DatumVreme` datetime NOT NULL,
  `IdPos` int NOT NULL,
  `IdPri` int NOT NULL,
  `Status` int NOT NULL,
  PRIMARY KEY (`IdP`),
  KEY `IdMaj` (`IdPos`,`IdPri`),
  KEY `IdKli` (`IdPri`)
) ENGINE=InnoDB AUTO_INCREMENT=116 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Dumping data for table `poruka`
--

INSERT INTO `poruka` (`IdP`, `Tekst`, `DatumVreme`, `IdPos`, `IdPri`, `Status`) VALUES
(1, 'desi dulou', '2023-06-04 19:48:45', 32, 29, 2),
(2, 'e evo buraz', '2023-06-04 19:51:39', 29, 32, 2),
(3, 'desi boro', '2023-06-04 20:11:18', 57, 26, 2),
(4, 'sta ima, mozes li mi opraviti sporet?', '2023-06-05 00:47:11', 32, 29, 2),
(5, 'mogu samo mi reci kad ti odgovara?', '2023-06-05 00:57:20', 29, 32, 2),
(6, 'poruka', '2023-06-05 01:01:01', 47, 29, 2),
(7, 'poruka', '2023-06-05 01:01:03', 47, 29, 2),
(8, 'poruka', '2023-06-05 01:01:06', 47, 29, 2),
(9, 'poruka1', '2023-06-05 01:01:25', 43, 29, 2),
(10, 'poruka1', '2023-06-05 01:01:27', 43, 29, 2),
(11, 'poruka1', '2023-06-05 01:01:29', 43, 29, 2),
(12, 'poruka2', '2023-06-05 01:01:51', 46, 29, 2),
(13, 'poruka2', '2023-06-05 01:01:52', 46, 29, 2),
(14, 'poruka2', '2023-06-05 01:01:54', 46, 29, 2),
(15, 'poruka3', '2023-06-05 01:03:06', 45, 29, 2),
(16, 'poruka3', '2023-06-05 01:03:08', 45, 29, 2),
(17, 'poruka3', '2023-06-05 01:03:10', 45, 29, 2),
(18, 'desi fico', '2023-06-05 01:04:38', 29, 43, 0),
(19, 'proba', '2023-06-05 01:11:31', 29, 32, 2),
(20, 'proba', '2023-06-05 01:14:20', 29, 47, 2),
(21, 'proba', '2023-06-05 03:14:23', 29, 43, 0),
(22, 'de si juda', '2023-06-05 09:27:41', 47, 32, 2),
(23, 'sta se radi', '2023-06-05 09:27:44', 47, 32, 2),
(24, 'jel ide spremanje ispita', '2023-06-05 09:27:47', 47, 32, 2),
(25, 'jel sve ok', '2023-06-05 09:27:51', 47, 32, 2),
(26, 'cao judaa', '2023-06-05 09:28:10', 29, 32, 2),
(27, 'ako se ne uozbiljis izbacujem te iz sistema', '2023-06-05 09:28:38', 16, 32, 2),
(28, 'ee', '2023-06-05 09:29:08', 41, 32, 2),
(29, 'ee', '2023-06-05 09:29:32', 37, 32, 2),
(30, 'ee', '2023-06-05 09:29:47', 35, 32, 2),
(31, 'eee', '2023-06-05 09:30:06', 36, 32, 2),
(32, 'ee', '2023-06-05 09:33:50', 32, 35, 0),
(33, 'vazi', '2023-06-05 09:38:14', 32, 16, 2),
(34, 'afkasofas', '2023-06-05 09:41:09', 16, 29, 2),
(35, 'fas', '2023-06-05 09:41:10', 16, 29, 2),
(36, 'as', '2023-06-05 09:41:11', 16, 29, 2),
(37, 'asf', '2023-06-05 09:41:11', 16, 29, 2),
(38, 'asf', '2023-06-05 09:41:12', 16, 29, 2),
(39, 'asf', '2023-06-05 09:41:13', 16, 29, 2),
(40, 's', '2023-06-05 09:41:13', 16, 29, 2),
(41, 'fa', '2023-06-05 09:41:14', 16, 29, 2),
(42, 'fa', '2023-06-05 09:41:15', 16, 29, 2),
(43, 'hgasfhasfjkhsafjhksfjak', '2023-06-05 09:41:35', 47, 29, 2),
(44, 'hgksafhkgasfhghgasf', '2023-06-05 09:41:36', 47, 29, 2),
(45, 'hhasfghasfhgsafhj', '2023-06-05 09:41:37', 47, 29, 2),
(46, 'asfhhkgsafhgksa', '2023-06-05 09:41:39', 47, 29, 2),
(47, 'hgkafhgsafhgkashg', '2023-06-05 09:41:40', 47, 29, 2),
(48, 'gkhasfhkgasgfhkafs', '2023-06-05 09:41:41', 47, 29, 2),
(49, 'safghghasghkasf', '2023-06-05 09:41:42', 47, 29, 2),
(50, 'afssafsaf', '2023-06-05 09:41:55', 44, 29, 2),
(51, 'asfasf', '2023-06-05 09:41:55', 44, 29, 2),
(52, 'asf', '2023-06-05 09:41:56', 44, 29, 2),
(53, 'saf', '2023-06-05 09:41:56', 44, 29, 2),
(54, 'asf', '2023-06-05 09:41:56', 44, 29, 2),
(55, 'sa', '2023-06-05 09:41:56', 44, 29, 2),
(56, 'fsa', '2023-06-05 09:41:57', 44, 29, 2),
(57, 'saf', '2023-06-05 09:41:57', 44, 29, 2),
(58, 'asf', '2023-06-05 09:41:57', 44, 29, 2),
(59, 'asf', '2023-06-05 09:41:58', 44, 29, 2),
(60, 'asf', '2023-06-05 09:41:58', 44, 29, 2),
(61, 'asf', '2023-06-05 09:41:58', 44, 29, 2),
(62, 'a', '2023-06-05 09:41:58', 44, 29, 2),
(63, 'af', '2023-06-05 09:41:59', 44, 29, 2),
(64, 'aga', '2023-06-05 09:42:41', 29, 43, 0),
(65, 'sekulaa', '2023-06-05 09:53:37', 47, 36, 2),
(66, 'as', '2023-06-05 09:53:38', 47, 36, 2),
(67, 'saf', '2023-06-05 09:53:38', 47, 36, 2),
(68, 'asf', '2023-06-05 09:53:39', 47, 36, 2),
(69, 'asf', '2023-06-05 09:53:39', 47, 36, 2),
(70, 'asf', '2023-06-05 09:53:40', 47, 36, 2),
(71, 'f', '2023-06-05 09:53:40', 47, 36, 2),
(72, 'fas', '2023-06-05 09:53:41', 47, 36, 2),
(73, 'fas', '2023-06-05 09:53:42', 47, 36, 2),
(74, 'as', '2023-06-05 09:53:42', 47, 36, 2),
(75, 'asfa', '2023-06-05 09:53:42', 47, 36, 2),
(76, 'as', '2023-06-05 09:53:43', 47, 36, 2),
(77, 'sfa', '2023-06-05 09:53:43', 47, 36, 2),
(78, 'saf', '2023-06-05 09:53:44', 47, 36, 2),
(79, 'asf', '2023-06-05 09:53:44', 47, 36, 2),
(80, 's', '2023-06-05 09:53:45', 47, 36, 2),
(81, 'sfa', '2023-06-05 09:53:45', 47, 36, 2),
(82, 'fa', '2023-06-05 09:53:46', 47, 36, 2),
(83, 'fsa', '2023-06-05 09:53:46', 47, 36, 2),
(84, 'saf', '2023-06-05 09:53:46', 47, 36, 2),
(85, 'asf', '2023-06-05 09:53:47', 47, 36, 2),
(86, 'sf', '2023-06-05 09:53:47', 47, 36, 2),
(87, 'saf', '2023-06-05 09:53:48', 47, 36, 2),
(88, 'f', '2023-06-05 09:53:48', 47, 36, 2),
(89, 's', '2023-06-05 09:53:49', 47, 36, 2),
(90, 'f', '2023-06-05 09:53:49', 47, 36, 2),
(91, 'sa', '2023-06-05 09:53:49', 47, 36, 2),
(92, 'sa', '2023-06-05 09:53:50', 47, 36, 2),
(93, 'f', '2023-06-05 09:53:50', 47, 36, 2),
(94, 'fsa', '2023-06-05 09:53:51', 47, 36, 2),
(95, 'as', '2023-06-05 09:53:51', 47, 36, 2),
(96, 'f', '2023-06-05 09:53:51', 47, 36, 2),
(97, 'sfas', '2023-06-05 09:53:52', 47, 36, 2),
(98, 'af', '2023-06-05 09:53:52', 47, 36, 2),
(99, 'fas', '2023-06-05 09:53:53', 47, 36, 2),
(100, 'fas', '2023-06-05 09:53:53', 47, 36, 2),
(101, 'f', '2023-06-05 09:53:53', 47, 36, 2),
(102, 'as', '2023-06-05 09:53:54', 47, 36, 2),
(103, 'f', '2023-06-05 09:53:54', 47, 36, 2),
(104, 'f', '2023-06-05 09:53:55', 47, 36, 2),
(105, 'sa', '2023-06-05 09:53:55', 47, 36, 2),
(106, 'as', '2023-06-05 09:53:55', 47, 36, 2),
(107, 'fa', '2023-06-05 09:53:56', 47, 36, 2),
(108, 'fasfasfasfa', '2023-06-05 15:57:52', 16, 47, 2),
(109, 'ako se ne uozbiljis', '2023-06-05 15:57:59', 16, 47, 2),
(110, 'izbacujem te', '2023-06-05 15:58:02', 16, 47, 2),
(111, 'trajno', '2023-06-05 15:58:03', 16, 47, 2),
(112, 'iz ', '2023-06-05 15:58:04', 16, 47, 2),
(113, 'sistema', '2023-06-05 15:58:08', 16, 47, 2),
(114, 'jel jasno', '2023-06-05 15:58:10', 16, 47, 2),
(115, 'sto te prijavljuje igor onoliko', '2023-06-05 16:11:37', 16, 29, 2);

-- --------------------------------------------------------

--
-- Table structure for table `prijava`
--

DROP TABLE IF EXISTS `prijava`;
CREATE TABLE IF NOT EXISTS `prijava` (
  `IdPri` int NOT NULL AUTO_INCREMENT,
  `IdKli` int NOT NULL,
  `IdPrijavljenog` int NOT NULL,
  `Tekst` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `DatumVreme` datetime NOT NULL,
  PRIMARY KEY (`IdPri`),
  KEY `IdKli` (`IdKli`),
  KEY `prijava_ibfk_3` (`IdPrijavljenog`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Dumping data for table `prijava`
--

INSERT INTO `prijava` (`IdPri`, `IdKli`, `IdPrijavljenog`, `Tekst`, `DatumVreme`) VALUES
(1, 47, 29, 'los', '2023-06-05 15:20:04'),
(2, 47, 29, 'eafe', '2023-06-05 15:22:25'),
(3, 47, 29, 'fsasaas', '2023-06-05 15:22:29'),
(4, 47, 29, 'fasassfsafasafsfasassfsafasafsfasassfsafasafsfasassfsafasafsfasassfsafasafsfasassfsafasafsfasassfsaf', '2023-06-05 15:22:37'),
(5, 47, 29, 'fasfasfsasfa', '2023-06-05 15:22:42'),
(6, 47, 29, 'fsafsafsasaf', '2023-06-05 15:22:46'),
(7, 47, 29, 'afssfafsafsafsaasfasf', '2023-06-05 15:22:50'),
(8, 47, 29, 'fsaasfsafsafsfa', '2023-06-05 15:22:54'),
(9, 47, 29, 'sfaasfasffasasf', '2023-06-05 15:22:58'),
(10, 47, 29, 'fasfsafsasaf', '2023-06-05 15:29:00'),
(11, 47, 29, 'fsafasasfsfa', '2023-06-05 15:29:04'),
(12, 47, 29, 'asfsafsaf', '2023-06-05 15:29:08');

-- --------------------------------------------------------

--
-- Table structure for table `recenzija`
--

DROP TABLE IF EXISTS `recenzija`;
CREATE TABLE IF NOT EXISTS `recenzija` (
  `IdKli` int NOT NULL,
  `IdMaj` int NOT NULL,
  `Tekst` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `DatumVreme` datetime NOT NULL,
  PRIMARY KEY (`IdKli`,`IdMaj`),
  KEY `IdMaj` (`IdMaj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Dumping data for table `recenzija`
--

INSERT INTO `recenzija` (`IdKli`, `IdMaj`, `Tekst`, `DatumVreme`) VALUES
(43, 26, '', '2023-06-01 21:29:59'),
(43, 28, 'Sve pohvale. Svaka cast. Bravo. Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.Bravo.', '2023-06-01 21:23:57'),
(43, 29, '', '2023-06-01 21:35:52'),
(43, 30, '', '2023-06-01 21:33:49'),
(43, 32, 'NE zna nista.\r\n', '2023-06-01 21:35:25'),
(43, 33, '', '2023-06-02 00:03:22'),
(43, 34, '', '2023-06-01 21:42:19'),
(43, 36, '', '2023-06-02 00:00:29'),
(43, 39, '', '2023-06-02 00:03:31'),
(43, 40, '', '2023-06-02 00:14:08'),
(44, 29, '', '2023-06-02 00:46:07'),
(45, 26, '', '2023-06-01 21:12:59'),
(45, 27, '', '2023-06-01 21:13:06'),
(45, 28, '', '2023-06-01 21:13:13'),
(45, 29, '', '2023-06-01 21:13:20'),
(45, 30, '', '2023-06-01 21:13:27'),
(45, 31, '', '2023-06-01 21:13:36'),
(45, 32, '', '2023-06-01 21:13:45'),
(45, 33, '', '2023-06-01 21:14:07'),
(45, 34, '', '2023-06-01 21:14:14'),
(45, 35, '', '2023-06-01 21:14:58'),
(45, 36, '', '2023-06-01 21:15:06'),
(45, 37, '', '2023-06-01 21:15:16'),
(45, 38, '', '2023-06-01 21:15:24'),
(46, 29, 'Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, bravo, svaka cast!Sjajno, brav', '2023-06-03 15:44:47'),
(47, 26, 'sve pohvale', '2023-06-01 21:00:09'),
(47, 27, '', '2023-06-01 21:04:24'),
(47, 28, 'Pravi automehanicar. Radi svoj posao s ljubavlju. Sve preporuke.', '2023-06-01 21:03:48'),
(47, 29, '', '2023-06-01 21:04:32'),
(47, 30, 'dobar zna matu\r\n', '2023-06-01 21:03:02'),
(47, 31, 'jeftin, ali nema pojma', '2023-06-01 21:02:12'),
(47, 32, '', '2023-06-01 21:04:43'),
(47, 33, '', '2023-06-01 21:05:04'),
(47, 34, '', '2023-06-01 21:04:53'),
(47, 35, '', '2023-06-01 21:05:20'),
(47, 36, '', '2023-06-01 21:05:36'),
(47, 37, '', '2023-06-01 21:05:46'),
(47, 38, '', '2023-06-01 21:05:28'),
(48, 26, '', '2023-06-01 20:47:48'),
(48, 27, '', '2023-06-01 21:06:23'),
(48, 28, '', '2023-06-01 21:06:31'),
(48, 29, '', '2023-06-01 21:06:45'),
(48, 30, '', '2023-06-01 21:06:54'),
(48, 31, '', '2023-06-01 21:07:02'),
(48, 32, '', '2023-06-01 21:07:10'),
(48, 33, '', '2023-06-01 21:07:17'),
(48, 34, '', '2023-06-01 21:07:25'),
(48, 35, '', '2023-06-01 21:07:50'),
(48, 36, '', '2023-06-01 21:07:59'),
(48, 37, '', '2023-06-01 21:08:07'),
(48, 38, '', '2023-06-01 21:08:15'),
(50, 29, 'Najbolji je.', '2023-06-03 16:49:19'),
(51, 29, 'bravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravo', '2023-06-03 16:50:13'),
(52, 29, 'bbravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravooobravoooravooo', '2023-06-03 16:51:07'),
(53, 29, '', '2023-06-03 16:51:48'),
(54, 29, '', '2023-06-03 16:52:22'),
(55, 29, '', '2023-06-03 19:00:30'),
(56, 29, 'bravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravobravo', '2023-06-03 19:01:32'),
(57, 28, 'Sve dodbro odradio.', '2023-06-04 20:13:00');

-- --------------------------------------------------------

--
-- Table structure for table `registrovani_korisnik`
--

DROP TABLE IF EXISTS `registrovani_korisnik`;
CREATE TABLE IF NOT EXISTS `registrovani_korisnik` (
  `IdKor` int NOT NULL AUTO_INCREMENT,
  `Ime` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `Prezime` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `MejlAdresa` varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `KorisnickoIme` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `Lozinka` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `IdGra` int NOT NULL,
  `IdSli` int DEFAULT NULL,
  `TipKorisnika` char(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  PRIMARY KEY (`IdKor`),
  UNIQUE KEY `MejlAdresa` (`MejlAdresa`),
  UNIQUE KEY `KorisnickoIme` (`KorisnickoIme`),
  KEY `IdGra` (`IdGra`),
  KEY `IdSli` (`IdSli`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Dumping data for table `registrovani_korisnik`
--

INSERT INTO `registrovani_korisnik` (`IdKor`, `Ime`, `Prezime`, `MejlAdresa`, `KorisnickoIme`, `Lozinka`, `IdGra`, `IdSli`, `TipKorisnika`) VALUES
(16, 'Ljubica', 'Majstorović', 'ljubmajstorovic9@gmail.com', 'ljups', 'ljups123', 35, NULL, 'A'),
(17, 'Nikola', 'Nikolić', 'posta.nikolan@gmail.com', 'dzon', 'dzon123', 9, NULL, 'A'),
(18, 'Pavle', 'Šarenac', 'sarenac.pavle@gmail.com', 'pajo', 'pajo123', 9, NULL, 'A'),
(26, 'Boris', 'Tončić', 'boki@gmail.com', 'boki', 'boki123', 24, 3, 'M'),
(27, 'Dušan', 'Cvetković', 'duki@gmail.com', 'duki', 'duki123', 28, NULL, 'M'),
(28, 'Vladimir', 'Aleksić', 'vlajko@gmail.com', 'vlajko', 'vlajko123', 9, NULL, 'M'),
(29, 'Dušan', 'Pešić', 'pesou@gmail.com', 'pesou', 'pesou123', 19, NULL, 'M'),
(30, 'Žarko', 'Bulić', 'zarou@gmail.com', 'zarou', 'zarou123', 18, NULL, 'M'),
(31, 'Mihajlo', 'Krunić', 'krunou@gmail.com', 'krunou', 'krunou123', 22, NULL, 'M'),
(32, 'Nikola', 'Janaćković', 'djono@gmail.com', 'djono', 'djono123', 17, NULL, 'M'),
(33, 'Luka', 'Golijanin', 'kalur@gmail.com', 'kalur', 'kalur123', 18, NULL, 'M'),
(34, 'Vojin', 'Radosavljević', 'voja@gmail.com', 'voja', 'voja123', 30, NULL, 'M'),
(35, 'Aleksa', 'Trifković', 'trifke@gmail.com', 'trifke', 'trifke123', 27, NULL, 'M'),
(36, 'Dušan', 'Sekulić', 'sekula@gmail.com', 'sekula', 'sekula123', 25, NULL, 'M'),
(37, 'Jovan', 'Šimpraga', 'simpri@gmail.com', 'simpri', 'simpri123', 18, NULL, 'M'),
(38, 'Vladimir', 'Beljić', 'dovla@gmail.com', 'dovla', 'dovla123', 36, NULL, 'M'),
(39, 'Vuk', 'Radović', 'cevu@gmail.com', 'cevu', 'cevu123', 30, NULL, 'M'),
(40, 'Aleksandar', 'Šarac', 'saki@gmail.com', 'saki', 'saki123', 20, NULL, 'M'),
(41, 'Vladimir', 'Stojanović', 'vlada@gmail.com', 'vlada', 'vlada123', 26, NULL, 'M'),
(42, 'Andrej', 'Savić', 'savke@gmail.com', 'savke', 'savke123', 19, NULL, 'M'),
(43, 'Filip', 'Gajić', 'fikus@gmail.com', 'fikus', 'fikus123', 23, NULL, 'K'),
(44, 'Irina', 'Majstorović', 'rinka@gmail.com', 'rinka', 'rinka123', 35, NULL, 'K'),
(45, 'Nataša', 'Majstorović', 'tasta@gmail.com', 'tasta', 'tasta123', 16, NULL, 'K'),
(46, 'Olivera', 'Antić', 'majka@gmail.com', 'majka', 'majka123', 9, NULL, 'K'),
(47, 'Igor', 'Šarenac', 'tataa@gmail.com', 'tata', 'tata123', 25, 1, 'K'),
(48, 'Branko', 'Majstorović', 'branko@gmail.com', 'tast', 'tast123', 25, NULL, 'K'),
(49, 'Miloš', 'Miladinović', 'losmi@gmail.com', 'losmi', 'losmi123', 24, NULL, 'M'),
(50, 'test1', 'test1', 'test1@gmail.com', 'test1', 'test123', 27, NULL, 'K'),
(51, 'test2', 'test2', 'test2@gmail.com', 'test2', 'test123', 27, NULL, 'K'),
(52, 'test3', 'test3', 'test3@gmail.com', 'test3', 'test123', 17, NULL, 'K'),
(53, 'test4', 'test4', 'test4@gmail.com', 'test4', 'test123', 18, NULL, 'K'),
(54, 'test5', 'test5', 'test5@gmail.com', 'test5', 'test123', 21, NULL, 'K'),
(55, 'test6', 'test6', 'test6@gmail.com', 'test6', 'test123', 23, NULL, 'K'),
(56, 'test7', 'test7', 'test7@gmail.com', 'test7', 'test123', 22, NULL, 'K'),
(57, 'Zoran', 'Riboskic', 'zoki@gmail.com', 'zoki', 'zoki123', 9, NULL, 'K'),
(58, 'test10', 'test10', 'test10@gmail.com', 'test10', 'test123', 25, NULL, 'K');

-- --------------------------------------------------------

--
-- Table structure for table `slika`
--

DROP TABLE IF EXISTS `slika`;
CREATE TABLE IF NOT EXISTS `slika` (
  `IdSli` int NOT NULL AUTO_INCREMENT,
  `Path` varchar(200) COLLATE utf8mb3_unicode_ci NOT NULL,
  PRIMARY KEY (`IdSli`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Dumping data for table `slika`
--

INSERT INTO `slika` (`IdSli`, `Path`) VALUES
(1, 'uploads/1685972443_5e89d95c2df4b69d7835.jpeg'),
(2, 'uploads/1685973153_1552b30357e4e67a74eb.png'),
(3, 'uploads/1685973317_29298136ce546afc233a.jpeg');

-- --------------------------------------------------------

--
-- Table structure for table `specijalnosti`
--

DROP TABLE IF EXISTS `specijalnosti`;
CREATE TABLE IF NOT EXISTS `specijalnosti` (
  `IdSpec` int NOT NULL AUTO_INCREMENT,
  `Opis` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`IdSpec`)
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Dumping data for table `specijalnosti`
--

INSERT INTO `specijalnosti` (`IdSpec`, `Opis`) VALUES
(8, 'Moler'),
(9, 'Gipsar'),
(10, 'Fasader'),
(11, 'Zidar'),
(12, 'Parketar'),
(13, 'Keramičar'),
(14, 'Građevinski limar'),
(15, 'Varilac'),
(16, 'Građevinski stolar'),
(17, 'Izolater'),
(18, 'Tesar'),
(19, 'Izrada bazena i fontana'),
(20, 'Pomoćni radnik'),
(21, 'Postavljanje podnih površina'),
(22, 'Sečenje i bušenje'),
(23, 'Armirač'),
(24, 'Bravar-monter'),
(25, 'Električar'),
(26, 'Vikler elektromotora'),
(27, 'Serviser liftova'),
(28, 'Monter klima uređaja'),
(29, 'Audio-video serviser'),
(30, 'Serviser mobilnih telefona'),
(31, 'Serviser računara'),
(32, 'Spremačica'),
(33, 'Čistač'),
(34, 'Perač podnih površina'),
(35, 'Odžačar'),
(36, 'Baštovan'),
(37, 'Drvoseča'),
(38, 'Visinski radnik'),
(39, 'Perač fasada'),
(40, 'Visinski radnik'),
(41, 'Domar'),
(42, 'Vodoinstalater'),
(43, 'Monter grejnih instalacija'),
(44, 'Ventilacioni sistemi'),
(45, 'Ključar'),
(46, 'Tapetar'),
(47, 'Metalostrugar'),
(48, 'Stolar'),
(49, 'Staklorezac'),
(50, 'Grnčar'),
(51, 'Kamenorezac'),
(52, 'Mašin-bravar'),
(53, 'Kovač'),
(54, 'Površinska obrada'),
(55, 'Livac'),
(56, 'Uramljivač'),
(57, 'Metaloglodač'),
(58, 'Metalooštrač'),
(59, 'Pečatorezac'),
(60, 'Krojač'),
(61, 'Obućar'),
(62, 'Tašner'),
(63, 'Sajdžija - Časovničar'),
(64, 'Juvelir - Zlatar'),
(65, 'Mašinski vez'),
(66, 'Ručni vez'),
(67, 'Krznar'),
(68, 'Šeširdžija'),
(69, 'Serviser šivećih mašina'),
(70, 'Auto električar'),
(71, 'Auto limar'),
(72, 'Auto bravar'),
(73, 'Auto mehaničar'),
(74, 'Serviser autogas sistema'),
(75, 'Auto grafičar'),
(76, 'Auto tapetar'),
(77, 'Auto perač'),
(78, 'Vulkanizer'),
(79, 'Šlep služba'),
(80, 'Serviser trapa'),
(81, 'Auto stakla'),
(82, 'Auto plastičar'),
(83, 'Serviser auspuha'),
(84, 'Serviser motocikala'),
(85, 'Vozač'),
(86, 'Transport selidbi'),
(87, 'Fizički radnik'),
(88, 'Grafičar'),
(89, 'Firmopisac'),
(90, 'Ikonopisac'),
(91, 'Serviser za bicikle'),
(92, 'Ski serviser'),
(93, 'Serviser medicinske opreme'),
(94, 'Roletne i venecijaneri');

-- --------------------------------------------------------

--
-- Table structure for table `telefon`
--

DROP TABLE IF EXISTS `telefon`;
CREATE TABLE IF NOT EXISTS `telefon` (
  `IdKor` int NOT NULL,
  `Telefon` varchar(15) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  PRIMARY KEY (`IdKor`,`Telefon`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Dumping data for table `telefon`
--

INSERT INTO `telefon` (`IdKor`, `Telefon`) VALUES
(26, '+381-62-468-313'),
(27, '+381-62-468-314'),
(28, '+381-62-468-315'),
(29, '+381-62-468-316'),
(30, '+381-62-468-317'),
(31, '+381-62-468-318'),
(32, '+381-62-468-319'),
(33, '+381-62-468-320'),
(34, '+381-62-468-321'),
(35, '+381-62-468-322'),
(36, '+381-62-468-323'),
(37, '+381-62-468-324'),
(38, '+381-62-468-325'),
(39, '+381-62-468-326'),
(40, '+381-62-468-327'),
(41, '+381-62-468-328'),
(42, '+381-62-468-329'),
(43, '+381-62-468-330'),
(44, '+381-62-468-331'),
(45, '+381-62-468-332'),
(46, '+381-62-468-333'),
(47, '+381-62-468-334'),
(48, '+381-62-468-335'),
(49, '+381-62-468-336'),
(50, '+381-62-468-337'),
(51, '+381-62-468-338'),
(52, '+381-62-468-339'),
(53, '+381-62-468-340'),
(54, '+381-62-468-341'),
(55, '+381-62-468-342'),
(56, '+381-62-468-343'),
(57, '+381-65-956-344'),
(58, '+381-62-468-345');

-- --------------------------------------------------------

--
-- Table structure for table `zabranjeni_mejlovi`
--

DROP TABLE IF EXISTS `zabranjeni_mejlovi`;
CREATE TABLE IF NOT EXISTS `zabranjeni_mejlovi` (
  `MejlAdresa` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `IdAdm` int NOT NULL,
  PRIMARY KEY (`MejlAdresa`),
  KEY `KorisnickoIme` (`IdAdm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Dumping data for table `zabranjeni_mejlovi`
--

INSERT INTO `zabranjeni_mejlovi` (`MejlAdresa`, `IdAdm`) VALUES
('majstorZaIzbacivanje@gmail.com', 16);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `majstor`
--
ALTER TABLE `majstor`
  ADD CONSTRAINT `majstor_ibfk_1` FOREIGN KEY (`IdMaj`) REFERENCES `registrovani_korisnik` (`IdKor`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `majstor_ibfk_2` FOREIGN KEY (`IdSpec`) REFERENCES `specijalnosti` (`IdSpec`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `poruka`
--
ALTER TABLE `poruka`
  ADD CONSTRAINT `poruka_ibfk_1` FOREIGN KEY (`IdPos`) REFERENCES `registrovani_korisnik` (`IdKor`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `poruka_ibfk_2` FOREIGN KEY (`IdPri`) REFERENCES `registrovani_korisnik` (`IdKor`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `prijava`
--
ALTER TABLE `prijava`
  ADD CONSTRAINT `prijava_ibfk_2` FOREIGN KEY (`IdKli`) REFERENCES `registrovani_korisnik` (`IdKor`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `prijava_ibfk_3` FOREIGN KEY (`IdPrijavljenog`) REFERENCES `majstor` (`IdMaj`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `recenzija`
--
ALTER TABLE `recenzija`
  ADD CONSTRAINT `recenzija_ibfk_2` FOREIGN KEY (`IdMaj`) REFERENCES `majstor` (`IdMaj`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `recenzija_ibfk_3` FOREIGN KEY (`IdKli`) REFERENCES `registrovani_korisnik` (`IdKor`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `registrovani_korisnik`
--
ALTER TABLE `registrovani_korisnik`
  ADD CONSTRAINT `registrovani_korisnik_ibfk_1` FOREIGN KEY (`IdGra`) REFERENCES `grad` (`IdGra`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `registrovani_korisnik_ibfk_2` FOREIGN KEY (`IdSli`) REFERENCES `slika` (`IdSli`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `telefon`
--
ALTER TABLE `telefon`
  ADD CONSTRAINT `telefon_ibfk_1` FOREIGN KEY (`IdKor`) REFERENCES `registrovani_korisnik` (`IdKor`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `zabranjeni_mejlovi`
--
ALTER TABLE `zabranjeni_mejlovi`
  ADD CONSTRAINT `zabranjeni_mejlovi_ibfk_1` FOREIGN KEY (`IdAdm`) REFERENCES `registrovani_korisnik` (`IdKor`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
