CREATE DATABASE  IF NOT EXISTS `jxsn_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `jxsn_db`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: jxsn_db
-- ------------------------------------------------------
-- Server version	8.0.45

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
-- Table structure for table `craft_process_definition`
--

DROP TABLE IF EXISTS `craft_process_definition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `craft_process_definition` (
  `process_id` int NOT NULL AUTO_INCREMENT COMMENT '工序ID',
  `process_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '工序名称',
  `standard_rules` text COLLATE utf8mb4_general_ci COMMENT '行业标准规范逻辑',
  PRIMARY KEY (`process_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='酿酒工艺工序定义表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `craft_process_definition`
--

LOCK TABLES `craft_process_definition` WRITE;
/*!40000 ALTER TABLE `craft_process_definition` DISABLE KEYS */;
INSERT INTO `craft_process_definition` VALUES (1,'原料处理','原料处理阶段应控制原料比例、清洗状态和投料顺序。'),(2,'发酵控制','发酵阶段应重点控制温度、湿度、微生物浓度和发酵时长。'),(3,'蒸馏操作','蒸馏阶段应重点控制温度、压力和蒸馏时间。'),(4,'陈酿管理','陈酿阶段应关注储存时间、环境温湿度和容器状态。'),(5,'原料处理','原料处理阶段应控制原料清洗、粉碎程度和投料顺序。'),(6,'发酵控制','发酵阶段应重点控制温度、湿度、微生物浓度和发酵时长。'),(7,'蒸馏操作','蒸馏阶段应重点控制温度、压力、冷凝状态和蒸馏时间。'),(8,'陈酿管理','陈酿阶段应关注储存时间、环境温湿度和容器密封状态。');
/*!40000 ALTER TABLE `craft_process_definition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_log`
--

DROP TABLE IF EXISTS `operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_log` (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '操作日志ID',
  `session_id` bigint NOT NULL COMMENT '所属会话ID',
  `step_index` int DEFAULT NULL COMMENT '操作步骤序号',
  `real_time_value` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '实时采集参数值',
  `is_correct` tinyint(1) DEFAULT NULL COMMENT '规则匹配纠错标记',
  `ai_feedback` text COLLATE utf8mb4_general_ci COMMENT 'AI个性化指导话术',
  `op_time` datetime(3) DEFAULT NULL COMMENT '毫秒级操作时间戳',
  PRIMARY KEY (`log_id`),
  KEY `fk_log_session` (`session_id`),
  CONSTRAINT `fk_log_session` FOREIGN KEY (`session_id`) REFERENCES `training_session` (`session_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='实训操作过程实时记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_log`
--

LOCK TABLES `operation_log` WRITE;
/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
INSERT INTO `operation_log` VALUES (1,1,1,'temperature=36',1,'当前温度符合发酵工艺要求，可继续进行下一步。','2026-05-27 14:51:35.506'),(2,1,2,'humidity=95',0,'系统检测到湿度偏高，请适当降低湿度并重新观察发酵状态。','2026-05-27 14:51:35.506'),(3,2,1,'temperature=36',1,'当前发酵温度符合工艺要求，可继续进行下一步。','2026-05-27 15:35:09.707'),(4,2,2,'humidity=95',0,'系统检测到湿度参数异常，请检查发酵环境湿度设置，适当降低或提高湿度，使其回到标准工艺范围内。','2026-05-27 15:45:09.707'),(5,2,3,'microbe=0.72',1,'当前微生物浓度处于合理范围，发酵状态正常。','2026-05-27 16:00:09.707'),(6,3,1,'temperature=92',1,'当前蒸馏温度符合要求，可继续保持观察。','2026-05-27 15:00:09.712'),(7,3,2,'pressure=0.32',0,'系统检测到蒸馏压力偏高，请检查阀门状态并适当降低加热强度。','2026-05-27 15:20:09.712'),(8,3,3,'duration=95',0,'系统检测到工艺时长异常，请核对当前步骤的标准操作时间，避免过短或过长影响实训结果。','2026-05-27 15:50:09.712'),(9,4,1,'wash_status=完成',1,'原料清洗完成，符合实训流程要求。','2026-05-27 14:20:09.717'),(10,4,2,'crush_level=适中',1,'原料粉碎程度适中，可进入投料环节。','2026-05-27 14:40:09.717'),(11,4,3,'feed_order=正确',1,'投料顺序正确，本次原料处理实训完成。','2026-05-27 15:40:09.717'),(12,5,1,'storage_temp=24',1,'当前陈酿环境温度正常。','2026-05-27 15:50:09.721'),(13,5,2,'seal_status=未密封',0,'系统检测到容器密封状态异常，请重新检查封口情况，确认密封完成后再继续后续操作。','2026-05-27 16:02:09.721');
/*!40000 ALTER TABLE `operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scene_craft_parameter`
--

DROP TABLE IF EXISTS `scene_craft_parameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scene_craft_parameter` (
  `param_id` bigint NOT NULL AUTO_INCREMENT COMMENT '参数ID',
  `template_id` bigint NOT NULL COMMENT '关联模板ID',
  `temp_threshold` decimal(5,2) DEFAULT NULL COMMENT '温度阈值',
  `humidity_threshold` decimal(5,2) DEFAULT NULL COMMENT '湿度阈值',
  `microbe_concentration` decimal(10,4) DEFAULT NULL COMMENT '微生物浓度标准',
  `duration_minutes` int DEFAULT NULL COMMENT '标准工艺时长',
  PRIMARY KEY (`param_id`),
  KEY `fk_param_template` (`template_id`),
  CONSTRAINT `fk_param_template` FOREIGN KEY (`template_id`) REFERENCES `scene_template` (`template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='场景工艺参数阈值表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scene_craft_parameter`
--

LOCK TABLES `scene_craft_parameter` WRITE;
/*!40000 ALTER TABLE `scene_craft_parameter` DISABLE KEYS */;
INSERT INTO `scene_craft_parameter` VALUES (1,1,38.00,85.00,0.8000,120),(2,2,95.00,70.00,0.3000,90);
/*!40000 ALTER TABLE `scene_craft_parameter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scene_template`
--

DROP TABLE IF EXISTS `scene_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scene_template` (
  `template_id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '场景方案名称',
  `process_id` int DEFAULT NULL COMMENT '所属工序',
  `creator_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `llm_prompt` text COLLATE utf8mb4_general_ci COMMENT '大模型Prompt指令',
  `config_json` json DEFAULT NULL COMMENT '场景组件JSON配置',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  PRIMARY KEY (`template_id`),
  KEY `fk_scene_template_process` (`process_id`),
  KEY `fk_scene_template_creator` (`creator_id`),
  CONSTRAINT `fk_scene_template_creator` FOREIGN KEY (`creator_id`) REFERENCES `sys_user` (`user_id`),
  CONSTRAINT `fk_scene_template_process` FOREIGN KEY (`process_id`) REFERENCES `craft_process_definition` (`process_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='虚拟实训场景模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scene_template`
--

LOCK TABLES `scene_template` WRITE;
/*!40000 ALTER TABLE `scene_template` DISABLE KEYS */;
INSERT INTO `scene_template` VALUES (1,'发酵控制实训场景',2,3,'生成发酵控制虚拟实训场景','{\"device\": \"fermentation_tank\"}',1),(2,'蒸馏操作实训场景',3,3,'生成蒸馏操作虚拟实训场景','{\"device\": \"distillation_unit\"}',1),(3,'原料处理实训场景',5,9,'生成原料处理虚拟实训场景','{\"device\": \"raw_material_device\"}',1),(4,'发酵控制实训场景',6,9,'生成发酵控制虚拟实训场景','{\"device\": \"fermentation_tank\"}',1),(5,'蒸馏操作实训场景',7,9,'生成蒸馏操作虚拟实训场景','{\"device\": \"distillation_unit\"}',1),(6,'陈酿管理实训场景',8,9,'生成陈酿管理虚拟实训场景','{\"device\": \"aging_container\"}',1);
/*!40000 ALTER TABLE `scene_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录账号',
  `real_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
  `role_type` enum('STUDENT','TEACHER','RESEARCHER') COLLATE utf8mb4_general_ci DEFAULT 'STUDENT' COMMENT '角色类型',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'student','张三','STUDENT','2026-05-27 14:51:35'),(2,'teacher','李老师','TEACHER','2026-05-27 14:51:35'),(3,'researcher','研创管理员','RESEARCHER','2026-05-27 14:51:35'),(4,'202309001','张三','STUDENT','2026-05-27 16:10:09'),(5,'202309002','李四','STUDENT','2026-05-27 16:10:09'),(6,'202309003','王五','STUDENT','2026-05-27 16:10:09'),(7,'202309004','赵六','STUDENT','2026-05-27 16:10:09'),(8,'teacher01','李老师','TEACHER','2026-05-27 16:10:09'),(9,'researcher01','研创管理员','RESEARCHER','2026-05-27 16:10:09');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_intervention`
--

DROP TABLE IF EXISTS `teacher_intervention`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_intervention` (
  `intervention_id` bigint NOT NULL AUTO_INCREMENT COMMENT '干预记录ID',
  `session_id` bigint NOT NULL COMMENT '关联实训会话ID',
  `teacher_id` bigint NOT NULL COMMENT '操作教师ID',
  `intervention_action` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '远程干预指令内容',
  `intervention_time` datetime DEFAULT NULL COMMENT '干预执行时间',
  PRIMARY KEY (`intervention_id`),
  KEY `fk_intervention_session` (`session_id`),
  KEY `fk_intervention_teacher` (`teacher_id`),
  CONSTRAINT `fk_intervention_session` FOREIGN KEY (`session_id`) REFERENCES `training_session` (`session_id`),
  CONSTRAINT `fk_intervention_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `sys_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师远程干预日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_intervention`
--

LOCK TABLES `teacher_intervention` WRITE;
/*!40000 ALTER TABLE `teacher_intervention` DISABLE KEYS */;
INSERT INTO `teacher_intervention` VALUES (1,1,2,'请降低湿度并重新提交参数。','2026-05-27 14:51:35'),(2,1,2,'请重新调整发酵湿度，不用谢','2026-05-27 15:24:38'),(3,2,8,'请降低发酵湿度，并重新提交湿度参数。','2026-05-27 15:50:09'),(4,3,8,'蒸馏压力偏高，请检查阀门并降低加热强度。','2026-05-27 15:25:09'),(5,5,8,'请检查陈酿容器密封状态，确认后继续操作。','2026-05-27 16:05:09'),(6,5,2,'干预测试一下','2026-05-27 16:26:53');
/*!40000 ALTER TABLE `teacher_intervention` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `training_session`
--

DROP TABLE IF EXISTS `training_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_session` (
  `session_id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `student_id` bigint NOT NULL COMMENT '学生用户ID',
  `template_id` bigint NOT NULL COMMENT '使用模板ID',
  `start_time` datetime DEFAULT NULL COMMENT '实训开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '实训结束时间',
  `session_status` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '会话状态',
  `final_report_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学习报告存储路径',
  PRIMARY KEY (`session_id`),
  KEY `fk_session_student` (`student_id`),
  KEY `fk_session_template` (`template_id`),
  CONSTRAINT `fk_session_student` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`user_id`),
  CONSTRAINT `fk_session_template` FOREIGN KEY (`template_id`) REFERENCES `scene_template` (`template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生实训会话表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_session`
--

LOCK TABLES `training_session` WRITE;
/*!40000 ALTER TABLE `training_session` DISABLE KEYS */;
INSERT INTO `training_session` VALUES (1,1,1,'2026-05-27 14:51:35',NULL,'进行中',NULL),(2,4,4,'2026-05-27 15:30:09',NULL,'进行中',NULL),(3,5,5,'2026-05-27 14:50:09',NULL,'进行中',NULL),(4,6,3,'2026-05-27 14:10:09','2026-05-27 15:50:09','已完成','/report/student3-report.pdf'),(5,7,6,'2026-05-27 15:45:09',NULL,'进行中',NULL);
/*!40000 ALTER TABLE `training_session` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-28 10:14:01
