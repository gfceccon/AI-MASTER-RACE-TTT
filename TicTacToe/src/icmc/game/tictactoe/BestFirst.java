package icmc.game.tictactoe;

import java.util.Comparator;
import java.util.PriorityQueue;

public class BestFirst implements CpuStrategy
{
	private byte playerSymbol;
	private byte cpuSymbol;
	private byte TABLE_SIZE;
	private byte MINIMUM_TO_WIN;
	private int BIG_WEIGHT = 10000;
	private int MEDIUM_WEIGHT = 500;
	private int LOW_WEIGHT = 10;

	private PriorityQueue<State> queue;

	public BestFirst()
	{
		this.TABLE_SIZE = TicTacToe.TABLE_SIZE;
		this.MINIMUM_TO_WIN = TicTacToe.MINIMUM_TO_WIN;
		queue = new PriorityQueue<>(TABLE_SIZE * TABLE_SIZE, new Comparator<State>()
		{

			@Override
			public int compare(State o1, State o2)
			{
				return -Double.compare(o1.score, o2.score);
			}
		});
	}

	private double getVerticalScore(byte[][] board, byte x, byte y)
	{
		int numberOfCpuSymbols = 0;
		int numberOfPlayerSymbols = 0;

		for (int i = y - 1; i > -1 && board[x][i] == cpuSymbol; i--)
			numberOfCpuSymbols++;
		for (int i = y + 1; i < TABLE_SIZE && board[x][i] == cpuSymbol; i++)
			numberOfCpuSymbols++;

		for (int i = y - 1; i > -1 && board[x][i] == playerSymbol; i--)
			numberOfPlayerSymbols++;
		for (int i = y + 1; i < TABLE_SIZE && board[x][i] == playerSymbol; i++)
			numberOfPlayerSymbols++;

		double quotient = numberOfCpuSymbols / (MINIMUM_TO_WIN - 1);
		double quotient2 = numberOfPlayerSymbols / (MINIMUM_TO_WIN - 2);

		return Math.pow(BIG_WEIGHT, quotient) + Math.pow(MEDIUM_WEIGHT, quotient2);
	}

	private double getHorizontalScore(byte[][] board, byte x, byte y)
	{
		int numberOfCpuSymbols = 0;
		int numberOfPlayerSymbols = 0;

		for (int i = x - 1; i > -1 && board[i][y] == cpuSymbol; i--)
			numberOfCpuSymbols++;
		for (int i = x + 1; i < TABLE_SIZE && board[i][y] == cpuSymbol; i++)
			numberOfCpuSymbols++;

		for (int i = x - 1; i > -1 && board[i][y] == playerSymbol; i--)
			numberOfPlayerSymbols++;
		for (int i = x + 1; i < TABLE_SIZE && board[i][y] == playerSymbol; i++)
			numberOfPlayerSymbols++;

		double quotient = numberOfCpuSymbols / (MINIMUM_TO_WIN - 1);
		double quotient2 = numberOfPlayerSymbols / (MINIMUM_TO_WIN - 2);

		return Math.pow(BIG_WEIGHT, quotient) + Math.pow(MEDIUM_WEIGHT, quotient2);
	}

	private double getDiagonalDownScore(byte[][] board, byte x, byte y)
	{
		int numberOfCpuSymbols = 0;
		int numberOfPlayerSymbols = 0;

		for (int i = -1; x + i > -1 && y + i > -1 && board[x + i][y + i] == cpuSymbol; i--)
			numberOfCpuSymbols++;
		for (int i = 1; x + i < TABLE_SIZE && y + i < TABLE_SIZE && board[x + i][y + i] == cpuSymbol; i++)
			numberOfCpuSymbols++;

		for (int i = -1; x + i > -1 && y + i > -1 && board[x + i][y + i] == playerSymbol; i--)
			numberOfPlayerSymbols++;
		for (int i = 1; x + i < TABLE_SIZE && y + i < TABLE_SIZE && board[x + i][y + i] == playerSymbol; i++)
			numberOfPlayerSymbols++;

		double quotient = numberOfCpuSymbols / (MINIMUM_TO_WIN - 1);
		double quotient2 = numberOfPlayerSymbols / (MINIMUM_TO_WIN - 2);

		return Math.pow(BIG_WEIGHT, quotient) + Math.pow(MEDIUM_WEIGHT, quotient2);
	}

