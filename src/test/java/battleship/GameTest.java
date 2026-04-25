package battleship;

import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Game.
 * Author: britoeabreu
 * Cyclomatic Complexity for each method:
 * - Game (constructor): 1
 * - fireShots: 4
 * - fireSingleShot: 7
 * - getAlienMoves: 1
 * - getRepeatedShots: 1
 * - getInvalidShots: 1
 * - getHits: 1
 * - getSunkShips: 1
 * - getRemainingShips: 1
 * - repeatedShot: 2
 * - jsonShots: 3
 * - readEnemyFire: 8
 * - readAlienFire: 8
 * - randomEnemyFire: 3
 * - randomPlayerFire: 3
 */
class GameTest {

	private IFleet myFleet;
	private Game game;

	@BeforeEach
	void setUp() {
		myFleet = new Fleet();
		game = new Game(myFleet);
	}

	@AfterEach
	void tearDown() {
		game = null;
		myFleet = null;
	}

	// -------------------------------------------------------
	// Construtor
	// -------------------------------------------------------

	@Test
	@DisplayName("Construtor: instância não nula e estado inicial correto")
	void constructor() {
		assertNotNull(game, "Game instance should not be null after construction.");
		assertNotNull(game.getAlienMoves(), "AlienMoves list should not be null after initialization.");
		assertTrue(game.getAlienMoves().isEmpty(), "AlienMoves list should be empty upon initialization.");
		assertNotNull(game.getMyMoves(), "MyMoves list should not be null after initialization.");
		assertTrue(game.getMyMoves().isEmpty(), "MyMoves list should be empty upon initialization.");
		assertNotNull(game.getMyFleet(), "MyFleet should not be null after initialization.");
		assertNotNull(game.getAlienFleet(), "AlienFleet should not be null after initialization.");
		assertEquals(0, game.getInvalidShots(), "Invalid shots count should be zero upon initialization.");
		assertEquals(0, game.getRepeatedShots(), "Repeated shots count should be zero upon initialization.");
		assertEquals(0, game.getHits(), "Hits count should be zero upon initialization.");
		assertEquals(0, game.getSunkShips(), "Sunk ships count should be zero upon initialization.");
	}

	@Test
	@DisplayName("Construtor: frota do jogador é a mesma passada no construtor")
	void constructorSetsMyFleet() {
		assertSame(myFleet, game.getMyFleet());
	}

	// -------------------------------------------------------
	// fireSingleShot
	// -------------------------------------------------------

	@Test
	@DisplayName("fireSingleShot: posição inválida incrementa countInvalidShots")
	void fire2() {
		Position invalidPosition = new Position(-1, 5);
		game.fireSingleShot(invalidPosition, false);
		assertEquals(1, game.getInvalidShots(), "Invalid shots counter should increase for an invalid shot.");
	}

	@Test
	@DisplayName("fireSingleShot: tiro repetido incrementa countRepeatedShots")
	void fire3() {
		Position position = new Position(2, 3);
		game.fireSingleShot(position, false);
		game.fireSingleShot(position, true);
		assertEquals(1, game.getRepeatedShots(), "Repeated shots counter should increase for a repeated shot.");
	}

	@Test
	@DisplayName("fireSingleShot: posição na água não incrementa hits")
	void fireSingleShotWater() {
		IPosition waterPos = findWaterPosition(myFleet);
		game.fireSingleShot(waterPos, false);
		assertEquals(0, game.getHits(), "Water shot should not increment hits.");
		assertEquals(0, game.getInvalidShots(), "Water shot should not increment invalid shots.");
		assertEquals(0, game.getRepeatedShots(), "Water shot should not increment repeated shots.");
	}

	@Test
	@DisplayName("fireSingleShot: acerto num navio incrementa countHits")
	void fireSingleShotHitsShip() {
		Ship ship = new Barge(Compass.NORTH, new Position(1, 1));
		myFleet.addShip(ship);
		IPosition shipPos = ship.getPositions().get(0);
		game.fireSingleShot(shipPos, false);
		assertEquals(1, game.getHits(), "Hitting a ship should increment hits counter.");
	}

