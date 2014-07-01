--
-- Table structure for table `uxResourceDirectory`
--

CREATE TABLE IF NOT EXISTS `uxResourceDirectory` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `directoryName` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `group_id` bigint(20) DEFAULT NULL,
  `rootDirectory_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_4ibvf2m67l7hm2ke4i15opf07` (`directoryName`),
  KEY `FK_8kds2ks0an1o4wqkaw8lwghcp` (`group_id`),
  KEY `FK_3t9pdtbe3rkastrn48uirv5wg` (`rootDirectory_id`),
  KEY `FK_orrj6yeuktq0960qpc0vp0uq5` (`user_id`),
  CONSTRAINT `FK_orrj6yeuktq0960qpc0vp0uq5` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_3t9pdtbe3rkastrn48uirv5wg` FOREIGN KEY (`rootDirectory_id`) REFERENCES `uxResourceDirectory` (`id`),
  CONSTRAINT `FK_8kds2ks0an1o4wqkaw8lwghcp` FOREIGN KEY (`group_id`) REFERENCES `ccGroup` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;



--
-- Table structure for table `uxResourceDirectory_uxpermission`
--

CREATE TABLE IF NOT EXISTS `uxResourceDirectory_uxpermission` (
  `uxResourceDirectory_id` bigint(20) NOT NULL,
  `uxPermissions_id` bigint(20) NOT NULL,
  PRIMARY KEY (`uxResourceDirectory_id`,`uxPermissions_id`),
  KEY `FK_7uroh2qr81y1euiwro9vbo1gw` (`uxPermissions_id`),
  CONSTRAINT `FK_ofsxhmp9gvdualgafeiok906q` FOREIGN KEY (`uxResourceDirectory_id`) REFERENCES `uxResourceDirectory` (`id`),
  CONSTRAINT `FK_7uroh2qr81y1euiwro9vbo1gw` FOREIGN KEY (`uxPermissions_id`) REFERENCES `uxpermission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



--
-- Table structure for table `uxResourceRequest`
--

CREATE TABLE IF NOT EXISTS `uxResourceRequest` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` longtext,
  `isTemplate` bit(1) DEFAULT NULL,
  `requestName` varchar(255) DEFAULT NULL,
  `request` longtext,
  `version` int(11) DEFAULT NULL,
  `group_id` bigint(20) DEFAULT NULL,
  `rootDirectory_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_m6djei8v9qmhgqxefhitrgfvd` (`requestName`),
  KEY `FK_6thjvhlsiithxccayfxsbtgof` (`group_id`),
  KEY `FK_839sxpl3axthgdkav5ve54d7d` (`rootDirectory_id`),
  KEY `FK_kx9355l2wf4k08humfg4cqxh2` (`user_id`),
  CONSTRAINT `FK_kx9355l2wf4k08humfg4cqxh2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_6thjvhlsiithxccayfxsbtgof` FOREIGN KEY (`group_id`) REFERENCES `ccGroup` (`id`),
  CONSTRAINT `FK_839sxpl3axthgdkav5ve54d7d` FOREIGN KEY (`rootDirectory_id`) REFERENCES `uxResourceDirectory` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;



--
-- Table structure for table `uxResourceRequest_uxpermission`
--

CREATE TABLE IF NOT EXISTS `uxResourceRequest_uxpermission` (
  `uxResourceRequest_id` bigint(20) NOT NULL,
  `uxPermissions_id` bigint(20) NOT NULL,
  PRIMARY KEY (`uxResourceRequest_id`,`uxPermissions_id`),
  KEY `FK_ab5neaushm0b01l11hcgudt2v` (`uxPermissions_id`),
  CONSTRAINT `FK_q6gkp241nh3vp9wmkjl342tp` FOREIGN KEY (`uxResourceRequest_id`) REFERENCES `uxResourceRequest` (`id`),
  CONSTRAINT `FK_ab5neaushm0b01l11hcgudt2v` FOREIGN KEY (`uxPermissions_id`) REFERENCES `uxpermission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;