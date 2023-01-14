package com.ndz.wheatmall.dto.org;

import com.ndz.wheatmall.annotation.History;
import com.ndz.wheatmall.annotation.HistoryRecord;

import lombok.Data;

import java.time.LocalDateTime;

@Data
@History(table = "employee")
public class EmployeeDTO {

    private String empId;
    @HistoryRecord(field = "姓名")
    private String name;
    @HistoryRecord(field = "工作")
    private String job;
    @HistoryRecord(field = "部门")
    private String department;
    @HistoryRecord(field = "入职日期")
    private LocalDateTime entryDateTime;
}
