/*
 *  Copyright (c) 2017-2018 Chiang Fong Sin
 *  @author Chiang Fong Sin
 */
package application;

import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

/*
 *  Mouse Event Handler
 */
public class MouseGestures {
	
	// For creating line
	public static Point source, target;
	
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    
    /*
	 *  Should apply to the main body like triangle or rectangle, the rest should follow
	 *  Note to self: mouseEventHandler is global while setOnMouse___ is local to shape
	 */
	public void makeMousable(Component cpnt) {
		
		// Primary key pressed
		cpnt.mainBody.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				orgSceneX = e.getSceneX();
				orgSceneY = e.getSceneY();
				orgTranslateX = cpnt.getTranslateX();
				orgTranslateY = cpnt.getTranslateY();
				
				if (e.isPrimaryButtonDown()) {
					// NOTE: HashMap may map value as null also, but not intention here
					if (Main.currentlyActiveKeys.get("CONTROL") != null) {	// TODO: CONTROL magic string
						if (Main.DEBUG) {
							System.out.println("Control + Left Mouse");
						}
						
						// sometimes deleting component leaves hanging lines
						// UPDATE: should have deleted by changing condition to check size > 0 in points -> removingParent
						for (int i = 0; i < cpnt.getPoints().size(); i++) {
								System.out.println("Checking point " + i);
								cpnt.getPoints().get(i).removingParent();
						}
						Controller.getInstance().removeComponent(cpnt);
					}
				} else if (e.isSecondaryButtonDown()) {
					// Just to test
					//System.out.println(cpnt.updateLogic());
				}

				if (Main.DEBUG) {
					System.out.println("Mouse pressed. orgSceneX: " + orgSceneX + ", orgSceneY: " + orgSceneY + ", orgTranslateX:" + orgTranslateX +  ", orgTranslateY: " + orgTranslateY);
		   		}
				e.consume();
			}
		});
		
		// Mouse dragged
		cpnt.mainBody.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
            	//gc.clearRect(e.getX() - 2, e.getY() - 2, 5, 5);
            	double offsetX = e.getSceneX() - orgSceneX;
                double offsetY = e.getSceneY() - orgSceneY;

                double newTranslateX = orgTranslateX + offsetX;
                double newTranslateY = orgTranslateY + offsetY;

        		if (Main.DEBUG) {
        			//System.out.println("Primary key dragged. " + e.getSceneX() + ", " + e.getSceneY() + " (" + orgTranslateX + ", " + orgTranslateY + ") -> (" + newTranslateX + ", " + newTranslateY + ")");
        		}
        		
        		cpnt.setTranslateX(newTranslateX);
        		cpnt.setTranslateY(newTranslateY);
        		
        		ArrayList<Point> points = cpnt.getPoints();
        		for (int i = 0; i < points.size(); i++) {
        			points.get(i).updateLines();
        		}
        		e.consume();
           	}
        });
		
		cpnt.mainBody.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (Main.DEBUG) {
					//System.out.println("Mouse exited");
				}
			}
		});
		
		cpnt.mainBody.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (Main.DEBUG) {
					//System.out.println("Mouse entered");
				}
			}
			
		});
		
		// Primary key released
		/*cpnt.mainBody.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				orgSceneX = orgTranslateX;
				orgSceneY = orgTranslateY;
        		if (Main.DEBUG) {
        			System.out.println("Primary key released. orgSceneX: " + orgSceneX + " orgSceneY: " + orgSceneY);
        		}
			}
		});*/
  
        // Mouse double clicked
        /*node.addEventHandler(MouseEvent.MOUSE_CLICKED, 
        		new EventHandler<MouseEvent>() {
        			@Override
        			public void handle(MouseEvent e) {            
        				if (e.getClickCount() >1) {
        					System.out.println("Mouse clicked");
        					//reset(node, Color.BLUE);
             			}
        			}
        		}
        );*/
		
		/*// Mouse exited
        node.addEventHandler(MouseEvent.MOUSE_EXITED, 
        		new EventHandler<MouseEvent>() {
        	@Override
        	public void handle(MouseEvent e) {
        		if (e.isPrimaryButtonDown()) {
        			System.out.println("Mouse exited and primary key pressed. col: " + e.getX() + " row: " + e.getY());
             	}
        	}
        });*/
	}
	
	public void makeClickable(Point point) {
		point.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.getSource() instanceof Point) {
					Point p = (Point) e.getSource();
					
					if (e.isPrimaryButtonDown()) {
						if (source == null) {
							source = p;
						} else if (source != p) {	// pressed is fired off in both
							target = p;
							// TODO: simplify if else block & update description
							if (source == target) {
								System.out.println("drawLine failed: source and target same Point!");
							} else if (source.parent == target.parent) {
								System.out.println("drawLine failed: source and target same Component!");
							} else if (source == null) {
								System.out.println("drawLine failed: source is null");
							} else if (target == null) {
								System.out.println("drawLine failed: target is null");
							} else {
								/*double sourceX = source.parent.getTranslateX() + source.getCenterX();
								double sourceY = source.parent.getTranslateY() + source.getCenterY();
								double targetX = target.parent.getTranslateX() + target.getCenterX();
								double targetY = target.parent.getTranslateY() + target.getCenterY();*/

								EPLine line = new EPLine(source, target);

								Controller.getInstance().addLine(line);

								source.getLines().add(line);
								target.getLines().add(line);
								
								//System.out.println("Line from: " + sourceX + ", " + sourceY);
								//System.out.println("Line to: " + targetX + ", " + targetY);
								source.printState();
								target.printState();
								
								source = null;
								target = null;
							}
						}
					} else if (e.isSecondaryButtonDown()) {
						source = null;	// cancel line creation if right click on Point
					}
				}
				
				if (Main.DEBUG) {
					System.out.println("pressed point");
				}
				e.consume();
			}
		});
	}
	
	public void makeClickable(EPLine line) {
		line.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.getSource() instanceof Line) {
					EPLine l = (EPLine) e.getSource();
					
					if (e.isSecondaryButtonDown()) {
						Controller.getInstance().removeLine(l);
					}
				}
				
				if (Main.DEBUG) {
					System.out.println("pressed line");
				}
				e.consume();
			}
		});
	}
	
	public void makeRightCancel(Pane pane) {
		pane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.isSecondaryButtonDown()) {
					source = null;
				}
				e.consume();
			}
		});
	}
	
	public void addComponentOnClick(Component cpnt) {
		cpnt.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.getClickCount() > 1) {	// more than one click
					Component.EndType o, t;
					
					if (e.getSource() instanceof Component) {
						Component c = (Component) e.getSource();
						Component toAdd = null;
						if (c instanceof Compressor) {
							toAdd = new Compressor();
						} else if (c instanceof Exhaust) {
							toAdd = new Exhaust();
						} else if (c instanceof Cylinder) {
							boolean so = ((Cylinder) c).getSwitchOne();
							boolean st = ((Cylinder) c).getSwitchTwo();
							toAdd = new Cylinder(so, st);
						} else if (c instanceof ThreeTwo) {
							o = ((ThreeTwo) c).getEndTypeOne();
							t = ((ThreeTwo) c).getEndTypeTwo();
							toAdd = new ThreeTwo(o, t);
						} else if (c instanceof FourTwo) {
							o = ((FourTwo) c).getEndTypeOne();
							t = ((FourTwo) c).getEndTypeTwo();
							toAdd = new FourTwo(o, t);
						} else {
							System.out.println("MouseGestures addComponentOnClick method do not contain this component");
						}
						
						if (toAdd != null) {
							Controller.getInstance().addComponent(toAdd);
						}
					}
				}
				e.consume();
			}
		});
	}
	
	public void addLabelMenu(Valve valve) {
		valve.hiddenEndShapeOne.setOnContextMenuRequested(contextMenuEvent -> {
			ContextMenu contextMenu = new ContextMenu();
			MenuItem edit = new MenuItem("Edit");
			
			edit.setOnAction(actionEvent -> {
				Main.inputForm(valve.labelOne, "New label");
			});
			
			contextMenu.getItems().addAll(edit);
			contextMenu.show(valve.hiddenEndShapeOne, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
		});
		
		valve.hiddenEndShapeTwo.setOnContextMenuRequested(contextMenuEvent -> {
			ContextMenu contextMenu = new ContextMenu();
			MenuItem edit = new MenuItem("Edit");
			
			edit.setOnAction(actionEvent -> {
				Main.inputForm(valve.labelTwo, "New label");
			});
			
			contextMenu.getItems().addAll(edit);
			contextMenu.show(valve.hiddenEndShapeTwo, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
		});
	}
	
	public void addLabelMenu(Cylinder cylinder) {
		if (cylinder.getSwitchOne()) {
			cylinder.hiddenShapeOne.setOnContextMenuRequested(contextMenuEvent -> {
				ContextMenu contextMenu = new ContextMenu();
				MenuItem edit = new MenuItem("Edit");

				edit.setOnAction(actionEvent -> {
					Main.inputForm(cylinder.labelOne, "New label");
				});

				contextMenu.getItems().addAll(edit);
				contextMenu.show(cylinder.hiddenShapeOne, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
			});
		}

		if (cylinder.getSwitchTwo()) {
			cylinder.hiddenShapeTwo.setOnContextMenuRequested(contextMenuEvent -> {
				ContextMenu contextMenu = new ContextMenu();
				MenuItem edit = new MenuItem("Edit");

				edit.setOnAction(actionEvent -> {
					Main.inputForm(cylinder.labelTwo, "New label");
				});

				contextMenu.getItems().addAll(edit);
				contextMenu.show(cylinder.hiddenShapeTwo, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
			});
		}
	}
	
	public void addLadderMenu(Middle pane) {
		pane.setOnContextMenuRequested(contextMenuEvent -> {
			pane.contextMenu.show(pane, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
		});
	}
	
	public void addLadderMenu(SecondFromRight pane) {
		pane.setOnContextMenuRequested(contextMenuEvent -> {
			pane.contextMenu.show(pane, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
		});
	}
}