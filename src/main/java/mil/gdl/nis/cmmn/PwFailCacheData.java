package mil.gdl.nis.cmmn;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
public class PwFailCacheData {
		
	private int pwFailCnt;
	
	private LocalDateTime pwFailDateTime;
	
	@Builder
    public PwFailCacheData(int pwFailCnt, LocalDateTime pwFailDateTime) {
        this.pwFailCnt = pwFailCnt;
        this.pwFailDateTime = pwFailDateTime;

    }

}
