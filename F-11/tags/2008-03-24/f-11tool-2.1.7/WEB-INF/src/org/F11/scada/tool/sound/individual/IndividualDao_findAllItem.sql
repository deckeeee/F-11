SELECT
        i.provider
        ,i.holder
        ,p.point
        ,p.unit
        ,p.name
        ,a.name AS attribute
        ,a.sound_type AS attributetype
        ,ais.type
    FROM
        item_table i LEFT JOIN alarm_individual_setting_table ais
            ON (
            i.provider = ais.provider
            AND i.holder = ais.holder
        ) LEFT JOIN point_table p
            ON (
            p.point = i.point
        ) LEFT JOIN attribute_table a
            ON (
            i.attribute_id = a.attribute
        )
    WHERE
        i.system = '1'
        AND i.data_type = 0
/*IF dto.unit != null*/AND p.unit like /*dto.unit*/'%'  /*END*/
/*IF dto.name != null*/AND p.name like /*dto.name*/'%'  /*END*/
/*IF dto.attribute != null*/AND a.name like /*dto.attribute*/'%'  /*END*/
/*IF dto.attributetype != null*/AND a.sound_type = /*dto.attributetype*/'%'  /*END*/
/*IF dto.individualtype != null*/AND type = /*dto.individualtype*/'%' /*END*/
    ORDER BY
        i.provider
        ,i.holder
