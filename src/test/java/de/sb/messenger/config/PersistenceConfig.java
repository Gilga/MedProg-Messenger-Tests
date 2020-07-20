package de.sb.messenger.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
//import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.sb.messenger.LogManager;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager")
@PropertySource({ 
	"classpath:META-INF/persistence-mysql.properties" 
})
@ComponentScan({ "de.sb.messages.*" })
@Import({LogManager.class,}) //SecurityConfig.class,
//@EnableWebMvc
public class PersistenceConfig {
	
	static String persistenceUnitName = "messenger";
	static final String[] packagesToScan = { "de.sb.messages.persistence" };

	private static final Logger LOGGER = LogManager.getRootLogger();
	
	@Autowired
	private Environment ENV;

    public PersistenceConfig() {
    	super();
    	LOGGER.debug("PersistenceConfig");
    }
    
 	/**
	 * SessionFactory
	 * @return SessionFactory
	 */
    @Autowired
    @Bean(name = "sessionFactory")
    public LocalSessionFactoryBean sessionFactory() {
    	LOGGER.debug("sessionFactory");
    	
        final LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(getDataSource());
        sessionFactory.setPackagesToScan(packagesToScan);
        sessionFactory.setHibernateProperties(getProperties());

        return sessionFactory;
    }
    
//    @Autowired
//    @Bean(name = "hibernate5AnnotatedSessionFactory")
//    public LocalSessionFactoryBuilder getLocalSessionFactoryBuilderBean() {
//        LocalSessionFactoryBuilder localSessionFactoryBean = new LocalSessionFactoryBuilder(getDataSource());
//        localSessionFactoryBean.scanPackages(packagesToScan);
//        localSessionFactoryBean.addProperties(getProperties());
//        localSessionFactoryBean.buildSessionFactory();
//
//        return localSessionFactoryBean;
//    }
    
    @Autowired
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean getLocalEntityManagerFactoryBean() {
       LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
       
       //emf.setPersistenceXmlLocation("classpath:META-INF/persistence.xml");
       emf.setPersistenceUnitName(persistenceUnitName);
       emf.setPackagesToScan(packagesToScan);
       
       emf.setDataSource(getDataSource());
       emf.setJpaVendorAdapter(getJpaVendorAdapter());
       emf.setJpaDialect(getJpaDialect());
       emf.setJpaProperties(getProperties());
       emf.afterPropertiesSet();
  
       return emf;
    }
    
//    @Bean
//    public EntityManagerFactory entityManagerFactory() {
//        LocalContainerEntityManagerFactoryBean emf = localContainerEntityManagerFactoryBean();
//        return emf.getObject();
//    }
    
    @Autowired
    @Bean(name = "dataSource")
    public DataSource getDataSource() {
    	LOGGER.debug("dataSource");
    	
        final DriverManagerDataSource  dataSource = new DriverManagerDataSource ();
        setDataSourceProperties(dataSource);
         
        return dataSource;
    }
    
    @Autowired
    @Bean(name = "transactionManager")
    public PlatformTransactionManager getTransactionManager(@Qualifier("entityManagerFactory") EntityManagerFactory emf) {
    	LOGGER.debug("JpaTransactionManager");
    	
		// hibernate dependency
		// final HibernateTransactionManager tm = new HibernateTransactionManager();
		// tm.setSessionFactory(sessionFactory().getObject());
    	
    	// JPA dependency (more generic = better approach)
    	//final JpaTransactionManager tm = new JpaTransactionManager();
        //tm.setEntityManagerFactory(getEntityManagerFactory().getObject());
    	
    	final JpaTransactionManager tm = new JpaTransactionManager(emf);
    	
    	tm.setDataSource(getDataSource());
    	tm.setJpaDialect(getJpaDialect());
    	tm.setEntityManagerFactory(emf);
    	tm.afterPropertiesSet();
        
        return tm;
    }
    
    @Autowired
    @Bean(name = "jpaDialect")
    public JpaDialect getJpaDialect() {
        return new HibernateJpaDialect();
    }
    
    @Autowired
    @Bean(name = "jpaVendorAdapter")
    HibernateJpaVendorAdapter getJpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.MYSQL);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        return vendorAdapter;
    }
    
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
    	LOGGER.debug("PersistenceExceptionTranslationPostProcessor");
    	
    	return new PersistenceExceptionTranslationPostProcessor();
    }
    
   // ---
    
    private final void setDataSourceProperties(final DriverManagerDataSource dataSource) {
		dataSource.setDriverClassName(ENV.getProperty("jdbc.driverClassName"));
		dataSource.setUrl(ENV.getProperty("jdbc.url"));
		dataSource.setUsername(ENV.getProperty("jdbc.user"));
		dataSource.setPassword(ENV.getProperty("jdbc.pass"));
		//dataSource.setDefaultSchema(ENV.getProperty("jdbc.defaultSchema"));
    }
    
    private final Properties getProperties() {
        final Properties poperties = new Properties();
        
        String[] property_names = {
    		"hibernate.hbm2ddl.auto",
            "hibernate.dialect",
            "hibernate.show_sql",
            "hibernate.archive.autodetection",
            "hibernate.cache.use_second_level_cache",
            "hibernate.cache.provider_class",
            "hibernate.jdbc.batch_size",
        };
        
        for(String name : property_names) {
        	poperties.setProperty(name, ENV.getProperty(name));
        }
        

        return poperties;
    }
}
