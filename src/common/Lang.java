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

package common;

/**
 * The {@Lang} is class to get localized text resources.
 * 
 * @author olegshchepilov
 *
 */

public class Lang {
    public enum Res {
        PLAY, EDIT, CONTINUE, EXIT, WIDTH, HEIGHT, SAVE, DIALOG_OVERWRITE_CONFIRM_TITLE, DIALOG_OVERWRITE_CONFIRM_TEXT,
        DIALOG_GAME_VALIDATION_TEXT, ERROR_THERE_IS_NO_MOLE, ERROR_TARGET_POINTS_MORE_THAN_BOXES
    }

    static public String get(Res res) {
        switch (res) {
            case PLAY:
                return "Play";
            case EDIT:
                return "Edit";
            case CONTINUE:
                return "Continue";
            case EXIT:
                return "Exit";
            case WIDTH:
                return "Width";
            case HEIGHT:
                return "Height";
            case SAVE:
                return "Save";
            case DIALOG_OVERWRITE_CONFIRM_TITLE:
                return "Confirm Save";
            case DIALOG_OVERWRITE_CONFIRM_TEXT:
                return "File already exists. Do you want to replace it?";
            case DIALOG_GAME_VALIDATION_TEXT:
                return "There is an issue: {0}. Do you want to save the level?";
            case ERROR_THERE_IS_NO_MOLE:
                return "there is no mole on the field";
            case ERROR_TARGET_POINTS_MORE_THAN_BOXES:
                return "target point count is more than box count";
            default:
                return "<Unknown>";
        }
    }
}
