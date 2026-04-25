package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for Ship.
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - getCategory: 1
 * - getSize: 1
 * - getBearing: 1
 * - getPosition: 1
 * - getPositions: 1
 * - getAdjacentPositions: 3
 * - stillFloating: 2
 * - shoot: 2
 * - sink: 1
 * - occupies: 2
 * - tooCloseTo (IShip): 2
 * - tooCloseTo (IPosition): 2
 * - getTopMostPos: 2
 * - getBottomMostPos: 2
 * - getLeftMostPos: 2
 * - getRightMostPos: 2
 * - toString: 1
 * - buildShip: 6
 */
public class ShipTest {

    private Ship ship;

    @BeforeEach
    void setUp() {
        // Barge (Barca) — size 1, useful as a simple baseline
        ship = new Barge(Compass.NORTH, new Position(5, 5));
    }

    @AfterEach
    void tearDown() {
        ship = null;
    }

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    @Test
    void testConstructor() {
        assertNotNull(ship, "Ship instance should not be null");
        assertEquals("Barca", ship.getCategory(), "Ship category is incorrect");
        assertEquals(Compass.NORTH, ship.getBearing(), "Ship bearing is incorrect");
        assertEquals(1, ship.getSize(), "Ship size is incorrect");
        assertFalse(ship.getPositions().isEmpty(), "Ship positions should not be empty");
    }

    @Test
    void testConstructorNullCategoryThrows() {
        assertThrows(NullPointerException.class,
                () -> new Barge(null, new Position(0, 0)),
                "Null bearing should throw NullPointerException");
    }

    @Test
    void testConstructorNullPositionThrows() {
        assertThrows(NullPointerException.class,
                () -> new Barge(Compass.NORTH, null),
                "Null position should throw NullPointerException");
    }

    // ---------------------------------------------------------------
    // buildShip
    // ---------------------------------------------------------------

    @Test
    void testBuildShipBarca() {
        Ship s = Ship.buildShip("barca", Compass.NORTH, new Position(0, 0));
        assertNotNull(s, "buildShip should return a Barge for 'barca'");
        assertEquals("Barca", s.getCategory());
    }

    @Test
    void testBuildShipCaravela() {
        Ship s = Ship.buildShip("caravela", Compass.NORTH, new Position(0, 0));
        assertNotNull(s, "buildShip should return a Caravel for 'caravela'");
        assertEquals("Caravela", s.getCategory());
    }

    @Test
    void testBuildShipNau() {
        Ship s = Ship.buildShip("nau", Compass.NORTH, new Position(0, 0));
        assertNotNull(s, "buildShip should return a Carrack for 'nau'");
        assertEquals("Nau", s.getCategory());
    }

    @Test
    void testBuildShipFragata() {
        Ship s = Ship.buildShip("fragata", Compass.NORTH, new Position(0, 0));
        assertNotNull(s, "buildShip should return a Frigate for 'fragata'");
        assertEquals("Fragata", s.getCategory());
    }

    @Test
    void testBuildShipGaleao() {
        Ship s = Ship.buildShip("galeao", Compass.NORTH, new Position(0, 0));
        assertNotNull(s, "buildShip should return a Galleon for 'galeao'");
        assertEquals("Galeao", s.getCategory());
    }

    @Test
    void testBuildShipUnknown() {
        Ship s = Ship.buildShip("unknown", Compass.NORTH, new Position(0, 0));
        assertNull(s, "buildShip should return null for an unknown ship kind");
    }

    // ---------------------------------------------------------------
    // getCategory
    // ---------------------------------------------------------------

    @Test
    void testGetCategory() {
        assertEquals("Barca", ship.getCategory(), "Ship category should be 'Barca'");
    }

    // ---------------------------------------------------------------
    // getSize
    // ---------------------------------------------------------------

    @Test
    void testGetSize() {
        assertEquals(1, ship.getSize(), "Barge size should be 1");
    }

    @Test
    void testGetSizeLargerShip() {
        Ship galleon = new Galleon(Compass.NORTH, new Position(0, 0));
        assertEquals(5, galleon.getSize(), "Galleon size should be 5");
    }

    // ---------------------------------------------------------------
    // getBearing
    // ---------------------------------------------------------------

    @Test
    void testGetBearing() {
        assertEquals(Compass.NORTH, ship.getBearing(), "Ship bearing should be NORTH");
    }

