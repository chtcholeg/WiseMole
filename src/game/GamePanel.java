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

package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JPanel;

import common.*;
import utils.*;

/**
 * The {@GamePanel} is a panel that is responsible for drawing the game board
 *
 * @author olegshchepilov
 *
 */

public class GamePanel extends PanelBase implements KeyListener {
	public GamePanel(String levelId) {
		game = new Game();
		game.loadGame(levelId);
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
	
	@Override
	public KeyListener keyListener() { return this; }
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {
		final int keyCode = e.getKeyCode();
		boolean haveChanges = false;
		switch (keyCode) { 
			case KeyEvent.VK_UP: haveChanges = game.tryToMoveMole(Game.MoleMovementDirection.UP); break;
			case KeyEvent.VK_DOWN: haveChanges = game.tryToMoveMole(Game.MoleMovementDirection.DOWN); break;
			case KeyEvent.VK_LEFT: haveChanges = game.tryToMoveMole(Game.MoleMovementDirection.LEFT); break;
			case KeyEvent.VK_RIGHT: haveChanges = game.tryToMoveMole(Game.MoleMovementDirection.RIGHT); break;
		}
		if (haveChanges) {
			revalidate();
			repaint();
		}

	}
	@Override
	public void keyReleased(KeyEvent e) {}

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
				Rectangle cellRect = calcCellRect(workingRect, cellSize, columnIndex, rowIndex);
				drawCell(graphics2d, cellRect, game.getCell(columnIndex, rowIndex));
			}
		}
		
		// Foreground
		Point molePosition = game.getMolePosition();
		Rectangle cellRect = calcCellRect(workingRect, cellSize, molePosition.x, molePosition.y);
		drawImage(graphics2d, cellRect, "mole.png");
		List<Point> boxes = game.getBoxes();
		for (Point box : boxes) {
			cellRect = calcCellRect(workingRect, cellSize, box.x, box.y);
			drawImage(graphics2d, cellRect, "box_inactive.png");			
		}
	}
	
	private void drawCell(Graphics2D graphics, Rectangle cellRect, Cell cell) {
		Cell.Type type = cell.type;
		if (type == Cell.Type.WALL) {
			graphics.setColor(Color.RED);
			graphics.fillRect(cellRect.x, cellRect.y, cellRect.width, cellRect.height);
			drawImage(graphics, cellRect, "wall.png");
		} else if (type == Cell.Type.FLOOR) {
			graphics.setColor(Color.BLACK);
			graphics.fillRect(cellRect.x, cellRect.y, cellRect.width, cellRect.height);
			drawImage(graphics, cellRect, "floor.png");
		}
	}
	
	private void drawImage(Graphics2D graphics, Rectangle cellRect, String resourceId) {
		Image image = ImageStorage.getImage(resourceId);
		if (image != null) {
			graphics.drawImage(image, cellRect.x, cellRect.y, cellRect.width, cellRect.height, null);				
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
	
	private Rectangle calcCellRect(Rectangle workingRect, Dimension cellSize, int x, int y) {
		return new Rectangle(workingRect.x + cellSize.width * x, workingRect.y + cellSize.height * y, cellSize.width, cellSize.height);
	}

	static private void deflateRect(Rectangle rect, int dx, int dy) {
		rect.x += dx;
		rect.y += dy;
		rect.width -= 2 * dx;
		rect.height -= 2 * dy;
	}

	private final int padding = 10;
	private Game game = null;
	private static final long serialVersionUID = 1L;
}
