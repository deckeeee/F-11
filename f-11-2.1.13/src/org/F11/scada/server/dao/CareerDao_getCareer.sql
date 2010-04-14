SELECT
  c.entrydate
  ,p.unit
  ,p.name
  ,CASE WHEN att.message_mode THEN att.name ELSE NULL END AS attributeName
  ,m.message
  ,pri.name AS priorityName
FROM
  point_table p
  ,career_table c
  ,item_table i LEFT JOIN priority_table pri ON i.auto_jump_priority = pri.id,message_table m,attribute_table att
WHERE
  p.point=i.point
  AND c.point=i.point
  AND c.provider=i.provider
  AND c.holder=i.holder
  AND i.message_id=m.message_id
  AND c.bit_value=m.type
  AND i.attribute_id=att.attribute
  AND ((att.career_mode=1 AND c.bit_value='0')
    OR (att.career_mode=2 AND c.bit_value='1')
    OR (att.career_mode=3)
    OR (att.career_mode=4 AND c.bit_value='1')
    OR (att.career_mode=5 AND c.bit_value='0'))
  AND c.entrydate BETWEEN /*startdate*/'2009-02-17 00:00:00' AND /*enddate*/'2009-02-17 23:59:59'
ORDER BY entrydate DESC
