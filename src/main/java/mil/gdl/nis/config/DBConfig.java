package mil.gdl.nis.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


@Configuration
@EnableTransactionManagement
public class DBConfig {
	@Value("${spring.datasource.username}")
	private String username;
	@Value("${spring.datasource.password}")
	private String password;
	@Value("${spring.datasource.url}")
	private String jdbcUrl;
	@Value("${spring.datasource.driverClassName}")
	private String driverClassName;
	
	@Value("${spring.datasource.hikari.maximum-pool-size}")
	private int maximumPoolSize;
	@Value("${spring.datasource.hikari.connection-timeout}")
	private long connectionTimeout;
	@Value("${spring.datasource.hikari.connection-init-sql}")
	private String connectionInitSql;

	@Bean
	@Qualifier("dataSource")
	@Primary
	public DataSource dataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setUsername(username);
		hikariConfig.setPassword(password);
		hikariConfig.setJdbcUrl(jdbcUrl);
		hikariConfig.setMaximumPoolSize(75);
		hikariConfig.setDriverClassName(driverClassName);
		hikariConfig.setMaximumPoolSize(maximumPoolSize);
		hikariConfig.setConnectionTimeout(connectionTimeout);
		hikariConfig.setConnectionInitSql(connectionInitSql);
		return new HikariDataSource(hikariConfig);
	}
	
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}
	


}
