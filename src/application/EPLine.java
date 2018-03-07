/*
 *  Copyright (c) 2017-2018 Chiang Fong Sin
 *  @author Chiang Fong Sin
 */
package application;

import javafx.scene.shape.Line;

/*
 * Has to add to point.lines after constructor terminates and not within
 */
class EPLine extends Line {
	
	private Point start, end;
	
	EPLine(Point source, Point target) {
		start = source;
		end = target;
		updateLine();
		this.setStrokeWidth(2);
		Component.mg.makeClickable(this);
	}
	
	EPLine(Point source, Point target, double sourceX, double sourceY, double targetX, double targetY) {
		super(sourceX, sourceY, targetX, targetY);
		start = source;
		end = target;
		this.setStrokeWidth(2);
		Component.mg.makeClickable(this);
	}
	
	public Point getStart() { return start; }
	public void setStart(Point p) { start = p; updateLine(); }
	public Point getEnd() { return end; }
	public void setEnd(Point p) { end = p; updateLine(); }
	public boolean isStart(Point p) { return p == start; }
	public boolean isEnd(Point p) { return p == end; }
	
	public Point getOtherPoint(Point p) throws PointNotFoundException {
		if (p == start) {
			return end;
		} else if (p == end) {
			return start;
		} else {
			System.out.println("Point given not in this line");
			throw new PointNotFoundException("Point given not in this line");
		}
	}
	
	public void updateLine() {
		this.setStartX(start.parent.getTranslateX() + start.getCenterX());
		this.setStartY(start.parent.getTranslateY() + start.getCenterY());
		this.setEndX(end.parent.getTranslateX() + end.getCenterX());
		this.setEndY(end.parent.getTranslateY() + end.getCenterY());
	}
	
	public boolean updateLine(Point p) {
		if (this.isStart(p)) {
			this.setStartX(start.parent.getTranslateX() + start.getCenterX());
			this.setStartY(start.parent.getTranslateY() + start.getCenterY());
			return true;
		} else if (this.isEnd(p)){
			this.setEndX(end.parent.getTranslateX() + end.getCenterX());
			this.setEndY(end.parent.getTranslateY() + end.getCenterY());
			return true;
		} else {
			System.out.println("Error: Line does not know point");
			return false;
		}
	}
	
	/*
	 * return if hasChanged
	 */
	public boolean updateOtherPoint(Point p) {
		boolean hasChanged = false;
		Point otherPoint;
		try {
			otherPoint = this.getOtherPoint(p);
			if (p.getIsHigh() != otherPoint.getIsHigh()) {
				otherPoint.flipIsHigh();
				hasChanged = true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return hasChanged;
	}
}