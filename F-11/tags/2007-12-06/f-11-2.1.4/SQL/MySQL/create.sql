-- MySQL dump 9.11
--
-- Host: localhost    Database: wifedb
-- ------------------------------------------------------
-- Server version	4.0.27-nt

--
-- Table structure for table `alarm_email_sent_addresses_table`
--

DROP TABLE IF EXISTS alarm_email_sent_addresses_table;
CREATE TABLE alarm_email_sent_addresses_table (
  alarm_email_sent_addresses_id bigint(20) NOT NULL auto_increment,
  alarm_email_sent_id bigint(20) NOT NULL default '0',
  address varchar(255) NOT NULL default '',
  PRIMARY KEY  (alarm_email_sent_addresses_id),
  UNIQUE KEY alarm_email_sent_addresses_id (alarm_email_sent_addresses_id),
  KEY alarm_email_sent_address_id_idx (alarm_email_sent_id),
  KEY alarm_email_sent_address_idx (address)
) TYPE=MyISAM;

--
-- Table structure for table `alarm_email_sent_table`
--

DROP TABLE IF EXISTS alarm_email_sent_table;
CREATE TABLE alarm_email_sent_table (
  alarm_email_sent_id bigint(20) NOT NULL auto_increment,
  provider varchar(100) NOT NULL default '',
  holder varchar(100) NOT NULL default '',
  sentdate datetime NOT NULL default '0000-00-00 00:00:00',
  value tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (alarm_email_sent_id),
  UNIQUE KEY alarm_email_sent_id (alarm_email_sent_id),
  KEY alarm_email_sent_provider_idx (provider,holder),
  KEY alarm_email_sent_sentdate_idx (sentdate)
) TYPE=MyISAM;

--
-- Table structure for table `alarm_individual_setting_table`
--

DROP TABLE IF EXISTS alarm_individual_setting_table;
CREATE TABLE alarm_individual_setting_table (
  alarm_individual_setting_id bigint(20) NOT NULL auto_increment,
  provider varchar(100) NOT NULL default '',
  holder varchar(100) NOT NULL default '',
  type int(11) NOT NULL default '0',
  PRIMARY KEY  (alarm_individual_setting_id),
  UNIQUE KEY alarm_individual_setting_id (alarm_individual_setting_id),
  UNIQUE KEY alarm_individual_setting_provider_idx (provider,holder)
) TYPE=MyISAM;

--
-- Table structure for table `analog_type_table`
--

DROP TABLE IF EXISTS analog_type_table;
CREATE TABLE analog_type_table (
  analog_type_id int(11) NOT NULL default '0',
  analog_type_name text,
  convert_min double default NULL,
  convert_max double default NULL,
  input_min double default NULL,
  input_max double default NULL,
  format text,
  convert_type text,
  PRIMARY KEY  (analog_type_id)
) TYPE=MyISAM;

--
-- Table structure for table `attribute_table`
--

DROP TABLE IF EXISTS attribute_table;
CREATE TABLE attribute_table (
  attribute int(11) NOT NULL default '0',
  name text,
  on_alarm_color text,
  off_alarm_color text,
  on_summary_color text,
  off_summary_color text,
  on_printer_color text,
  off_printer_color text,
  sound_type int(11) default NULL,
  check_type tinyint(1) default NULL,
  summary_mode int(11) default NULL,
  history_mode int(11) default NULL,
  career_mode int(11) default NULL,
  new_info_mode int(11) default NULL,
  printer_mode tinyint(1) default NULL,
  chatering_timer int(11) default NULL,
  message_mode tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (attribute)
) TYPE=MyISAM;

--
-- Table structure for table `auto_print_param_table`
--

DROP TABLE IF EXISTS auto_print_param_table;
CREATE TABLE auto_print_param_table (
  name varchar(50) NOT NULL default '',
  schedule varchar(25) NOT NULL default '',
  auto_flag tinyint(1) NOT NULL default '0',
  paramdate datetime NOT NULL default '0000-00-00 00:00:00',
  displayname varchar(50) default NULL,
  PRIMARY KEY  (name)
) TYPE=MyISAM;

--
-- Table structure for table `autoprint_property_table`
--

DROP TABLE IF EXISTS autoprint_property_table;
CREATE TABLE autoprint_property_table (
  task_name varchar(50) NOT NULL default '',
  property varchar(50) NOT NULL default '',
  value text,
  PRIMARY KEY  (task_name,property)
) TYPE=MyISAM;