	@Test
	@DisplayName("fireSingleShot: afundar navio incrementa countSinks")
	void fireSingleShotSinksShip() {
		Ship ship = new Barge(Compass.NORTH, new Position(1, 1));
		myFleet.addShip(ship);
		for (IPosition pos : ship.getPositions()) {
			game.fireSingleShot(pos, false);
		}
		assertEquals(1, game.getSunkShips());
	}

	@Test
	@DisplayName("fireSingleShot: tiro já usado em jogadas anteriores é contado como repetido")
	void fireSingleShotRepeatedFromPreviousMove() {
		List<IPosition> shots = buildDistinctShots(new Position(0, 0));
		game.fireShots(shots);
		game.fireSingleShot(new Position(0, 0), false);
		assertEquals(1, game.getRepeatedShots(), "Shot at already-fired position should be counted as repeated.");
	}

	// -------------------------------------------------------
	// fireShots
	// -------------------------------------------------------

	@Test
	@DisplayName("fireShots: número errado de tiros lança IllegalArgumentException")
	void fireShotsWrongCount() {
		List<IPosition> shots = List.of(new Position(0, 0));
		assertThrows(IllegalArgumentException.class, () -> game.fireShots(shots));
	}

	@Test
	@DisplayName("fireShots: 3 tiros válidos adicionam um movimento à lista")
	void fireShotsAddsMove() {
		game.fireShots(buildDistinctShots(new Position(0, 0)));
		assertEquals(1, game.getAlienMoves().size());
	}

	@Test
	@DisplayName("fireShots: tiros duplicados na mesma rajada contabilizam repetidos")
	void fireShotsDuplicatesInSameVolley() {
		IPosition pos = new Position(0, 0);
		game.fireShots(List.of(pos, pos, pos));
		assertTrue(game.getRepeatedShots() >= 2);
	}

	@Test
	@DisplayName("fireShots: múltiplas jogadas consecutivas incrementam moveNumber")
	void fireShotsMultipleMoves() {
		game.fireShots(buildDistinctShots(new Position(0, 0)));
		game.fireShots(buildDistinctShots(new Position(1, 0)));
		game.fireShots(buildDistinctShots(new Position(2, 0)));
		assertEquals(3, game.getAlienMoves().size());
	}

	// -------------------------------------------------------
	// repeatedShot
	// -------------------------------------------------------

