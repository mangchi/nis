package mil.gdl.nis.img.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.cmmn.SessionData;
import mil.gdl.nis.cmmn.util.ImgUtil;
import mil.gdl.nis.cmmn.util.Uid;
import mil.gdl.nis.cmmn.util.XmlUtil;
import mil.gdl.nis.dao.DAO;
import mil.gdl.nis.dataset.vo.DatasetVo;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImgServiceImpl implements ImgService{
	
	private final SessionData sessionData;
    private final DAO dao;
    @Value("${spring.servlet.multipart.location}")
	private String uploadPath;

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getImgList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectPage("Img.selectList", map);
	}

	@Override
	public Map<String, Object> getImgInfo(Map<String, Object> map) throws Exception {
		
		return dao.selectMap("Img.select", map).orElseGet(() -> new HashMap<String, Object>());
	}

	@Override
	public DatasetVo getImg(Map<String, Object> map) throws Exception {
		
		return (DatasetVo)dao.selectOptionalObject("Img.selectVo", map).orElseGet(() -> new DatasetVo());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String,Object>> getFileList(Map<String, Object> map) throws Exception {
		return (List<Map<String,Object>>)dao.selectPage("Img.selectFileList", map);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String,Object>> getObjecList(Map<String, Object> map) throws Exception {
		return (List<Map<String,Object>>)dao.selectPage("Img.selectObjectList", map);
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int save(Map<String, Object> map) throws Exception {
		int rtn = 0;
		if(map.containsKey("list")) {
			List<Map<String,Object>> xmlList = new ArrayList();
			List<Map<String,Object>> imgList = new ArrayList();
			List<Map<String,Object>> list = (List<Map<String,Object>>)map.get("list");
			Map<String,Object> deleteParam = new HashMap();
			deleteParam.put("id",list.get(0).get("id"));
			dao.update("Img.deleteImgByParentId",  deleteParam);
			for(Map<String,Object> item:list) {
				String filePath = uploadPath+item.get("filePath")+"/"+item.get("fileNm");
				item.put("parentId",item.get("id"));
				item.put("fileSeq",item.get("seq"));
				item.put("status","001");
				item.put("createUserId",sessionData.getUserVo().getUserId());
				item.put("updateUserId",sessionData.getUserVo().getUserId());
				item.put("id",Uid.makeLongUUID());
				rtn += dao.update("Img.insertImg",  item);
				if(item.get("fileType").toString().endsWith("xml")) {
					Map<String,Object> objectMap = XmlUtil.getObjectXmlData(filePath);
					List<Map<String,Object>> objectList = (List<Map<String,Object>>)objectMap.get("objectList");
					for(Map<String,Object> objectItem:objectList) {
						objectItem.put("id", Uid.makeLongUUID());
						objectItem.put("classInfoId", objectItem.get("id"));
						objectItem.put("upperId", "0");
						objectItem.put("classNm", objectItem.get("name"));
						rtn += dao.update("Cls.insertClassByXml",objectItem);
					}
					xmlList.add(objectMap);
				}
				else {
					File f = new File(filePath);
					String workFilePath = uploadPath+item.get("filePath")+"/work";
					Map<String,Object> sizeMap = ImgUtil.resizeFile(f,workFilePath,String.valueOf(item.get("fileNm")));
					item.put("workFilePath", item.get("filePath")+"/work");
					item.put("workFileNm", sizeMap.get("workFileNm"));
					item.put("width", sizeMap.get("width"));
					item.put("height", sizeMap.get("height"));
					imgList.add(item);
				}
			}
			for(Map<String,Object> img:imgList) {
				rtn += dao.update("File.updateFileSize", img);
				for(Map<String,Object> xml:xmlList) {
                    if(img.get("originFileNm").equals(xml.get("fileNm"))) {
                    	double newWidth = Double.parseDouble(String.valueOf(img.get("width")));
                    	double newHeight = Double.parseDouble(String.valueOf(img.get("height")));
                 		double orgWidth = Double.parseDouble(String.valueOf(xml.get("width")));
                    	double orgHeight = Double.parseDouble(String.valueOf(xml.get("height")));
                    	List<Map<String,Object>> objList = (List<Map<String,Object>>)xml.get("objectList");
                    	for(Map<String,Object> obj:objList) {
                    		double nx = Math.round((Double.parseDouble(String.valueOf(obj.get("x")))*newWidth)/orgWidth);
                    		double nex = Math.round((Double.parseDouble(String.valueOf(obj.get("ex")))*newWidth)/orgWidth);
                    		double ny = Math.round((Double.parseDouble(String.valueOf(obj.get("y")))*newHeight)/orgHeight);
                    		double ney = Math.round((Double.parseDouble(String.valueOf(obj.get("ey")))*newHeight)/orgHeight);
                    		obj.put("id", Uid.makeLongUUID());
                    		obj.put("x", nx+"");
                        	obj.put("y", ny+"");
                        	obj.put("ex", nex+"");
                        	obj.put("ey", ney+"");
                        	if(!obj.containsKey("truncated")) {
                        		obj.put("truncated","");
                        	}
                        	
                    	}
                    	xml.put("imageId", img.get("id"));
                    	xml.put("createUserId",sessionData.getUserVo().getUserId());
                    	xml.put("updateUserId",sessionData.getUserVo().getUserId());
                    	log.debug("xml:{}",xml);
                    	rtn += dao.update("Cmmn.insertObjectInfo",xml);
					}
				}
			}
			
		}
		else {
			map.put("id",Uid.makeLongUUID());
			map.put("status","001");
			map.put("createUserId",sessionData.getUserVo().getUserId());
			map.put("updateUserId",sessionData.getUserVo().getUserId());
			rtn += dao.update("Img.insertImg",  map);
		}
		return rtn;
		
	}
	
	@Override
	public int deleteImgs(Map<String,Object> map) throws Exception {
		return dao.delete("Img.deleteImgs", map);
	}

	@Override
	public int approve(Map<String, Object> map) throws Exception {
		return dao.update("Img.approve", map);
	}


}
