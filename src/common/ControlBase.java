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
import java.awt.Rectangle;

/**
 * The {@ControlBase} is base class for controls.
 * @author olegshchepilov
 *
 */
public class ControlBase {
	public ControlBase() {
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
	public void paint(Graphics graphics) {
	}
	
	protected void onPositionChanged() {}
	
	protected Rectangle position = null;
}
