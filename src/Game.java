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

import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

/**
 * The {@Game} class is logical representation of the game.
 * 
 * @author olegshchepilov
 *
 */

final public class Game {
	public Dimension getFieldSize() {
		return (field == null) ? new Dimension(0, 0) : field.getSize();
	}
	public Cell getCell(int columnIndex, int rowIndex) {
		return (field == null) ? null : field.at(columnIndex, rowIndex);
	}
	public Point getMolePosition() {
		return (mole == null) ? new Point(-1, -1) : mole.getCurrentPosition();
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
		Point molePoint = new Point();
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
					molePoint.setLocation(x, y);
				}
				if (ch == 'B') {
					boxes.add(new Point(x, y));
				}
			}
		}
		
		mole = new Mole();
		mole.setCurrentPosition(molePoint);
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
		
		mole = new Mole();
		mole.setCurrentPosition(new Point(2, 2));
	}
	
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
	
	private Field field = null;
	private Mole mole = null;
	private List<Point> boxes = new ArrayList<Point>();
}
