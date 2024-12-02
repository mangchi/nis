package mil.gdl.nis.cmmn.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.cmmn.error.ErrorCode;
import mil.gdl.nis.cmmn.service.FileStorageService;
import mil.gdl.nis.dao.DAO;
import mil.gdl.nis.exception.BizException;

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@Component
public class PythonUtil {
	
	@Value("${spring.servlet.multipart.location}")
	private String uploadPath;
	
	@Value("${work.local}")
    private String localWorkPath;
	
	@Value("${work.dev}")
    private String devWorkPath;
	
	@Value("${work.prod}")
    private String prodWorkPath;
	
	@Value("${learning.local}")
    private String localLearningPath;
	
	@Value("${learning.dev}")
    private String devLearningPath;
	
	@Value("${learning.prod}")
    private String prodLearningPath;
	
	@Value("${spring.profiles.active:}") 
	private String activeProfile;
	
	private final DAO dao;
	private final FileStorageService fileStorageService;

	

	@SuppressWarnings("unchecked")
	public  List<Map<String,Object>> calllObjectList(Map<String,Object> map) throws Exception{
		log.debug("calllObjectList map:{}",map);
		/*
		 * Python C:\Users\Administrator\Desktop\Navy\ODBC_Autolabeling_T.py -- fileNm=C:/Users/Administrator/Desktop/Navy/Aden.jpg

		 */
		
		List<Map<String,Object>> list = new ArrayList<>();
		String objStr = ""; 
		if(activeProfile.equals("dev")) {
			String url = "http://192.168.25.65:4449/image";
			try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
	            final HttpPost httppost = new HttpPost(url);
	            log.debug("url:{}",url);
	            File file = new File(uploadPath+map.get("filePath")+"/"+map.get("fileNm"));
	            if(!file.exists()) {
	            	log.error("file is not exist:{}",uploadPath+map.get("filePath")+"/"+map.get("fileNm"));
	            }

	            final FileBody image = new FileBody(file);
	            StringBody  comment = null;
	            if(String.valueOf(map.get("fileNm")).endsWith("gif")) {
	            	comment = new StringBody("image", ContentType.IMAGE_GIF);
	            }
	            else if(String.valueOf(map.get("fileNm")).endsWith("png")) {
	            	comment = new StringBody("image", ContentType.IMAGE_PNG);
	            }
	            else if(String.valueOf(map.get("fileNm")).endsWith("jpg")) {
	            	comment = new StringBody("image", ContentType.IMAGE_JPEG);
	            }
	            
	            final HttpEntity reqEntity = MultipartEntityBuilder.create()
	                    .addPart("image", image)
	                    .addPart("comment", comment)
	                    .addTextBody("threshold", "0.5")
	                    .build();


	            httppost.setEntity(reqEntity);
	            log.debug("executing request:{}",httppost);
	           
	            try (final CloseableHttpResponse response = httpclient.execute(httppost)) {
	                log.debug("response:{}",response);
	                final HttpEntity resEntity = response.getEntity();
	                if (resEntity != null) {
	                	log.debug("Response content length:{} ",resEntity.getContentLength());
	                	log.debug("Response content :{} ",resEntity.getContent());
	                	InputStream is = resEntity.getContent();
	                	BufferedReader reader = new BufferedReader(new InputStreamReader(is));         // 반복해서 입력 데이터 읽기        
	                	String str = "";
	                	while ((str = reader.readLine()) != null) {            
	                		objStr += str;
	                	}
	        
	                	
	                }
	                EntityUtils.consume(resEntity);
	            }
	        }
			catch(Exception e) {
				throw new BizException(ErrorCode.AUOT_LABELING);
			}
		}
		else if(activeProfile.equals("prod")) {
			
		}
		else {
			objStr = "[{\"name\": \"Auto Labeling API\", \"version\": \"1.0\", \"numObjects\": \"2\", \"threshold\": 0.5},"
				     + "{\"name\": \"Object\", \"y\": 371, \"x\": 152, \"height\": 388, \"width\": 225, \"class_name\": \"8888\", \"score\": 0.9910654425621033},"
					 + "{\"name\": \"Object\", \"y\": 378, \"x\": 74, \"height\": 410, \"width\": 110, \"class_name\": \"7777\", \"score\": 0.9999977350234985}]";
		}
		
     
		log.debug("objStr:{}",objStr);  
		Gson gson = new GsonBuilder().create();
		list = gson.fromJson(objStr, ArrayList.class);
		log.debug("list:{}",list);  
		for(Map<String,Object> objectItem:list) {
			log.debug("objectItem:{}",objectItem);
			if(objectItem.containsKey("name") && objectItem.get("name").equals("Object")) {
				objectItem.put("classNm", objectItem.get("class_name"));
				String classId  = (String)dao.selectOne("Cmmn.SelectClasIdByName", objectItem);
				if(classId == null) {
					objectItem.put("id", Uid.makeLongUUID());   ////TB_CLASS_INFO id 생성
					objectItem.put("classInfoId", objectItem.get("id"));
					objectItem.put("classId", objectItem.get("id"));
					objectItem.put("upperId", "0");
					dao.update("Cls.insertClassByXml",objectItem);
				}else {
					objectItem.put("classId", classId);
				}

			}
			
		}
		
