package mil.gdl.nis.cmmn.controller;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShutdownController implements ApplicationContextAware {
    
    private ApplicationContext context;
    
    /**
     * 
      * @Method Name	: shutdownContext
      * @author			: mangchi
      * @Date			: 2022. 4. 22.
      * @Description	: 시스템  종료 hook
      * @see            : curl -X POST localhost:port/shutdownContext
      * @History		:
     */
    @PostMapping("/shutdownContext")
    public void shutdownContext() {
        ((ConfigurableApplicationContext) context).close();
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;
        
    }
}
