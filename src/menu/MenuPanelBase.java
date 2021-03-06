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
import java.util.List;

import common.PanelBase;
import utils.FontUtils;
import utils.ImageStorage;
import utils.PanelUtils;

/**
 * The {@MenuPanelBase} is a base panel for menu
 *
 * @author olegshchepilov
 *
 */

public class MenuPanelBase extends PanelBase implements KeyListener, MouseListener, MouseMotionListener {

    public MenuPanelBase() {
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
                onEscape();
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

    private class ItemBlock {
        Rectangle area = null;
        int id = -1;
        String string = "";
    }

    private class RenderedContent {
        boolean rendered = false;
        List<ItemBlock> itemBlocks = new ArrayList<ItemBlock>();
        Dimension menuTotalSize = null;
    }

    private RenderedContent renderedContent = new RenderedContent();
    private int selectedItemIndex = 0;
    private static final long serialVersionUID = 1L;

    private void renderContent(Graphics graphics) {
        final List<Integer> items = getItems();
        final int itemOffset = 10;
        renderedContent.menuTotalSize = new Dimension(0, 0);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        final int fontHeight = FontUtils.getFontHeight(graphics.getFont());
        
        for (Integer itemId : items) {
            ItemBlock block = new ItemBlock();
            block.id = itemId;
            block.string = convertIdToString(itemId);
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
        if (selectedItemIndex >= 0 && selectedItemIndex < renderedContent.itemBlocks.size()) {
            final ItemBlock block = renderedContent.itemBlocks.get(selectedItemIndex);
            onCommand(block.id);
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

    protected void onEscape() {
    }

    protected List<Integer> getItems() {
        return new ArrayList<Integer>();
    }

    protected void onCommand(int itemId) {
    }

    protected String convertIdToString(Integer id) {
        return "<Unknown>";
    }

    protected void selectItem(int index) {
        if (selectedItemIndex != index) {
            selectedItemIndex = index;
            repaint();
        }
    }
}
