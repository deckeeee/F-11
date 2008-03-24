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
        i.attribute_id = /*attribute*/0
    ORDER BY
        i.provider
        ,i.holder