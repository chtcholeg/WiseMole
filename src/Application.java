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


import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;

import common.*;
import game.*;
import menu.*;

/**
 * The {@Application} class is main class of Application.
 * It:
 * - contains application entry point (function 'main')
 * - initializes application
 * @author olegshchepilov
 *
 */

public class Application extends JFrame implements MainMenuPanel.Callback {
	public static void main(String[] args) {
		Application app = new Application();
		app.setVisible(true);
	}
	
	public Application() {
		initFont();
		
		setPanel(new MainMenuPanel(this));

        setSize(ApplicationDefines.FRAME_WIDTH, ApplicationDefines.FRAME_HEIGHT);

        setTitle("Wise Mole");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
	}
	
	private void initFont() {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream input = classLoader.getResourceAsStream("font/pixy/PIXY.ttf");
		    applicationFont = Font.createFont(Font.TRUETYPE_FONT, input).deriveFont(ApplicationDefines.FONT_SIZE);
		    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		    graphicsEnvironment.registerFont(applicationFont);
		} catch (IOException | FontFormatException e) {
		    e.printStackTrace();
		}
	}
	
	// MainMenuPanel.Callback
	@Override
	public void OnMainMenuCommandPlay() {
		setPanel(new GamePanel("level1.game"));
		repaint();
	}
	@Override
	public void OnMainMenuCommandExit() {
		System.exit(0); 
	}
	
	private PanelBase currentPanel = null;
	private Font applicationFont = null;
	private static final long serialVersionUID = 1L;

	private void setPanel(PanelBase panel) {
		if (currentPanel != null) {
			removeKeyListener(currentPanel.keyListener());
			remove(currentPanel);
		}
		currentPanel = panel;
		currentPanel.setFont(applicationFont);
		addKeyListener(currentPanel.keyListener());
		add(currentPanel);
	}
}
