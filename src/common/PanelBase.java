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

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

/**
 * The {@PanelBase} is base class for panels used in the application.
 * @author olegshchepilov
 *
 */

public class PanelBase extends JPanel {
	// If a child class wants to process key events, it has to return non-null object
	public KeyListener keyListener() { return null; }
	// If a child class wants to process mouse events, it has to return non-null object
	public MouseListener mouseListener() { return null; }
	// If a child class wants to process mouse motion, it has to return non-null object
	public MouseMotionListener mouseMotionListener() { return null; }

	private static final long serialVersionUID = 1L;
}
