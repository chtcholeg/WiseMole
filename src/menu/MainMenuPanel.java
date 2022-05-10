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

package menu;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import common.Lang;
import common.PanelBase;
import utils.FontUtils;
import utils.ImageStorage;
import utils.PanelUtils;

/**
 * The {@MainMenuPanel} is a panel that display main menu of the game
 *
 * @author olegshchepilov
 *
 */

public class MainMenuPanel extends PanelBase implements KeyListener, MouseListener, MouseMotionListener {
    public interface Callback {
        public void onMainMenuCommandPlay(boolean continuePrevGame);

        public void onMainMenuCommandLoadFromFile();

        public void onMainMenuCommandEdit();

        public void onMainMenuCommandExit();
    }

    public MainMenuPanel(Callback mainMenuCallback, boolean hasClosedGame) {
        showContinueItem = hasClosedGame;
        callback = mainMenuCallback;
    }

    @Override
    public KeyListener keyListener() {
        return this;
    }

    @Override
    public MouseListener mouseListener() {
        return this;
    }

    @Override
    public MouseMotionListener mouseMotionListener() {
        return this;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        final int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                moveSelection(true);
                break;
            case KeyEvent.VK_DOWN:
                moveSelection(false);
                break;
            case KeyEvent.VK_ENTER:
                commandSelection();
                break;
            case KeyEvent.VK_ESCAPE:
                if (callback != null)
                    callback.onMainMenuCommandExit();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        final int index = getIndexOfItemUnderPoint(PanelUtils.getRelativePoint(e, this));
        if (index != -1) {
            selectedItemIndex = index;
            commandSelection();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        final int index = getIndexOfItemUnderPoint(PanelUtils.getRelativePoint(e, this));
        if (index == -1) {
            setCursor(Cursor.DEFAULT_CURSOR);
        } else {
            selectedItemIndex = index;
            setCursor(Cursor.HAND_CURSOR);
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (!renderedContent.rendered) {
            renderContent(graphics);
        }

        final Point offset = calcContentOffset();

        for (int itemIndex = 0; itemIndex < renderedContent.itemBlocks.size(); ++itemIndex) {
            final ItemBlock block = renderedContent.itemBlocks.get(itemIndex);
            final boolean selected = (selectedItemIndex == itemIndex);
            graphics.setColor(selected ? Color.RED : Color.BLACK);
            graphics.drawString(block.string, offset.x + block.area.x, offset.y + block.area.y + block.area.height);

            if (selected) {
                Image image = ImageStorage.getImage("arrow.png");
                if (image != null) {
                    final int orignalArrowWidth = image.getWidth(null);
                    final int originalArrowHeight = image.getHeight(null);
                    final int targetArrowHeight = block.area.height;
                    final int targetArrowWidth = orignalArrowWidth * targetArrowHeight / originalArrowHeight;
                    final int arrowX = offset.x - targetArrowWidth;
                    final int arrowY = offset.y + block.area.y;
                    graphics.drawImage(image, arrowX, arrowY, targetArrowWidth, targetArrowHeight, null);
                }
            }
        }
    }

    private enum ItemType {
        PLAY, CONTINUE, LOAD_FROM_FILE, EDIT, EXIT, UNKNOWN
    }

    private class ItemBlock {
        Rectangle area = null;
        ItemType type = ItemType.UNKNOWN;
        String string = "";
    }

    private class RenderedContent {
        boolean rendered = false;
        List<ItemBlock> itemBlocks = new ArrayList<ItemBlock>();
        Dimension menuTotalSize = null;
    }

    private RenderedContent renderedContent = new RenderedContent();
    private int selectedItemIndex = 0;
    private boolean showContinueItem = false;
    private Callback callback = null;
    private static final long serialVersionUID = 1L;

    private static String convertTypeToString(ItemType type) {
        switch (type) {
            case PLAY:
                return Lang.get(Lang.Res.PLAY);
            case CONTINUE:
                return Lang.get(Lang.Res.CONTINUE);
            case LOAD_FROM_FILE:
                return Lang.get(Lang.Res.LOAD_FROM_FILE);
            case EDIT:
                return Lang.get(Lang.Res.EDIT);
            case EXIT:
                return Lang.get(Lang.Res.EXIT);
            case UNKNOWN:
                return "<Unknown>";
            default:
                return "";
        }
    }

    private void renderContent(Graphics graphics) {
        final List<ItemType> items = showContinueItem
                ? Arrays.asList(ItemType.PLAY, ItemType.CONTINUE, ItemType.LOAD_FROM_FILE, ItemType.EDIT, ItemType.EXIT)
                : Arrays.asList(ItemType.PLAY, ItemType.LOAD_FROM_FILE, ItemType.EDIT, ItemType.EXIT);
        final int itemOffset = 10;
        renderedContent.menuTotalSize = new Dimension(0, 0);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        final int fontHeight = FontUtils.getFontHeight(fontMetrics);

        for (ItemType itemType : items) {
            ItemBlock block = new ItemBlock();
            block.type = itemType;
            block.string = convertTypeToString(itemType);
            final int width = fontMetrics.stringWidth(block.string);
            block.area = new Rectangle(0, renderedContent.menuTotalSize.height, width, fontHeight);

            renderedContent.menuTotalSize.width = Math.max(renderedContent.menuTotalSize.width, width);
            renderedContent.menuTotalSize.height += fontHeight + itemOffset;
            renderedContent.itemBlocks.add(block);
        }
        renderedContent.rendered = true;
    }

    private void moveSelection(boolean up) {
        final int delta = up ? -1 : 1;
        final int prevSelectedItemIndex = selectedItemIndex;
        selectedItemIndex = Math.min(renderedContent.itemBlocks.size() - 1, Math.max(0, selectedItemIndex + delta));
        if (prevSelectedItemIndex != selectedItemIndex) {
            repaint();
        }
    }

    private void commandSelection() {
        if (callback != null && selectedItemIndex >= 0 && selectedItemIndex < renderedContent.itemBlocks.size()) {
            final ItemBlock block = renderedContent.itemBlocks.get(selectedItemIndex);
            switch (block.type) {
                case PLAY:
                    callback.onMainMenuCommandPlay(false);
                    break;
                case CONTINUE:
                    callback.onMainMenuCommandPlay(true);
                    break;
                case LOAD_FROM_FILE:
                    callback.onMainMenuCommandLoadFromFile();
                    break;
                case EDIT:
                    callback.onMainMenuCommandEdit();
                    break;
                case EXIT:
                    callback.onMainMenuCommandExit();
                    break;
                default:
                    break;
            }
        }
    }

    private Point calcContentOffset() {
        final Dimension currentPanelTotalSize = getSize();
        if (renderedContent.menuTotalSize == null) {
            return new Point(0, 0);
        }
        return new Point((currentPanelTotalSize.width - renderedContent.menuTotalSize.width) / 2,
                (currentPanelTotalSize.height - renderedContent.menuTotalSize.height) / 2);
    }

    private int getIndexOfItemUnderPoint(Point point) {
        final Point offset = calcContentOffset();

        for (int index = 0; index < renderedContent.itemBlocks.size(); ++index) {
            final ItemBlock block = renderedContent.itemBlocks.get(index);
            Rectangle rect = (Rectangle) block.area.clone();
            rect.translate(offset.x, offset.y);
            if (rect.contains(point)) {
                return index;
            }
        }
        return -1;
    }

    private void setCursor(int cursorId) {
        setCursor(new Cursor(cursorId));
    }
}
