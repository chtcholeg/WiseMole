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

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import utils.ImageStorage;
import utils.RectangleUtils;

/**
 * The {@NumericLeftRightControl} is class for painting and processing
 * numeric-control.
 * 
 * @author olegshchepilov
 *
 */

public class NumericLeftRightControl extends ControlBase {
    final public static String TYPE = "NumericLeftRight";

    public interface Callback {
        public void onNumericControlValueChanged(String controlId, int value);
    }

    private enum Part {
        LEFT_BUTTON, RIGHT_BUTTON, SPACE
    }

    public NumericLeftRightControl(int min, int max, int initValue) {
        this(min, max, initValue, "", null);
    }

    public NumericLeftRightControl(int min, int max, int initValue, String controlId, Callback controlCallback) {
        super(TYPE, controlId);
        minValue = min;
        maxValue = max;
        setValue(initValue);
        label.setText(Integer.toString(value));
        callback = controlCallback;
    }

    public static int getImageWidth() {
        return controlImage.getWidth(null);
    }

    @Override
    public int getIdealHeight() {
        return controlImage.getHeight(null);
    }

    @Override
    public int getIdealWidth() {
        return controlImage.getWidth(null);
    }

    @Override
    public void onPositionChanged() {
        Rectangle labelPosition = (Rectangle) position.clone();
        RectangleUtils.deflateRect(labelPosition, LEFT_RIGHT_PADDING, TOP_BOTTOM_PADDING);
        label.setPosition(labelPosition);
    }

    @Override
    public int onMouseMove(Point mousePt) {
        return (calcControlPart(mousePt) == Part.SPACE) ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR;
    }

    @Override
    public void onMouseClick(Point mousePt) {
        switch (calcControlPart(mousePt)) {
            case LEFT_BUTTON:
                setValue(value - 1);
                break;
            case RIGHT_BUTTON:
                setValue(value + 1);
                break;
            default:
                break;
        }
    }

    @Override
    public void paint(Graphics graphics) {
        if (position == null || graphics == null) {
            return;
        }
        if (controlImage != null) {
            graphics.drawImage(controlImage, position.x, position.y, position.width, position.height, null);
        }
        label.paint(graphics);
    }

    private void setValue(int newValue) {
        newValue = Math.min(maxValue, Math.max(newValue, minValue));
        if (value != newValue) {
            value = newValue;
            label.setText(Integer.toString(value));
            if (callback != null) {
                callback.onNumericControlValueChanged(getId(), value);
            }
        }
    }

    private Part calcControlPart(Point mousePt) {
        if ((mousePt.y < 0) || (mousePt.y > position.height)) {
            return Part.SPACE;
        }
        if ((mousePt.x < 0) || (mousePt.x > position.width)) {
            return Part.SPACE;
        }
        if (mousePt.x < LEFT_RIGHT_PADDING) {
            return Part.LEFT_BUTTON;
        }
        if (mousePt.x > (position.width - LEFT_RIGHT_PADDING)) {
            return Part.RIGHT_BUTTON;
        }
        return Part.SPACE;
    }

    private Callback callback = null;
    private int value = 0;
    private int minValue = 0;
    private int maxValue = 100;
    private LabelControl label = new LabelControl("", LabelControl.Alignment.CENTER);
    private static int LEFT_RIGHT_PADDING = 20;
    private static int TOP_BOTTOM_PADDING = 4;
    private static Image controlImage = ImageStorage.getImage("numeric_left_right.png");
}
