package mil.gdl.nis.cmmn.vo;

import lombok.Builder;
import lombok.Data;

@Data
public class CodeVo {

	private String grpCd;
	
	private String grpCdNm;
	
	private String grpCdDesc;
	
	private String cd;
	
	private String cdNm;
	
	private String cdDesc;
	
	private String cdFilter;
	
	public CodeVo() {

	}
	
	@Builder
    public CodeVo(String grpCd, String cd) {
        this.grpCd = grpCd;
        this.cd = cd;
	}

}
