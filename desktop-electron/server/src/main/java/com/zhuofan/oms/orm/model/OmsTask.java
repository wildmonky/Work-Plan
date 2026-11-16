package com.zhuofan.oms.orm.model;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@TableName("OMS_TASK")
@ExcelIgnoreUnannotated
public class OmsTask {

    @TableId("ID")
    private String ID;

    /**
     * 操作内容
     */
    @ExcelProperty(value = "操作内容（关键设备停、送电及方式调整）", index = 1)
    @TableField("CZNR")
    private String CZNR;

    /**
     * 项目管理单位/分部
     */
    @ExcelProperty(value = "项目管理单位/分部", index = 2)
    @TableField("XMGLDW")
    private String XMGLDW;

    /**
     * 工作开始时间
     */
    @ExcelProperty(value = "工作开始时间", index = 3)
    @TableField("GZKSSJ")
    private String GZKSSJ;

    /**
     * 工作结束时间
     */
    @ExcelProperty(value = "工作结束时间", index = 4)
    @TableField("GZJSSJ")
    private String GZJSSJ;

    /**
     * 检修内容
     */
    @ExcelProperty(value = "检修内容", index = 5)
    @TableField("JXNR")
    private String JXNR;

    /**
     * 主要方式调整
     */
    @ExcelProperty(value = "主要方式调整", index = 6)
    @TableField("ZYFSTZ")
    private String ZYFSTZ;

    /**
     * 保护、安置投退/定值检修
     */
    @ExcelProperty(value = "保护、安置投退/定值检修", index = 7)
    @TableField("BHZZ")
    private String BHZZ;

    /**
     * 风险等级
     */
    @ExcelProperty(value = "风险等级", index = 8)
    @TableField("FXDJ")
    private String FXDJ;

    /**
     * 操作风险点
     */
    @TableField("CZFXD")
    private String CZFXD;

    /**
     * 检修单管控
     */
    @TableField("JXDGK")
    private String JXDGK;

    /**
     * 监督管理人员
     */
    @TableField("JDGLRY")
    private String JDGLRY;


    /**
     * 计划状态
     */
    @TableField("JHZT")
    private String JHZT;

    /**
     * 操作人
     */
    @TableField("CZR")
    private String CZR;

    /**
     * 值班负责人
     */
    @TableField("ZBFZR")
    private String ZBFZR;

    @TableField("FILE_NAME")
    private String fileName;

}
