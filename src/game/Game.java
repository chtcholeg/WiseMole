/*
 * Copyright (C) 2022 The Java Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package game;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import utils.PointSet;

/**
 * The {@Game} class is logical representation of the game.
 * 
 * @author olegshchepilov
 *
 */

final public class Game {
    public enum MoleMovementDirection {
        UP, DOWN, LEFT, RIGHT
    }

    public final static int MAX_FIELD_WIDTH = 40;
    public final static int MAX_FIELD_HEIGHT = 40;

    public interface SizeListener {
        public void onGameSizeChanged();
    }

    public interface ActionListener {
        public void onGameMoleMove();

        public void onGameUserWon();
    }

    public Game(String mazeName) {
        this.mazeName = mazeName;
    }

    public String getMazeName() {
        return mazeName;
    }

    public void addSizeListener(SizeListener listener) {
        if (listener != null) {
            sizeListeners.add(listener);
        }
    }

    public void addActionListener(ActionListener listener) {
        if (listener != null) {
            actionListeners.add(listener);
        }
    }

    public Dimension getFieldSize() {
        return (field == null) ? new Dimension(0, 0) : field.getSize();
    }

    public void setFieldSize(Dimension newSize) {
        final Dimension prevSize = (field == null) ? null : field.getSize();
        if (field == null) {
            field = new Field();
        }
        field.setSize(newSize);
        if (prevSize == null || !prevSize.equals(field.getSize())) {
            for (SizeListener listener : sizeListeners) {
                listener.onGameSizeChanged();
            }
        }
        removeOutsideObject();
    }

    public Cell getCell(int columnIndex, int rowIndex) {
        return (field == null) ? null : field.at(columnIndex, rowIndex);
    }

    public Point getMolePosition() {
        return currentState.moleLocation;
    }

    public void setMolePosition(Point point) {
        currentState.moleLocation = point;
    }

    public List<Point> getBoxes() {
        return currentState.boxes;
    }

    public void setBoxPoint(Point point, boolean enable) {
        setItem(currentState.boxes, point, enable);
    }

    public List<Point> getTargetPoints() {
        return targetPoints;
    }

    public void setTargetPoint(Point point, boolean enable) {
        setItem(targetPoints, point, enable);
    }

    public byte[] getBinaryData() {
        final Dimension fieldSize = getFieldSize();
        byte[] result = new byte[(fieldSize.width + 1) * fieldSize.height];

        for (int y = 0; y < fieldSize.height; ++y) {
            for (int x = 0; x < fieldSize.width; ++x) {
                result[(fieldSize.width + 1) * y + x] = getDataByte(x, y);
            }
            result[(fieldSize.width + 1) * y + fieldSize.width] = '\n';
        }

        return result;
    }

    public byte getDataByte(int x, int y) {
        Cell cell = getCell(x, y);
        if ((cell == null) || (cell.type == Cell.Type.NULL)) {
            return CellDataByte.EMPTY.toByte();
        }
        if (cell.type == Cell.Type.WALL) {
            return CellDataByte.WALL.toByte();
        }
        if ((currentState.moleLocation != null) && (currentState.moleLocation.x == x)
                && (currentState.moleLocation.y == y)) {
            return CellDataByte.MOLE.toByte();
        }
        final boolean hasBox = currentState.boxes.contains(new Point(x, y));
        final boolean hasTargetPoint = targetPoints.contains(new Point(x, y));
        if (hasBox && hasTargetPoint) {
            return CellDataByte.ACTIVE_BOX.toByte();
        } else if (hasBox) {
            return CellDataByte.INACTIVE_BOX.toByte();
        } else if (hasTargetPoint) {
            return CellDataByte.TARGET_POINT.toByte();
        }

        return CellDataByte.EMPTY_FLOOR.toByte();
    }

