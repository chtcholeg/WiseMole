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

package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import common.Lang;
import utils.FontUtils;
import utils.RectangleUtils;

/**
 * The {@GamePanel} is a panel that is responsible for drawing the game board
 *
 * @author olegshchepilov
 *
 */

public class GamePanel extends GamePanelBase implements KeyListener, Game.ActionListener {
    public interface Callback {
        public void onGamePanelCommandExit(Game currentGame);

        public void onGamePanelCommandExitOnVictory();
    }

    public GamePanel(Game passedGame, Callback gamePanelCallback) {
        callback = gamePanelCallback;
        setGame(passedGame);
        passedGame.addActionListener(this);
        passedGame.checkIfUserWon();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        drawField(graphics);
        if (userWon) {
            drawWinPlate(graphics);
        }
    }

    @Override
    public KeyListener keyListener() {
        return this;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        final int keyCode = e.getKeyCode();
        boolean haveChanges = false;
        switch (keyCode) {
            case KeyEvent.VK_UP:
                haveChanges = getGame().tryToMoveMole(Game.MoleMovementDirection.UP);
                break;
            case KeyEvent.VK_DOWN:
                haveChanges = getGame().tryToMoveMole(Game.MoleMovementDirection.DOWN);
                break;
            case KeyEvent.VK_LEFT:
                haveChanges = getGame().tryToMoveMole(Game.MoleMovementDirection.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                haveChanges = getGame().tryToMoveMole(Game.MoleMovementDirection.RIGHT);
                break;
            case KeyEvent.VK_ENTER:
                if (userWon && callback != null) {
                    callback.onGamePanelCommandExitOnVictory();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                if (callback != null) {
                    if (userWon) {
                        callback.onGamePanelCommandExitOnVictory();
                    } else {
                        callback.onGamePanelCommandExit(getGame());
                    }
                }
                break;
        }
        if (haveChanges) {
            revalidate();
            repaint();
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void onGameMoleMove() {

    }

    @Override
    public void onGameUserWon() {
        userWon = true;
        repaint();
    }

    private void drawWinPlate(Graphics graphics) {
        final Rectangle fieldArea = renderDetails.fieldArea;
        RectangleUtils.deflateRect(fieldArea, fieldArea.width / 4, fieldArea.height / 4);
        graphics.setColor(Color.RED);
        final int radius = (fieldArea.width + fieldArea.height) / 10;
        graphics.fillRoundRect(fieldArea.x, fieldArea.y, fieldArea.width, fieldArea.height, radius, radius);

        graphics.setColor(Color.WHITE);

        Font currentFont = getFont();
        Font newFont = new Font(currentFont.getFontName(), currentFont.getStyle(), radius);
        graphics.setFont(newFont);
        final String text = Lang.get(Lang.Res.VICTORY);
        FontMetrics metrics = graphics.getFontMetrics();
        final int fontHeight = FontUtils.getFontHeight(metrics);
        final int textWidth = metrics.stringWidth(text);
        final int x = fieldArea.x + (fieldArea.width - textWidth) / 2;
        final int y = fieldArea.y + (fieldArea.height + fontHeight) / 2;
        graphics.drawString(text, x, y);
        graphics.setFont(currentFont);
    }

    private boolean userWon = false;
    private Callback callback = null;
    private static final long serialVersionUID = 1L;
}
