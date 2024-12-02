package mil.gdl.nis.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MsgConfig {

	@Bean
	public LocaleResolver defaultLocaleResolver() {
		AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
		localeResolver.setDefaultLocale(Locale.KOREAN);
		log.info("localeResolver Bean Created.");
		return localeResolver;
	}

	@Bean 
	public ReloadableResourceBundleMessageSource messageSource() { 
		Locale.setDefault(Locale.KOREAN); // 제공하지 않는 언어로 들어왔을 때 처리 
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource(); 
		messageSource.setBasename("classpath:/messages/messages"); 
		messageSource.setDefaultEncoding(Encoding.DEFAULT_CHARSET.toString()); 
		messageSource.setDefaultLocale(Locale.getDefault()); 
		messageSource.setCacheSeconds(600); 
		log.info("messageSource Bean Created. Default Charset is {} and Default Locale is {}", Encoding.DEFAULT_CHARSET.toString(), Locale.getDefault()); 
		return messageSource; } 
	
	
	@Bean public MessageSourceAccessor messageSourceAccessor ( @Autowired ReloadableResourceBundleMessageSource messageSource) {
		return new MessageSourceAccessor(messageSource); 
	}
	

	
	@Bean
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setValidationMessageSource(messageSource());

        return factoryBean;
    }


}
