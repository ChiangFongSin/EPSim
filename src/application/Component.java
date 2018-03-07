/*
 *  Copyright (c) 2017-2018 Chiang Fong Sin
 *  @author Chiang Fong Sin
 */
package application;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

/*
 *  Pane chosen instead of Canvas after trying both because Pane exposes children
 */
class Component extends Pane {
	// Common
	static int paneHeight = 100;
	static int paneWidth = 100;
	static int pointRad = 3;
	
	// Error messages
	final static String ERROR_MISSING_CONNECTIONS = ": Missing point connections";
	final static String ERROR_UPDATE_COMPONENT = ": Error in updateState";
	final static String ERROR_ILLEGAL_ENDTYPE = ": Illegal EndType";
	final static String ERROR_ILLEGAL_STATE = ": Illegal state";
	final static String WARNING_NO_EFFECT = ": Warning no effect";
	
	// Magic numbers
	final static double STROKE_WIDTH = 1.5;
	
	final static Color COLOR_DEFAULT = Color.BLACK;
	final static Color COLOR_HIGH = Color.BLUE;
	final static Color COLOR_LOW = Color.DEEPSKYBLUE;
	final static Color COLOR_ACTIV = Color.LIMEGREEN;
	final static Color COLOR_DEACTIV = Color.DARKGREEN;
	final static Color COLOR_TRANS = Color.TRANSPARENT;
	
	// Logical
	protected int state = 0;	// 0 is initial, < 0 are illegal states
	protected Label labelOne, labelTwo;	// electrical lettering
	
	// Mouse
	public boolean isDisplay;
	public static MouseGestures mg = new MouseGestures();
	public Shape mainBody;
	public ArrayList<Point> points;
	
	// Type of valves
	public enum EndType {
		NA, PILOT_VALVE, SOLENOID, SPRING, PUSH_BUTTON;
	}
	
	// Controller
	protected static Controller controller = Controller.getInstance();
	
	public Component() {
		this(paneWidth, paneHeight);
		constructHelper();
	}
	public Component(double width, double height) {
		constructHelper();
		this.setPrefWidth(width);
		this.setPrefHeight(height);
	}
	public void constructHelper() {
		points = new ArrayList<Point>();
		isDisplay = false;
	}
	
	public ArrayList<Point> getPoints() { return points; }
	public int getPointIndex(Point p) { return points.indexOf(p); }
	
	/*
	 *  For Panes, positive-y direction is downwards
	 */
	public Polygon drawArrowHead(double startX, double startY, double endX, double endY) {
		double diffY = endY-startY;
		double diffX = endX-startX;
		
		double angle = Math.atan2(-(diffY), diffX);
		double arrowAngle = Math.toRadians(15.0);		// for one side, but there are two sides
		double base = Math.sqrt(diffY*diffY + diffX*diffX)/5.0;
		
		double point1x, point1y, point2x, point2y;
		double principal, h;
		
		if (angle < -Math.PI/2) {
			principal = Math.PI+angle;
			h = base/Math.cos(arrowAngle);
			point1x = endX + h*Math.cos(principal-arrowAngle);
			point2x = endX + h*Math.cos(principal+arrowAngle);
			point1y = endY - h*Math.sin(principal-arrowAngle);
			point2y = endY - h*Math.sin(principal+arrowAngle);
		} else if (angle < 0) {
			principal = -angle;
			h = base/Math.cos(arrowAngle);
			point1x = endX - h*Math.cos(principal-arrowAngle);
			point2x = endX - h*Math.cos(principal+arrowAngle);
			point1y = endY - h*Math.sin(principal-arrowAngle);
			point2y = endY - h*Math.sin(principal+arrowAngle);
		} else if (angle > Math.PI/2) {
			principal = Math.PI-angle;
			h = base/Math.cos(arrowAngle);
			point1x = endX + h*Math.cos(principal-arrowAngle);
			point2x = endX + h*Math.cos(principal+arrowAngle);
			point1y = endY + h*Math.sin(principal-arrowAngle);
			point2y = endY + h*Math.sin(principal+arrowAngle);
		} else {
			principal = angle;
			h = base/Math.cos(arrowAngle);
			point1x = endX - h*Math.cos(principal-arrowAngle);
			point2x = endX - h*Math.cos(principal+arrowAngle);
			point1y = endY + h*Math.sin(principal-arrowAngle);
			point2y = endY + h*Math.sin(principal+arrowAngle);
		}
		
		/*if (Main.DEBUG) {
			System.out.println("principal=" + principal + "(" + (int) point1x + "," + (int) point1y + ")-(" + (int) point2x + "," + (int) point2y + ")");
		}*/
		
		Polygon arrowHead = new Polygon(new double[] {
				point1x, point1y,
				point2x, point2y,
				endX, endY
		});
		
		return arrowHead;
	}
	
	protected void addMouseGestures() {
		mainBody.toFront();
		mg.makeMousable(this);
    	for (int i = 0; i < points.size(); i++) {
    		points.get(i).toFront();
			mg.makeClickable(points.get(i));
		}
	}
	
	public Text createText(String string) {
        Text text = new Text(string);
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setStyle(
                "-fx-font-family: \"Times New Roman\";" +
                "-fx-font-style: italic;" +
                "-fx-font-size: 48px;"
        );

        return text;
    }
	
	/*
	 *  a = swap(b, b = a);
	 */
	<T> T swap(T a, T b) {
		return a;
	}
	
	protected void updateDrawing() {
		Main.popUp("Error: Component.updateDrawing() called");
	}
	
	public boolean updateState() {
		Main.popUp("Error: Component.updateState() called");
		return false;
	}
	
	public boolean propagatePressure() {
		Main.popUp("Error: Component.propagatePressure() called");
		return false;
	}
}


class Compressor extends Component {
	public Polygon polygon;
	public Point point;
	public Rectangle rect;
	public Compressor() {	// default is not display
		super(paneWidth/paneWidth*25, paneHeight/paneHeight*25);
		draw();
		this.getChildren().addAll(polygon, point);
		addMouseGestures();
	}
	
	public Compressor(boolean display) {
		super(paneWidth/paneWidth*25, paneHeight/paneHeight*25);
		isDisplay = display;
		draw();
		this.getChildren().addAll(polygon, point);
		if (isDisplay) {
			mg.addComponentOnClick(this);
		} else {
			addMouseGestures();
		}
	}
	
