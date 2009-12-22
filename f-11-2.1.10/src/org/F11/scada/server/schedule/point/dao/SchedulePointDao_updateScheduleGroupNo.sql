UPDATE
        schedule_point_table
    SET
        group_no = /*dto.groupNo*/1
    WHERE
        group_provider = /*dto.groupNoProvider*/'P1'
        AND group_holder = /*dto.groupNoHolder*/'H1'
