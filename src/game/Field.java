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

import utils.*;

/**
 * The {@Field} is a logical game field that contains persistent (static) objects
 *
 * @author olegshchepilov
 *
 */

public class Field {
	public Field() {}
	
	public void setSize(Dimension size) {
		setSize(size.width, size.height);
	}
	public void setSize(int width, int height) {
		final Cell[][] prevCells = cells;
		final Dimension prevSize = getSize(); 
		cells = new Cell[height][width];
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				if ((prevCells != null) && (x < prevSize.width) && (y < prevSize.height)) { 
					cells[y][x] = prevCells[y][x];					
				} else {
					cells[y][x] = new Cell();					
				}
			}
		}
	}
	public Dimension getSize() {
		return (cells == null) ? new Dimension(0, 0) : new Dimension(cells[0].length, cells.length);
	}
	public Cell at(int x, int y) {
		return cells[y][x];
	}
	
	public PointSet getCellCoordinatesByType(Cell.Type type) {
		PointSet set = new PointSet();
		for (int y = 0 ; y < cells.length; ++y) {
			final Cell[] row = cells[y];
			for (int x = 0; x < row.length; ++x) {
				final Cell cell = row[x];
				if (cell.type == type) {
					set.add(new Point(x, y));
				}
			}
		}
		return set;
	}
	
	
	private Cell[][] cells = null;
}
