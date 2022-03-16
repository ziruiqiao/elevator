package tests;

import elevator.Floor.PassengerRequest;
import elevator.util.Serializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zirui Qiao
 *
 */

class PassengerRequestTest {
	PassengerRequest passengerRequest;

	@BeforeEach
	void setUp() throws Exception {
		passengerRequest = new PassengerRequest(3, 1, 60603001, "UP");
	}

	@AfterEach
	void tearDown() throws Exception {
		passengerRequest = null;
	}
	
	@Test
	void getFloorTest() {
		assertEquals(3, passengerRequest.getFloor());
	}
	
	@Test
	void getElevatorNumberTest() {
		assertEquals(1, passengerRequest.getElevatorNumber());
	}
	
	@Test
	void getTimeTest() {
		assertEquals(60603001, passengerRequest.getTime());
	}
	
	@Test
	void getDirectionalButtonTest() {
		assertEquals("UP", passengerRequest.getDirectionalButton());
	}

	@Test
	void toStringTest() {
		assertEquals("16:50:03.001 3 UP 1", passengerRequest.toString());
		assertEquals("16:50:03.001 3 UP 1", passengerRequest.toString());
	}

	@Test
	void serializeTest(){
		assertEquals(passengerRequest, Serializer.fromByteArray(
				Serializer.toByteArray(passengerRequest), PassengerRequest.class));
		ArrayList<PassengerRequest> e = new ArrayList<>();
		e.add(passengerRequest);
		ArrayList<PassengerRequest> converted = Serializer.fromByteArray(
				Serializer.toByteArray(e), ArrayList.class);
		assertEquals(converted.get(0), passengerRequest);
	}

	@Test
	void parseEventStringTest() {
		PassengerRequest testEvent = PassengerRequest.parseEventString("16:50:03.001 3 UP 1");
		assertEquals(testEvent.getFloor(), passengerRequest.getFloor());
		assertEquals(testEvent.getElevatorNumber(), passengerRequest.getElevatorNumber());
		assertEquals(testEvent.getTime(), passengerRequest.getTime());
		assertEquals(testEvent.getDirectionalButton(), passengerRequest.getDirectionalButton());
	}

}
