package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for Position.
 * Author: britoeabreu
 * Date: 2024-03-19 15:30
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - getRow: 1
 * - getColumn: 1
 * - isValid: 4
 * - isAdjacentTo: 4
 * - isOccupied: 1
 * - isHit: 1
 * - occupy: 1
 * - shoot: 1
 * - equals: 3
 * - hashCode: 1
 * - toString: 1
 */
public class PositionTest {
	private Position position;

	@BeforeEach
	void setUp() {
		position = new Position(2, 3);
	}

	@AfterEach
	void tearDown() {
		position = null;
	}

	// ---------------------------------------------------------------
	// Constructor (int, int)
	// ---------------------------------------------------------------

	@Test
	void constructor() {
		Position pos = new Position(1, 1);
		assertNotNull(pos, "Failed to create Position: object is null");
		assertEquals(1, pos.getRow(), "Failed to set row: expected 1 but got " + pos.getRow());
		assertEquals(1, pos.getColumn(), "Failed to set column: expected 1 but got " + pos.getColumn());
		assertFalse(pos.isOccupied(), "New position should not be occupied");
		assertFalse(pos.isHit(), "New position should not be hit");
	}

	// ---------------------------------------------------------------
	// Constructor (char, int)
	// ---------------------------------------------------------------

	@Test
	void constructorClassic() {
		Position pos = new Position('C', 4);
		assertNotNull(pos, "Failed to create Position from classic notation");
		assertEquals(2, pos.getRow(), "Expected row 2 (C) but got " + pos.getRow());
		assertEquals(3, pos.getColumn(), "Expected column 3 (4-1) but got " + pos.getColumn());
		assertFalse(pos.isOccupied(), "New position should not be occupied");
		assertFalse(pos.isHit(), "New position should not be hit");
	}

	@Test
	void constructorClassicLowercase() {
		Position pos = new Position('c', 4);
		assertEquals(2, pos.getRow(), "Lowercase row letter should be handled correctly");
		assertEquals(3, pos.getColumn(), "Column should be 3 for classic column 4");
	}

	// ---------------------------------------------------------------
	// getRow / getColumn
	// ---------------------------------------------------------------

	@Test
	void getRow() {
		assertEquals(2, position.getRow(), "Failed to get row: expected 2 but got " + position.getRow());
	}

	@Test
	void getColumn() {
		assertEquals(3, position.getColumn(), "Failed to get column: expected 3 but got " + position.getColumn());
	}

	// ---------------------------------------------------------------
	// getClassicRow / getClassicColumn
	// ---------------------------------------------------------------

	@Test
	void getClassicRow() {
		assertEquals('C', position.getClassicRow(), "Expected classic row 'C' for row 2");
	}

	@Test
	void getClassicColumn() {
		assertEquals(4, position.getClassicColumn(), "Expected classic column 4 for column index 3");
	}

	// ---------------------------------------------------------------
	// isInside
	// ---------------------------------------------------------------

	@Test
	void isValid1() {
		position = new Position(0, 0);
		assertTrue(position.isInside(), "Position (0,0) should be valid");
	}

	@Test
	void isValid2() {
		position = new Position(-1, 5);
		assertFalse(position.isInside(), "Position with negative row should be invalid");
	}

	@Test
	void isValid3() {
		position = new Position(5, -1);
		assertFalse(position.isInside(), "Position with negative column should be invalid");
	}

	@Test
	void isValid4() {
		position = new Position(Game.BOARD_SIZE, 5);
		assertFalse(position.isInside(), "Position with row >= BOARD_SIZE should be invalid");
	}

	@Test
	void isValid5() {
		position = new Position(5, Game.BOARD_SIZE);
		assertFalse(position.isInside(), "Position with column >= BOARD_SIZE should be invalid");
	}

	// ---------------------------------------------------------------
	// isAdjacentTo
	// ---------------------------------------------------------------

	@Test
	void isAdjacentTo1() {
		Position other = new Position(2, 4);
		assertTrue(position.isAdjacentTo(other), "Failed to detect horizontally adjacent position");
	}

	@Test
	void isAdjacentTo2() {
		Position other = new Position(3, 3);
		assertTrue(position.isAdjacentTo(other), "Failed to detect vertically adjacent position");
	}

	@Test
	void isAdjacentTo3() {
		Position other = new Position(3, 4);
		assertTrue(position.isAdjacentTo(other), "Failed to detect diagonally adjacent position");
	}

	@Test
	void isAdjacentTo4() {
		Position other = new Position(4, 5);
		assertFalse(position.isAdjacentTo(other), "Non-adjacent position incorrectly identified as adjacent");
	}

	@Test
	void isAdjacentToSelf() {
		assertTrue(position.isAdjacentTo(position), "A position should be adjacent to itself");
	}

