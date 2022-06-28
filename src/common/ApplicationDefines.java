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

package common;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

/**
 * The {@ApplicationDefines} class that contains all main constants.
 * 
 * @author olegshchepilov
 *
 */

public class ApplicationDefines {
	static public final float FONT_SIZE = 20f;
	static public Font font = null;
	static public final Dimension DEFAULT_FRAME_SIZE = new Dimension(800, 800);

	static public void init() {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream input = classLoader.getResourceAsStream("font/pixy/PIXY.ttf");
			font = Font.createFont(Font.TRUETYPE_FONT, input).deriveFont(ApplicationDefines.FONT_SIZE);
			GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			graphicsEnvironment.registerFont(font);
		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
		}
	}
}
