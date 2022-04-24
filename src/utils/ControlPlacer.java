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

package utils;

import java.awt.Rectangle;

/**
 * The {@ControlPlacer} control that place controls in some place
 *
 * @author olegshchepilov
 *
 */

public class ControlPlacer {
	public ControlPlacer(Rectangle workingRectangle) {
		workingRect = (Rectangle) workingRectangle.clone();
	}
	
	public Rectangle addBottom(int height) {
		Rectangle controlRect = (Rectangle) workingRect.clone();
		final int bottom = workingRect.y + workingRect.height;
		controlRect.y = bottom - height;
		controlRect.height = height;
		workingRect.height -= height; 
		return controlRect;
	}
	
	public Rectangle addTop(int height) {
		Rectangle controlRect = (Rectangle) workingRect.clone();
		controlRect.height = height;
		workingRect.y += height;
		workingRect.height -= height;
		return controlRect;
	}

	private Rectangle workingRect = null;
}
