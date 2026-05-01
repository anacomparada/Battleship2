package battleship;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    private Ship ship;

    private static class TestShip extends Ship {
        TestShip(String category, Compass bearing, IPosition pos, int size) {
            super(category, bearing, pos, size);
        }

        void addPosition(IPosition position) {
            this.positions.add(position);
        }
    }

    @BeforeEach
    void setUp() {
        ship = new Barge(Compass.NORTH, new Position(5, 5));
    }

    @AfterEach
    void tearDown() {
        ship = null;
    }

    @Test
    void constructor() {
        assertAll(
                () -> assertNotNull(ship, "Error: expected ship not null"),
                () -> assertEquals("Barca", ship.getCategory(), "Error: expected category Barca"),
                () -> assertEquals(Compass.NORTH, ship.getBearing(), "Error: expected bearing NORTH"),
                () -> assertEquals(1, ship.getSize(), "Error: expected size 1"),
                () -> assertFalse(ship.getPositions().isEmpty(), "Error: expected positions not empty")
        );
    }

    @Test
    void constructorNullCategoryThrows() {
        Position pos = new Position(0, 0);

        assertThrows(NullPointerException.class,
                () -> new TestShip(null, Compass.NORTH, pos, 0),
                "Error: expected NullPointerException for null category");
    }

    @Test
    void constructorNullBearingThrows() {
        Position pos = new Position(0, 0);

        assertThrows(NullPointerException.class,
                () -> new TestShip("Teste", null, pos, 0),
                "Error: expected NullPointerException for null bearing");
    }

    @Test
    void constructorNullPositionThrows() {
        assertThrows(NullPointerException.class,
                () -> new TestShip("Teste", Compass.NORTH, null, 0),
                "Error: expected NullPointerException for null position");
    }

    @Test
    void buildShip1() {
        Ship s = Ship.buildShip("barca", Compass.NORTH, new Position(0, 0));
        assertTrue(s instanceof Barge, "Error: expected Barge for barca");
    }

    @Test
    void buildShip2() {
        Ship s = Ship.buildShip("caravela", Compass.NORTH, new Position(0, 0));
        assertTrue(s instanceof Caravel, "Error: expected Caravel for caravela");
    }

    @Test
    void buildShip3() {
        Ship s = Ship.buildShip("nau", Compass.NORTH, new Position(0, 0));
        assertTrue(s instanceof Carrack, "Error: expected Carrack for nau");
    }

    @Test
    void buildShip4() {
        Ship s = Ship.buildShip("fragata", Compass.NORTH, new Position(0, 0));
        assertTrue(s instanceof Frigate, "Error: expected Frigate for fragata");
    }

    @Test
    void buildShip5() {
        Ship s = Ship.buildShip("galeao", Compass.NORTH, new Position(0, 0));
        assertTrue(s instanceof Galleon, "Error: expected Galleon for galeao");
    }

    @Test
    void buildShip6() {
        Ship s = Ship.buildShip("unknown", Compass.NORTH, new Position(0, 0));
        assertNull(s, "Error: expected null for unknown ship kind");
    }

    @Test
    void getCategory() {
        assertEquals("Barca", ship.getCategory(), "Error: expected category Barca");
    }

    @Test
    void getSize() {
        assertEquals(1, ship.getSize(), "Error: expected size 1");
    }

    @Test
    void getBearing() {
        assertEquals(Compass.NORTH, ship.getBearing(), "Error: expected bearing NORTH");
    }

    @Test
    void getPosition() {
        assertAll(
                () -> assertEquals(5, ship.getPosition().getRow(), "Error: expected row 5"),
                () -> assertEquals(5, ship.getPosition().getColumn(), "Error: expected column 5")
        );
    }

    @Test
    void getPositions() {
        List<IPosition> positions = ship.getPositions();

        assertAll(
                () -> assertNotNull(positions, "Error: expected positions not null"),
                () -> assertEquals(1, positions.size(), "Error: expected 1 position"),
                () -> assertEquals(new Position(5, 5), positions.get(0), "Error: expected position (5,5)")
        );
    }

    @Test
    void stillFloating1() {
        assertTrue(ship.stillFloating(), "Error: expected ship to still be floating");
    }

    @Test
    void stillFloating2() {
        ship.sink();
        assertFalse(ship.stillFloating(), "Error: expected ship not floating after sink");
    }

    @Test
    void shootInvalidPosition() {
        Position invalid = new Position(-1, -1);

        assertThrows(IllegalArgumentException.class,
                () -> ship.shoot(invalid),
                "Error: expected IllegalArgumentException for invalid position");
    }

    @Test
    void occupiesNullThrowsAssertionError() {
        assertThrows(NullPointerException.class,
                () -> ship.occupies(null),
                "Error: expected NullPointerException when occupies receives null");
    }

    @Test
    void tooCloseToPositionNullThrowsAssertionError() {
        assertThrows(NullPointerException.class,
                () -> ship.tooCloseTo((IPosition) null),
                "Error: expected NullPointerException when position is null");
    }

    @Test
    void tooCloseToShipNullThrowsAssertionError() {
        assertThrows(NullPointerException.class,
                () -> ship.tooCloseTo((IShip) null),
                "Error: expected NullPointerException when ship is null");
    }

    @Test
    void shootNullThrowsAssertionError() {
        assertThrows(NullPointerException.class,
                () -> ship.shoot(null),
                "Error: expected NullPointerException when shoot receives null");
    }

    @Test
    void toStringTest() {
        String expected = "[Barca n F6]";

        assertEquals(expected, ship.toString(),
                "Error: expected " + expected + " but got " + ship.toString());
    }
}
