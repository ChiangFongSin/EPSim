/*
 *  Copyright (c) 2017-2018 Chiang Fong Sin
 *  @author Chiang Fong Sin
 */
package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

/*
 *  Pane chosen instead of Canvas after trying both because Pane exposes children
 */
class LadderComponent extends Pane {
	// Common
	static int paneHeight = 50;
	static int paneWidth = 50;
	
	// Options
	public enum Options {
		BLANK, HORI, NO, NC, PUSH_NO, PUSH_NO_ACTIV, PUSH_NC, PUSH_NC_ACTIV, VERT, VERT_UP, OUT, NA
	}
	public ContextMenu contextMenu = new ContextMenu();
	HashMap<Options, ArrayList<Shape>> hash = new HashMap<Options, ArrayList<Shape>>();
	private Text name = new Text();
	private boolean boolVert = false;
	private boolean boolVertUp = false;
	
	// Error messages
	final static String ERROR_ILLEGAL_OPTION = ": Illegal option";
	
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
	
	public LadderComponent() {
		this(paneWidth, paneHeight);
		constructHelper();
	}
	public LadderComponent(double width, double height) {
		this.setPrefWidth(width);
		this.setPrefHeight(height);
		constructHelper();
	}
	public void constructHelper() {
		this.setStyle("-fx-background-color: #FFFFFF;");
		draw();
	}
	
	protected void addMouseGestures() {
		//TODO: may or may not be necessary
	}
	
	/*
	 * Pre-condition: Pref height and pref width should be set.
	 */
	private void draw() {
		double width = this.getPrefWidth(), height = this.getPrefHeight();
		
		ArrayList<Shape> list = new ArrayList<Shape>();
		hash.put(Options.BLANK, list);
		
		list.add(new Line(0, height/2, width, height/2));
		hash.put(Options.HORI, list);
		
		list = new ArrayList<Shape>();
		list.add(name);
		list.add(new Line(0, height/2, width*0.4, height/2));				// -
		list.add(new Line(width*0.4, height*0.35, width*0.4, height*0.65));	// |
		list.add(new Line(width*0.6, height*0.35, width*0.6, height*0.65));	// |
		list.add(new Line(width*0.6, height/2, width, height/2));			// -
		hash.put(Options.NO, list);
		
		list = new ArrayList<Shape>();
		list.add(new Line(width*0.7, height*0.3, width*0.3, height*0.7));	// /
		hash.put(Options.NC, list);
		
		list = new ArrayList<Shape>();
		list.add(name);
		list.add(new Line(0, height/2, width*0.4, height/2));				// -
		list.add(new Line(width*0.6, height/2, width, height/2));			// -
		list.add(new Line(width*0.4, height/2, width*0.6, height*0.35));	// /
		list.add(new Line(width*0.5, height*0.25, width*0.5, height*0.4));	// |
		list.add(new Polyline(new double[] {
				width*0.4, height*0.3,
				width*0.4, height*0.25,
				width*0.6, height*0.25,
				width*0.6, height*0.3}));
		hash.put(Options.PUSH_NO, list);
		
		list = new ArrayList<Shape>();
		list.add(name);
		list.add(new Line(0, height/2, width*0.4, height/2));				// -
		list.add(new Line(width*0.6, height/2, width, height/2));			// -
		list.add(new Line(width*0.4, height/2, width*0.6, height*0.47));	// /
		list.add(new Line(width*0.5, height*0.35, width*0.5, height*0.48));	// |
		list.add(new Polyline(new double[] {
				width*0.4, height*0.4,
				width*0.4, height*0.35,
				width*0.6, height*0.35,
				width*0.6, height*0.4}));
		hash.put(Options.PUSH_NO_ACTIV, list);
		
		list = new ArrayList<Shape>();
		list.add(name);
		list.add(new Line(0, height/2, width*0.4, height/2));				// -
		list.add(new Line(width*0.6, height/2, width, height/2));			// -
		list.add(new Line(width*0.4, height/2, width*0.6, height*0.53));	// \
		list.add(new Line(width*0.5, height*0.3, width*0.5, height*0.52));	// |
		list.add(new Polyline(new double[] {
				width*0.4, height*0.35,
				width*0.4, height*0.3,
				width*0.6, height*0.3,
				width*0.6, height*0.35}));
		hash.put(Options.PUSH_NC, list);
		
		list = new ArrayList<Shape>();
		list.add(name);
		list.add(new Line(0, height/2, width*0.4, height/2));				// -
		list.add(new Line(width*0.6, height/2, width, height/2));			// -
		list.add(new Line(width*0.4, height/2, width*0.6, height*0.65));	// \
		list.add(new Line(width*0.5, height*0.4, width*0.5, height*0.58));	// |
		list.add(new Polyline(new double[] {
				width*0.4, height*0.45,
				width*0.4, height*0.4,
				width*0.6, height*0.4,
				width*0.6, height*0.45}));
		hash.put(Options.PUSH_NC_ACTIV, list);
		
		list = new ArrayList<Shape>();
		list.add(new Line(0, height*0.5, 0, height));
		hash.put(Options.VERT, list);
		
		list = new ArrayList<Shape>();
		list.add(new Line(0, 0, 0, height*0.5));
		hash.put(Options.VERT_UP, list);
		
		list = new ArrayList<Shape>();
		list.add(name);
		Arc arc;
		list.add(new Line(0, height/2, width*0.2, height/2));		// -
		list.add(new Line(width*0.8, height/2, width, height/2));	// -
		arc = new Arc(width*0.4, height/2, width*0.2, height*0.5, 150.0f, 60.0f);	// (
		arc.setStroke(COLOR_DEFAULT);
		arc.setFill(COLOR_TRANS);
		list.add(arc);
		arc = new Arc(width*0.6, height/2, width*0.2, height*0.5, 330.0f, 60.0f);	// )
		arc.setStroke(COLOR_DEFAULT);
		arc.setFill(COLOR_TRANS);
		list.add(arc);
		hash.put(Options.OUT, list);
	}
	
