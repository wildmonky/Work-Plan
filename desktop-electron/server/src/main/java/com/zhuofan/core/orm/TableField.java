package com.zhuofan.core.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.zhuofan.core.eval.Eval;

@Retention(RetentionPolicy.RUNTIME)
@Target(
        { ElementType.FIELD, ElementType.METHOD })
public @interface TableField {
    public static final String TF_VARCHAR = "VARCHAR";
    public static final String TF_NUMBER = "NUMBER";
    public static final String TF_UNIXTIME = "UNIXTIME";
    public static final String TF_FILEUPLOAD = "FILEUPLOAD";

    String TF_LOCAL_DATE_TIME = "LOCLADATETIME";

    String TF_BIGDECIMAL = "BIGDECIMAL";

    public static final String TF_SYSUXINTIME = "sysUnixTime";

    public static final String TF_EVAL_YAARMONTH = Eval.EVAL
            + "return CommonFunc.unix2Date(CommonFunc.unixTime(),\"yyyyMM\");";

    public enum TFInpytType {
        text, textArea, date, array, hashMap, sql, dialog, sysuser,password
    }

    public String name();

    /**
     * 样式
     * @return
     */
    public String css() default "";

    /**
     * @Title type
     * @Description 数据类型，目前仅支持VARCHAR和NUMBER两种
     * @return
     * @author 广州卓凡扶小冬
     */
    public String type() default TF_VARCHAR;

    /**
     * @Title 是否主键
     *
     * @return
     */
    public boolean isPrimaryKey() default false;

    /**
     * 是否是排序字段
     * @return
     */
    public boolean isSortField() default false;

    /**
     * 默认DESC降序
     * @return
     */
    public boolean isASC() default false;

    /**
     * @Title 字段注释
     * @Description 在自动生成录入表单时，此元素将成为列标题或录入元素前的文字
     * @return
     */
    public String comment() default "";

    /**
     * @Title 是否显示在录入表单时
     * @Description 当相应的表设置了formType时，在自动录入表单中，此配置将起作用 取值为1表示显示，取值为0表示不显示
     * @return
     */
    public boolean visible() default false;

    /**
     * @Title 在录入表单中，是否只读
     * @Description
     * @return
     */
    public boolean readOnly() default false;

    /**
     * @Title 是否合并，当相应的表formType设置为multiLine和fixedLine时，此元素将起作用
     * @Description isMergeRow取值为1时，表示会对该列在相邻的行中，相同的值进行合并 isMergeRow
     *
     * @return
     */
    public boolean isMergeRow() default false;

    /**
     * @Title defaultValue
     * @Description 默认值 内置默认值包括： sysUnixTime：用10位数表示的unix时间 loginUserID：当前登录用户ID
     *                  loginUserName：当前登录用户帐户号 loginDeptID：当前登录部门ID
     *                  loginDeptName：当前登录部门名称 其它：支持eval语法，凡是符合java语法的代码，全可以支持。
     * @return
     * @author 广州卓凡扶小冬
     */
    public String defaultValue() default "";

    /**
     * @Title inputType
     * @Description 表单输入元素类型，取值包括：
     *              <br> text：<input type=text/>
     *              <br> textArea:<textarea></textarea>
     *              <br> date：<input type=text onclick='日期控件'/>
     *              <br> array:用分号分开的字符串数组，如：1;2;3;4;5，最终显示元素为<select/>
     *              <br> hashMap：用分号和冒号分开的key->value键值，如： 1:中调;2 地调;3:县调,最终显示元素为<select/>
     *              <br> sql:从数据库中取值，值为sql，将取前两个字段做为 key和value， 如：select userid,username from
     *                             users，最终显示元素为<select/>
     *              <br> dialog：从弹出对话框中取值，设置值为对话框的URL ，如：selectUser.action?xxxx
     *              <br> 说明：不支持radio和checkbox，认为此两种方式，用select表示是一样的效果。
     *              <br> sysuser 系统用户;使用情况见ImportWorkPlan.java
     * @return
     * @author 广州卓凡扶小冬
     */
    public TFInpytType inputType() default TFInpytType.text;

    /**
     * @Title initValue
     * @Description 初始化值，多个用分号隔开
     * @return
     * @author 广州卓凡扶小冬
     */
    public String initValue() default "";

    /**
     * @Title isYearMonth
     * @Description 年月字段标记
     * @return
     * @author 广州卓凡扶小冬
     */
    public boolean isYearMonth() default false;

    /**
     * @Title 是否统计字段
     * @Description 统计范围为按同一个月份的数据同一字段值的统计
     * @return
     * @author 广州卓凡扶小冬
     */
    public boolean isMonthTotal() default false;

    /**
     * @Title 是否为累计值
     * @Description 累计值为从每年一月份开始累加到录入的月份
     * @return
     */
    public boolean isYearTotal() default false;

    public boolean isYearPlan() default false;

    public boolean isMonthPlan() default false;

    /**
     * @Title 获取统计值或计划值的依据
     * @Description 必须为定义表中的一个字段名称
     * @since 若字段定义了计划或统计，则必须有对accordTotalPlan的定义
     * @举例 DeviceStart.java 中 fnjhz
     * @return
     */
    public String accordTotalPlan() default "";

    /**
     * @Title 要统计的字段
     * @Description 必须为定义表中的一个字段名称
     * @since 若字段定义了计划或统计，则必须有对accordTotalPlan的定义
     * @举例 DeviceStart.java 中 fnjhz
     * @return
     */
    public String fieldTotal() default "";

    /**
     * @Title 是否为分组字段
     * @Description 即group by 字段，仅当表的hasGroup设置为true里有效
     * @return
     * @author 广州卓凡扶小冬
     */
    public boolean isGroup() default false;

    /**
     * @Title groupInfo
     * @Description 需要统计的分组信息 ,仅当表的hasGroup设置为true里有效
     * @设置格式参考 用分号 隔开，用冒号隔开，分组方式和展示名称 (参考： count:台数;sum:容量)
     * @return
     * @author 广州卓凡扶小冬
     */
    public String groupInfo() default "";


    public String idType() default "";
}
