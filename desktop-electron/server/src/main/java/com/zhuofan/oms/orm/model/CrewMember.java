package com.zhuofan.oms.orm.model;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.booleanconverter.BooleanNumberConverter;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhuofan.core.orm.BaseModel;
import com.zhuofan.core.orm.Table;
import com.zhuofan.oms.config.BooleanChineseStringConverter;

/**
 * 班组成员实体
 * @see Crew
 */
@TableName(value = "CREW_MEMBER", excludeProperty = {"changeInfo", "extraInfo", "extraInfo1"})
@Table(tableName = "CREW_MEMBER")
@ExcelIgnoreUnannotated
public class CrewMember extends BaseModel {

    @TableId(type = IdType.AUTO)
    @com.zhuofan.core.orm.TableField(name = "ID", type = com.zhuofan.core.orm.TableField.TF_NUMBER, isPrimaryKey = true)
    private Long id;

    @ExcelProperty("人员姓名")
    @com.zhuofan.core.orm.TableField(name = "NAME")
    private String name;

    /**
     * 班组Id {@link Crew#getId()}
     */
    @com.zhuofan.core.orm.TableField(name = "CREW_ID", type = com.zhuofan.core.orm.TableField.TF_NUMBER)
    private Long crewId;
    /**
     * {@link Crew#getName()}
     */
    @TableField(exist = false)
//    @ExcelProperty("班组名称")
//    @com.zhuofan.core.orm.TableField(name = "CREW_NAME")
    private String crewName;

    // 如：变一继一
    @TableField(exist = false)
    @ExcelProperty("班组名称")
    private String unitCrewName;

    // 是否工作负责人：0-否：1-是
    @ExcelProperty(value = "工作负责人", index = 2, converter = BooleanChineseStringConverter.class)
    @com.zhuofan.core.orm.TableField(name = "IS_MANAGER")
    private Boolean isManager;

    @ExcelProperty(value = "操作人", index = 3, converter = BooleanNumberConverter.class)
    @com.zhuofan.core.orm.TableField(name = "FLAG")
    private Boolean flag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.name = name;
    }

    public Long getCrewId() {
        return crewId;
    }

    public void setCrewId(Long crewId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.crewId = crewId;
    }

    public void setCrewId(long crewId) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.crewId = crewId;
    }

    public String getCrewName() {
        return crewName;
    }

    public void setCrewName(String crewName) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.crewName = crewName;
    }

    public Boolean getIsManager() {
        return isManager;
    }

    public void setIsManager(Boolean manager) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        isManager = manager;
    }

    public void setIsManager(String manager) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        isManager = Boolean.parseBoolean(manager) || "1".equals(manager) || "是".equals(manager) || "yes".equalsIgnoreCase(manager);
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flag = Boolean.parseBoolean(flag) ||"1".equals(flag) || "是".equals(flag) || "yes".equalsIgnoreCase(flag);
    }

    public void setFlag(Boolean flag) {
        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());
        this.flag = flag;
    }


    public String getUnitCrewName() {
        return unitCrewName;
    }

    public void setUnitCrewName(String unitCrewName) {
        this.unitCrewName = unitCrewName;
    }
}