	@Test
	void isAdjacentToWithNull() {
		assertThrows(NullPointerException.class, () -> position.isAdjacentTo(null),
				"isAdjacentTo should throw NullPointerException for null input");
	}

	// ---------------------------------------------------------------
	// adjacentPositions
	// ---------------------------------------------------------------

	@Test
	void adjacentPositionsCenter() {
		// position = (2,3) — away from all borders, should have 8 neighbours
		List<IPosition> adj = position.adjacentPositions();
		assertEquals(8, adj.size(), "Central position should have 8 adjacent positions");
	}

	@Test
	void adjacentPositionsCorner() {
		// Corner (0,0) — only 3 valid neighbours
		Position corner = new Position(0, 0);
		List<IPosition> adj = corner.adjacentPositions();
		assertEquals(3, adj.size(), "Corner position should have 3 adjacent positions");
	}

	@Test
	void adjacentPositionsEdge() {
		// Top edge, not a corner: (0,3) — 5 valid neighbours
		Position edge = new Position(0, 3);
		List<IPosition> adj = edge.adjacentPositions();
		assertEquals(5, adj.size(), "Edge (non-corner) position should have 5 adjacent positions");
	}

	@Test
	void adjacentPositionsAllInsideBoard() {
		List<IPosition> adj = position.adjacentPositions();
		for (IPosition p : adj) {
			assertTrue(((Position) p).isInside(), "All adjacent positions must be inside the board");
		}
	}

	@Test
	void adjacentPositionsDoesNotContainSelf() {
		List<IPosition> adj = position.adjacentPositions();
		assertFalse(adj.contains(position), "adjacentPositions should not contain the position itself");
	}

	// ---------------------------------------------------------------
	// randomPosition
	// ---------------------------------------------------------------

	@Test
	void randomPosition() {
		Position pos = Position.randomPosition();
		assertNotNull(pos, "randomPosition should not return null");
		assertTrue(pos.isInside(), "Random position should always be inside the board");
	}

	@Test
	void randomPositionMultipleCalls() {
		// Ensures no call ever produces an out-of-bounds position
		for (int i = 0; i < 100; i++) {
			Position pos = Position.randomPosition();
			assertTrue(pos.isInside(), "Random position must always be inside the board (iteration " + i + ")");
		}
	}

	// ---------------------------------------------------------------
	// isOccupied / occupy
	// ---------------------------------------------------------------

	@Test
	void isOccupied() {
		assertFalse(position.isOccupied(), "New position should not be occupied");
		position.occupy();
		assertTrue(position.isOccupied(), "Position should be occupied after occupy()");
	}

	// ---------------------------------------------------------------
	// isHit / shoot
	// ---------------------------------------------------------------

	@Test
	void isHit() {
		assertFalse(position.isHit(), "New position should not be hit");
		position.shoot();
		assertTrue(position.isHit(), "Position should be hit after shoot()");
	}

	// ---------------------------------------------------------------
	// equals
	// ---------------------------------------------------------------

	@Test
	void equals1() {
		Position same = new Position(2, 3);
		assertTrue(position.equals(same), "Equal positions not identified as equal");
	}

	@Test
	void equals2() {
		assertFalse(position.equals(null), "Position should not equal null");
	}

	@Test
	void equals3() {
		Object other = new Object();
		assertFalse(position.equals(other), "Position should not equal non-Position object");
	}

	@Test
	void equals4() {
		Position other = new Position(2, 4);
		assertFalse(position.equals(other), "Positions with same row but different column should not be equal");
	}

	@Test
	void equals5() {
		assertTrue(position.equals(position), "A position should be equal to itself");
	}

	@Test
	void equalsIgnoresOccupiedAndHitState() {
		// equals is based only on row/column, not state
		Position other = new Position(2, 3);
		other.occupy();
		other.shoot();
		assertTrue(position.equals(other),
				"Positions with same coordinates should be equal regardless of occupied/hit state");
	}

	// ---------------------------------------------------------------
	// hashCode
	// ---------------------------------------------------------------

	@Test
	void hashCodeConsistency() {
		Position same = new Position(2, 3);
		assertEquals(position.hashCode(), same.hashCode(),
				"Hash codes should be consistent for positions with the same coordinates and state");
	}

	@Test
	void hashCodeDifferentPositions() {
		Position other = new Position(2, 4);
		assertNotEquals(position.hashCode(), other.hashCode(),
				"Different positions should typically have different hash codes");
	}

	// ---------------------------------------------------------------
	// toString
	// ---------------------------------------------------------------

	@Test
	void toStringFormat() {
		String expected = "C4";
		assertEquals(expected, position.toString(),
				"Incorrect string representation: expected '" + expected +
						"' but got '" + position.toString() + "'");
	}

}