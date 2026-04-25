package battleship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.*;

public class Game implements IGame
{
	//------------------------------------------------------------------
	public static final int BOARD_SIZE = 10;
	public static final int NUMBER_SHOTS = 3;

	private static final char EMPTY_MARKER = '.';
	private static final char SHIP_MARKER = '#';
	private static final char SHOT_SHIP_MARKER = '*';
	private static final char SHOT_WATER_MARKER = 'o';
	private static final char SHIP_ADJACENT_MARKER = '-';

	// Refabricação 1: Introduce Constant (S1192)
	private static final String RAJADA_PREFIX = "rajada";

	//------------------------------------------------------------------
	private final IFleet myFleet;
	private final List<IMove> alienMoves;
	private final IFleet alienFleet;
	private final List<IMove> myMoves;

	private Integer countInvalidShots;
	private Integer countRepeatedShots;
	private Integer countHits;
	private Integer countSinks;
	private int moveNumber;

	//------------------------------------------------------------------
	public Game(IFleet myFleet)
	{
		this.moveNumber = 1;
		this.alienMoves = new ArrayList<>();
		this.myMoves = new ArrayList<>();
		this.alienFleet = Fleet.createRandom();
		this.myFleet = myFleet;
		this.countInvalidShots = 0;
		this.countRepeatedShots = 0;
		this.countHits = 0;
		this.countSinks = 0;
	}

	//------------------------------------------------------------------

	// Refabricação 6: Extract Method (S3776) — inicialização do mapa extraída de buildMap
	private static char[][] createEmptyMap() {
		char[][] map = new char[BOARD_SIZE][BOARD_SIZE];
		for (int r = 0; r < BOARD_SIZE; r++)
			for (int c = 0; c < BOARD_SIZE; c++)
				map[r][c] = EMPTY_MARKER;
		return map;
	}

	// Refabricação 6: Extract Method (S3776) — marcação de navios extraída de buildMap
	private static void markShipsOnMap(char[][] map, IFleet fleet) {
		for (IShip ship : fleet.getShips()) {
			for (IPosition shipPos : ship.getPositions())
				map[shipPos.getRow()][shipPos.getColumn()] = SHIP_MARKER;
			if (!ship.stillFloating()) {
				for (IPosition adjacentPos : ship.getAdjacentPositions())
					map[adjacentPos.getRow()][adjacentPos.getColumn()] = SHIP_ADJACENT_MARKER;
			}
		}
	}

	// Refabricação 6: Extract Method (S3776) — marcação de tiros extraída de buildMap
	private static void markShotsOnMap(char[][] map, List<IMove> moves) {
		for (IMove move : moves) {
			for (IPosition shot : move.getShots()) {
				if (shot.isInside()) {
					int row = shot.getRow();
					int col = shot.getColumn();
					if (map[row][col] == SHIP_MARKER)
						map[row][col] = SHOT_SHIP_MARKER;
					if (map[row][col] == EMPTY_MARKER || map[row][col] == SHIP_ADJACENT_MARKER)
						map[row][col] = SHOT_WATER_MARKER;
				}
			}
		}
	}

	// Refabricação 6: Extract Method (S3776) — ocultação de navios extraída de buildMap
	private static void hideShipsOnMap(char[][] map) {
		for (int r = 0; r < BOARD_SIZE; r++)
			for (int c = 0; c < BOARD_SIZE; c++)
				if (map[r][c] == SHIP_MARKER)
					map[r][c] = EMPTY_MARKER;
	}

	private static char[][] buildMap(IFleet fleet, List<IMove> moves, boolean showShots, boolean hideShips) {
		// Refabricação 7: Replace assert with proper check (S4274)
		if (fleet == null) throw new IllegalArgumentException("fleet cannot be null");
		if (moves == null) throw new IllegalArgumentException("moves cannot be null");

		// Refabricação 6: usa métodos extraídos
		char[][] map = createEmptyMap();
		markShipsOnMap(map, fleet);
		if (showShots) markShotsOnMap(map, moves);
		if (hideShips) hideShipsOnMap(map);
		return map;
	}

