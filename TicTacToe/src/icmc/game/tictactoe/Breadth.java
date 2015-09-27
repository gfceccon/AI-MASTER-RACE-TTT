package icmc.game.tictactoe;

import java.util.LinkedList;

public class Breadth implements CpuStrategy
{

	LinkedList<State> queue = new LinkedList<>();

	@Override
	public Vector2D getMove(byte[][] board, byte playerSymbol, byte cpuSymbol)
	{
		byte randomPlaceX = 0;
		byte randomPlaceY = 0;
		State currentState = null;
		byte[][] currentBoard = board;
		do
		{
			if (!queue.isEmpty())
			{
				currentState = queue.removeFirst();
				currentBoard = currentState.board;
			}
			if (currentState != null)
			{
				if (currentState.playerMove)
				{
					if (TicTacToe.checkWin(currentBoard, currentState.moveX, currentState.moveY, playerSymbol))
						continue;
				}
				else
				{
					if (TicTacToe.checkWin(currentBoard, currentState.moveX, currentState.moveY, cpuSymbol))
					{
						while(!queue.isEmpty())
							State.statePool.add(queue.removeFirst());
						return new Vector2D(currentState.initialMoveX, currentState.initialMoveY);
					}
				}
			}

			for (byte i = 0; i < currentBoard.length; i++)
			{
				for (byte j = 0; j < currentBoard[i].length; j++)
				{
					if (currentBoard[i][j] == TicTacToe.EMPTY)
					{
						State s;
						if (currentState != null)
							s = State.getPooledState(currentBoard, i, j, currentState.initialMoveX, currentState.initialMoveY, !currentState.playerMove);
						else
						{
							s = State.getPooledState(currentBoard, i, j, i, j, false);
							randomPlaceX = i;
							randomPlaceY = j;
						}

						if (s.playerMove)
							s.board[i][j] = playerSymbol;
						else
							s.board[i][j] = cpuSymbol;
						queue.add(s);
					}
				}
			}
			if(currentState != null)
				State.statePool.add(currentState);
		} while (!queue.isEmpty());
		return new Vector2D(randomPlaceX, randomPlaceY);
	}
}
