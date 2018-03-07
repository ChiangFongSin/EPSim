/*
 *  Copyright (c) 2017-2018 Chiang Fong Sin
 *  @author Chiang Fong Sin
 */
package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import application.Component.EndType;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/*
 * Singleton pattern to prevent multiple access to mutable data stored on local machine
 * Manual edits of saved files may corrupt saved file
 */
class DataManager {
	// Folder name for data files
	final String DATA_DIR = "Saved Circuits";
	final String FILE_EXTENSION_TEXT = ".txt";
	final String FILE_EXTENSION_CSV = ".csv";
	
	// CSV (Comma-Separated Values)
	final char QUOTES = '\"';
	final char CSV_DELIMITER = ',';
	
	// Magic strings
	final String SPECIAL = "@#@";
	final String NEWLINE = "\r\n";	// windows need both char
	final String COMPONENT = "Component";
	final String LINE = "Line";
	final String END_OF_FILE = SPECIAL + "END";
	final String COMPRESSOR = "Compressor";
	final String EXHAUST = "Exhaust";
	final String CYLINDER = "Cylinder";
	final String THREETWO = "ThreeTwo";
	final String FOURTWO = "FourTwo";
	final String NA = "NA";

	final String LADDER = "Ladder";
	final String LEFTEDGE = "LeftEdge";
	final String RIGHTEDGE = "RightEdge";
	final String MIDDLE = "Middle";
	final String SECONDFROMRIGHT = "SecondFromRight";
	final static String BLANK = "BLANK";
	final static String HORI = "HORI";
	final static String NO = "NO";
	final static String NC = "NC";
	final static String PUSH_NO = "PUSH_NO";
	final static String PUSH_NO_ACTIV = "PUSH_NO_ACTIV";
	final static String PUSH_NC = "PUSH_NC";
	final static String PUSH_NC_ACTIV = "PUSH_NC_ACTIV";
	final static String VERT = "VERT";
	final static String OUT = "OUT";
	final String TRUE = "true";
	final String FALSE = "false";
	
	final int ladderNum = 5;	// number of fields stored for each ladderComponent
	
	private static DataManager dataManager;
	private static Controller controller = Controller.getInstance();
	
	private DataManager() {
		
	}
	
	public static DataManager getInstance() {
		if (null == dataManager) {
            dataManager = new DataManager();
        }
        return dataManager;
	}
	
	private void checkFileDir() {
		boolean dir = new File(DATA_DIR).mkdirs();
		if (!dir) {
			System.out.println("Directory already exists.");
		}
	}
	
