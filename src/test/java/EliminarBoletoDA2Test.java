import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.junit.jupiter.api.Test;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import dataAccess.DataAccessPinedo2;
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
import utility.TestUtilityDataAccess;

class EliminarBoletoDA2Test {

	static DataAccessPinedo2 sut = new DataAccessPinedo2(ConfigXML.getInstance().getDataBaseOpenMode().equals("initialize"));;
	static TestUtilityDataAccess testDA = new TestUtilityDataAccess();
	
	private Boleto b;
	private User usr;
//	@Test
//	// sut.eliminarBoleto: El boleto no existe en la base de datos.
//	void test1() {
//
//		assertThrows(BoletoNoExiste.class, () -> sut.eliminarBoleto("gratis"));
//	}
	
	@Test
	// sut.eliminarBoleto: El boleto ha sido usado su maximo de veces.
	void test2() {
		// configure the state of the system (create object in the dabatase)
		try {
			String codigo = "20gratis";
			int max = 1;
			double valor = 20;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date oneDate = sdf.parse("05/10/2001");
			User ad = new User(11223344, "12345678","admin", "ad", "ad", "ad@gmail.com",oneDate);
			testDA.open();
			b = testDA.crearBoleto(codigo, max, valor);
			testDA.useBoleto(codigo);
			testDA.createUser(ad);
			testDA.close();

			// invoke System Under Test (sut) and Assert
			sut.eliminarBoleto(codigo);
			
			// verify DB
			testDA.open();
			assertNull(testDA.getBoleto(codigo));
			assertEquals(1000000.0,ad.getChutiGoles());
			testDA.close();				
			
			
		} catch (NotEnoughChuti e) {
			fail("Admin has not enough chuti");
		} catch (CodigoRepetido e) {
			fail("Codigo esta repetido");
		} catch (BoletoNoExiste e) {
			fail ("No deberia ocurrir esto");
		} catch (MaxUsed e) {
			fail ("No deberia ocurrir esto");
		} catch (BoletoUsado e) {
			fail ("No deberia ocurrir esto");
		} catch (ParseException e) {
			fail ("Fecha mal puesta");
		}

	}
	
//	@Test
//	// sut.eliminarBoleto: El boleto falta por usarse una vez.
//	void test3() {
//		// configure the state of the system (create object in the dabatase)
//		try {
//			String codigo = "20gratis";
//			int max = 2;
//			double valor = 20;
//			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//			Date oneDate = sdf.parse("05/10/2001");
//			User ad = new User(11223344, "12345678","admin", "ad", "ad", "ad@gmail.com",oneDate);
//			testDA.open();
//			b = testDA.crearBoleto(codigo, max, valor);
//			testDA.useBoleto(codigo);
//			testDA.close();
//
//			// invoke System Under Test (sut) and Assert
//			sut.eliminarBoleto(codigo);
//			
//			// verify DB
//			testDA.open();
//			assertNull(testDA.getBoleto(codigo));
//			User admin= testDA.createUser(ad);
//			assertEquals(1000020.0,admin.getChutiGoles());
//			testDA.close();				
//			
//			
//		} catch (NotEnoughChuti e) {
//			fail("Admin has not enough chuti");
//		} catch (CodigoRepetido e) {
//			fail("Codigo esta repetido");
//		} catch (BoletoNoExiste e) {
//			fail ("No deberia ocurrir esto");
//		} catch (MaxUsed e) {
//			fail ("No deberia ocurrir esto");
//		} catch (BoletoUsado e) {
//			fail ("No deberia ocurrir esto");
//		} catch (ParseException e) {
//			fail ("Fecha mal puesta");
//		}
//		}
	
//	@Test
//	// sut.eliminarBoleto: El boleto falta por usarse varias veces.
//	void test4() {
//		// configure the state of the system (create object in the dabatase)
//		try {
//			String codigo = "10gratis";
//			int max = 40;
//			double valor = 10;
//			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//			Date oneDate = sdf.parse("05/10/2001");
//			User ad = new User(11223344, "12345678","admin", "ad", "ad", "ad@gmail.com",oneDate);
//			testDA.open();
//			b = testDA.crearBoleto(codigo, max, valor);
//			testDA.close();
//			
//			// invoke System Under Test (sut) and Assert
//			sut.eliminarBoleto(codigo);
//			
//			// verify DB
//			testDA.open();
//			assertNull(testDA.getBoleto(codigo));
//			User admin= testDA.createUser(ad);
//			assertEquals(1000420.0,admin.getChutiGoles());
//			testDA.close();				
//			
//			
//		} catch (NotEnoughChuti e) {
//			fail("Admin has not enough chuti");
//		} catch (CodigoRepetido e) {
//			fail("Codigo esta repetido");
//		} catch (BoletoNoExiste e) {
//			fail ("No deberia ocurrir esto");
//		} catch (ParseException e) {
//			fail ("Fecha mal puesta");
//		} catch (MaxUsed e) {
//			fail ("No deberia ocurrir esto");
//		}
//
//	}
	
//	@Test
//	// sut.eliminarBoleto: El boleto falta por usarse varias veces.
//	void test5() {
//		// configure the state of the system (create object in the dabatase)
//		try {
//			String codigo = "15gratis";
//			int max = 1;
//			double valor = 15;
//			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//			Date oneDate = sdf.parse("05/10/2001");
//			User ad = new User(11223344, "12345678","admin", "ad", "ad", "ad@gmail.com",oneDate);
//			testDA.open();
//			b = testDA.crearBoleto(codigo, max, valor);
//			testDA.close();
//			
//			// invoke System Under Test (sut) and Assert
//			sut.eliminarBoleto(codigo);
//			
//			// verify DB
//			testDA.open();
//			assertNull(testDA.getBoleto(codigo));
//			User admin= testDA.createUser(ad);
//			assertEquals(1000435.0,admin.getChutiGoles());
//			testDA.close();				
//			
//			
//		} catch (NotEnoughChuti e) {
//			fail("Admin has not enough chuti");
//		} catch (CodigoRepetido e) {
//			fail("Codigo esta repetido");
//		} catch (BoletoNoExiste e) {
//			fail ("No deberia ocurrir esto");
//		} catch (ParseException e) {
//			fail ("Fecha mal puesta");
//		} catch (MaxUsed e) {
//			fail ("No deberia ocurrir esto");
//		}
//
//	}

}
