package mil.gdl.nis.dataset.vo;

import java.sql.Timestamp;

import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mil.gdl.nis.dataset.vo.DataValidGroups.AirValid;
import mil.gdl.nis.dataset.vo.DataValidGroups.BaseValid;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DatasetVo {

	
	private String id;
	
	//수집종류
	
	@NotBlank(groups = {BaseValid.class,AirValid.class},message = "{error.notNull}")
	private String sourceType;

	//데이타 수집시간
	@NotBlank(groups = {BaseValid.class,AirValid.class},message = "{error.notNull}")
    private String collectionDt;
	
	//데이타 수집위치
	@NotBlank(groups = {BaseValid.class,AirValid.class},message = "{error.notNull}")
	private String collectionPos;
	
	//데이타수집수단
	@NotBlank(groups = {BaseValid.class,AirValid.class},message = "{error.notNull}")
	private String collectionDeviceCd;
	
	//자료구분
	@NotBlank(groups =BaseValid.class,message = "{error.notNull}")
    private String stillOrMovie;
	
	//데이터 종류
	@NotBlank(groups =BaseValid.class,message = "{error.notNull}")
	private String dataType;
	
	//영상이미지 종류
	@NotBlank(groups =BaseValid.class,message = "{error.notNull}")
	private String dayOrNight;
	
	//데이터 춢처
	@NotBlank(groups =BaseValid.class,message = "{error.notNull}")
	private String sourceCd;
	
	//파일변경유무
	@NotBlank(groups = {BaseValid.class,AirValid.class},message = "{error.notNull}")
	private String isChangeFile;
		
	//데이터 X좌표
	@NotBlank(groups =BaseValid.class,message = "{error.notNull}")
	private String dataCoordX;
	
	//데이터 Y좌표
	@NotBlank(groups =BaseValid.class,message = "{error.notNull}")
	private String dataCoordY;
	

    private String parentId;
	
	private String etc;
	
	@NotBlank(groups =AirValid.class,message = "{error.notNull}")
	private String dataCd;
	
	@NotBlank(groups =AirValid.class,message = "{error.notNull}")
	private String nationCd;
	
	@NotBlank(groups =AirValid.class,message = "{error.notNull}")
	private String shipClass;
	
	@NotBlank(groups =AirValid.class,message = "{error.notNull}")
	private String shipNm;
	
	//진행 상태
	private String status;

	//레이블링 결과
	private String labelingResult;
	
	//파일 아이디
	private String fileId;
		
	private String fileType;
		
	private int seq;
		
	private String filePath;
		
	private String originFileNm;
	
	private String workFilePath;
	
	private String workFileNm;
		
		
	private String fileNm;

    private String createUserId;
	
	private String createUserNm;
	
	private String updateUserId;
	
	private String updateUserNm;
	
	private Timestamp createTs;
	private Timestamp updateTS;
	private Timestamp approvedTs;

    @Builder
    public DatasetVo(String id) {
        this.id = id;

    }
    

	
}