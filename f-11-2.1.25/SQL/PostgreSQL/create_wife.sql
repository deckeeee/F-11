/* テーブル定義の詳細はDb_OuterDesign.xlsを参照 */

/* サマリ定義 */

CREATE TABLE summary_table (
	point INTEGER NOT NULL,
	provider VARCHAR(100) NOT NULL,
	holder VARCHAR(100) NOT NULL,
	on_date TIMESTAMP,
	off_date TIMESTAMP,
	bit_value BOOL,
	PRIMARY KEY (point, provider, holder)
);
/* サマリインデックス定義 */
CREATE INDEX summary_on_date_idx ON summary_table (on_date);

/* ヒストリ定義 */
CREATE TABLE history_table (
	point INTEGER NOT NULL,
	provider VARCHAR(100) NOT NULL,
	holder VARCHAR(100) NOT NULL,
	on_date TIMESTAMP,
	off_date TIMESTAMP,
	check_flag BOOL
);
/* ヒストリインデックス定義 */
CREATE INDEX history_on_date_idx ON history_table (point, provider, holder, on_date);
CREATE INDEX history_off_date_idx ON history_table (point, provider, holder, off_date);
CREATE INDEX history_pri_idx ON history_table (point, provider, holder, on_date, off_date, check_flag);
CREATE INDEX history_on_date2_idx ON history_table (on_date);


/* 履歴定義 */
CREATE TABLE career_table (
	point INTEGER NOT NULL,
	provider VARCHAR(100) NOT NULL,
	holder VARCHAR(100) NOT NULL,
	entrydate TIMESTAMP NOT NULL,
	bit_value BOOL
);
/* 履歴インデックス定義 */
CREATE INDEX career_date_idx ON career_table (entrydate);

/* ポイント定義 */
CREATE TABLE point_table (
	point INTEGER NOT NULL,
	unit text,
	name text,
	unit_mark text,
	attribute1 varchar(255) NOT NULL,
	attribute2 varchar(255) NOT NULL,
	attribute3 varchar(255) NOT NULL,
	point_flag BOOL NOT NULL default '1',
	unit_flag BOOL NOT NULL default '1',
	name_flag BOOL NOT NULL default '1',
	unit_mark_flag BOOL NOT NULL default '1',
	attribute1_flag BOOL NOT NULL default '1',
	attribute2_flag BOOL NOT NULL default '1',
	attribute3_flag BOOL NOT NULL default '1',
	PRIMARY KEY (point)
);
CREATE INDEX point_attribute1_idx ON point_table(attribute1);
CREATE INDEX point_attribute2_idx ON point_table(attribute2);
CREATE INDEX point_attribute3_idx ON point_table(attribute3);

/* ポイントアイテム定義 */
CREATE TABLE item_table (
	point INTEGER NOT NULL,
	provider VARCHAR(100) NOT NULL,
	holder VARCHAR(100) NOT NULL,
	com_cycle INTEGER,
	com_cycle_mode BOOL,
	com_memory_kinds INTEGER,
	com_memory_address INTEGER,
	b_flag BOOL,
	message_id INTEGER,
	attribute_id INTEGER,
	data_type INTEGER,
	data_argv text,
	jump_path text,
	auto_jump_flag BOOL,
	auto_jump_priority INTEGER,
	on_sound_path text,
	off_sound_path text,
	analog_type_id INTEGER,
	email_group_id INTEGER,
	email_send_mode INTEGER,
	off_delay INTEGER,
	system BOOL NOT NULL DEFAULT '0',
	PRIMARY KEY (point, provider, holder)
);

CREATE INDEX item_table_finder_idx ON item_table (provider, holder);
CREATE INDEX item_attribute_id_idx ON item_table (attribute_id);

/* 表示メッセージ定義 */
CREATE TABLE message_table (
	message_id INTEGER NOT NULL,
	type BOOL NOT NULL,
	message text,
	PRIMARY KEY (message_id, type)
);

