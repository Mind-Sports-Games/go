package com.joansala.game.go;

/*
 * Aalina engine.
 * Copyright (c) 2021 Joan Sala Soler <contact@joansala.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.StringJoiner;
import com.joansala.engine.base.BaseBoard;
import com.joansala.util.bits.Bitset;
import com.joansala.util.bits.BitsetConverter;
import com.joansala.util.notation.CoordinateConverter;
import com.joansala.util.notation.DiagramConverter;
import static com.joansala.game.go.Go.*;
import static com.joansala.game.go.GoGame.*;

/**
 * Represents a othello board.
 */
public class GoBoard extends BaseBoard<Bitset[]> {

	/** Bitboard converter */
	private BitsetConverter bitset;

	/** Algebraic coordinates converter */
	private CoordinateConverter algebraic;

	/** Piece placement converter */
	private static DiagramConverter fen;

	/** Ko point for current state */
	private int kopoint = -1;

	/** the go game size 9,13 or 19 */
	private int gameSize = 19;

	/**
	 * Initialize notation converters.
	 */
	static {
		fen = new DiagramConverter(PIECES);
	}

	/**
	 * Creates a new board for the default start position and game size.
	 */
	public GoBoard() {
		this(START_POSITION, SOUTH, DEFAULT_GAME_SIZE);
	}

	/**
	 * Creates a new board for the start position for the game size.
	 */
	public GoBoard(int gameSize) {
		this(START_POSITION, SOUTH, gameSize);
	}

	/**
	 * Creates a new board instance.
	 *
	 * @param position Position array
	 * @param turn     Player to move
	 */
	public GoBoard(Bitset[] position, int turn, int gameSize) {
		super(clone(position), turn);
		this.gameSize = gameSize;
		switch (gameSize) {
		case 9:
			algebraic = new CoordinateConverter(COORDINATES_9);
			bitset = new BitsetConverter(BITS_9);
			break;
		case 13:
			algebraic = new CoordinateConverter(COORDINATES_13);
			bitset = new BitsetConverter(BITS_13);
			break;
		default:
			algebraic = new CoordinateConverter(COORDINATES_19);
			bitset = new BitsetConverter(BITS_19);
		}
	}