	    log.debug("list:{}",list);
		//System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(data));
	

		return list;
	}
	
	@Async
	public void callExtract(Map<String,Object> map) throws Exception{
		log.debug("callExtract map:{}",map);
		/*
		 * Python C:\Users\Administrator\Desktop\Navy\VideoToImages_T.py -- fileNm=C:/Users/Administrator/Desktop/Navy/id0001_short.mp4
--filetype=jpg --width=1024 --height=768 --fps=30 --filePath= C:/Users/Administrator/Desktop/Navy/Output_Result/ --parentId=27050243502322302082 --user_id=test33

		 */
		String cmd = "";
		if(activeProfile.equals("dev")) {
			cmd += "C:/Users/Administrator/AppData/Local/Programs/Python/Python37/python.exe";
			cmd += " C:/Users/Administrator/Desktop/Navy/VideoToImages_T.py";
		}
        else if(activeProfile.equals("prod")) {
			
		}
		else {
			
		}
    	cmd += " --fileNm="+uploadPath+map.get("filePath")+"/"+map.get("fileNm");
    	cmd += " --filetype="+map.get("fileTypeTxt");
    	cmd += " --width="+map.get("width");
    	cmd += " --height="+map.get("height");
    	cmd += " --fps="+map.get("fps");
    	cmd += " --filePath="+uploadPath+map.get("filePath")+"/extract";
    	cmd += " --parentId="+map.get("movId");
    	cmd += " --user_id="+map.get("userId");
		
		
    	CommandLine cmdLine = CommandLine.parse(cmd);
    	try {
    		log.debug("pyton call.......");
    		log.debug("pyton cmd:{}",cmd);
    		DefaultExecutor executor = new DefaultExecutor();
    		executor.execute(cmdLine);
    		log.debug("pyton call finish.......");
    	}catch (Exception e){
    		e.printStackTrace();
    		log.error("python error:", e );
   	    }
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Async
	public void callLearning(Map<String,Object> map) throws Exception{
		log.debug("callLearing map:{}",map);
		/*
		 * C:\Users\Administrator\Desktop\Navy\venv\Scripts\python.exe c:/Users/Administrator/Desktop/Navy/mmdetection-2_25_1/tools/set_train_args.py 

--config_id=27050243502322302082 --create_user_id=‘test2’ --epoch=1 --learning_rate=0.0001 --batch_size=8 --optimizer_type=‘SGD’ 
--train_path=C:/Users/Administrator/Desktop/Navy/mmdetection-2_25_1/tools/Annotations/train_warships_mini/ 
--validation_path=C:/Users/Administrator/Desktop/Navy/mmdetection-2_25_1/tools/Annotations/val_warships_mini/ 
--class_nms=C:/Users/Administrator/Desktop/Navy/mmdetection-2_25_1/tools/warships_classes.txt 
--output_dir=C:/Users/Administrator/Desktop/Navy/mmdetection-2_25_1/work_dirs/

		 */

		
		BufferedWriter writer = null;
		try {
			//classNm Txt file  생성
			File learingDir = new File(String.valueOf(map.get("learningPath"))); 
			String validaionPath = map.get("learningPath")+"/"+"validaion";
			String outputPath = map.get("learningPath")+"/"+"output";
			if(!learingDir.exists()) {
				learingDir.setExecutable(false, true);
				learingDir.setReadable(true);
				learingDir.setWritable(false, true);
				learingDir.mkdirs();
				File validaionDir = new File(validaionPath); 
				validaionDir.setExecutable(false, true);
				validaionDir.setReadable(true);
				validaionDir.setWritable(false, true);
				validaionDir.mkdirs();
				File outputDir = new File(outputPath); 
				outputDir.setExecutable(false, true);
				outputDir.setReadable(true);
				outputDir.setWritable(false, true);
				outputDir.mkdirs();
			}
			String txtPath = map.get("learningPath")+"/"+map.get("id")+".txt";
	        File txtFile = new File(txtPath); // File객체 생성
	        if(!txtFile.exists()){
	        	txtFile.createNewFile(); 
	        }
	        // BufferedWriter 생성
	        writer = new BufferedWriter(new FileWriter(txtFile, true));
	        List<Map<String,Object>> classList = (List<Map<String,Object>>)map.get("classList");
	        for(Map<String,Object> classItem:classList) {
	        	writer.write(classItem.get("classNm")+"");
	            writer.newLine();
	        }
	        writer.flush(); 
	        writer.close(); 
	        
			List<Map<String,Object>> learningFileList = (List<Map<String,Object>>)map.get("learningFileList");
			Map<String,Object> xmlMap = null;
			List<Map<String,Object>> copyList = new ArrayList();
			List<Map<String,Object>> objList = null;
			String fileNm = "";
			int idx = 0;
			for(Map<String,Object> learningFile:learningFileList) {
				if(!fileNm.equals(learningFile.get("workFileNm"))) {
					if(idx > 0) {
						XmlUtil.makeObjectXmlFile(xmlMap);
					}
					xmlMap = new HashMap();
					xmlMap.put("workFileNm", learningFile.get("workFileNm"));
					xmlMap.put("learningPath", map.get("learningPath"));
					int size[] = ImgUtil.getSize(new File(uploadPath+String.valueOf(learningFile.get("srcFile"))));
					xmlMap.put("width", size[0]);
					xmlMap.put("height", size[1]);
					copyList.add(learningFile);
					objList = new ArrayList();
					xmlMap.put("objList", objList);
				}
				objList.add(learningFile);
				fileNm = String.valueOf(learningFile.get("workFileNm"));
				idx ++;
				if(idx == learningFileList.size()) {
					XmlUtil.makeObjectXmlFile(xmlMap);
				}
			}
			Map<String,Object> copyMap = new HashMap();
			copyMap.put("learningPath", map.get("learningPath"));
			copyMap.put("fileList", copyList);
			fileStorageService.move(copyMap);
			String cmd = "";
	        if(activeProfile.equals("dev")) {
	        	cmd += "C:/Users/Administrator/Desktop/Navy/venv/Scripts/python.exe";
	     		cmd += " c:/Users/Administrator/Desktop/Navy/mmdetection-2_25_1/tools/set_train_args.py";
			}
	        else if(activeProfile.equals("prod")) {
	        	cmd += "C:/Users/Administrator/Desktop/Navy/venv/Scripts/python.exe";
	     		cmd += " c:/Users/Administrator/Desktop/Navy/mmdetection-2_25_1/tools/set_train_args.py";
			}
			else {
				
			}
       
	    	cmd += " --config_id="+map.get("id");
	    	cmd += " --create_user_id="+map.get("userId");
	    	cmd += " --epoch="+map.get("epoch");
	    	cmd += " --learning_rate="+map.get("learningRate");
	    	cmd += " --batch_size="+map.get("batchSize");
	    	cmd += " --optimizer_type="+map.get("optimizer");
	    	cmd += " --train_path="+map.get("learningPath");
	    	cmd += " --validation_path="+validaionPath;
	    	cmd += " --class_nms="+map.get("learningPath")+"/"+map.get("id")+".txt";
	    	cmd += " --output_dir="+outputPath;
	    	cmd += " --auto_lr="+map.get("auto");
	    	log.debug("pyton cmd:{}",cmd);
			int j = 3;
			if(1>j) {
				CommandLine cmdLine = CommandLine.parse(cmd);
	    		log.debug("pyton call.......");
	    		DefaultExecutor executor = new DefaultExecutor();
	    		executor.execute(cmdLine);
	    		log.debug("pyton call finish.......");
			}
	    	
    	}catch (Exception e){
    		e.printStackTrace();
    		log.error("python error:", e );
   	    }
    	
	}
	
	@Async
	public void callGatheringObject(Map<String,Object> map) throws Exception{
		log.debug("callGatheringObject map:{}",map);
		/*
		 * Python C:\Users\Administrator\Desktop\Navy\ftp_Folderimgs_to_DB_T.py -- ftp_ip=192.168.25.65 -- ftp_port=21 -- ftp_user=test 
-- ftp_passwd=avm1234 -- ftp_directory=/ -- ftp_out_file_dir=/download/file 
--work_id=27050243502322302082 --create_user_id =test33

		 */
		String cmd = "";
        if(activeProfile.equals("dev")) {
        	cmd += "C:/Users/Administrator/AppData/Local/Programs/Python/Python37/python.exe";
    		cmd += " C:/Users/Administrator/Desktop/Navy/ftp_Folderimgs_to_DB_T.py";
		}
        else if(activeProfile.equals("prod")) {
			
		}
		else {
			
		}
        
    	cmd += " --ftp_ip="+map.get("gatherIp");
    	cmd += " --ftp_port="+map.get("gatherPort");
    	cmd += " --ftp_user="+"test";
    	cmd += " --ftp_passwd="+"avm1234";
    	cmd += " --ftp_directory="+map.get("gatherPath");
    	String destPath = localWorkPath;
		if(activeProfile.equals("dev")) {
			destPath = devWorkPath;
		}else if(activeProfile.equals("prod")) {
			destPath = prodWorkPath;
		}
		destPath += "/" + DateUtil.currentYear() + "/"+ DateUtil.currentMonth();
    	cmd += " --ftp_out_file_dir="+destPath;
    	cmd += " --work_id="+map.get("id");
    	cmd += " --create_user_id="+map.get("createUserId");
    	
    	CommandLine cmdLine = CommandLine.parse(cmd);
    	try {
    		log.debug("pyton call.......");
    		log.debug("pyton cmd:{}",cmd);
    		DefaultExecutor executor = new DefaultExecutor();
    		executor.execute(cmdLine);
    		log.debug("pyton call finish.......");
    	}catch (Exception e){
    		e.printStackTrace();
    		log.error("python error:", e );
   	    }
    	
	}
	@Async
	public void callTest(Map<String,Object> map) throws Exception{
		String cmd = "d:/app/Anaconda3/python.exe D:/work/py/test.py 10 ";

		//String cmd = "C:/Users/Administrator/AppData/Local/Programs/Python/Python37/python.exe";
		//cmd += " C:/Users/Administrator/Desktop/Navy/test.py 10 ";
		//String line = "python " + resolvePythonScriptPath("hello.py");
	    CommandLine cmdLine = CommandLine.parse(cmd);
	    try {
	    	log.debug("pyton call.......");
	        DefaultExecutor executor = new DefaultExecutor();
	        log.debug("pyton call finish.....");
	    executor.execute(cmdLine);
	    }catch(Exception e){
	    	e.printStackTrace();
	    	log.error( "python error:", e );
		}
    	/*
    	Runtime rt = Runtime.getRuntime();
	    try {
	    	log.debug("cmd:{}",cmd);
	        Process proc = rt.exec(cmd);
	        InputStream is = proc.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        String line;
	        while ((line = br.readLine()) != null) {
	          log.debug("line:{}",line);
	        }
	    }catch (Exception e){
	      log.error("error:", e );
	    }
	    */
		/*
		List<String> cmd = new ArrayList();
		cmd.add("d:/app/Anaconda3/python.exe");
		cmd.add("D:/work/py/img.py");
		//cmd.add("10");
		//cmd.add("20");
		ProcessBuilder builder = new ProcessBuilder(cmd);
		Process process = builder.start();
		int exitVal = process.waitFor();  // 자식 프로세스가 종료될 때까지 기다림
		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8")); // 서브 프로세스가 출력하는 내용을 받기 위해
		String line;
		while ((line = br.readLine()) != null) {
			log.debug("line :{}  " , line); // 표준출력에 쓴다
		}
		if(exitVal != 0) {
			log.debug("프로세스가 비정상 종료되었다.exitVal:{} ",exitVal);
		}
		*/
	}
	
	
	 
	
	
	
	
	
	
}