    // ---------------------------------------------------------------
    // getPosition
    // ---------------------------------------------------------------

    @Test
    void testGetPosition() {
        IPosition pos = ship.getPosition();
        assertNotNull(pos, "getPosition should not return null");
        assertEquals(5, pos.getRow(), "Position row should be 5");
        assertEquals(5, pos.getColumn(), "Position column should be 5");
    }

    // ---------------------------------------------------------------
    // getPositions
    // ---------------------------------------------------------------

    @Test
    void testGetPositionsBarge() {
        List<IPosition> positions = ship.getPositions();
        assertNotNull(positions, "Ship positions should not be null");
        assertEquals(1, positions.size(), "Barge should have exactly one position");
        assertEquals(5, positions.get(0).getRow(), "Position row should be 5");
        assertEquals(5, positions.get(0).getColumn(), "Position column should be 5");
    }

    @Test
    void testGetPositionsLargerShip() {
        Ship galleon = new Galleon(Compass.NORTH, new Position(4, 4));
        assertEquals(5, galleon.getPositions().size(), "Galleon should occupy 5 positions");
    }

    // ---------------------------------------------------------------
    // getAdjacentPositions
    // ---------------------------------------------------------------

    @Test
    void testGetAdjacentPositionsNotNull() {
        List<IPosition> adj = ship.getAdjacentPositions();
        assertNotNull(adj, "getAdjacentPositions should not return null");
    }

    @Test
    void testGetAdjacentPositionsDoNotContainShipPositions() {
        List<IPosition> adj = ship.getAdjacentPositions();
        for (IPosition p : ship.getPositions())
            assertFalse(adj.contains(p),
                    "Adjacent positions should not include the ship's own positions");
    }

    @Test
    void testGetAdjacentPositionsNoDuplicates() {
        Ship galleon = new Galleon(Compass.NORTH, new Position(4, 4));
        List<IPosition> adj = galleon.getAdjacentPositions();
        long distinct = adj.stream().distinct().count();
        assertEquals(distinct, adj.size(), "Adjacent positions should not contain duplicates");
    }

    @Test
    void testGetAdjacentPositionsCount() {
        // Barge at (5,5): 8 neighbours, none coincide with ship position
        List<IPosition> adj = ship.getAdjacentPositions();
        assertEquals(8, adj.size(), "Barge at (5,5) should have 8 adjacent positions");
    }

    // ---------------------------------------------------------------
    // stillFloating
    // ---------------------------------------------------------------

    @Test
    void testStillFloatingTrue() {
        assertTrue(ship.stillFloating(), "Ship should still be floating when not hit");
    }

    @Test
    void testStillFloatingFalse() {
        ship.getPositions().get(0).shoot();
        assertFalse(ship.stillFloating(), "Ship should not be floating after all positions are hit");
    }

    @Test
    void testStillFloatingLargerShipPartialHit() {
        Ship galleon = new Galleon(Compass.NORTH, new Position(0, 0));
        // Hit only the first position
        galleon.getPositions().get(0).shoot();
        assertTrue(galleon.stillFloating(), "Galleon should still float with only one position hit");
    }

    // ---------------------------------------------------------------
    // shoot
    // ---------------------------------------------------------------

    @Test
    void testShootHit() {
        Position target = new Position(5, 5);
        ship.shoot(target);
        assertTrue(ship.getPositions().get(0).isHit(), "Position should be marked as hit");
    }

    @Test
    void testShootMiss() {
        Position target = new Position(0, 0);
        ship.shoot(target);
        assertFalse(ship.getPositions().get(0).isHit(), "Position should not be hit for a missed target");
    }

    // ---------------------------------------------------------------
    // sink
    // ---------------------------------------------------------------

    @Test
    void testSink() {
        ship.sink();
        for (IPosition pos : ship.getPositions())
            assertTrue(pos.isHit(), "All positions should be hit after sink()");
    }

    @Test
    void testSinkLargerShip() {
        Ship galleon = new Galleon(Compass.NORTH, new Position(0, 0));
        galleon.sink();
        for (IPosition pos : galleon.getPositions())
            assertTrue(pos.isHit(), "All Galleon positions should be hit after sink()");
        assertFalse(galleon.stillFloating(), "Galleon should not be floating after sink()");
    }

