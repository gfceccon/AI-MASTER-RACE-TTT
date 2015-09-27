package icmc.game.tictactoe;

import java.util.ArrayDeque;

public class State
{
	public byte[][] board;
	public byte moveX;
	public byte moveY;
	public byte initialMoveX;
	public byte initialMoveY;
	public boolean playerMove;
	public double score;
	

	public static ArrayDeque<State> statePool = new ArrayDeque<>();

	public static State getPooledState(byte[][] matrix, byte moveX, byte moveY, byte initialMoveX, byte initialMoveY, boolean playerMove)
	{
		State result;
		if (statePool.isEmpty())
		{
			result = new State();
			result.board = new byte[matrix.length][];
			for (byte i = 0; i < matrix.length; i++)
				result.board[i] = matrix[i].clone();
		}
		else
		{
			result = statePool.removeFirst();
			for (byte i = 0; i < matrix.length; i++)
				for (byte j = 0; j < matrix[i].length; j++)
					result.board[i][j] = matrix[i][j];
		}
		result.moveX = moveX;
		result.moveY = moveY;
		result.initialMoveX = initialMoveX;
		result.initialMoveY = initialMoveY;
		result.playerMove = playerMove;
		return result;
	}
}