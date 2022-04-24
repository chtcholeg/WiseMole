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

package common;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import utils.*;

/**
 * The {@NumericLeftRightControl} is class for painting and processing numeric-control.
 * @author olegshchepilov
 *
 */

public class NumericLeftRightControl extends ControlBase {
	public NumericLeftRightControl(int min, int max) {
		minValue = min;
		maxValue = max;
		setValue(Math.min(maxValue, Math.max(value, minValue)));
	}
	
	public static int getImageWidth() {
		return controlImage.getWidth(null);		
	}
	
	@Override
	public int getIdealHeight() {
		return controlImage.getHeight(null);
	}
	@Override
	public int getIdealWidth() {
		return controlImage.getWidth(null);
	}
	@Override
	public void onPositionChanged() {
		Rectangle labelPosition = (Rectangle) position.clone();
		RectangleUtils.deflateRect(labelPosition, LEFT_RIGHT_PADDING, TOP_BOTTOM_PADDING); 
		label.setPosition(labelPosition);
	}
	@Override
	public void paint(Graphics graphics) {
		if (position == null || graphics == null) {
			return;
		}
		if (controlImage != null) {
			graphics.drawImage(controlImage, position.x, position.y, position.width, position.height, null);				
		}
		label.paint(graphics);
	}
	
	private void setValue(int newValue) {
		value = newValue;
		label.setText(Integer.toString(value));
	}
	
	private int value = 0;
	private int minValue = 0;
	private int maxValue = 100;
	private LabelControl label = new LabelControl("", LabelControl.Alignment.CENTER);
	private static int LEFT_RIGHT_PADDING = 20;
	private static int TOP_BOTTOM_PADDING = 4;	
	private static Image controlImage = ImageStorage.getImage("numeric_left_right.png");
}
