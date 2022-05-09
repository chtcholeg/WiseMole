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

package editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import common.Lang;
import controls.ButtonControl;
import controls.ControlBase;
import controls.ImageControl;
import controls.LabelControl;
import controls.NumericLeftRightControl;
import controls.SpaceControl;
import game.Cell;
import game.Game;
import game.GamePanelBase;
import utils.ControlPlacer;
import utils.Margins;
import utils.PanelUtils;
import utils.RectangleUtils;

/**
 * The {@EditorPanel} is a panel that is responsible editing game board
 *
 * @author olegshchepilov
 *
 */

public class EditorPanel extends GamePanelBase
        implements MouseListener, MouseMotionListener, NumericLeftRightControl.Callback, ControlBase.ClickListener {
    public interface Callback {
        public void onEditorPanelCommandExit(Game game);
    }

    public EditorPanel(Callback editorPanelCallback) {
        toolPanelWidth = NumericLeftRightControl.getImageWidth() + 2 * PADDING;
        setMargins(new Margins(toolPanelWidth, 0, 0, 0));
        callback = editorPanelCallback;

        setGame(new Game());
        getGame().setFieldSize(new Dimension(DEFAULT_FIELD_WIDTH, DEFAULT_FIELD_HEIGHT));

        initControls();
        updateControlsPostions();
    }

    @Override
    public void onResize() {
        updateControlsPostions();
        super.onResize();
    }

    @Override
    public MouseListener mouseListener() {
        return this;
    }

    @Override
    public MouseMotionListener mouseMotionListener() {
        return this;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        processClick(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        processClick(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point mousePos = PanelUtils.getRelativePoint(e, this);
        final int index = getIndexOfControlUnderPoint(mousePos);
        if (index == -1) {
            setCursor(Cursor.DEFAULT_CURSOR);
        } else {
            ControlBase control = controls.get(index).control;
            final Rectangle position = control.getPosition();
            mousePos.translate(-position.x, -position.y);
            setCursor(control.onMouseMove(mousePos));
        }
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (getGame() != null) {
            drawToolPanel(graphics);
            drawField(graphics);
            drawFieldGrid(graphics);
        }
    }

    @Override
    public void onNumericControlValueChanged(String controlId, int value) {
        Dimension fieldSize = (Dimension) getGame().getFieldSize().clone();
        if (controlId == HEIGHT_CONTROL_ID) {
            fieldSize.height = value;
        } else if (controlId == WIDTH_CONTROL_ID) {
            fieldSize.width = value;
        }
        getGame().setFieldSize(fieldSize);
        repaint();
    }

    @Override
    public void onControlClick(ControlBase control) {
        if (control.getType() == ImageControl.TYPE) {
            switch (control.getId()) {
                case MOLE_CONTROL_ID:
                    selectedFieldType = FieldType.MOLE;
                    break;
                case BOX_ACTIVE_CONTROL_ID:
                    selectedFieldType = FieldType.BOX_ACTIVE;
                    break;
                case BOX_INACTIVE_CONTROL_ID:
                    selectedFieldType = FieldType.BOX_INACTIVE;
                    break;
                case TARGET_POINT_CONTROL_ID:
                    selectedFieldType = FieldType.TARGET_POINT;
                    break;
                case WALL_CONTROL_ID:
                    selectedFieldType = FieldType.WALL;
                    break;
                case FLOOR_CONTROL_ID:
                    selectedFieldType = FieldType.FLOOR;
                    break;
                default:
                    selectedFieldType = FieldType.NULL;
            }

            List<ControlBase> imageControls = getControlsByType(ImageControl.TYPE);
            for (ControlBase imageControl : imageControls) {
                ((ImageControl) imageControl).setSelection(imageControl.getId() == control.getId());
            }
            repaint();
        } else if (control.getType() == ButtonControl.TYPE) {
            switch (control.getId()) {
                case SAVE_BUTTON_CONTROL_ID:
                    try {
                        onSave();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case EXIT_BUTTON_CONTROL_ID:
                    if (callback != null) {
                        callback.onEditorPanelCommandExit(getGame());
                    }
                    break;
            }
        }
    }

    private void drawFieldGrid(Graphics graphics) {
        renderIfRequired();
        final Dimension fieldSize = getGame().getFieldSize();
        final Dimension cellSize = renderDetails.cellSize;

        int y = renderDetails.fieldArea.y;
        for (int rowIndex = 0; rowIndex <= fieldSize.height; ++rowIndex) {
            drawDashedLine(graphics, renderDetails.fieldArea.x, y,
                    renderDetails.fieldArea.x + renderDetails.fieldArea.width, y);
            y += cellSize.height;
        }

        int x = renderDetails.fieldArea.x;
        for (int columnIndex = 0; columnIndex <= fieldSize.width; ++columnIndex) {
            drawDashedLine(graphics, x, renderDetails.fieldArea.y, x,
                    renderDetails.fieldArea.y + renderDetails.fieldArea.height);
            x += cellSize.width;
        }
    }

    private static void drawDashedLine(Graphics graphics, int x1, int y1, int x2, int y2) {
        Graphics2D graphics2d = (Graphics2D) graphics.create();
        Color color = new Color(0, 0, 0, 50);
        graphics2d.setColor(color);
        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 2 }, 0);
        graphics2d.setStroke(dashed);
        graphics2d.drawLine(x1, y1, x2, y2);
        graphics2d.dispose();
    }

    private void drawToolPanel(Graphics graphics) {
        for (ControlInfo controlInfo : controls) {
            controlInfo.control.paint(graphics);
        }
    }

    private Rectangle calcToolPanelRect() {
        return new Rectangle(0, 0, toolPanelWidth, getSize().height);
    }

    private void initControls() {
        addTopSpacer(PADDING);
        addClickableTopImage("mole.png", MOLE_CONTROL_ID);

        addTopSpacer(PADDING);
        addClickableTopImage("box_active.png", BOX_ACTIVE_CONTROL_ID);

        addTopSpacer(PADDING);
        addClickableTopImage("box_inactive.png", BOX_INACTIVE_CONTROL_ID);

        addTopSpacer(PADDING);
        addClickableTopImage("target_point.png", TARGET_POINT_CONTROL_ID);

        addTopSpacer(PADDING);
        addClickableTopImage("wall.png", WALL_CONTROL_ID);

        addTopSpacer(PADDING);
        addClickableTopImage("floor.png", FLOOR_CONTROL_ID);

        addTopSpacer(PADDING);
        addTopLabel(Lang.Res.HEIGHT);
        addTopSpacer(PADDING / 2);
        addTopNumericLeftRightControl(1, Game.MAX_FIELD_HEIGHT, DEFAULT_FIELD_HEIGHT, HEIGHT_CONTROL_ID);

        addTopSpacer(PADDING);
        addTopLabel(Lang.Res.WIDTH);
        addTopSpacer(PADDING / 2);
        addTopNumericLeftRightControl(1, Game.MAX_FIELD_WIDTH, DEFAULT_FIELD_WIDTH, WIDTH_CONTROL_ID);

        addBottomSpacer(PADDING);
        addClickableBottomButton(Lang.Res.SAVE, SAVE_BUTTON_CONTROL_ID);

        addBottomSpacer(PADDING);
        addClickableBottomButton(Lang.Res.EXIT, EXIT_BUTTON_CONTROL_ID);
    }

    private void addTopSpacer(int padding) {
        controls.add(new ControlInfo(new SpaceControl(PADDING, padding), true));
    }

    private void addBottomSpacer(int padding) {
        controls.add(new ControlInfo(new SpaceControl(PADDING, padding), false));
    }

    private void addClickableTopImage(String resourceId, String controlId) {
        addClickableControl(new ControlInfo(new ImageControl(resourceId, controlId, true), true));
    }

    private void addTopLabel(Lang.Res stringId) {
        controls.add(new ControlInfo(new LabelControl(Lang.get(stringId), LabelControl.Alignment.CENTER), true));
    }

    private void addTopNumericLeftRightControl(int min, int max, int initValue, String controlId) {
        controls.add(new ControlInfo(new NumericLeftRightControl(min, max, initValue, controlId, this)));
    }

    private void addClickableBottomButton(Lang.Res stringId, String controlId) {
        addClickableControl(new ControlInfo(new ButtonControl(Lang.get(stringId), controlId), false));
    }

    private void addClickableControl(ControlInfo controlInfo) {
        controlInfo.control.addClickListener(this);
        controls.add(controlInfo);
    }

    private void updateControlsPostions() {
        Rectangle toolPanelRect = calcToolPanelRect();
        RectangleUtils.deflateRect(toolPanelRect, PADDING, 2 * PADDING);
        ControlPlacer placer = new ControlPlacer(toolPanelRect);

        for (ControlInfo controlInfo : controls) {
            ControlBase control = controlInfo.control;
            if (controlInfo.placeToTop) {
                control.setPosition(placer.addTop(control.getIdealWidth(), control.getIdealHeight()));
            }
        }

        ListIterator<ControlInfo> controlIterator = controls.listIterator(controls.size());
        while (controlIterator.hasPrevious()) {
            ControlInfo controlInfo = controlIterator.previous();
            if (!controlInfo.placeToTop) {
                ControlBase control = controlInfo.control;
                control.setPosition(placer.addBottom(control.getIdealWidth(), control.getIdealHeight()));
            }
        }

        revalidate();
        repaint();
    }

    private int getIndexOfControlUnderPoint(Point point) {
        for (int index = 0; index < controls.size(); ++index) {
            final ControlBase control = controls.get(index).control;
            final Rectangle rect = control.getPosition();
            if (rect.contains(point)) {
                return index;
            }
        }
        return -1;
    }

    private void setCursor(int cursorId) {
        setCursor(new Cursor(cursorId));
    }

    private List<ControlBase> getControlsByType(String type) {
        List<ControlBase> result = new ArrayList<ControlBase>();
        for (ControlInfo controlinfo : controls) {
            if (controlinfo.control.getType() == type) {
                result.add(controlinfo.control);
            }
        }
        return result;
    }

    private void applySelectedCell(Point cellCoordinates, FieldType fieldType) {
        Game game = getGame();
        if (game == null || cellCoordinates == null) {
            return;
        }
        Cell cell = game.getCell(cellCoordinates.x, cellCoordinates.y);
        Point molePos = game.getMolePosition();
        final boolean isMoleCell = (molePos != null) && cellCoordinates.equals(molePos);
        Point newMolePosition = game.getMolePosition();
        boolean addTargetPoint = false;
        boolean addBox = false;
        switch (fieldType) {
            case MOLE:
                cell.type = Cell.Type.FLOOR;
                newMolePosition = cellCoordinates;
                break;
            case BOX_ACTIVE:
                cell.type = Cell.Type.FLOOR;
                addTargetPoint = true;
                addBox = true;
                newMolePosition = isMoleCell ? null : newMolePosition;
                break;
            case BOX_INACTIVE:
                cell.type = Cell.Type.FLOOR;
                addBox = true;
                newMolePosition = isMoleCell ? null : newMolePosition;
                break;
            case TARGET_POINT:
                cell.type = Cell.Type.FLOOR;
                addTargetPoint = true;
                newMolePosition = isMoleCell ? null : newMolePosition;
                break;
            case WALL:
                cell.type = Cell.Type.WALL;
                newMolePosition = isMoleCell ? null : newMolePosition;
                break;
            case FLOOR:
                cell.type = Cell.Type.FLOOR;
                break;
            case NULL:
                cell.type = Cell.Type.NULL;
                newMolePosition = isMoleCell ? null : newMolePosition;
                break;
        }
        game.setTargetPoint(cellCoordinates, addTargetPoint);
        game.setBoxPoint(cellCoordinates, addBox);
        game.setMolePosition(newMolePosition);

        repaint();
    }

    private void processClick(MouseEvent e) {
        Point mousePos = PanelUtils.getRelativePoint(e, this);
        final int index = getIndexOfControlUnderPoint(mousePos);
        if (index == -1) {
            final Point coordinates = findCellUnderPoint((Point) mousePos.clone());
            if (coordinates != null) {
                FieldType fieldType = (e.getButton() == MouseEvent.BUTTON1) ? selectedFieldType : FieldType.NULL;
                applySelectedCell(coordinates, fieldType);
            }
        } else {
            ControlBase control = controls.get(index).control;
            final Rectangle position = control.getPosition();
            mousePos.translate(-position.x, -position.y);
            control.onMouseClick(mousePos);
        }
    }

    private void onSave() throws IOException {
        LevelSaver.save(this, getGame());
    }

    private enum FieldType {
        MOLE, BOX_ACTIVE, BOX_INACTIVE, TARGET_POINT, WALL, FLOOR, NULL
    }

    private class ControlInfo {
        public ControlBase control = null;
        public boolean placeToTop = true;

        public ControlInfo(ControlBase control) {
            this(control, true);
        }

        public ControlInfo(ControlBase control, boolean top) {
            this.control = control;
            this.placeToTop = top;
        }
    }

    private Callback callback = null;
    private List<ControlInfo> controls = new ArrayList<ControlInfo>();
    private int toolPanelWidth = 100;
    private FieldType selectedFieldType = FieldType.NULL;
    private static final int DEFAULT_FIELD_WIDTH = 25;
    private static final int DEFAULT_FIELD_HEIGHT = 15;
    private static final String MOLE_CONTROL_ID = "MoleImageControlId";
    private static final String BOX_ACTIVE_CONTROL_ID = "BoxActiveImageControlId";
    private static final String BOX_INACTIVE_CONTROL_ID = "BoxInactiveImageControlId";
    private static final String TARGET_POINT_CONTROL_ID = "TargetPointImageControlId";
    private static final String WALL_CONTROL_ID = "WallImageControlId";
    private static final String FLOOR_CONTROL_ID = "FloorImageControlId";
    private static final String HEIGHT_CONTROL_ID = "HeightControlId";
    private static final String WIDTH_CONTROL_ID = "WidthControlId";
    private static final String SAVE_BUTTON_CONTROL_ID = "SaveButton";
    private static final String EXIT_BUTTON_CONTROL_ID = "ExitButton";
    private static final long serialVersionUID = 1L;
}
