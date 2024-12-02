package mil.gdl.nis.frame.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mil.gdl.nis.cmmn.SessionData;
import mil.gdl.nis.cmmn.util.PythonUtil;
import mil.gdl.nis.dao.DAO;
import mil.gdl.nis.dataset.vo.DatasetVo;

@RequiredArgsConstructor
@Service
public class FrameServiceImpl implements FrameService{
	
	@Value("${spring.servlet.multipart.location}")
	private String uploadPath;
	private final PythonUtil pythonUtil;
    private final SessionData sessionData;
    private final DAO dao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getFrameList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectPage("Frame.selectList", map);
	}
    
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getExtractList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectPage("Frame.selectList", map);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getExtractImageList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectPage("Frame.selectExtractImageList", map);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getImageList(Map<String, Object> map) throws Exception {
		return (List<Map<String, Object>>)dao.selectList("Frame.selectImageList", map);
	}

	@Override
	public Map<String, Object> getFrameInfo(Map<String, Object> map) throws Exception {
		
		return dao.selectMap("Frame.select", map).orElseGet(() -> new HashMap<String, Object>());
	}

	@Override
	public DatasetVo getFrame(Map<String, Object> map) throws Exception {
		
		return (DatasetVo)dao.selectOptionalObject("Frame.selectVo", map).orElseGet(() -> new DatasetVo());
	}

	@SuppressWarnings("unchecked")
	@Override
	public int  extractMov(Map<String,Object> map) throws Exception{
		//python call
		int rtn = 0;
		
		if(map.containsKey("list")) {
			List<Map<String,Object>> list = (List<Map<String,Object>>)map.get("list");
			for(Map<String,Object> item:list) {
				
				item.put("userId",sessionData.getUserVo().getUserId());
				rtn += dao.update("Frame.extractMov", item);
				pythonUtil.callExtract(map);
			}
		}
		else {
			map.put("userId",sessionData.getUserVo().getUserId());
			rtn += dao.update("Frame.extractMov", map);
			pythonUtil.callExtract(map);
			
		}
		
		return rtn;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public int deleteImageByFrame(Map<String, Object> map) throws Exception {
		int rtn = 0;
		rtn += dao.delete("Frame.deleteImgsByFrame",map);
		rtn += dao.delete("Frame.deleteFilesByFrame",map);
		
		List<Map<String,Object>> deleteImgList = (List<Map<String,Object>>)map.get("list");
		for(Map<String,Object> deleteImg:deleteImgList) {
			File deleteFile = null;
			if(String.valueOf(map.get("filePath")).indexOf(uploadPath) > -1) {
				deleteFile = new File(map.get("filePath")+"/"+map.get("fileNm"));
			}else {
				deleteFile = new File(uploadPath+map.get("filePath")+"/"+map.get("fileNm"));
			}
			if(deleteFile.exists() && deleteFile.isFile()) {
				deleteFile.delete();
			}
		}
			
		return rtn;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public int deleteFrame(Map<String, Object> map) throws Exception {
		map.put("userId",sessionData.getUserVo().getUserId());
		int rtn = 0;
		List<Map<String,Object>> deleteImgList = (List<Map<String,Object>>)dao.selectList("selectDeleteFileFrame",map);
		rtn += dao.update("Frame.deleteFrame",map);
		rtn += dao.delete("Frame.deleteAllImageFrame",map);
		rtn += dao.delete("Frame.deleteAllFileFrame",map);
		for(Map<String,Object> deleteImg:deleteImgList) {
			File deleteFile = null;
			if(String.valueOf(map.get("filePath")).indexOf(uploadPath) > -1) {
				deleteFile = new File(map.get("filePath")+"/"+map.get("fileNm"));
			}else {
				deleteFile = new File(uploadPath+map.get("filePath")+"/"+map.get("fileNm"));
			}
			if(deleteFile.exists() && deleteFile.isFile()) {
				deleteFile.delete();
			}
		}
		return rtn;
	}

	

	


}
