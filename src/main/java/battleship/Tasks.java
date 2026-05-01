package battleship;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * The type Tasks.
 */
public class Tasks {

    private Tasks() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * The constant GOODBYE_MESSAGE.
     */
    private static final String GOODBYE_MESSAGE = "Bons ventos!";

    /**
     * Strings to be used by the user
     */
    private static final String AJUDA = "ajuda";
    private static final String GERAFROTA = "gerafrota";
    private static final String LEFROTA = "lefrota";
    private static final String DESISTIR = "desisto";
    private static final String RAJADA = "rajada";
    private static final String TIROS = "tiros";
    private static final String MAPA = "mapa";
    private static final String STATUS = "estado";
    private static final String SIMULA = "simula";
    private static final String GERAREPORT = "gerareport";
    private static final String RANKING    = "ranking";   // NEW: show scoreboard
    private static final String NOME       = "nome";      // NEW: set player name

    /** Default player name if none is set. */
    private static String playerName = "Jogador";

    /**
     * Classe auxiliar interna para guardar o estado da sessão de jogo e reduzir
     * a complexidade cognitiva do método menu() (Refabricação do Brain Method)
     */
    private static class GameSession {
        IFleet myFleet = null;
        Game game = null;
        Game lastGame = null;
    }

    /**
     * Asks the player to type their name at the start of the session.
     * The prompt is repeated until a non-blank name is entered.
     * After a valid name is set, a welcome message is printed.
     *
     * @param in the active {@link Scanner}
     */
    public static void askPlayerName(Scanner in) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   ⚓  Bem-vindo ao Battleship!  ⚓   ║");
        System.out.println("╚══════════════════════════════════════╝");
        String nome = "";
        while (nome.isEmpty()) {
            System.out.print("Introduz o teu nome de navegador: ");
            if (in.hasNextLine()) nome = in.nextLine().trim();
        }
        playerName = nome;
        System.out.println("\nBoas vindas, Capitão " + playerName + "! Que a fortuna te guie.\n");
    }

    /**
     * Main menu loop.
     */
    public static void menu() {
        GameSession session = new GameSession();
        Scanner in = new Scanner(System.in);
        askPlayerName(in);
        menuHelp();
        System.out.print("> ");
        String command = in.next();
        while (!command.equals(DESISTIR)) {
            processCommand(command, session, in);
            System.out.print("> ");
            command = in.next();
        }
        System.out.println(GOODBYE_MESSAGE);
    }

    /**
     * Processa comandos isolados. Extraído para reduzir a Complexidade Cognitiva.
     */
    private static void processCommand(String command, GameSession session, Scanner in) {
        switch (command) {
            case GERAFROTA:
                session.myFleet = Fleet.createRandom();
                session.game = new Game(session.myFleet);
                session.game.printMyBoard(false, true);
                break;
            case LEFROTA:
                session.myFleet = buildFleet(in);
                session.game = new Game(session.myFleet);
                session.game.printMyBoard(false, true);
                break;
            case STATUS:
                if (session.myFleet != null) session.myFleet.printStatus();
                break;
            case MAPA:
                if (session.game != null) session.game.printMyBoard(false, true);
                break;
            case RAJADA:
                handleRajada(session, in);
                break;
            case SIMULA:
                handleSimula(session);
                break;
            case TIROS:
                if (session.game != null) session.game.printMyBoard(true, true);
                break;
            case RANKING:
                Scoreboard.interactiveDisplay(in);
                break;
            case NOME:
                handleNome(in);
                break;
            case GERAREPORT:
                handleReport(session);
                break;
            case AJUDA:
                menuHelp();
                break;
            default:
                System.out.println("Que comando é esse??? Repete ...");
        }
    }

    private static void handleNome(Scanner in) {
        System.out.print("Novo nome de navegador: ");
        in.nextLine(); // consume leftover newline after in.next()
        String nome = in.hasNextLine() ? in.nextLine().trim() : "";
        if (!nome.isEmpty()) {
            playerName = nome;
            System.out.println("Nome actualizado para: Capitão " + playerName);
        } else {
            System.out.println("Nome não alterado (continua: " + playerName + ")");
        }
    }

    private static void handleReport(GameSession session) {
        Game reportGame = (session.game != null) ? session.game : session.lastGame;
        if (reportGame != null) {
            PdfExporter.exportGameReport(reportGame, "report.pdf");
        } else {
            System.out.println("O jogo ainda não começou. Usa o comando 'gerafrota' primeiro.");
        }
    }

    private static void handleRajada(GameSession session, Scanner in) {
        // --- CORREÇÃO SONARQUBE: Validação para NullPointerException (Game e Fleet) ---
        if (session.game == null || session.myFleet == null) return;

        session.game.readEnemyFire(in);
        session.game.printAlienBoard(true, false);

        if (session.game.getAlienFleet().getFloatingShips().isEmpty()) {
            session.game.over();
            Scoreboard.saveResult(new GameResult(playerName, session.game.getMyMoves().size(), true));
            System.out.println("Podes usar 'gerareport' ou 'ranking' antes de saíres.");
            session.lastGame = session.game;
            session.game = null;
            session.myFleet = null;
            return;
        }

        System.out.print("> rajada inimiga ");
        session.game.readAlienFire(in);
        session.myFleet.printStatus();
        session.game.printMyBoard(true, false);

        if (session.game.getRemainingShips() == 0) {
            session.game.over();
            Scoreboard.saveResult(new GameResult(playerName, session.game.getMyMoves().size(), false));
            System.out.println("Podes usar 'gerareport' ou 'ranking' antes de saíres.");
            session.lastGame = session.game;
            session.game = null;
            session.myFleet = null;
        }
    }

    private static void handleSimula(GameSession session) {
        // --- CORREÇÃO SONARQUBE: Validação para NullPointerException (Game e Fleet) ---
        if (session.game == null || session.myFleet == null) return;

        while (session.game.getRemainingShips() > 0 && !session.game.getAlienFleet().getFloatingShips().isEmpty()) {
            session.game.randomPlayerFire();
            session.game.printAlienBoard(true, false);

            if (session.game.getAlienFleet().getFloatingShips().isEmpty()) break;

            session.game.randomEnemyFire();
            session.myFleet.printStatus();
            session.game.printMyBoard(true, false);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        boolean won = session.game.getAlienFleet().getFloatingShips().isEmpty();
        session.game.over();
        Scoreboard.saveResult(new GameResult(playerName, session.game.getMyMoves().size(), won));
        System.out.println("Podes usar 'gerareport' ou 'ranking' antes de saíres.");
        session.lastGame = session.game;
        session.game = null;
        session.myFleet = null;
    }

    /**
     * This function provides help information about the menu commands.
     */
    public static void menuHelp() {
        System.out.println("======================= AJUDA DO MENU =========================");
        System.out.println("Digite um dos comandos abaixo para interagir com o jogo:");
        System.out.println("- " + NOME      + ":      Define o teu nome de jogador.");
        System.out.println("- " + GERAFROTA + ": Gera uma frota aleatória de navios.");
        System.out.println("- " + LEFROTA   + ":   Permite criar e carregar uma frota personalizada.");
        System.out.println("- " + STATUS    + ":    Mostra o status atual da frota.");
        System.out.println("- " + MAPA      + ":      Exibe o mapa da frota.");
        System.out.println("- " + RAJADA    + ":    Realiza uma rajada de disparos.");
        System.out.println("- " + SIMULA    + ":    Simula um jogo completo.");
        System.out.println("- " + TIROS     + ":     Lista os tiros válidos realizados.");
        System.out.println("- " + RANKING   + ":   Mostra o Hall of Fame.");
        System.out.println("- " + GERAREPORT + ": Exporta o histórico de jogadas para um ficheiro PDF.");
        System.out.println("- " + DESISTIR  + ":    Encerra o jogo.");
        System.out.println("===============================================================");
    }

    /**
     * This operation allows the build up of a fleet, given user data
     *
     * @param in The scanner to read from
     * @return The fleet that has been built
     */
    public static Fleet buildFleet(Scanner in) {
        if (in == null) throw new IllegalArgumentException("Scanner cannot be null");

        Fleet fleet = new Fleet();
        int i = 0;
        while (i < IFleet.FLEET_SIZE) {
            IShip s = readShip(in);
            if (s != null) {
                boolean success = fleet.addShip(s);
                if (success)
                    i++;
                else
                    LOGGER.info("Falha na criacao de {} {} {}", s.getCategory(), s.getBearing(), s.getPosition());
            } else {
                LOGGER.info("Navio desconhecido!");
            }
        }
        LOGGER.info("{} navios adicionados com sucesso!", i);
        return fleet;
    }

    /**
     * This operation reads data about a ship, build it and returns it
     *
     * @param in The scanner to read from
     * @return The created ship based on the data that has been read
     */
    public static Ship readShip(Scanner in) {
        if (in == null) throw new IllegalArgumentException("Scanner cannot be null");

        String shipKind = in.next();
        Position pos    = readPosition(in);
        char c          = in.next().charAt(0);
        Compass bearing = Compass.charToCompass(c);
        return Ship.buildShip(shipKind, bearing, pos);
    }

    /**
     * This operation allows reading a position in the map
     *
     * @param in The scanner to read from
     * @return The position that has been read
     */
    public static Position readPosition(Scanner in) {
        if (in == null) throw new IllegalArgumentException("Scanner cannot be null");

        int row    = in.nextInt();
        int column = in.nextInt();
        return new Position(row, column);
    }

    /**
     * This operation allows reading a classic position (e.g. A3) from the map
     *
     * @param in The scanner to read from
     * @return The classic position that has been read
     */
    public static IPosition readClassicPosition(@NotNull Scanner in) {
        if (!in.hasNext())
            throw new IllegalArgumentException("Nenhuma posição válida encontrada!");

        String part1 = in.next();
        String part2 = null;

        if (in.hasNextInt()) {
            part2 = in.next();
        }

        String input = (part2 != null) ? part1 + part2 : part1;
        input = input.toUpperCase();

        if (input.matches("[A-Z]\\d+")) {
            char column = input.charAt(0);
            int  row    = Integer.parseInt(input.substring(1));
            return new Position(column, row);
        }
        else if (part2 != null && part1.matches("[A-Z]") && part2.matches("\\d+")) {
            char column = part1.charAt(0);
            int  row    = Integer.parseInt(part2);
            return new Position(column, row);
        } else {
            throw new IllegalArgumentException("Formato inválido. Use 'A3', 'A 3' ou similar.");
        }
    }
}