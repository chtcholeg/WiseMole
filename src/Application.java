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

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;

import common.ApplicationDefines;
import common.PanelBase;
import editor.EditorPanel;
import game.Game;
import game.GamePanel;
import menu.MainMenuPanel;
import utils.ImageStorage;

/**
 * The {@Application} class is main class of Application. It: - contains
 * application entry point (function 'main') - initializes application
 * 
 * @author olegshchepilov
 *
 */

public class Application extends JFrame implements MainMenuPanel.Callback, GamePanel.Callback, EditorPanel.Callback {
    public static void main(String[] args) {
        Application app = new Application();
        app.setVisible(true);
    }

    public Application() {
        this.setIconImage(ImageStorage.getImage("app_icon.png"));

        initFont();

        setPanel(new MainMenuPanel(this, closedGame != null));

        setSize(ApplicationDefines.DEFAULT_FRAME_SIZE);
        setMinimumSize(ApplicationDefines.DEFAULT_FRAME_SIZE);

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
    public void onMainMenuCommandPlay(boolean continueClosedGame) {
        GamePanel gamePanel = continueClosedGame ? new GamePanel(closedGame, this) : new GamePanel(currentGameId, this);
        setPanel(gamePanel);
    }

    @Override
    public void onMainMenuCommandEdit() {
        setPanel(new EditorPanel(this));
    }

    @Override
    public void onMainMenuCommandExit() {
        System.exit(0);
    }

    // GamePanel.Callback
    @Override
    public void onGamePanelCommandExit(Game currentGame) {
        closedGame = currentGame;
        setPanel(new MainMenuPanel(this, closedGame != null));
    }

    private PanelBase currentPanel = null;
    private Font applicationFont = null;
    private String currentGameId = "level1.game";
    private Game closedGame = null;
    private static final long serialVersionUID = 1L;

    private void setPanel(PanelBase panel) {
        if (currentPanel != null) {
            removeKeyListener(currentPanel.keyListener());
            removeMouseListener(currentPanel.mouseListener());
            removeMouseMotionListener(currentPanel.mouseMotionListener());
            remove(currentPanel);
        }
        currentPanel = panel;
        currentPanel.setFont(applicationFont);
        addKeyListener(currentPanel.keyListener());
        addMouseListener(currentPanel.mouseListener());
        addMouseMotionListener(currentPanel.mouseMotionListener());
        add(currentPanel);
        currentPanel.revalidate();
        currentPanel.repaint();
    }

    // EditorPanel.Callback
    @Override
    public void onEditorPanelCommandExit(Game game) {
        setPanel(new MainMenuPanel(this, closedGame != null));
    }

}
