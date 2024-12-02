package mil.gdl.nis.cmmn.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.cmmn.SessionData;
import mil.gdl.nis.cmmn.util.DateUtil;
import mil.gdl.nis.cmmn.util.Uid;
import mil.gdl.nis.dao.DAO;


@Slf4j
@RequiredArgsConstructor
@Service
public class FileStorageServiceImpl implements FileStorageService {

	private final SessionData sessionData;
	private final DAO dao;

	@Value("${spring.servlet.multipart.location}")
	private String uploadPath;

	@Override
	public void init(String filePath) {
		try {
			Files.createDirectories(Paths.get(filePath));
		} catch (IOException e) {
			log.error("error:", e );
			throw new RuntimeException("Could not initialize folder for upload!");
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String,Object> save(MultipartFile[] files,Map<String,Object> appendParam) throws Exception{
		Map<String,Object> fileMap =new HashMap();
		String savePath = uploadPath + "/" + DateUtil.currentYear() + "/"
				+ DateUtil.currentMonth();
		
		List<Map<String, Object>> fileList = new ArrayList<>();
		String fileId = Uid.makeLongUUID();
		try {
			
			List<Map<String, Object>> paramList = new ObjectMapper().readValue(String.valueOf(appendParam.get("fileParam")), new TypeReference<List<Map<String, Object>>>(){});
			if(paramList != null && paramList.size() > 0) {
				savePath = uploadPath + String.valueOf(paramList.get(0).get("filePath"));
				fileId = String.valueOf(paramList.get(0).get("id"));
			}
			Path root = Paths.get(savePath);
			if (!Files.exists(root)) {
				init(savePath);
			}
            int seq = 0;
            List<Map<String,Object>> deleteList = new ArrayList();
            if(paramList != null && paramList.size() > 0) {
            	for(Map<String,Object> deleteFile:paramList) {
        			if(deleteFile.containsKey("isDelete")) {
        				deleteList.add(deleteFile);
        			}
        			else {
        				seq=Integer.parseInt(String.valueOf(deleteFile.get("seq")));
            		}
            	}
            	if(deleteList.size() > 0) {
            		Map<String,Object> deleteParam = new HashMap();
            		deleteParam.put("id", fileId);
                	deleteParam.put("deleteList", deleteList);
                	dao.update("File.deleteFilesById", deleteParam);
            	}
            	
            	for (MultipartFile file : files) {
            		boolean isExist = false;
            		for (int i=0;i<paramList.size();i++) {
            			Map<String,Object> param = paramList.get(i);
                		if(file.getOriginalFilename().equals(param.get("fileNm"))) {
                			isExist = true;
                			break;
                		}
                		
                	}
            		if(!isExist) {
            			seq += 1;
            			Map<String, Object> fileInfo = new HashMap<>();
            			fileInfo.put("seq", seq);
        				fileInfo.put("fileSize", file.getSize());
        				fileInfo.put("originFileNm", file.getOriginalFilename());
        				fileInfo.put("fileType", file.getContentType());
        				String fileNm = Uid.makeLongUUID() + "."
        						+ file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);	
        				fileInfo.put("fileNm", fileNm);
        				fileInfo.put("filePath", String.valueOf(paramList.get(0).get("filePath")));
        				Files.copy(file.getInputStream(), root.resolve(fileNm), StandardCopyOption.REPLACE_EXISTING);
        				fileList.add(fileInfo);
            		}
        		}
            	
            }else {
            	for (MultipartFile file : files) {
    				Map<String, Object> fileInfo = new HashMap<>();
    				seq += 1;
    				fileInfo.put("seq", seq);
    				fileInfo.put("fileSize", file.getSize());
    				fileInfo.put("originFileNm", file.getOriginalFilename());
    				fileInfo.put("fileType", file.getContentType());
    				String fileNm = Uid.makeLongUUID() + "."
    						+ file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);	
    				fileInfo.put("fileNm", fileNm);
    				fileInfo.put("filePath", "/" + DateUtil.currentYear() + "/" + DateUtil.currentMonth());
    				Files.copy(file.getInputStream(), root.resolve(fileNm), StandardCopyOption.REPLACE_EXISTING);
    				fileList.add(fileInfo);
    			}
            }
            fileMap.put("isChangeFile", "N");
           
            if(fileList.size() > 0) {
            	Map<String, Object> param = new HashMap<>();
    			param.put("id",fileId);
    			param.put("fileStatus","001");
    			param.put("userId", sessionData.getUserVo().getUserId());
    			param.put("fileList", fileList);
    			dao.update("File.insertFiles", param);
    			fileMap.put("isChangeFile", "Y");
            }

			if(deleteList.size() > 0) {
        		for(Map<String,Object> deleteMap:deleteList) {
        			File f = new File(uploadPath+String.valueOf(deleteMap.get("filePath")+"/"+deleteMap.get("fileNm")));
        			if(f.exists() && f.isFile()) {
        				f.delete();
        			}
        			if(String.valueOf(deleteMap.get("fileType")).startsWith("image")) {
        				File wf = new File(uploadPath+String.valueOf(deleteMap.get("filePath")+"/work/"+deleteMap.get("fileNm")));
            			if(wf.exists() && wf.isFile()) {
            				wf.delete();
            			}
        			}
        			
        		}
        	}
			Map<String, Object> paramMap = new HashMap<>();
			List<Map<String,Object>> upList = new ArrayList();
			Map<String, Object> upParam = new HashMap<>();
	        upParam.put("id",fileId);
	        upParam.put("sourceCd",appendParam.get("sourceCd"));
            upList.add(upParam);
            paramMap.put("createUserId",sessionData.getUserVo().getUserId());
	        paramMap.put("upOrDown","U");
	        paramMap.put("sourceCd",appendParam.get("sourceCd"));
            paramMap.put("list", upList);
            log.debug("paramMap:{}",paramMap);
    		dao.update("File.insertUpDownLog", paramMap);

		    fileMap.put("fileId", fileId);
		}catch (Exception e) {
			if(e instanceof SQLException) {
				
			}
			log.error("error:", e );
			throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
		}
		
		return fileMap;
	}
    
