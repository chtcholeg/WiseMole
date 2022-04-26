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

public class EditorPanel extends GamePanelBase implements MouseListener, MouseMotionListener {
	public interface Callback {
		public void onEditorPanelCommandExit(Game game);
	}

	public class ComponentListenerImpl implements ComponentListener {
		public ComponentListenerImpl(EditorPanel panel) {
			parentPanel = panel;
		}

		@Override
		public void componentResized(ComponentEvent componentEvent) {
			parentPanel.onResize();
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}

		@Override
		public void componentHidden(ComponentEvent e) {
		}

		EditorPanel parentPanel = null;
	}

	public EditorPanel(Callback editorPanelCallback) {
		toolPanelWidth = NumericLeftRightControl.getImageWidth() + 2 * PADDING;
		callback = editorPanelCallback;

		game = new Game();

		game.setFieldSize(new Dimension(DEFAULT_FIELD_WIDTH, DEFAULT_FIELD_HEIGHT));
		addComponentListener(new ComponentListenerImpl(this));

		initControls();
		updateControlsPostions();
	}

	public void onResize() {
		updateControlsPostions();
	}

	@Override
	public MouseListener mouseListener() { return this; }
	@Override
	public MouseMotionListener mouseMotionListener() { return this; }
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
    public void mousePressed(MouseEvent e) {}
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

		if (game != null) {
			drawToolPanel(graphics);

			final Rectangle gameWorkingRect = calcWorkingGameRect(game, toolPanelWidth, 0, 0, 0);
			if (gameWorkingRect != null) {
				drawField(graphics, game, gameWorkingRect);
				drawFieldGrid(graphics, game, gameWorkingRect);
			}
		}
	}

	private void drawFieldGrid(Graphics graphics, Game game, Rectangle workingRect) {
		final Dimension fieldSize = game.getFieldSize();
		final Dimension cellSize = calcCellSize(fieldSize, workingRect);

		int y = workingRect.y;
		for (int rowIndex = 0; rowIndex <= fieldSize.height; ++rowIndex) {
			drawDashedLine(graphics, workingRect.x, y, workingRect.x + workingRect.width, y);
			y += cellSize.height;
		}

		int x = workingRect.x;
		for (int columnIndex = 0; columnIndex <= fieldSize.width; ++columnIndex) {
			drawDashedLine(graphics, x, workingRect.y, x, workingRect.y + workingRect.height);
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
		controls.add(new LabelControl(Lang.get(Lang.Res.HEIGHT), LabelControl.Alignment.CENTER));
		controls.add(new SpaceControl(PADDING, PADDING / 2));
		controls.add(new NumericLeftRightControl(1, Game.MAX_FIELD_HEIGHT));
		
		controls.add(new SpaceControl(PADDING, PADDING));
		controls.add(new LabelControl(Lang.get(Lang.Res.WIDTH), LabelControl.Alignment.CENTER));
		controls.add(new SpaceControl(PADDING, PADDING / 2));
		controls.add(new NumericLeftRightControl(1, Game.MAX_FIELD_WIDTH));
	}

	private void updateControlsPostions() {
		Rectangle toolPanelRect = calcToolPanelRect();
		RectangleUtils.deflateRect(toolPanelRect, PADDING, 2 * PADDING);
		ControlPlacer placer = new ControlPlacer(toolPanelRect);

		ListIterator<ControlBase> controlIterator = controls.listIterator(controls.size());
		while (controlIterator.hasPrevious()) {
			ControlBase control = (ControlBase) controlIterator.previous();
			control.setPosition(placer.addBottom(control.getIdealHeight()));
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

	private Game game = null;
	private Callback callback = null;
	private List<ControlBase> controls = new ArrayList<ControlBase>();
	private int toolPanelWidth = 100;
	private static final int DEFAULT_FIELD_WIDTH = 25;
	private static final int DEFAULT_FIELD_HEIGHT = 15;
	private static final long serialVersionUID = 1L;
}
