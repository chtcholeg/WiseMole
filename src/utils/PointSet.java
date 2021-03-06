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

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * The {@PointSet} is implementation of set of points
 *
 * @author olegshchepilov
 *
 */
public class PointSet {

    public PointSet() {

    }

    public PointSet(List<Point> pointList) {
        for (Point point : pointList) {
            add(point);
        }
    }

    public void add(Point point) {
        HashSet<Integer> set = pointSet.get(point.x);
        if (set == null) {
            set = new HashSet<Integer>();
            pointSet.put(point.x, set);
        }
        set.add(point.y);
    }

    public void remove(Point point) {
        HashSet<Integer> set = pointSet.get(point.x);
        if (set != null) {
            set.remove(point.y);
            if (set.isEmpty()) {
                pointSet.remove(point.x);
            }
        }
    }

    public boolean has(Point point) {
        HashSet<Integer> set = pointSet.get(point.x);
        return (set == null) ? false : set.contains(point.y);
    }

    private HashMap<Integer, HashSet<Integer>> pointSet = new HashMap<Integer, HashSet<Integer>>();
}
