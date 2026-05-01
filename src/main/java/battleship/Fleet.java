/**
 * 
 */
package battleship;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Fleet.
 */
public class Fleet implements IFleet
{
	// Extração do Magic Array para uma constante
	private static final String[] DEFAULT_SHIP_TYPES = {
			"galeao",                           // 1 galleon
			"fragata",                          // 1 frigate
			"nau", "nau",                       // 2 carracks
			"caravela", "caravela", "caravela", // 3 caravels
			"barca", "barca", "barca", "barca"  // 4 barges
	};

	/**
	 * Creates a randomly generated fleet containing ships of various predefined types.
	 * Each ship is assigned a random bearing and position. If a ship cannot be added
	 * due to constraints (e.g., collision or boundary issues), it will be retried.
	 *
	 * @return a fully constructed and valid fleet as an instance of IFleet
	 */
	public static IFleet createRandom() {

		Fleet randomFleet = new Fleet();
		int fleetSize = 0;

		while (fleetSize < DEFAULT_SHIP_TYPES.length) {

			// Build the ship
			Ship ship = Ship.buildShip(DEFAULT_SHIP_TYPES[fleetSize], Compass.randomBearing(), Position.randomPosition());

			// Attempt to add the ship to the fleet
			if (ship != null && randomFleet.addShip(ship)) {
				fleetSize++; // Increment count if ship is successfully added
			}
		}
		return randomFleet;
	}


    // -----------------------------------------------------

	/**
	 * The Ships.
	 */
	private final List<IShip> ships;

	// -----------------------------------------------------ge
	/**
	 * Instantiates a new Fleet.
	 */
	public Fleet()
    {
	ships = new ArrayList<>();
    }

	/**
	 * Gets ships.
	 *
	 * @return the ships
	 */
	@Override
    public List<IShip> getShips()
    {
	return ships;
    }
	
	/**
	 * Add ship boolean.
	 *
	 * @param s the s
	 * @return the boolean
	 */
    @Override
    public boolean addShip(IShip s)
    {
		if (s == null) {
			throw new IllegalArgumentException("Ship cannot be null");
		}

		boolean result = false;
		if ((ships.size() <= FLEET_SIZE) && (isInsideBoard(s)) && (!colisionRisk(s)))
		{
			ships.add(s);
			result = true;
		}
		return result;
    }

	/**
	 * Gets ships like.
	 *
	 * @param category the category
	 * @return the ships like
	 */
    @Override
    public List<IShip> getShipsLike(String category)
    {
		if (category == null) {
			throw new IllegalArgumentException("Category cannot be null");
		}

		List<IShip> shipsLike = new ArrayList<>();
		for (IShip s : ships)
			if (s.getCategory().equals(category))
				shipsLike.add(s);

		return shipsLike;
    }

	/**
	 * Gets floating ships.
	 *
	 * @return the floating ships
	 */
    @Override
    public List<IShip> getFloatingShips()
    {
		List<IShip> floatingShips = new ArrayList<>();
		for (IShip s : ships)
			if (s.stillFloating())
				floatingShips.add(s);

		return floatingShips;
    }

	/**
	 * Gets sunk ships.
	 *
	 * @return the sunk ships
	 */
	@Override
	public List<IShip> getSunkShips()
	{
		List<IShip> sunkShips = new ArrayList<>();
		for (IShip s : ships)
			if (!s.stillFloating())
				sunkShips.add(s);

		return sunkShips;
	}

	/**
	 * Ship at ship.
	 *
	 * @param pos the pos
	 * @return the ship
	 */
    @Override
    public IShip shipAt(IPosition pos)
    {
		if (pos == null) {
			throw new IllegalArgumentException("Position cannot be null");
		}

		for (IShip ship : ships)
			if (ship.occupies(pos))
				return ship;
		return null;
    }

	/**
	 * Is inside board boolean.
	 *
	 * @param s the s
	 * @return the boolean
	 */
	private boolean isInsideBoard(IShip s)
    {
		if (s == null) {
			throw new IllegalArgumentException("Ship cannot be null");
		}

		return (s.getLeftMostPos() >= 0 && s.getRightMostPos() <= Game.BOARD_SIZE - 1 && s.getTopMostPos() >= 0
			&& s.getBottomMostPos() <= Game.BOARD_SIZE - 1);
    }

	/**
	 * Colision risk boolean.
	 *
	 * @param s the s
	 * @return the boolean
	 */
	private boolean colisionRisk(IShip s)
    {
		if (s == null) {
			throw new IllegalArgumentException("Ship cannot be null");
		}

		for (IShip ship : ships) {
			if (ship.tooCloseTo(s)) {
				return true;
			}
		}
		return false;
    }

	/**
	 * This operation prints all the given ships
	 *
	 * @param ships The list of ships
	 */
	public void printShips(List<IShip> ships)
	{
		if (ships == null) {
			throw new IllegalArgumentException("List of ships cannot be null");
		}

		for (IShip ship : ships)
			System.out.println(ship);
	}

	/**
	 * This operation shows the state of a fleet
	 */
	public void printStatus()
    {
		System.out.println("Estado da Frota: " + this.getFloatingShips().size() + " a flutuar, " + this.getSunkShips().size() + " afundados!");
    }

	/**
	 * This operation prints all the ships of a fleet belonging to a particular
	 * category
	 *
	 * @param category The category of ships of interest
	 */
	public void printShipsByCategory(String category)
    {
		if (category == null) {
			throw new IllegalArgumentException("Category cannot be null");
		}

		printShips(getShipsLike(category));
    }

	/**
	 * This operation prints all the ships of a fleet but not yet shot
	 */
	public void printFloatingShips()
    {
	printShips(getFloatingShips());
    }

	/**
	 * This operation prints all the ships of a fleet
	 */
	void printAllShips()
    {
		printShips(ships);
    }
}