	@Override
	public void save(MultipartFile file,Map<String,Object> fileInfo) {
		String savePath = uploadPath+"/"+DateUtil.currentYear()+"/"+DateUtil.currentMonth();
		try {
			Path root = Paths.get(savePath);
			if (!Files.exists(root)) {
				init(savePath);
			}
			String fileNm = Uid.makeUUID()+"."+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
			Files.copy(file.getInputStream(), root.resolve(fileNm),StandardCopyOption.REPLACE_EXISTING);
			//fileInfo.put("filePath", savePath);
			fileInfo.put("filePath", "/" + DateUtil.currentYear() + "/" + DateUtil.currentMonth());
			fileInfo.put("fileNm", fileNm);

		} catch (Exception e) {
			log.error("error:", e );
			throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
		}
	}
	



	@Override
	public Resource load(String filePath,String fileNm) {
		try {
			Path file = Paths.get(uploadPath+"/"+filePath+"/").resolve(fileNm);
			if(filePath.indexOf("upload") > -1){
				file = Paths.get(filePath+"/").resolve(fileNm);
			}
			
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(Paths.get(uploadPath).toFile());
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			Path root = Paths.get(uploadPath);
			return Files.walk(root, 1).filter(path -> !path.equals(root)).map(root::relativize);
		} catch (IOException e) {
			throw new RuntimeException("Could not load the files!");
		}
	}

	@Override
	public int delete(Map<String, String> fileInfo) throws Exception{
		int rtn = dao.update("File.deleteFile", fileInfo);
		File deleteFile = new File(fileInfo.get("fileNm"));
		
		if(deleteFile.exists() && deleteFile.isFile()) {
			deleteFile.delete();
		}
		return rtn;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int deleteAllFile(Map<String, Object> map) throws Exception{
		
		List<Map<String,Object>> deleteList = (List<Map<String,Object>>)dao.selectList("File.selectDeleteFile", map);
		for(Map<String,Object> deleteMap: deleteList) {
			File deleteFile = new File(uploadPath+deleteMap.get("fileNm"));
			
			if(deleteFile.exists() && deleteFile.isFile()) {
				deleteFile.delete();
			}
            File deleteWorkFile = new File(uploadPath+deleteMap.get("workFileNm"));
			
			if(deleteWorkFile.exists() && deleteWorkFile.isFile()) {
				deleteWorkFile.delete();
			}
		}
        
		int rtn = dao.update("File.deleteFiles", map);
		
		return rtn;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> select(Map<String, Object> param) throws Exception {
		
		return (Map<String, Object>)dao.selectObject("File.selectFile",param);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> selectFileListByIds(Map<String, Object> param) throws Exception {
		param.put("createUserId",sessionData.getUserVo().getUserId());
		param.put("upOrDown","D");
		dao.update("File.insertUpDownLog", param);
		return (List<String>)dao.selectList("File.selectFileListByIds",param);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> selectList(String id) throws Exception {
		return (List<Map<String, Object>>)dao.selectList("File.selectFileList",id);
	}

	@Override
	public int saveDownLog(Map<String, Object> param) throws Exception {
		return dao.update("File.insertUpDownLog",param);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int move(Map<String, Object> fileInfo) throws Exception {
		File desttDir = new File(String.valueOf(fileInfo.get("learningPath")));
		if(desttDir.exists()) {
			if(!desttDir.isDirectory()) {
				throw new Exception("desttDir is not valid.");
			}
		}
		else {
			desttDir.setExecutable(false, true);
			desttDir.setReadable(true);
			desttDir.setWritable(false, true);
			desttDir.mkdirs();
		}
		List<Map<String,Object>> fileList = (List<Map<String,Object>>)fileInfo.get("fileList");
		for(Map<String,Object> fileItem:fileList) {
			File srcFile = new File(uploadPath+String.valueOf(fileItem.get("srcFile")));
			File destFile = new File(String.valueOf(fileItem.get("destFile")));
			FileUtils.copyFile(srcFile,destFile);
		}
		return fileList.size();
	}

}