	/*
	 *  loading operations for VERT and vertUp not supported
	 *  TODO: check for redundant code
	 */
	public void setOption(CheckMenuItem selected) {
		Options op = Options.valueOf(selected.getText());
		
		this.getChildren().removeAll(this.getChildren());
		
		if (selected.isSelected()) {
			double width = this.getPrefWidth(), height = this.getPrefHeight();
			
			switch (op) {
			case BLANK:
				break;
			case HORI:
				this.getChildren().addAll(hash.get(Options.HORI));
				break;
			case NO:
				this.getChildren().addAll(hash.get(Options.NO));
				name.setLayoutX(width*0.4);
				name.setLayoutY(height*0.2);
				break;
			case NC:
				this.getChildren().addAll(hash.get(Options.NO));
				this.getChildren().addAll(hash.get(Options.NC));
				name.setLayoutX(width*0.4);
				name.setLayoutY(height*0.2);
				break;
			case PUSH_NO:
				this.getChildren().addAll(hash.get(Options.PUSH_NO));
				name.setLayoutX(width*0.4);
				name.setLayoutY(height*0.2);
				break;
			case PUSH_NO_ACTIV:
				this.getChildren().addAll(hash.get(Options.PUSH_NO_ACTIV));
				name.setLayoutX(width*0.4);
				name.setLayoutY(height*0.2);
				break;
			case PUSH_NC:
				this.getChildren().addAll(hash.get(Options.PUSH_NC));
				name.setLayoutX(width*0.4);
				name.setLayoutY(height*0.2);
				break;
			case PUSH_NC_ACTIV:
				this.getChildren().addAll(hash.get(Options.PUSH_NC_ACTIV));
				name.setLayoutX(width*0.4);
				name.setLayoutY(height*0.2);
				break;
			case VERT:
				// handled outside switch case
				//this.getChildren().addAll(hash.get(Options.VERT));
				break;
			case OUT:
				this.getChildren().addAll(hash.get(Options.OUT));
				name.setLayoutX(width*0.3);
				name.setLayoutY(height*0.55);
				break;
			case NA:
				break;
			default:
				Main.popUp(this.toString() + LadderComponent.ERROR_ILLEGAL_OPTION);
			}
		}
		updateVert();
		updateVertUp();
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
	
	public void updateCheckMenuItems(CheckMenuItem selected) {
		if (Objects.equals(selected.getText(), DataManager.BLANK)) {
			for (MenuItem it : contextMenu.getItems()) {
				if (it instanceof CheckMenuItem && !Objects.equals(it, selected)) {
					CheckMenuItem temp = (CheckMenuItem) it;
					temp.setSelected(false);
				}
			}
		} else if (!Objects.equals(selected.getText(), DataManager.VERT)) {
			for (MenuItem it : contextMenu.getItems()) {
				if (it instanceof CheckMenuItem && !Objects.equals(it, selected) && !Objects.equals(it.getText(), DataManager.VERT)) {
					CheckMenuItem temp = (CheckMenuItem) it;
					temp.setSelected(false);
				}
			}
		}
	}
	
	/*
	 *  get selected option excluding VERT
	 */
	public CheckMenuItem getSelected() {
		for (MenuItem mi: contextMenu.getItems()) {
			if (mi instanceof CheckMenuItem) {
				CheckMenuItem cmi = (CheckMenuItem) mi;
				if (cmi.isSelected() && !Objects.equals(cmi.getText(), DataManager.VERT)) {
					return cmi;
				}
			}
		}
		return null;
	}
	
	/*
	 * return CheckMenuItem given query string
	 */
	public CheckMenuItem getCheckMenuItem(String query) {
		for (MenuItem mi: contextMenu.getItems()) {
			if (mi instanceof CheckMenuItem) {
				CheckMenuItem cmi = (CheckMenuItem) mi;
				if (Objects.equals(cmi.getText(), query)) {
					return cmi;
				}
			}
		}
		return null;
	}
	
	public Text getName() { return name; }
	public String getNameString() { return name.getText(); }
	public void setName(Text newName) { name = newName; }
	public void setName(String newName) { name = new Text(newName); }
	
	public boolean getVert() { return boolVert; }
	
	/*
	 *  BECAREFUL in usage! 
	 *  To support loading operations, set VERT to true. 
	 */
	public void setVert(boolean bool) {
		boolVert = bool;
		this.getCheckMenuItem(DataManager.VERT).setSelected(boolVert);
		updateVert();
	}
	
	private void updateVert() {
		this.getChildren().removeAll(hash.get(Options.VERT));
		if(boolVert) {
			this.getChildren().addAll(hash.get(Options.VERT));
		}
	}
	
	public boolean getVertUp() { return boolVertUp; }
	public void setVertUp(boolean bool) {
		boolVertUp = bool;
		updateVertUp();
	}
	
	private void updateVertUp() {
		this.getChildren().removeAll(hash.get(Options.VERT_UP));
		if(boolVertUp) {
			this.getChildren().addAll(hash.get(Options.VERT_UP));
		}
	}
}

class Middle extends LadderComponent {
	Middle() {
		super();
		addMenu(this);
		mg.addLadderMenu(this);
		//System.out.println(this.contextMenu.getItems().size());
	}

