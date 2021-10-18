package utility;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import configuration.ConfigXML;
import domain.Boleto;
import domain.Equipo;
import domain.Event;
import domain.User;
import exceptions.BoletoNoExiste;
import exceptions.BoletoUsado;
import exceptions.CodigoRepetido;
import exceptions.MaxUsed;
import exceptions.NotEnoughChuti;
import exceptions.UserAlreadyExist;
import exceptions.UserDoesntExist;

public class TestUtilityDataAccess {
	protected  EntityManager  db;
	protected  EntityManagerFactory emf;
	

	ConfigXML  c=ConfigXML.getInstance();


	public TestUtilityDataAccess()  {		
		System.out.println("Creating TestDataAccess instance");

		open();		
	}

	
	public void open(){
		
		System.out.println("Opening TestDataAccess instance ");

		String fileName=c.getDbFilename();
		
		if (c.isDatabaseLocal()) {
			  emf = Persistence.createEntityManagerFactory("objectdb:"+fileName);
			  db = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<String, String>();
			  properties.put("javax.persistence.jdbc.user", c.getUser());
			  properties.put("javax.persistence.jdbc.password", c.getPassword());

			  emf = Persistence.createEntityManagerFactory("objectdb://"+c.getDatabaseNode()+":"+c.getDatabasePort()+"/"+fileName, properties);

			  db = emf.createEntityManager();
    	   }
		
	}
	public void close(){
		db.close();
		System.out.println("DataBase closed");
	}

	public boolean removeEvent(Event ev) {
		System.out.println(">> DataAccessTest: removeEvent");
		Event e = db.find(Event.class, ev.getEventNumber());
		if (e!=null) {
			db.getTransaction().begin();
			db.remove(e);
			db.getTransaction().commit();
			return true;
		} else 
		return false;
    }
		
		public Event addEventWithQuestion(String desc, Date d, Equipo e1, Equipo e2, String question, float qty, Boolean equi) {
			System.out.println(">> DataAccessTest: addEvent");
			Event ev=null;
				db.getTransaction().begin();
				try {
				    ev=new Event(desc,d, e1, e2);
				    ev.addQuestion(question,  qty, equi);
					db.persist(ev);
					db.getTransaction().commit();
				}
				catch (Exception e){
					e.printStackTrace();
				}
				return ev;
	    }
		
		public Vector<Event> getEvents(Date date) {
			System.out.println(">> DataAccess: getEvents");
			Vector<Event> res = new Vector<Event>();	
			TypedQuery<Event> query = db.createQuery("SELECT ev FROM Event ev WHERE ev.eventDate=?1",Event.class);   
			query.setParameter(1, date);
			List<Event> events = query.getResultList();
		 	 for (Event ev:events){
		 	   System.out.println(ev.toString());		 
			   res.add(ev);
			  }
		 	return res;
		}
		
		public boolean existQuestion(Event event, String question) {
			System.out.println(">> DataAccess: existQuestion=> event= "+event+" question= "+question);
			Event ev = db.find(Event.class, event.getEventNumber());
			return ev.DoesQuestionExists(question);
			
		}
		
		public Boleto crearBoleto(String codigo, int max, double valor) throws NotEnoughChuti, CodigoRepetido{
			if (db.find(Boleto.class, codigo)!=null) throw new CodigoRepetido();
			db.getTransaction().begin();
			Boleto b = new Boleto(codigo, max, valor);
			db.persist(b);
			db.getTransaction().commit();
			return b;
		}
		
		public Boleto getBoleto(String codigo){
			Boleto b=db.find(Boleto.class, codigo);
			return b;
		}
		
		public void useBoleto(String codigo) throws MaxUsed, BoletoNoExiste, BoletoUsado {
			try {
				Boleto b = db.find(Boleto.class, codigo);
				if (b== null) throw new BoletoNoExiste();
				db.getTransaction().begin();
				b.used();
				db.persist(b);
				db.getTransaction().commit();
			} catch (MaxUsed e) {
				throw new MaxUsed();
			}
		}
		
		public User createUser(User usr){

			User us = db.find(User.class, usr.getDNI());
			if (us == null) {
				db.getTransaction().begin();
				db.persist(usr);
				db.getTransaction().commit();
				return usr;
			}
			return us;
		}
		
		public void setChutiGoles(int dni, Double chuti) {
			User us = db.find(User.class, dni);
			if (us != null) {
				db.getTransaction().begin();
				us.setChutiGoles(us.getChutiGoles() + chuti);
				db.getTransaction().commit();
				return;
			}

		}
		
}

