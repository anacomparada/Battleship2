package battleship;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Author: britoeabreu
 * Date: 2023-10-10
 * Time: 15:30
 *
 * Cyclomatic Complexity:
 * - Constructor: 5
 * - getSize: 1
 */

/**
 * Test class for the Caravel class.
 */
@DisplayName("Tests for the Caravel class")
class CaravelTest {

	static Caravel cN, cS, cE, cW;

	@BeforeAll
	static void setUpBeforeClass() {
		cN = new Caravel(Compass.NORTH, new Position(5, 5));
		cS = new Caravel(Compass.SOUTH, new Position(5, 5));
		cE = new Caravel(Compass.EAST, new Position(5, 5));
		cW = new Caravel(Compass.WEST, new Position(5, 5));
	}

	@AfterAll
	static void tearDownAfterClass() {
		cN = null;
		cS = null;
		cE = null;
		cW = null;
	}

	// ---------------------------------------------------------------
	// getSize
	// ---------------------------------------------------------------

	@Test
	@DisplayName("Test for the getSize method")
	void getSize() {
		assertEquals(2, cN.getSize(),
				"Error: The size of the Caravel should be 2.");
	}

	// ---------------------------------------------------------------
	// Constructors
	// ---------------------------------------------------------------

	@Test
	@DisplayName("Test for the constructor with NORTH direction")
	void constructor1() {
		assertAll(
				() -> assertNotNull(cN, "Error: Caravel should not be null"),
				() -> assertEquals(Compass.NORTH, cN.getBearing(), "Error: expected NORTH"),
				() -> assertEquals(5, cN.getTopMostPos(), "Error: expected topmost row 5")
		);
	}

	@Test
	@DisplayName("Test for the constructor with SOUTH direction")
	void constructor2() {
		assertAll(
				() -> assertNotNull(cS, "Error: Caravel should not be null"),
				() -> assertEquals(Compass.SOUTH, cS.getBearing(), "Error: expected SOUTH"),
				() -> assertEquals(6, cS.getBottomMostPos(), "Error: expected bottommost row 6")
		);
	}

	@Test
	@DisplayName("Test for the constructor with EAST direction")
	void constructor3() {
		assertAll(
				() -> assertNotNull(cE, "Error: Caravel should not be null"),
				() -> assertEquals(Compass.EAST, cE.getBearing(), "Error: expected EAST"),
				() -> assertEquals(6, cE.getRightMostPos(), "Error: expected rightmost column 6")
		);
	}

	@Test
	@DisplayName("Test for the constructor with WEST direction")
	void constructor4() {
		assertAll(
				() -> assertNotNull(cW, "Error: Caravel should not be null"),
				() -> assertEquals(Compass.WEST, cW.getBearing(), "Error: expected WEST"),
				() -> assertEquals(5, cW.getLeftMostPos(), "Error: expected leftmost column 5")
		);
	}

	@Test
	@DisplayName("Test for the constructor with invalid direction")
	void constructor5() {
		Position pos = new Position(0, 0); // ✔ criado fora

		assertThrows(NullPointerException.class,
				() -> new Caravel(null, pos),
				"Error: expected NullPointerException for null direction");
	}

	@Test
	@DisplayName("Test for the constructor with null values")
	void constructorNullPointerException() {

		Position pos = new Position(0, 0); // ✔ criado fora

		Exception exception = assertThrows(NullPointerException.class,
				() -> new Caravel(null, pos),
				"Error: expected exception for null bearing");

		assertEquals("Ship's bearing must not be null", exception.getMessage(),
				"Error: wrong exception message");

		Exception exception2 = assertThrows(NullPointerException.class,
				() -> new Caravel(null, null),
				"Error: expected exception for null bearing and position");

		assertEquals("Ship's bearing must not be null", exception2.getMessage(),
				"Error: wrong exception message");
	}
}