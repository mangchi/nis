package mil.gdl.nis.handler;

import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mil.gdl.nis.cmmn.util.PythonUtil;
import mil.gdl.nis.event.AppEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventHandler {
	
	private final PythonUtil pythonUtil;
  
    @SuppressWarnings("unchecked")
	@Async
    @TransactionalEventListener
    public void handle(AppEvent event) {
    	
    	Map<String,Object> map = (Map<String,Object>)event.getObj();
    	try {
    		if(null == map.get("type")) {
    			throw new Exception("EventHandler type is null");
    		}
    		else {
    			if(map.get("type").equals("WORK")) {
    				pythonUtil.callGatheringObject(map);
                	
                }
                else if(map.get("type").equals("EXTRACT")){
                	pythonUtil.callExtract(map);
                }
                else if(map.get("type").equals("LEARNING")){
                	//pythonUtil.callLearnginObject(map);
                }
                else if(map.get("type").equals("TEST")){
                	pythonUtil.callTest(map);
                }
                else {
                	throw new Exception("EventHandler type is not valid");
                }
    		}
            
    	}catch(Exception e) {
    		log.error("error:", e );
    	}
    }



}
