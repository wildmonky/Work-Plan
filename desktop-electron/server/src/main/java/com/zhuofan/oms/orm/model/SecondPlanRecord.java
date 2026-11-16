package com.zhuofan.oms.orm.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

import java.time.LocalDateTime;

@TableName(value = "SECOND_PLAN_RECORD", excludeProperty = {"changeInfo", "extraInfo", "extraInfo1"})
@Table(tableName = "SECOND_PLAN_RECORD")
public class SecondPlanRecord extends BaseModel {

    @ExcelProperty("二次数量")
    @TableField(name = "NUM", type = TableField.TF_NUMBER)
    private Short num;

    @ExcelProperty("主调作业数量")
    @TableField(name = "MAIN_NUM", type = TableField.TF_NUMBER)
    private Short mainNum;

    @ExcelProperty("配调作业数量")
    @TableField(name = "SUB_NUM", type = TableField.TF_NUMBER)
    private Short subNum;

    @ExcelProperty("日期")
    @DateTimeFormat("M月d日")
    @TableId
    @TableField(name = "TIME", type = TableField.TF_LOCAL_DATE_TIME, isPrimaryKey = true)
    private LocalDateTime time;

    public Short getNum() {
        return num;
    }

    public void setNum(short num) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.num = num;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.time = time;
    }

    public Short getMainNum() {
        return mainNum;
    }

    public void setMainNum(Short mainNUm) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.mainNum = mainNUm;
    }

    public Short getSubNum() {
        return subNum;
    }

    public void setSubNum(Short subNum) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.subNum = subNum;
    }
}
