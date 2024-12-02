package mil.gdl.nis.cmmn.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExcelUtil {

	public  List<ArrayList<String>> getExcelData(String fileNm) throws Throwable {
		List<ArrayList<String>> excelData = new ArrayList<ArrayList<String>>();
		FileInputStream file = new FileInputStream(fileNm);
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		int rowindex = 0;
		int columnindex = 0;
		// 시트 수 (첫번째에만 존재하므로 0을 준다)
		// 만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
		XSSFSheet sheet = workbook.getSheetAt(0);
		// 행의 수
		int rows = sheet.getPhysicalNumberOfRows();
		for (rowindex = 0; rowindex < rows; rowindex++) {
			// 행을읽는다
			XSSFRow row = sheet.getRow(rowindex);
			ArrayList<String> rowList = null;
			if (row != null) {
				rowList = new ArrayList<String>();
				// 셀의 수
				int cells = row.getPhysicalNumberOfCells();
				for (columnindex = 0; columnindex <= cells; columnindex++) {
					// 셀값을 읽는다
					XSSFCell cell = row.getCell(columnindex);

					String value = "";
					// 셀이 빈값일경우를 위한 널체크
					if (cell == null) {
						continue;
					} else {
						// 타입별로 내용 읽기
						switch (cell.getCellType()) {
						case FORMULA:
							value = cell.getCellFormula();
							break;
						case NUMERIC:
							value = cell.getNumericCellValue() + "";
							break;
						case STRING:
							value = cell.getStringCellValue() + "";
							break;
						case BLANK:
							value = cell.getBooleanCellValue() + "";
							break;
						case ERROR:
							value = cell.getErrorCellValue() + "";
							break;
						default:
							break;
						}
					}
					rowList.add(value);
					System.out.println(rowindex + "번 행 : " + columnindex + "번 열 값은: " + value);
				}
				excelData.add(rowList);
				workbook.close();
			}
		}
		return excelData;
	}

	public  void createExcelDownloadResponse(HttpServletResponse response,Map<String,Object> param, List<Map<String, Object>> list) {
		try {
			log.debug("createExcelDownloadResponse list:{}",list);
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			//Sheet sheet = null;
			SXSSFSheet sheet = null;
			CellStyle numberCellStyle = workbook.createCellStyle();
			numberCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
			// 파일명
			String fileName = "접속이력";
			String headNm = "자료 업로드 일시";
			if(param.containsKey("upOrDown") && param.get("upOrDown").equals("D")) {
				headNm = "자료 다운로드 일시";
			}
			String[] header0 = { "NO", "사용자명", "IP", "접속일시" };
			String[] header1 = { "NO", "사용자명", "열람자료정보", "열람일시" };
			String[] header2 = { "NO", "사용자명", headNm, "자료출처","자료구분", "자료종류", "자료정보" };
			Row row = null;
			Cell cell = null;
			
			CellStyle cellStyle = workbook.createCellStyle();
			//색상 지정
	        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 이걸 안쓰면 안채워 진다
	        //cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	        cellStyle.setAlignment(HorizontalAlignment.CENTER);

			int bIdx = 0;
			if (param.get("searchType").equals("CONN")) {
				sheet = workbook.createSheet("접속이력");
				
				
				row = sheet.createRow(0);
				for (int i = 0; i < header0.length; i++) {
					cell = row.createCell(i);
					cell.setCellValue(header0[i]);
					cell.setCellStyle(cellStyle);
				}
				for (Map<String, Object> item : list) {
					bIdx++;
					
					row = sheet.createRow(bIdx);
					cell = row.createCell(0);
					cell.setCellStyle(numberCellStyle);
					cell.setCellValue(bIdx);
					cell = row.createCell(1);
					cell.setCellValue(item.get("createUserNm") == null ? "" : String.valueOf(item.get("createUserNm")) );
					cell = row.createCell(2);
					cell.setCellValue(item.get("connectIp") == null ? "" : String.valueOf(item.get("connectIp")) );
					cell = row.createCell(3);
					cell.setCellValue(item.get("createTs") == null ? "" : String.valueOf(item.get("createDt")) );
				}
				sheet.trackAllColumnsForAutoSizing();
		        sheet.autoSizeColumn(3);
		   

			}
			else if (param.get("searchType").equals("VIEW")) {
				fileName = "자료열람이력";
				sheet = workbook.createSheet("자료열람");
				
				row = sheet.createRow(0);
				for (int i = 0; i < header1.length; i++) {
					cell = row.createCell(i);
					cell.setCellValue(header1[i]);
					cell.setCellStyle(cellStyle);
				}
				for (Map<String, Object> item : list) {
					bIdx++;
					row = sheet.createRow(bIdx);
					cell = row.createCell(0);
					cell.setCellStyle(numberCellStyle);
					cell.setCellValue(bIdx);
					cell = row.createCell(1);
					cell.setCellValue(item.get("createUserNm") == null ? "" : String.valueOf(item.get("createUserNm")) );
					cell = row.createCell(2);
					cell.setCellValue(item.get("originFileNm") == null ? "" : String.valueOf(item.get("originFileNm")) );
					cell = row.createCell(3);
					cell.setCellValue(item.get("createTs") == null ? "" : String.valueOf(item.get("createDt")) );
				}
				sheet.trackAllColumnsForAutoSizing();
				sheet.autoSizeColumn(2);
		        sheet.autoSizeColumn(3);

			} else {
				
				if(param.get("upOrDown").equals("U")) {
					fileName = "자료업로드 이력";
					sheet = workbook.createSheet("자료업로드");
				}else {
					fileName = "다운로드 이력";
					sheet = workbook.createSheet("다운로드");
				}
			
				row = sheet.createRow(0);
				for (int i = 0; i < header2.length; i++) {
					cell = row.createCell(i);
					cell.setCellValue(header2[i]);
					cell.setCellStyle(cellStyle);
				}
				for (Map<String, Object> item : list) {
					bIdx++;
					row = sheet.createRow(bIdx);
					cell = row.createCell(0);
					cell.setCellStyle(numberCellStyle);
					cell.setCellValue(bIdx);
					cell = row.createCell(1);
					cell.setCellValue(item.get("createUserNm") == null ? "" : String.valueOf(item.get("createUserNm")) );
					cell = row.createCell(2);
					cell.setCellValue(item.get("createTs") == null ? "" : String.valueOf(item.get("createDt")) );
					cell = row.createCell(3);
					cell.setCellValue(item.get("sourceNm") == null ? "" : String.valueOf(item.get("sourceNm")) );
					cell = row.createCell(4);
					cell.setCellValue(item.get("upOrDown") == null ? "" : String.valueOf(item.get("upOrDown")) );
					cell = row.createCell(5);
					cell.setCellValue(item.get("fileType") == null ? "" : String.valueOf(item.get("fileType")) );
					cell = row.createCell(6);
					cell.setCellValue(item.get("originFileNm") == null ? "" : String.valueOf(item.get("originFileNm")) );
				}
				sheet.trackAllColumnsForAutoSizing();
				sheet.autoSizeColumn(2);
				sheet.autoSizeColumn(3);
				sheet.autoSizeColumn(4);
		        sheet.autoSizeColumn(5);
		        sheet.autoSizeColumn(6);
			}

			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition",
					"attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");


			workbook.write(response.getOutputStream());
			workbook.close();

		} catch (IOException ie) {
			log.error("error:", ie );
		}
		catch(Exception e) {
			log.error("error:", e );
		}

	}

}
