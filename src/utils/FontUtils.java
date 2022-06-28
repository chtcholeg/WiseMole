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

package utils;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * The {@ImageStorage} is a set of font/text utils
 *
 * @author olegshchepilov
 *
 */

public class FontUtils {

    /*public static int getFontHeight(FontMetrics metrics) {
        final int originalFontHeight = metrics.getHeight();
        FontRenderContext context = metrics.getFontRenderContext();
        AffineTransform transform = context.getTransform();
        return (int) (originalFontHeight / transform.getScaleY());
    }*/

    public static int getFontHeight(Font font) {
    	AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
        Rectangle2D rect = font.getStringBounds("Wp", frc);
        return Math.abs((int) rect.getY());
    }
    
    public static int calculateTextWidth(Font font, String text) {
        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
        Rectangle2D rect = font.getStringBounds(text, frc);
        return (int) rect.getWidth();
    }
}