	/*
	 * Return if operation is successful. Load into data.
	 * Does not read if file does not exists
	 * Skips line if line contains error
	 * Calls loadComponent to decipher the dataLine
	 */
	public boolean loadData() {
		checkFileDir();
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load File");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Comma-Separated Values", "*.csv"),
				new ExtensionFilter("All Files", "*.*"));
		fileChooser.setInitialDirectory(new File(DATA_DIR));
		Stage mainStage = new Stage();
		//Scene scene = new Scene(fileChooser);
		File selectedFile = fileChooser.showOpenDialog(mainStage);
		if (null == selectedFile) {
			System.out.println("File not selected");
			return false;
		}
		/*if (selectedFile != null) {
			 mainStage.setScene(scene);
		    mainStage.display(selectedFile);
		}*/

		ArrayList<String> data;
		BufferedReader reader;
		String strLine;
		// TODO: make tempComList redundant by making load removes all existing cpnt and lines
		// Right not datafile has to have comList before lineList
		ArrayList<Component> tempComList = new ArrayList<Component>();	// for lines to know start and end
		ArrayList<EPLine> tempLineList = new ArrayList<EPLine>();
		ArrayList<LadderRow> tempLadderList = new ArrayList<LadderRow>();
		
		try {
			System.out.println("Loading...");
			reader = new BufferedReader(new FileReader(selectedFile));
			strLine = reader.readLine();
			
			while (strLine != null) {
				
				data = csvToData(strLine);
				
				if (data.size() > 0) {
					
					if (Objects.equals(data.get(0), COMPONENT)) {
						try {
							tempComList.add(loadComponent(data));
						} catch (ComponentInfoException e) {
							// TODO Auto-generated catch block
							System.out.println(e.getMessage());
							e.printStackTrace();
						}
					} else if (Objects.equals(data.get(0), LINE)) {
						try {
							tempLineList.add(loadLine(data, tempComList));
						} catch (LineInfoException e) {
							// TODO Auto-generated catch block
							System.out.println(e.getMessage());
							e.printStackTrace();
						}
					} else if (Objects.equals(data.get(0), LADDER)) { 
						try {
							tempLadderList.add(loadLadder(data));
						} catch (LadderInfoException e) {
							// TODO Auto-generated catch block
							System.out.println(e.getMessage());
							e.printStackTrace();
						}
					} else {
						System.out.println("CSV has unidentifiable data");
					}
				} else {
					System.out.println("CSV file has empty line");
				}
				System.out.println("Reading...");
				strLine = reader.readLine();
			}
			
			for (Component cpnt: tempComList) {
				controller.addComponent(cpnt);
				System.out.println("loaded: " + cpnt.toString());
			}
			
			for (EPLine line: tempLineList) {
				controller.addLine(line);
				System.out.println("loaded: " + line.toString());
			}
			
			for (LadderRow lr: tempLadderList) {
				controller.addLadderRow(lr);
				System.out.println("loaded: " + lr.toString());
			}
			
			System.out.println("Fully Loaded");
			tempComList = null;
			tempLineList = null;
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			tempComList = null;
			tempLineList = null;
			Main.popUp("Error: File not found");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			tempComList = null;
			tempLineList = null;
			Main.popUp("Error: Unexpected IOException, file may be locked");
			e.printStackTrace();
		}
		
		return false;
	}
	
	/*
	 * For future uses.
	 * Return if operation is successful. 
	 * Overwrites existing file
	 * TODO: lock file while writing with File Channel
	 */
	public boolean saveData() {
		
		checkFileDir();
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save File");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Comma-Separated Values", "*.csv"));
		fileChooser.setInitialDirectory(new File(DATA_DIR));
		Stage mainStage = new Stage();
		//Scene scene = new Scene(fileChooser);
		File selectedFile = fileChooser.showSaveDialog(mainStage);
		if (null == selectedFile) {
			System.out.println("File not selected");
			return false;
		}
		/*if (selectedFile != null) {
			 mainStage.setScene(scene);
		    mainStage.display(selectedFile);
		}*/
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(selectedFile));
			
			System.out.println("Saving...");
			
			// Save Components
			for (Component cpnt: controller.getComList()) {
				writer.write(saveComponent(cpnt));
			}
			
			// Save Lines
			for (EPLine line: controller.getLineList()) {
				writer.write(saveLine(line));
			}
			
			// Save Ladder
			for (LadderRow lr: controller.getLadderList()) {
				writer.write(saveLadder(lr));
			}
			
			writer.flush();	// may be extra
			writer.close();
			System.out.println("Saved");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Main.popUp("Error: Unexpected IOException, file may be locked");
			e.printStackTrace();
		}
		
		return false;
	}
	
	/*
	 * 
	 */
	private Component loadComponent(ArrayList<String> comData) throws ComponentInfoException {
		double translateX = new Double(comData.get(1)).doubleValue();
		double translateY = new Double(comData.get(2)).doubleValue();
		
		String comType = comData.get(3);
		String extraOne = comData.get(4);
		String extraTwo = comData.get(5);
		String labelNameOne = comData.get(6);
		String labelNameTwo = comData.get(7);
		Component cpnt;
		
		if (Objects.equals(COMPRESSOR, comType)) {
			cpnt = new Compressor();
		} else if (Objects.equals(EXHAUST, comType)) {
			cpnt = new Exhaust();
		} else if (Objects.equals(CYLINDER, comType)) {
			boolean switchOne = Boolean.valueOf(extraOne);
			boolean switchTwo = Boolean.valueOf(extraTwo);
			cpnt = new Cylinder(switchOne, switchTwo, labelNameOne, labelNameTwo);
		} else if (Objects.equals(THREETWO, comType)) {
			Component.EndType endOne = Component.EndType.valueOf(extraOne);
			Component.EndType endTwo = Component.EndType.valueOf(extraTwo);
			cpnt = new ThreeTwo(endOne, endTwo, labelNameOne, labelNameTwo);
		} else if (Objects.equals(FOURTWO, comType)) {
			Component.EndType endOne = Component.EndType.valueOf(extraOne);
			Component.EndType endTwo = Component.EndType.valueOf(extraTwo);
			cpnt = new FourTwo(endOne, endTwo, labelNameOne, labelNameTwo);
		} else {
			cpnt = new Compressor();	// not supposed to call parent Component, thus a random Component
			// TODO: Exception for this
			Main.popUp("Error: Unable to load Component");

			throw new ComponentInfoException("More or less information than Component requires");
		}
		
		cpnt.setTranslateX(translateX);
		cpnt.setTranslateY(translateY);
		
		return cpnt;
	}
	
	private String saveComponent(Component cpnt) {
		ArrayList<String> data = new ArrayList<String>();
		
		data.add(COMPONENT);
		
		// Position
		data.add(Double.toString(cpnt.getTranslateX()));
		data.add(Double.toString(cpnt.getTranslateY()));
		
		String comType;
		String extraOne = NA, extraTwo = NA;
		String labelNameOne = NA, labelNameTwo = NA;
		// Put extra details in if-else block
		if (cpnt instanceof Compressor) {
			comType = COMPRESSOR;
		} else if (cpnt instanceof Exhaust) {
			comType = EXHAUST;
		} else if (cpnt instanceof Cylinder) {
			Cylinder cyl = (Cylinder) cpnt;
			extraOne = Boolean.toString(cyl.getSwitchOne());
			extraTwo = Boolean.toString(cyl.getSwitchTwo());
			comType = CYLINDER;
			if (true == cyl.getSwitchOne()) {
				labelNameOne = cyl.labelOne.getText();
			}
			if (true == cyl.getSwitchTwo()) {
				labelNameTwo = cyl.labelTwo.getText();
			}
		} else if (cpnt instanceof ThreeTwo) {
			ThreeTwo threeTwo = (ThreeTwo) cpnt;
			extraOne = threeTwo.getEndTypeOne().toString();
			extraTwo = threeTwo.getEndTypeTwo().toString();
			comType = THREETWO;
			if (hasToSaveLabel(threeTwo.getEndTypeOne())) {
				labelNameOne = threeTwo.labelOne.getText();
			}
			if (hasToSaveLabel(threeTwo.getEndTypeTwo())) {
				labelNameTwo = threeTwo.labelTwo.getText();
			}
		} else if (cpnt instanceof FourTwo) {
			FourTwo fourTwo = (FourTwo) cpnt;
			extraOne = ((FourTwo) cpnt).getEndTypeOne().toString();
			extraTwo = ((FourTwo) cpnt).getEndTypeTwo().toString();
			comType = FOURTWO;
			if (hasToSaveLabel(fourTwo.getEndTypeOne())) {
				labelNameOne = fourTwo.labelOne.getText();
			}
			if (hasToSaveLabel(fourTwo.getEndTypeTwo())) {
				labelNameTwo = fourTwo.labelTwo.getText();
			}
		} else {
			comType = "Error";
			Main.popUp("Error: Unable to save component");
		}
		data.add(comType);
		data.add(extraOne);
		data.add(extraTwo);
		data.add(labelNameOne);
		data.add(labelNameTwo);
		
		System.out.println("Saving: " + cpnt.toString());
		
		return dataToCSV(data);	
	}
	
	private EPLine loadLine(ArrayList<String> lineData, ArrayList<Component> tempComList) throws LineInfoException {
		EPLine line;
		
		// TODO: Magic numbers
		if (lineData.size() == 5) {
			int startCNum = Integer.parseInt(lineData.get(1));
			int startPNum = Integer.parseInt(lineData.get(2));
			int endCNum = Integer.parseInt(lineData.get(3));
			int endPNum = Integer.parseInt(lineData.get(4));
			
			Point start = tempComList.get(startCNum).getPoints().get(startPNum);
			Point end = tempComList.get(endCNum).getPoints().get(endPNum);
			
			line = new EPLine(start, end);
			/*line = new EPLine(start, end, 
					start.parent.getTranslateX() + start.getCenterX(),
					start.parent.getTranslateY() + start.getCenterY(),
					end.parent.getTranslateX() + end.getCenterX(),
					end.parent.getTranslateY() + end.getCenterY());*/
			start.getLines().add(line);
			end.getLines().add(line);
		} else {
			throw new LineInfoException("More or less information than Line requires");
		}
		
		System.out.println("loadLine");
		
		return line;
	}
	
	private String saveLine(EPLine line) {
		ArrayList<String> data = new ArrayList<String>();
		
		data.add(LINE);
		
		Point start = line.getStart(), end = line.getEnd();
		Component startCpnt = start.parent, endCpnt = end.parent;
		
		data.add(Integer.toString(controller.getComList().indexOf(startCpnt)));
		data.add(Integer.toString(startCpnt.getPointIndex(start)));
		data.add(Integer.toString(controller.getComList().indexOf(endCpnt)));
		data.add(Integer.toString(endCpnt.getPointIndex(end)));
		
		return dataToCSV(data);
	}
	
	/*
	 *  Note: 	loading VERT was prevented because of line by line loading. 
	 *  		Work around by setting VERT when encountering vertUp. May be abused.
	 *  		TODO: a check for VERT when encountering vertUp
	 */
	private LadderRow loadLadder(ArrayList<String> ladderData) throws LadderInfoException {
		LadderRow lr = new LadderRow();
		
		// TODO: Magic number
		if (ladderData.size() == LadderRow.colSize*ladderNum+1) {
			for (int i = 0; i < ladderData.size()/ladderNum; i++) {
				String type = ladderData.get(i*ladderNum+1);
				String op = ladderData.get(i*ladderNum+2);
				String label = ladderData.get(i*ladderNum+3);
				String vert = ladderData.get(i*ladderNum+4);
				String vertUp = ladderData.get(i*ladderNum+5);
				
				if (Objects.equals(type, MIDDLE)) {
					if (i > 0 && i < LadderRow.colSize-2 && lr.getLadderComponent(i) instanceof Middle) {
						Middle lc = (Middle) lr.getLadderComponent(i);
						lc.getName().setText(label);
						CheckMenuItem cmi =  lc.getCheckMenuItem(op);
						if (cmi != null) {
							System.out.println(cmi.toString());
							cmi.setSelected(true);
							lc.updateCheckMenuItems(cmi);
							lc.setOption(cmi);
						}
						if (Objects.equals(vert, VERT)) {
							lc.setVert(true);
						}
						if (Objects.equals(vertUp, TRUE)) {
							lc.setVertUp(true);
						}
					} else {
						Main.popUp("Ladder information may be corrupted");
					}
				} else if (Objects.equals(type, SECONDFROMRIGHT)) {
					if (i == LadderRow.colSize-2 && lr.getLadderComponent(i) instanceof SecondFromRight) {
						SecondFromRight lc = (SecondFromRight) lr.getLadderComponent(i);
						lc.getName().setText(label);
						CheckMenuItem cmi =  lc.getCheckMenuItem(op);
						if (cmi != null) {
							cmi.setSelected(true);
							lc.updateCheckMenuItems(cmi);
							lc.setOption(cmi);
						}
						if (Objects.equals(vert, VERT)) {
							lc.setVert(true);
						}
						if (Objects.equals(vertUp, TRUE)) {
							lc.setVertUp(true);
							controller.updateVert(lc, true);
						}
					} else {
						Main.popUp("Ladder information may be corrupted");
					}
				}
			}
		} else {
			throw new LadderInfoException("More or less information than Line requires");
		}
		
		return lr;
	}
	
	private String saveLadder(LadderRow lr) {
		ArrayList<String> data = new ArrayList<String>();

		data.add(LADDER);
		for (Node node: lr.getChildren()) {
			String type = NA, op = NA, label = NA, vert = NA, vertUp = NA;
			if (node instanceof Middle) {
				Middle lc = (Middle) node;
				type = MIDDLE;
				label = lc.getName().getText();
				CheckMenuItem cmi = lc.getSelected();
				if (cmi != null) {
					op = cmi.getText();
				}
				if(lc.getCheckMenuItem(VERT).isSelected()) {
					vert = VERT;
				}
				vertUp = String.valueOf(lc.getVertUp());
			} else if (node instanceof SecondFromRight) {
				SecondFromRight lc = (SecondFromRight) node;
				type = SECONDFROMRIGHT;
				label = lc.getName().getText();
				CheckMenuItem cmi = lc.getSelected();
				if (cmi != null) {
					op = cmi.getText();
				}
				if (lc.getCheckMenuItem(VERT).isSelected()) {
					vert = VERT;
				}
				vertUp = String.valueOf(lc.getVertUp());
			} else if (node instanceof LeftEdge) {
				type = LEFTEDGE;
			} else if (node instanceof RightEdge) {
				type = RIGHTEDGE;
			}
			
			data.add(type);
			data.add(op);
			data.add(label);
			data.add(vert);
			data.add(vertUp);
		}

		System.out.println("Saving: " + lr.toString());
		
		return dataToCSV(data);
	}
	
	private ArrayList<String> csvToData(String csvLine) {
		ArrayList<String> data = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		boolean inQuotes = false;
		boolean hasStarted = false;

		for (char ch: csvLine.toCharArray()) {
			if (inQuotes) {
				hasStarted=true;
				if (ch == '\"') {
					inQuotes = false;
				} else {
					sb.append(ch);
				}
			} else {
				if (ch == '\"') {
					inQuotes = true;
					if (hasStarted) {
						sb.append(ch);
					}
				} else if (ch == ',') {
					data.add(sb.toString());
					sb = new StringBuilder();
					hasStarted = false;
				} else {
					sb.append(ch);
				}
			}
		}
		
		if (sb.length() > 0) {	// last element has no comma
			data.add(sb.toString());
			sb = new StringBuilder();
		}
		
		return data;
	}
	
	private String dataToCSV(ArrayList<String> data) {
		StringBuilder sb = new StringBuilder();
		
		for (String str: data) {
			sb.append(QUOTES);
			sb.append(str);
			sb.append(QUOTES);
			sb.append(CSV_DELIMITER);
		}
		
		sb.setLength(Math.max(sb.length() - 1, 0));	// removes last char if length > 0
		sb.append(NEWLINE);
		
		return sb.toString();
	}
	
	private boolean hasToSaveLabel(EndType et) {
		if (et == EndType.SOLENOID || et == EndType.PUSH_BUTTON) {
			return true;
		} else {
			return false;
		}
	}
}