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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import utils.ImageStorage;
import utils.RectangleUtils;

/**
 * The {@ButtonControl} is a button
 * @author olegshchepilov
 *
 */

public class ButtonControl extends ControlBase {
	final public static String TYPE = "Button";

	public ButtonControl(String text, String controlId) {
		super(TYPE, controlId);
		label.setText(text);	
		handCursor = true;
	}
	
	@Override
	public int getIdealHeight() {
		return middleImage.getHeight(null);
	}
	@Override
	public int getIdealWidth() {
		return 0;
	}
	@Override
	public void onPositionChanged() {
		Rectangle labelPosition = (Rectangle) position.clone();
		RectangleUtils.deflateRect(labelPosition, leftImage.getWidth(null), 0); 
		label.setPosition(labelPosition);
	}
	@Override
	public void paint(Graphics graphics) {
		if (position == null || graphics == null) {
			return;
		}
		final int leftWidth = leftImage.getWidth(null);
		final int rightWidth = rightImage.getWidth(null);
		graphics.drawImage(leftImage, position.x, position.y, leftWidth, position.height, null);				
		graphics.drawImage(middleImage, position.x + leftWidth, position.y, position.width - leftWidth - rightWidth, position.height, null);				
		graphics.drawImage(rightImage, position.x + position.width - rightWidth, position.y, rightWidth, position.height, null);				
		label.paint(graphics);
	}

	private LabelControl label = new LabelControl("", LabelControl.Alignment.CENTER);
	private static Image leftImage = ImageStorage.getImage("button_left.png");
	private static Image middleImage = ImageStorage.getImage("button_middle.png");
	private static Image rightImage = ImageStorage.getImage("button_right.png");
}
