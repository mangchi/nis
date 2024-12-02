package mil.gdl.nis.bbs.vo;

import java.sql.Timestamp;

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
public class BbsVo {
    private int rn;
	private String id;
	
	private String grpId;
	
	@NotBlank(message = "{error.notNull}")
	private String bbsType;
	
	@NotBlank(message = "{error.notNull}")
	private String title;
	
	@NotBlank(message = "{error.notNull}")
	private String content;
	
	private String createUserId;
	
	private String createUserNm;
	
	private String updateUserId;
	
	private String updateUserNm;
	
	private Timestamp createTs;
	private Timestamp updateTS;
	

	
	@Builder
    public BbsVo(String bbsType, String title, String content) {
        this.bbsType = bbsType;
        this.title = title;
        this.content = content;
	}

}
