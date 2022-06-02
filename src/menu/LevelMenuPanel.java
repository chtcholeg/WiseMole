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

import java.util.ArrayList;
import java.util.List;

import editor.LevelStorage;
import localization.L10n;

/**
 * The {@LevelMenuPanel} is a panel that display menu to select predefined level
 *
 * @author olegshchepilov
 *
 */

public class LevelMenuPanel extends MenuPanelBase {
    public interface Callback {
        public void onLevelMenuCommandLevel(int levelIndex);

        public void onLevelMenuCommandExit();
    }

    public LevelMenuPanel(Callback levelMenuCallback, Integer selectedLevel) {
        callback = levelMenuCallback;
        selectItem(selectedLevel - 1);
    }

    private Callback callback = null;
    private static final int GO_BACK = 0;
    private static final long serialVersionUID = 1L;

    @Override
    protected void onEscape() {
        callback.onLevelMenuCommandExit();
    }

    @Override
    protected List<Integer> getItems() {
        final List<Integer> items = new ArrayList<Integer>();
        for (int i = 1; i <= LevelStorage.predefinedLevelCount(); ++i) {
            items.add(i);
        }
        items.add(GO_BACK);
        return items;
    }

    @Override
    protected void onCommand(int itemId) {
        if (callback == null) {
            return;
        }
        if (itemId < 0 || itemId > LevelStorage.predefinedLevelCount()) {
            return;
        }
        if (itemId == GO_BACK) {
            callback.onLevelMenuCommandExit();
        }
        callback.onLevelMenuCommandLevel(itemId);
    }

    @Override
    protected String convertIdToString(Integer index) {
        return (index == 0) ? L10n.get(L10n.Id.GO_BACK) : "Level " + index.toString();
    }

}
