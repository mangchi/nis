package mil.gdl.nis.system.vo;

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
public class DbVo {

	
	private String id;
	
	@NotBlank(groups = {RegValid.class,ChgValid.class},message = "{error.notNull}")
    private String acquCd;
	
	private String acquNm;

	@NotBlank(groups = {RegValid.class,ChgValid.class},message = "{error.notNull}")
    private String dataClsCd;
	
	private String dataClsNm;
	
	@NotBlank(groups = {RegValid.class,ChgValid.class} ,message = "{error.notNull}")
	private String dayCd;
	
	private String dayNm;
	
	@NotBlank(groups = {RegValid.class,ChgValid.class} ,message = "{error.notNull}")
	private String writeUserId;
	
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
    public DbVo(String id) {
        this.id = id;

    }
    

	
}