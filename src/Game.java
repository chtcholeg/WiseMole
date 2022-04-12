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
	
	public void createDefField() {
		field = new Field();
		field.setSize(7, 6);
		field.at(1, 0).type = Cell.Type.WALL;
		field.at(2, 0).type = Cell.Type.WALL;
		field.at(3, 0).type = Cell.Type.WALL;
		field.at(4, 0).type = Cell.Type.WALL;
		field.at(5, 0).type = Cell.Type.WALL;
		
		field.at(0, 1).type = Cell.Type.WALL;
		field.at(1, 1).type = Cell.Type.SPACE;
		field.at(2, 1).type = Cell.Type.SPACE;
		field.at(3, 1).type = Cell.Type.SPACE;
		field.at(4, 1).type = Cell.Type.SPACE;
		field.at(5, 1).type = Cell.Type.SPACE;
		field.at(6, 1).type = Cell.Type.WALL;

		field.at(0, 2).type = Cell.Type.WALL;
		field.at(1, 2).type = Cell.Type.SPACE;
		field.at(2, 2).type = Cell.Type.SPACE;
		field.at(3, 2).type = Cell.Type.SPACE;
		field.at(4, 2).type = Cell.Type.SPACE;
		field.at(5, 2).type = Cell.Type.SPACE;
		field.at(6, 2).type = Cell.Type.WALL;

		field.at(0, 3).type = Cell.Type.WALL;
		field.at(1, 3).type = Cell.Type.SPACE;
		field.at(2, 3).type = Cell.Type.SPACE;
		field.at(3, 3).type = Cell.Type.SPACE;
		field.at(4, 3).type = Cell.Type.SPACE;
		field.at(5, 3).type = Cell.Type.SPACE;
		field.at(6, 3).type = Cell.Type.WALL;

		field.at(0, 4).type = Cell.Type.WALL;
		field.at(1, 4).type = Cell.Type.SPACE;
		field.at(2, 4).type = Cell.Type.SPACE;
		field.at(3, 4).type = Cell.Type.SPACE;
		field.at(4, 4).type = Cell.Type.SPACE;
		field.at(5, 4).type = Cell.Type.SPACE;
		field.at(6, 4).type = Cell.Type.WALL;

		field.at(1, 5).type = Cell.Type.WALL;
		field.at(2, 5).type = Cell.Type.WALL;
		field.at(3, 5).type = Cell.Type.WALL;
		field.at(4, 5).type = Cell.Type.WALL;
		field.at(5, 5).type = Cell.Type.WALL;
	}
	
	private Field field = null;
}
