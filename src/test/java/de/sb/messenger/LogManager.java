package de.sb.messenger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.eclipse.persistence.logging.LogLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

public class LogManager {
	
	private final static Logger LOGGER = org.apache.logging.log4j.LogManager.getRootLogger();
	
	// LOG LEVELS: OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL
	// -Dlog4j.configuration=file:log4j.properties -Dlog4j.debug
	
	@Bean
	public static Logger getRootLogger() {
		Logger log = LOGGER;

//		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
//		Configuration config = ctx.getConfiguration();
//
//		PatternLayout layout = PatternLayout.newBuilder()
//		  .withConfiguration(config)
//		  .withPattern("%d{HH:mm:ss.SSS} %level %msg%n")
//		  .build();
//
//		Appender appender = FileAppender.newBuilder()
//		  .setConfiguration(config)
//		  .setName("programmaticFileAppender")
//		  .setLayout(layout)
//		  .withFileName("java.log")
//		  .build();
//		    
//		appender.start();
//		config.addAppender(appender);
//		
//		AppenderRef ref = AppenderRef.createAppenderRef("programmaticFileAppender", null, null);
//		AppenderRef[] refs = new AppenderRef[] { ref };
//
//		LoggerConfig loggerConfig = LoggerConfig
//		  .createLogger(false, Level.INFO, "programmaticLogger", "true", refs, null, config, null);
//		loggerConfig.addAppender(appender, null, null);
//		config.addLogger("programmaticLogger", loggerConfig);
//		ctx.updateLoggers();		
		
		return log;
	}
}
