package de.sb.messenger.config;

import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;

public class HibernateDefaultConfig {
	public final static String DATASOURCE_DRIVER	= "com.mysql.cj.jdbc.Driver";
	public final static String DATASOURCE_URL		= "jdbc:mysql://localhost:3306/messenger";
	public final static String DATASOURCE_USER		= "root";
	public final static String DATASOURCE_PASSWORD	= "root";
	
    private final void setDataSourceProperties_withoutENV(final BasicDataSource dataSource) {
		dataSource.setDriverClassName(DATASOURCE_DRIVER);
		dataSource.setUrl(DATASOURCE_URL);
		dataSource.setUsername(DATASOURCE_USER);
		dataSource.setPassword(DATASOURCE_PASSWORD);
    }
    
    private final Properties hibernateProperties_withoutENV() {
    	final Properties poperties = new Properties();
    	
		poperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		poperties.setProperty("hibernate.show_sql", "true");
		poperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		poperties.setProperty("hibernate.archive.autodetection", "class, hbm");
		poperties.setProperty("hibernate.cache.use_second_level_cache", "false");
		poperties.setProperty("hibernate.cache.provider_class", "false");
		poperties.setProperty("hibernate.cache.use_second_level_cache", "org.hibernate.cache.HashtableCacheProvider");
		poperties.setProperty("hibernate.jdbc.batch_size", "0");
    	
    	return poperties;
    }
}