	private double getDiagonalUpScore(byte[][] board, byte x, byte y)
	{
		int numberOfCpuSymbols = 0;
		int numberOfPlayerSymbols = 0;

		for (int i = -1; x - i < TABLE_SIZE && y + i > -1 && board[x - i][y + i] == cpuSymbol; i--)
			numberOfCpuSymbols++;
		for (int i = 1; x - i > -1 && y + i < TABLE_SIZE && board[x - i][y + i] == cpuSymbol; i++)
			numberOfCpuSymbols++;

		for (int i = -1; x - i < TABLE_SIZE && y + i > -1 && board[x - i][y + i] == playerSymbol; i--)
			numberOfPlayerSymbols++;
		for (int i = 1; x - i > -1 && y + i < TABLE_SIZE && board[x - i][y + i] == playerSymbol; i++)
			numberOfPlayerSymbols++;

		double quotient = numberOfCpuSymbols / (MINIMUM_TO_WIN - 1);
		double quotient2 = numberOfPlayerSymbols / (MINIMUM_TO_WIN - 2);

		return Math.pow(BIG_WEIGHT, quotient) + Math.pow(MEDIUM_WEIGHT, quotient2);
	}

	private double getPositionScore(byte[][] board, byte i, byte j)
	{
		int center = TABLE_SIZE / 2;
		double quotient = (1.0 / (Math.abs(center - i) + 1.0) + 1.0 / (Math.abs(center - j) + 1.0)) / 2.0;
		return Math.pow(LOW_WEIGHT, quotient);
	}

	@Override
	public Vector2D getMove(byte[][] board, byte playerSymbol, byte cpuSymbol)
	{
		this.playerSymbol = playerSymbol;
		this.cpuSymbol = cpuSymbol;

		byte x = 0, y = 0;
		double bestScore = Double.NEGATIVE_INFINITY;

		State currentState = null;
		byte[][] currentBoard = board;

		do
		{
			currentState = queue.poll();
			if (currentState != null)
			{
				currentBoard = currentState.board;
				if (currentState.playerMove)
				{
					if (TicTacToe.checkWin(currentBoard, currentState.moveX, currentState.moveY, playerSymbol))
					{
						State.statePool.add(currentState);
						continue;
					}
				}
				else
				{
					if (TicTacToe.checkWin(currentBoard, currentState.moveX, currentState.moveY, cpuSymbol))
					{
						while (!queue.isEmpty())
							State.statePool.add(queue.poll());
						return new Vector2D(currentState.initialMoveX, currentState.initialMoveY);
					}
				}
			}

			for (byte i = 0; i < currentBoard.length; i++)
				for (byte j = 0; j < currentBoard[i].length; j++)
					if (currentBoard[i][j] == TicTacToe.EMPTY)
					{
						State s;
						if (currentState != null)
							s = State.getPooledState(currentBoard, i, j, currentState.initialMoveX,
									currentState.initialMoveY, !currentState.playerMove);
						else
							s = State.getPooledState(currentBoard, i, j, i, j, false);
						
						if (s.playerMove)
						{
							s.board[i][j] = playerSymbol;
							s.score = currentState.score;
						}
						else
						{
							s.board[i][j] = cpuSymbol;
							s.score = getVerticalScore(currentBoard, i, j) + getHorizontalScore(currentBoard, i, j)
									+ getDiagonalUpScore(currentBoard, i, j) + getDiagonalDownScore(currentBoard, i, j)
									+ getPositionScore(currentBoard, i, j);
							if(currentState == null && s.score > bestScore)
							{
								x = i;
								y = j;
								bestScore = s.score;
							}
						}
						queue.add(s);
					}
		} while (!queue.isEmpty());

		// for (byte i = 0; i < board.length; i++)
		// for (byte j = 0; j < board[i].length; j++)
		// if (board[i][j] == TicTacToe.EMPTY)
		// {
		// score = getVerticalScore(board, i, j) + getHorizontalScore(board, i,
		// j)
		// + getDiagonalUpScore(board, i, j) + getDiagonalDownScore(board, i, j)
		// + getPositionScore(board, i, j);
		// if (score > max_score)
		// {
		// x = i;
		// y = j;
		// max_score = score;
		// }
		// }
		return new Vector2D(x, y);
	}

}
