package de.sb.messenger;

import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class LoggingTest {
	
	private final static Logger LOGGER = LogManager.getRootLogger();
	
	@Test
    public void testLogger() {
		LOGGER.fatal("LOGGING: FATAL");
		LOGGER.error("LOGGING: ERROR");
		LOGGER.warn("LOGGING: WARN");
		LOGGER.info("LOGGING: INFO");
		LOGGER.debug("LOGGING: DEBUG");
		LOGGER.trace("LOGGING: TRACE");
		
    	try {
    		throw new Exception("LOGGING:EXCEPTION");
    	}
    	catch(Exception e) {
    		LOGGER.catching(e);
    	}
    }
}