	/**
	 * Prints the game board.
	 *
	 * @param fleet      the fleet of ships to be displayed.
	 * @param moves      the list of moves containing shots.
	 * @param showShots  if true, displays the shots taken during the game.
	 * @param showLegend if true, displays an explanatory legend.
	 */
	// Refabricação 3: Rename (S117) — show_shots → showShots, show_legend → showLegend
	public static void printBoard(IFleet fleet, List<IMove> moves, boolean showShots, boolean showLegend) {
		// Refabricação 7: Replace assert with proper check (S4274)
		if (fleet == null) throw new IllegalArgumentException("fleet cannot be null");
		if (moves == null) throw new IllegalArgumentException("moves cannot be null");

		// Refabricação 6: usa métodos extraídos
		char[][] map = createEmptyMap();
		markShipsOnMap(map, fleet);
		if (showShots) markShotsOnMap(map, moves);

		System.out.println();
		System.out.print("    ");
		for (int col = 0; col < BOARD_SIZE; col++)
			System.out.print(" " + (col + 1));
		System.out.println();

		System.out.print("   +-");
		for (int col = 0; col < BOARD_SIZE; col++)
			System.out.print("--");
		System.out.println("+");

		for (int row = 0; row < BOARD_SIZE; row++) {
			Position pos = new Position(row, 0);
			char rowLabel = pos.getClassicRow();
			System.out.print(" " + rowLabel + " |");
			for (int col = 0; col < BOARD_SIZE; col++)
				System.out.print(" " + map[row][col]);
			System.out.println(" |");
		}

		System.out.print("   +");
		for (int col = 0; col < BOARD_SIZE; col++)
			System.out.print("--");
		System.out.println("-+");

		if (showLegend) {
			System.out.println("          LEGENDA");
			System.out.println("'" + SHIP_MARKER + "'->navio, '" + SHIP_ADJACENT_MARKER + "'->adjacente a navio, '" + EMPTY_MARKER + "'->água");
			System.out.println("'" + SHOT_SHIP_MARKER + "'->Tiro certeiro, '" + SHOT_WATER_MARKER + "'->Tiro na água");
		}
		System.out.println();
	}

	public void printBothBoards(boolean showShots) {
		char[][] myMap = buildMap(this.myFleet, this.alienMoves, showShots, false);
		char[][] alienMap = buildMap(this.alienFleet, this.myMoves, showShots, true);

		List<IPosition> lastMyShots = this.myMoves.isEmpty()
				? new ArrayList<>()
				: this.myMoves.get(this.myMoves.size() - 1).getShots();

		List<IPosition> lastAlienShots = this.alienMoves.isEmpty()
				? new ArrayList<>()
				: this.alienMoves.get(this.alienMoves.size() - 1).getShots();

		BoardWindow.show(myMap, alienMap, lastMyShots, lastAlienShots);
	}

	/**
	 * Serializes a list of shot positions into a JSON string.
	 *
	 * @param shots a list of shot positions to be serialized.
	 * @return a formatted JSON string containing the shot positions.
	 * @throws IllegalStateException if an error occurs during JSON serialization.
	 */
	public static String jsonShots(List<IPosition> shots) {
		// Refabricação 7: Replace assert with proper check (S4274)
		if (shots == null) throw new IllegalArgumentException("shots cannot be null");

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

		List<Map<String, Object>> simplifiedShots = new ArrayList<>();
		for (IPosition shot : shots) {
			Map<String, Object> simplePos = new LinkedHashMap<>();
			simplePos.put("row", String.valueOf(shot.getClassicRow()));
			simplePos.put("column", shot.getClassicColumn());
			simplifiedShots.add(simplePos);
		}

		try {
			return objectMapper.writeValueAsString(simplifiedShots);
		} catch (JsonProcessingException e) {
			// Refabricação 8: Replace generic exception (S112) — RuntimeException → IllegalStateException
			throw new IllegalStateException("Erro ao serializar o JSON", e);
		}
	}

