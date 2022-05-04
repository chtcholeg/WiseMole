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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import common.PanelBase;
import utils.ImageStorage;
import utils.Margins;
import utils.PointSet;
import utils.RectangleUtils;

/**
 * The {@GamePanelBase} is base class for game panels (GamePanel and
 * EditorPanel) used in the application.
 * 
 * @author olegshchepilov
 *
 */

public class GamePanelBase extends PanelBase implements Game.SizeListener

{
    public GamePanelBase() {
    }

    @Override
    public void onResize() {
        renderDetails = null;
        repaint();
    }

    @Override
    public void onGameSizeChanged() {
        renderDetails = null;
        repaint();
    }

    public void setGame(Game newGame) {
        if (game == newGame) {
            return;
        }
        game = newGame;
        if (game == null) {
            return;
        }
        game.addSizeListener(this);
    }

    public Game getGame() {
        return game;
    }

    public void setMargins(Margins margins) {
        this.margins = margins;
    }

    protected Point findCellUnderPoint(Point point) {
        if (game == null) {
            return null;
        }
        renderIfRequired();
        if (!renderDetails.fieldArea.contains(point)) {
            return null;
        }
        final Dimension fieldSize = game.getFieldSize();
        if (fieldSize.width <= 0 || fieldSize.height <= 0) {
            return null;
        }
        point.x -= renderDetails.fieldArea.x;
        point.y -= renderDetails.fieldArea.y;

        final int columnIndex = (int) ((double) point.x / (double) renderDetails.fieldArea.width * fieldSize.width);
        final int rowIndex = (int) ((double) point.y / (double) renderDetails.fieldArea.height * fieldSize.height);

        if (columnIndex < 0 || columnIndex >= fieldSize.width) {
            return null;
        }
        if (rowIndex < 0 || rowIndex >= fieldSize.height) {
            return null;
        }
        return new Point(columnIndex, rowIndex);
    }

    protected void drawField(Graphics graphics) {
        if (game == null) {
            return;
        }
        renderIfRequired();

        RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        Graphics2D graphics2d = (Graphics2D) graphics;
        graphics2d.setRenderingHints(renderingHints);

        // -- Background
        final Dimension fieldSize = game.getFieldSize();
        for (int rowIndex = 0; rowIndex < fieldSize.height; ++rowIndex) {
            for (int columnIndex = 0; columnIndex < fieldSize.width; ++columnIndex) {
                Rectangle cellRect = calcCellRect(columnIndex, rowIndex);
                drawCell(graphics2d, cellRect, game.getCell(columnIndex, rowIndex));
            }
        }

        // -- Foreground
        Point molePosition = game.getMolePosition();
        List<Point> boxes = new ArrayList<Point>(game.getBoxes());
        List<Point> targetPoints = new ArrayList<Point>(game.getTargetPoints());
        List<Point> activeBoxes = extractActiveBoxes(boxes, targetPoints);
        Rectangle cellRect = null;
        // Active boxes
        for (Point activeBox : activeBoxes) {
            cellRect = calcCellRect(activeBox.x, activeBox.y);
            drawImage(graphics2d, cellRect, "box_active.png");
        }
        // Inactive boxes
        for (Point box : boxes) {
            cellRect = calcCellRect(box.x, box.y);
            drawImage(graphics2d, cellRect, "box_inactive.png");
        }
        // Target points
        for (Point targetPoint : targetPoints) {
            cellRect = calcCellRect(targetPoint.x, targetPoint.y);
            drawImage(graphics2d, cellRect, "target_point.png");
        }
        // Mole
        if (isValidPosition(fieldSize, molePosition)) {
            cellRect = calcCellRect(molePosition.x, molePosition.y);
            drawImage(graphics2d, cellRect, "mole.png");
        }
    }

    static private Dimension calcCellSize(Dimension fieldSize, Rectangle workingRect) {
        return new Dimension(workingRect.width / fieldSize.width, workingRect.height / fieldSize.height);

    }

