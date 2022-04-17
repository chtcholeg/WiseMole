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

import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import utils.PointSet;

/**
 * The {@Game} class is logical representation of the game.
 * 
 * @author olegshchepilov
 *
 */

final public class Game {
	public enum MoleMovementDirection {UP, DOWN, LEFT, RIGHT}
	
	public Dimension getFieldSize() {
		return (field == null) ? new Dimension(0, 0) : field.getSize();
	}
	public Cell getCell(int columnIndex, int rowIndex) {
		return (field == null) ? null : field.at(columnIndex, rowIndex);
	}
	public Point getMolePosition() {
		return (moleLocation == null) ? new Point(-1, -1) : moleLocation;
	}
	public List<Point> getBoxes() {
		return boxes;
	}
	
	public void loadGame(String gameId) {
		String[] lines = loadGameData(gameId);
		if (lines == null || lines.length == 0) {
			return;
		}
		
		final int maxLineLength = findMaxLineLength(lines);
		for (int i = 0; i < lines.length; ++i) {
			if (lines[i].length() < maxLineLength) {
				lines[i] += String.valueOf('E').repeat(maxLineLength - lines[i].length());
			}
		}
		
		field = new Field();
		field.setSize(maxLineLength, lines.length);
		for (int y = 0; y < lines.length; ++y) {
			String line = lines[y];
			for (int x = 0; x < maxLineLength; ++x) {
				Cell cell = field.at(x, y);
				final char ch = line.charAt(x); 
				if (ch == 'E') {
					cell.type = Cell.Type.NULL;					
				} else if (ch == 'W') {
					cell.type = Cell.Type.WALL;										
				} else {
					cell.type = Cell.Type.FLOOR;															
				}
				if (ch == 'M') {
					moleLocation = new Point(x, y);
				}
				if (ch == 'B') {
					boxes.add(new Point(x, y));
				}
			}
		}
	}
	
	public boolean tryToMoveMole(MoleMovementDirection direction) {
		if (!canMoveMole(direction)) {
			return false;
		}
		moveMole(direction);
		return true;
	}
	
	public void createDefGame() {
		field = new Field();
		field.setSize(7, 6);
		field.at(0, 0).type = Cell.Type.WALL;
		field.at(1, 0).type = Cell.Type.WALL;
		field.at(2, 0).type = Cell.Type.WALL;
		field.at(3, 0).type = Cell.Type.WALL;
		field.at(4, 0).type = Cell.Type.WALL;
		field.at(5, 0).type = Cell.Type.WALL;
		field.at(6, 0).type = Cell.Type.WALL;
		
		field.at(0, 1).type = Cell.Type.WALL;
		field.at(1, 1).type = Cell.Type.FLOOR;
		field.at(2, 1).type = Cell.Type.FLOOR;
		field.at(3, 1).type = Cell.Type.FLOOR;
		field.at(4, 1).type = Cell.Type.FLOOR;
		field.at(5, 1).type = Cell.Type.FLOOR;
		field.at(6, 1).type = Cell.Type.WALL;

		field.at(0, 2).type = Cell.Type.WALL;
		field.at(1, 2).type = Cell.Type.FLOOR;
		field.at(2, 2).type = Cell.Type.FLOOR;
		field.at(3, 2).type = Cell.Type.FLOOR;
		field.at(4, 2).type = Cell.Type.FLOOR;
		field.at(5, 2).type = Cell.Type.FLOOR;
		field.at(6, 2).type = Cell.Type.WALL;

		field.at(0, 3).type = Cell.Type.WALL;
		field.at(1, 3).type = Cell.Type.FLOOR;
		field.at(2, 3).type = Cell.Type.FLOOR;
		field.at(3, 3).type = Cell.Type.FLOOR;
		field.at(4, 3).type = Cell.Type.FLOOR;
		field.at(5, 3).type = Cell.Type.FLOOR;
		field.at(6, 3).type = Cell.Type.WALL;

		field.at(0, 4).type = Cell.Type.WALL;
		field.at(1, 4).type = Cell.Type.FLOOR;
		field.at(2, 4).type = Cell.Type.FLOOR;
		field.at(3, 4).type = Cell.Type.FLOOR;
		field.at(4, 4).type = Cell.Type.FLOOR;
		field.at(5, 4).type = Cell.Type.WALL;
		field.at(6, 4).type = Cell.Type.WALL;

		field.at(0, 5).type = Cell.Type.WALL;
		field.at(1, 5).type = Cell.Type.WALL;
		field.at(2, 5).type = Cell.Type.WALL;
		field.at(3, 5).type = Cell.Type.WALL;
		field.at(4, 5).type = Cell.Type.WALL;
		field.at(5, 5).type = Cell.Type.WALL;
		
		moleLocation = new Point(2, 2);
	}
	
	private Field field = null;
	private Point moleLocation = new Point(-1, -1);
	private List<Point> boxes = new ArrayList<Point>();
	
	
	private String[] loadGameData(String resourceId) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try (InputStream input = classLoader.getResourceAsStream("game/" + resourceId)) {
	        if (input == null) {
	        	return null;
	        }
	        List<String> lineList = new ArrayList<String>();
	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	            	lineList.add(line);
	            }
	    		String[] result = new String[lineList.size()];
	    		lineList.toArray(result);
	    		return result;
	        } catch (IOException exception) {
		    }
	    } catch (IOException exception) {
	    }
		
		return null;
	}
	
	private int findMaxLineLength(String[] lines) {
		int result = 0;
		for(String line : lines) {
			result = Math.max(result, line.length());
		}
		return result;
	}
	
	static Point convertDirectionToPoint(MoleMovementDirection direction) {
		switch(direction) {
			case UP: return new Point(0, -1);
			case DOWN: return new Point(0, 1);
			case LEFT: return new Point(-1, 0);
			case RIGHT: return new Point(1, 0);
		}
		return null;
	}
	private boolean canMoveMole(MoleMovementDirection direction) {
		PointSet floor = field.getCellCoordinatesByType(Cell.Type.FLOOR);
		Point offset = convertDirectionToPoint(direction);
		if (offset == null) {
			return false;
		}
		
		// Check floor cell
		Point newMoleLocation = new Point(moleLocation.x + offset.x, moleLocation.y + offset.y);
		if (!floor.has(newMoleLocation)) {
			return false;
		}
		
		// Do we have a box on this cell?
		boolean hasBox = false;
		for (int index = 0; index < boxes.size(); ++index) {
			Point box = boxes.get(index);
			if (box.equals(newMoleLocation)) {
				hasBox = true;
				break;
			}
		}
		if (!hasBox) {
			return true;
		}
		
		// Can the mole move the box
		Point newBoxLocation = new Point(newMoleLocation.x + offset.x, newMoleLocation.y + offset.y);
		if (!floor.has(newBoxLocation)) {
			return false;
		}
		
		return true;
	}
	
	private void moveMole(MoleMovementDirection direction) {
		if (!canMoveMole(direction)) {
			return;
		}
		Point offset = convertDirectionToPoint(direction);
		Point newMoleLocation = new Point(moleLocation.x + offset.x, moleLocation.y + offset.y);
		
		for (int index = 0; index < boxes.size(); ++index) {
			Point box = boxes.get(index);
			if (box.equals(newMoleLocation)) {
				box.translate(offset.x, offset.y);
				break;
			}
		}

		moleLocation = newMoleLocation;
	}
}
