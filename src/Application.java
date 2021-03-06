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

import javax.swing.JFrame;

import common.ApplicationDefines;
import common.PanelBase;
import editor.EditorPanel;
import editor.LevelStorage;
import game.Game;
import game.GamePanel;
import localization.L10n;
import menu.LevelMenuPanel;
import menu.MainMenuPanel;
import utils.ImageStorage;

/**
 * The {@Application} class is main class of Application. It: 1. contains
 * application entry point (function 'main') 2. initializes application
 * 
 * @author olegshchepilov
 *
 */

public class Application extends JFrame
        implements MainMenuPanel.Callback, LevelMenuPanel.Callback, GamePanel.Callback, EditorPanel.Callback {
    public static void main(String[] args) {
        ApplicationDefines.init();

        Application app = new Application();
        app.setVisible(true);
    }

    public Application() {
        this.setIconImage(ImageStorage.getImage("app_icon.png"));

        setPanel(new MainMenuPanel(this, closedGame != null));

        setSize(ApplicationDefines.DEFAULT_FRAME_SIZE);
        setMinimumSize(ApplicationDefines.DEFAULT_FRAME_SIZE);

        setTitle(L10n.get(L10n.Id.TITLE));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // MainMenuPanel.Callback
    @Override
    public void onMainMenuCommandPlay(boolean continueClosedGame) {
        Game gameToLoad = continueClosedGame ? closedGame : LevelStorage.loadPredefined(currentPredefinedLevelIndex);
        setPanel(new GamePanel(gameToLoad, this));
    }

    @Override
    public void onMainMenuCommandSelectPredefined() {
        setPanel(new LevelMenuPanel(this, currentPredefinedLevelIndex));
    }

    @Override
    public void onMainMenuCommandLoadFromFile() {
        Game game = LevelStorage.loadFromFile(this);
        if (game != null) {
            setPanel(new GamePanel(game, this));
        }
    }

    @Override
    public void onMainMenuCommandCreate() {
        setPanel(new EditorPanel(this));
    }

    @Override
    public void onMainMenuCommandExit() {
        System.exit(0);
    }

    // LevelMenuPanel.Callback
    @Override
    public void onLevelMenuCommandLevel(int index) {
        Game game = LevelStorage.loadPredefined(index);
        if (game != null) {
            currentPredefinedLevelIndex = index;
            setPanel(new GamePanel(game, this));
        } else {
            onLevelMenuCommandExit();
        }
    }

    @Override
    public void onLevelMenuCommandExit() {
        setPanel(new MainMenuPanel(this, closedGame != null));
    }

    // GamePanel.Callback
    @Override
    public void onGamePanelCommandExit(Game currentGame) {
        closedGame = currentGame;
        setPanel(new MainMenuPanel(this, closedGame != null));
    }

    @Override
    public void onGamePanelCommandExitOnVictory() {
        currentPredefinedLevelIndex = Math.min(currentPredefinedLevelIndex + 1, LevelStorage.predefinedLevelCount());
        closedGame = null;
        setPanel(new MainMenuPanel(this, false));
    }

    private PanelBase currentPanel = null;
    private int currentPredefinedLevelIndex = 1;
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
        currentPanel.setFont(ApplicationDefines.font);
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
