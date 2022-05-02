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

package controls;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@ControlBase} is base class for controls.
 * @author olegshchepilov
 *
 */
public abstract class ControlBase {
	public ControlBase(String controlType, String controlId) {
		type = controlType;
		id = controlId;
	}
	
	public interface ClickListener {
		public void onControlClick(ControlBase control);
	}
	public void addClickListener(ClickListener listener) {
		if (listener != null) {
			clickListeners.add(listener);			
		}
	}

	public String getType() {
		return type;
	}
	public String getId() {
		return id;
	}
	public int getIdealHeight() {
		return 0;
	}
	public int getIdealWidth() {
		return 0;
	}
	public void setPosition(Rectangle rect) {
		position = rect;
		onPositionChanged();
	}
	public Rectangle getPosition() {
		return position == null ? null : (Rectangle)position.clone();
	}
	public void paint(Graphics graphics) {}
	public int onMouseMove(Point mousePoistion) 
	{ 
		return handCursor ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR; 
	}
	public void onMouseClick(Point mousePoistion) {
		if (position == null || mousePoistion == null) {
			return;
		}
		if ((mousePoistion.x < 0) || (mousePoistion.x > position.width)) {
			return;
		}
		if ((mousePoistion.y < 0) || (mousePoistion.y > position.height)) {
			return;
		}
		for (ClickListener listener : clickListeners) {
			listener.onControlClick(this);
		}
	}
	protected void onPositionChanged() {}
	
	private String type = null;
	private String id = "";
	private List<ClickListener> clickListeners = new ArrayList<ClickListener>();
	protected Rectangle position = null;
	protected boolean handCursor = false;
}
