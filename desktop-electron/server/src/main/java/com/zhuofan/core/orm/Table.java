package com.zhuofan.core.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {

    /**
     * 表单类型，取值为以下<br>
     * @Description single：单条数据的录入，一个表单只录入一条数据<br>
     *              <br> multiLine：多条数据的录入，一个表单可以录入多条数据，可以自由增加，删除<br>
     *              <br> fixedLine：固定行数的录入，一个表单可以录入固定行数据，不可以增加和删除<br>
     * @author 广州卓凡扶小冬
     *
     */
    public enum tableFormType {
        single,multiLine,fixedLine
    }

    /**
     * 统一列表中需要自动计算统计，按钮onclick="total()",以业务名称business命名的js文件放在js下的business
     * @return
     */
    public boolean jsButton() default false;

    /**
     * 一些特殊的表单要特殊标明其他预览地址
     * @return
     */
    public String viewUrl() default "";

    /**
     * 一些特殊的表单要特殊标明其他编辑地址
     * @return
     */
    public String editUrl() default "";

    /**
     * 当请求当月数据为空时，是否显示上个月的历史数据
     * @return
     */
    public boolean isShowHistory() default true;

    /**
     * @Title showRowNum <br>
     * @Description 是否在前面显示行的序号 <br>
     * @author 广州卓凡扶小冬
     */
    public boolean showRowNum() default true;

    /**
     * @Title isTimeNext <br>
     * @Description 若是总结与计划模块中，判断属于计划或是总结，依据此项显示页面日期 <br>
     * @author 广州卓凡孙艳妮
     */
    public boolean isTimeNext() default false;

    /**
     * @Title 每个表单前后的中文描述
     * @举例 CheckStopSradio.java的设置
     * @实现 TableForm.java 中搜索 table.tableNotes().isEmpty()
     * @return
     */
    public boolean tableNotes() default false;

    /**
     * @Title ORACLE中数据表名
     * @return
     */
    public String tableName();

    /**
     * @Title 表的中文定义
     * @return
     */
    public String comment() default "";

    /**
     * @Title 表单类型，取值为以下
     * @Description single：单条数据的录入，一个表单只录入一条数据
     *              <br> multiLine：多条数据的录入，一个表单可以录入多条数据，可以自由增加，删除
     *              <br> fixedLine：固定行数的录入，一个表单可以录入固定行数据，不可以增加和删除
     * @return
     */
    public tableFormType formType() default tableFormType.single;

    /**
     * @Title 初始行数<br>
     * @Description 新增数据时，自动初始化的行数。列的相关设置，将受此设置制约。
     *              <br>仅在formType取值为multiLine和fixedLine时可以用
     * @return
     */
    public int initRow() default 1;

    /**
     * 导入Excel时是累加/替换，
     * 默认替换，大部分都是替换，只有2.5.1和2.5.2下面的4个界面是累加
     */
    public boolean needDelete() default true;

}