/* 表示属性定義 */
CREATE TABLE attribute_table (
	attribute INTEGER NOT NULL,
	name text,
	on_alarm_color text,
	off_alarm_color text,
	on_summary_color text,
	off_summary_color text,
	on_printer_color text,
	off_printer_color text,
	sound_type INTEGER,
	check_type BOOL,
	summary_mode INTEGER,
	history_mode INTEGER,
	career_mode INTEGER,
	new_info_mode INTEGER,
	printer_mode BOOL,
	chatering_timer INTEGER,
	message_mode BOOL DEFAULT true NOT NULL,
	PRIMARY KEY (attribute)
);

/* 色情報定義 */
CREATE TABLE color_table (
	name VARCHAR(100) NOT NULL,
	rgb text,
	jp_read text,
	jp_mean text,
	PRIMARY KEY (name)
);

/* アナログタイプ */
CREATE TABLE analog_type_table (
	analog_type_id INTEGER NOT NULL,
	analog_type_name text,
	convert_min double precision,
	convert_max double precision,
	input_min double precision,
	input_max double precision,
	format text,
	convert_type text,
	PRIMARY KEY (analog_type_id)
);

/* デバイスプロパティ */
CREATE TABLE device_properties_table (
	id VARCHAR(100) NOT NULL,
	kind text,
	ip text,
	port INTEGER,
	command text,
	net INTEGER,
	node INTEGER,
	unit INTEGER,
	watch_wait INTEGER,
	timeout INTEGER,
	retry_count INTEGER,
	recovery_wait INTEGER,
	host_net INTEGER,
	host_port INTEGER,
	host_ip text,
	ip2 text,
	PRIMARY KEY (id)
);

CREATE TABLE user_define_table (
	username VARCHAR(100) NOT NULL,
	password text,
	entrydate TIMESTAMP,
	renewdate TIMESTAMP,
	PRIMARY KEY (username)
);

CREATE TABLE group_define_table (
	username VARCHAR(100) NOT NULL,
	groupname VARCHAR(100) NOT NULL,
	entrydate TIMESTAMP,
	renewdate TIMESTAMP,
	PRIMARY KEY (username, groupname)
);

CREATE TABLE policy_define_table (
	principal VARCHAR(100) NOT NULL,
	name VARCHAR(100) NOT NULL,
	permission VARCHAR(100) NOT NULL,
	entrydate TIMESTAMP,
	renewdate TIMESTAMP,
	PRIMARY KEY (principal, name, permission)
);
CREATE INDEX policy_define_table_name_idx ON policy_define_table (name);

/* E_Mail送信先グループ */
CREATE TABLE email_group_table (
	email_group_id INTEGER NOT NULL,
	email_address text
);

/* 履歴印字記憶テーブル */
CREATE TABLE career_printed_table (
	last_date TIMESTAMP NOT NULL
);

/* ツールポリシー */
CREATE TABLE tool_policy_define_table (
	principal VARCHAR(100) NOT NULL,
	name VARCHAR(100) NOT NULL,
	permission VARCHAR(100) NOT NULL,
	entrydate TIMESTAMP,
	renewdate TIMESTAMP,
	PRIMARY KEY (principal, name, permission)
);

/* 自動印字パラメータ */
CREATE TABLE auto_print_param_table (
	name VARCHAR(100) NOT NULL,
	schedule VARCHAR(50) NOT NULL,
	auto_flag BOOL NOT NULL,
	paramdate TIMESTAMP NOT NULL,
	displayname VARCHAR(100),
	starthour INTEGER NOT NULL default '-1',
	startminute INTEGER NOT NULL default '-1',
	PRIMARY KEY (name)
);

/*
ページ定義XMLファイルパステーブル
*/
CREATE TABLE page_define_table (
	page_name VARCHAR(100) NOT NULL,
	page_xml_path text,
	PRIMARY KEY (page_name)
);

/*
自動印刷プロパティテーブル
*/
CREATE TABLE autoprint_property_table (
	task_name VARCHAR(100) NOT NULL,
	property VARCHAR(100) NOT NULL,
	value text,
	PRIMARY KEY (task_name, property)
);