	/*
	 *  procedure to draw the component
	 */
	private void draw() {
		double width = this.getPrefWidth();
		double height = this.getPrefHeight();
		
		polygon = new Polygon(new double[] {
				0, height, 
				width, height, 
				width/2, 0
		});
		polygon.setStroke(COLOR_HIGH);
		polygon.setStrokeWidth(STROKE_WIDTH);
		polygon.setFill(COLOR_TRANS);
		
		point = new Point(width/2, 0, pointRad, this);
		point.setFill(COLOR_HIGH);
		point.setIsHigh(true);
		
		mainBody = polygon;
		points.add(point);
	}
	
	/*
	 * (non-Javadoc)
	 * @see application.Component#updateDrawing()
	 */
	protected void updateDrawing() {}
	
	/*
	 * return false since no change
	 * 
	 * (non-Javadoc)
	 * @see application.Component#updateState()
	 */
	public boolean updateState() { return false; }
	
	/*
	 * return if this component experience change
	 * 
	 * (non-Javadoc)
	 * @see application.Component#propagatePressure()
	 */
	public boolean propagatePressure() {
		boolean hasChanged = false;
		point.setIsHigh(true);
		
		if (point.getLines().size() <= 0) {
			Main.popUp(this.toString() + Component.ERROR_MISSING_CONNECTIONS);
		} else {
			hasChanged = point.updateLines();
		}
		
		return hasChanged;
	}
}

class Exhaust extends Component {
	public Polygon polygon;
	public Point point;
	public Exhaust() {
		super(paneWidth/paneWidth*25, paneHeight/paneHeight*25);
		draw();
		this.getChildren().addAll(polygon, point);
		addMouseGestures();
	}
	
	public Exhaust(boolean display) {
		super(paneWidth/paneWidth*25, paneHeight/paneHeight*25);
		isDisplay = display;
		draw();
		this.getChildren().addAll(polygon, point);
		if (isDisplay) {
			mg.addComponentOnClick(this);
		} else {
			addMouseGestures();
		}
	}
	
	/*
	 *  procedure to draw the component
	 */
	private void draw() {
		double width = this.getPrefWidth();
		double height = this.getPrefHeight();
		
		polygon = new Polygon(new double[] {
				0, 0,
				width, 0,
				width/2, height
		});
    	
		polygon.setStroke(COLOR_LOW);
		polygon.setFill(COLOR_TRANS);
		polygon.setStrokeWidth(STROKE_WIDTH);
		
		point = new Point(width/2, 0, pointRad, this);
		point.setFill(COLOR_LOW);
		point.setIsHigh(false);
        
		mainBody = polygon;
		points.add(point);
		
        return;
	}

	protected void updateDrawing() {}
	
	/*
	 * return false since no change
	 * 
	 * (non-Javadoc)
	 * @see application.Component#updateState()
	 */
	public boolean updateState() { return false; }
	
	/*
	 * return if this component experience change
	 * 
	 * (non-Javadoc)
	 * @see application.Component#propagatePressure()
	 */
	public boolean propagatePressure() {
		boolean hasChanged = false;
		point.setIsHigh(false);
		
		if (point.getLines().size() <= 0) {
			Main.popUp(this.toString() + Component.ERROR_MISSING_CONNECTIONS);
		} else {
			hasChanged = point.updateLines();
		}
		
		return hasChanged;
	}
}

class Cylinder extends Component {
	public Rectangle mainRect;
	public Polygon polygon;
	public Point point1, point2;
	public Rectangle rect1, rect2;
	public Line line1, line2;
	
	private boolean switchOne, switchTwo;
	
	// Context Menu
	protected Shape hiddenShapeOne, hiddenShapeTwo;
	
	public Cylinder(boolean so, boolean st) {
		super(paneWidth*1.2, paneHeight/2);
		switchOne = so;
		switchTwo = st;
		
		if (switchOne) {
			labelOne = new Label("a1");
			Main.inputForm(labelOne, "Switch One");
			System.out.println(labelOne.getText());
			controller.setSolenoid(labelOne.getText(), true);
		}
		if (switchTwo) {
			labelTwo = new Label("a2");
			Main.inputForm(labelTwo, "Switch Two");
			System.out.println(labelTwo.getText());
			controller.setSolenoid(labelTwo.getText(), false);
		}
		
		draw();
		
		this.getChildren().addAll(polygon, mainRect, point1, point2);
		if (switchOne) {
			this.getChildren().addAll(rect1, line1, labelOne, hiddenShapeOne);
		}
		if (switchTwo) {
			this.getChildren().addAll(rect2, line2, labelTwo, hiddenShapeTwo);
		}
		
		state = 1;
		addMouseGestures();
		mg.addLabelMenu(this);
	}
	public Cylinder(boolean so, boolean st, boolean display) {
		super(paneWidth*1.2, paneHeight/2);
		switchOne = so;
		switchTwo = st;
		isDisplay = display;
		
		if (display) {
			labelOne = new Label("a1");
			labelTwo = new Label("a2");
		}
		
		draw();
		
		this.getChildren().addAll(polygon, mainRect, point1, point2);
		if (switchOne) {
			this.getChildren().addAll(rect1, line1, labelOne, hiddenShapeOne);
		}
		if (switchTwo) {
			this.getChildren().addAll(rect2, line2, labelTwo, hiddenShapeTwo);
		}
		
		state = 1;
		if (isDisplay) {
			mg.addComponentOnClick(this);
		} else {
			addMouseGestures();
			mg.addLabelMenu(this);
		}
	}
	public Cylinder(boolean so, boolean st, String strOne, String strTwo) {
		super(paneWidth*1.2, paneHeight/2);
		switchOne = so;
		switchTwo = st;
		
		if (switchOne) {
			labelOne = new Label(strOne);
			controller.setSolenoid(labelOne.getText(), true);
		}
		if (switchTwo) {
			labelTwo = new Label(strTwo);
			controller.setSolenoid(labelTwo.getText(), false);
		}
		
		draw();
		
		this.getChildren().addAll(polygon, mainRect, point1, point2);
		if (switchOne) {
			this.getChildren().addAll(rect1, line1, labelOne, hiddenShapeOne);
		}
		if (switchTwo) {
			this.getChildren().addAll(rect2, line2, labelTwo, hiddenShapeTwo);
		}
		
		state = 1;
		addMouseGestures();
		mg.addLabelMenu(this);
	}
	
	public boolean getSwitchOne() { return switchOne; }
	public boolean getSwitchTwo() { return switchTwo; }
	
