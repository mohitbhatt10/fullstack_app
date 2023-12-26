--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `role_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
INSERT INTO `roles` VALUES (1,'ROLE_USER','2023-12-25 07:29:24','2023-12-25 07:29:24'),
(2,'ROLE_ADMIN','2023-12-25 07:29:24','2023-12-25 07:29:24'),
(3,'ROLE_MODERATOR','2023-12-25 07:29:24','2023-12-25 07:29:24');
UNLOCK TABLES;

--
-- Table structure for table `todo`
--

DROP TABLE IF EXISTS `todo`;
CREATE TABLE `todo` (
  `is_done` bit(1) NOT NULL,
  `id` bigint NOT NULL,
  `target_date` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `todo`
--

LOCK TABLES `todo` WRITE;
INSERT INTO `todo` VALUES (_binary '\0',1,'2023-12-28 05:30:00.000000','New Task in MySQL','in28minutes'),
(_binary '\0',2,'2023-12-29 05:30:00.000000','Another task','in28minutes'),
(_binary '\0',52,'2023-12-24 16:24:02.963000','Any one','in28minutes'),
(_binary '\0',102,'2023-12-26 05:30:00.000000','sdvdfvffdvdf','in28minutes'),
(_binary '\0',103,'2023-12-25 00:21:21.250000','fdvfdvdfvdf','in28minutes'),
(_binary '\0',104,'2023-12-25 00:21:25.379000','dfvdfvdfvdfv','in28minutes'),
(_binary '\0',105,'2023-12-25 00:21:30.923000','dfvdfvdfvfdvdfvdfv','in28minutes'),
(_binary '\0',152,'2023-12-25 23:39:18.483000','Just the new Todo','in28minutes'),
(_binary '\0',153,'2023-12-25 23:39:57.695000','fdfbgbfgb','in28minutes'),
(_binary '\0',154,'2023-12-25 23:46:47.491000','dcsdvdfvdgbfgb','in28minutes'),
(_binary '\0',155,'2023-12-25 23:50:52.769000','erevvfvvrtvrvttbtfbfgbfgb gh ','raju123'),
(_binary '\0',156,'2023-12-25 23:53:11.366000','dvdf f fdgf ','raju123'),
(_binary '\0',157,'2023-12-26 00:27:56.326000','sdvfvfdvdf','mohitbhatt10'),
(_binary '\0',158,'2023-12-26 12:17:49.488000','a new todo','mohitbhatt10'),
(_binary '\0',202,'2024-01-01 05:30:00.000000','bhaijaan ka kaam','bhaijaan'),
(_binary '\0',252,'2023-12-26 15:46:55.077000','today\'s new task','in28minutes');
UNLOCK TABLES;

--
-- Table structure for table `todo_seq`
--

DROP TABLE IF EXISTS `todo_seq`;
CREATE TABLE `todo_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `todo_seq` WRITE;
INSERT INTO `todo_seq` VALUES (351);
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `user_id` bigint unsigned NOT NULL,
  `role_id` bigint unsigned NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
INSERT INTO `user_roles` VALUES (5,1),(11,1),(12,1),(13,1),(6,2);
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `enabled` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_id` (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


LOCK TABLES `users` WRITE;
INSERT INTO `users` VALUES 
(5,'Ranga','Karnam','in28minutes','$2a$10$y5B0QNa.X2VKgerqdUUBPeIBra295V5J0tDS2MSXTTEypGAt0QpNW','ranga.karnam@example.com','1234567890',1),
(6,'Mohit','Bhatt','mohitbhatt10','$2a$10$H1pYXZoq2GZdP1uZU2thh.Fd4XoW1zAXXW62K48r5G.H3KBOSD/LK','mohitbhatt10@gmail.com','1234567890',1),
(11,'Rajesh','Singh','raju123','$2a$10$A0M535hK3YrwPEc1mkG0E.OtpsdsWypoPEqv7uR/MMwl7vYy3TyWy','rajubhai@gmail.com','1234567890',1),
(12,'Champak','Lal','champak','$2a$10$IIIF6IKqSXNtBDzNPLB9ueISgOPBLqYtklGmI/yTYDR35I1b65YSq','champaklal@gmail.com','2345345353',1),
(13,'Bhai','Jaan','bhaijaan','$2a$10$Guw5P.meV6s8j2dzPgyW4OOyuAqMF4qvGeR46p79lI/isMxAFDdRO','bhaijaan@gmail.com','234552434534',1);
UNLOCK TABLES;
