-- MySQL dump 9.11
--
-- Host: localhost    Database: wifedb
-- ------------------------------------------------------
-- Server version	4.0.27-nt

--
-- Table structure for table `log_table_month`
--

CREATE TABLE log_table_weekly (
  f_date datetime NOT NULL default '0000-00-00 00:00:00',
  f_revision int(11) NOT NULL default '0',
  f_P1_D_1900000_Digital tinyint(1) default NULL,
  f_P1_D_500_BcdSingle double default NULL,
  f_P1_D_501_BcdSingle double default NULL,
  f_P1_D_502_BcdSingle double default NULL,
  PRIMARY KEY  (f_date,f_revision)
) TYPE=MyISAM;

--
-- Dumping data for table `log_table_month`
--

INSERT INTO log_table_weekly VALUES ('2012-05-20 01:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 02:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 03:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 04:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 05:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 06:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 07:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 08:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 09:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 10:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 11:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 12:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 13:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 14:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 15:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 16:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 17:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 18:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 19:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 20:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 21:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 22:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-20 23:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 00:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 01:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 02:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 03:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 04:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 05:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 06:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 07:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 08:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 09:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 10:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 11:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 12:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 13:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 14:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 15:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 16:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 17:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 18:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 19:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 20:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 21:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 22:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-21 23:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 00:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 01:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 02:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 03:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 04:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 05:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 06:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 07:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 08:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 09:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 10:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 11:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 12:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 13:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 14:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 15:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 16:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 17:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 18:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 19:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 20:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 21:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 22:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-22 23:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 00:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 01:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 02:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 03:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 04:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 05:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 06:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 07:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 08:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 09:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 10:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 11:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 12:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 13:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 14:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 15:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 16:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 17:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 18:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 19:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 20:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 21:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 22:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-23 23:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 00:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 01:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 02:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 03:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 04:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 05:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 06:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 07:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 08:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 09:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 10:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 11:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 12:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 13:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 14:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 15:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 16:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 17:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 18:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 19:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 20:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 21:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 22:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-24 23:01:00',0,0,0,0,0);
INSERT INTO log_table_weekly VALUES ('2012-05-25 00:01:00',0,0,0,0,0);
