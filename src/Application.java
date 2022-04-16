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

import game.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The {@Application} class is main class of Application.
 * It:
 * - contains application entry point (function 'main')
 * - initializes application
 * @author olegshchepilov
 *
 */

public class Application extends JFrame {
	public static void main(String[] args) {
		Application app = new Application();
		app.setVisible(true);
	}
	
	public Application() {
		init();
	}
	
	private void init() {
		currentPanel = new GamePanel("level1.game");
        add(currentPanel);

        setSize(800, 600);

        setTitle("Wise Mole");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
	}
	
	private JPanel currentPanel = null;
	private static final long serialVersionUID = 1L;

}
