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

package controls;

/**
 * The {@SpaceControl} is class presents empty space.
 * 
 * @author olegshchepilov
 *
 */

public class SpaceControl extends ControlBase {
    final public static String TYPE = "Space";

    public SpaceControl(int controlWidth, int controlHeight) {
        super(TYPE, "");
        width = controlWidth;
        height = controlHeight;
    }

    @Override
    public int getIdealHeight() {
        return height;
    }

    @Override
    public int getIdealWidth() {
        return width;
    }

    private int width = 0;
    private int height = 0;
}