	/*
	 *  Pre-condition: labelOne and labelTwo be instantiated
	 *  procedure to draw the component
	 */
	private void draw() {
		double width = this.getPrefWidth();
		double unitWidth = width/12.0;
		double height = this.getPrefHeight();
		
		mainRect = new Rectangle(width/2, height);
		mainRect.setStroke(COLOR_DEFAULT);
		mainRect.setStrokeWidth(STROKE_WIDTH);
		mainRect.setFill(COLOR_TRANS);
		
		polygon = new Polygon(new double[] {
				unitWidth, 0,
				unitWidth*2, 0,
				unitWidth*2, height*0.45,
				unitWidth*8, height*0.45,
				unitWidth*8, height*0.65,
				unitWidth*7.5, height*0.55,
				unitWidth*2, height*0.55,
				unitWidth*2, height,
				unitWidth, height
		});
		
		if (switchOne) {
			rect1 = new Rectangle(unitWidth*7, 0, unitWidth*2, height*0.3);
			rect1.setStroke(COLOR_ACTIV);
			rect1.setStrokeWidth(STROKE_WIDTH);
			rect1.setFill(COLOR_TRANS);
			line1 = new Line(unitWidth*8, height*0.3, unitWidth*7, height*0.5);
			line1.setStroke(COLOR_ACTIV);
			line1.setStrokeWidth(STROKE_WIDTH);
			labelOne.setTranslateX(unitWidth*7.3);
			labelOne.setTranslateY(0);
			hiddenShapeOne = new Rectangle(unitWidth*7, 0, unitWidth*2, height*0.3);
			hiddenShapeOne.setFill(COLOR_TRANS);
		}

		if (switchTwo) {
			rect2 = new Rectangle(unitWidth*10, 0, unitWidth*2, height*0.3);
			rect2.setStroke(COLOR_DEFAULT);
			rect2.setStrokeWidth(STROKE_WIDTH);
			rect2.setFill(COLOR_TRANS);
			line2 = new Line(unitWidth*11, height*0.3, unitWidth*12, height*0.5);
			line2.setStrokeWidth(STROKE_WIDTH);
			labelTwo.setTranslateX(unitWidth*10.3);
			labelTwo.setTranslateY(0);
			hiddenShapeTwo = new Rectangle(unitWidth*10, 0, unitWidth*2, height*0.3);
			hiddenShapeTwo.setFill(COLOR_TRANS);
		}
		
		point1 = new Point(unitWidth/2, height, pointRad, this);
		point2 = new Point(unitWidth*5.5, height, pointRad, this);
		
		mainBody = mainRect;
		points.add(point1);
		points.add(point2);
    	
    	return;
	}
	
	/*
	 * (non-Javadoc)
	 * @see application.Component#updateDrawing()
	 */
	protected void updateDrawing() {
		switch(state) {
		case 1:
			polygon.setTranslateX(0);
			if (switchOne) {
				rect1.setStroke(COLOR_ACTIV);
				line1.setStroke(COLOR_ACTIV);
			}
			if (switchTwo) {
				rect2.setStroke(COLOR_DEFAULT);
				line2.setStroke(COLOR_DEFAULT);
			}
			break;
		case 2:
			polygon.setTranslateX(this.getPrefWidth()*0.3);
			if (switchOne) {
				rect1.setStroke(COLOR_DEFAULT);
				line1.setStroke(COLOR_DEFAULT);
			}
			if (switchTwo) {
				rect2.setStroke(COLOR_ACTIV);
				line2.setStroke(COLOR_ACTIV);
			}
			break;
		case 0:
			if (switchOne) {
				rect1.setStroke(COLOR_DEFAULT);
				line1.setStroke(COLOR_DEFAULT);
			}
			if (switchTwo) {
				rect2.setStroke(COLOR_DEFAULT);
				line2.setStroke(COLOR_DEFAULT);
			}
			break;
		default:
			Main.popUp(this.toString() + Component.ERROR_ILLEGAL_STATE);
		}
	}
	
	/*
	 * update component given pressures at point1 and point2
	 * pre-condition: proper running of pressure propagation
	 * 
	 * (non-Javadoc)
	 * @see application.Component#updateState()
	 */
	public boolean updateState() {
		int prevState = state;
		
		if (!point1.getIsHigh() && point2.getIsHigh()) {
			state = 1;
			if (null != labelOne) {
				Boolean prevActiv = controller.getSolenoid(labelOne.getText());
				if (prevActiv == null || prevActiv != true) {
					controller.addSequence(labelOne.getText());
				}
				controller.setSolenoid(labelOne.getText(), true);
			}
			if (null != labelTwo) {
				controller.setSolenoid(labelTwo.getText(), false);
			}
		} else if (point1.getIsHigh() && !point2.getIsHigh()) {
			state = 2;
			if (null != labelOne) {
				controller.setSolenoid(labelOne.getText(), false);
			}
			if (null != labelTwo) {
				Boolean prevActiv = controller.getSolenoid(labelTwo.getText());
				if (prevActiv == null || prevActiv != true) {
					controller.addSequence(labelTwo.getText());
				}
				controller.setSolenoid(labelTwo.getText(), true);
			}
		} else {
			System.out.println(this.toString() + Component.WARNING_NO_EFFECT);
		}
		
		this.updateDrawing();
		
		return prevState != state;
	}
	
	/*
	 * return false since no change
	 * 
	 * (non-Javadoc)
	 * @see application.Component#propagatePressure()
	 */
	public boolean propagatePressure() { return false; }
}

/*
 * class to contain elements common to all valves
 */
class Valve extends Component {
	// EndType shapes should be the shape 
	protected ArrayList<Shape> endShapeOne, endShapeTwo;
	// EndType attributes
	// Pilot & solenoid
	protected Rectangle rect;
	// Pilot
	protected Point pointOne, pointTwo;
	// Spring
	protected Polyline polylineOne, polylineTwo;
	// Push button
	protected Polygon pushButtonOne, pushButtonTwo;
	// Context Menu
	protected Shape hiddenEndShapeOne, hiddenEndShapeTwo;
	protected EndType endTypeOne, endTypeTwo;
	
	protected Valve() {
		super();
	}
	protected Valve(double width, double height) {
		super(width, height);
	}
	
	public EndType getEndTypeOne() { return endTypeOne; }
	public EndType getEndTypeTwo() { return endTypeTwo; }
	public void setEndTypeOne(EndType o) { endTypeOne = o; }
	public void setEndTypeTwo(EndType t) { endTypeTwo = t; }	
}

