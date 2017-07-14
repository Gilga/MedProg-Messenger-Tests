package de.sb.messenger.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static javax.ws.rs.core.MediaType.MEDIA_TYPE_WILDCARD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import de.sb.messenger.persistence.Document;
import de.sb.messenger.persistence.Message;
import de.sb.messenger.persistence.Person;
import de.sb.messenger.persistence.Person.Group;

public class PersonServiceTest extends ServiceTest {

	public String authorizationHeaderName;
	public String authorizationHeaderValue;
	public String authorizationHeaderInvalidValue;

	// WebTarget userTarget;
	// WebTarget userTargetInvalid;

	static String getLogin(String username, String password) {
		String usernameAndPassword = username + ":" + password;
		return "Basic " + java.util.Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());
	}

	@Before
	public void setupBefore() {
		authorizationHeaderName = "Authorization";
		authorizationHeaderValue = getLogin("ines.bergmann@web.de", "ines");
		authorizationHeaderInvalidValue = "Basic "
				+ java.util.Base64.getEncoder().encodeToString("ines.bergmann@web.de:bla".getBytes());
	}

	@Test
	public void testDBConnection() throws SQLException {
		java.sql.Connection connect = DriverManager
				.getConnection("jdbc:mysql://localhost/messenger?user=root&password=root");
		assertTrue(connect.isClosed() == false);
		connect.close();
		assertTrue(connect.isClosed() == true);
	}

	@Test
	public void testPerson() throws SQLException, InterruptedException, ExecutionException {
		WebTarget userTarget = newWebTarget("ines.bergmann@web.de", "ines");
		Response response = userTarget.path("people/2").request(APPLICATION_JSON).get();
		assertTrue(response.getStatus() == 200);
	}

	@Test
	public void testLogin() throws SQLException, InterruptedException, ExecutionException {
		WebTarget userTarget = newWebTarget("ines.bergmann@web.de", "ines");
		Response response = userTarget.path("people/requester").request(APPLICATION_JSON)
				.header(authorizationHeaderName, authorizationHeaderValue).get();
		assertTrue(response.getStatus() == 200);
	}

	@Test
	public void testCriteriaQueries() {
		System.out.println("testCriteriaQueries");
		WebTarget userTarget = newWebTarget("ines.bergmann@web.de", "ines");
		
		/*
		 * Test GET queryParam
		 */
		Person[] people;

		final int offset = 10, limit = 200;
		Response response = userTarget.path("people").queryParam("offset", offset).queryParam("limit", limit)
				.request(APPLICATION_JSON).get();

		assertTrue(response.getStatus() == 200);

		people = response.readEntity(Person[].class);

		assertTrue(people != null);

		for (Person p : people) {
			assertTrue(offset <= p.getVersion());
			assertTrue(limit >= p.getVersion());
		}

		assertTrue(response.getStatus() == 200);

		/*
		 * Test timeStamp
		 */
		final long lowerCreationTimestamp = 0, upperCreationTimestamp = 50;
		response = userTarget.path("people").queryParam("lowerCreationTimestamp", lowerCreationTimestamp)
				.queryParam("upperCreationTimestamp", upperCreationTimestamp).request(APPLICATION_JSON).get();

		people = response.readEntity(Person[].class);

		for (Person p : people) {
			assertTrue(lowerCreationTimestamp <= p.getCreationTimestamp());
			assertTrue(upperCreationTimestamp >= p.getCreationTimestamp());
		}
		assertTrue(response.getStatus() == 200);
		/*
		 * Test givenName param
		 */
		response = userTarget.path("people").queryParam("givenName", "Ines").request(APPLICATION_JSON).get();

		people = response.readEntity(Person[].class);

		assertEquals("Ines", people[0].getName().getGiven());
		assertTrue(response.getStatus() == 200);

	}

	/*
	 * Test authentification and
	 */
	@Test
	public void testIdentityQueries() {
		WebTarget userTarget1 = newWebTarget("sascha.baumeister@gmail.com", "sascha");
		System.out.println("BBBBB" + userTarget1.toString());
		// userTargetInvalid = newWebTarget("ines.bergmann@web.de",
		// "password").path("people");

		/*
		 * Test getRequester
		 */

		Response res = userTarget1.path("/people/2").request().header(authorizationHeaderName, authorizationHeaderValue)
				.get();
		// correct authentication.no exception

		assertTrue(res.getStatus() == 200);
		//
		// res = userTargetInvalid.request().header(authorizationHeaderName,
		// authorizationHeaderInvalidValue).get();
		// // 401 Unauthorized
		// assertTrue(res.getStatus() == 401);

		res = userTarget1.path("/people/2").request().header(authorizationHeaderName, authorizationHeaderValue).get();
		// valid. keine exception
		assertTrue(res.getStatus() == 200);

		res = userTarget1.path("/people/20").request().header(authorizationHeaderName, authorizationHeaderValue).get();
		// ClientErrorException 404keine passende Entitaet
		assertTrue(res.getStatus() == 404);

		res = userTarget1.path("people/2").request().header(authorizationHeaderName, authorizationHeaderValue)
				.accept(APPLICATION_JSON).get();

		// return media type APPLICATION_JSON
		assertEquals(APPLICATION_JSON_TYPE, res.getMediaType());

		res = userTarget1.path("people/2").request().header(authorizationHeaderName, authorizationHeaderValue)
				.accept(APPLICATION_XML).get();
		// returns media type APPLICATION_XML_TYPE because APPLICATION_XMLis
		// simply a string
		assertEquals(APPLICATION_XML_TYPE, res.getMediaType());
		// .getWasteBasket().add(userTargetIvalidUser.ge)
	}

	// @Test
	// public void ObserverRelationQueries() {
	//
	// }

	@Test
	public void testLifecycle() {
		String s = "some content";
		byte[] content = s.getBytes();
		Document doc = new Document("image/jpeg", content);
		// create entity
		Person person = new Person("test@gmail.com", doc);
		person.getName().setGiven("John");
		person.getName().setFamily("Smith");
		person.getAddress().setStreet("Falkenbergerstr. 1");
		person.getAddress().setPostcode("12345");
		person.getAddress().setCity("Berlin");
		person.setGroup(Group.USER);
		String passwort = "password";
		byte[] hash = person.passwordHash(passwort);
		person.setPasswordHash(hash);

		WebTarget webTarget = newWebTarget("ines.bergmann@web.de", "ines");
		/*
		 * test PUT update person/create person
		 */
		Response response = webTarget.path("people").request(APPLICATION_XML).header("Set-Password", "ines").put(Entity.json(person));
		
		assertEquals(200,response.getStatus());
		long idPerson = response.readEntity(Long.class);
		// TODO idPerson how to get
		assertNotEquals(0L, idPerson);
		System.out.println("AAAAAAAAA" + idPerson);
		 

		/*
		 * Test getPerson
		 */
		response = webTarget.path("people/2").request(APPLICATION_JSON).get();
		Person returnedPerson = response.readEntity(Person.class);
		assertTrue(response.getStatus() == 200);
		assertEquals(2L, returnedPerson.getIdentiy());

		/*
		 * Test getMessagesAuthored
		 */
		List<Message> msgs;
		response = webTarget.path("people/2/messagesAuthored").request(APPLICATION_JSON).get();
		assertTrue(response.getStatus() == 200);

		msgs = response.readEntity(new GenericType<List<Message>>() {
		});
		assertEquals(0, msgs.size());

		/*
		 * Test peopleObserving
		 */
		// WebTarget webTargetInesPeopleObserving =
		// newWebTarget("ines.bergmann@web.de",
		// "ines").path("people/2/peopleObserving");
		Person[] peopleObserving;
		response = webTarget.path("people/2/peopleObserving").request(APPLICATION_JSON).get();
		peopleObserving = response.readEntity(Person[].class);
		
		assertEquals(6, peopleObserving.length);
		assertTrue(response.getStatus() == 200);
		assertEquals(3L, peopleObserving[0].getIdentiy());
		assertEquals(4L, peopleObserving[1].getIdentiy());
		assertEquals(5L, peopleObserving[2].getIdentiy());
		assertEquals(6L, peopleObserving[3].getIdentiy());
		assertEquals(7L, peopleObserving[4].getIdentiy());
		assertEquals(8L, peopleObserving[5].getIdentiy());
		
		
		
		/*
		 * Test peopleObserved
		 */
		List<Person> peopleObserved;
		response = webTarget.path("people/2/peopleObserved").request(APPLICATION_JSON).get();
		peopleObserved = response.readEntity(new GenericType<List<Person>>() {
		});

		assertEquals(6, peopleObserved.size());
		assertTrue(response.getStatus() == 200);
		assertEquals(3L, peopleObserved.get(0).getIdentiy());

		/*
		 * Test put updatePerson peopleObserved
		 */
		response = webTarget.path("people/2/peopleObserved").request(APPLICATION_JSON).put(Entity.json(person));
		peopleObserved = response.readEntity(new GenericType<List<Person>>() {
		});
		Person testP = null;
		for (Person p : peopleObserved) {
			if (p.getName().getFamily() == "Smith") {
				testP = p;
			}
		}

		assertEquals(7, peopleObserved.size());
		assertTrue(testP.getName().getGiven() == "John");
		assertTrue(response.getStatus() == 200);

		/*
		 * Test getAvatar
		 */
		response = webTarget.path("people/2/avatar").request().get();
		Document docum = response.readEntity(Document.class);

		assertEquals(1, docum.getIdentiy());
		assertEquals("image/jpeg", docum.getContentType());
		assertEquals(MEDIA_TYPE_WILDCARD, response.getMediaType());
		assertTrue(response.getStatus() == 200);

		this.getWasteBasket().add(idPerson);
	}

	// https://dennis-xlc.gitbooks.io/restful-java-with-jax-rs-2-0-2rd-edition/en/part1/chapter8/building_and_invoking_requests.html
	// https://stackoverflow.com/questions/27211012/how-to-send-json-object-from-rest-client-using-javax-ws-rs-client-webtarget
}
