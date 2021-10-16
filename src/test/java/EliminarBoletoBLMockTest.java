import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import businessLogic.BLFacade;
import businessLogic.BLFacadeImplementation;
import dataAccess.DataAccess;
import domain.Boleto;
import domain.Event;
import domain.User;
import exceptions.BoletoNoExiste;
import exceptions.MaxUsed;
import exceptions.QuestionAlreadyExist;

class EliminarBoletoBLMockTest {

	DataAccess dataAccess = Mockito.mock(DataAccess.class);
	Boleto mockedBoleto = Mockito.mock(Boleto.class);
	
	BLFacade sut = new BLFacadeImplementation(dataAccess);
	
	@SuppressWarnings("unchecked")
	@DisplayName("sut.eliminarBoleto: El boleto no existe en la base de datos.")
	@Test
	void test1() {
		try {
			// define paramaters
				Mockito.doThrow(BoletoNoExiste.class)
		       .when(dataAccess)
		       .eliminarBoleto(Mockito.any(String.class));

				// invoke System Under Test (sut)
				assertThrows(BoletoNoExiste.class, () -> sut.eliminarBoleto("gratis"));
		} catch (BoletoNoExiste e) {
			fail("No deberia ocurrir esto");
		} catch (MaxUsed e) {
			fail("No deberia ocurrir esto");
		}

	}
	
	@SuppressWarnings("unchecked")
	@DisplayName("sut.eliminarBoleto: El boleto ha sido usado su maximo de veces o o un numero menor a su maximo de veces..")
	@Test
	void test2() {
		try {
			// define paramaters
			String codigo = "20gratis";

			// invoke System Under Test (sut)
			sut.eliminarBoleto(codigo);

			ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
			Mockito.verify(dataAccess, Mockito.times(1)).eliminarBoleto(stringCaptor.capture());
			assertEquals(codigo, stringCaptor.getValue());
		} catch (BoletoNoExiste e) {
			fail("Esto no deberia pasar");
		} catch (MaxUsed e) {
			fail("Esto no deberia pasar");
		}

	}
	

}
