package icmc.game.tictactoe;

public interface CpuStrategy
{
	Vector2D getMove(byte[][] board, byte playerSymbol, byte cpuSymbol);
}