
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.junit.jupiter.api.Test;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import domain.Equipo;
import domain.Event;
import domain.Question;
import exceptions.QuestionAlreadyExist;
import utility.TestUtilityDataAccess;

class CreateQuestionDATest {

	static DataAccess sut = new DataAccess(ConfigXML.getInstance().getDataBaseOpenMode().equals("initialize"));;
	static TestUtilityDataAccess testDA = new TestUtilityDataAccess();

	private Event ev;

	@Test
	// sut.createQuestion: The event has one question with a queryText.
	void test1() {

		try {
			// configure the state of the system (create object in the dabatase)
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date oneDate = sdf.parse("05/10/2022");
			String eventText = "Event Text";
			String queryText = "Query Text";
			Float betMinimum = 2f;
			Boolean equipo = false;
			Equipo eq1 = new Equipo("nombre1", 2021);
			Equipo eq2 = new Equipo("nombre2", 2021);

			testDA.open();
			ev = testDA.addEventWithQuestion(eventText, oneDate, eq1, eq2, queryText, betMinimum, equipo);
			testDA.close();

			// invoke System Under Test (sut) and Assert
			assertThrows(QuestionAlreadyExist.class, () -> sut.createQuestion(ev, queryText, betMinimum, equipo));

		} catch (ParseException e) {
			fail("It should be correct: check the date format");
		}

		// Remove the created objects in the database (cascade removing)
		testDA.open();
		boolean b = testDA.removeEvent(ev);
		System.out.println("Removed event " + b);
		testDA.close();

	}

	@Test
	// sut.createQuestion: The event has NOT one question with a queryText.
	void test2() {
		try {

			// configure the state of the system (create object in the dabatase)
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date oneDate = sdf.parse("05/10/2022");
			String eventText = "Event Text";
			Float betMinimum = 2f;
			Boolean equipo = false;
			Equipo eq1 = new Equipo("nombre1", 2021);
			Equipo eq2 = new Equipo("nombre2", 2021);
			
			testDA.open();
			ev = testDA.addEventWithQuestion(eventText, oneDate, eq1, eq2, "otra", 10.0f, false);
			testDA.close();

			String queryText = "Query Text";
			try {
				// invoke System Under Test (sut)
				Question q = sut.createQuestion(ev, queryText, betMinimum, equipo);

				// verify the results returned
				assertNotNull(q);
				assertEquals(queryText, q.getQuestion());
				assertEquals(betMinimum, q.getBetMinimum());
				
				// verify DB
				testDA.open();
				Vector<Event> es = testDA.getEvents(oneDate);
				testDA.close();

				assertEquals(1, es.size());
				assertEquals(2, es.get(0).getQuestions().size());
				assertEquals(queryText, es.get(0).getQuestions().get(1).getQuestion());
				assertEquals(betMinimum, es.get(0).getQuestions().get(1).getBetMinimum());
			} catch (QuestionAlreadyExist e) {
				// if the program goes to this point fail
				fail();
			} finally {
				// Remove the created objects in the database (cascade removing)
				testDA.open();
				boolean b = testDA.removeEvent(ev);
				testDA.close();
				System.out.println("Finally " + b);
			}
		} catch (ParseException e) {
			fail("It should be correct: check the date format");
		}

	}
	
	@Test
	// sut.createQuestion: The event is null.
	void test3() {

			// configure the state of the system (create object in the dabatase)
			Float betMinimum = 2f;
			String queryText = "Query Text";
			Boolean equipo = false;
			// verify the results returned
			assertThrows(NullPointerException.class, ()-> sut.createQuestion(null, queryText, betMinimum, equipo));
	}
	
	@Test
	// sut.createQuestion: The queryText is null.
	void test4() {
		try {

			// configure the state of the system (create object in the dabatase)
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date oneDate = sdf.parse("05/10/2022");
			String eventText = "Event Text";
			Float betMinimum = 2f;
			Boolean equipo = false;
			Equipo eq1 = new Equipo("nombre1", 2021);
			Equipo eq2 = new Equipo("nombre2", 2021);

			testDA.open();
			ev = testDA.addEventWithQuestion(eventText, oneDate, eq1, eq2, "una", 0.0f, equipo);
			System.out.println("**************"+ev.getEventNumber());
			testDA.close();

			String queryText = null;
			try {
				testDA.open();
				// verify the results returned
				assertThrows(NullPointerException.class, ()-> sut.createQuestion(ev, queryText, betMinimum, equipo));
				
				// verify DB
				Vector<Event> es = testDA.getEvents(oneDate);
				testDA.close();

				assertTrue(es.contains(ev));


			} finally {
				// Remove the created objects in the database (cascade removing)
				testDA.open();
				boolean b = testDA.removeEvent(ev);
				System.out.println("Finally " + b);
				testDA.close();
			}
		} catch (ParseException e) {
			fail("It should be correct: check the date format");
		}

	}
	
	@Test
	// sut.createQuestion: The betMinimum is negative.
	void test5() {
		try {

			// configure the state of the system (create object in the dabatase)
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date oneDate = sdf.parse("05/10/2022");
			String eventText = "Event Text";
			Float betMinimum = -2f;
			Boolean equipo = false;
			Equipo eq1 = new Equipo("nombre1", 2021);
			Equipo eq2 = new Equipo("nombre2", 2021);
			
			testDA.open();
			ev = testDA.addEventWithQuestion(eventText, oneDate, eq1, eq2, "otra", 0.0f, equipo);
			testDA.close();

			String queryText = "Query Text";
			try {
				// invoke System Under Test (sut)
				Question q = sut.createQuestion(ev, queryText, betMinimum, false);

				// verify the results returned
				assertNotNull(q);
				assertEquals(queryText, q.getQuestion());
				assertEquals(betMinimum, q.getBetMinimum(), 0);
				
				// verify DB
				testDA.open();
				Vector<Event> es = testDA.getEvents(oneDate);
				testDA.close();				assertEquals(1, es.size());
				assertEquals(eventText, es.get(0).getDescription());
				assertEquals(oneDate, es.get(0).getEventDate());


			} catch (QuestionAlreadyExist e) {
				// if the program goes to this point fail
				fail();
			} finally {
				// Remove the created objects in the database (cascade removing)
				testDA.open();
				boolean b = testDA.removeEvent(ev);
				testDA.close();
				System.out.println("Finally " + b);
			}
		} catch (ParseException e) {
			fail("It should be correct: check the date format");
		}

	}

}
