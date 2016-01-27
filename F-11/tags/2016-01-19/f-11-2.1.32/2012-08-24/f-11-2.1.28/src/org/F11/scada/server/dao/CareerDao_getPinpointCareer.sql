SELECT
        i.jump_path
        ,CASE
            WHEN c.bit_value = '1'
            THEN i.auto_jump_flag
            ELSE '0'
        END AS auto_jump_flag
        ,i.auto_jump_priority
        ,CASE
            WHEN c.bit_value = '1'
            THEN att.on_alarm_color
            ELSE att.off_alarm_color
        END AS color
        ,i.point
        ,i.provider
        ,i.holder
        ,att.sound_type
        ,CASE
            WHEN c.bit_value = '1'
            THEN i.on_sound_path
            ELSE i.off_sound_path
        END AS sound_path
        ,i.email_group_id
        ,i.email_send_mode
        ,c.bit_value AS onoff
        ,c.entrydate
        ,p.unit
        ,p.name AS name
        ,CASE
            WHEN att.message_mode
            THEN att.name
            ELSE NULL
        END AS attributeName
        ,m.message
        ,pri.name AS priorityname
        ,att.new_info_mode
    FROM
        point_table p
        ,career_table c
        ,item_table i LEFT JOIN priority_table pri
            ON i.auto_jump_priority = pri.id
        ,message_table m
        ,attribute_table att
    WHERE
        p.point = i.point
        AND c.point = i.point
        AND c.provider = i.provider
        AND c.holder = i.holder
        AND i.message_id = m.message_id
        AND c.bit_value = m.type
        AND i.attribute_id = att.attribute
        AND (
            (
                att.career_mode = 1
                AND c.bit_value = '0'
            )
            OR (
                att.career_mode = 2
                AND c.bit_value = '1'
            )
            OR (
                att.career_mode = 3
            )
            OR (
                att.career_mode = 4
                AND c.bit_value = '1'
            )
            OR (
                att.career_mode = 5
                AND c.bit_value = '0'
            )
        )
        AND
        	(
        	    /*$holders*/'hol'
        	)
    ORDER BY
        entrydate DESC LIMIT /*$limit*/50;
