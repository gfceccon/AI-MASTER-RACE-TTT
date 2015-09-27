package icmc.game.tictactoe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class TicTacToe extends Application
{
	// Constants for gameplay, you can change this anytime
	public static final byte TABLE_SIZE = 3;
	public static final byte MINIMUM_TO_WIN = 3;

	public static final byte EMPTY = 0;
	public static final byte X = 1;
	public static final byte O = 2;

	public static void main(String[] args)
	{
		launch(args);
	}

	// Handler for mouse input
	private Handler eventHandler;

	// Game scene application
	private Game scene;

	// This player symbol and my turn flag
	public byte symbol;

	// Round counter
	public byte round = 0;

	// Game over flag
	private boolean isGameOver;

	private CpuStrategy strategyX = null;
	private CpuStrategy strategyO = null;

	// Handler for canvas mouse input
	private class Handler implements EventHandler<MouseEvent>
	{
		@Override
		public void handle(MouseEvent event)
		{
			if (isGameOver)
				return;
			// Get matrix position from X, Y coordinates
			Vector2D point = scene.getPosition(event.getX(), event.getY());

			// If is not avaible
			if (point == null)
				return;

			if (!isGameOver)
			{
				if (strategyX != null)
				{
					Vector2D result = strategyX.getMove(scene.matrix, TicTacToe.O, TicTacToe.X);
					makeMove(result.X, result.Y, TicTacToe.X);
				}
				else
					makeMove(point.X, point.Y, TicTacToe.X);
			}
			if (!isGameOver)
			{
				if (strategyO != null)
				{
					Vector2D result = strategyO.getMove(scene.matrix, TicTacToe.X, TicTacToe.O);
					makeMove(result.X, result.Y, TicTacToe.O);
				}
			}
		}
	}

	public void start(Stage stage) throws IOException
	{
		eventHandler = new Handler();
		scene = new Game(this, eventHandler, TABLE_SIZE, MINIMUM_TO_WIN);
		isGameOver = true;

		stage.setTitle("MASTER RACE Tic Tac Toe");

		stage.setMinWidth(600);
		stage.setMinHeight(600);
		stage.setWidth(600);
		stage.setHeight(600);

		stage.setScene(scene.getScene());
		stage.show();
	}

	// Make move and check for winner
	// If symbol is null then it's the enemy turn
	private void makeMove(byte x, byte y, byte symbol)
	{
		if (x < 0 || y < 0 || x > TABLE_SIZE - 1 || y > TABLE_SIZE - 1)
			return;

		round++;
		scene.matrix[x][y] = symbol;
		scene.draw();

		if (TicTacToe.checkWin(scene.matrix, x, y, symbol))
			gameOver(false, symbol);
		else if (round == TABLE_SIZE * TABLE_SIZE)
			gameOver(true, (byte) 0);
	}

	public static boolean checkWin(byte[][] board, byte x, byte y, byte symbol)
	{
		byte numberOfSymbols = 1;
		// SAME LINE
		for (int i = x - 1; i > -1 && board[i][y] == symbol; i--)
			numberOfSymbols++;
		for (int i = x + 1; i < TABLE_SIZE && board[i][y] == symbol; i++)
			numberOfSymbols++;

		// SAME COLUMN
		if (numberOfSymbols < MINIMUM_TO_WIN)
		{
			numberOfSymbols = 1;

			for (int i = y - 1; i > -1 && board[x][i] == symbol; i--)
				numberOfSymbols++;
			for (int i = y + 1; i < TABLE_SIZE && board[x][i] == symbol; i++)
				numberOfSymbols++;

			// SAME DIAGONAL POSITIVE
			if (numberOfSymbols < MINIMUM_TO_WIN)
			{
				numberOfSymbols = 1;

				for (byte i = -1; x + i > -1 && y + i > -1 && board[x + i][y + i] == symbol; i--)
					numberOfSymbols++;
				for (byte i = 1; x + i < TABLE_SIZE && y + i < TABLE_SIZE && board[x + i][y + i] == symbol; i++)
					numberOfSymbols++;

				// SAME DIAGONAL NEGATIVE
				if (numberOfSymbols < MINIMUM_TO_WIN)
				{
					numberOfSymbols = 1;

					for (byte i = -1; x - i < TABLE_SIZE && y + i > -1 && board[x - i][y + i] == symbol; i--)
						numberOfSymbols++;
					for (byte i = 1; x - i > -1 && y + i < TABLE_SIZE && board[x - i][y + i] == symbol; i++)
						numberOfSymbols++;
				}
			}
		}

		if (numberOfSymbols >= MINIMUM_TO_WIN)
			return true;
		return false;
	}

	private void gameOver(boolean tie, byte winner)
	{
		isGameOver = true;
		if (tie)
			Platform.runLater(() -> {
				scene.tie();
			});
		else
		{
			Platform.runLater(() -> {
				scene.win(winner);
			});
		}
	}

	public void startGame(CpuStrategy strategy)
	{
		scene.reset();
		this.strategyO = strategy;
		this.round = 0;
		this.isGameOver = false;
	}

	public void startGame(CpuStrategy strategyX, CpuStrategy strategyO)
	{
		scene.reset();
		this.strategyX = strategyX;
		this.strategyO = strategyO;
		this.round = 0;
		this.isGameOver = false;

	}
}