--
-- Table structure for table `career_print_temp_table`
--

DROP TABLE IF EXISTS career_print_temp_table;
CREATE TABLE career_print_temp_table (
  point int(11) NOT NULL default '0',
  provider varchar(50) NOT NULL default '',
  holder varchar(50) NOT NULL default '',
  entrydate datetime NOT NULL default '0000-00-00 00:00:00',
  bit_value tinyint(1) default NULL,
  KEY career_print_temp_table_date_idx (entrydate)
) TYPE=MyISAM;

--
-- Table structure for table `career_printed_table`
--

DROP TABLE IF EXISTS career_printed_table;
CREATE TABLE career_printed_table (
  last_date datetime NOT NULL default '0000-00-00 00:00:00'
) TYPE=MyISAM;

--
-- Table structure for table `career_table`
--

DROP TABLE IF EXISTS career_table;
CREATE TABLE career_table (
  point int(11) NOT NULL default '0',
  provider varchar(50) NOT NULL default '',
  holder varchar(50) NOT NULL default '',
  entrydate datetime NOT NULL default '0000-00-00 00:00:00',
  bit_value tinyint(1) default NULL,
  KEY career_date_idx (entrydate)
) TYPE=MyISAM;

--
-- Table structure for table `device_properties_table`
--

DROP TABLE IF EXISTS device_properties_table;
CREATE TABLE device_properties_table (
  id varchar(50) NOT NULL default '',
  kind text,
  ip text,
  port int(11) default NULL,
  command text,
  net int(11) default NULL,
  node int(11) default NULL,
  unit int(11) default NULL,
  watch_wait int(11) default NULL,
  timeout int(11) default NULL,
  retry_count int(11) default NULL,
  recovery_wait int(11) default NULL,
  host_net int(11) default NULL,
  host_port int(11) default NULL,
  host_ip text,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

--
-- Table structure for table `download_loggingname_table`
--

DROP TABLE IF EXISTS download_loggingname_table;
CREATE TABLE download_loggingname_table (
  logging_table_name varchar(100) NOT NULL default '',
  name varchar(100) NOT NULL default '',
  PRIMARY KEY  (logging_table_name),
  UNIQUE KEY download_loggingname_table_name_idx (name)
) TYPE=MyISAM;

--
-- Table structure for table `email_attribute_setting_table`
--

DROP TABLE IF EXISTS email_attribute_setting_table;
CREATE TABLE email_attribute_setting_table (
  email_attribute_setting_id bigint(20) NOT NULL auto_increment,
  attribute_id int(11) NOT NULL default '0',
  email_group_id int(11) NOT NULL default '0',
  email_address text,
  PRIMARY KEY  (email_attribute_setting_id),
  UNIQUE KEY email_attribute_setting_id (email_attribute_setting_id),
  UNIQUE KEY email_attribute_setting_attribute_idx (attribute_id,email_group_id)
) TYPE=MyISAM;

--
-- Table structure for table `email_group_master_table`
--

DROP TABLE IF EXISTS email_group_master_table;
CREATE TABLE email_group_master_table (
  email_group_id int(11) NOT NULL default '0',
  kind int(11) NOT NULL default '0',
  email_address text,
  PRIMARY KEY  (email_group_id,kind)
) TYPE=MyISAM;

--
-- Table structure for table `email_group_table`
--

DROP TABLE IF EXISTS email_group_table;
CREATE TABLE email_group_table (
  email_group_id int(11) NOT NULL default '0',
  email_address text
) TYPE=MyISAM;

--
-- Table structure for table `email_individual_setting_table`
--

DROP TABLE IF EXISTS email_individual_setting_table;
CREATE TABLE email_individual_setting_table (
  email_individual_setting_id bigint(20) NOT NULL auto_increment,
  provider varchar(100) NOT NULL default '',
  holder varchar(100) NOT NULL default '',
  email_group_id int(11) NOT NULL default '0',
  email_address text,
  PRIMARY KEY  (email_individual_setting_id),
  UNIQUE KEY email_individual_setting_id (email_individual_setting_id),
  UNIQUE KEY email_individual_setting_holder_idx (provider,holder,email_group_id),
  KEY email_individual_setting_email_idx (email_group_id)
) TYPE=MyISAM;

--
-- Table structure for table `group_define_table`
--

DROP TABLE IF EXISTS group_define_table;
CREATE TABLE group_define_table (
  username varchar(100) binary NOT NULL default '',
  groupname varchar(100) binary NOT NULL default '',
  entrydate datetime default NULL,
  renewdate datetime default NULL,
  PRIMARY KEY  (username,groupname)
) TYPE=MyISAM;

--
-- Table structure for table `history_table`
--

DROP TABLE IF EXISTS history_table;
CREATE TABLE history_table (
  point int(11) NOT NULL default '0',
  provider varchar(50) NOT NULL default '',
  holder varchar(50) NOT NULL default '',
  on_date datetime default NULL,
  off_date datetime default NULL,
  check_flag tinyint(1) default NULL,
  KEY history_on_date_idx (point,provider,holder,on_date),
  KEY history_off_date_idx (point,provider,holder,off_date)
) TYPE=MyISAM;

--
-- Table structure for table `item_table`
--

DROP TABLE IF EXISTS item_table;
CREATE TABLE item_table (
  point int(11) NOT NULL default '0',
  provider varchar(50) NOT NULL default '',
  holder varchar(50) NOT NULL default '',
  com_cycle int(11) default NULL,
  com_cycle_mode tinyint(1) default NULL,
  com_memory_kinds int(11) default NULL,
  com_memory_address int(11) default NULL,
  b_flag tinyint(1) default NULL,
  message_id int(11) default NULL,
  attribute_id int(11) default NULL,
  data_type int(11) default NULL,
  data_argv text,
  jump_path text,
  auto_jump_flag tinyint(1) default NULL,
  auto_jump_priority int(11) default NULL,
  on_sound_path text,
  off_sound_path text,
  analog_type_id int(11) default NULL,
  email_group_id int(11) default NULL,
  email_send_mode int(11) default NULL,
  off_delay int(11) default NULL,
  system tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (point,provider,holder),
  KEY item_table_system_idx (system)
) TYPE=MyISAM;

--
-- Table structure for table `message_table`
--

DROP TABLE IF EXISTS message_table;
CREATE TABLE message_table (
  message_id int(11) NOT NULL default '0',
  type tinyint(1) NOT NULL default '0',
  message text,
  PRIMARY KEY  (message_id,type)
) TYPE=MyISAM;

--
-- Table structure for table `multi_record_define_table`
--

DROP TABLE IF EXISTS multi_record_define_table;
CREATE TABLE multi_record_define_table (
  logging_table_name varchar(100) NOT NULL default '',
  provider varchar(100) NOT NULL default '',
  com_memory_kinds int(11) default NULL,
  com_memory_address int(11) default NULL,
  word_length int(11) default NULL,
  record_count int(11) default NULL,
  PRIMARY KEY  (logging_table_name)
) TYPE=MyISAM;

--
-- Table structure for table `operation_logging_table`
--

DROP TABLE IF EXISTS operation_logging_table;
CREATE TABLE operation_logging_table (
  id bigint(20) NOT NULL auto_increment,
  ope_date datetime NOT NULL default '0000-00-00 00:00:00',
  ope_ip varchar(50) NOT NULL default '',
  ope_user varchar(100) NOT NULL default '',
  ope_before_value text NOT NULL,
  ope_after_value text NOT NULL,
  ope_provider varchar(100) NOT NULL default '',
  ope_holder varchar(100) NOT NULL default '',
  PRIMARY KEY  (id),
  UNIQUE KEY id (id),
  KEY operation_logging_table_id_idx (ope_date)
) TYPE=MyISAM;

--
-- Table structure for table `page_define_table`
--

DROP TABLE IF EXISTS page_define_table;
CREATE TABLE page_define_table (
  page_name varchar(50) NOT NULL default '',
  page_xml_path text,
  PRIMARY KEY  (page_name)
) TYPE=MyISAM;

--
-- Table structure for table `point_comment_table`
--

DROP TABLE IF EXISTS point_comment_table;
CREATE TABLE point_comment_table (
  id bigint(20) NOT NULL auto_increment,
  provider varchar(100) NOT NULL default '',
  holder varchar(100) NOT NULL default '',
  comment text,
  PRIMARY KEY  (id),
  UNIQUE KEY id (id),
  UNIQUE KEY point_comment_table_holder_idx (provider,holder)
) TYPE=MyISAM;

--
-- Table structure for table `point_table`
--

DROP TABLE IF EXISTS point_table;
CREATE TABLE point_table (
  point int(11) NOT NULL default '0',
  unit varchar(255) default NULL,
  name varchar(255) default NULL,
  unit_mark varchar(255) default NULL,
  attribute1 varchar(255) NOT NULL default '',
  attribute2 varchar(255) NOT NULL default '',
  attribute3 varchar(255) NOT NULL default '',
  PRIMARY KEY  (point),
  KEY point_table_unit_idx (unit),
  KEY point_table_name_idx (name),
  KEY point_attribute1_idx (attribute1),
  KEY point_attribute2_idx (attribute2),
  KEY point_attribute3_idx (attribute3)
) TYPE=MyISAM;

--
-- Table structure for table `policy_define_table`
--

DROP TABLE IF EXISTS policy_define_table;
CREATE TABLE policy_define_table (
  principal varchar(50) NOT NULL default '',
  name varchar(50) NOT NULL default '',
  permission varchar(50) NOT NULL default '',
  entrydate datetime default NULL,
  renewdate datetime default NULL,
  PRIMARY KEY  (principal,name,permission)
) TYPE=MyISAM;

--
-- Table structure for table `priority_table`
--

DROP TABLE IF EXISTS priority_table;
CREATE TABLE priority_table (
  id int(11) NOT NULL default '0',
  name varchar(255) NOT NULL default '',
  PRIMARY KEY  (id)
) TYPE=MyISAM;

--
-- Table structure for table `schedule_group_table`
--

DROP TABLE IF EXISTS schedule_group_table;
CREATE TABLE schedule_group_table (
  id bigint(20) NOT NULL auto_increment,
  provider varchar(100) NOT NULL default '',
  holder varchar(100) NOT NULL default '',
  group_no int(11) NOT NULL default '0',
  page_id varchar(100) NOT NULL default '',
  PRIMARY KEY  (id),
  UNIQUE KEY id (id),
  KEY schedule_group_table_groupno (group_no),
  KEY schedule_group_table_pageid (page_id)
) TYPE=MyISAM;

--
-- Table structure for table `schedule_point_table`
--

DROP TABLE IF EXISTS schedule_point_table;
CREATE TABLE schedule_point_table (
  id bigint(20) NOT NULL auto_increment,
  provider varchar(100) NOT NULL default '',
  holder varchar(100) NOT NULL default '',
  page_id varchar(100) NOT NULL default '',
  group_no int(11) NOT NULL default '0',
  group_provider varchar(100) NOT NULL default '',
  group_holder varchar(100) NOT NULL default '',
  sort tinyint(1) NOT NULL default '0',
  separate_provider varchar(100) NOT NULL default '',
  separate_holder varchar(100) NOT NULL default '',
  PRIMARY KEY  (id),
  UNIQUE KEY id (id),
  KEY schedule_point_table_groupno (group_no),
  KEY schedule_point_table_pageid (page_id),
  KEY schedule_point_table_provider (provider),
  KEY schedule_point_table_holder (holder)
) TYPE=MyISAM;

--
-- Table structure for table `summary_table`
--

DROP TABLE IF EXISTS summary_table;
CREATE TABLE summary_table (
  point int(11) NOT NULL default '0',
  provider varchar(50) NOT NULL default '',
  holder varchar(50) NOT NULL default '',
  on_date datetime default NULL,
  off_date datetime default NULL,
  bit_value tinyint(1) default NULL,
  PRIMARY KEY  (point,provider,holder),
  KEY summary_on_date_idx (on_date)
) TYPE=MyISAM;

--
-- Table structure for table `tool_policy_define_table`
--

DROP TABLE IF EXISTS tool_policy_define_table;
CREATE TABLE tool_policy_define_table (
  principal varchar(50) NOT NULL default '',
  name varchar(50) NOT NULL default '',
  permission varchar(50) NOT NULL default '',
  entrydate datetime default NULL,
  renewdate datetime default NULL,
  PRIMARY KEY  (principal,name,permission)
) TYPE=MyISAM;

--
-- Table structure for table `user_define_table`
--

DROP TABLE IF EXISTS user_define_table;
CREATE TABLE user_define_table (
  username varchar(100) binary NOT NULL default '',
  password text,
  entrydate datetime default NULL,
  renewdate datetime default NULL,
  PRIMARY KEY  (username)
) TYPE=MyISAM;