    private Rectangle calcCellRect(int x, int y) {
        return new Rectangle(renderDetails.fieldArea.x + renderDetails.cellSize.width * x,
                renderDetails.fieldArea.y + renderDetails.cellSize.height * y, renderDetails.cellSize.width,
                renderDetails.cellSize.height);
    }

    static boolean isValidPosition(Dimension fieldSize, Point position) {
        if (fieldSize == null || position == null) {
            return false;
        }
        if (position.x < 0 || position.y < 0) {
            return false;
        }
        if (position.x >= fieldSize.width || position.y >= fieldSize.height) {
            return false;
        }
        return true;
    }

    static private List<Point> extractActiveBoxes(List<Point> boxes, List<Point> targetPoints) {
        List<Point> activeBoxes = new ArrayList<>();
        PointSet boxSet = new PointSet(boxes);
        PointSet targetPointSet = new PointSet(targetPoints);
        List<Point> newBoxes = new ArrayList<Point>();
        List<Point> newTargetPoints = new ArrayList<Point>();
        // 1. Active boxes + new box list
        for (Point box : boxes) {
            if (targetPointSet.has(box)) {
                activeBoxes.add(box);
            } else {
                newBoxes.add(box);
            }
        }
        // 2. Remove target points
        for (Point targetPoint : targetPoints) {
            if (!boxSet.has(targetPoint)) {
                newTargetPoints.add(targetPoint);
            }
        }
        // 3. Update data
        boxes.clear();
        boxes.addAll(newBoxes);
        targetPoints.clear();
        targetPoints.addAll(newTargetPoints);

        return activeBoxes;
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

    protected void renderIfRequired() {
        if (renderDetails != null) {
            return;
        }
        renderDetails = new RenderDetails();
        renderDetails.fieldArea = calcWorkingGameRect();
        renderDetails.cellSize = calcCellSize(game.getFieldSize(), renderDetails.fieldArea);
    }

    // Calculates work space for panel which has a field
    private Rectangle calcWorkingGameRect() {
        Dimension size = getSize();
        final double width = size.getWidth();
        final double height = size.getHeight();
        if (width <= 0.0 || height <= 0.0) {
            return null;
        }
        Rectangle workRect = new Rectangle(0, 0, (int) width, (int) height);
        RectangleUtils.deflateRect(workRect, margins);

        RectangleUtils.deflateRect(workRect, PADDING, PADDING);
        if (game != null) {
            final double workRectRatio = (double) workRect.width / (double) workRect.height;
            Dimension fieldSize = game.getFieldSize();
            if (fieldSize.width <= 0.0 || fieldSize.height <= 0.0) {
                return null;
            }
            // Calculating scale rate
            final double fieldRatio = (double) fieldSize.width / (double) fieldSize.height;
            final boolean scaleByWidth = (fieldRatio < workRectRatio);
            if (scaleByWidth) {
                final int newWorkRectWidth = (int) (workRect.getHeight() * fieldRatio);
                workRect.x = workRect.x + (workRect.width - newWorkRectWidth) / 2;
                workRect.width = newWorkRectWidth;
            } else {
                final int newWorkRectHeight = (int) (workRect.getWidth() / fieldRatio);
                workRect.y = workRect.y + (workRect.height - newWorkRectHeight) / 2;
                workRect.height = newWorkRectHeight;
            }
            // There are rounding errors
            final Dimension cellSize = calcCellSize(fieldSize, workRect);
            final int correctedWorkingRectWidth = cellSize.width * fieldSize.width;
            final int correctedWorkingRectHeight = cellSize.height * fieldSize.height;
            workRect.x = workRect.x + (workRect.width - correctedWorkingRectWidth) / 2;
            workRect.width = correctedWorkingRectWidth;
            workRect.y = workRect.y + (workRect.height - correctedWorkingRectHeight) / 2;
            workRect.height = correctedWorkingRectHeight;
        }

        return workRect;
    }

    protected class RenderDetails {
        public Rectangle fieldArea = null;
        public Dimension cellSize = null;
    }

    private Margins margins = new Margins();
    private Game game = null;
    protected final int PADDING = 10;
    protected RenderDetails renderDetails = null;
    private static final long serialVersionUID = 1L;
}