	private void addMenu(Middle pane) {
		String name = "this component";
		CheckMenuItem blank = new CheckMenuItem(DataManager.BLANK);
		CheckMenuItem line = new CheckMenuItem(DataManager.HORI);
		CheckMenuItem no = new CheckMenuItem(DataManager.NO);
		CheckMenuItem nc = new CheckMenuItem(DataManager.NC);
		CheckMenuItem push_no = new CheckMenuItem(DataManager.PUSH_NO);
		CheckMenuItem push_no_activ = new CheckMenuItem(DataManager.PUSH_NO_ACTIV);
		CheckMenuItem push_nc = new CheckMenuItem(DataManager.PUSH_NC);
		CheckMenuItem push_nc_activ = new CheckMenuItem(DataManager.PUSH_NC_ACTIV);
		CheckMenuItem vert = new CheckMenuItem(DataManager.VERT);
		CheckMenuItem out = new CheckMenuItem(DataManager.OUT);
		
		blank.setOnAction(actionEvent -> {
			pane.updateCheckMenuItems(blank);
			pane.setOption(blank);
		});
		line.setOnAction(actionEvent -> {
			pane.updateCheckMenuItems(line);
			pane.setOption(line);
		});
		no.setOnAction(actionEvent -> {
			if (no.isSelected()) {
				Main.inputForm(pane.getName(), name);
			}
			pane.updateCheckMenuItems(no);
			pane.setOption(no);
		});
		nc.setOnAction(actionEvent -> {
			if (nc.isSelected()) {
				Main.inputForm(pane.getName(), name);
			}
			pane.updateCheckMenuItems(nc);
			pane.setOption(nc);
		});
		push_no.setOnAction(actionEvent -> {
			if (push_no.isSelected()) {
				Main.inputForm(pane.getName(), name);
			}
			pane.updateCheckMenuItems(push_no);
			pane.setOption(push_no);
		});
		push_no_activ.setOnAction(actionEvent -> {
			if (push_no_activ.isSelected()) {
				Main.inputForm(pane.getName(), name);
			}
			pane.updateCheckMenuItems(push_no_activ);
			pane.setOption(push_no_activ);
		});
		push_nc.setOnAction(actionEvent -> {
			if (push_nc.isSelected()) {
				Main.inputForm(pane.getName(), name);
			}
			pane.updateCheckMenuItems(push_nc);
			pane.setOption(push_nc);
		});
		push_nc_activ.setOnAction(actionEvent -> {
			if (push_nc_activ.isSelected()) {
				Main.inputForm(pane.getName(), name);
			}
			pane.updateCheckMenuItems(push_nc_activ);
			pane.setOption(push_nc_activ);
		});
		vert.setOnAction(actionEvent -> {
			if (controller.updateVertUp(this, vert.isSelected())) {
				pane.setVert(vert.isSelected());
			} else {
				pane.setVert(false);
			}
		});
		out.setOnAction(actionEvent -> {
			if (out.isSelected()) {
				Main.inputForm(pane.getName(), name);
			}
			pane.updateCheckMenuItems(out);
			pane.setOption(out);
		});
		
		pane.contextMenu.getItems().addAll(blank, line, no, nc, 
				push_no, push_no_activ, push_nc, push_nc_activ, vert, out);
	}
}

class SecondFromRight extends LadderComponent {
	SecondFromRight() {
		super();
		addMenu(this);
		mg.addLadderMenu(this);
	}
	