/*
 * 			 _____________
 * 			|	   |	  |
 * 		One	|	   |	  | Two
 * 			|______|______|
 * 			State 1	State 2
 * Added to allow permutations in One & Two as solenoid, spring, or push button
 * TODO: implement various endTypes, TextFlow
 */
class TwoOrientValve extends Valve {
	protected TwoOrientValve(EndType o, EndType t) {
		super();
		endTypeOne = o;
		endTypeTwo = t;
	}
	protected TwoOrientValve(EndType o, EndType t, double width, double height) {
		super(width,height);
		endTypeOne = o;
		endTypeTwo = t;
	}
	
	/*
	 * Pre-condition: labelOne and labelTwo be instantiated accordingly
	 * translate accurate for twoOrientValves only
	 * Difference between drawEndTypeOne & drawEndTypeTwo should be orientation, translateX, and translateY
	 * Order is important to show which shape on top
	 */
	protected void drawEndTypeOne(boolean userInput, double width, double height, double unitWidth, double unitHeight) {
		endShapeOne = new ArrayList<Shape>();
		
		switch (endTypeOne) {
		case NA:
			Main.popUp(this.toString() + ERROR_ILLEGAL_ENDTYPE);
			break;
		case PILOT_VALVE:
			rect = new Rectangle(0, unitHeight*3, unitWidth*2, unitHeight*6);
	    	rect.setStroke(COLOR_DEFAULT);
	    	rect.setFill(COLOR_TRANS);
	    	endShapeOne.add(rect);
	    	
	    	pointOne = new Point(0, height/2, pointRad, this);
	    	endShapeOne.add(pointOne);
	    	points.add(pointOne);
			break;
		case SOLENOID:
			rect = new Rectangle(0, unitHeight*3, unitWidth*2, unitHeight*6);
	    	rect.setStroke(COLOR_DEFAULT);
	    	rect.setFill(COLOR_TRANS);
	    	endShapeOne.add(rect);
	    	
	    	if (userInput) {
	    		labelOne = new Label("a1");
	    		Main.inputForm(labelOne, "Solenoid One");
	    	}
	    	labelOne.setTranslateX(unitWidth*0.25);
	    	labelOne.setTranslateY(unitHeight*4);
			break;
		case SPRING:
			polylineOne = new Polyline(new double [] {
					0, height/2,
					unitWidth*0.5, height*0.25,
					unitWidth, height*0.75,
					unitWidth*1.5, height*0.25,
					unitWidth*2, height/2
			});
			endShapeOne.add(polylineOne);
			break;
		case PUSH_BUTTON:
			pushButtonOne = new Polygon(new double [] {
					0, height*0.25,
					unitWidth*0.5, height*0.25,
					unitWidth*0.5, height*0.45,
					unitWidth*2, height*0.45,
					unitWidth*2, height*0.55,
					unitWidth*0.5, height*0.55,
					unitWidth*0.5, height*0.75,
					0, height*0.75
			});
			endShapeOne.add(pushButtonOne);
			
			if (userInput) {
	    		labelOne = new Label("START");
	    		Main.inputForm(labelOne, "Push Button One");
	    	}
	    	labelOne.setTranslateX(unitWidth*0);
	    	labelOne.setTranslateY(unitHeight*-1);
			break;
		default:
			System.out.println("Error in drawEndTypeOne: unexpected endTypeOne");
		}
		hiddenEndShapeOne = new Rectangle(unitWidth*2, height);
		hiddenEndShapeOne.setFill(COLOR_TRANS);
		endShapeOne.add(hiddenEndShapeOne);
	}
	
	protected void drawEndTypeTwo(boolean userInput, double width, double height, double unitWidth, double unitHeight) {
		endShapeTwo = new ArrayList<Shape>();
		switch (endTypeTwo) {
		case NA:
			Main.popUp(this.toString() + ERROR_ILLEGAL_ENDTYPE);
			break;
		case PILOT_VALVE:
			rect = new Rectangle(unitWidth*10, unitHeight*3, unitWidth*2, unitHeight*6);
	    	rect.setStroke(COLOR_DEFAULT);
	    	rect.setFill(COLOR_TRANS);
	    	endShapeTwo.add(rect);
	    	
	    	pointTwo = new Point(width, height/2, pointRad, this);
	    	endShapeTwo.add(pointTwo);
	    	points.add(pointTwo);
			break;
		case SOLENOID:
			if (userInput) {
				labelTwo = new Label("a2");
				Main.inputForm(labelTwo, "Solenoid Two");
			}
			labelTwo.setTranslateX(unitWidth*10.25);
	    	labelTwo.setTranslateY(unitHeight*4);
			
	    	Rectangle rect = new Rectangle(unitWidth*10, unitHeight*3, unitWidth*2, unitHeight*6);
	    	rect.setStroke(COLOR_DEFAULT);
	    	rect.setFill(COLOR_TRANS);
	    	endShapeTwo.add(rect);
			break;
		case SPRING:
			polylineTwo = new Polyline(new double [] {
					unitWidth*10, height/2,
					unitWidth*10.5, height*0.25,
					unitWidth*11, height*0.75,
					unitWidth*11.5, height*0.25,
					width, height/2
			});
			endShapeTwo.add(polylineTwo);
			break;
		case PUSH_BUTTON:
			pushButtonTwo = new Polygon(new double [] {
					width, height*0.25,
					unitWidth*11.5, height*0.25,
					unitWidth*11.5, height*0.45,
					unitWidth*10, height*0.45,
					unitWidth*10, height*0.55,
					unitWidth*11.5, height*0.55,
					unitWidth*11.5, height*0.75,
					width, height*0.75
			});
			endShapeTwo.add(pushButtonTwo);
			
			if (userInput) {
				labelTwo = new Label("STOP");
				Main.inputForm(labelTwo, "Push Button Two");
			}
			labelTwo.setTranslateX(unitWidth*10.25);
	    	labelTwo.setTranslateY(unitHeight*-1);
			break;
		default:
			System.out.println("Error in drawEndTypeTwo: unexpected endTypeTwo");
		}
		hiddenEndShapeTwo = new Rectangle(unitWidth*10, 0, unitWidth*2, height);
		hiddenEndShapeTwo.setFill(COLOR_TRANS);
		endShapeTwo.add(hiddenEndShapeTwo);
	}
	
}

/*
 *			  1      1
 * 			 _____________
 * 			| ^	   |	  |
 * 		One	| |	   |	  | Two
 * 			|_|____|______|
 * 			  2  3   2  3
 * 
 *			  0      3
 * 			 _____________
 * 			| ^	   |	  |
 * 			| |	   |	  | 
 * 			|_|____|______|
 * 			  1  2   4  5
 */

