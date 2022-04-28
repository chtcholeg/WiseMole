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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import common.*;
import game.*;
import utils.*;

/**
 * The {@EditorPanel} is a panel that is responsible editing game board
 *
 * @author olegshchepilov
 *
 */

public class EditorPanel 
		extends 
			GamePanelBase 
		implements 
			MouseListener, 
			MouseMotionListener,
			NumericLeftRightControl.Callback,
			ControlBase.ClickListener
{
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

	public void onResize() {
		updateControlsPostions();
		super.onResize();
	}

	@Override
	public MouseListener mouseListener() { return this; }
	@Override
	public MouseMotionListener mouseMotionListener() { return this; }
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
    public void mousePressed(MouseEvent e) {
		Point mousePos = PanelUtils.getRelativePoint(e, this);
		final int index = getIndexOfControlUnderPoint(mousePos);
		if (index == -1) {
			final Point coordinates = findCellUnderPoint((Point)mousePos.clone());
			if (coordinates != null) {
				applySelectedCell(coordinates, selectedFieldType);
			}
		} else {
			ControlBase control = controls.get(index);
			final Rectangle position = control.getPosition();
			mousePos.translate(-position.x, -position.y);
			control.onMouseClick(mousePos);
		}		
	}
	@Override
    public void mouseReleased(MouseEvent e) {}
	@Override
    public void mouseEntered(MouseEvent e) {}
	@Override
    public void mouseExited(MouseEvent e) {}
	@Override
    public void mouseDragged(MouseEvent e) {}
	@Override
    public void mouseMoved(MouseEvent e) {
		Point mousePos = PanelUtils.getRelativePoint(e, this);
		final int index = getIndexOfControlUnderPoint(mousePos);
		if (index == -1) {
			setCursor(Cursor.DEFAULT_CURSOR);
		} else {
			ControlBase control = controls.get(index);
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
			switch(control.getId()) {
				case MOLE_CONTROL_ID: selectedFieldType = FieldType.MOLE; break;
				case BOX_ACTIVE_CONTROL_ID: selectedFieldType = FieldType.BOX_ACTIVE; break;
				case BOX_INACTIVE_CONTROL_ID: selectedFieldType = FieldType.BOX_INACTIVE; break;
				case TARGET_POINT_CONTROL_ID: selectedFieldType = FieldType.TARGET_POINT; break;
				case WALL_CONTROL_ID: selectedFieldType = FieldType.WALL; break;
				case FLOOR_CONTROL_ID: selectedFieldType = FieldType.FLOOR; break;
				default:
					selectedFieldType = FieldType.NULL;
			}
						
			List<ControlBase> imageControls = getControlsByType(ImageControl.TYPE);
			for (ControlBase imageControl : imageControls) {
				((ImageControl) imageControl).setSelection(imageControl.getId() == control.getId());
			}
		}
		repaint();
	}

	private void drawFieldGrid(Graphics graphics) {
		renderIfRequired();
		final Dimension fieldSize = getGame().getFieldSize();
		final Dimension cellSize = renderDetails.cellSize;

		int y = renderDetails.fieldArea.y;
		for (int rowIndex = 0; rowIndex <= fieldSize.height; ++rowIndex) {
			drawDashedLine(graphics, renderDetails.fieldArea.x, y, renderDetails.fieldArea.x + renderDetails.fieldArea.width, y);
			y += cellSize.height;
		}

		int x = renderDetails.fieldArea.x;
		for (int columnIndex = 0; columnIndex <= fieldSize.width; ++columnIndex) {
			drawDashedLine(graphics, x, renderDetails.fieldArea.y, x, renderDetails.fieldArea.y + renderDetails.fieldArea.height);
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
		for (ControlBase control : controls) {
			control.paint(graphics);
		}
	}

	private Rectangle calcToolPanelRect() {
		return new Rectangle(0, 0, toolPanelWidth, getSize().height);
	}

	private void initControls() {
		controls.add(new SpaceControl(PADDING, PADDING));
		addClickableControl(new ImageControl("mole.png", MOLE_CONTROL_ID, true));

		controls.add(new SpaceControl(PADDING, PADDING));
		addClickableControl(new ImageControl("box_active.png", BOX_ACTIVE_CONTROL_ID, true));

		controls.add(new SpaceControl(PADDING, PADDING));
		addClickableControl(new ImageControl("box_inactive.png", BOX_INACTIVE_CONTROL_ID, true));

		controls.add(new SpaceControl(PADDING, PADDING));
		addClickableControl(new ImageControl("target_point.png", TARGET_POINT_CONTROL_ID, true));

		controls.add(new SpaceControl(PADDING, PADDING));
		addClickableControl(new ImageControl("wall.png", WALL_CONTROL_ID, true));

		controls.add(new SpaceControl(PADDING, PADDING));
		addClickableControl(new ImageControl("floor.png", FLOOR_CONTROL_ID, true));

		controls.add(new SpaceControl(PADDING, PADDING));
		controls.add(new LabelControl(Lang.get(Lang.Res.HEIGHT), LabelControl.Alignment.CENTER));
		controls.add(new SpaceControl(PADDING, PADDING / 2));
		controls.add(new NumericLeftRightControl(1, Game.MAX_FIELD_HEIGHT, DEFAULT_FIELD_HEIGHT, HEIGHT_CONTROL_ID, this));
		
		controls.add(new SpaceControl(PADDING, PADDING));
		controls.add(new LabelControl(Lang.get(Lang.Res.WIDTH), LabelControl.Alignment.CENTER));
		controls.add(new SpaceControl(PADDING, PADDING / 2));
		controls.add(new NumericLeftRightControl(1, Game.MAX_FIELD_WIDTH, DEFAULT_FIELD_WIDTH, WIDTH_CONTROL_ID, this));
	}
	
	private void addClickableControl(ControlBase control) {
		control.addClickListener(this);
		controls.add(control);
	}

	private void updateControlsPostions() {
		Rectangle toolPanelRect = calcToolPanelRect();
		RectangleUtils.deflateRect(toolPanelRect, PADDING, 2 * PADDING);
		ControlPlacer placer = new ControlPlacer(toolPanelRect);

		ListIterator<ControlBase> controlIterator = controls.listIterator(controls.size());
		while (controlIterator.hasPrevious()) {
			ControlBase control = (ControlBase) controlIterator.previous();
			control.setPosition(placer.addBottom(control.getIdealWidth(), control.getIdealHeight()));
		}
		
		revalidate();
		repaint();
	}
	
	private int getIndexOfControlUnderPoint(Point point) {
		for (int index = 0; index < controls.size(); ++index) {
			final ControlBase control = controls.get(index);
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
		for (ControlBase control : controls) {
			if (control.getType() == type) {
				result.add(control);
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
		}
		game.setTargetPoint(cellCoordinates, addTargetPoint);
		game.setBoxPoint(cellCoordinates, addBox);
		game.setMolePosition(newMolePosition);
		
		repaint();
	}
	
	private enum FieldType {
		MOLE,
		BOX_ACTIVE,
		BOX_INACTIVE,
		TARGET_POINT,
		WALL,
		FLOOR,
		NULL
	}
	
	private Callback callback = null;
	private List<ControlBase> controls = new ArrayList<ControlBase>();
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
	private static final long serialVersionUID = 1L;
}
