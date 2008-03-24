SELECT a.alarm_email_sent_id, a.provider, a.holder, a.sentdate, a.value, p.unit, p.name, m.message
FROM alarm_email_sent_table a, item_table i, message_table m, point_table p
WHERE a.provider = i.provider AND a.holder = i.holder
AND i.message_id = m.message_id AND a.value = m.type AND i.point = p.point
ORDER BY a.sentdate DESC;