class ThreeTwo extends TwoOrientValve {
	public Rectangle rect;
	public Line line;
	
	public Line lineOne1, lineOne2, lineOne3;
	public Line lineTwo1, lineTwo2, lineTwo3;
	
	public Point pointOne1, pointOne2, pointOne3;
	public Point pointTwo1, pointTwo2, pointTwo3;
	
	public Polygon polygonOne, polygonTwo;
	
	public ThreeTwo(EndType o, EndType t) {
		super(o, t, paneWidth, paneHeight*0.5);
		draw(true);
		if (endTypeOne == EndType.PILOT_VALVE || endTypeOne == EndType.SOLENOID
				|| endTypeOne == EndType.PUSH_BUTTON) {
			this.getChildren().add(labelOne);
		}
		if (endTypeTwo == EndType.PILOT_VALVE || endTypeTwo == EndType.SOLENOID
				|| endTypeTwo == EndType.PUSH_BUTTON) {
			this.getChildren().add(labelTwo);
		}
		this.getChildren().addAll(
				line, lineOne1, lineOne2, lineOne3, lineTwo1, lineTwo2, lineTwo3, 
				polygonOne, polygonTwo, rect, 
				pointOne1, pointOne2, pointOne3, 
				pointTwo1, pointTwo2, pointTwo3);
		this.getChildren().addAll(endShapeOne);
		this.getChildren().addAll(endShapeTwo);
		
		addMouseGestures();
		mg.addLabelMenu(this);
	}
	public ThreeTwo(EndType o, EndType t, String strOne, String strTwo) {
		super(o, t, paneWidth, paneHeight*0.5);
		labelOne = new Label(strOne);
		labelTwo = new Label(strTwo);
		draw(false);
		if (endTypeOne == EndType.PILOT_VALVE || endTypeOne == EndType.SOLENOID
				|| endTypeOne == EndType.PUSH_BUTTON) {
			this.getChildren().add(labelOne);
		}
		if (endTypeTwo == EndType.PILOT_VALVE || endTypeTwo == EndType.SOLENOID
				|| endTypeTwo == EndType.PUSH_BUTTON) {
			this.getChildren().add(labelTwo);
		}
		this.getChildren().addAll(
				line, lineOne1, lineOne2, lineOne3, lineTwo1, lineTwo2, lineTwo3, 
				polygonOne, polygonTwo, rect, 
				pointOne1, pointOne2, pointOne3, 
				pointTwo1, pointTwo2, pointTwo3);
		this.getChildren().addAll(endShapeOne);
		this.getChildren().addAll(endShapeTwo);

		addMouseGestures();
		mg.addLabelMenu(this);
	}
	public ThreeTwo(EndType o, EndType t, boolean display) {
		super(o, t, paneWidth, paneHeight*0.5);
		isDisplay = display;
		if (isDisplay) {
			labelOne = new Label("a1");
			labelTwo = new Label("a2");
		}
		draw(!isDisplay);
		if (endTypeOne == EndType.PILOT_VALVE || endTypeOne == EndType.SOLENOID
				|| endTypeOne == EndType.PUSH_BUTTON) {
			this.getChildren().add(labelOne);
		}
		if (endTypeTwo == EndType.PILOT_VALVE || endTypeTwo == EndType.SOLENOID
				|| endTypeTwo == EndType.PUSH_BUTTON) {
			this.getChildren().add(labelTwo);
		}
		this.getChildren().addAll(
				line, lineOne1, lineOne2, lineOne3, lineTwo1, lineTwo2, lineTwo3, 
				polygonOne, polygonTwo, rect, 
				pointOne1, pointOne2, pointOne3, 
				pointTwo1, pointTwo2, pointTwo3);
		this.getChildren().addAll(endShapeOne);
		this.getChildren().addAll(endShapeTwo);
		
		if (isDisplay) {
			mg.addComponentOnClick(this);
		} else {
			addMouseGestures();
			mg.addLabelMenu(this);
		}
		
	}
	
	/*
	 *  procedure to draw the component
	 */
	private void draw(boolean userInput) {
		double width = this.getPrefWidth();
		double unitWidth = width/12.0;
		double height = this.getPrefHeight();
		double unitHeight = height/12.0;
		
		rect = new Rectangle(unitWidth*2, 0, unitWidth*8, height);
		
		rect.setStroke(COLOR_DEFAULT);
		rect.setFill(COLOR_TRANS);
		
		line = new Line(width/2, 0, width/2, height);
		line.setStroke(COLOR_DEFAULT);
		
    	// Position One
    	lineOne1 = new Line(unitWidth*3, 0, unitWidth*3, height);
    	lineOne1.setStroke(COLOR_DEFAULT);
    	polygonOne = drawArrowHead(unitWidth*3, height, unitWidth*3, 0);
    	
    	lineOne2 = new Line(unitWidth*5, height*0.8, unitWidth*5, height);
    	lineOne2.setStroke(COLOR_DEFAULT);
    	lineOne3 = new Line(unitWidth*4.5, height*0.8, unitWidth*5.5, height*0.8);
    	lineOne3.setStroke(COLOR_DEFAULT);
    	
    	drawEndTypeOne(userInput, width, height, unitWidth, unitHeight);
    	pointOne1 = new Point(unitWidth*3, 0, pointRad, this);
    	pointOne2 = new Point(unitWidth*3, height, pointRad, this);
    	pointOne3 = new Point(unitWidth*5, height, pointRad, this);
    	
    	// Position Two
    	lineTwo1 = new Line(unitWidth*7, 0, unitWidth*9, height);
    	lineTwo1.setStroke(COLOR_DEFAULT);
    	polygonTwo = drawArrowHead(unitWidth*7, 0, unitWidth*9, height);
    	
    	lineTwo2 = new Line(unitWidth*7, height*0.8, unitWidth*7, height);
    	lineTwo2.setStroke(COLOR_DEFAULT);
    	lineTwo3 = new Line(unitWidth*6.5, height*0.8, unitWidth*7.5, height*0.8);
    	lineTwo3.setStroke(COLOR_DEFAULT);
    	
    	drawEndTypeTwo(userInput, width, height, unitWidth, unitHeight);
    	pointTwo1 = new Point(unitWidth*7, 0, pointRad, this);
    	pointTwo2 = new Point(unitWidth*7, height, pointRad, this);
    	pointTwo3 = new Point(unitWidth*9, height, pointRad, this);
    	
    	mainBody = rect;
    	// Order of adding is important
    	points.add(pointOne1);
    	points.add(pointOne2);
    	points.add(pointOne3);
    	points.add(pointTwo1);
    	points.add(pointTwo2);
    	points.add(pointTwo3);
    	
    	return;
	}
	
