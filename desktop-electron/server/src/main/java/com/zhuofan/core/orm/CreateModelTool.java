package com.zhuofan.core.orm;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.datasource.JdbcDao;

import java.util.ArrayList;
import java.util.HashMap;

/*
*快速通过数据表创建类的工具
 */
public class CreateModelTool {
    private static String sysPath="D:/app/idea/oms-server/src/main/java/com/zhuofan/core/orm";
    private static String busPath="D:/app/idea/oms-server/src/main/java/com/zhuofan/oms/orm";

    private static String sysPack="com.zhuofan.core.orm";
    private static String busPack="com.zhuofan.oms.orm";

    /*
     *创建DAO方法
     */
    public void createDao(String keys,String path){
        try(JdbcDao jdbcDao=new JdbcDao()) {
            String user = jdbcDao.getDs().getUser();
            String sql = "select table_name,comments from  all_tab_comments where owner='" + user + "' and table_type = 'TABLE' and table_name like '" + keys + "%'";

            ArrayList<HashMap<String, String>> rs = jdbcDao.getResult(sql);

            String pack = path.equals("bus") ? this.busPack : this.sysPack;

            for (int i = 0; i < rs.size(); i++) {

                String tableName = rs.get(i).get("TABLE_NAME");
                String comments = rs.get(i).get("COMMENTS");
                String className = CommonFunc.changeWord(tableName);

                StringBuffer sb = new StringBuffer();

                sb.append("/**\n" +
                        "         * " + pack + ".dao\n" +
                        "         *\n" +
                        "         *\n" +
                        "         *   ver     date           author \n" +
                        "         * " + comments + "：" + tableName + " \n" +
                        "         *           " + CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime())) + "      fuxiaodong \n" +
                        "         *\n" +
                        "         * Copyright (c) 2015, TNT All Rights Reserved. \n" +
                        "**/\n\n");

                sb.append("package " + pack + ".dao;\n" +
                        "\n" +
                        "import com.zhuofan.core.orm.BaseOrmSupport;\n" +
                        "import " + pack + ".model." + className + ";\n\n");

                sb.append("/**\n" +
                        " * " + tableName + "DAO " + comments + "\n" +
                        " *\n" +
                        " * @author admin\n" +
                        " * @Date " + CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime())) + "\n" +
                        " *\n" +
                        " */\n\n");


                sb.append("public class " + className + "DAO extends BaseOrmSupport<" + className + "> {\n\n");

                sb.append("}\n");

                String filePath = path.equals("bus") ? this.busPath : this.sysPath;
                String fileName = filePath + "/dao/" + className + "DAO.java";
                CommonFunc.writeTxtFile(fileName, sb.toString());

                System.out.println("write " + fileName + " suss.");
            }
            jdbcDao.close();
        }


    }

    /*
    *创建模型方法
     */
    public void createModel(String keys,String path){
        try(JdbcDao jdbcDao=new JdbcDao()) {
            String user = jdbcDao.getDs().getUser();
            String sql = "select table_name,comments from  all_tab_comments where owner='" + user + "' and table_type = 'TABLE' and table_name like '" + keys + "%'";

            ArrayList<HashMap<String, String>> rs = jdbcDao.getResult(sql);

            String pack = path.equals("bus") ? this.busPack : this.sysPack;

            for (int i = 0; i < rs.size(); i++) {

                String tableName = rs.get(i).get("TABLE_NAME");
                String comments = rs.get(i).get("COMMENTS");
                String className = CommonFunc.changeWord(tableName);

                StringBuffer sb = new StringBuffer();

                sb.append("/**\n" +
                        "         * " + pack + ".model\n" +
                        "         *\n" +
                        "         *\n" +
                        "         *   ver     date           author \n" +
                        "         * " + comments + "：" + tableName + " \n" +
                        "         *           2015-05-20      fuxiaodong \n" +
                        "         *\n" +
                        "         * Copyright (c) 2015, TNT All Rights Reserved. \n" +
                        "**/\n\n");

                sb.append("package " + pack + ".model;\n" +
                        "\n" +
                        "import com.zhuofan.core.orm.BaseModel;\n" +
                        "import com.zhuofan.core.orm.Table;\n" +
                        "import com.zhuofan.core.orm.TableField;\n\n");

                sb.append("/**\n" +
                        " * " + tableName + " " + comments + "\n" +
                        " *\n" +
                        " * @author admin\n" +
                        " * @Date " + CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime())) + "\n" +
                        " *\n" +
                        " */\n\n");

                sb.append("@Table(\n" +
                        "    tableName = \"" + tableName + "\",\n" +
                        "    comment = \"" + comments + "\")\n\n");

                sb.append("public class " + className + " extends BaseModel {\n\n");

                sql = "select  b.comment$ ,COLUMN_NAME,data_type,data_length,nullable\n" +
                        "\tfrom user_tab_columns a left join syscolumncomments b  on a.column_name=b.colname \n" +
                        "\twhere Table_Name='" + tableName + "' and b.schname = '" + user + "' and b.tvname='" + tableName + "' \n" +
                        "\torder by column_id\n";

                ArrayList<HashMap<String, String>> rsCol = jdbcDao.getResult(sql);

                for (int x = 0; x < rsCol.size(); x++) {
                    String fldn = rsCol.get(x).get("COLUMN_NAME");
                    String commentCol = rsCol.get(x).get("COMMENT$");
                    String dataTypeTmp = rsCol.get(x).get("DATA_TYPE");
                    System.out.println("fldn:" + fldn + ",dataTypeTmp:" + dataTypeTmp);
                    String dataType = dataTypeTmp.equals("VARCHAR2") || dataTypeTmp.equals("TEXT") || dataTypeTmp.equals("TIMESTAMP") ? "String" : "Long";
                    String fieldType = "String".equals(dataType) ? "TF_VARCHAR" : "TF_NUMBER";
                    String isPk = (x == 0) ? "true" : "false";
                    String fldnCodeU = CommonFunc.changeWord(fldn);

                    char[] items = fldnCodeU.toCharArray();
                    items[0] += 32;
                    String fldnCode = new String(items);

                    sb.append("            @TableField(\n" +
                            "                    name = \"" + fldn + "\",\n" +
                            "                    type = TableField." + fieldType + ",\n" +
                            "                    isPrimaryKey = " + isPk + ")\n" +
                            "            private " + dataType + " " + fldnCode + ";\n\n");
                }

                for (int x = 0; x < rsCol.size(); x++) {
                    String fldn = rsCol.get(x).get("COLUMN_NAME");
                    String commentCol = rsCol.get(x).get("COMMENT$");
                    String dataTypeTmp = rsCol.get(x).get("DATA_TYPE");
                    System.out.println("fldn:" + fldn + ",dataTypeTmp:" + dataTypeTmp);
                    String dataType = dataTypeTmp.equals("VARCHAR2") || dataTypeTmp.equals("TEXT") || dataTypeTmp.equals("TIMESTAMP") ? "String" : "Long";
                    String isPk = (x == 0) ? "true" : "false";
                    String fldnCodeU = CommonFunc.changeWord(fldn);

                    char[] items = fldnCodeU.toCharArray();
                    items[0] += 32;
                    String fldnCode = new String(items);

                    sb.append("    public " + dataType + " get" + fldnCodeU + "() {\n" +
                            "        return this." + fldnCode + ";\n" +
                            "    }\n" +
                            "\n" +
                            "    public void set" + fldnCodeU + "(" + dataType + " " + fldnCode + ") {\n" +
                            "        addChangeInfo(Thread.currentThread().getStackTrace()[1].getMethodName());\n" +
                            "        this." + fldnCode + " = " + fldnCode + ";\n" +
                            "    }\n\n");
                }

                sb.append("}\n");

                String filePath = path.equals("bus") ? this.busPath : this.sysPath;
                String fileName = filePath + "/model/" + className + ".java";
                CommonFunc.writeTxtFile(fileName, sb.toString());

                System.out.println("write " + fileName + " suss.");
            }
            jdbcDao.close();
        }
    }

    //主方法
    public static void main(String[] args) {
        CreateModelTool t=new CreateModelTool();
        t.createModel("SYS_FLOW","sys");
        t.createDao("SYS_FLOW","sys");
    }

}

