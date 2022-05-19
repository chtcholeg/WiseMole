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

package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@GameState} class that store a state
 * 
 * @author olegshchepilov
 *
 */

public class GameState implements Cloneable {

    @Override
    public Object clone() {
        GameState result = new GameState();
        result.moleLocation = moleLocation == null ? null : (Point) moleLocation.clone();
        for (Point box : boxes) {
            result.boxes.add((Point) box.clone());
        }
        return result;
    }

    public Point moleLocation = null;
    public List<Point> boxes = new ArrayList<Point>();
}