    public boolean loadGame(String[] lines) {
        if (lines == null || lines.length == 0) {
            return false;
        }

        final int maxLineLength = findMaxLineLength(lines);
        for (int i = 0; i < lines.length; ++i) {
            if (lines[i].length() < maxLineLength) {
                lines[i] += String.valueOf('E').repeat(maxLineLength - lines[i].length());
            }
        }

        field = new Field();
        field.setSize(maxLineLength, lines.length);
        currentState = new GameState();
        for (int y = 0; y < lines.length; ++y) {
            String line = lines[y];
            for (int x = 0; x < maxLineLength; ++x) {
                Cell cell = field.at(x, y);
                final CellDataByte cellDataByte = CellDataByte.fromChar(line.charAt(x));
                switch (cellDataByte) {
                    case EMPTY:
                        cell.type = Cell.Type.NULL;
                        break;
                    case WALL:
                        cell.type = Cell.Type.WALL;
                        break;
                    case EMPTY_FLOOR:
                        cell.type = Cell.Type.FLOOR;
                        break;
                    case MOLE:
                        currentState.moleLocation = new Point(x, y);
                        cell.type = Cell.Type.FLOOR;
                        break;
                    case INACTIVE_BOX:
                        currentState.boxes.add(new Point(x, y));
                        cell.type = Cell.Type.FLOOR;
                        break;
                    case ACTIVE_BOX:
                        currentState.boxes.add(new Point(x, y));
                        targetPoints.add(new Point(x, y));
                        cell.type = Cell.Type.FLOOR;
                        break;
                    case TARGET_POINT:
                        targetPoints.add(new Point(x, y));
                        cell.type = Cell.Type.FLOOR;
                        break;
                    default:
                        cell.type = Cell.Type.NULL;
                }
            }
        }

        history.add(currentState);

        return true;
    }

    public boolean loadGame(List<String> lines) {
        if (lines == null) {
            return false;
        }
        String[] lineArray = new String[lines.size()];
        lines.toArray(lineArray);
        return loadGame(lineArray);
    }

    public void checkIfUserWon() {
        PointSet boxSet = new PointSet(currentState.boxes);
        for (Point point : targetPoints) {
            if (!boxSet.has(point)) {
                return;
            }
        }
        fireUserWon();
    }

    public boolean canUndo() {
        return history.canUndo();
    }

    public boolean undo() {
        if (!canUndo()) {
            return false;
        }

        GameState prevState = currentState;
        GameState state = history.undo();
        if (state == null) {
            return false;
        }
        currentState = state;
        --stepCount;
        if (boxChanged(prevState, state)) {
            --stepWithLoadCount;
        }
        fireMoleMove();
        return true;
    }

    public boolean canRedo() {
        return history.canRedo();
    }

    public boolean redo() {
        if (!canRedo()) {
            return false;
        }
        GameState prevState = currentState;
        GameState state = history.redo();
        if (state == null) {
            return false;
        }
        currentState = state;
        ++stepCount;
        if (boxChanged(prevState, state)) {
            ++stepWithLoadCount;
        }
        fireMoleMove();
        return true;
    }

    public int getStepCount() {
        return stepCount;
    }

    public int getStepWithLoadCount() {
        return stepWithLoadCount;
    }

    private Field field = null;
    private GameState currentState = new GameState();
    private StepHistory history = new StepHistory();
    private List<Point> targetPoints = new ArrayList<Point>();
    private List<SizeListener> sizeListeners = new ArrayList<SizeListener>();
    private List<ActionListener> actionListeners = new ArrayList<ActionListener>();
    private int stepCount = 0;
    private int stepWithLoadCount = 0;
    private String mazeName = "";

    protected boolean tryToMoveMole(MoleMovementDirection direction) {
        if (!canMoveMole(direction)) {
            return false;
        }
        moveMole(direction);
        return true;
    }

    private int findMaxLineLength(String[] lines) {
        int result = 0;
        for (String line : lines) {
            result = Math.max(result, line.length());
        }
        return result;
    }

