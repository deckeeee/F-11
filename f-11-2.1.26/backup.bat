@ECHO OFF
REM F-11データベースバックアップバッチファイル

IF "%1"=="" goto ERROR
IF "%2"=="" goto ERROR
IF "%3"=="" goto ERROR
IF "%4"=="" goto ERROR
mysqldump -q -u %1 -p%2 -h %3 %4 alarm_email_sent_addresses_table > %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 alarm_email_sent_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 --no-data %4 alarm_individual_setting_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 analog_type_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 attribute_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 auto_print_param_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 autoprint_property_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 career_print_temp_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 career_printed_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 --no-data %4 career_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 device_properties_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 download_loggingname_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 email_attribute_setting_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 email_group_master_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 email_group_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 email_individual_setting_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 group_define_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 --no-data %4 history_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 item_formula_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 item_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 message_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 multi_record_define_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 --no-data %4 operation_logging_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 page_define_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 point_comment_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 point_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 policy_define_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 priority_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 schedule_group_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 schedule_point_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 --no-data %4 summary_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 --no-data %4 system_summary_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 tool_policy_define_table >> %4.sql
mysqldump -q -u %1 -p%2 -h %3 %4 user_define_table >> %4.sql
exit /b

rem 引数が指定されていない場合
:ERROR
echo Usage : backup [ユーザー] [パスワード] [ホストIP] [データベース名]
exit /b