	@Test
	@DisplayName("repeatedShot: retorna true após tiro nessa posição")
	void repeatedShot1() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		assertTrue(game.repeatedShot(new Position(2, 3)), "Position (2,3) should be marked as repeated after firing.");
	}

	@Test
	@DisplayName("repeatedShot: retorna false para posição ainda não usada")
	void repeatedShot2() {
		assertFalse(game.repeatedShot(new Position(2, 3)), "Position (2,3) should not be marked as repeated before firing.");
	}

	// -------------------------------------------------------
	// getAlienMoves
	// -------------------------------------------------------

	@Test
	@DisplayName("getAlienMoves: contém um movimento após um fireShots")
	void getAlienMoves() {
		game.fireShots(List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5)));
		assertEquals(1, game.getAlienMoves().size(), "AlienMoves should contain one move after firing once.");
	}

	// -------------------------------------------------------
	// getRemainingShips
	// -------------------------------------------------------

	@Test
	@DisplayName("getRemainingShips: contagem correta ao adicionar e afundar navios")
	void getRemainingShips() {
		Ship ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		Ship ship2 = new Frigate(Compass.EAST, new Position(5, 5));
		myFleet.addShip(ship1);
		assertEquals(1, game.getRemainingShips(), "Just one ship was created!");
		myFleet.addShip(ship2);
		assertEquals(2, game.getRemainingShips(), "Two ships were created!");
		ship2.sink();
		assertEquals(1, game.getRemainingShips(), "Remaining ships count should be 1 after sinking one of two ships.");
	}

	// -------------------------------------------------------
	// jsonShots
	// -------------------------------------------------------

	@Test
	@DisplayName("jsonShots: serializa lista de posições em JSON com os campos corretos")
	void jsonShotsValidOutput() {
		List<IPosition> shots = List.of(new Position('A', 1), new Position('B', 2), new Position('C', 3));
		String json = Game.jsonShots(shots);
		assertNotNull(json);
		assertTrue(json.contains("\"row\""));
		assertTrue(json.contains("\"column\""));
	}

	@Test
	@DisplayName("jsonShots: lista vazia retorna array JSON vazio")
	void jsonShotsEmptyList() {
		String json = Game.jsonShots(new ArrayList<>());
		assertNotNull(json);
		assertTrue(json.trim().startsWith("[") && json.trim().endsWith("]"));
	}

	@Test
	@DisplayName("jsonShots: valores de row e column estão corretos no JSON")
	void jsonShotsCorrectValues() {
		String json = Game.jsonShots(List.of(new Position('A', 1)));
		assertTrue(json.contains("1"));
	}

	// -------------------------------------------------------
	// readEnemyFire
	// -------------------------------------------------------

	@Test
	@DisplayName("readEnemyFire: formato clássico concatenado (ex: A1 B2 C3) é aceite")
	void readEnemyFireClassicFormat() {
		assertDoesNotThrow(() -> game.readEnemyFire(new Scanner("A1 B2 C3")));
		assertEquals(1, game.getMyMoves().size());
	}

	@Test
	@DisplayName("readEnemyFire: formato letra separada de número (ex: A 1 B 2 C 3) é aceite")
	void readEnemyFireSpaceSeparatedFormat() {
		assertDoesNotThrow(() -> game.readEnemyFire(new Scanner("A 1 B 2 C 3")));
		assertEquals(1, game.getMyMoves().size());
	}

	@Test
	@DisplayName("readEnemyFire: letra sem número seguinte lança exceção")
	void readEnemyFireLetterWithoutNumber() {
		assertThrows(IllegalArgumentException.class,
				() -> game.readEnemyFire(new Scanner("A B C")));
	}

	@Test
	@DisplayName("readEnemyFire: formato JSON array é aceite")
	void readEnemyFireJsonFormat() {
		String json = "[{\"row\":\"A\",\"column\":1},{\"row\":\"B\",\"column\":2},{\"row\":\"C\",\"column\":3}]";
		assertDoesNotThrow(() -> game.readEnemyFire(new Scanner(json)));
		assertEquals(1, game.getMyMoves().size());
	}

	@Test
	@DisplayName("readEnemyFire: formato JSON objeto com chave 'shots' é aceite")
	void readEnemyFireJsonObjectWithShotsKey() {
		String json = "{\"shots\":[{\"row\":\"A\",\"column\":1},{\"row\":\"B\",\"column\":2},{\"row\":\"C\",\"column\":3}]}";
		assertDoesNotThrow(() -> game.readEnemyFire(new Scanner(json)));
		assertEquals(1, game.getMyMoves().size());
	}

	@Test
	@DisplayName("readEnemyFire: JSON cujo nó shots não é array lança exceção")
	void readEnemyFireJsonShotsNotArray() {
		String json = "{\"shots\":\"invalid\"}";
		assertThrows(IllegalArgumentException.class,
				() -> game.readEnemyFire(new Scanner(json)));
	}

	@Test
	@DisplayName("readEnemyFire: JSON com número errado de tiros lança exceção")
	void readEnemyFireJsonWrongCount() {
		String json = "[{\"row\":\"A\",\"column\":1}]";
		assertThrows(IllegalArgumentException.class,
				() -> game.readEnemyFire(new Scanner(json)));
	}

	@Test
	@DisplayName("readEnemyFire: prefixo 'rajada' é ignorado")
	void readEnemyFireWithRajadaPrefix() {
		assertDoesNotThrow(() -> game.readEnemyFire(new Scanner("rajada A1 B2 C3")));
		assertEquals(1, game.getMyMoves().size());
	}

	@Test
	@DisplayName("readEnemyFire: prefixo 'RAJADA' maiúsculas é ignorado")
	void readEnemyFireWithRajadaUpperCase() {
		assertDoesNotThrow(() -> game.readEnemyFire(new Scanner("RAJADA A1 B2 C3")));
		assertEquals(1, game.getMyMoves().size());
	}

	@Test
	@DisplayName("readEnemyFire: número errado de posições no formato clássico lança exceção")
	void readEnemyFireClassicWrongCount() {
		assertThrows(IllegalArgumentException.class,
				() -> game.readEnemyFire(new Scanner("A1 B2")));
	}

	// -------------------------------------------------------
	// readAlienFire
	// -------------------------------------------------------

	@Test
	@DisplayName("readAlienFire: formato clássico concatenado é aceite")
	void readAlienFireClassicFormat() {
		assertDoesNotThrow(() -> game.readAlienFire(new Scanner("A1 B2 C3")));
		assertEquals(1, game.getAlienMoves().size());
	}

	@Test
	@DisplayName("readAlienFire: formato letra separada de número (ex: A 1 B 2 C 3) é aceite")
	void readAlienFireSpaceSeparatedFormat() {
		assertDoesNotThrow(() -> game.readAlienFire(new Scanner("A 1 B 2 C 3")));
		assertEquals(1, game.getAlienMoves().size());
	}

	@Test
	@DisplayName("readAlienFire: letra sem número seguinte lança exceção")
	void readAlienFireLetterWithoutNumber() {
		assertThrows(IllegalArgumentException.class,
				() -> game.readAlienFire(new Scanner("A B C")));
	}

	@Test
	@DisplayName("readAlienFire: formato JSON array é aceite")
	void readAlienFireJsonFormat() {
		String json = "[{\"row\":\"A\",\"column\":1},{\"row\":\"B\",\"column\":2},{\"row\":\"C\",\"column\":3}]";
		assertDoesNotThrow(() -> game.readAlienFire(new Scanner(json)));
		assertEquals(1, game.getAlienMoves().size());
	}

	@Test
	@DisplayName("readAlienFire: formato JSON objeto com chave 'shots' é aceite")
	void readAlienFireJsonObjectWithShotsKey() {
		String json = "{\"shots\":[{\"row\":\"A\",\"column\":1},{\"row\":\"B\",\"column\":2},{\"row\":\"C\",\"column\":3}]}";
		assertDoesNotThrow(() -> game.readAlienFire(new Scanner(json)));
		assertEquals(1, game.getAlienMoves().size());
	}

	@Test
	@DisplayName("readAlienFire: JSON com número errado de tiros lança exceção")
	void readAlienFireJsonWrongCount() {
		String json = "[{\"row\":\"A\",\"column\":1}]";
		assertThrows(IllegalArgumentException.class,
				() -> game.readAlienFire(new Scanner(json)));
	}

	@Test
	@DisplayName("readAlienFire: prefixo 'rajada' é ignorado")
	void readAlienFireWithRajadaPrefix() {
		assertDoesNotThrow(() -> game.readAlienFire(new Scanner("rajada A1 B2 C3")));
		assertEquals(1, game.getAlienMoves().size());
	}

	@Test
	@DisplayName("readAlienFire: prefixo 'Rajada' misto é ignorado")
	void readAlienFireWithRajadaMixedCase() {
		assertDoesNotThrow(() -> game.readAlienFire(new Scanner("Rajada D1 E2 F3")));
		assertEquals(1, game.getAlienMoves().size());
	}

	// -------------------------------------------------------
	// randomEnemyFire / randomPlayerFire
	// -------------------------------------------------------

	@Test
	@DisplayName("randomEnemyFire: retorna JSON não nulo e regista movimento")
	void randomEnemyFire() {
		String json = game.randomEnemyFire();
		assertNotNull(json);
		assertEquals(1, game.getAlienMoves().size());
	}

	@Test
	@DisplayName("randomPlayerFire: retorna JSON não nulo e regista movimento")
	void randomPlayerFire() {
		String json = game.randomPlayerFire();
		assertNotNull(json);
		assertEquals(1, game.getMyMoves().size());
	}

	@Test
	@DisplayName("randomEnemyFire seguido de randomPlayerFire cobre ramos de ambos")
	void randomFireBothDirections() {
		String jsonEnemy = game.randomEnemyFire();
		String jsonPlayer = game.randomPlayerFire();
		assertNotNull(jsonEnemy);
		assertNotNull(jsonPlayer);
		assertEquals(1, game.getAlienMoves().size());
		assertEquals(1, game.getMyMoves().size());
	}

	@Test
	@DisplayName("randomEnemyFire: cobre o ramo else quando posições disponíveis < NUMBER_SHOTS")
	void randomEnemyFireElseBranch() {
		for (int i = 0; i < 32; i++) game.randomEnemyFire();
		assertDoesNotThrow(() -> game.randomEnemyFire());
		assertTrue(game.getAlienMoves().size() >= 32);
	}

	@Test
	@DisplayName("randomPlayerFire: cobre o ramo else quando posições disponíveis < NUMBER_SHOTS")
	void randomPlayerFireElseBranch() {
		for (int i = 0; i < 32; i++) game.randomPlayerFire();
		assertDoesNotThrow(() -> game.randomPlayerFire());
		assertTrue(game.getMyMoves().size() >= 32);
	}

	// -------------------------------------------------------
	// over
	// -------------------------------------------------------

	@Test
	@DisplayName("over: não lança exceção")
	void overDoesNotThrow() {
		assertDoesNotThrow(() -> game.over());
	}

	// -------------------------------------------------------
	// printBoard
	// -------------------------------------------------------

	@Test
	@DisplayName("printBoard: sem tiros e sem legenda não lança exceção")
	void printBoardNoShotsNoLegend() {
		assertDoesNotThrow(() ->
				Game.printBoard(myFleet, new ArrayList<>(), false, false));
	}

	@Test
	@DisplayName("printBoard: com legenda não lança exceção")
	void printBoardWithLegend() {
		assertDoesNotThrow(() ->
				Game.printBoard(myFleet, new ArrayList<>(), false, true));
	}

	@Test
	@DisplayName("printBoard: tiro em navio (SHIP_MARKER → SHOT_SHIP_MARKER)")
	void printBoardShotHitsShip() {
		Ship ship = new Barge(Compass.NORTH, new Position(0, 0));
		myFleet.addShip(ship);
		IPosition shipPos = ship.getPositions().get(0);
		game.fireShots(buildDistinctShots(shipPos));
		assertDoesNotThrow(() ->
				Game.printBoard(myFleet, game.getAlienMoves(), true, false));
	}

	@Test
	@DisplayName("printBoard: tiro em água (EMPTY_MARKER → SHOT_WATER_MARKER)")
	void printBoardShotHitsWater() {
		IPosition waterPos = findWaterPosition(myFleet);
		game.fireShots(buildDistinctShots(waterPos));
		assertDoesNotThrow(() ->
				Game.printBoard(myFleet, game.getAlienMoves(), true, false));
	}

	@Test
	@DisplayName("printBoard: tiro fora dos limites é ignorado (isInside == false)")
	void printBoardShotOutOfBounds() {
		List<IPosition> shots = List.of(
				new Position(-1, -1),
				new Position(0, 1),
				new Position(0, 2)
		);
		game.fireShots(shots);
		assertDoesNotThrow(() ->
				Game.printBoard(myFleet, game.getAlienMoves(), true, false));
	}

	@Test
	@DisplayName("printBoard: navio afundado mostra posições adjacentes")
	void printBoardSunkShipShowsAdjacentPositions() {
		Ship ship = new Barge(Compass.NORTH, new Position(3, 3));
		myFleet.addShip(ship);
		for (IPosition pos : ship.getPositions()) {
			game.fireShots(buildDistinctShots(pos));
		}
		assertDoesNotThrow(() ->
				Game.printBoard(myFleet, game.getAlienMoves(), true, false));
	}

	@Test
	@DisplayName("printBoard: tiro em posição adjacente a navio afundado (SHIP_ADJACENT_MARKER → SHOT_WATER_MARKER)")
	void printBoardShotOnAdjacentPosition() {
		Ship ship = new Barge(Compass.NORTH, new Position(3, 3));
		myFleet.addShip(ship);
		for (IPosition pos : ship.getPositions()) {
			game.fireShots(buildDistinctShots(pos));
		}
		List<IPosition> adjacentPositions = ship.getAdjacentPositions();
		if (!adjacentPositions.isEmpty()) {
			IPosition adjPos = adjacentPositions.get(0);
			if (adjPos.isInside()) {
				game.fireShots(buildDistinctShots(adjPos));
			}
		}
		assertDoesNotThrow(() ->
				Game.printBoard(myFleet, game.getAlienMoves(), true, false));
	}

	@Test
	@DisplayName("printBoard: com tiros e com legenda não lança exceção")
	void printBoardWithShotsAndLegend() {
		Ship ship = new Barge(Compass.NORTH, new Position(0, 0));
		myFleet.addShip(ship);
		game.fireShots(buildDistinctShots(ship.getPositions().get(0)));
		assertDoesNotThrow(() ->
				Game.printBoard(myFleet, game.getAlienMoves(), true, true));
	}

	// -------------------------------------------------------
	// printBothBoards / buildMap (S2699 + S108 corrigidos)
	// -------------------------------------------------------

	@Test
	@DisplayName("printBothBoards: ramo myMoves e alienMoves vazios (showShots=false)")
	void printBothBoardsEmptyMoves() {
		try { game.printBothBoards(false); }
		catch (java.awt.HeadlessException ignored) {
			// Esperado em ambiente CI sem display — comportamento correto
		}
		assertTrue(game.getMyMoves().isEmpty(), "MyMoves should still be empty after printBothBoards");
		assertTrue(game.getAlienMoves().isEmpty(), "AlienMoves should still be empty after printBothBoards");
	}

	@Test
	@DisplayName("printBothBoards: ramo myMoves e alienMoves não vazios (showShots=true)")
	void printBothBoardsWithMovesShowShots() {
		game.randomPlayerFire();
		game.randomEnemyFire();
		int myMovesBefore = game.getMyMoves().size();
		int alienMovesBefore = game.getAlienMoves().size();
		try { game.printBothBoards(true); }
		catch (java.awt.HeadlessException ignored) {
			// Esperado em ambiente CI sem display — comportamento correto
		}
		assertEquals(myMovesBefore, game.getMyMoves().size(), "MyMoves size should not change");
		assertEquals(alienMovesBefore, game.getAlienMoves().size(), "AlienMoves size should not change");
	}

	@Test
	@DisplayName("buildMap: ramo hideShips=true e showShots=true com tiros")
	void printBothBoardsHideShipsAndShots() {
		game.randomPlayerFire();
		game.randomEnemyFire();
		int myMovesBefore = game.getMyMoves().size();
		try { game.printBothBoards(true); }
		catch (java.awt.HeadlessException ignored) {
			// Esperado em ambiente CI sem display — comportamento correto
		}
		assertEquals(myMovesBefore, game.getMyMoves().size(), "MyMoves size should not change after printBothBoards");
	}

	@Test
	@DisplayName("buildMap: tiro em navio cobre SHIP_MARKER → SHOT_SHIP_MARKER em buildMap")
	void printBothBoardsShotHitsShipInBuildMap() {
		Ship ship = new Barge(Compass.NORTH, new Position(1, 1));
		myFleet.addShip(ship);
		game.fireShots(buildDistinctShots(ship.getPositions().get(0)));
		int movesBefore = game.getAlienMoves().size();
		try { game.printBothBoards(true); }
		catch (java.awt.HeadlessException ignored) {
			// Esperado em ambiente CI sem display — comportamento correto
		}
		assertEquals(movesBefore, game.getAlienMoves().size(), "AlienMoves size should not change");
	}

	@Test
	@DisplayName("buildMap: navio afundado cobre ramo !stillFloating e adjacentPositions")
	void printBothBoardsSunkShipAdjacent() {
		Ship ship = new Barge(Compass.NORTH, new Position(1, 1));
		myFleet.addShip(ship);
		for (IPosition pos : ship.getPositions()) {
			game.fireShots(buildDistinctShots(pos));
		}
		assertEquals(1, game.getSunkShips(), "Ship should be sunk before printBothBoards");
		try { game.printBothBoards(true); }
		catch (java.awt.HeadlessException ignored) {
			// Esperado em ambiente CI sem display — comportamento correto
		}
		assertEquals(1, game.getSunkShips(), "Sunk ships count should not change after printBothBoards");
	}

	@Test
	@DisplayName("buildMap: tiro fora dos limites cobre ramo isInside=false em buildMap")
	void printBothBoardsShotOutOfBoundsInBuildMap() {
		List<IPosition> shots = List.of(
				new Position(-1, -1),
				new Position(0, 1),
				new Position(0, 2)
		);
		game.fireShots(shots);
		assertEquals(1, game.getInvalidShots(), "Invalid shot should be counted");
		try { game.printBothBoards(true); }
		catch (java.awt.HeadlessException ignored) {
			// Esperado em ambiente CI sem display — comportamento correto
		}
		assertEquals(1, game.getInvalidShots(), "Invalid shots count should not change after printBothBoards");
	}

	@Test
	@DisplayName("printMyBoard: não lança exceção inesperada")
	void printMyBoardDoesNotThrowUnexpected() {
		game.randomEnemyFire();
		int movesBefore = game.getAlienMoves().size();
		try { game.printMyBoard(true, true); }
		catch (java.awt.HeadlessException ignored) {
			// Esperado em ambiente CI sem display — comportamento correto
		}
		assertEquals(movesBefore, game.getAlienMoves().size(), "AlienMoves should not change after printMyBoard");
	}

	@Test
	@DisplayName("printAlienBoard: não lança exceção inesperada")
	void printAlienBoardDoesNotThrowUnexpected() {
		game.randomPlayerFire();
		int movesBefore = game.getMyMoves().size();
		try { game.printAlienBoard(true, false); }
		catch (java.awt.HeadlessException ignored) {
			// Esperado em ambiente CI sem display — comportamento correto
		}
		assertEquals(movesBefore, game.getMyMoves().size(), "MyMoves should not change after printAlienBoard");
	}

	// -------------------------------------------------------
	// Constantes públicas
	// -------------------------------------------------------

	@Test
	@DisplayName("BOARD_SIZE é 10")
	void boardSizeConstant() {
		assertEquals(10, Game.BOARD_SIZE);
	}

	@Test
	@DisplayName("NUMBER_SHOTS é 3")
	void numberShotsConstant() {
		assertEquals(3, Game.NUMBER_SHOTS);
	}

	// -------------------------------------------------------
	// Métodos auxiliares
	// -------------------------------------------------------

	/** Devolve uma posição sem navio na frota indicada */
	private IPosition findWaterPosition(IFleet fleet) {
		for (int r = 0; r < Game.BOARD_SIZE; r++)
			for (int c = 0; c < Game.BOARD_SIZE; c++) {
				IPosition pos = new Position(r, c);
				if (fleet.shipAt(pos) == null) return pos;
			}
		throw new IllegalStateException("Não foi possível encontrar posição de água");
	}

	/** Constrói uma rajada de 3 tiros distintos começando em first */
	private List<IPosition> buildDistinctShots(IPosition first) {
		List<IPosition> shots = new ArrayList<>();
		shots.add(first);
		for (int r = 0; r < Game.BOARD_SIZE && shots.size() < Game.NUMBER_SHOTS; r++)
			for (int c = 0; c < Game.BOARD_SIZE && shots.size() < Game.NUMBER_SHOTS; c++) {
				IPosition pos = new Position(r, c);
				if (!shots.contains(pos)) shots.add(pos);
			}
		return shots;
	}
}