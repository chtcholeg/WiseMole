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

import java.awt.Point;

/**
 * The {@Mole} class represents the 'mole'.
 * It's moved by a user.
 * 
 * @author olegshchepilov
 *
 */

public class Mole {
	
	public Mole() {}
	
	public final Point getCurrentPosition() {
		return currentPosition;
	}
	public void setCurrentPosition(Point newPosition) {
		currentPosition = newPosition;
	}
	
	private Point currentPosition = new Point(0, 0);
}