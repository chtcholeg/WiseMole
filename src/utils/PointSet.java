package utils;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
