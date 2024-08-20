-- 
-- Disable foreign keys
-- 
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

-- 
-- Set SQL mode
-- 
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


CREATE TABLE user (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL COMMENT 'Имя пользователя (ФИО)',
  login_cert varchar(100) NOT NULL COMMENT 'Логин пользователя (для входа по сертификату)',
  login varchar(30) NOT NULL COMMENT 'Логин пользователя (для входа по логину-паролю)',
  valid bit(1) NOT NULL,
  password char(64) NOT NULL COMMENT 'пароль, закодированный в sha-1',
  position varchar(100) NOT NULL COMMENT 'должность',
  PRIMARY KEY (id)
)
ENGINE = INNODB,
AUTO_INCREMENT = 3,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci;

--
-- Create table `role`
--
CREATE TABLE role (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB,
AUTO_INCREMENT = 3,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci;

--
-- Create table `user_role`
--
CREATE TABLE user_role (
  user_id int(11) NOT NULL,
  role_id int(11) NOT NULL,
  PRIMARY KEY (user_id, role_id)
)
ENGINE = INNODB,
AVG_ROW_LENGTH = 4096,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci;

--
-- Create foreign key
--
ALTER TABLE user_role
ADD CONSTRAINT FK_user_role_role_id FOREIGN KEY (role_id)
REFERENCES role (id);

--
-- Create foreign key
--
ALTER TABLE user_role
ADD CONSTRAINT FK_user_role_user_id FOREIGN KEY (user_id)
REFERENCES user (id);

--
-- Create table `ref_responsible_person`
--
CREATE TABLE ref_responsible_person (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = 'Справочник ответственных лиц';

--
-- Create table `ref_equipment_type`
--
CREATE TABLE ref_equipment_type (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = 'Справочник типов объектов инвентаризации';

--
-- Create index `UK_equipment_type_name` on table `equipment_type`
--
ALTER TABLE ref_equipment_type
ADD UNIQUE INDEX UK_ref_equipment_type_name (name);

--
-- Create table `ref_equipment_state`
--
CREATE TABLE ref_equipment_state (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = 'Справочник состояний объектов инвентаризации';

--
-- Create table `equipment`
--
CREATE TABLE equipment (
  id binary(16) NOT NULL,
  type_id int(11) NOT NULL,
  person_id int(11) NOT NULL,
  state_id int(11) NOT NULL,
  comment varchar(1000) DEFAULT NULL,
  inv_number varchar(20) NOT NULL,
  purchase_date date DEFAULT NULL,
  serial varchar(50) DEFAULT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB,
AVG_ROW_LENGTH = 1489,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci;

--
-- Create foreign key
--
ALTER TABLE equipment
ADD CONSTRAINT FK_ref_equipment_person_id FOREIGN KEY (person_id)
REFERENCES ref_responsible_person (id);

--
-- Create foreign key
--
ALTER TABLE equipment
ADD CONSTRAINT FK_ref_equipment_state_id FOREIGN KEY (state_id)
REFERENCES ref_equipment_state (id);

--
-- Create foreign key
--
ALTER TABLE equipment
ADD CONSTRAINT FK_ref_equipment_type_id FOREIGN KEY (type_id)
REFERENCES ref_equipment_type (id);

--
-- Create table `log`
--
CREATE TABLE log (
  equipment_id binary(16) NOT NULL,
  action_id tinyint(4) NOT NULL,
  user_id int(11) NOT NULL,
  comment varchar(8000) DEFAULT NULL,
  time datetime DEFAULT NULL
)
ENGINE = INNODB,
AVG_ROW_LENGTH = 2048,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci;

--
-- Create table `ref_action`
--
CREATE TABLE ref_action (
  id tinyint(4) NOT NULL,
  name varchar(50) NOT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci;

ALTER TABLE ref_action COMMENT = 'Справочник действий с объектами инвентаризации';

-- 
-- Dumping data for table user
--
INSERT INTO user VALUES
(1, 'test', 'CN=test,OU=UIST,O=BTO,L=Minsk,C=BY', 'test', True, '5ee0edb9e2229c0838f1959779f1949031de0123', 'вед.спец.');

-- 
-- Dumping data for table role
--
INSERT INTO role VALUES
(1, 'user'),
(2, 'admin');

-- 
-- Dumping data for table ref_responsible_person
--
INSERT INTO ref_responsible_person VALUES
(1, 'test');

-- 
-- Dumping data for table ref_equipment_type
--
INSERT INTO ref_equipment_type VALUES
(3, 'МФУ'),
(2, 'Ноутбук'),
(1, 'Системный блок');

-- 
-- Dumping data for table ref_equipment_state
--
INSERT INTO ref_equipment_state VALUES
(1, 'Новый'),
(2, 'В эксплуатации'),
(3, 'В резерве'),
(4, 'В ремонте'),
(5, 'Списан');

INSERT INTO user_role VALUES
(1, 1),
(1, 2);

-- 
-- Dumping data for table action
--
INSERT INTO ref_action VALUES
(0, 'Нет действия'),
(1, 'Создание'),
(2, 'Изменение'),
(3, 'Обслуживание'),
(4, 'Списание');


-- 
-- Restore previous SQL mode
-- 
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;

-- 
-- Enable foreign keys
-- 
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