/* メッセージ印刷一時テーブル */
CREATE TABLE career_print_temp_table (
	printid SERIAL,
	point INTEGER NOT NULL,
	provider VARCHAR(100) NOT NULL,
	holder VARCHAR(100) NOT NULL,
	entrydate TIMESTAMP NOT NULL,
	bit_value BOOL,
	PRIMARY KEY (printid)
);
/* 履歴インデックス定義 */
CREATE INDEX career_print_temp_table_date_idx ON career_print_temp_table (entrydate);

--  操作ログテーブル
CREATE TABLE operation_logging_table (
	id SERIAL,
	ope_date TIMESTAMP NOT NULL,
	ope_ip VARCHAR(50) NOT NULL,
	ope_user VARCHAR(100) NOT NULL,
	ope_before_value text NOT NULL,
	ope_after_value text NOT NULL,
	ope_provider VARCHAR(100) NOT NULL,
	ope_holder VARCHAR(100) NOT NULL,
	PRIMARY KEY (id)
);
--  操作ログインデックス定義
CREATE INDEX operation_logging_table_id_idx ON operation_logging_table (ope_date);

--  バージョンアップで操作ログを追加した場合は、必ず以下のインデックスを作成してください。
--  CREATE INDEX item_table_finder_idx ON item_table (provider, holder);

--  多レコードロギング定義
CREATE TABLE multi_record_define_table (
	logging_table_name VARCHAR(100) NOT NULL,
	provider VARCHAR(100) NOT NULL,
	com_memory_kinds INTEGER,
	com_memory_address INTEGER,
	word_length INTEGER,
	record_count INTEGER,
	PRIMARY KEY (logging_table_name)
);

--  ポイントコメント
CREATE TABLE point_comment_table (
	id SERIAL,
	provider VARCHAR(100) NOT NULL,
	holder VARCHAR(100) NOT NULL,
	comment text,
	PRIMARY KEY (id)
);
CREATE UNIQUE INDEX point_comment_table_holder_idx ON point_comment_table (provider, holder);

--  E_Mail送信先グループマスター
CREATE TABLE email_group_master_table (
	email_group_id INTEGER NOT NULL,
	kind INTEGER NOT NULL,
	email_address text,
	PRIMARY KEY (email_group_id, kind)
);


-- E_Mail 属性別設定テーブル
CREATE TABLE email_attribute_setting_table (
	email_attribute_setting_id SERIAL,
	attribute_id INTEGER NOT NULL,
	email_group_id INTEGER NOT NULL,
	email_address text,
	PRIMARY KEY (email_attribute_setting_id)
);
CREATE UNIQUE INDEX email_attribute_setting_attribute_idx ON email_attribute_setting_table (attribute_id, email_group_id);

-- E_Mail 個別設定テーブル
CREATE TABLE email_individual_setting_table (
	email_individual_setting_id SERIAL,
	provider VARCHAR(100) NOT NULL,
	holder VARCHAR(100) NOT NULL,
	email_group_id INTEGER NOT NULL,
	email_address text,
	PRIMARY KEY (email_individual_setting_id)
);
CREATE UNIQUE INDEX email_individual_setting_holder_idx ON email_individual_setting_table (provider, holder, email_group_id);
CREATE INDEX email_individual_setting_email_idx ON email_individual_setting_table (email_group_id);

-- ダウンロード対象ロギングテーブル名称テーブル
CREATE TABLE download_loggingname_table (
	logging_table_name VARCHAR(100) NOT NULL,
	name VARCHAR(100) NOT NULL,
	PRIMARY KEY (logging_table_name)
);
CREATE UNIQUE INDEX download_loggingname_table_name_idx ON download_loggingname_table (name);

