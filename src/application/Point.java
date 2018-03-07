/*
 *  Copyright (c) 2017-2018 Chiang Fong Sin
 *  @author Chiang Fong Sin
 */
package application;

import java.util.ArrayList;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

class Point extends Circle {
	public Component parent;
	//private ArrayList<Point> connectedTo;	// also used to check for size for not connectedToed
	private ArrayList<EPLine> lines;
	//private ArrayList<Boolean> isSource;
	
	// Logical
	private boolean isHigh;
	
	Point(double centerX, double centerY, double radius, Component cpnt) {
		super(centerX, centerY, radius);
		parent = cpnt;
		//connectedTo = new ArrayList<Point>();
		lines = new ArrayList<EPLine>();
		//isSource = new ArrayList<Boolean>();
		
		isHigh = false;
	}
	
	// Setters and Getters
	//public void setConnectedTo(ArrayList<Point> p) { connectedTo = p; }
	//public ArrayList<Point> getConnectedTo() { return connectedTo; }
	public void setLines(ArrayList<EPLine> l) { lines = l; }
	public ArrayList<EPLine> getLines() { return lines; }
	//public void setIsSource(ArrayList<Boolean> bool) { isSource = bool; }
	//public ArrayList<Boolean> getIsSource() { return isSource; }
	public void flipIsHigh() { isHigh = !isHigh; }
	public void setIsHigh(boolean bool) { isHigh = bool; }
	public boolean getIsHigh() { return isHigh; }
	
	public void printState() {
		System.out.println("Point has " + lines.size());
	}
	
	/*
	 * Update both line graphics and logic
	 * return if causes change from high to low or low to high
	 */
	public boolean updateLines() {
		boolean hasChanged = false;	// considered no change if point unconnected
		for (EPLine line: lines) {
			if (line.updateLine(this) == true) {
				if (line.updateOtherPoint(this)) {
					hasChanged = true;
				}
			} else {
				System.out.println("Error: Point knows line but line don't know point");
			}
		}
		
		return hasChanged;
	}
	
	public void removeLine(Line l) {
		boolean found = false;
		for (int i = 0; i < lines.size() && !found; i++) {
			if (lines.get(i) == l) {
				//connectedTo.remove(i);
				lines.remove(i);
				//isSource.remove(i);
				found = true;
			}
		}
	}
	
	/*public void removeConnectedTo(Point p) {
		boolean found = false;
		for (int i = 0; i < connectedTo.size() && !found; i++) {
			if (connectedTo.get(i) == p) {
				connectedTo.remove(i);
				lines.remove(i);
				isSource.remove(i);
				found = true;
			}
		}
	}*/
	
	public void removingParent() {
		parent = null;
		while (lines.size() > 0) {
			Controller.getInstance().removeLine(lines.get(0));
		}
	}
	
	//TODO: eventlistener for state change and colour change when pressurised or not pressurised
}
