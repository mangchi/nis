package mil.gdl.nis.cmmn.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.annotation.Log;
import mil.gdl.nis.annotation.PageExcel;
import mil.gdl.nis.cmmn.service.FileStorageService;
import mil.gdl.nis.cmmn.util.ExcelUtil;
import mil.gdl.nis.cmmn.vo.FileVo;
import mil.gdl.nis.cmmn.vo.FileVo.FileVoBuilder;
import mil.gdl.nis.log.service.LogService;

@Slf4j
@CrossOrigin("http://localhost:8080")
@RequiredArgsConstructor
@Controller
public class FileController {
	
	private final FileStorageService fileStorageService;
	
	private final LogService logService;
	
	private final MessageSourceAccessor messageSource;
	
	private final ExcelUtil excelUtil;
	
	//private final ServletContext sc;

	
	@Value("${spring.servlet.multipart.location}")
	private String uploadPath;

	@PostMapping("/upload")
	public @ResponseBody Map<String,Object> uploadFile(@RequestParam("file") MultipartFile[] uploadFiles) {
		Map<String,Object> rtnMap = new HashMap<>();
		List<Map<String,Object>> fileList = new ArrayList<>();
		try {
			for(MultipartFile file:uploadFiles) {
				Map<String,Object> fileInfo = new HashMap<>();
				fileStorageService.save(file,fileInfo);
				fileInfo.put("size", file.getSize());
				fileInfo.put("originalFileNm", file.getOriginalFilename());
				fileInfo.put("contentType", file.getContentType());
				fileList.add(fileInfo);
			}
			rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
			rtnMap.put("fileList", fileList);
	
		} catch (Exception e) {
			rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
		}
		return rtnMap;
	}
	
	@PostMapping("/uploadFileDb")
	public @ResponseBody Map<String,Object> uploadFileDb(@RequestParam("file") MultipartFile[] uploadFiles,@RequestParam Map<String,Object> appendParam) {
	//public @ResponseBody Map<String,Object> uploadFileDb(@RequestParam("file") MultipartFile[] uploadFiles,@RequestParam("fileParam") List<Map<String,Object>> fileParam) {
		Map<String,Object> rtnMap = new HashMap<>();
		try {
			log.debug("appendParam:{}",appendParam);
			for(MultipartFile file:uploadFiles) {
				log.debug("size:{}", file.getSize());
				log.debug("originalFileNm:{}", file.getOriginalFilename());
				log.debug("contentType:{}", file.getContentType());
				log.debug("fileNm:{}", file.getName());

			}
			Map<String,Object> fileMap = fileStorageService.save(uploadFiles,appendParam);
			rtnMap.put("fileId", fileMap.get("fileId"));
			rtnMap.put("isChangeFile", fileMap.get("isChangeFile"));
			rtnMap.put("success_msg", messageSource.getMessage("msg.success"));
		} catch (Exception e) {
			log.error("error:", e );
			rtnMap.put("fail_msg", messageSource.getMessage("msg.fail"));
		}
		return rtnMap;
	}

	@GetMapping(value="/files" , produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FileVoBuilder>> getListFiles() {
		List<FileVoBuilder> fileVos = fileStorageService.loadAll().map(path -> {
			String filename = path.getFileName().toString();
			String url = MvcUriComponentsBuilder
					.fromMethodName(FileController.class, "getFile", path.getFileName().toString()).build().toString();
			
			return FileVo.builder().name(filename).url(url);
		}).collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.OK).body(fileVos);
	}

	@Log
	@GetMapping(value= "/fileId/{fileId}/{fileSeq}/{workType}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Resource> getFile(@PathVariable String fileId,@PathVariable int fileSeq,@PathVariable String workType,HttpServletRequest req,HttpServletResponse res) throws Exception{
		Map<String,Object> map = new HashMap<>();
		map.put("id",fileId);
		map.put("seq", fileSeq);
		Map<String,Object> fileInfo = fileStorageService.select(map);
		
		Resource file = null;
		HttpHeaders headers = new HttpHeaders();
		if(workType.equals("N")) {
			file = fileStorageService.load(String.valueOf(fileInfo.get("filePath")),String.valueOf(fileInfo.get("fileNm")));
		}else {
			file = fileStorageService.load(String.valueOf(fileInfo.get("workFilePath")),String.valueOf(fileInfo.get("workFileNm")));
		}
		
		/*
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.contentType(MediaType.valueOf(String.valueOf(fileInfo.get("fileType"))))
				.body(file);
		*/
		
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
		ResponseEntity<Resource> responseEntity = new ResponseEntity<>(file, headers, HttpStatus.OK);
		return responseEntity;
		
		
		
	}

	@Log
	@PostMapping("/fileDownZip")
	public void downLoadZip(@RequestBody Map<String,Object> param,HttpServletResponse response) throws Exception{
		List<String> fileNmList = fileStorageService.selectFileListByIds(param);
		response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=download.zip");
        try(ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
            for(String fileNm : fileNmList) {
            	log.debug("fileNm:{}",fileNm);
                FileSystemResource fileSystemResource = new FileSystemResource(uploadPath+fileNm);
                ZipEntry zipEntry = new ZipEntry(fileSystemResource.getFilename());
                zipEntry.setSize(fileSystemResource.contentLength());
                zipEntry.setTime(System.currentTimeMillis());

                zipOutputStream.putNextEntry(zipEntry);

                StreamUtils.copy(fileSystemResource.getInputStream(), zipOutputStream);
                zipOutputStream.closeEntry();
            }
            zipOutputStream.finish();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
	}
	
    @PageExcel
	@PostMapping("/fileDownExcel")
	public void downLoadExcel(@RequestBody Map<String,Object> param,HttpServletResponse response) throws Exception{
		List<Map<String,Object>> list = logService.getList(param);
		excelUtil.createExcelDownloadResponse(response,param, list);
		
	}
	
	@PostMapping("/fileDown")
	public void downLoad(@RequestBody Map<String,Object> param,HttpServletResponse response) throws Exception{
		Map<String,Object> fileInfo = fileStorageService.select(param);
		byte[] fileByte = FileUtils.readFileToByteArray(new File(uploadPath+fileInfo.get("filePath")+"/"+fileInfo.get("fileNm")));

	    response.setContentType("application/octet-stream");
	    response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode("'"+fileInfo.get("fileNm")+"'", "UTF-8")+"\";");
	    response.setHeader("Content-Transfer-Encoding", "binary");

	    response.getOutputStream().write(fileByte);
	    response.getOutputStream().flush();
	    response.getOutputStream().close();
	}
}
