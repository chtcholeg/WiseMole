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

package common.controls;

import java.awt.Dimension;

/**
 * The {@SpaceControl} is class presents empty space.
 * 
 * @author olegshchepilov
 *
 */

public class SpaceControl extends ControlBase {
    final public static String TYPE = "Space";

    // Constructor for stretchable spacer
    public SpaceControl() {
        super(TYPE, "");
    }

    // Constructors for fix-size spacer
    public SpaceControl(int controlWidth, int controlHeight) {
        super(TYPE, "");
        size = new Dimension(controlWidth, controlHeight);
    }

    public SpaceControl(int size) {
        this(size, size);
    }

    public boolean isStretchable() {
        return size == null;
    }

    @Override
    public int getIdealHeight() {
        return (size != null) ? size.height : 0;
    }

    @Override
    public int getIdealWidth() {
        return (size != null) ? size.width : 0;
    }

    private Dimension size = null;
}
