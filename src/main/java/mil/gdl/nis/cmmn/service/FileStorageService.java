package mil.gdl.nis.cmmn.service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

	void init(String filePath);

	void save(MultipartFile file,Map<String,Object> fileInfo) throws Exception;

	Map<String,Object> save(MultipartFile[]  files,Map<String,Object> fileParam) throws Exception;

	Resource load(String filePath,String filename) ;

	void deleteAll();
	
	int delete(Map<String,String> fileInfo) throws Exception;
	
	int move(Map<String,Object> fileInfo) throws Exception;
	
	int deleteAllFile(Map<String,Object> param) throws Exception;
	
	List<Map<String,Object>> selectList(String id) throws Exception;

	Map<String,Object> select(Map<String, Object> param) throws Exception;
	
	Stream<Path> loadAll();
	
	List<String> selectFileListByIds(Map<String, Object> param) throws Exception;
	
	int saveDownLog(Map<String, Object> param) throws Exception;

}