-- 警報音個別設定テーブル
CREATE TABLE alarm_individual_setting_table (
	alarm_individual_setting_id SERIAL,
	provider VARCHAR(100) NOT NULL,
	holder VARCHAR(100) NOT NULL,
	type INTEGER NOT NULL,
	PRIMARY KEY (alarm_individual_setting_id)
);
CREATE UNIQUE INDEX alarm_individual_setting_provider_idx ON alarm_individual_setting_table (provider, holder);
CREATE INDEX item_table_system_idx ON item_table (system);

-- 警報メール送信テーブル
CREATE TABLE alarm_email_sent_table (
	alarm_email_sent_id SERIAL,
	provider VARCHAR(100) NOT NULL,
	holder VARCHAR(100) NOT NULL,
	sentdate TIMESTAMP NOT NULL,
	value BOOLEAN NOT NULL,
	PRIMARY KEY (alarm_email_sent_id)
);
CREATE INDEX alarm_email_sent_provider_idx ON alarm_email_sent_table (provider, holder);
CREATE INDEX alarm_email_sent_sentdate_idx ON alarm_email_sent_table (sentdate);

-- 警報メールアドレステーブル
CREATE TABLE alarm_email_sent_addresses_table (
	alarm_email_sent_addresses_id SERIAL,
	alarm_email_sent_id bigint NOT NULL,
	address VARCHAR(255) NOT NULL,
	PRIMARY KEY (alarm_email_sent_addresses_id)
);
CREATE INDEX alarm_email_sent_address_id_idx ON alarm_email_sent_addresses_table (alarm_email_sent_id);
CREATE INDEX alarm_email_sent_address_idx ON alarm_email_sent_addresses_table (address);

-- プライオリティ名テーブル
CREATE TABLE priority_table (
	id INTEGER NOT NULL
	, name VARCHAR(255) NOT NULL
	, PRIMARY KEY (id)
);
INSERT INTO priority_table VALUES(0, '');

-- 機器一覧対象ポイント
CREATE TABLE schedule_point_table (
	id SERIAL,
	provider VARCHAR(100) NOT NULL,
	holder VARCHAR(100) NOT NULL,
	page_id VARCHAR(100) NOT NULL,
	group_no INTEGER NOT NULL,
	group_provider VARCHAR(100) NOT NULL,
	group_holder VARCHAR(100) NOT NULL,
	sort BOOL NOT NULL,
	separate_provider VARCHAR(100) NOT NULL,
	separate_holder VARCHAR(100) NOT NULL,
	PRIMARY KEY (id)
);
CREATE INDEX schedule_point_table_groupno ON schedule_point_table (group_no);
CREATE INDEX schedule_point_table_pageid ON schedule_point_table (page_id);
CREATE INDEX schedule_point_table_provider ON schedule_point_table (provider);
CREATE INDEX schedule_point_table_holder ON schedule_point_table (holder);

-- スケジュールグループ
CREATE TABLE schedule_group_table (
	id SERIAL,
	provider VARCHAR(100) NOT NULL,
	holder VARCHAR(100) NOT NULL,
	page_id VARCHAR(100) NOT NULL,
	group_no INTEGER NOT NULL,
	PRIMARY KEY (id)
);
CREATE INDEX schedule_group_table_groupno ON schedule_group_table (group_no);
CREATE INDEX schedule_group_table_pageid ON schedule_group_table (page_id);

-- 無通信プロバイダ アイテム計算式テーブル
CREATE TABLE item_formula_table (
  id SERIAL,
  holder VARCHAR(100) NOT NULL,
  formula text,
  PRIMARY KEY (id)
);
CREATE INDEX item_formula_table_holder ON item_formula_table (holder);

-- システムサマリ
CREATE TABLE system_summary_table (
	point INTEGER NOT NULL,
	provider VARCHAR(100) NOT NULL,
	holder VARCHAR(100) NOT NULL,
	on_date DATETIME,
	off_date DATETIME,
	bit_value boolean,
	PRIMARY KEY (point, provider, holder)
);
/* サマリインデックス定義 */
CREATE INDEX system_summary_on_date_idx ON system_summary_table (on_date);
