package mines;

import java.util.Random;

public class Mines {
	private int[][] mineField;
	private boolean[][] openedSquares; // 2d array hat shows which square are shown
	private boolean[][] flags; // 2d array showing where flags were placed
	private boolean showAll = false;
	private boolean gameOver=false;
	private boolean gameLost = false;
	private int height, width; // kept for checks

	public Mines(int height, int width, int numMines) {
		// inits all arrays with given size
		this.mineField = new int[height][width];
		this.openedSquares = new boolean[height][width];
		this.flags = new boolean[height][width];

		this.height = height;
		this.width = width;

		// randomly add mines in our 2d space.
		// decrement i when a mine is already placed there,
		// so we make sure to add exactly "numMines" amount of mines.
		Random rand = new Random();
		for (int i = 0; i < numMines; i++)
			if (!addMine(rand.nextInt(height), rand.nextInt(width)))
				i--;
	}

	public boolean addMine(int i, int j) {
		// if mine is already there
		if (mineField[i][j] == -1)
			return false;

		// add mine
		mineField[i][j] = -1;

		// increasing border counters all around
		// doing each one manually is the only way
		incBorder(i + 1, j);
		incBorder(i - 1, j);
		incBorder(i, j + 1);
		incBorder(i, j - 1);
		incBorder(i + 1, j + 1);
		incBorder(i - 1, j - 1);
		incBorder(i + 1, j - 1);
		incBorder(i - 1, j + 1);

		return true;
	}

	public boolean open(int i, int j) {
		// reveals closed squares

		// returns true because only mines should return false
		// the point is to not continue with the function and cause errors
		/*
		 * even if the user inputs only correct inputs, this is still needed for the
		 * recursive calls below that opens the surroundings of a square
		 */
		if (!inBounds(i, j))
			return true;

		// if square has a mine,
		if (mineField[i][j] == -1) {
			setGameLost(true);
			return false;
		}

		// check if closed so we dont reopen already opened squares and get stuck in an
		// infinite loop of reopening already open squares
		if (!openedSquares[i][j]) {

			openedSquares[i][j] = true; // open square

			// check if no more squares can be revealed, end the game
			if (isDone())
				setGameLost(false);

			// if the square has not been affected yet
			// meaning it has no neighboring mines
			// then the neighboring squares are opened recursively
			// and since it opens recursively, all openings will go through the same checks
			if (mineField[i][j] == 0) {
				open(i + 1, j);
				open(i - 1, j);
				open(i, j + 1);
				open(i, j - 1);
				open(i + 1, j + 1);
				open(i - 1, j - 1);
				open(i + 1, j - 1);
				open(i - 1, j + 1);
			}
		}
		return true;
	}

	public void toggleFlag(int x, int y) {
		// flip the flag
		// true means there's a flag, false means no flag
		if (inBounds(x, y))
			flags[x][y] = !flags[x][y];
	}

	public boolean isDone() {
		// if a square is found to not have a mine and still be closed, returns false;
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				if (mineField[i][j] > -1 && openedSquares[i][j] == false)
					return false;

		return true;
	}

	public String get(int i, int j) {
		if (openedSquares[i][j] || showAll) // if opened or showAll=true:
			if (mineField[i][j] == -1) // if mine
				return "X";
			else if (mineField[i][j] == 0) // if no neighboring mines
				return " ";
			else
				return mineField[i][j] + ""; // else return count of neighboring mines as String

		// if closed: if flagged return "F", if not flagged return "."
		return (flags[i][j]) ? "F" : ".";
	}

	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}

	// build string to represents the board
	public String toString() {

		String str = "";
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++)
				str += get(i, j);
			str += "\n";
		}
		return str;

	}

	// my own helper methods

	// inBounds: checks if the given coordinates are in the set bounds
	private boolean inBounds(int i, int j) {
		return i >= 0 && i < height && j >= 0 && j < width;
	}

	// incBorder: increments a specific border square
	private void incBorder(int i, int j) {
		// if in bounds and not mine
		if (inBounds(i, j) && mineField[i][j] > -1)
			mineField[i][j]++;
	}

	// sets proper parameters to signal that the game is over and if it was won/lost
	private void setGameLost(boolean gameLost) {
		this.gameLost = gameLost;
		gameOver = true;
	}

	// showAll signals end of game, and gamelost is well... game lost
	public boolean gameWon() {
		return gameOver && !gameLost;
	}

	// showall signals end of game
	public boolean gameOver() {
		return gameOver;
	}
}
