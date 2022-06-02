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

import common.PanelBar;
import common.controls.LabelControl;
import common.controls.LabelControl.Alignment;
import common.controls.NumericLeftRightControl;
import localization.L10n;
import utils.FontUtils;
import utils.Margins;
import utils.RectangleUtils;
import utils.SystemUtils;

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
        topStatusBarHeight = stepCountValueLabel.getIdealHeight() + 2 * PADDING;
        bottomStatusBarHeight = exitHotKeyValueLabel.getIdealHeight() + 2 * PADDING;

        setMargins(new Margins(0, bottomStatusBarHeight, 0, bottomStatusBarHeight));

        callback = gamePanelCallback;
        setGame(passedGame);
        passedGame.addActionListener(this);
        passedGame.checkIfUserWon();

        initControls();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        drawField(graphics);
        if (userWon) {
            drawVictoryPlate(graphics);
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
        boolean haveChanges = false;
        switch (e.getKeyCode()) {
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
            default:
                haveChanges = processPlatformDepended(e);
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
        stepCountValueLabel.setText(Integer.toString(getGame().getStepCount()));
        stepCountWithLoadValueLabel.setText(Integer.toString(getGame().getStepWithLoadCount()));
        repaint();
    }

    @Override
    public void onGameUserWon() {
        userWon = true;
        repaint();
    }

    @Override
    protected Rectangle calcBarArea(PanelBar bar) {
        if (bar == topStatusBar) {
            return calcTopStatusBarRect();
        }
        if (bar == bottomStatusBar) {
            return calcBottomStatusBarRect();
        }
        return null;
    }

    private void drawVictoryPlate(Graphics graphics) {
        final Rectangle fieldArea = renderDetails.fieldArea;
        RectangleUtils.deflateRect(fieldArea, fieldArea.width / 4, fieldArea.height / 4);
        graphics.setColor(Color.GREEN);
        final int radius = (fieldArea.width + fieldArea.height) / 10;
        graphics.fillRoundRect(fieldArea.x, fieldArea.y, fieldArea.width, fieldArea.height, radius, radius);

        graphics.setColor(Color.WHITE);

        Font currentFont = getFont();
        Font newFont = new Font(currentFont.getFontName(), currentFont.getStyle(), radius);
        graphics.setFont(newFont);
        final String text = L10n.get(L10n.Id.VICTORY);
        FontMetrics metrics = graphics.getFontMetrics();
        final int fontHeight = FontUtils.getFontHeight(metrics);
        final int textWidth = metrics.stringWidth(text);
        final int x = fieldArea.x + (fieldArea.width - textWidth) / 2;
        final int y = fieldArea.y + (fieldArea.height + fontHeight) / 2;
        graphics.drawString(text, x, y);
        graphics.setFont(currentFont);
    }

    private boolean processPlatformDepended(KeyEvent e) {
        if (SystemUtils.getOSFamily() == SystemUtils.OSFamily.MACOS) {
            if (e.getKeyCode() == KeyEvent.VK_Z) {
                final boolean commandPressed = e.isMetaDown();
                final boolean shiftPressed = e.isShiftDown();
                if (commandPressed) {
                    return shiftPressed ? getGame().redo() : getGame().undo();
                }
            }
        } else {
            if (e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown()) {
                return getGame().undo();
            }
            if (e.getKeyCode() == KeyEvent.VK_Y && e.isControlDown()) {
                return getGame().redo();
            }

        }
        return false;
    }

    private void initControls() {
        // Top status bar
        topStatusBar.addControl(new LabelControl(L10n.get(L10n.Id.STATUSBAR_GAME_LABEL)), true);
        topStatusBar.addStretchableSpacer(true);
        topStatusBar.addControl(new LabelControl(L10n.get(L10n.Id.STATUSBAR_STEP_COUNT_LABEL)), true);
        topStatusBar.addControl(stepCountValueLabel, true);
        topStatusBar.addStretchableSpacer(true);
        topStatusBar.addControl(new LabelControl(L10n.get(L10n.Id.STATUSBAR_STEP_COUNT_WITH_LOAD_LABEL)), true);
        topStatusBar.addControl(stepCountWithLoadValueLabel, true);
        topStatusBar.addStretchableSpacer(true);
        topStatusBar.addControl(new LabelControl(L10n.get(L10n.Id.STATUSBAR_MAZE_LABEL)), true);
        topStatusBar.addControl(new LabelControl(getGame().getMazeName(), Alignment.LEFT, STEP_VALUE_COUNT_LABEL_WIDTH),
                true);
        addBar(topStatusBar);

        // Bottom status bar
        bottomStatusBar.addControl(new LabelControl(L10n.get(L10n.Id.STATUSBAR_EXIT_LABEL)), true);
        bottomStatusBar.addControl(exitHotKeyValueLabel, true);
        addBar(bottomStatusBar);
    }

    private Rectangle calcTopStatusBarRect() {
        Rectangle result = new Rectangle(0, 0, getSize().width, topStatusBarHeight);
        RectangleUtils.deflateRect(result, PADDING, PADDING);
        return result;
    }

    private Rectangle calcBottomStatusBarRect() {
        Rectangle result = new Rectangle(0, getSize().height - topStatusBarHeight, getSize().width, topStatusBarHeight);
        RectangleUtils.deflateRect(result, PADDING, PADDING);
        return result;
    }

    private static int STEP_VALUE_COUNT_LABEL_WIDTH = 50;
    private boolean userWon = false;
    private Callback callback = null;
    // Top status bar
    private int topStatusBarHeight = NumericLeftRightControl.getImageHeight() + 2 * PADDING;
    private PanelBar topStatusBar = new PanelBar(true);
    private LabelControl stepCountValueLabel = new LabelControl("0", Alignment.LEFT, STEP_VALUE_COUNT_LABEL_WIDTH);
    private LabelControl stepCountWithLoadValueLabel = new LabelControl("0", Alignment.LEFT,
            STEP_VALUE_COUNT_LABEL_WIDTH);
    // Bottom status bar
    private int bottomStatusBarHeight = NumericLeftRightControl.getImageHeight() + 2 * PADDING;
    private PanelBar bottomStatusBar = new PanelBar(true);
    private LabelControl exitHotKeyValueLabel = new LabelControl("Esc", Alignment.LEFT, STEP_VALUE_COUNT_LABEL_WIDTH);

    private static final long serialVersionUID = 1L;
}
