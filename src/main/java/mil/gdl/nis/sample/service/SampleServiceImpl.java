package mil.gdl.nis.sample.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mil.gdl.nis.cmmn.error.ErrorCode;
import mil.gdl.nis.dao.DAO;
import mil.gdl.nis.exception.BizException;

@RequiredArgsConstructor
@Service
public class SampleServiceImpl implements SampleService{
	

    private final DAO dao;
	/*
	 * private final SampleMapper sampleMapper;
	 * 
	 * public SampleServiceImpl(SampleMapper sampleMapper) { this.sampleMapper =
	 * sampleMapper; }
	 */
	
	/*
	 * @Override public List<Map<String,Object>> getSampleList(Map<String,Object>
	 * map) throws Exception{ return sampleMapper.selectSampleList(map);
	 * 
	 * }
	 */
	@Override
	public Map<String,Object> getUser(Map<String,Object> map) throws Exception{
		//throw new BizException("Duplicated Email", ErrorCode.USER_EXISTS);
		return dao.selectMap("User.selectUser", map).orElseThrow(() -> new BizException(ErrorCode.USER_NOT_FOUND));
	}

}
