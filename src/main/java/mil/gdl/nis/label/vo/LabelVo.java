package mil.gdl.nis.label.vo;

import java.sql.Timestamp;
import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LabelVo {

	
	private String id;
	

	//데이타 수집위치
	@NotBlank(message = "{error.notNull}")
	private String collectionPos;
	
	//데이타수집수단
	private String collectionDeviceCd;
	
	private String collectionDeviceNM;
	
	//자료구분
    private String stillOrMovie;
	
	
	//데이터 종류

	private String dataType;
	
	//영상이미지 종류
	private String dayOrNight;
	
	private String dayOrNightNm;
	
	//데이터 춢처
	@NotBlank(message = "{error.notNull}")
	private String sourceCd;

	
	//데이터 X좌표
	@NotBlank(message = "{error.notNull}")
	private String dataCoordX;
	
	//데이터 Y좌표
	@NotBlank(message = "{error.notNull}")
	private String dataCoordY;
	
	@NotBlank(message = "{error.notNull}")
    private String dataId;
	
	//진행 상태
	private String status;

	//레이블링 결과
	private String labelingResult;
	
	//파일 아이디
	private String fileId;
	
	private int seq;
	
	private String filePath;
	
	private String workFilePath;
	
	private String originFileNm;
	
	
	private String fileNm;
	
	private String workFileNm;
	
	
	private String createUserId;
	
	private String createUserNm;

    private Timestamp createTs;
    
    private String updateUserId;
    
    private Timestamp updateTs;
    
    private List<ObjectVo> objectList;


    @Builder
    public LabelVo(String id) {
        this.id = id;

    }
    

	
}