package de.sb.messenger.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import de.sb.messenger.persistence.BaseEntity;
import de.sb.messenger.persistence.Message;
import de.sb.messenger.persistence.Person;

public class MessageServiceTest extends ServiceTest {

	@Test
	public void testCriteriaQueries() {
		WebTarget webTarget = newWebTarget("ines.bergmann@web.de", "ines");
		
		Response res = webTarget.path("people/2").request().accept(APPLICATION_JSON).get();
		Person returnedPerson = res.readEntity(Person.class);
		BaseEntity baseEntity = new BaseEntity();
		Message message = new Message(returnedPerson, baseEntity, "Hi there!");

		Response response = webTarget.path("messages").queryParam("subjectReference", 2L).request()
				.put(Entity.text(message.getBody()));
		long idMsg = response.readEntity(Long.class);
		
		assertEquals(200, response.getStatus());
		assertNotEquals(0L, idMsg);
		
		//getWasteBasket().add(idMsg);
	}

	@Test
	public void testIdentityQueries() {
		
		/*
		 * Test getMessage
		 */
		WebTarget webTarget = newWebTarget("ines.bergmann@web.de", "ines");
		// cannot find the msg just put, so created a msg with id 11
		Response response = webTarget.path("messages/11").request().accept(APPLICATION_JSON).get();
		Message returnedMsg = response.readEntity(Message.class);

		assertEquals(200, response.getStatus());
		assertNotNull(returnedMsg);
		assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());

		/*
		 * Test getAuthor
		 */
		response = webTarget.path("messages/11/author").request().accept(APPLICATION_JSON).get();
		Person author = response.readEntity(Person.class);

		assertTrue(response.getStatus() == 200);
		assertNotNull(author);
		assertEquals("Zeta", author.getName().getGiven());
		assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());

		/*
		 * Test getSubject
		 */
		response = webTarget.path("messages/11/subject").request().accept(APPLICATION_JSON).get();
		BaseEntity subject = response.readEntity(BaseEntity.class);

		assertTrue(response.getStatus() == 200);
		assertNotNull(subject);
		assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());

	}
}
