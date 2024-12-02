package mil.gdl.nis.cls.vo;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LearningVo {
	private String id;
	private String epochs;
	private String learningRate;
	private String batchSize;
	private String optimizer;
	private String augMethod;
	private String repoPath;
	private String writeUserId;
	private Timestamp crtTs;
	private Timestamp updTS;

	@Builder
    public LearningVo(String id) {
        this.id = id;
      
	}
}