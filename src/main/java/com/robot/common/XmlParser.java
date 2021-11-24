package com.robot.common;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Author: cxx
 * Dom4j解析xml
 * @Date: 2018/5/30 12:21
 */
public class XmlParser {
    public static Map<String,Object> parseXml(String xml) throws Exception {
        //1.创建Reader对象
        SAXReader reader = new SAXReader();
        //2.加载xml
        Document document = reader.read(new ByteArrayInputStream(xml.getBytes()));
        //3.获取根节点
        Element rootElement = document.getRootElement();
        Iterator iterator = rootElement.elementIterator();
        Map<String,Object> map = new HashMap<>();
        while (iterator.hasNext()){
            Element stu = (Element) iterator.next();
            List<Attribute> attributes = stu.attributes();

            for (Attribute attribute : attributes) {
                //获取属性值
//                System.out.println(attribute.getName()+","+attribute.getValue());
                map.put(attribute.getName(),attribute.getValue());
            }
        }

        return map;
    }
}
