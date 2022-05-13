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

import java.util.ArrayList;

/**
 * The {@StepHistory} class that saves actions and do undo()/redo().
 * 
 * @author olegshchepilov
 *
 */

public class StepHistory {

    public void add(GameState state) {
        removeTail();
        states.add((GameState) state.clone());
        ++currentIndex;
    }

    public boolean canUndo() {
        return currentIndex > 0;

    }

    public GameState undo() {
        if (!canUndo()) {
            return null;
        }
        --currentIndex;
        return (GameState) states.get(currentIndex).clone();
    }

    public boolean canRedo() {
        return currentIndex < (states.size() - 1);
    }

    public GameState redo() {
        if (!canRedo()) {
            return null;
        }
        ++currentIndex;
        return (GameState) states.get(currentIndex).clone();

    }

    private void removeTail() {
        if (currentIndex == -1) {
            return;
        }
        while (currentIndex < states.size() - 1) {
            states.remove(states.size() - 1);
        }
    }

    private int currentIndex = -1;
    private ArrayList<GameState> states = new ArrayList<GameState>();
}
