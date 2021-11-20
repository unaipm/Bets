package dataAccess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.jws.WebMethod;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;

import configuration.ConfigXML;
import configuration.UtilDate;
import domain.Apuesta;
import domain.Boleto;
import domain.Card;
import domain.Equipo;
import domain.EquipoId;
import domain.Event;
import domain.Payment;
import domain.Pronosticos;
import domain.Question;
import domain.User;
import exceptions.BoletoNoExiste;
import exceptions.BoletoUsado;
import exceptions.CodigoRepetido;
import exceptions.MaxUsed;
import exceptions.NotEnoughChuti;
import exceptions.PrognosticAlreadyExist;
import exceptions.QuestionAlreadyExist;
import exceptions.UserAlreadyExist;
import exceptions.UserDoesntExist;

/**
 * It implements the data access to the objectDb database
 */
public class DataAccessPinedo2 {
	protected static EntityManager db;
	protected static EntityManagerFactory emf;

	private int adminDNI = 11223344;

	ConfigXML c = ConfigXML.getInstance();

	public DataAccessPinedo2(boolean initializeMode) {

		System.out.println("Creating DataAccess instance => isDatabaseLocal: " + c.isDatabaseLocal()
		+ " getDatabBaseOpenMode: " + c.getDataBaseOpenMode());

		open(initializeMode);

	}

	public DataAccessPinedo2() {
		this(false);
	}

	/**
	 * This is the data access method that initializes the database with some events
	 * and questions. This method is invoked by the business logic (constructor of
	 * BLFacadeImplementation) when the option "initialize" is declared in the tag
	 * dataBaseOpenMode of resources/config.xml file
	 */
	public void initializeDB() {

		db.getTransaction().begin();
		try {

			Calendar today = Calendar.getInstance();

			int month = today.get(Calendar.MONTH);
			month += 1;
			int year = today.get(Calendar.YEAR);
			if (month == 12) {
				month = 0;
				year += 1;
			}
			User admin = new User(adminDNI, "1234567", "Admin", "Administrador", "SuperUser", "admin@ehu.eus",
					UtilDate.newDate(1998, 4, 9));//b
			User elpepe = new User(123456789, 12345678, "1234567", "Pepe", "Lope", "deVega", "elpepe@ehu.eus",
					UtilDate.newDate(1998, 4, 9));//z
			User emue = new User(11111111, 11111111, "1234567", "emue", "Lope", "deVega", "elpepe@ehu.eus",
					UtilDate.newDate(1998, 4, 9));//h

			admin.setChutiGoles(1000.0);
			elpepe.setChutiGoles(10.0);
			emue.setChutiGoles(100.0);

			db.persist(admin);
			db.persist(elpepe);
			db.persist(emue);

			db.getTransaction().commit();
			System.out.println("Db initialized");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo que abre una base de datos en caso de que el parametro inizializeMode
	 * sea falso y en caso contrario borra la base de datos por si estuviese ya
	 * creada y crea una nueva base de datos
	 * 
	 * @param initializeMode Boolean que representa si tiene que abrir o crear una
	 *                       nueva base de datos
	 */
	public void open(boolean initializeMode) {

		System.out.println("Opening DataAccess instance => isDatabaseLocal: " + c.isDatabaseLocal()
		+ " getDatabBaseOpenMode: " + c.getDataBaseOpenMode());

		String fileName = c.getDbFilename();
		if (initializeMode) {
			fileName = fileName + ";drop";
			System.out.println("Deleting the DataBase");
		}

		if (c.isDatabaseLocal()) {
			emf = Persistence.createEntityManagerFactory("objectdb:" + fileName);
			db = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("javax.persistence.jdbc.user", c.getUser());
			properties.put("javax.persistence.jdbc.password", c.getPassword());

			emf = Persistence.createEntityManagerFactory(
					"objectdb://" + c.getDatabaseNode() + ":" + c.getDatabasePort() + "/" + fileName, properties);

			db = emf.createEntityManager();
		}

	}

	/**
	 * Cierra la base de datos
	 */
	public void close() {
		db.close();
		System.out.println("DataBase closed");
	}

	/**
	 * Metodo para eliminar un boleto que devuelve los chutigoles no usados al Admin.
	 * @param codigo codigo del boleto a eliminar
	 * @throws BoletoNoExiste si el boleto no existe
	 * @throws MaxUsed si el boleto ha sido usado mas veces de su maximo
	 */
	public void eliminarBoleto(String codigo) throws BoletoNoExiste, MaxUsed {
		Boleto b = db.find(Boleto.class, codigo);
		
		if (b==null) {
			throw new BoletoNoExiste();
		}
		User admin = db.find(User.class, adminDNI);
		db.getTransaction().begin();
		while(b.getUsados()<b.getMax()) {
				b.used();
				admin.addChutiGoles(b.getPrecio());
		}
		db.remove(b);
		db.persist(admin);
		db.getTransaction().commit();
	}

}