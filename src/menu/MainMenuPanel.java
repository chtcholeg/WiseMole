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

import java.util.Arrays;
import java.util.List;

import localization.L10n;

/**
 * The {@MainMenuPanel} is a panel that display main menu of the game
 *
 * @author olegshchepilov
 *
 */

public class MainMenuPanel extends MenuPanelBase {
    public interface Callback {
        public void onMainMenuCommandPlay(boolean continuePrevGame);

        public void onMainMenuCommandSelectPredefined();

        public void onMainMenuCommandLoadFromFile();

        public void onMainMenuCommandCreate();

        public void onMainMenuCommandExit();
    }

    public MainMenuPanel(Callback mainMenuCallback, boolean hasClosedGame) {
        showContinueItem = hasClosedGame;
        callback = mainMenuCallback;
    }

    private class ItemId {
        public final static int UNKNOWN = -1;
        public final static int PLAY = 0;
        public final static int CONTINUE = 1;
        public final static int SELECT_PREDEFINED = 2;
        public final static int LOAD_FROM_FILE = 3;
        public final static int CREATE = 4;
        public final static int EXIT = 5;
    }

    private boolean showContinueItem = false;
    private Callback callback = null;
    private static final long serialVersionUID = 1L;

    @Override
    protected void onEscape() {
        callback.onMainMenuCommandExit();
    }

    @Override
    protected List<Integer> getItems() {
        final List<Integer> items = showContinueItem
                ? Arrays.asList(ItemId.PLAY, ItemId.CONTINUE, ItemId.SELECT_PREDEFINED, ItemId.LOAD_FROM_FILE,
                        ItemId.CREATE, ItemId.EXIT)
                : Arrays.asList(ItemId.PLAY, ItemId.SELECT_PREDEFINED, ItemId.LOAD_FROM_FILE, ItemId.CREATE,
                        ItemId.EXIT);
        return items;
    }

    @Override
    protected void onCommand(int itemId) {
        if (callback == null) {
            return;
        }
        switch (itemId) {
            case ItemId.PLAY:
                callback.onMainMenuCommandPlay(false);
                break;
            case ItemId.CONTINUE:
                callback.onMainMenuCommandPlay(true);
                break;
            case ItemId.SELECT_PREDEFINED:
                callback.onMainMenuCommandSelectPredefined();
                break;
            case ItemId.LOAD_FROM_FILE:
                callback.onMainMenuCommandLoadFromFile();
                break;
            case ItemId.CREATE:
                callback.onMainMenuCommandCreate();
                break;
            case ItemId.EXIT:
                callback.onMainMenuCommandExit();
                break;
            default:
                break;
        }
    }

    @Override
    protected String convertIdToString(Integer id) {
        switch (id) {
            case ItemId.PLAY:
                return L10n.get(L10n.Id.PLAY);
            case ItemId.CONTINUE:
                return L10n.get(L10n.Id.CONTINUE);
            case ItemId.SELECT_PREDEFINED:
                return L10n.get(L10n.Id.SELECT_PREDEFINED);
            case ItemId.LOAD_FROM_FILE:
                return L10n.get(L10n.Id.LOAD_FROM_FILE);
            case ItemId.CREATE:
                return L10n.get(L10n.Id.CREATE);
            case ItemId.EXIT:
                return L10n.get(L10n.Id.EXIT);
            case ItemId.UNKNOWN:
                return "<Unknown>";
            default:
                return "";
        }
    }

}
