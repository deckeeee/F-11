SELECT
        id
        ,group_no AS groupNo
        ,CASE
            WHEN group_no = 0
            THEN '–¢Š„•t'
            ELSE p.name
        END AS groupName
        ,gt.provider
        ,gt.holder
        ,gt.page_id
    FROM
        schedule_group_table gt LEFT JOIN item_table i
            ON gt.provider = i.provider
        AND gt.holder = i.holder LEFT JOIN point_table p
            ON i.point = p.point
    WHERE
/*IF dto.pageId != null*/gt.page_id = /*dto.pageId*/'schedule01'/*END*/
    ORDER BY
        groupNo