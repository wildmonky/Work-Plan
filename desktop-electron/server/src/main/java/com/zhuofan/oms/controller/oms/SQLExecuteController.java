package com.zhuofan.oms.controller.oms;

import com.zhuofan.oms.controller.sys.BaseController;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/sql")
@Controller
public class SQLExecuteController extends BaseController {

    @Resource
    private DataSource dataSource;

    @PostMapping("/execute")
    @ResponseBody
    public Data execute(@RequestParam("sql") String inputSql, Integer startRow, Integer size) throws SQLException {
        inputSql = inputSql.replaceAll(";", "").trim();
        Data data = new Data();

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            // 设置回滚点
            Savepoint savepoint = connection.setSavepoint();


            try(PreparedStatement preparedStatement = connection.prepareStatement(inputSql + " limit ?,? ");) {
                startRow = startRow == null ? 0 : startRow;
                size = size == null ? 100 : size;
                preparedStatement.setInt(1, startRow);
                preparedStatement.setInt(2, size);
                try(ResultSet resultSet = preparedStatement.executeQuery();) {
                    // 回滚 不允许变更数据
                    connection.rollback(savepoint);

                    ResultSetMetaData metaData = resultSet.getMetaData();
                    List<String> columnNames = new ArrayList<>();
                    List<Column> columns = new ArrayList<>();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        String columnName = metaData.getColumnName(i);
                        columns.add(new Column(i, columnName, metaData.getColumnType(i)));
                        columnNames.add(columnName);
                    }

                    List<Map<String, Object>> rows = new ArrayList<>();
                    while (resultSet.next()) {
                        if (resultSet.isBeforeFirst()) {
                            continue;
                        }

                        if (resultSet.isAfterLast()) {
                            break;
                        }

                        Map<String, Object> columnData = parseColumnData(resultSet, columns);
                        rows.add(columnData);
                    }


                    data.setColumnNames(columnNames);
                    data.setRows(rows);
                    data.setStartRowNum(startRow);
                    data.setSize(rows.size());

                    resultSet.close();
                }
                preparedStatement.close();
            }
            connection.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return data;
    }

    @PostMapping("/execute/download")
    public void download(@RequestParam("sql") String inputSql, Integer startRow, Integer size, HttpServletResponse response) throws SQLException {
        Data data = execute(inputSql, startRow, size);
        try (
                XSSFWorkbook workbook = new XSSFWorkbook()
        ) {
            XSSFSheet sheet = workbook.createSheet();
            int firstRowNum = 0, cellNum = 0;
            XSSFRow firstRow = sheet.createRow(firstRowNum++);
            for (String columnName : data.getColumnNames()) {
                firstRow.createCell(cellNum++).setCellValue(columnName);
            }

            for (Map<String, Object> rowData : data.getRows()) {
                XSSFRow row = sheet.createRow(firstRowNum++);
                cellNum = 0;
                for (String columnName : data.getColumnNames()) {
                    Object d = rowData.get(columnName);
                    row.createCell(cellNum++).setCellValue(d == null ? null : d.toString());
                }
            }

            super.setResponseHeader(response, "sql执行结果.xlsx");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> parseColumnData(ResultSet resultSet, List<Column> columns) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        for (Column column : columns) {
            int columnIndex = column.getIndex();
            String columnName = column.getName();
            Object columnValue = resultSet.getObject(columnIndex);
            map.put(columnName, columnValue);
        }

        return map;
    }

    @Getter
    @Setter
    public static class Data {

        private List<String> columnNames;

        private List<Map<String, Object>> rows;

        private int startRowNum;

        private int size;

    }

    @Getter
    @AllArgsConstructor
    public static class Column {

        int index;

        String name;

        int type;

    }

}
