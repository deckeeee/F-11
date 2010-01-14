SELECT
        sp.id AS id
        ,sp.group_no AS groupNo
        ,CASE
            WHEN sp.group_no = 0
            THEN '–¢Š„•t'
            ELSE gp.name
        END AS groupName
        ,p.unit AS unit
        ,p.name AS name
        ,sp.sort AS sort
        ,sp.group_provider AS groupNoProvider
        ,sp.group_holder AS groupNoHolder
    FROM
        schedule_point_table sp LEFT JOIN item_table i
            ON sp.provider = i.provider
        AND sp.holder = i.holder LEFT JOIN point_table p
            ON i.point = p.point
        ,schedule_group_table sg LEFT JOIN item_table gi
            ON sg.provider = gi.provider
        AND sg.holder = gi.holder LEFT JOIN point_table gp
            ON gi.point = gp.point
    WHERE
        sp.group_no = sg.group_no
        AND sp.page_id = sg.page_id
    ORDER BY
        groupNo
        ,unit
        ,name