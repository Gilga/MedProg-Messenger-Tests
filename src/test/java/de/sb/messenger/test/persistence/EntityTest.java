package de.sb.messenger.test.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import de.sb.messenger.LogManager;
import de.sb.messenger.config.PersistenceConfig;
import de.sb.messenger.persistence.BaseEntity;
import de.sb.messenger.test.InstanceTestClassListener;
import de.sb.messenger.test.SpringInstanceTestClassRunner;

@RunWith(SpringInstanceTestClassRunner.class)
@ContextConfiguration(classes = { PersistenceConfig.class }, loader = AnnotationConfigContextLoader.class)
public class EntityTest implements InstanceTestClassListener {
	
	// entity manager object creates entity transaction instance
	@PersistenceUnit(unitName = "messenger")
	protected EntityManagerFactory emf;

	protected static ValidatorFactory VALIDATOR_FACTORY;
	
	private Set<Long> wasteBasket = new HashSet<>();
	
	protected final static Logger LOGGER = LogManager.getRootLogger();
	
    @Override
    public void beforeClassSetup() {
    	LOGGER.debug("#beforeClassSetup");
    	assertNotEquals(emf, null);
    }

    @Override
    public void afterClassSetup() {
    	LOGGER.debug("#afterClassSetup");
    	assertNotEquals(emf, null);
    	emf.close();
    	LOGGER.debug("#afterClassSetup#");
    }
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
		LOGGER.debug("#beforeClass");
	}
	

	@AfterClass
	public static void afterClass() throws Exception {
		LOGGER.debug("#afterClass");
	}
	
	public EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}

	public ValidatorFactory getEntityValidatorFactory() {
		return VALIDATOR_FACTORY;

	}

	public Set<Long> getWasteBasket() {
		return wasteBasket;
	}
	
	@Test
	public void testEMF() {
		assertNotNull(getEntityManagerFactory());
	}
	
	//copyright = "2015-2015 Sascha Baumeister, 2020 Mario Link, all rights reserved"
	@After
	public void emptyWasteBasket() {
		LOGGER.debug("#emptyWasteBasket");
		
		final EntityManagerFactory emf = getEntityManagerFactory();
		final EntityManager entityManager = emf.createEntityManager();
		
		try {
			entityManager.getTransaction().begin();
			for (final Long identity : this.wasteBasket) {
				try {
					final Object entity = entityManager.find(BaseEntity.class, identity);
					if (entity != null) entityManager.remove(entity);
					
				} catch (final Exception exception) {
					//Logger.getGlobal().log(WARNING, exception.getMessage(), exception);
					LOGGER.catching(exception);
				}
			}
			entityManager.getTransaction().commit();
			this.wasteBasket.clear();
			
		} finally {
			entityManager.close();
		}
		
		LOGGER.debug("#emptyWasteBasket#");
	}
}

