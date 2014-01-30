-- MySQL dump 9.11
--
-- Host: localhost    Database: wifedb
-- ------------------------------------------------------
-- Server version	4.0.27-nt

--
-- Table structure for table `log_table_thirtyminute`
--

CREATE TABLE log_table_thirtyminute (
  f_date datetime NOT NULL default '0000-00-00 00:00:00',
  f_revision int(11) NOT NULL default '0',
  f_P1_D_500_BcdSingle double default NULL,
  f_P1_D_501_BcdSingle double default NULL,
  f_P1_D_502_BcdSingle double default NULL,
  f_P1_D_503_BcdSingle double default NULL,
  PRIMARY KEY  (f_date,f_revision)
) TYPE=MyISAM;

--
-- Dumping data for table `log_table_thirtyminute`
--

INSERT INTO log_table_thirtyminute VALUES ('2012-10-01 10:30:00',0,0,0,0,0);

