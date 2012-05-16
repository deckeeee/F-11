SELECT
        sp.id AS id
        ,sp.group_no AS groupNo
        ,CASE
            WHEN sp.group_no = 0
            THEN '未割付'
            WHEN sp.group_no = 2147483647
            THEN '通信エラー'
            ELSE gp.name
        END AS groupName
        ,p.unit AS unit
        ,p.name AS name
        ,sp.sort AS sort
        ,sp.group_provider AS groupNoProvider
        ,sp.group_holder AS groupNoHolder
        ,sp.separate_provider AS separateProvider
        ,sp.separate_holder AS separateHolder
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
/*IF dto.pageId != null*/AND sp.page_id = /*dto.pageId*/'schedule01'/*END*/
/*IF dto.groupNo != null*/AND sp.group_no = /*dto.groupNo*/0/*END*/
    ORDER BY
        groupNo
        ,unit
        ,name