	private void swapOrientPoints() {
		// TODO: swap other orient lines back or just expect them to not be connected?
		for (int i = 0; i < 3; i++) {
			Point pOne = points.get(i), pTwo = points.get(i+3);
			ArrayList<EPLine> linesOne = pOne.getLines(), linesTwo = pTwo.getLines();
			
			// TODO: 
			// swap the lines, though ideally only one orient has lines()
			for (int j = 0; j < linesOne.size(); j++) {
				if (linesOne.get(j).isStart(pOne)) {
					linesOne.get(j).setStart(pTwo);
				} else if (linesOne.get(j).isEnd(pOne)) {
					linesOne.get(j).setEnd(pTwo);
				}
			}
			
			for (int j = 0; j < linesTwo.size(); j++) {
				if (linesTwo.get(j).isStart(pTwo)) {
					linesTwo.get(j).setStart(pOne);
				} else if (linesTwo.get(j).isEnd(pTwo)) {
					linesTwo.get(j).setEnd(pOne);
				}
			}
			
			pOne.setLines(linesTwo);
			pTwo.setLines(linesOne);
		}
		
		// Swap does not work probably because of other reference to object not changed?
		/*pointOne1 = swap(pointTwo1, pointTwo1 = pointOne1);
		pointOne2 = swap(pointTwo2, pointTwo2 = pointOne2);
		pointOne3 = swap(pointTwo3, pointTwo3 = pointOne3);
		pointOne4 = swap(pointTwo4, pointTwo4 = pointOne4);*/

		System.out.println("swapOrientPoints");
	}
	
	protected void updateDrawing(boolean hasChanged) {
		if (hasChanged) {
			switch(state) {
			case 1:
				this.setTranslateX(this.getTranslateX() + this.getPrefWidth()/2);
				this.swapOrientPoints();
				break;
			case 2:
				this.setTranslateX(this.getTranslateX() - this.getPrefWidth()/2);
				this.swapOrientPoints();
				break;
			default:
				Main.popUp(this.toString() + Component.ERROR_ILLEGAL_STATE);
			}
		} else {
			System.out.println(this.toString() + ": no change");
		}
		
		for (Point p : points) {
			p.updateLines();
		}
	}
	

	/*
	 *  Only support some endTypeOne and endTypeTwo combinations only!
	 *  This allows users to start with position 1 or position 2
	 *  
	 * (non-Javadoc)
	 * @see application.Component#updateState()
	 */
	public boolean updateState() {
		int prevState = state;
		if (state > 0) {
			Boolean soleOne = controller.getSolenoid(labelOne.getText()),
					soleTwo = controller.getSolenoid(labelTwo.getText());
			if (endTypeOne == EndType.SOLENOID && endTypeTwo == EndType.SOLENOID) {
				if (soleOne == true && soleTwo == false) {
					state = 1;
				} else if (soleOne == false && soleTwo == true) {
					state = 2;
				} else {
					System.out.println(this.toString() + Component.WARNING_NO_EFFECT);
				}
			} else if (endTypeOne == EndType.SOLENOID && endTypeTwo == EndType.SPRING) {
				if (soleOne == true) {
					state = 1;
				} else if (soleOne == false) {
					state = 2;
				} else {
					System.out.println(this.toString() + Component.WARNING_NO_EFFECT);
				}
			} else if (endTypeOne == EndType.SPRING && endTypeTwo == EndType.SOLENOID) {
				if (soleTwo == true) {
					state = 2;
				} else if (soleTwo == false) {
					state = 1;
				} else {
					System.out.println(this.toString() + Component.WARNING_NO_EFFECT);
				}
			} else if (endTypeOne == EndType.PUSH_BUTTON && endTypeTwo == EndType.SPRING) {
				// TODO: implement
			} else if (endTypeOne == EndType.SPRING && endTypeTwo == EndType.PUSH_BUTTON) {
				// TODO: implement
			}
		} else if (state == 0) {
			System.out.println("State 0");

			if (pointOne2.getLines().size() > 0) {	// pointOne2 is input
				state = 1;
			} else if (pointTwo1.getLines().size() > 0) {	// pointTwo1 is input
				state = 2;
			} else {
				state = -1;
				Main.popUp(this.toString() + Component.ERROR_MISSING_CONNECTIONS);
			}
			if (controller.getSolenoid(labelOne.getText()) == null) {
				controller.setSolenoid(labelOne.getText(), false);
			}
			if (controller.getSolenoid(labelTwo.getText()) == null) {
				controller.setSolenoid(labelTwo.getText(), false);
			}
			return true;
		} else {
			Main.popUp(this.toString() + Component.ERROR_ILLEGAL_STATE);
		}
		this.updateDrawing(prevState != state);
		return prevState != state;
	}
	
	/*
	 * (non-Javadoc)
	 * @see application.Component#propagatePressure()
	 */
	public boolean propagatePressure() {
		boolean hasChanged = false;
		switch(state) {
		case 1:		// Position One
			pointOne1.setIsHigh(pointOne2.getIsHigh());
			hasChanged = pointOne1.updateLines();
			break;
		case 2:		// Position Two
			pointTwo3.setIsHigh(pointTwo1.getIsHigh());
			hasChanged = pointTwo3.updateLines();
			break;
		case 0:
			hasChanged = false;
			break;
		default:
			Main.popUp(this.toString() + Component.ERROR_ILLEGAL_STATE);
		}
		
		return hasChanged;
	}
}

/*
 *			  1  2   1  2
 * 			 _____________
 * 			| ^	 | |	  |
 * 		One	| |	 | |	  | Two
 * 			|_|__V_|______|
 * 			  3  4   3  4
 * 
 *			  0  1   4  5
 * 			 _____________
 * 			| ^	 | |	  |
 * 			| |	 | |	  | 
 * 			|_|__V_|______|
 * 			  2  3   6  7
 */

class FourTwo extends TwoOrientValve {
	public Rectangle rect;
	public Line line;
	
	public Line lineOne1, lineOne2;
	public Line lineTwo1, lineTwo2;
	
	// just to help identify when drawing
	private Point pointOne1, pointOne2, pointOne3, pointOne4;
	private Point pointTwo1, pointTwo2, pointTwo3, pointTwo4;
	