	/**
	 * Creates a new board instance.
	 *
	 * @param position Position array
	 * @param turn     Player to move
	 * @param kopoint  Forbbiden intersection
	 * @param gameSize Game size 9,13,19
	 */
	public GoBoard(Bitset[] position, int turn, int kopoint, int gameSize) {
		this(position, turn, gameSize);
		this.kopoint = kopoint;
		this.gameSize = gameSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bitset[] position() {
		return clone(position);
	}

	/**
	 * Forbbiden move on current turn.
	 */
	public int kopoint() {
		return kopoint;
	}

	/**
	 * Game size of board.
	 */
	public int gameSize() {
		return gameSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int toMove(String notation) {
		return algebraic.toIndex(notation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toCoordinates(int move) {
		return algebraic.toCoordinate(move);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GoBoard toBoard(String notation) {
		String[] fields = notation.split(" ");
		Bitset[] position = toPosition(fen.toArray(fields[0]));
		int turn = toTurn(fields[1].charAt(0));
		int kopoint = toKoPoint(fields[2]);
		int gameSize = fen.toArray(fields[0]).length;
		return new GoBoard(position, turn, kopoint, gameSize);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toDiagram() {
		StringJoiner notation = new StringJoiner(" ");

		notation.add(fen.toDiagram(toOccupants(position)));
		notation.add(String.valueOf(toPlayerSymbol(turn)));
		notation.add(toKoCoordinate(kopoint()));

		return notation.toString();
	}

	/**
	 * An array of piece symbols placed on the board.
	 */
	private String[] toPieceSymbols(Bitset[] position) {
		return fen.toSymbols(toOccupants(position));
	}

	/**
	 * Bidimensional array of piece identifiers from bitboards.
	 */
	public int[][] toOccupants(Bitset[] position) {
		int[][] occupants = new int[this.gameSize][this.gameSize];
		return bitset.toOccupants(occupants, position);
	}

	/**
	 * Bitboards from a bidimensional array of piece identifiers.
	 */
	private Bitset[] toPosition(int[][] occupants) {
		Bitset[] position = new Bitset[PIECE_COUNT];

		for (int i = 0; i < position.length; i++) {
			position[i] = new Bitset(BITSET_SIZE);
		}

		return bitset.toPosition(position, occupants);
	}

	/**
	 * Converts a turn identifier to a player notation.
	 */
	private static char toPlayerSymbol(int turn) {
		return turn == SOUTH ? SOUTH_SYMBOL : NORTH_SYMBOL;
	}

	/**
	 * Converts a turn identifier to a player name.
	 */
	private static String toPlayerName(int turn) {
		return turn == SOUTH ? SOUTH_NAME : NORTH_NAME;
	}

	/**
	 * Converts a player symbol to a turn identifier.
	 */
	private static int toTurn(char symbol) {
		return symbol == SOUTH_SYMBOL ? SOUTH : NORTH;
	}

	/**
	 * Converts a Ko target point to a coordinate.
	 */
	private String toKoCoordinate(int point) {
		return point == -1 ? "-" : algebraic.toCoordinate(point);
	}

	/**
	 * Converts a coodinate to a Ko target point.
	 */
	private int toKoPoint(String coordinate) {
		return "-".equals(coordinate) ? -1 : algebraic.toIndex(coordinate);
	}

	/**
	 * Replace empty symbols by star symbols if the empty symbol is placed on a star
	 * point intersection.
	 *
	 * @param symbols Symbols array
	 * @return Symbols array
	 */
	private static Object[] replaceStars(String[] symbols, int[] starPoints) {
		for (int i = 0; i < symbols.length; i++) {
			if (symbols[i].charAt(0) == DiagramConverter.EMPTY_SYMBOL) {
				symbols[i] = Character.toString(EMPTY_SYMBOL);
			}
		}
		for (int i : starPoints) {
			if (symbols[i].charAt(0) == EMPTY_SYMBOL) {
				symbols[i] = Character.toString(STAR_SYMBOL);
			}
		}

		return symbols;
	}

	/**
	 * Clones the given position array.
	 */
	private static Bitset[] clone(Bitset[] position) {
		Bitset[] state = new Bitset[PIECE_COUNT];

		for (int i = 0; i < PIECE_COUNT; i++) {
			state[i] = position[i].clone();
		}

		return state;
	}

	private static String boardString19 = ("==============( %turn to move )=============%n"
			+ "   +---------------------------------------+%n" + "19 | # # # # # # # # # # # # # # # # # # # |%n"
			+ "18 | # # # # # # # # # # # # # # # # # # # |%n" + "17 | # # # # # # # # # # # # # # # # # # # |%n"
			+ "16 | # # # # # # # # # # # # # # # # # # # |%n" + "15 | # # # # # # # # # # # # # # # # # # # |%n"
			+ "14 | # # # # # # # # # # # # # # # # # # # |%n" + "13 | # # # # # # # # # # # # # # # # # # # |%n"
			+ "12 | # # # # # # # # # # # # # # # # # # # |%n" + "11 | # # # # # # # # # # # # # # # # # # # |%n"
			+ "10 | # # # # # # # # # # # # # # # # # # # |%n" + " 9 | # # # # # # # # # # # # # # # # # # # |%n"
			+ " 8 | # # # # # # # # # # # # # # # # # # # |%n" + " 7 | # # # # # # # # # # # # # # # # # # # |%n"
			+ " 6 | # # # # # # # # # # # # # # # # # # # |%n" + " 5 | # # # # # # # # # # # # # # # # # # # |%n"
			+ " 4 | # # # # # # # # # # # # # # # # # # # |%n" + " 3 | # # # # # # # # # # # # # # # # # # # |%n"
			+ " 2 | # # # # # # # # # # # # # # # # # # # |%n" + " 1 | # # # # # # # # # # # # # # # # # # # |%n"
			+ "   +---------------------------------------+%n" + "     a b c d e f g h j k l m n o p q r s t %n"
			+ "============================================");

	private static String boardString13 = ("===========( %turn to move )========%n"
			+ "   +---------------------------+%n" + "13 | # # # # # # # # # # # # # |%n"
			+ "12 | # # # # # # # # # # # # # |%n" + "11 | # # # # # # # # # # # # # |%n"
			+ "10 | # # # # # # # # # # # # # |%n" + " 9 | # # # # # # # # # # # # # |%n"
			+ " 8 | # # # # # # # # # # # # # |%n" + " 7 | # # # # # # # # # # # # # |%n"
			+ " 6 | # # # # # # # # # # # # # |%n" + " 5 | # # # # # # # # # # # # # |%n"
			+ " 4 | # # # # # # # # # # # # # |%n" + " 3 | # # # # # # # # # # # # # |%n"
			+ " 2 | # # # # # # # # # # # # # |%n" + " 1 | # # # # # # # # # # # # # |%n"
			+ "   +---------------------------+%n" + "     a b c d e f g h j k l m n %n"
			+ "================================");

	private static String boardString9 = ("========( %turn to move )======%n" + "   +-------------------+%n"
			+ " 9 | # # # # # # # # # |%n" + " 8 | # # # # # # # # # |%n" + " 7 | # # # # # # # # # |%n"
			+ " 6 | # # # # # # # # # |%n" + " 5 | # # # # # # # # # |%n" + " 4 | # # # # # # # # # |%n"
			+ " 3 | # # # # # # # # # |%n" + " 2 | # # # # # # # # # |%n" + " 1 | # # # # # # # # # |%n"
			+ "   +-------------------+%n" + "     a b c d e f g h j %n" + "========================");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String boardString;
		int[] starPoints;
		if (this.gameSize == 9) {
			boardString = boardString9;
			starPoints = STAR_POINTS_9;
		} else if (this.gameSize == 13) {
			boardString = boardString13;
			starPoints = STAR_POINTS_13;
		} else {
			boardString = boardString19;
			starPoints = STAR_POINTS_19;
		}

		return String.format(boardString.replaceAll("(#)", "%1s").replace("%turn", toPlayerName(turn)),
				replaceStars(toPieceSymbols(position), starPoints));
	}
}
