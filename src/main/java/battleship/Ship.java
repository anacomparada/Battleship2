package battleship;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * The type Ship.
 */
public abstract class Ship implements IShip {

	private static final String GALEAO = "galeao";
	private static final String FRAGATA = "fragata";
	private static final String NAU = "nau";
	private static final String CARAVELA = "caravela";
	private static final String BARCA = "barca";

	// ✅ Constante criada para evitar duplicação (S1192)
	private static final String POSITION_MUST_NOT_BE_NULL = "Position must not be null";

	/**
	 * Create a new ship.
	 *
	 * @param shipKind the ship kind
	 * @param bearing  the bearing
	 * @param pos      the pos
	 * @return s ship
	 */
	static Ship buildShip(String shipKind, Compass bearing, Position pos) {
		Objects.requireNonNull(shipKind, "Ship kind must not be null");
		Objects.requireNonNull(bearing, "Ship bearing must not be null");
		Objects.requireNonNull(pos, "Ship position must not be null");

		Ship s;
		switch (shipKind) {
			case BARCA:
				s = new Barge(bearing, pos);
				break;
			case CARAVELA:
				s = new Caravel(bearing, pos);
				break;
			case NAU:
				s = new Carrack(bearing, pos);
				break;
			case FRAGATA:
				s = new Frigate(bearing, pos);
				break;
			case GALEAO:
				s = new Galleon(bearing, pos);
				break;
			default:
				s = null;
		}
		return s;
	}

	//---------------------------------------------------------

	private String category;
	private Compass bearing;
	private IPosition pos;
	private Integer size;
	protected List<IPosition> positions;

	protected Ship(String category, Compass bearing, IPosition pos, int size) {
		this.category = Objects.requireNonNull(category, "Ship's category must not be null");
		this.bearing = Objects.requireNonNull(bearing, "Ship's bearing must not be null");
		this.pos = Objects.requireNonNull(pos, "Ship's position must not be null");
		this.size = size;

		positions = new ArrayList<>();
	}

	@Override
	public String getCategory() {
		return category;
	}

	public List<IPosition> getPositions() {
		return positions;
	}

	public List<IPosition> getAdjacentPositions() {
		List<IPosition> adjacentPositions = new ArrayList<>();

		for (IPosition position : getPositions()) {
			List<IPosition> adjacents = position.adjacentPositions();

			for (IPosition adj : adjacents) {
				if (!getPositions().contains(adj) && !adjacentPositions.contains(adj)) {
					adjacentPositions.add(adj);
				}
			}
		}

		return adjacentPositions;
	}

	@Override
	public IPosition getPosition() {
		return pos;
	}

	@Override
	public Compass getBearing() {
		return bearing;
	}

	public Integer getSize() {
		return size;
	}

	@Override
	public boolean stillFloating() {
		for (int i = 0; i < getSize(); i++) {
			if (!getPositions().get(i).isHit()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getTopMostPos() {
		int top = getPositions().get(0).getRow();

		for (int i = 1; i < getSize(); i++) {
			if (getPositions().get(i).getRow() < top) {
				top = getPositions().get(i).getRow();
			}
		}

		return top;
	}

	@Override
	public int getBottomMostPos() {
		int bottom = getPositions().get(0).getRow();

		for (int i = 1; i < getSize(); i++) {
			if (getPositions().get(i).getRow() > bottom) {
				bottom = getPositions().get(i).getRow();
			}
		}

		return bottom;
	}

	@Override
	public int getLeftMostPos() {
		int left = getPositions().get(0).getColumn();

		for (int i = 1; i < getSize(); i++) {
			if (getPositions().get(i).getColumn() < left) {
				left = getPositions().get(i).getColumn();
			}
		}

		return left;
	}

	@Override
	public int getRightMostPos() {
		int right = getPositions().get(0).getColumn();

		for (int i = 1; i < getSize(); i++) {
			if (getPositions().get(i).getColumn() > right) {
				right = getPositions().get(i).getColumn();
			}
		}

		return right;
	}

	@Override
	public boolean occupies(IPosition pos) {
		Objects.requireNonNull(pos, POSITION_MUST_NOT_BE_NULL);

		for (int i = 0; i < getSize(); i++) {
			if (getPositions().get(i).equals(pos)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean tooCloseTo(IShip other) {
		Objects.requireNonNull(other, "Ship must not be null");

		Iterator<IPosition> otherPos = other.getPositions().iterator();

		while (otherPos.hasNext()) {
			if (tooCloseTo(otherPos.next())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean tooCloseTo(IPosition pos) {
		Objects.requireNonNull(pos, POSITION_MUST_NOT_BE_NULL);

		for (int i = 0; i < this.getSize(); i++) {
			if (getPositions().get(i).isAdjacentTo(pos)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void shoot(IPosition pos) {
		Objects.requireNonNull(pos, POSITION_MUST_NOT_BE_NULL);

		if (!pos.isInside()) {
			throw new IllegalArgumentException("Position must be inside the board");
		}

		for (IPosition position : getPositions()) {
			if (position.equals(pos)) {
				position.shoot();
			}
		}
	}

	@Override
	public void sink() {
		for (IPosition position : getPositions()) {
			position.shoot();
		}
	}

	@Override
	public String toString() {
		return "[" + category + " " + bearing + " " + pos + "]";
	}
}