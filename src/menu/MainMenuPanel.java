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

package menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import common.*;
import utils.*;

/**
 * The {@MainMenuPanel} is a panel that display main menu of the game
 *
 * @author olegshchepilov
 *
 */

public class MainMenuPanel extends PanelBase implements KeyListener {
	public interface Callback {
		public void OnMainMenuCommandPlay();
		public void OnMainMenuCommandExit();
	}
	public MainMenuPanel(Callback mainMenuCallback) {
		callback = mainMenuCallback;
	}

	@Override
	public KeyListener keyListener() { return this;  }
	@Override
	public void keyTyped(KeyEvent e) { }
	@Override
	public void keyPressed(KeyEvent e) {
		final int keyCode = e.getKeyCode();
		switch (keyCode) { 
			case KeyEvent.VK_UP: moveSelection(true); break;
			case KeyEvent.VK_DOWN: moveSelection(false); break;
			case KeyEvent.VK_ENTER: commandSelection(); break; 
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {}
	
	
	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		if (!renderedContent.rendered) {
			renderContent(graphics);
		}
		
		final Dimension currentPanelTotalSize = getSize();		
		final int offsetX = (currentPanelTotalSize.width - renderedContent.menuTotalSize.width) / 2;
		final int offsetY = (currentPanelTotalSize.height - renderedContent.menuTotalSize.height) / 2;
		
		for (int itemIndex = 0; itemIndex < renderedContent.itemBlocks.size(); ++itemIndex) {
		    final ItemBlock block = renderedContent.itemBlocks.get(itemIndex);
			final boolean selected = (selectedItemIndex == itemIndex);
			graphics.setColor(selected ? Color.RED : Color.BLACK);
			graphics.drawString(block.string, offsetX + block.area.x, offsetY + block.area.y);
			
			if (selected) {
				Image image = ImageStorage.getImage("arrow.png");
				if (image != null) {
					final int orignalArrowWidth = image.getWidth(null);
					final int originalArrowHeight = image.getHeight(null);
					final int targetArrowHeight = block.area.height;
					final int targetArrowWidth = orignalArrowWidth * targetArrowHeight / originalArrowHeight;
					final int arrowX = offsetX - targetArrowWidth;
					final int arrowY = offsetY + block.area.y - block.area.height + (block.area.height - targetArrowHeight) / 2;
					graphics.drawImage(image, arrowX, arrowY, targetArrowWidth, targetArrowHeight, null);
				}
			}
		}
	}
	
	private enum ItemType {
		PLAY,
		EXIT,
		UNKNOWN
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
	private Callback callback = null;
	private static final long serialVersionUID = 1L;
	
	
	private static String convertTypeToString(ItemType type) {
		switch(type) {
			case PLAY: return "Play";
			case EXIT: return "Exit";
			case UNKNOWN: return "<Unknown>";
		}
		return "";
	}
	
	private void renderContent(Graphics graphics) {
		final List<ItemType> items = Arrays.asList(ItemType.PLAY, ItemType.EXIT);
		final int itemOffset = 10;
		renderedContent.menuTotalSize = new Dimension(0, 0);
		FontMetrics fontMetrics = graphics.getFontMetrics();
		final int fontHeight = fontMetrics.getHeight()/2;
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
		if (callback != null && selectedItemIndex >=0 && selectedItemIndex < renderedContent.itemBlocks.size()) {
			final ItemBlock block = renderedContent.itemBlocks.get(selectedItemIndex);
			switch(block.type) {
				case PLAY: callback.OnMainMenuCommandPlay(); break;
				case EXIT: callback.OnMainMenuCommandExit(); break;
			}
		}
	}

}
