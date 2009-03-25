SELECT
 point
 ,unit
 ,name
 ,unit_mark
 ,attribute1
 ,attribute2
 ,attribute3
FROM
 point_table
/*BEGIN*/WHERE
  /*IF dto.unit != null*/unit LIKE /*dto.unit*/'CV'/*END*/
  /*IF dto.unitMark != null*/AND unit_mark LIKE /*dto.unitMark*/'CV'/*END*/
  /*IF dto.name != null*/AND name LIKE /*dto.name*/'CV'/*END*/
/*END*/