    static Point convertDirectionToPoint(MoleMovementDirection direction) {
        switch (direction) {
            case UP:
                return new Point(0, -1);
            case DOWN:
                return new Point(0, 1);
            case LEFT:
                return new Point(-1, 0);
            case RIGHT:
                return new Point(1, 0);
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
        Point newMoleLocation = new Point(currentState.moleLocation.x + offset.x,
                currentState.moleLocation.y + offset.y);
        if (!floor.has(newMoleLocation)) {
            return false;
        }

        // Do we have a box on this cell?
        boolean hasBox = false;
        for (int index = 0; index < currentState.boxes.size(); ++index) {
            Point box = currentState.boxes.get(index);
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
        PointSet boxSet = new PointSet(currentState.boxes);
        if (boxSet.has(newBoxLocation)) {
            return false;
        }

        return true;
    }

    private void moveMole(MoleMovementDirection direction) {
        if (!canMoveMole(direction)) {
            return;
        }
        ++stepCount;
        Point offset = convertDirectionToPoint(direction);
        Point newMoleLocation = new Point(currentState.moleLocation.x + offset.x,
                currentState.moleLocation.y + offset.y);

        for (int index = 0; index < currentState.boxes.size(); ++index) {
            Point box = currentState.boxes.get(index);
            if (box.equals(newMoleLocation)) {
                box.translate(offset.x, offset.y);
                ++stepWithLoadCount;
                break;
            }
        }

        currentState.moleLocation = newMoleLocation;
        history.add(currentState);
        fireMoleMove();

        checkIfUserWon();
    }

    private static void setItem(List<Point> points, Point point, boolean enable) {
        final boolean contained = points.contains(point);
        if (contained == enable) {
            return;
        }
        if (enable) {
            points.add(point);
        } else {
            points.remove(point);
        }
    }

    static boolean isOutside(Dimension fieldSize, Point point) {
        if (point == null) {
            return false;
        }
        return (point.x >= fieldSize.width) || (point.y >= fieldSize.height);
    }

    private void removeOutsideObject() {
        final Dimension fieldSize = getFieldSize();
        if (isOutside(fieldSize, currentState.moleLocation)) {
            currentState.moleLocation = null;
        }
        currentState.boxes.removeIf(box -> isOutside(fieldSize, box));
        targetPoints.removeIf(point -> isOutside(fieldSize, point));
    }

    private void fireMoleMove() {
        for (ActionListener listener : actionListeners) {
            listener.onGameMoleMove();
        }
    }

    private void fireUserWon() {
        for (ActionListener listener : actionListeners) {
            listener.onGameUserWon();
        }
    }

    private static boolean boxChanged(GameState state1, GameState state2) {
        if (state1 == null || state2 == null) {
            return true;
        }
        HashSet<Point> boxSet1 = new HashSet<Point>(state1.boxes);
        HashSet<Point> boxSet2 = new HashSet<Point>(state2.boxes);
        return !Objects.equals(boxSet1, boxSet2);
    }

    private enum CellDataByte {
        UNDEFINED('\n'), EMPTY('E'), WALL('W'), EMPTY_FLOOR('F'), MOLE('M'), INACTIVE_BOX('B'), ACTIVE_BOX('A'),
        TARGET_POINT('T');

        public byte toByte() {
            return (byte) val;
        }

        static public CellDataByte fromChar(char ch) {
            switch (ch) {
                case 'E':
                    return CellDataByte.EMPTY;
                case 'W':
                    return CellDataByte.WALL;
                case 'F':
                    return CellDataByte.EMPTY_FLOOR;
                case 'M':
                    return CellDataByte.MOLE;
                case 'B':
                    return CellDataByte.INACTIVE_BOX;
                case 'A':
                    return CellDataByte.ACTIVE_BOX;
                case 'T':
                    return CellDataByte.TARGET_POINT;
                default:
                    return CellDataByte.UNDEFINED;
            }
        }

        private char val = '\n';

        CellDataByte(char c) {
            val = c;
        }
    }
}
