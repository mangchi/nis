package mil.gdl.nis.sample.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SampleMapper {
	
	List<Map<String,Object>> selectSampleList(Map<String,Object> map) throws Exception;

}
