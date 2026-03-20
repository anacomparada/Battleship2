package battleship;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Represents the result of a single Battleship game.
 * Stores the player name, number of moves taken, whether the player won,
 * and the date/time the game ended.
 */
public class GameResult {

    private String playerName;
    private int totalMoves;
    private boolean playerWon;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    /** Default constructor required by Jackson for JSON deserialisation. */
    public GameResult() {
    }

    /**
     * Creates a GameResult and stamps it with the current date/time.
     *
     * @param playerName name of the human player
     * @param totalMoves total number of move rounds played
     * @param playerWon  true if the player sank all enemy ships first
     */
    public GameResult(String playerName, int totalMoves, boolean playerWon) {
        this.playerName = playerName;
        this.totalMoves = totalMoves;
        this.playerWon = playerWon;
        this.date = LocalDateTime.now();
    }

    // ── Getters & setters ────────────────────────────────────────────────────

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public int getTotalMoves() { return totalMoves; }
    public void setTotalMoves(int totalMoves) { this.totalMoves = totalMoves; }

    public boolean isPlayerWon() { return playerWon; }
    public void setPlayerWon(boolean playerWon) { this.playerWon = playerWon; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
