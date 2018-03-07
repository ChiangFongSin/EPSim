/*
 *  Copyright (c) 2017-2018 Chiang Fong Sin
 *  @author Chiang Fong Sin
 */
package application;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/*
 *  Pane chosen instead of Canvas after trying both because Pane exposes children
 */
class LadderRow extends HBox {
	// Common
	static int colSize = 12;
	
	// Error messages
	final static String ERROR_ILLEGAL_LC_INDEX = ": Illegal LC index";
	
	// Magic numbers
	final static double STROKE_WIDTH = 1.5;
	
	final static Color COLOR_DEFAULT = Color.BLACK;
	final static Color COLOR_HIGH = Color.BLUE;
	final static Color COLOR_LOW = Color.DEEPSKYBLUE;
	final static Color COLOR_ACTIV = Color.LIME;
	final static Color COLOR_DEACTIV = Color.DARKGREEN;
	final static Color COLOR_TRANS = Color.TRANSPARENT;
	
	// Logical
	
	// Mouse
	public static MouseGestures mg = new MouseGestures();
	
	// Controller
	protected static Controller controller = Controller.getInstance();
	
	public LadderRow() {
		this(LadderComponent.paneWidth*colSize, LadderComponent.paneHeight);
	}
	private LadderRow(double width, double height) {
		this.setPrefWidth(width);
		this.setPrefHeight(height);
		constructHelper();
	}
	public void constructHelper() {
		LadderComponent toAdd;
		for (int i = 0; i < colSize; i++) {
			if (i == 0) {
				toAdd = new LeftEdge();
			} else if (i == LadderRow.colSize-1) {
				toAdd = new RightEdge();
			} else if (i == LadderRow.colSize-2) {
				toAdd = new SecondFromRight();
			} else {
				toAdd = new Middle();
			}
			this.getChildren().add(i, toAdd);
		}
	}
	
	public void delete() {
		for(Node node : this.getChildren()) {
			if (node instanceof Middle) {
				Middle middle = (Middle) node;
				controller.updateVert(middle, false);
			} else if (node instanceof SecondFromRight) {
				SecondFromRight sfr = (SecondFromRight) node;
				controller.updateVert(sfr, false);
			}
		}
	}
	
	public LadderComponent getLadderComponent(int index) {
		Node lc = this.getChildren().get(index);
		if (lc instanceof LadderComponent) {
			return (LadderComponent) this.getChildren().get(index);
		} else {
			Main.popUp(this.toString() + ERROR_ILLEGAL_LC_INDEX);
			return null;
		}
	}
}