	/**
	 * Converte um JSON com tiros numa lista de posições.
	 *
	 * @param json string JSON com tiros
	 * @return lista de posições a disparar
	 */
	private static List<IPosition> jsonToShots(String json) {
		// Refabricação 7: Replace assert with proper check (S4274)
		if (json == null) throw new IllegalArgumentException("json cannot be null");

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode root = objectMapper.readTree(json);
			JsonNode shotsNode = root;
			if (root.isObject() && root.has("shots"))
				shotsNode = root.get("shots");

			if (!shotsNode.isArray())
				throw new IllegalArgumentException("JSON INVALIDO : ENVIA O JSON TODO NUMA SÓ LINHA  (sem espacos em branco) ");

			List<IPosition> shots = new ArrayList<>();
			for (JsonNode shotNode : shotsNode) {
				String row = shotNode.get("row").asText();
				int column = shotNode.get("column").asInt();
				shots.add(new Position(row.charAt(0), column));
			}
			return shots;
		} catch (Exception e) {
			throw new IllegalArgumentException("JSON inválido: não foi possível ler os tiros.", e);
		}
	}

	// Refabricação 4: Extract Method (S3776) — lógica de geração de tiros duplicada
	/**
	 * Gera uma lista de tiros aleatórios a partir dos candidatos disponíveis.
	 *
	 * @param candidateShots posições disponíveis para disparar
	 * @param random         instância de Random para geração aleatória
	 * @return lista de tiros gerados
	 */
