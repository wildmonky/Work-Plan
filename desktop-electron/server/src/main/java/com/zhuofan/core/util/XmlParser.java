package com.zhuofan.core.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

public class XmlParser {
    // xml 转换为json
    public static JSONObject xmlToJSON(String xmlString) {
        try {
            // 1、读取文件并转换为Document文档对象
            Document doc = new SAXReader().read(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
            //2、使用asXML()方法将DOM文档对象转换为字符串
            String s = doc.asXML();
            //3、调用自定义的方法转换为JSON数据格式
            JSONObject jsonObject = startXMLToJSON(s);
            //4、输出结果
//            System.out.println(jsonObject);
            return jsonObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 自定义*/
    public static JSONObject startXMLToJSON(String xml){
        //1、定义JSON对象保存结果
        JSONObject result = new JSONObject();
        try {
            //2、使用DocumentHelper.parseText()转换为DOM文档对象
            Document document = DocumentHelper.parseText(xml);
            //3、获取DOM文档根节点
            Element rootElement = ((Document) document).getRootElement();
            //4、调用自定义的方法转换为JSON数据格式
            parseJson(rootElement,result);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void parseJson(Element element,JSONObject result){
        //1、获取子节点列表
        List<Element> elements = element.elements();
        //2、循环子节点列表获取数据
        for (Element e:elements) {
            //3、有数据则获取
            if (!e.elements().isEmpty() && !e.getText().isEmpty()){
                //4、定义另一个JSON对象保存子节点JSON数据
                JSONObject cjson = new JSONObject();
                //5、此处调用自身继续方法继续循环取值，知道遍历完所有字节点数据
                parseJson(e,cjson);
                if (!cjson.isEmpty()){
                    //6、添加到JSON对象
                    result.put(e.getName(),cjson);
                }
            }else {
                if (!e.getText().isEmpty()){
                    //6、添加到JSON对象
                    result.put(e.getName(),e.getText());
                }
            }
        }
    }

}
