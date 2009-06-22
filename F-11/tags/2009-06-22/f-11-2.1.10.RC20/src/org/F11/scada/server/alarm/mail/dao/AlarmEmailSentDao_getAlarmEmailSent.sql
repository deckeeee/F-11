SELECT alarm_email_sent_id, provider, holder, sentdate, value
FROM alarm_email_sent_table
WHERE provider=/*dto.provider*/'P1' AND holder=/*dto.holder*/'H1'
AND sentdate=/*dto.sentdate*/'2006/01/01 00:00:00'
AND value=/*dto.value*/'1';