	public Polygon polygonOne1, polygonOne2, polygonTwo1, polygonTwo2;
	
	public FourTwo(EndType o, EndType t) {
		super(o, t, paneWidth, paneHeight*0.5);
		draw(true);
		if (endTypeOne == EndType.PILOT_VALVE || endTypeOne == EndType.SOLENOID
				|| endTypeOne == EndType.PUSH_BUTTON) {
			this.getChildren().add(labelOne);
		}
		if (endTypeTwo == EndType.PILOT_VALVE || endTypeTwo == EndType.SOLENOID
				|| endTypeTwo == EndType.PUSH_BUTTON) {
			this.getChildren().add(labelTwo);
		}
		this.getChildren().addAll(
				line, lineOne1, lineOne2, lineTwo1, lineTwo2, 
				polygonOne1, polygonOne2, polygonTwo1, polygonTwo2, rect, 
				pointOne1, pointOne2, pointOne3, pointOne4, 
				pointTwo1, pointTwo2, pointTwo3, pointTwo4);
		this.getChildren().addAll(endShapeOne);
		this.getChildren().addAll(endShapeTwo);

		addMouseGestures();
		mg.addLabelMenu(this);
	}
	public FourTwo(EndType o, EndType t, String strOne, String strTwo) {
		super(o, t, paneWidth, paneHeight*0.5);
		labelOne = new Label(strOne);
		labelTwo = new Label(strTwo);
		draw(false);
		if (endTypeOne == EndType.PILOT_VALVE || endTypeOne == EndType.SOLENOID
				|| endTypeOne == EndType.PUSH_BUTTON) {
			this.getChildren().add(labelOne);
		}
		if (endTypeTwo == EndType.PILOT_VALVE || endTypeTwo == EndType.SOLENOID
				|| endTypeTwo == EndType.PUSH_BUTTON) {
			this.getChildren().add(labelTwo);
		}
		this.getChildren().addAll(
				line, lineOne1, lineOne2, lineTwo1, lineTwo2, 
				polygonOne1, polygonOne2, polygonTwo1, polygonTwo2, rect, 
				pointOne1, pointOne2, pointOne3, pointOne4, 
				pointTwo1, pointTwo2, pointTwo3, pointTwo4);
		this.getChildren().addAll(endShapeOne);
		this.getChildren().addAll(endShapeTwo);

		addMouseGestures();
		mg.addLabelMenu(this);
	}
	public FourTwo(EndType o, EndType t, boolean display) {
		super(o, t, paneWidth, paneHeight*0.5);
		isDisplay = display;
		if (isDisplay) {
			labelOne = new Label("a1");
			labelTwo = new Label("a2");
		}
		draw(!isDisplay);
		if (endTypeOne == EndType.PILOT_VALVE || endTypeOne == EndType.SOLENOID
				|| endTypeOne == EndType.PUSH_BUTTON) {
			this.getChildren().add(labelOne);
		}
		if (endTypeTwo == EndType.PILOT_VALVE || endTypeTwo == EndType.SOLENOID
				|| endTypeTwo == EndType.PUSH_BUTTON) {
			this.getChildren().add(labelTwo);
		}
		this.getChildren().addAll(
				line, lineOne1, lineOne2, lineTwo1, lineTwo2, 
				polygonOne1, polygonOne2, polygonTwo1, polygonTwo2, rect, 
				pointOne1, pointOne2, pointOne3, pointOne4, 
				pointTwo1, pointTwo2, pointTwo3, pointTwo4);
		this.getChildren().addAll(endShapeOne);
		this.getChildren().addAll(endShapeTwo);
		
		if (isDisplay) {
			mg.addComponentOnClick(this);
		} else {
			addMouseGestures();
			mg.addLabelMenu(this);
		}
	}
	
	/*
	 *  procedure to draw the component
	 */
	private void draw(boolean userInput) {
		double width = this.getPrefWidth();
		double unitWidth = width/12.0;
		double height = this.getPrefHeight();
		double unitHeight = height/12.0;
		
		rect = new Rectangle(unitWidth*2, 0, unitWidth*8, height);
		
		rect.setStroke(COLOR_DEFAULT);
		rect.setFill(COLOR_TRANS);
		
		line = new Line(width/2, 0, width/2, height);
		line.setStroke(COLOR_DEFAULT);
		
    	// Position One
    	lineOne1 = new Line(unitWidth*3, 0, unitWidth*3, height);
    	lineOne1.setStroke(COLOR_DEFAULT);
    	polygonOne1 = drawArrowHead(unitWidth*3, height, unitWidth*3, 0);
    	
    	lineOne2 = new Line(unitWidth*5, 0, unitWidth*5, height);
    	lineOne2.setStroke(COLOR_DEFAULT);
    	polygonOne2 = drawArrowHead(unitWidth*5, 0, unitWidth*5, height);
    	
    	drawEndTypeOne(userInput, width, height, unitWidth, unitHeight);
    	pointOne1 = new Point(unitWidth*3, 0, pointRad, this);
    	pointOne2 = new Point(unitWidth*5, 0, pointRad, this);
    	pointOne3 = new Point(unitWidth*3, height, pointRad, this);
    	pointOne4 = new Point(unitWidth*5, height, pointRad, this);
    	
    	// Position Two
    	lineTwo1 = new Line(unitWidth*7, 0, unitWidth*9, height);
    	lineTwo1.setStroke(COLOR_DEFAULT);
    	polygonTwo1 = drawArrowHead(unitWidth*7, height, unitWidth*9, 0);
    	
    	lineTwo2 = new Line(unitWidth*9, 0, unitWidth*7, height);
    	lineTwo2.setStroke(COLOR_DEFAULT);
    	polygonTwo2 = drawArrowHead(unitWidth*7, 0, unitWidth*9, height);
    	
    	drawEndTypeTwo(userInput, width, height, unitWidth, unitHeight);
    	pointTwo1 = new Point(unitWidth*7, 0, pointRad, this);
    	pointTwo2 = new Point(unitWidth*9, 0, pointRad, this);
    	pointTwo3 = new Point(unitWidth*7, height, pointRad, this);
    	pointTwo4 = new Point(unitWidth*9, height, pointRad, this);
    	/*pointOne1.setFill(Color.GRAY);
    	pointOne2.setFill(Color.BROWN);
    	pointOne3.setFill(Color.PINK);
    	pointOne4.setFill(Color.PURPLE);
    	pointTwo1.setFill(Color.GREEN);
    	pointTwo2.setFill(Color.YELLOW);
    	pointTwo3.setFill(Color.RED);
    	pointTwo4.setFill(Color.BLUE);*/
    	
    	mainBody = rect;
    	// Order of adding is important
    	points.add(pointOne1);
    	points.add(pointOne2);
    	points.add(pointOne3);
    	points.add(pointOne4);
    	points.add(pointTwo1);
    	points.add(pointTwo2);
    	points.add(pointTwo3);
    	points.add(pointTwo4);
    	
    	return;
	}
	