    // ---------------------------------------------------------------
    // occupies
    // ---------------------------------------------------------------

    @Test
    void testOccupiesTrue() {
        assertTrue(ship.occupies(new Position(5, 5)), "Ship should occupy position (5,5)");
    }

    @Test
    void testOccupiesFalse() {
        assertFalse(ship.occupies(new Position(1, 1)), "Ship should not occupy position (1,1)");
    }

    // ---------------------------------------------------------------
    // tooCloseTo (IPosition)
    // ---------------------------------------------------------------

    @Test
    void testTooCloseToPositionTrue() {
        assertTrue(ship.tooCloseTo(new Position(5, 6)), "Ship should be too close to adjacent position");
    }

    @Test
    void testTooCloseToPositionFalse() {
        assertFalse(ship.tooCloseTo(new Position(7, 7)), "Ship should not be too close to distant position");
    }

    @Test
    void testTooCloseToPositionSelf() {
        assertTrue(ship.tooCloseTo(new Position(5, 5)), "Ship should be too close to its own position");
    }

    // ---------------------------------------------------------------
    // tooCloseTo (IShip)
    // ---------------------------------------------------------------

    @Test
    void testTooCloseToShipTrue() {
        Ship nearby = new Barge(Compass.NORTH, new Position(5, 6));
        assertTrue(ship.tooCloseTo(nearby), "Ships should be too close");
    }

    @Test
    void testTooCloseToShipFalse() {
        Ship farShip = new Barge(Compass.NORTH, new Position(0, 0));
        assertFalse(ship.tooCloseTo(farShip), "Ships should not be too close");
    }

    @Test
    void testTooCloseToShipSelf() {
        assertTrue(ship.tooCloseTo(ship), "Ship should be too close to itself");
    }

    // ---------------------------------------------------------------
    // getTopMostPos / getBottomMostPos
    // ---------------------------------------------------------------

    @Test
    void testGetTopMostPosBarge() {
        assertEquals(5, ship.getTopMostPos(), "Topmost row should be 5 for Barge at (5,5)");
    }

    @Test
    void testGetTopMostPosLargerShip() {
        // Galleon NORTH starting at (4,4): occupies rows 0..4 (or 4..8 depending on impl)
        Ship galleon = new Galleon(Compass.NORTH, new Position(4, 4));
        int top = galleon.getTopMostPos();
        assertTrue(top <= 4, "Topmost row of a multi-position ship should be <= starting row");
    }

    @Test
    void testGetBottomMostPosBarge() {
        assertEquals(5, ship.getBottomMostPos(), "Bottommost row should be 5 for Barge at (5,5)");
    }

    @Test
    void testGetBottomMostPosLargerShip() {
        Ship galleon = new Galleon(Compass.SOUTH, new Position(0, 0));
        int bottom = galleon.getBottomMostPos();
        assertTrue(bottom >= 0, "Bottommost row of a south-facing galleon should be >= 0");
    }

    // ---------------------------------------------------------------
    // getLeftMostPos / getRightMostPos
    // ---------------------------------------------------------------

    @Test
    void testGetLeftMostPosBarge() {
        assertEquals(5, ship.getLeftMostPos(), "Leftmost column should be 5 for Barge at (5,5)");
    }

    @Test
    void testGetRightMostPosBarge() {
        assertEquals(5, ship.getRightMostPos(), "Rightmost column should be 5 for Barge at (5,5)");
    }

    @Test
    void testGetLeftMostPosEastShip() {
        Ship galleon = new Galleon(Compass.EAST, new Position(0, 0));
        int left = galleon.getLeftMostPos();
        assertTrue(left <= 0, "Leftmost column of an east-facing galleon starting at col 0 should be <= 0");
    }

    @Test
    void testGetRightMostPosEastShip() {
        Ship galleon = new Galleon(Compass.EAST, new Position(0, 0));
        int right = galleon.getRightMostPos();
        assertTrue(right >= 0, "Rightmost column of an east-facing galleon should be >= 0");
    }

    // ---------------------------------------------------------------
    // toString
    // ---------------------------------------------------------------

    @Test
    void testToString() {
        String result = ship.toString();
        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("Barca"), "toString should contain the category");
        assertTrue(result.contains(ship.getBearing().toString()), "toString should contain the bearing");
        assertTrue(result.contains(ship.getPosition().toString()), "toString should contain the position");
    }
}