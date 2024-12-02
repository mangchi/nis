package mil.gdl.nis.cmmn.vo;

import javax.validation.constraints.Min;

import org.apache.ibatis.session.RowBounds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({ "offset", "limit" })
public class PageInfo extends RowBounds {
    
	private Boolean isFirstPage = false;
	
	private Boolean isLastPage = false;
	
	private Integer pageNum;

	@Min(1)
	private Integer rowPerPage;
	
	private Integer totalPage;

	private Long totalCount = -1L; // set이 되지않았다는 의미로 -1
	
	private String orderBy;

	@Builder
    public PageInfo(int pageNum, int rowPerPage,String orderBy) {
		this.pageNum = pageNum;
		this.rowPerPage = rowPerPage;
		this.orderBy = orderBy;
	}
}