	private void addMenu(SecondFromRight pane) {
		String name = "this component";
		CheckMenuItem blank = new CheckMenuItem(DataManager.BLANK);
		CheckMenuItem vert = new CheckMenuItem(DataManager.VERT);
		CheckMenuItem out = new CheckMenuItem(DataManager.OUT);
		
		blank.setOnAction(actionEvent -> {
			pane.updateCheckMenuItems(blank);
			pane.setOption(blank);
		});
		vert.setOnAction(actionEvent -> {
			if (controller.updateVertUp(this, vert.isSelected())) {
				pane.setVert(vert.isSelected());
			} else {
				pane.setVert(false);
			}
		});
		out.setOnAction(actionEvent -> {
			if (out.isSelected()) {
				Main.inputForm(pane.getName(), name);
			}
			pane.updateCheckMenuItems(out);
			pane.setOption(out);
		});
		
		pane.contextMenu.getItems().addAll(blank, vert, out);
	}
}

class LeftEdge extends LadderComponent {
	LeftEdge() {
		super();
		Line line = new Line(this.getPrefWidth()-STROKE_WIDTH, 0, this.getPrefWidth()-STROKE_WIDTH, this.getPrefHeight());
		line.setStroke(COLOR_DEFAULT);
		line.setStrokeWidth(STROKE_WIDTH);
		this.getChildren().add(line);
	}
}

class RightEdge extends LadderComponent {
	RightEdge() {
		super();
		Line line = new Line(0, 0, 0, this.getPrefHeight());
		line.setStroke(COLOR_DEFAULT);
		line.setStrokeWidth(STROKE_WIDTH);
		this.getChildren().add(line);
	}
}