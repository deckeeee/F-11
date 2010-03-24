UPDATE
  item_table i
    JOIN attribute_table a ON i.attribute_id = a.attribute
    JOIN history_table h ON i.point = h.point AND i.provider = h.provider AND i.holder = h.holder 
SET
  h.check_flag = '1'
WHERE
  h.check_flag IS NULL
  AND a.check_type = '1';
