package mil.gdl.nis.cmmn.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XmlUtil {
	
	@Value("${learning.depth}")
    private static String learningDepth;
	
	@Value("${learning.segmented}")
    private static String learningSegmented;
	
	
	public static Map<String,Object> getAisXmlData(String filePath) throws Exception{
		Map<String,Object> rtnMap = new HashMap<>();
		List<Map<String,Object>> list = new ArrayList<>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			File aisFile = new File(filePath);
			Document doc = builder.parse(aisFile);
			NodeList filePathNodeList =  doc.getElementsByTagName("folername");
			rtnMap.put("filePath",filePathNodeList.item(0).getChildNodes().item(0).getNodeValue());
			NodeList fileNameNodeList =  doc.getElementsByTagName("filename");
			rtnMap.put("fileNm",fileNameNodeList.item(0).getChildNodes().item(0).getNodeValue());
			NodeList sizeNodeList =  doc.getElementsByTagName("size");
			for(int i=0;i<sizeNodeList.getLength();i++) {
				Node node = sizeNodeList.item(i);
				NodeList childList = node.getChildNodes();
				for(int k=0;k<childList.getLength();k++) {
					Node item = childList.item(k);
					if (item.getNodeType() == Node.ELEMENT_NODE) {
						switch(item.getNodeName().toLowerCase().trim() ) {
						case "width" : rtnMap.put("width", item.getTextContent()); break;
					    case "height" : rtnMap.put("height", item.getTextContent()); break;
					    default: break;
						}
					}
				}
			}
			NodeList nodeList = doc.getElementsByTagName("object");
	
			for(int i=0;i<nodeList.getLength();i++) {
				Map<String,Object> map = new HashMap<>();
				Node node = nodeList.item(i);
				NodeList childList = node.getChildNodes();
	
				for(int k=0;k<childList.getLength();k++) {
					Node item = childList.item(k);
					if (item.getNodeType() == Node.ELEMENT_NODE) {
						switch(item.getNodeName().toLowerCase().trim() ) {
						    case "mmsi" : map.put("mmsi", item.getTextContent()); break;
						    case "name" : map.put("name", item.getTextContent()); break;
						    case "type" : map.put("type", item.getTextContent()); break;
						    case "call_sign" : map.put("callSign", item.getTextContent()); break;
						    case "length" : map.put("length", item.getTextContent()); break;
						    case "breadth" : map.put("breadth", item.getTextContent()); break;
						    case "point" : Element pointItem = (Element)item;
						    	           NodeList xList = pointItem.getElementsByTagName("x");
						    	           if(xList.item(0).getChildNodes().getLength() == 1) {
						    	        	   map.put("x",xList.item(0).getChildNodes().item(0).getTextContent());
						    	           }
						    	           else {
						    	        	   map.put("x","");
						    	           }
						    	           NodeList yList = pointItem.getElementsByTagName("y");
						    	           if(yList.item(0).getChildNodes().getLength() == 1) {
						    	        	   map.put("y",yList.item(0).getChildNodes().item(0).getTextContent());
						    	           }
						    	           else {
						    	        	   map.put("y","");
						    	           }
						    	           break;
							default: break;
						}
					}
				}
	            list.add(map);
			}
		}catch(Exception e) {
			throw new Exception("xml 파일에 문제가 있습니다.");
		}
		rtnMap.put("objectList", list);
		return rtnMap;
	}
	
	public static Map<String,Object> getObjectXmlData(String filePath) throws Exception{
		Map<String,Object> rtnMap = new HashMap<>();
		List<Map<String,Object>> list = new ArrayList<>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			File objFile = new File(filePath);
			Document doc = builder.parse(objFile);
			NodeList fileNameNodeList =  doc.getElementsByTagName("filename");
			rtnMap.put("fileNm",fileNameNodeList.item(0).getChildNodes().item(0).getNodeValue());
			NodeList sizeNodeList =  doc.getElementsByTagName("size");
			for(int i=0;i<sizeNodeList.getLength();i++) {
				Node node = sizeNodeList.item(i);
				NodeList childList = node.getChildNodes();
				for(int k=0;k<childList.getLength();k++) {
					Node item = childList.item(k);
					if (item.getNodeType() == Node.ELEMENT_NODE) {
						switch(item.getNodeName().toLowerCase().trim() ) {
						case "width" : rtnMap.put("width", item.getTextContent()); break;
					    case "height" : rtnMap.put("height", item.getTextContent()); break;
					    case "depth" : rtnMap.put("depth", item.getTextContent()); break;
					    default: break;
						}
					}
				}
			}
	
			NodeList nodeList = doc.getElementsByTagName("object");
			for(int i=0;i<nodeList.getLength();i++) {
				Map<String,Object> map = new HashMap<>();
				Node node = nodeList.item(i);
				NodeList childList = node.getChildNodes();
				for(int k=0;k<childList.getLength();k++) {
					Node item = childList.item(k);
					if (item.getNodeType() == Node.ELEMENT_NODE) {
						switch(item.getNodeName().toLowerCase().trim() ) {
						    case "name" : map.put("classNm", item.getTextContent()); break;
						    case "pose" : map.put("pose", item.getTextContent()); break;
						    case "difficult" : map.put("difficult", item.getTextContent()); break;
						    case "bndbox" : Element pointItem = (Element)item;
						    	            NodeList xList = pointItem.getElementsByTagName("xmin");
						    	            if(xList.item(0).getChildNodes().getLength() == 1) {
						    	        	    map.put("x",xList.item(0).getChildNodes().item(0).getTextContent());
						    	            }
						    	            else {
						    	        	    map.put("x","");
						    	            }
						    	            NodeList yList = pointItem.getElementsByTagName("ymin");
						    	            if(yList.item(0).getChildNodes().getLength() == 1) {
						    	        	    map.put("y",yList.item(0).getChildNodes().item(0).getTextContent());
						    	            }
						    	            else {
						    	        	    map.put("y","");
						    	            }
						    	            NodeList exList = pointItem.getElementsByTagName("xmax");
						    	            if(exList.item(0).getChildNodes().getLength() == 1) {
						    	        	    map.put("ex",exList.item(0).getChildNodes().item(0).getTextContent());
						    	            }
						    	            else {
						    	        	    map.put("ex","");
						    	            }
						    	            NodeList eyList = pointItem.getElementsByTagName("ymax");
						    	            if(eyList.item(0).getChildNodes().getLength() == 1) {
						    	        	    map.put("ey",eyList.item(0).getChildNodes().item(0).getTextContent());
						    	            }
						    	            else {
						    	        	    map.put("ey","");
						    	            }
						    	            break;
							default: break;
						}
					}
				}
	            list.add(map);
	            
			}
		}catch(Exception e) {
			throw new Exception("xml 파일에 문제가 있습니다.");
		}
		rtnMap.put("objectList", list);
		return rtnMap;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static void makeObjectXmlFile(Map<String,Object> map) throws Exception{
		log.debug("makeObjectXmlFile map:{}",map);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document doc = documentBuilder.newDocument();
        doc.setXmlStandalone(true);

		Element root = doc.createElement("annotation");
		Element filename = doc.createElement("filename");
		filename.setTextContent(String.valueOf(map.get("workFileNm")));
		Element size = doc.createElement("size");
		Element width = doc.createElement("width");
		width.setTextContent(String.valueOf(map.get("width")));
		Element height = doc.createElement("height");
		height.setTextContent(String.valueOf(map.get("height")));
		Element depth = doc.createElement("depth");
		depth.setTextContent(learningDepth);
		
		Element segmented = doc.createElement("segmented");
		segmented.setTextContent(learningSegmented);
		doc.appendChild(root);
		root.appendChild(filename);
		root.appendChild(size);
		size.appendChild(width);
		size.appendChild(height);
		size.appendChild(depth);
		root.appendChild(segmented);
		
		List<Map<String,Object>> objList = (List<Map<String,Object>>)map.get("objList");
		for(Map<String,Object> item:objList) {
			Element object = doc.createElement("object");
			Element name = doc.createElement("name");
			name.setTextContent(String.valueOf(item.get("classNm")));
			Element pose = doc.createElement("pose");
			pose.setTextContent(String.valueOf(item.get("pose")));
			Element difficult = doc.createElement("difficult");
			difficult.setTextContent(String.valueOf(item.get("difficult")));
			Element bndbox = doc.createElement("bndbox");
			Element xmin = doc.createElement("xmin");
			xmin.setTextContent(String.valueOf(item.get("x")));
			Element ymin = doc.createElement("ymin");
			ymin.setTextContent(String.valueOf(item.get("y")));
			Element xmax = doc.createElement("xmax");
			xmax.setTextContent(String.valueOf(item.get("ex")));
			Element ymax = doc.createElement("ymax");
			ymax.setTextContent(String.valueOf(item.get("ey")));
			object.appendChild(name);
			object.appendChild(pose);
			object.appendChild(difficult);
			object.appendChild(bndbox);
			bndbox.appendChild(xmin);
			bndbox.appendChild(ymin);
			bndbox.appendChild(xmax);
			bndbox.appendChild(ymax);
			root.appendChild(object);
		}
		

		DOMSource source = new DOMSource(doc);
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes"); 
		String fileNm = String.valueOf(map.get("workFileNm"));
        log.debug("fileNm:{}",fileNm);
		fileNm = fileNm.substring(0, fileNm.lastIndexOf(".")+1)+"xml";
		StreamResult result = new StreamResult(new FileOutputStream(new File(map.get("learningPath")+"/"+fileNm)));
		transformer.transform(source, result);
		
	}
	
}
