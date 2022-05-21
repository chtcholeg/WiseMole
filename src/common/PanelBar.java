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

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.controls.ControlBase;
import common.controls.SpaceControl;

/**
 * The {@PanelBar} is base class for subpanel in {@PanelBase}
 * 
 * @author olegshchepilov
 *
 */

public class PanelBar {

    public PanelBar(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public void addControl(ControlBase control, boolean leftOrTop) {
        if (control == null) {
            return;
        }
        if (leftOrTop) {
            leftOrTopControls.add(control);
        } else {
            rightOrBottomControls.add(0, control);
        }
    }

    public void addSpacer(boolean leftOrTop, int size) {
        addControl(new SpaceControl(size), leftOrTop);
    }

    public void addStretchableSpacer(boolean leftOrTop) {
        addControl(new SpaceControl(), leftOrTop);
    }

    public void updatePostition(Rectangle rect) {
        Rectangle workRect = (Rectangle) rect.clone();
        HashMap<ControlBase, Rectangle> controlsPlaces = new HashMap<ControlBase, Rectangle>();
        ArrayList<ControlBase> orderedControls = new ArrayList<ControlBase>();
        int stretchableSpacerCount = 0;

        // 1. Locating fix-size controls
        for (ControlBase control : leftOrTopControls) {
            locateInitially(control, controlsPlaces, workRect, true);
            orderedControls.add(control);
            stretchableSpacerCount += (isStretchableSpacer(control) ? 1 : 0);
        }
        final int insertPos = orderedControls.size();
        for (ControlBase control : rightOrBottomControls) {
            locateInitially(control, controlsPlaces, workRect, false);
            orderedControls.add(insertPos, control);
            stretchableSpacerCount += (isStretchableSpacer(control) ? 1 : 0);
        }

        // 2. Relocating if we have stretchable controls
        if (stretchableSpacerCount > 0) {
            final int freeSpaceSize = horizontal ? workRect.width : workRect.height;
            final int stretchableSpacerSize = freeSpaceSize / stretchableSpacerCount;
            int offset = 0;
            for (ControlBase control : orderedControls) {
                offset = correctPosBecauseOfStretch(controlsPlaces, control, stretchableSpacerSize, offset);
            }
        }

        // 3. Applying the result
        List<ControlBase> allControls = getControls();
        for (ControlBase control : allControls) {
            control.setPosition(controlsPlaces.get(control));
        }
    }

    public List<ControlBase> getControls() {
        List<ControlBase> allControls = new ArrayList<ControlBase>(rightOrBottomControls);
        allControls.addAll(0, leftOrTopControls);
        return allControls;
    }

    public void paint(Graphics graphics) {
        List<ControlBase> sidebarControls = getControls();
        for (ControlBase control : sidebarControls) {
            control.paint(graphics);
        }
    }

    private void locateInitially(ControlBase control, HashMap<ControlBase, Rectangle> controlsPlaces,
            Rectangle workRect, boolean leftOrTop) {

        final int controlWidth = control.getIdealWidth();
        final int controlHeight = control.getIdealHeight();
        Rectangle controlRect = (Rectangle) workRect.clone();
        if (horizontal) {
            controlRect.width = controlWidth;
            if (leftOrTop) {
                workRect.x += controlWidth;
            } else {
                controlRect.x = workRect.x + workRect.width - controlWidth;
            }
            workRect.width -= controlWidth;
            // Vertical adjustment
            if (controlHeight > 0) {
                controlRect.y = controlRect.y + (controlRect.height - controlHeight) / 2;
                controlRect.height = controlHeight;
            }
        } else {
            controlRect.height = controlHeight;
            if (leftOrTop) {
                workRect.y += controlHeight;
            } else {
                controlRect.y = workRect.y + workRect.height - controlHeight;
            }
            workRect.height -= controlHeight;
            // Horizontal adjustment
            if (controlWidth > 0) {
                controlRect.x = controlRect.x + (controlRect.width - controlWidth) / 2;
                controlRect.width = controlWidth;
            }
        }
        controlsPlaces.put(control, controlRect);
    }

    private int correctPosBecauseOfStretch(HashMap<ControlBase, Rectangle> controlsPlaces, ControlBase control,
            int freeSpaceSize, int offset) {
        Rectangle rect = controlsPlaces.get(control);
        if (horizontal) {
            rect.x += offset;
        } else {
            rect.y += offset;
        }
        if (isStretchableSpacer(control)) {
            rect.width = freeSpaceSize;
            rect.height = freeSpaceSize;
            offset += freeSpaceSize;
        }
        return offset;
    }

    private static boolean isStretchableSpacer(ControlBase control) {
        return (control instanceof SpaceControl) && ((SpaceControl) control).isStretchable();
    }

    private boolean horizontal = false;
    private List<ControlBase> leftOrTopControls = new ArrayList<ControlBase>();
    private List<ControlBase> rightOrBottomControls = new ArrayList<ControlBase>();
}
