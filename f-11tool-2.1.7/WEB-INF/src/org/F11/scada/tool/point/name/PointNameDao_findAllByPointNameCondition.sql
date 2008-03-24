SELECT
	p.point
	, p.unit
	, p.name
	, p.unit_mark
	, CASE WHEN COUNT(i.point) > 0 THEN '1' ELSE '0' END AS used
	, p.attribute1
	, p.attribute2
	, p.attribute3
FROM
	point_table p LEFT JOIN item_table i ON p.point = i.point
/*BEGIN*/WHERE
	/*IF condition.unit != null*/unit like /*condition.unit*/'%AHU%'  /*END*/
	/*IF condition.name != null*/AND name like /*condition.name*/'%1F%'  /*END*/
	/*IF condition.unitMark != null*/AND unit_mark like /*condition.unitMark*/'%mark%'  /*END*/
	/*IF condition.attribute1 != null*/AND attribute1 like /*condition.attribute1*/'%ŒyŒx•ñ%'  /*END*/
	/*IF condition.attribute2 != null*/AND attribute2 like /*condition.attribute2*/'%ŒyŒx•ñ%'  /*END*/
	/*IF condition.attribute3 != null*/AND attribute3 like /*condition.attribute3*/'%ŒyŒx•ñ%'  /*END*/
/*END*/
GROUP BY
	p.point
	, p.unit
	, p.name
	, p.unit_mark
	, p.attribute1
	, p.attribute2
	, p.attribute3
ORDER BY
	p.point