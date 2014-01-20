@ECHO OFF
IF "%1"=="" goto ERROR
mysqldump -q -u root -pfreedom9713 minami_senba alarm_email_sent_addresses_table > %1.sql
mysqldump -q -u root -pfreedom9713 %1 alarm_email_sent_table >> %1.sql
mysqldump -q -u root -pfreedom9713 --no-data %1 alarm_individual_setting_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 analog_type_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 attribute_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 auto_print_param_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 autoprint_property_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 career_print_temp_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 career_printed_table >> %1.sql
mysqldump -q -u root -pfreedom9713 --no-data %1 career_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 device_properties_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 download_loggingname_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 email_attribute_setting_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 email_group_master_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 email_group_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 email_individual_setting_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 group_define_table >> %1.sql
mysqldump -q -u root -pfreedom9713 --no-data %1 history_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 item_formula_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 item_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 message_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 multi_record_define_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 operation_logging_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 page_define_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 point_comment_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 point_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 policy_define_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 priority_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 schedule_group_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 schedule_point_table >> %1.sql
mysqldump -q -u root -pfreedom9713 --no-data %1 summary_table >> %1.sql
mysqldump -q -u root -pfreedom9713 --no-data %1 system_summary_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 tool_policy_define_table >> %1.sql
mysqldump -q -u root -pfreedom9713 %1 user_define_table >> %1.sql
exit /b

rem 引数が指定されていない場合
:ERROR
echo Usage : backup [データベース名]
exit /b
