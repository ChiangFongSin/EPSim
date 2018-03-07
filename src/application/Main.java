/*
 *  Copyright (c) 2017-2018 Chiang Fong Sin
 *  @author Chiang Fong Sin
 */
package application;
	
import java.util.HashMap;
import application.Component.EndType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class Main extends Application {
	final static boolean DEBUG = true;
	final static int Y_CORRECTION = 25;	// menuBar.getHeight(), but some places getHeight() give 0
	//final static ScrollBar sc = new ScrollBar();
	
	// DOM
	private static double appWidth = 1000, appHeight = 400;
	public static Pane root;
	public static MenuBar menuBar;
	public static GridPane grid;
	public static Pane work;
	public static Pane groupLeft;
	public static ScrollBar sb;
	private static double listHeight = 0.0;
	
	private static Stage ladderStage = new Stage();
	
	public static VBox ladder;
	
	// Key
	public static HashMap<String, Boolean> currentlyActiveKeys = new HashMap<>();
    
	// Controller
	private Controller controller = Controller.getInstance();
    private boolean runWithStep = false;

	@Override
	public void start(Stage primaryStage) {
		
		// Stage Attributes
		primaryStage.setTitle("Electro-Pneumatic Simulator");
		
		// Initialisation
		// Panes for the Scene
		root = new Pane();
		menuBar = createMenuBar();
        grid = new GridPane();
        sb = new ScrollBar();
        
        createLadderStage();
        
        // Listener for resizing of application
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
        	appWidth = (double) newValue;
        	updateScene();
        	if (DEBUG) {
        		System.out.println("appWidth changed to: " + primaryStage.getWidth());
        	}
        });
        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
        	appHeight = (double) newValue;
        	updateScene();
        	if (DEBUG) {
        		System.out.println("appHeight changed to: " + primaryStage.getHeight());
        	}
        });
        /*ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->
        System.out.println("Height: " + primaryStage.getHeight() + " Width: " + primaryStage.getWidth());

        primaryStage.widthProperty().addListener(stageSizeListener);
        primaryStage.heightProperty().addListener(stageSizeListener);
        */
                 
        root.setPrefSize(appWidth, appHeight);
        //grid.setPrefHeight(2000);
        //grid.setPrefWidth(1000);
        
        grid.setPadding(new Insets(5, 5, 5, 5));
        //grid.setMinSize(200, 300);
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setStyle("-fx-background-color: #ffffb2;");
        //grid.setGridLinesVisible(true);
        
        
        menuBar.setPrefWidth(appWidth);
        menuBar.setLayoutX(0);
        menuBar.setLayoutY(0);
        groupLeft = createList();
        groupLeft.setLayoutX(0);
        groupLeft.setLayoutY(Y_CORRECTION);
        
        positionWorkPane();
        
        root.getChildren().addAll(menuBar, groupLeft, work);
        
        Scene scene = new Scene(root, appWidth, appHeight);
        
        scene.setOnKeyPressed(event -> {
            String codeString = event.getCode().toString();
            if (!currentlyActiveKeys.containsKey(codeString)) {
                currentlyActiveKeys.put(codeString, true);
                //System.out.println(codeString + " pressed.");
            }
        });
        
        scene.setOnKeyReleased(event -> {
        	String codeString = event.getCode().toString();
        	currentlyActiveKeys.remove(codeString);
        	//System.out.println(codeString + " released.");
        });
        
        //scene.addEventFilter(arg0, arg1);
        primaryStage.setScene(scene);
        primaryStage.setOnHidden(e -> Platform.exit());
        primaryStage.show();
		
        // Original code when creating file. Since not using CSS, commented out. KIV
        /*try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}*/
	}

	private void positionWorkPane() {
		MouseGestures mg = new MouseGestures();
        
		work = controller.getCommonPane();
        work.setStyle("-fx-background-color: #FFFFFF;");
        mg.makeRightCancel(work);
		
        work.setPrefSize(appWidth-groupLeft.getPrefWidth(), appHeight-Y_CORRECTION);
        work.setLayoutX(groupLeft.getPrefWidth());
        work.setLayoutY(Y_CORRECTION);
	}
	
	private void updateScene() {
		menuBar.setPrefWidth(appWidth);
		work.setPrefSize(appWidth-groupLeft.getPrefWidth(), appHeight-Y_CORRECTION);
		sb.setPrefHeight(appHeight-Y_CORRECTION-40);	// TODO: Magic number
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public static void popUp(String str) {
		popUp("Important",str);
	}
	
	public static void popUp(String title, String str) {
		VBox root = new VBox();
		root.setPadding(new Insets(5, 5, 5, 5));
		Text t = new Text(str);
		root.getChildren().add(t);
		Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();
	}
	
	public static void inputForm(Label label, String name) {
		Stage stage = new Stage();
        VBox root = new VBox();
        root.setPadding(new Insets(5, 5, 5, 5));
		Label prompt = new Label("Enter name/value for " + name);
		TextField textField = new TextField();
		textField.setOnKeyPressed(keyEvent -> {
			if (keyEvent.getCode() == KeyCode.ENTER) {
				stage.close();
				label.setText(textField.getText());
			}
		});
		root.getChildren().addAll(prompt, textField);
		stage.setTitle("Pending input");
        stage.setScene(new Scene(root));
        stage.showAndWait();
	}
	
	public static void inputForm(Text text, String name) {
		Stage stage = new Stage();
        VBox root = new VBox();
        root.setPadding(new Insets(5, 5, 5, 5));
		Label prompt = new Label("Enter name/value for " + name);
		TextField textField = new TextField(text.getText());
		textField.setOnKeyPressed(keyEvent -> {
			if (keyEvent.getCode() == KeyCode.ENTER) {
				stage.close();
				text.setText(textField.getText());
			}
		});
		root.getChildren().addAll(prompt, textField);
		stage.setTitle("Pending input");
        stage.setScene(new Scene(root));
        stage.showAndWait();
	}
	
	private void createLadderStage() {
		Stage stage = ladderStage;
        Pane root = new Pane();
        ladder = controller.getLadderPane();
        
        MenuBar mb = new MenuBar();
		
        // fileMenu
        Menu fileMenu = new Menu("File");
		MenuItem add = new MenuItem("Add row");
		add.setOnAction(actionEvent -> {
			Controller.getInstance().addLadderRow(new LadderRow());
		});
		MenuItem remove = new MenuItem("Remove row");
		remove.setOnAction(actionEvent -> {
			Text text = new Text("");
			inputForm(text, "index (starts from 0)");
			Controller.getInstance().removeLadderRow(Integer.parseInt(text.getText()));
		});
		fileMenu.getItems().addAll(add, remove);
		
		// runMenu
		Menu runMenu = createRunMenu();
		mb.getMenus().addAll(fileMenu, runMenu);
        
        double maxWidth = LadderRow.colSize*LadderComponent.paneWidth;
        double prefHeight = LadderComponent.paneHeight*12;	// note magic number 12
        
        ScrollBar sb = new ScrollBar();
        ladder.setPrefWidth(maxWidth+sb.getWidth());
        sb.setMin(0);
        sb.setOrientation(Orientation.VERTICAL);
        sb.setPrefHeight(prefHeight);
        sb.setMax(prefHeight);	// magic value
        
        sb.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
            	ladder.setTranslateY(-new_val.doubleValue());
            	mb.toFront();
            }
        });

        // let mouse scroll on grid to scroll too
        ladder.setOnScroll(scrollEvent -> {
        	double newScrollValue = sb.getValue() - scrollEvent.getDeltaY();

        	if (newScrollValue < sb.getMin()) {
        		newScrollValue = sb.getMin();
        	} else if (newScrollValue > sb.getMax()) {
        		newScrollValue = sb.getMax();
        	}

        	sb.setValue(newScrollValue);
        	ladder.setTranslateY(-newScrollValue);
        	mb.toFront();
        });
        
        root.setPrefSize(maxWidth+sb.getWidth()-5, prefHeight+Y_CORRECTION);
        mb.setPrefWidth(maxWidth+sb.getWidth()-5);
        mb.setLayoutX(0);
        mb.setLayoutY(0);
        //ladder.setPrefSize(maxWidth, prefHeight);
        ladder.setLayoutX(0);
        ladder.setLayoutY(Y_CORRECTION);
        sb.setLayoutX(maxWidth);
        sb.setLayoutY(Y_CORRECTION);
        
        
		root.getChildren().addAll(mb, ladder, sb);
		stage.setTitle("Ladder Diagram");
        stage.setScene(new Scene(root));
        //stage.showAndWait();
	}
	
	/* Code dump for opening window
	btnOpenNewWindow.setOnAction(new EventHandler<ActionEvent>() {
	    public void handle(ActionEvent event) {
	        Parent root;
	        try {
	            root = FXMLLoader.load(getClass().getClassLoader().getResource("path/to/other/view.fxml"), resources);
	            Stage stage = new Stage();
	            stage.setTitle("My New Stage Title");
	            stage.setScene(new Scene(root, 450, 450));
	            stage.show();
	            // Hide this current window (if this is what you want)
	            ((Node)(event.getSource())).getScene().getWindow().hide();
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}*/
	
	/*
	 * Creates and returns the menuBar
	 */
	private MenuBar createMenuBar() {
		Menu fileMenu = new Menu("File");
		Menu optionsMenu = new Menu("Options");
		Menu helpMenu = new Menu("Help");
		Menu ladderMenu = new Menu("Ladder");
		Menu runMenu = new Menu("Run");
		
		// fileMenu
		MenuItem newMenuItem = new MenuItem("New");
		newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		newMenuItem.setOnAction(actionEvent -> {
			// TODO: Perhaps download a memory analyser to see if memory is really freed.
			controller.reset();
			root.getChildren().remove(work);
			work = controller.getCommonPane();
			ladder = controller.getLadderPane();
			positionWorkPane();
			root.getChildren().add(work);
			ladderStage.hide();
			ladderStage = new Stage();
			createLadderStage();
		});
		MenuItem loadMenuItem = new MenuItem("Load");
		loadMenuItem.setOnAction(actionEvent -> {
	    	if (DataManager.getInstance().loadData()) {
	    		// load successful
	    	} else {
	    		// load unsuccessful but warnings should be given in DataManager
	    	}
	    });
	    MenuItem saveMenuItem = new MenuItem("Save");
		saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
	    saveMenuItem.setOnAction(actionEvent -> {
	    	if (DataManager.getInstance().saveData()) {
	    		// save successful
	    	} else {
	    		// save unsuccessful but warnings should be given in DataManager
	    	}
	    });
	    MenuItem exitMenuItem = new MenuItem("Exit");
	    exitMenuItem.setOnAction(actionEvent -> Platform.exit());
	    
	    fileMenu.getItems().addAll(newMenuItem, loadMenuItem, saveMenuItem,
	            new SeparatorMenuItem(), exitMenuItem);
	    
	    // optionsMenu
	    /*CustomMenuItem customMenuItem = new CustomMenuItem(new Slider());
        customMenuItem.setHideOnClick(false);
        optionsMenu.getItems().add(customMenuItem);
        */
        // helpMenu
        MenuItem helpMenuItem = new MenuItem("About");
        helpMenuItem.setOnAction(actionEvent -> {
        	popUp("About", "<-under construction->");	
        });
        helpMenu.getItems().add(helpMenuItem);
        
        // ladderMenu
        MenuItem ladderMenuItem = new MenuItem("Ladder");
        ladderMenuItem.setOnAction(actionEvent -> {
        	ladderStage.show();
        });
        ladderMenu.getItems().add(ladderMenuItem);
        
        //runMenu
        runMenu = createRunMenu();
        
	    MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu, helpMenu, ladderMenu, runMenu);
		
		return menuBar;
	}
	
	private Menu createRunMenu() {
		Menu runMenu = new Menu("Run");
		MenuItem runMenuItem = new MenuItem("Run");
        runMenuItem.setOnAction(actionEvent -> {
        	// TODO: check for errors with user input (exists but incomplete)
        	System.out.println("Running...");
        	
        	controller.resetSequence();
        	
        	int iter = 0;
        	boolean hasChanged = true;
        	while (hasChanged) {
        		hasChanged = false;
        		hasChanged = controller.runOnePass();
        		
        		iter++;
        		
        		if (iter > 10000) {
        			Main.popUp("Unsolvable!");
        			break;
        		}
        	}
        	
        	controller.showSequence();
        	
        	System.out.println("End of run");
        });
        MenuItem runWithStepsMenuItem = new MenuItem("Run with steps");
        runWithStepsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.CONTROL_DOWN));
        runWithStepsMenuItem.setOnAction(actionEvent -> {
        	// TODO: Prevent race condition or how to run until no change
        	System.out.println("Run with step");
        	controller.resetSequence();
        	runWithStep = true;
        	// Should step menuItem simply combine here?
        });
        MenuItem stepMenuItem = new MenuItem("Step");
        stepMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.CONTROL_DOWN));
        stepMenuItem.setOnAction(actionEvent -> {
        	System.out.println("Stepping...");
        	if (runWithStep) {
        		boolean hasChanged = controller.runOnePass();
        		if (!hasChanged) {
        			runWithStep = false;
            		Main.popUp("Completed. Stopping run with step...");
        		}
        	} else {
        		Main.popUp("Run with steps not activated");
        	}
        });
        MenuItem stopSteppingMenuItem = new MenuItem("Stop stepping");
        stopSteppingMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.CONTROL_DOWN));
        stopSteppingMenuItem.setOnAction(actionEvent -> {
        	System.out.println("Stop stepping...");
        	runWithStep = false;
        });
        
        MenuItem showSequenceMenuItem = new MenuItem("Show sequence");
        showSequenceMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT4, KeyCombination.CONTROL_DOWN));
        showSequenceMenuItem.setOnAction(actionEvent -> {
        	System.out.println("Show sequence.");
        	controller.showSequence();
        });
        
        runMenu.getItems().addAll(runMenuItem, runWithStepsMenuItem, stepMenuItem, 
        		stopSteppingMenuItem, showSequenceMenuItem);
		
        return runMenu;
	}
	
	private Pane createList() {
		Pane groupLeft = new Pane();
		
		// Displays not added to Controller.comList
        boolean isDisplay = true;
        listHeight = 0.0;
        int rowIndex = 0;
        DoubleProperty maxWidth = new SimpleDoubleProperty();
        maxWidth.set(0.0);
        
        addListComponent(rowIndex, maxWidth, new Compressor(isDisplay));
        rowIndex++;
        
        addListComponent(rowIndex, maxWidth, new Exhaust(isDisplay));
        rowIndex++;
        
        addListComponent(rowIndex, maxWidth, new Cylinder(false, false, isDisplay));
        rowIndex++;
        addListComponent(rowIndex, maxWidth, new Cylinder(false, true, isDisplay));
        rowIndex++;
        addListComponent(rowIndex, maxWidth, new Cylinder(true, true, isDisplay));
        rowIndex++;
        
        addListComponent(rowIndex, maxWidth, new ThreeTwo(EndType.SOLENOID, EndType.SOLENOID, isDisplay));
        rowIndex++;
        addListComponent(rowIndex, maxWidth, new ThreeTwo(EndType.SOLENOID, EndType.SPRING, isDisplay));
        rowIndex++;
        addListComponent(rowIndex, maxWidth, new ThreeTwo(EndType.SPRING, EndType.SOLENOID, isDisplay));
        rowIndex++;
        /*addListComponent(rowIndex, maxWidth, new ThreeTwo(EndType.PUSH_BUTTON, EndType.SPRING, isDisplay));
        rowIndex++;
        addListComponent(rowIndex, maxWidth, new ThreeTwo(EndType.SPRING, EndType.PUSH_BUTTON, isDisplay));
        rowIndex++;*/
        
        addListComponent(rowIndex, maxWidth, new FourTwo(EndType.SOLENOID, EndType.SOLENOID, isDisplay));
        rowIndex++;
        addListComponent(rowIndex, maxWidth, new FourTwo(EndType.SOLENOID, EndType.SPRING, isDisplay));
        rowIndex++;
        addListComponent(rowIndex, maxWidth, new FourTwo(EndType.SPRING, EndType.SOLENOID, isDisplay));
        rowIndex++;
        /*addListComponent(rowIndex, maxWidth, new FourTwo(EndType.PUSH_BUTTON, EndType.SPRING, isDisplay));
        rowIndex++;
        addListComponent(rowIndex, maxWidth, new FourTwo(EndType.SPRING, EndType.PUSH_BUTTON, isDisplay));
        rowIndex++;*/

        //grid.setPrefHeight(listHeight);
        
        sb.setTranslateX(maxWidth.getValue()+10);
        groupLeft.setPrefWidth(maxWidth.getValue()+sb.getWidth()+5);	// I think because of padding
        sb.setMin(0);
        sb.setOrientation(Orientation.VERTICAL);
        sb.setPrefHeight(appHeight-Y_CORRECTION-40);	// TODO: Magic number, not relevant
        sb.setMax(listHeight);
        
        groupLeft.getChildren().addAll(grid, sb);
        
        sb.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                    grid.setTranslateY(-new_val.doubleValue());
                    menuBar.toFront();
            }
        });

        // let mouse scroll on grid to scroll too
        grid.setOnScroll(scrollEvent -> {
        	double newScrollValue = sb.getValue() - scrollEvent.getDeltaY();

        	if (newScrollValue < sb.getMin()) {
        		newScrollValue = sb.getMin();
        	} else if (newScrollValue > sb.getMax()) {
        		newScrollValue = sb.getMax();
        	}

        	sb.setValue(newScrollValue);
        	grid.setTranslateY(-newScrollValue);
        	menuBar.toFront();
        });

        return groupLeft;
	}

	private void addListComponent(int rowIndex, DoubleProperty maxWidth, Component cpnt) {
		grid.add(cpnt, 0, rowIndex);
        grid.getRowConstraints().add(new RowConstraints(cpnt.getPrefHeight()));
        listHeight += cpnt.getPrefHeight();
        if (cpnt.getPrefWidth() > maxWidth.getValue()) {
        	maxWidth.set(cpnt.getPrefWidth());
        }
	}
	
	public static double getAppWidth() {return appWidth;}
	public static double getAppHeight() {return appHeight;}
}
