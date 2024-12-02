package mil.gdl.nis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.cmmn.service.CacheService;


@Slf4j
@EnableAsync
@RequiredArgsConstructor
@EnableCaching
@SpringBootApplication
public class NisApplication {
	
	private final CacheService cacheService;
	@Value("${user.pwFailKey}")
    private String pwFailKey;
	@Value("${code.codeKey}")
    private String codeKey;
	public static void main(String[] args) {

		SpringApplication.run(NisApplication.class, args);
	}
	
	@Bean(initMethod = "initNis")
    InitNisApp initNisApp() {
        return new InitNisApp();
    }
	
	class InitNisApp {
		public void initNis() {
			log.debug("Init Nis Application....................................");
		
			cacheService.getPwdFailCacheData(pwFailKey);
			cacheService.getCodeCacheData(codeKey);
			//cacheService.updateFailureCntInit();
		

		}
	}
	
	
	@Bean(destroyMethod = "destoryNis")
    ShutdownNisApp destoryNisApp() {
        return new ShutdownNisApp();
    }
	
	class ShutdownNisApp {
		public void destoryNis() {
			log.debug("Shutdown Nis Application...................................");
			/*
			Map<String,Integer> pwdFailCnt = cacheService.getPwdFailCacheData(pwFailKey);
			if(!pwdFailCnt.isEmpty()) {
				for(String key : pwdFailCnt.keySet()) {
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("userId",key);
					map.put("pwFailCnt",pwdFailCnt.get(key));
					cacheService.updateFailureCount(map);
				}
				
			}
			*/

		}
	}
	

}
