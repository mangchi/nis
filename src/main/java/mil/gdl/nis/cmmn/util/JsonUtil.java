package mil.gdl.nis.cmmn.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil{
	
	
	public static String toGsonStr(Object obj) {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		return gson.toJson(obj);
		
	}
	public static Map<String, List<Map<String, Object>>> gridModifyData(String jsonString) {
		if(jsonString == null || jsonString.isEmpty()) return null;
			
		log.debug("┌──────────── Grid Json Object Parser ──────────────────────────────────────────────────────────────────────────────────────");
		Map<String,List<Map<String, Object>>> rtnMap = new HashMap<String,List<Map<String, Object>>>();
	
		
		return rtnMap;
	}
	

	public static String toJson(Object obj){
		StringBuffer buffer = new StringBuffer(128);
		toJsonFromObject(obj, buffer);
		return buffer.toString();
	}
	

	@SuppressWarnings("rawtypes")
	private static void toJsonFromObject(Object obj, StringBuffer buffer){
		if(obj instanceof List){
			toJsonFromList((List)obj, buffer);
		} else if(obj instanceof Map){
			toJsonFromMap((Map)obj, buffer);
		}else {
			/*
			 * buffer .append('"') .append(JSONObject.escape(obj==null ? "" :
			 * obj.toString())) .append('"');
			 */
		}
	}
	

	@SuppressWarnings("rawtypes")
	private static void toJsonFromList(List list, StringBuffer buffer){
		buffer.append('[');
		int i, size = list.size();
		for(i=0;i<size;i++){
			if(i>0) buffer.append(',');
			toJsonFromObject(list.get(i), buffer);
		}
		buffer.append(']');
	}
	

	@SuppressWarnings("rawtypes")
	private static void toJsonFromMap(Map map, StringBuffer buffer){
		buffer.append('{');
		String key;
		Object object;
		boolean isFirst = true;
		Iterator it =  map.keySet().iterator();
		while(it.hasNext()){
			key = it.next().toString();
			object = map.get(key);
			
			if(shouldSkipFromMap(key)) continue;
			
			if(isFirst) isFirst = false;
			else buffer.append(',');
			/*
			buffer
				.append('"')
				.append(JSONObject.escape(key))
				.append('"').append(':');
			*/
			toJsonFromObject(object, buffer);
		}
		buffer.append('}');
	}
	
	/**
	  * @Method Name	: shouldSkipFromMap
	  * @author			: choi
	  * @Date			: 2019. 11. 11.
	  * @Description	: Map 을 변환할때 생략할 인자인지 체크
	  * @History		: 
	  * @param attributeName
	  * @return
	  */
	private static boolean shouldSkipFromMap(String attributeName){
		return attributeName.equals("class") || attributeName.equals("jsonString");
	}
	

	public static Map<String, Object> jsonToMap(JSONObject json) {
	    Map<String, Object> retMap = new HashMap<String, Object>();

	    if(json != null) {
	        retMap = toMap(json);
	    }
	    return retMap;
	}


	public static Map<String, Object> jsonStringToMap(String jsonStr) {
	    Map<String, Object> retMap = new HashMap<String, Object>();

	    return retMap;
	}	
	

	public static Object jsonToObj(String value) {
	
	    return null;
	}
	

	public static Map<String, Object> toMap(JSONObject jsonObject) {
	    Map<String, Object> map = new HashMap<String, Object>();
	    
	 
	    return map;
	}
	

	public static List<Object> toList(JSONArray array) {
	    List<Object> list = new ArrayList<Object>();
	  
	    return list;
	}
	

	public static JSONObject xmlToJson(String value) {
		JSONObject jsonObj = null;
		
		return jsonObj;
	}
}