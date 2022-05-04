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

import java.awt.Rectangle;

/**
 * The {@RectangleUtils} is a bunch of useful functions
 *
 * @author olegshchepilov
 *
 */
public class RectangleUtils {
    static public void deflateRect(Rectangle rect, int dx, int dy) {
        deflateRect(rect, dx, dy, dx, dy);
    }

    static public void deflateRect(Rectangle rect, int left, int top, int right, int bottom) {
        rect.x += left;
        rect.y += top;
        rect.width -= (left + right);
        rect.height -= (top + bottom);
    }

    static public void deflateRect(Rectangle rect, Margins margins) {
        deflateRect(rect, margins.getLeft(), margins.getTop(), margins.getRight(), margins.getBottom());
    }
}
