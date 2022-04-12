/*
 * Copyright (C) 2022 The Java Open Source Project 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Rectangle;

/**
 * The {@AppBoard} is a panel that is responsible for drawing the board
 *
 * @author olegshchepilov
 *
 */

public class AppBoard extends JPanel {
	public AppBoard() {

	}

	public void setGame(Game newGame) {
		game = newGame;
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		if (game != null) {
			final Rectangle workingRect = calcWorkingRect();
			if (workingRect != null) {
				drawField(graphics, workingRect);				
			}
		}
	}

	private void drawField(Graphics graphics, Rectangle workingRect) {
		RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		Graphics2D graphics2d = (Graphics2D) graphics;
		graphics2d.setRenderingHints(renderingHints);

		// Background
		final Dimension fieldSize = game.getFieldSize();
		final Dimension cellSize = new Dimension(workingRect.width / fieldSize.width, workingRect.height / fieldSize.height);
		for (int rowIndex = 0; rowIndex < fieldSize.height; ++rowIndex) {
			for (int columnIndex = 0; columnIndex < fieldSize.width; ++columnIndex) {
				Rectangle cellRect = new Rectangle(workingRect.x + cellSize.width * columnIndex, workingRect.y + cellSize.height * rowIndex, cellSize.width, cellSize.height);
				drawCell(graphics2d, cellRect, game.getCell(columnIndex, rowIndex));
			}
		}
		
		// Foreground

		
	}
	
	private void drawCell(Graphics2D graphics, Rectangle cellRect, Cell cell) {
		Cell.Type type = cell.type;
		if (type == Cell.Type.WALL) {
			graphics.setColor(Color.RED);
			graphics.fillRect(cellRect.x, cellRect.y, cellRect.width, cellRect.height);
			try {
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				InputStream input = classLoader.getResourceAsStream("wall.png");
				Image image =  ImageIO.read(input);
				graphics.drawImage(image, cellRect.x, cellRect.y, cellRect.width, cellRect.height, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (type == Cell.Type.SPACE) {
			graphics.setColor(Color.BLACK);
			graphics.fillRect(cellRect.x, cellRect.y, cellRect.width, cellRect.height);
			try {
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				InputStream input = classLoader.getResourceAsStream("floor.png");
				Image image =  ImageIO.read(input);
				graphics.drawImage(image, cellRect.x, cellRect.y, cellRect.width, cellRect.height, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			//graphics.setColor(Color.BLACK);
			//graphics.fillRect(cellRect.x, cellRect.y, cellRect.width, cellRect.height);
		}
	}
	
	private Rectangle calcWorkingRect() {
		final Dimension size = getSize();
		final double width = size.getWidth();
		final double height = size.getHeight();
		if (width <= 0.0 || height <= 0.0) {
			return null;
		}
		Rectangle workRect = new Rectangle(0, 0, (int) width, (int) height);
		deflateRect(workRect, padding, padding);
		final double workRectRatio = width / height;
		Dimension fieldSize = game.getFieldSize();
		if (fieldSize.width <= 0.0 || fieldSize.height <= 0.0) {
			return null;
		}
		final double fieldRatio = (double) fieldSize.width / (double) fieldSize.height;
		final boolean scaleByWidth = (fieldRatio < workRectRatio);
		if (scaleByWidth) {
			final int newWorkRectWidth = (int)((double) workRect.getHeight() * fieldRatio);
			workRect.x = workRect.x + (workRect.width - newWorkRectWidth) / 2;
			workRect.width = newWorkRectWidth;
		} else {
			final int newWorkRectHeight = (int)((double) workRect.getWidth() / fieldRatio);
			workRect.y = workRect.y + (workRect.height - newWorkRectHeight) / 2;
			workRect.height = newWorkRectHeight;			
		}		
		
		return workRect;
	}

	static private void deflateRect(Rectangle rect, int dx, int dy) {
		rect.x += dx;
		rect.y += dy;
		rect.width -= 2 * dx;
		rect.height -= 2 * dy;
	}

	private final int padding = 10;
	private Game game = null;
}
