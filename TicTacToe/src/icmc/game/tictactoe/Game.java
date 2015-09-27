package icmc.game.tictactoe;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class Game
{
	public byte[][] matrix;

	private Scene scene;
	private final int TABLE_SIZE;
	private ResizableCanvas canvas;
	private CpuStrategy bestFirstStrategy = new BestFirst();
	private CpuStrategy breadthStrategy = new Breadth();

	public Game(TicTacToe game, EventHandler<MouseEvent> handler, final int size, final int minimumToWin)
	{
		TABLE_SIZE = size;
		matrix = new byte[TABLE_SIZE][TABLE_SIZE];
		for (int i = 0; i < TABLE_SIZE; i++)
			for (int j = 0; j < TABLE_SIZE; j++)
				matrix[i][j] = TicTacToe.EMPTY;
		
		VBox panel = new VBox();
		canvas = new ResizableCanvas();
		
		MenuItem bestFirst = new MenuItem("Best-First");
		MenuItem depth = new MenuItem("Breadth Search");
		MenuItem bot = new MenuItem("BOT FIGHT");
		bestFirst.setOnAction(event -> {
			game.startGame(bestFirstStrategy);
		});
		depth.setOnAction(event -> {
			game.startGame(breadthStrategy);
		});
		bot.setOnAction(event -> {
			game.startGame(breadthStrategy, bestFirstStrategy);
		});
		Menu menu = new Menu("Options");
		menu.getItems().addAll(bestFirst, depth, bot);
		
		Menu aboutMenu = new Menu("About");
		MenuItem instructionsMenuItem = new MenuItem("Rules");
		instructionsMenuItem.setOnAction(event ->
		{
			Alert instructions = new Alert(AlertType.INFORMATION);
			instructions.setContentText("Make " + minimumToWin + " respective marks in horizontal, vertical or diagonal!\nGood luck!");
			instructions.setHeaderText("Welcome to MASTER RACE Tic Tac Toe!");
			instructions.setTitle("Instructions");
			instructions.show();
		});
		MenuItem aboutMenuItem = new MenuItem("About");
		aboutMenuItem.setOnAction(event ->
		{
			Alert about = new Alert(AlertType.INFORMATION);
			about.setContentText("Made by Seduq, .zb, Candiru, VHeclis and Guru");
			about.setHeaderText("Thanks for playing");
			about.setTitle("About");
			about.show();
		});
		aboutMenu.getItems().addAll(instructionsMenuItem, aboutMenuItem);
		
		MenuBar menuBar = new MenuBar(menu, aboutMenu);
		panel.getChildren().addAll(menuBar, canvas);

		// Bind canvas size to stack pane size.
		canvas.widthProperty().bind(panel.widthProperty());
		canvas.heightProperty().bind(panel.heightProperty());
		canvas.setOnMousePressed(handler);
		
		
		scene = new Scene(panel);
	}

	public Scene getScene()
	{
		return scene;
	}

	private class ResizableCanvas extends Canvas
	{
		public boolean gameOver = false;
		public ResizableCanvas()
		{
			// Redraw canvas when size changes.
			widthProperty().addListener(evt -> draw());
			heightProperty().addListener(evt -> draw());
		}

		private void draw()
		{
			double width = getWidth();
			double height = getHeight() - 20.0;

			double offsetX = width * 0.2 / TABLE_SIZE;
			double offsetY = height * 0.2 / TABLE_SIZE;

			GraphicsContext gc = getGraphicsContext2D();
			gc.clearRect(0, 0, width, height);

			double cellWidth = width / TABLE_SIZE;
			double cellHeight = height / TABLE_SIZE;

			double xPos = cellWidth, yPos = cellHeight;
			gc.setLineWidth(6.0);
			gc.setStroke(Color.BLACK);
			gc.beginPath();
			for (int i = 0; i < TABLE_SIZE - 1; i++)
			{
				gc.moveTo(xPos, 0.0);
				gc.lineTo(xPos, height);

				gc.moveTo(0.0, yPos);
				gc.lineTo(width, yPos);

				xPos += cellWidth;
				yPos += cellHeight;
			}
			gc.stroke();

			xPos = cellWidth;
			yPos = cellHeight;
			gc.setLineWidth(4.0);
			gc.setStroke(Color.GREEN);
			gc.beginPath();
			for (int i = 0; i < TABLE_SIZE - 1; i++)
			{
				gc.moveTo(xPos, 0.0);
				gc.lineTo(xPos, height);

				gc.moveTo(0.0, yPos);
				gc.lineTo(width, yPos);

				xPos += cellWidth;
				yPos += cellHeight;
			}
			gc.stroke();

			for (int i = 0; i < TABLE_SIZE; i++)
			{
				for (int j = 0; j < TABLE_SIZE; j++)
				{
					double x0 = cellWidth * i + offsetX;
					double y0 = cellHeight * j + offsetY;
					double x1 = cellWidth * (i + 1) - offsetX;
					double y1 = cellHeight * (j + 1) - offsetY;

					switch (matrix[i][j])
					{
						case TicTacToe.X:
							gc.setStroke(Color.RED);
							gc.setLineWidth(8.0);
							gc.beginPath();
							gc.moveTo(x0, y0);
							gc.lineTo(x1, y1);
							gc.moveTo(x0, y1);
							gc.lineTo(x1, y0);
							gc.stroke();

							gc.setStroke(Color.BLACK);
							gc.setLineWidth(3.0);
							gc.beginPath();
							gc.moveTo(x0, y0);
							gc.lineTo(x1, y1);
							gc.moveTo(x0, y1);
							gc.lineTo(x1, y0);
							gc.stroke();
							break;

						case TicTacToe.O:
							gc.setStroke(Color.BLUE);
							gc.setLineWidth(8.0);
							gc.strokeOval(x0, y0, x1 - x0, y1 - y0);

							gc.setStroke(Color.BLACK);
							gc.setLineWidth(3.0);
							gc.strokeOval(x0, y0, x1 - x0, y1 - y0);
							break;

						default:
							break;
					}
				}
			}
			if(gameOver)
			{
				gc.setFill(Color.gray(0.5, 0.5));
				gc.fillRect(0.0, 0.0, width, height);
			}
		}

		@Override
		public boolean isResizable()
		{
			return true;
		}

		@Override
		public double prefWidth(double height)
		{
			return getWidth();
		}

		@Override
		public double prefHeight(double width)
		{
			return getHeight();
		}
	}

	public Vector2D getPosition(double x, double y)
	{
		byte X = (byte) (x / (canvas.getWidth()) * TABLE_SIZE);
		byte Y = (byte) (y / (canvas.getHeight() - 20.0) * TABLE_SIZE);

		if (matrix[X][Y] == TicTacToe.EMPTY)
			return new Vector2D(X, Y);

		return null;
	}

	public void draw()
	{
		canvas.draw();
	}

	//Specify symbol or null for current player (multiplayer mode)
	public void win(int symbol)
	{
		Alert youWin = new Alert(AlertType.INFORMATION);
		
		if(symbol == TicTacToe.X)
			youWin.setContentText("Player X win!!");
		else
			youWin.setContentText("Player O win!!");
		youWin.setHeaderText("Congratulation!");
		youWin.setTitle("Game over");
		youWin.show();
		canvas.gameOver = true;
		canvas.draw();
	}

	public void lose()
	{
		Alert youWin = new Alert(AlertType.INFORMATION);
		youWin.setContentText("You lose!!");
		youWin.setHeaderText("Too bad!");
		youWin.setTitle("Game over");
		youWin.show();
		canvas.gameOver = true;
		canvas.draw();
	}

	public void tie()
	{
		Alert youWin = new Alert(AlertType.INFORMATION);
		youWin.setContentText("It's a draw!!");
		youWin.setHeaderText("Too bad!");
		youWin.setTitle("Game over");
		youWin.show();
		canvas.gameOver = true;
		canvas.draw();
	}
	
	public void reset()
	{
		canvas.gameOver = false;
		for (int i = 0; i < TABLE_SIZE; i++)
			for (int j = 0; j < TABLE_SIZE; j++)
				matrix[i][j] = TicTacToe.EMPTY;
		canvas.draw();
	}
}
