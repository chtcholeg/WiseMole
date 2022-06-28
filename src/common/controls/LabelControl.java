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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Objects;

import common.ApplicationDefines;
import utils.FontUtils;

/**
 * The {@LabelControl} is class for painting text control.
 * 
 * @author olegshchepilov
 *
 */

public class LabelControl extends ControlBase {
    final public static String TYPE = "Label";

    public enum Alignment {
        LEFT, CENTER
    }

    public LabelControl(String labelText, Alignment alignment, Integer fixWidth) {
        super(TYPE, "");
        setText(labelText);
        setAlignment(alignment);
        setFixWidth(fixWidth);
    }

    public LabelControl(String labelText, Alignment alignment) {
        this(labelText, alignment, null);
    }

    public LabelControl(String labelText) {
        this(labelText, Alignment.LEFT);
    }

    public LabelControl() {
        this("");
    }

    public void setText(String labelText) {
        if (!Objects.equals(text, labelText)) {
            text = labelText;
        }
    }

    public void setAlignment(Alignment newAlignment) {
        alignment = newAlignment;
    }

    public void setFixWidth(Integer newFixWidth) {
        fixWidth = newFixWidth;
    }

    @Override
    public int getIdealHeight() {
        return 20;
    }

    @Override
    public int getIdealWidth() {
        if (fixWidth != null) {
            return fixWidth;
        }
        Font font = ApplicationDefines.font;
        if (font == null || text == null) {
            return 100;
        }
        return FontUtils.calculateTextWidth(font, text);
    }

    @Override
    public void paint(Graphics graphics) {
        if (position == null || graphics == null) {
            return;
        }
        int y = position.y + position.height;
        FontMetrics metrics = graphics.getFontMetrics();
        final int fontHeight = FontUtils.getFontHeight(graphics.getFont());
        y -= (position.height - fontHeight) / 2;
        int x = position.x;
        if (alignment == Alignment.CENTER) {
            final int width = metrics.stringWidth(text);
            x += (position.width - width) / 2;
        }
        graphics.drawString(text, x, y);
    }

    private String text = null;
    private Alignment alignment = Alignment.LEFT;
    private Integer fixWidth = null;
}
