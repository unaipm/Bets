package utility;



import java.util.Date;
import java.util.Vector;

import configuration.ConfigXML;
import domain.Equipo;
import domain.Event;

/**
 * Utilities to access Data Base
 * @author IS2
 *
 */
public class TestUtilityFacadeImplementation {
		private TestUtilityDataAccess dbManagerTest;
 	
    
	   public TestUtilityFacadeImplementation()  {			
			System.out.println("Creating TestFacadeImplementation instance");
			ConfigXML.getInstance();
			dbManagerTest=new TestUtilityDataAccess(); 
			dbManagerTest.close();
		}
		
		 
		public boolean removeEvent(Event ev) {
			dbManagerTest.open();
			boolean b=dbManagerTest.removeEvent(ev);
			dbManagerTest.close();
			return b;

		}
		
		public Event addEventWithQuestion(String desc, Date d, Equipo eq1, Equipo eq2, String question, float qty, Boolean equipo) {
			dbManagerTest.open();
			Event o=dbManagerTest.addEventWithQuestion(desc,d, eq1, eq2, question, qty, equipo);
			dbManagerTest.close();
			return o;

		}
		
		public Vector<Event> getEvents(Date date)  {
			dbManagerTest.open();
			Vector<Event>  events=dbManagerTest.getEvents(date);
			dbManagerTest.close();
			return events;
		}

}
