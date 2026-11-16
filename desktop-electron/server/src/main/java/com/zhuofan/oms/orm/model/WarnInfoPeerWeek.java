package com.zhuofan.oms.orm.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.core.orm.TableField;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

@TableName(value = "WARN_INFO_PEER_WEEK", excludeProperty = {"changeInfo", "extraInfo", "extraInfo1"})
@Table(tableName = "WARN_INFO_PEER_WEEK")
public class WarnInfoPeerWeek extends BaseModel {

    @TableId(type = IdType.AUTO)
    @TableField(name = "ID", isPrimaryKey = true, idType = "AUTO")
    private Long id;

    // 一级风险数量
    @TableField(name = "FIRST_LEVEL_NUM")
    private String firstLevelNum;

    // 二级风险数量
    @TableField(name = "SECOND_LEVEL_NUM")
    private String secondLevelNum;

    // 三级风险数量
    @TableField(name = "THIRD_LEVEL_NUM")
    private String thirdLevelNum;

    // 四级风险数量
    @TableField(name = "FOURTH_LEVEL_NUM")
    private String fourthLevelNum;

    // 时间
    @TableField(name = "TIME")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime time;

    public String getFirstLevelNum() {
        return firstLevelNum;
    }

    public void setFirstLevelNum(String firstLevelNum) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.firstLevelNum = firstLevelNum;
    }

    public String getSecondLevelNum() {
        return secondLevelNum;
    }

    public void setSecondLevelNum(String secondLevelNum) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.secondLevelNum = secondLevelNum;
    }

    public String getThirdLevelNum() {
        return thirdLevelNum;
    }

    public void setThirdLevelNum(String thirdLevelNum) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.thirdLevelNum = thirdLevelNum;
    }

    public String getFourthLevelNum() {
        return fourthLevelNum;
    }

    public void setFourthLevelNum(String fourthLevelNum) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.fourthLevelNum = fourthLevelNum;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(String time) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());

        try {
            this.time = LocalDateTime.parse(time, new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd HH:mm:ss")
                    .toFormatter()
            );
        } catch (DateTimeParseException e) {
            this.time = LocalDateTime.parse(time, new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
                    .toFormatter()
            );
        }

    }

    public void setTime(LocalDateTime time) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.time = time;
    }

    public String message() {
        return time.toLocalDate().format(new DateTimeFormatterBuilder().appendPattern("MM月dd日").toFormatter())
                + "，东莞电网检修风险预警：地区一级事件风险" + firstLevelNum + "项，地区二级事件风险"
                + secondLevelNum + "项，地区三级事件风险" + thirdLevelNum + "项";
    }

}
