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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;

import utils.ImageStorage;

/**
 * The {@ImageControl} is class for painting and processing image
 * 
 * @author olegshchepilov
 *
 */

public class ImageControl extends ControlBase {
    final public static String TYPE = "Image";

    public ImageControl(String resourceId) {
        this(resourceId, "", false);
    }

    public ImageControl(String resourceId, String controlId, boolean clickableValue) {
        super(TYPE, controlId);
        image = ImageStorage.getImage(resourceId);
        handCursor = clickableValue;
    }

    public void setSelection(boolean value) {
        selection = value;
    }

    @Override
    public int getIdealHeight() {
        return image != null ? image.getHeight(null) : 0;
    }

    @Override
    public int getIdealWidth() {
        return image != null ? image.getWidth(null) : 0;
    }

    @Override
    public void paint(Graphics graphics) {
        if (position == null || graphics == null) {
            return;
        }
        graphics.drawImage(image, position.x, position.y, position.width, position.height, null);
        if (selection) {
            Graphics2D graphics2d = (Graphics2D) graphics.create();
            Color color = new Color(50, 0, 0, 50);
            graphics2d.setColor(color);
            Stroke dashed = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
            graphics2d.setStroke(dashed);
            graphics2d.drawLine(position.x, position.y, position.x + position.width, position.y);
            graphics2d.drawLine(position.x + position.width, position.y, position.x + position.width,
                    position.y + position.height);
            graphics2d.drawLine(position.x + position.width, position.y + position.height, position.x,
                    position.y + position.height);
            graphics2d.drawLine(position.x, position.y + position.height, position.x, position.y);
            graphics2d.dispose();
        }
    }

    private Image image = null;
    private boolean selection = false;
}
