package mil.gdl.nis.cmmn.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImgUtil {
	
	public static Map<String,Object> resizeFile(InputStream is,String savePath,String fileNm) throws IOException{
		BufferedImage bi = ImageIO.read(is);
		return resizeImg(bi, savePath, fileNm);
	}
	
	public static Map<String,Object> resizeFile(File imgFile,String savePath,String fileNm) throws IOException{
		BufferedImage bi = ImageIO.read(imgFile);
		return resizeImg(bi, savePath, fileNm);
	}
	
	public static int[] getSize(File imgFile) throws IOException{
		BufferedImage bi = ImageIO.read(imgFile);
	    int[] arr = new int[2];
	    arr[0] = bi.getWidth();
	    arr[1] = bi.getHeight();
	    return arr;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<String,Object> resizeImg(BufferedImage bi, String savePath,String fileNm) throws IOException {
		Map<String,Object> sizeMap = new HashMap();
		String formatType = "PNG";
		int originWidth = bi.getWidth();
	    int originHeight = bi.getHeight();
	    // 변경할 가로 길이
        int newWidth = 1010;
        int newHeight = 600;
        if (originWidth > newWidth) {
        	if(originHeight > newHeight) {
        		newHeight = (originHeight * newWidth) / originWidth;
        	}
        	if(newHeight > 600) {
        		newHeight = 600;
        	}
        }
        if (originWidth <= newWidth) {
        	newWidth = originWidth;
        	if(originHeight > newHeight) {
        		newHeight = (originWidth * newHeight) / originHeight;
        	}
        	if(newHeight > 600) {
        		newHeight = 600;
        	}
        }
        // 이미지 품질 설정         
 		// Image.SCALE_DEFAULT : 기본 이미지 스케일링 알고리즘 사용
 		// Image.SCALE_FAST : 이미지 부드러움보다 속도 우선
 		// Image.SCALE_REPLICATE : ReplicateScaleFilter 클래스로 구체화 된 이미지 크기 조절 알고리즘
 		// Image.SCALE_SMOOTH : 속도보다 이미지 부드러움을 우선
 		// Image.SCALE_AREA_AVERAGING : 평균 알고리즘 사용
        sizeMap.put("width", newWidth);
        sizeMap.put("height", newHeight);
        //int last = fileNm.lastIndexOf(".");
        //sizeMap.put("workFileNm", fileNm.substring(0, last+1)+formatType);
        sizeMap.put("workFileNm", fileNm);
        Image resizeImage = bi.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = newImage.getGraphics();
        graphics.drawImage(resizeImage, 0, 0, null);
        graphics.dispose();
        File saveDir = new File(savePath);
        if(!saveDir.exists()) {
        	saveDir.mkdir();
        }
        File newFile = new File(savePath+File.separator+fileNm);
        
        ImageIO.write(newImage, formatType, newFile);
        return sizeMap;
	}

}