	private void swapOrientPoints() {
		// TODO: swap other orient lines back or just expect them to not be connected?
		for (int i = 0; i < 4; i++) {
			Point pOne = points.get(i), pTwo = points.get(i+4);
			ArrayList<EPLine> linesOne = pOne.getLines(), linesTwo = pTwo.getLines();
			
			// TODO: Fix 	Error: Line does not know point
			//				Error: Point knows line but line don't know point
			// swap the lines, though ideally only one orient has lines()
			for (int j = 0; j < linesOne.size(); j++) {
				if (linesOne.get(j).isStart(pOne)) {
					linesOne.get(j).setStart(pTwo);
				} else if (linesOne.get(j).isEnd(pOne)) {
					linesOne.get(j).setEnd(pTwo);
				}
			}
			
			for (int j = 0; j < linesTwo.size(); j++) {
				if (linesTwo.get(j).isStart(pTwo)) {
					linesTwo.get(j).setStart(pOne);
				} else if (linesTwo.get(j).isEnd(pTwo)) {
					linesTwo.get(j).setEnd(pOne);
				}
			}
			
			pOne.setLines(linesTwo);
			pTwo.setLines(linesOne);
		}
		
		// Swap does not work probably because of other reference to object not changed?
		/*pointOne1 = swap(pointTwo1, pointTwo1 = pointOne1);
		pointOne2 = swap(pointTwo2, pointTwo2 = pointOne2);
		pointOne3 = swap(pointTwo3, pointTwo3 = pointOne3);
		pointOne4 = swap(pointTwo4, pointTwo4 = pointOne4);*/

		System.out.println("swapOrientPoints");
	}
	
	protected void updateDrawing(boolean hasChanged) {
		if (hasChanged) {
			switch(state) {
			case 1:
				this.setTranslateX(this.getTranslateX() + this.getPrefWidth()/2);
				this.swapOrientPoints();
				break;
			case 2:
				this.setTranslateX(this.getTranslateX() - this.getPrefWidth()/2);
				this.swapOrientPoints();
				break;
			default:
				Main.popUp(this.toString() + Component.ERROR_ILLEGAL_STATE);
			}
		} else {
			System.out.println(this.toString() + ": no change");
		}
		
		for (Point p : points) {
			p.updateLines();
		}
	}
	

	/*
	 *  Only support some endTypeOne and endTypeTwo combinations only!
	 *  State 0 should be handled by other methods before calling updateState()
	 *  This allows users to start with position 1 or position 2
	 *  
	 * (non-Javadoc)
	 * @see application.Component#updateState()
	 */
	public boolean updateState() {
		int prevState = state;
		if (state > 0) {
			Boolean soleOne = controller.getSolenoid(labelOne.getText()),
					soleTwo = controller.getSolenoid(labelTwo.getText());
			if (endTypeOne == EndType.SOLENOID && endTypeTwo == EndType.SOLENOID) {
				if (soleOne == true && soleTwo == false) {
					state = 1;
				} else if (soleOne == false && soleTwo == true) {
					state = 2;
				} else {
					System.out.println(this.toString() + Component.WARNING_NO_EFFECT);
				}
			} else if (endTypeOne == EndType.SOLENOID && endTypeTwo == EndType.SPRING) {
				if (soleOne == true) {
					state = 1;
				} else if (soleOne == false) {
					state = 2;
				} else {
					System.out.println(this.toString() + Component.WARNING_NO_EFFECT);
				}
			} else if (endTypeOne == EndType.SPRING && endTypeTwo == EndType.SOLENOID) {
				if (soleTwo == true) {
					state = 2;
				} else if (soleTwo == false) {
					state = 1;
				} else {
					System.out.println(this.toString() + Component.WARNING_NO_EFFECT);
				}
			} else if (endTypeOne == EndType.PUSH_BUTTON && endTypeTwo == EndType.SPRING) {
				// TODO: implement
			} else if (endTypeOne == EndType.SPRING && endTypeTwo == EndType.PUSH_BUTTON) {
				// TODO: implement
			}
		} else if (state == 0) {
			System.out.println("State 0");
			if (pointOne2.getLines().size() > 0 && pointOne3.getLines().size() > 0) {	// pointOne2 is input
				state = 1;
			} else if (pointTwo1.getLines().size() > 0 && pointTwo3.getLines().size() > 0) {	// pointTwo1 is input
				state = 2;
			} else {
				state = -1;
				Main.popUp(this.toString() + Component.ERROR_MISSING_CONNECTIONS);
			}
			if (controller.getSolenoid(labelOne.getText()) == null) {
				controller.setSolenoid(labelOne.getText(), false);
			}
			if (controller.getSolenoid(labelTwo.getText()) == null) {
				controller.setSolenoid(labelTwo.getText(), false);
			}
			return true;
		} else {
			Main.popUp(this.toString() + Component.ERROR_ILLEGAL_STATE);
		}
		this.updateDrawing(prevState != state); 
		return prevState != state;
	}
	
	/*
	 * (non-Javadoc)
	 * @see application.Component#propagatePressure()
	 */
	public boolean propagatePressure() {
		boolean hasChanged = false;
		switch(state) {
		case 1:		// Position One
			pointOne1.setIsHigh(pointOne3.getIsHigh());
			pointOne4.setIsHigh(pointOne2.getIsHigh());
			if (pointOne1.updateLines() || pointOne4.updateLines()) {
				hasChanged = true;
			}
			break;
		case 2:		// Position Two
			pointTwo2.setIsHigh(pointTwo3.getIsHigh());
			pointTwo4.setIsHigh(pointTwo1.getIsHigh());
			if (pointTwo2.updateLines() || pointTwo4.updateLines()) {
				hasChanged = true;
			}
			break;
		case 0:
			hasChanged = false;
			break;
		default:
			Main.popUp(this.toString() + Component.ERROR_ILLEGAL_STATE);
		}
		
		return hasChanged;
	}
}