// Extrai o preenchimento aleatório de tiros (ramo if)
	private List<IPosition> fillShotsRandomly(List<IPosition> candidateShots, Random random) {
		List<IPosition> shots = new ArrayList<>();
		while (shots.size() < NUMBER_SHOTS) {
			IPosition newShot = candidateShots.get(random.nextInt(candidateShots.size()));
			if (!shots.contains(newShot))
				shots.add(newShot);
		}
		return shots;
	}

	// Extrai o preenchimento a partir dos candidatos disponíveis (ramo else)
	private List<IPosition> fillShotsFromCandidates(List<IPosition> candidateShots) {
		List<IPosition> shots = new ArrayList<>();
		for (IPosition pos : candidateShots) {
			if (!shots.contains(pos))
				shots.add(pos);
		}
		return shots;
	}

	// Extrai o padding quando há poucos candidatos
	private void padShots(List<IPosition> shots) {
		if (!shots.isEmpty()) {
			while (shots.size() < NUMBER_SHOTS)
				shots.add(shots.get(0));
		} else {
			IPosition fallback = new Position(0, 0);
			while (shots.size() < NUMBER_SHOTS)
				shots.add(fallback);
		}
	}

	// Refabricação 4: generateShots agora simples e com baixa complexidade
	private List<IPosition> generateShots(List<IPosition> candidateShots, Random random) {
		if (candidateShots.size() >= NUMBER_SHOTS)
			return fillShotsRandomly(candidateShots, random);

		List<IPosition> shots = fillShotsFromCandidates(candidateShots);
		padShots(shots);
		return shots;
	}

	// Refabricação 5: Extract Method (S3776) — parsing duplicado em readEnemyFire/readAlienFire
	/**
	 * Faz o parsing de uma string no formato clássico (ex: "A1 B2 C3") para uma lista de posições.
	 *
	 * @param input string com as posições no formato clássico
	 * @return lista de posições
	 */
	private List<IPosition> parseClassicShots(String input) {
		List<IPosition> shots = new ArrayList<>();
		try (Scanner inputScanner = new Scanner(input)) {
			while (shots.size() < NUMBER_SHOTS && inputScanner.hasNext()) {
				String token = inputScanner.next();
				if (token.matches("[A-Za-z]")) {
					if (inputScanner.hasNextInt()) {
						int row = inputScanner.nextInt();
						shots.add(new Position(token.toUpperCase().charAt(0), row));
					} else {
						throw new IllegalArgumentException("Posição incompleta! A coluna '" + token + "' não é seguida por uma linha.");
					}
				} else {
					try (Scanner singleScanner = new Scanner(token)) {
						shots.add(Tasks.readClassicPosition(singleScanner));
					}
				}
			}
		}
		return shots;
	}

	/**
	 * Simulates a random firing action by the enemy.
	 *
	 * @return A JSON string representing the list of randomly generated enemy shots.
	 */
	public String randomEnemyFire() {
		Random random = new Random(System.currentTimeMillis());

		// Refabricação 2: Replace Type with Diamond (S2293)
		Set<IPosition> usablePositions = new HashSet<>();
		for (int r = 0; r < BOARD_SIZE; r++)
			for (int c = 0; c < BOARD_SIZE; c++)
				usablePositions.add(new Position(r, c));

		this.myFleet.getSunkShips().forEach(ship -> usablePositions.removeAll(ship.getAdjacentPositions()));
		this.alienMoves.forEach(move -> usablePositions.removeAll(move.getShots()));

		// Refabricação 4: usa generateShots()
		List<IPosition> shots = generateShots(new ArrayList<>(usablePositions), random);

		System.out.println();
		System.out.print(RAJADA_PREFIX + " ");
		for (IPosition shot : shots)
			System.out.print(shot + " ");
		System.out.println();

		this.fireShots(shots);
		return Game.jsonShots(shots);
	}

	/**
	 * Simula uma rajada aleatória do jogador contra a frota inimiga.
	 *
	 * @return JSON com os tiros gerados pelo jogador.
	 */
	public String randomPlayerFire() {
		Random random = new Random(System.currentTimeMillis());

		// Refabricação 2: Replace Type with Diamond (S2293)
		Set<IPosition> usablePositions = new HashSet<>();
		for (int r = 0; r < BOARD_SIZE; r++)
			for (int c = 0; c < BOARD_SIZE; c++)
				usablePositions.add(new Position(r, c));

		this.alienFleet.getSunkShips().forEach(ship -> usablePositions.removeAll(ship.getAdjacentPositions()));
		this.myMoves.forEach(move -> usablePositions.removeAll(move.getShots()));

		// Refabricação 4: usa generateShots()
		List<IPosition> shots = generateShots(new ArrayList<>(usablePositions), random);

		System.out.println();
		System.out.print(RAJADA_PREFIX + " ");
		for (IPosition shot : shots)
			System.out.print(shot + " ");
		System.out.println();

		this.fireShotsAtAlien(shots);
		return Game.jsonShots(shots);
	}

	/**
	 * Lê uma rajada do jogador e aplica os tiros à frota inimiga.
	 *
	 * @param in scanner usado para ler as posições da rajada.
	 * @throws IllegalArgumentException se as posições forem inválidas ou o número errado.
	 */
	public String readEnemyFire(Scanner in) {
		// Refabricação 7: Replace assert with proper check (S4274)
		if (in == null) throw new IllegalArgumentException("scanner cannot be null");

		String input = in.nextLine().trim();
		// Refabricação 1: Introduce Constant (S1192)
		if (input.toLowerCase().startsWith(RAJADA_PREFIX))
			input = input.substring(RAJADA_PREFIX.length()).trim();
		while (input.isEmpty() && in.hasNextLine())
			input = in.nextLine().trim();

		List<IPosition> shots;

		if (input.startsWith("{") || input.startsWith("[")) {
			shots = jsonToShots(input);
			if (shots.size() != NUMBER_SHOTS)
				throw new IllegalArgumentException("Deves inserir exatamente " + NUMBER_SHOTS + " posicoes");
			this.fireShotsAtAlien(shots);
			return Game.jsonShots(shots);
		}

		// Refabricação 5: usa parseClassicShots()
		shots = parseClassicShots(input);

		if (shots.size() != NUMBER_SHOTS)
			throw new IllegalArgumentException("Você deve inserir exatamente " + NUMBER_SHOTS + " posições!");

		this.fireShotsAtAlien(shots);
		return Game.jsonShots(shots);
	}

	/**
	 * Lê uma rajada do inimigo e aplica os tiros à nossa frota.
	 *
	 * @param in scanner usado para ler as posições da rajada
	 * @return JSON com os tiros do inimigo
	 */
	public String readAlienFire(Scanner in) {
		// Refabricação 7: Replace assert with proper check (S4274)
		if (in == null) throw new IllegalArgumentException("scanner cannot be null");

		String input = in.nextLine().trim();
		// Refabricação 1: Introduce Constant (S1192)
		if (input.toLowerCase().startsWith(RAJADA_PREFIX))
			input = input.substring(RAJADA_PREFIX.length()).trim();
		while (input.isEmpty() && in.hasNextLine())
			input = in.nextLine().trim();

		List<IPosition> shots;

		if (input.startsWith("{") || input.startsWith("[")) {
			shots = jsonToShots(input);
			if (shots.size() != NUMBER_SHOTS)
				throw new IllegalArgumentException("Deves inserir exatamente " + NUMBER_SHOTS + " posicoes");
			this.fireShots(shots);
			return Game.jsonShots(shots);
		}

		// Refabricação 5: usa parseClassicShots()
		shots = parseClassicShots(input);

		if (shots.size() != NUMBER_SHOTS)
			throw new IllegalArgumentException("Você deve inserir exatamente " + NUMBER_SHOTS + " posições!");

		this.fireShots(shots);
		return Game.jsonShots(shots);
	}

	/**
	 * Fires a set of shots during a player's move.
	 *
	 * @param shots a list of positions to fire shots at.
	 * @throws IllegalArgumentException if the number of shots is not exactly NUMBER_SHOTS.
	 */
	public void fireShots(List<IPosition> shots)
	{
		// Refabricação 7: Replace assert with proper check (S4274)
		if (shots == null) throw new IllegalArgumentException("shots cannot be null");

		// Refabricação 2: Replace Type with Diamond (S2293)
		List<ShotResult> shotResults = new ArrayList<>();
		if (shots.size() != NUMBER_SHOTS)
			throw new IllegalArgumentException("Must fire exactly " + NUMBER_SHOTS + " shots per move.");

		List<IPosition> alreadyShot = new ArrayList<>();
		for (IPosition pos : shots) {
			shotResults.add(fireSingleShot(pos, alreadyShot.contains(pos)));
			alreadyShot.add(pos);
		}

		Move move = new Move(moveNumber, shots, shotResults);
		move.processEnemyFire(true);
		alienMoves.add(move);
		moveNumber++;
	}

	/**
	 * Dispara uma rajada do jogador contra a frota inimiga.
	 *
	 * @param shots lista de posições a atingir na frota inimiga
	 */
	private void fireShotsAtAlien(List<IPosition> shots)
	{
		// Refabricação 7: Replace assert with proper check (S4274)
		if (shots == null) throw new IllegalArgumentException("shots cannot be null");

		// Refabricação 2: Replace Type with Diamond (S2293)
		List<ShotResult> shotResults = new ArrayList<>();
		if (shots.size() != NUMBER_SHOTS)
			throw new IllegalArgumentException("Must fire exactly " + NUMBER_SHOTS + " shots per move.");

		List<IPosition> alreadyShot = new ArrayList<>();
		for (IPosition pos : shots) {
			shotResults.add(fireSingleShotAtAlien(pos, alreadyShot.contains(pos)));
			alreadyShot.add(pos);
		}

		Move move = new Move(moveNumber, shots, shotResults);
		move.processEnemyFire(true);
		myMoves.add(move);
		moveNumber++;
	}

	/**
	 * Fires a single shot at the specified position.
	 *
	 * @param pos        the position to fire the shot at.
	 * @param isRepeated true if the shot is marked as a repeat attempt.
	 * @return a ShotResult object containing the result of the shot.
	 */
	public ShotResult fireSingleShot(IPosition pos, boolean isRepeated) {
		// Refabricação 7: Replace assert with proper check (S4274)
		if (pos == null) throw new IllegalArgumentException("pos cannot be null");

		if (!pos.isInside()) {
			countInvalidShots++;
			return new ShotResult(false, false, null, false);
		}

		if (isRepeated || repeatedShot(pos)) {
			countRepeatedShots++;
			return new ShotResult(true, true, null, false);
		}

		IShip ship = myFleet.shipAt(pos);
		if (ship == null)
			return new ShotResult(true, false, null, false);
		else {
			ship.shoot(pos);
			countHits++;
			if (!ship.stillFloating())
				countSinks++;
			return new ShotResult(true, false, ship, !ship.stillFloating());
		}
	}

	/**
	 * Resolve um tiro do jogador sobre a frota inimiga.
	 *
	 * @param pos        posição a atingir
	 * @param isRepeated indica se o tiro já aparece na mesma rajada
	 * @return resultado do tiro
	 */
	private ShotResult fireSingleShotAtAlien(IPosition pos, boolean isRepeated) {
		// Refabricação 7: Replace assert with proper check (S4274)
		if (pos == null) throw new IllegalArgumentException("pos cannot be null");

		if (!pos.isInside())
			return new ShotResult(false, false, null, false);

		if (isRepeated || repeatedShotAtAlien(pos))
			return new ShotResult(true, true, null, false);

		IShip ship = alienFleet.shipAt(pos);
		if (ship == null)
			return new ShotResult(true, false, null, false);

		ship.shoot(pos);
		return new ShotResult(true, false, ship, !ship.stillFloating());
	}

	@Override
	public IFleet getMyFleet() { return myFleet; }

	@Override
	public List<IMove> getAlienMoves() { return alienMoves; }

	@Override
	public IFleet getAlienFleet() { return alienFleet; }

	@Override
	public List<IMove> getMyMoves() { return myMoves; }

	@Override
	public int getRepeatedShots() { return this.countRepeatedShots; }

	@Override
	public int getInvalidShots() { return this.countInvalidShots; }

	@Override
	public int getHits() { return this.countHits; }

	@Override
	public int getSunkShips() { return this.countSinks; }

	@Override
	public int getRemainingShips() {
		List<IShip> floatingShips = myFleet.getFloatingShips();
		return floatingShips.size();
	}

	public boolean repeatedShot(IPosition pos)
	{
		if (pos == null) throw new IllegalArgumentException("pos cannot be null");
		for (IMove move : alienMoves)
			if (move.getShots().contains(pos))
				return true;
		return false;
	}

	/**
	 * Verifica se uma posição já foi usada em jogadas do jogador.
	 *
	 * @param pos posição a verificar
	 * @return true se a posição já foi usada, false caso contrário
	 */
	private boolean repeatedShotAtAlien(IPosition pos)
	{
		if (pos == null) throw new IllegalArgumentException("pos cannot be null");
		for (IMove move : myMoves)
			if (move.getShots().contains(pos))
				return true;
		return false;
	}

	// Refabricação 3: Rename (S117) — show_shots → showShots, show_legend → showLegend
	public void printMyBoard(boolean showShots, boolean showLegend)
	{
		System.out.println("=== MINHA FROTA ===");
		Game.printBoard(this.myFleet, this.alienMoves, showShots, showLegend);
		printBothBoards(showShots);
		System.out.println("=== === ===");
	}

	// Refabricação 3: Rename (S117) — show_shots → showShots, show_legend → showLegend
	public void printAlienBoard(boolean showShots, boolean showLegend)
	{
		System.out.println("=== FROTA INIMIGA ===");
		Game.printBoard(this.alienFleet, this.myMoves, showShots, showLegend);
		printBothBoards(showShots);
		System.out.println("=== === ===");
	}

	public void over() {
		System.out.println();
		System.out.println("+--------------------------------------------------------------+");
		System.out.println("| Maldito sejas, Java Sparrow, eu voltarei, glub glub glub ... |");
		System.out.println("+--------------------------------------------------------------+");
		BoardWindow.close();
	}
}