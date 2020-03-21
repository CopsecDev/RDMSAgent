package com.copsec.railway.rms.domUtils;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * 用于解析server.xml
 */
public class XmlDomUtils {

	public static String getObjectByPath(String xpath,String filePath) throws Exception{

		SAXReader reader = new SAXReader();
		Document document = reader.read(filePath);
		Node node = document.selectSingleNode(xpath);
		return node.getText().trim();
	}

	public static String getAttributeByPath(String xpath ,String filePath,String attrName) throws Exception{

		SAXReader reader = new SAXReader();
		Document document = reader.read(filePath);
		Element element = (Element) document.selectSingleNode(xpath);
		return element.attributeValue(attrName);
	}

	public static List<Element> getAllElement(String xpath ,String filePath) throws Exception{

		SAXReader reader = new SAXReader();
		Document document = reader.read(filePath);
		return document.selectNodes(xpath);
	}
}
