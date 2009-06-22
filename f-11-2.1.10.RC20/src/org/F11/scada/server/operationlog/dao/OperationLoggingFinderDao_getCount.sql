SELECT COUNT(o.id) FROM operation_logging_table o LEFT JOIN item_table i ON o.ope_provider = i.provider AND o.ope_holder = i.holder LEFT JOIN point_table p ON i.point = p.point LEFT JOIN message_table m ON m.message_id = i.message_id AND m.type = '0' 
/*BEGIN*/WHERE
 /*IF finder.startDate != null*/o.ope_date >= /*finder.startDate*/'2005/07/05 00:00:00'/*END*/
 /*IF finder.endDate != null*/AND o.ope_date <= /*finder.endDate*/'2006/07/05 23:59:59'/*END*/
 /*IF finder.opeUser != null*/AND o.ope_user = /*finder.opeUser*/'user1'/*END*/
 /*IF finder.opeIp != null*/AND o.ope_ip = /*finder.opeIp*/'192.168.0.123'/*END*/
 /*IF finder.opeName != null*/AND p.name LIKE /*finder.opeName*/'�d�͎g�p��'/*END*/
 /*IF finder.opeMessage != null*/AND m.message LIKE /*finder.opeMessage*/'����'/*END*/
/*END*/;