SELECT
 p.point
 ,p.unit
 ,p.name
 ,p.unit_mark
 ,p.attribute1
 ,p.attribute2
 ,p.attribute3
 ,i.provider
 ,i.holder
 ,a.convert_min min
 ,a.convert_max max
 ,a.format
 ,a.convert_type convert
FROM
 item_table i
  LEFT JOIN point_table p ON i.point = p.point
   LEFT JOIN analog_type_table a ON i.analog_type_id = a.analog_type_id
WHERE
 i.data_type IN(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)
 AND i.holder != 'ERR_HOLDER'
 AND /*$holders*/i.provider='P1'
  /*IF dto.unit != null*/AND p.unit LIKE /*dto.unit*/'CV'/*END*/
 /*IF dto.unitMark != null*/AND p.unit_mark LIKE /*dto.unitMark*/'CV'/*END*/
 /*IF dto.name != null*/AND p.name LIKE /*dto.name*/'CV'/*END*/
ORDER BY
 i.provider
 ,i.holder
