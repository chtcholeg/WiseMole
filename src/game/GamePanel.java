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

package game;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * The {@GamePanel} is a panel that is responsible for drawing the game board
 *
 * @author olegshchepilov
 *
 */

public class GamePanel extends GamePanelBase implements KeyListener {
	public interface Callback {
		public void onGamePanelCommandExit(Game currentGame);
	}
	
	public GamePanel(String levelId, Callback gamePanelCallback) {
		callback = gamePanelCallback;
		game = new Game();
		game.loadGame(levelId);
	}
	public GamePanel(Game passedGame, Callback gamePanelCallback) {
		callback = gamePanelCallback;
		game = passedGame;	
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		if (game != null) {
			final Rectangle workingRect = calcWorkingGameRect(game);
			if (workingRect != null) {
				drawField(graphics, game, workingRect);				
			}
		}
	}
	
	@Override
	public KeyListener keyListener() { return this; }
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {
		final int keyCode = e.getKeyCode();
		boolean haveChanges = false;
		switch (keyCode) { 
			case KeyEvent.VK_UP: haveChanges = game.tryToMoveMole(Game.MoleMovementDirection.UP); break;
			case KeyEvent.VK_DOWN: haveChanges = game.tryToMoveMole(Game.MoleMovementDirection.DOWN); break;
			case KeyEvent.VK_LEFT: haveChanges = game.tryToMoveMole(Game.MoleMovementDirection.LEFT); break;
			case KeyEvent.VK_RIGHT: haveChanges = game.tryToMoveMole(Game.MoleMovementDirection.RIGHT); break;
			case KeyEvent.VK_ESCAPE: if (callback != null) callback.onGamePanelCommandExit(game); break;
		}
		if (haveChanges) {
			revalidate();
			repaint();
		}

	}
	@Override
	public void keyReleased(KeyEvent e) {}

	private Game game = null;
	private Callback callback = null;
	private static final long serialVersionUID = 1L;
}
