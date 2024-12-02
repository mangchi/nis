package mil.gdl.nis.movImg.vo;

import java.sql.Timestamp;

import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mil.gdl.nis.user.vo.UserValidGroups.ChgValid;
import mil.gdl.nis.user.vo.UserValidGroups.RegValid;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MovImgVo {

	
	private String id;
	
	@NotBlank(groups = {RegValid.class,ChgValid.class},message = "{error.notNull}")
    private Timestamp dataAcquTs;
	
	private String dataAcquLoc;
    
	//자료구분
	@NotBlank(groups = {RegValid.class,ChgValid.class},message = "{error.notNull}")
    private String dataClsCd;
	
	//데이터 종류
	private String objCd;
	
	private String dataAcquMth;
	
	@NotBlank(groups = {RegValid.class,ChgValid.class} ,message = "{error.notNull}")
	private String dayCd;
	
	//데이터 춢처
	private String dataOrgin;
	
	//데이터 좌표
	@NotBlank(groups = {RegValid.class,ChgValid.class} ,message = "{error.notNull}")
	private String dataCoord;
	
	private String writeUserNm;
	
	@NotBlank(groups = {RegValid.class,ChgValid.class} ,message = "{error.notNull}")
	private String progStatCd;
	
	private String progStatNm;
	
	@NotBlank(groups = {RegValid.class,ChgValid.class} ,message = "{error.notNull}")
	private String aprvStatCD;
	
	private String aprvStatNm;
	
	@NotBlank(groups = {RegValid.class,ChgValid.class} ,message = "{error.notNull}")
	private String crtrUserId;

    
    private Timestamp crtTs;
    private Timestamp updTS;


    @Builder
    public MovImgVo(String id) {
        this.id = id;

    }
    

	
}