package de.sb.messenger.test.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import de.sb.messenger.config.PersistenceConfig;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceConfig.class }, loader = AnnotationConfigContextLoader.class)
public class JDBCConnectionTest {
	
	@PersistenceUnit(unitName = "messenger")
	private EntityManagerFactory entityManagerFactory;
	
	Connection connection = null;
	
	@Before
	public void testStart() throws Exception {
		
	}
	
    @Autowired
    private SessionFactory sessionFactory;
	
	@Test
	public void testHibernateSession() throws Exception {
		Session session;
		
		try {
		    session = sessionFactory.getCurrentSession();
		} catch (HibernateException e) {
		    session = sessionFactory.openSession();
		}
		
		assertNotEquals(session,null);
		
		session.close();
	}
		
	
	@Test
	@Order(1)
	public void testGetEmailsFromPersons() throws Exception {
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://localhost/messenger?user=root&password=root");
		
		assertEquals(connection.isClosed(),false);
		assertNotEquals(connection, null);
		
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM person");
		
		while(resultSet.next()){
			System.out.println("RESULT: " + resultSet.getString("email"));
		}
		
		if(connection == null) return;
		connection.close();
		
		assertTrue(connection.isClosed() == true);
	}
	
//	@Test
//	@Order(2)
//	public void testPersistenceWithProperties() throws Exception {
//		Class.forName("org.hibernate.jpa.HibernatePersistenceProvider");
//		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
//		
//		Map<String, String> properties = new HashMap<>();
//
//		properties.put("javax.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
//		properties.put("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/messenger"); //jdbc:oracle:thin:@localhost:1521:ORCL
//		properties.put("javax.persistence.jdbc.user", "root");
//		properties.put("javax.persistence.jdbc.password", "root");
//		
//	    EntityManagerFactory emf = Persistence.createEntityManagerFactory("messenger", properties); // will only work with persistence.xml
//		
//		assertNotEquals(emf,null);
//	}
	
	@Test
	@Order(3)
	public void testPersistenceWithoutProperties() throws Exception {
		assertNotEquals(entityManagerFactory,null);
	}

	@After
	public void testEnd() throws Exception {

	}
}
