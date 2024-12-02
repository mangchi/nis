package mil.gdl.nis.cmmn.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.cmmn.PwFailCacheData;
import mil.gdl.nis.cmmn.vo.CodeVo;
import mil.gdl.nis.dao.DAO;

@Slf4j
@RequiredArgsConstructor
@Service
public class CacheService {

	private final MessageSourceAccessor messageSource;
	private final CacheManager cacheManager;
	private final DAO dao;
	//private final CmmnService cmmnService;
	// private static final PwFailCacheData EMPTY_DATA = new PwFailCacheData();
	//private static Map<String, Integer> PwFailCacheMap = new HashMap<String, Integer>();
	
	@Value("${user.pwFaileCnt}")
    private int pwFailCnt;
	private static Map<String, PwFailCacheData> pwFailCacheMap = new HashMap<String, PwFailCacheData>();
	

	// @Cacheable(cacheNames = "sessionStore", key="#key")
	@Cacheable(value = "PwFailCacheData", key = "#key")
	public Map<String, PwFailCacheData> getPwdFailCacheData(final String key) {
		String[] replaceVal = new String[] {key};
		log.info(messageSource.getMessage("error.cache.key.null",replaceVal));
		/*
		@SuppressWarnings("unchecked")
		List<Map<String,String>> pwFailList = (List<Map<String,String>>)dao.selectList("User.selectPwFail");
		if(!pwFailList.isEmpty()) {
			//pwFailList.forEach(pwFail -> PwFailCacheMap.put(pwFail.get("userId"),Integer.parseInt(String.valueOf(pwFail.get("pwFailCnt")))));
			pwFailList.forEach(pwFail -> pwFailCacheMap.put(pwFail.get("userId"),PwFailCacheData.builder()
					.pwFailCnt(Integer.parseInt(String.valueOf(pwFail.get("pwFailCnt")))).pwFailDateTime(LocalDateTime.now()).build()));
		}
		*/
		return pwFailCacheMap;
	}

	@CachePut(cacheNames = "PwFailCacheData", key = "#key")
	public Map<String, PwFailCacheData> setPwdFailCacheData(final String key, final String userId,boolean clearYn) {
		// PwFailCacheData cacheData = new PwFailCacheData();
		pwFailCacheMap = this.getPwdFailCacheData(key);
		if(clearYn) {
			if(pwFailCacheMap.containsKey(userId)) {
				pwFailCacheMap.remove(userId);
			}
			return pwFailCacheMap;
		}
		else {
			if (pwFailCacheMap.containsKey(userId)) {
				PwFailCacheData PwFailCachObj = pwFailCacheMap.get(userId);
				PwFailCachObj.setPwFailCnt(PwFailCachObj.getPwFailCnt()+1);
				if(pwFailCnt == PwFailCachObj.getPwFailCnt()) {
					PwFailCachObj.setPwFailDateTime(LocalDateTime.now());
				}
				pwFailCacheMap.put(userId, PwFailCachObj);
			} else {
				pwFailCacheMap.put(userId, PwFailCacheData.builder()
						.pwFailCnt(1).pwFailDateTime(LocalDateTime.now()).build());
			}
		}
		return pwFailCacheMap;

	}

	// @CacheEvict(cacheNames = "sessionStore", key="#key")
	@CacheEvict(cacheNames = "PwFailCacheData", key = "#key")
	public boolean expirePwdFailCacheData(final String key) {
		log.info(messageSource.getMessage("msg.cache.expire", key));
		return true;
	}
	

	
	@SuppressWarnings("unchecked")
	@Cacheable(value = "codeCacheData", key = "#codeKey")
	public List<CodeVo> getCodeCacheData(final String codeKey) {
		//return cmmnService.getSelectCode(null);
		return (List<CodeVo>)dao.selectList("Cmmn.selectCode",null);
	}

	public void evictAllCaches() {
		cacheManager.getCacheNames().stream().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
	}
	
	public void getAllCacheNames() {
		cacheManager.getCacheNames().stream().forEach(cacheName -> log.debug(cacheName));
	}
	
	public int updateFailureCntInit() {
		return dao.update("User.updateFailureCntInit",null);
	}
	
	public int updateFailureCount(Map<String,Object> param) {
		return dao.update("User.updateFailureCount",param);
	}
	
	

}
