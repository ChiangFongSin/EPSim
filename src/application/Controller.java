/*
 *  Copyright (c) 2017-2018 Chiang Fong Sin
 *  @author Chiang Fong Sin
 */
package application;

import java.util.ArrayList;
import java.util.HashMap;
import application.LadderComponent.Options;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/*
 *  Singleton
 *  addComponent and addLine are for DataManager
 *  supports cross-object operations
 */
class Controller{
	private static Controller controller;
	private HashMap<String, Boolean> solenoidList = new HashMap<String, Boolean>();
	// Master list of components and lines
    private ArrayList<Component> comList = new ArrayList<Component>();
    private ArrayList<EPLine> lineList = new ArrayList<EPLine>();
    private ArrayList<LadderRow> ladderList = new ArrayList<LadderRow>();
    private Pane commonPane = new Pane();
    private VBox ladderPane = new VBox();
    private String sequence = "";
    
	private Controller() {
		
	}
	
	public static Controller getInstance() {
		if (null == controller) {
            controller = new Controller();
        }
        return controller;
	}
	
	// should be called by Main only
	public void reset() {
		solenoidList = new HashMap<String, Boolean>();
		comList = new ArrayList<Component>();
		lineList = new ArrayList<EPLine>();
		ladderList = new ArrayList<LadderRow>();
		commonPane = new Pane();
		ladderPane = new VBox();
	}
	
	public Pane getCommonPane() {
		return commonPane;
	}
	
	public VBox getLadderPane() {
		return ladderPane;
	}
	
	// solenoidList
	public void setSolenoid(String name, boolean status) {
		if (name == "M1") {
			System.out.println("Setting " + name + " to " + status);
		}
		solenoidList.put(name, new Boolean(status));
	}
	
	// reset when solenoid is removed
	public void resetSolenoid(String name) {
		solenoidList.remove(name);
	}
	
	// WARNING: May return null!
	public Boolean getSolenoid(String name) {
		return solenoidList.get(name);
	}
	
	public int getSolenoidListSize() {
		return solenoidList.size();
	}
	
	/*public String getNextAvailableSolenoid() {
		for (int i = 0; i < 26; i++) {
			int integer = 'A' + i;
			char c = (char) integer;
			for (int j = 0; j < solenoidList.size(); j++) {
				// check for existing letters
				if (null == getSolenoid(String.valueOf(c))) {
					return String.valueOf(c);
				}
			}
		}
		
		System.out.println("Max solenoids");
		Main.popUp("Maximum 26 solenoids");
		
		return null;
	}*/
	
	// TODO: implement ways to delete cpnt with solenoids. Currently reset solenoidList
	public void deleteSolenoid(String name) {
		solenoidList.put(name, null);
	}
	
	// comList
	public ArrayList<Component> getComList() {
		return comList;
	}
	public void addComponent(Component cpnt) {
		comList.add(cpnt);
		commonPane.getChildren().add(cpnt);
	}
	public void removeComponent(Component cpnt) {
		comList.remove(cpnt);
		commonPane.getChildren().remove(cpnt);
	}
	
	// lineList
	public ArrayList<EPLine> getLineList() {
		return lineList;
	}
	public void addLine(EPLine line) {
		lineList.add(line);
		commonPane.getChildren().add(0, line);	// to the back so that points remain in front
	}
	public void removeLine(EPLine line) {
		line.getStart().removeLine(line);
		line.getEnd().removeLine(line);
		lineList.remove(line);
		commonPane.getChildren().remove(line);
		System.out.println("Line deleted");
	}
	
	// ladderList
	public ArrayList<LadderRow> getLadderList() {
		return ladderList;
	}
	public void addLadderRow(LadderRow lr) {
		int index = ladderList.size();
		ladderList.add(index, lr);
		ladderPane.getChildren().add(index, lr);
	}
	public void addLadderRow(int index, LadderRow lr) {
		ladderList.add(index, lr);
		ladderPane.getChildren().add(index, lr);
	}
	public void removeLadderRow(int index) {
		if (index >= 0 && index < ladderList.size()) {
			removingCheck(ladderList.get(index));
			ladderList.remove(index);
			ladderPane.getChildren().remove(index);
		} else {
			System.out.println("ladderList index out of bounds");
		}
	}
	public void removeLadderRow(LadderRow lr) {
		if (lr != null) {
			removingCheck(lr);
			ladderList.remove(lr);
			ladderPane.getChildren().remove(lr);
		} else {
			System.out.println("ladderRow null pointer");
		}
	}
	/*
	 *  Updates LadderComponent vertUp below source caller.
	 *  pre-condition: caller should be a LadderComponent with VERT selected.
	 *  post-condition: return false if operation fails.
	 */
	public boolean updateVertUp(LadderComponent source, boolean vertUp) {
		for (LadderRow lr : ladderList) {
			for (Node node : lr.getChildren()) {
				if (node == source) {
					Node temp;
					int row = ladderList.indexOf(lr)+1, 
							col = lr.getChildren().indexOf(source);
					if (row < ladderList.size()) {	// check index within bounds
						System.out.println("Found target of vertUp: " + row + " / " + ladderList.size());
						temp = ladderList.get(row).getChildren().get(col);
						if (temp instanceof LadderComponent) {
							LadderComponent target = (LadderComponent) temp;
							target.setVertUp(vertUp);
							System.out.println("Returning true from updateVertUp");
							return true;
						}
					} else {
						System.out.println("No row below");
					}
					return false;
				}
			}
		}
		return false;
	}
	/*
	 *  Updates LadderComponent vert above source caller.
	 *  pre-condition: caller should be a LadderComponent with vertUp changes such as a delete of LadderRow.
	 *  post-condition: return false if operation fails.
	 */
	public boolean updateVert(LadderComponent source, boolean vert) {
		for (LadderRow lr : ladderList) {
			for (Node node : lr.getChildren()) {
				if (node == source) {
					Node temp;
					int row = ladderList.indexOf(lr)-1, 
							col = lr.getChildren().indexOf(source);
					if (row >= 0 && row < ladderList.size()) {	// check index within bounds
						System.out.println("Found target of vert: " + row + " / " + ladderList.size());
						temp = ladderList.get(row).getChildren().get(col);
						if (temp instanceof LadderComponent) {
							LadderComponent target = (LadderComponent) temp;
							CheckMenuItem cmi = target.getCheckMenuItem(DataManager.VERT);
							cmi.setSelected(vert);
							target.setOption(cmi);
							System.out.println("Returning true from updateVert");
							return true;
						}
					} else {
						System.out.println("No row below");
					}
					return false;
				}
			}
		}
		return false;
	}
	/*
	 *  Does checks for vert and vertUps in the rows above and below
	 */
	public void removingCheck(LadderRow source) {
		int row = ladderList.indexOf(source);
		LadderRow rowUp = null, rowDown = null;
		if (row - 1 >= 0) {
			rowUp = ladderList.get(row-1);
		}
		if (row + 1 < ladderList.size()) {
			rowDown = ladderList.get(row+1);
		}
		for (Node node : source.getChildren()) {
			if (!(node instanceof LeftEdge) && !(node instanceof RightEdge)) {
				int col = source.getChildren().indexOf(node);
				if (rowUp != null) {
					Node above = rowUp.getChildren().get(col);
					if (above instanceof LadderComponent) {
						LadderComponent lc = (LadderComponent) above;
						CheckMenuItem cmi = lc.getCheckMenuItem(DataManager.VERT);
						cmi.setSelected(false);
						lc.setOption(cmi);
					}
				}		
				if (rowDown != null) {
					Node below = rowDown.getChildren().get(col);
					if (below instanceof LadderComponent) {
						LadderComponent lc = (LadderComponent) below;
						lc.setVertUp(false);
					}
				}
			}
		}
	}
	
	// Running
	public boolean runOnePass() {
		boolean cpntChanged = false, linesChanged = false;
		cpntChanged = runComponentsOnce();
		linesChanged = runLaddersOnce();
		if (cpntChanged || linesChanged) {
			if (!cpntChanged) {
				return runComponentsOnce();
			}
			return true;
		} else {
			return false;
		}
	}
	
	public boolean runComponentsOnce() {
		boolean hasChanged = false, isChanging = true;
		int i = 0;
		while (isChanging) {
			isChanging = false;
			for (Component cpnt : comList) {
				System.out.println("Propagating component " + cpnt.toString());
				if (cpnt.propagatePressure()) {
					isChanging = true;
					System.out.println("Propagated pressure change");
				}
			}
			i++;
			if (i == 10000) {
				Main.popUp("Conflicting pressure");
				break;
			}
		}
		
		for (Component cpnt : comList) {
			System.out.println("Updating component " + cpnt.toString());
			if (cpnt.updateState()) {
				return true;	// changed to show one component change at a time
				//hasChanged = true;
				//System.out.println("hasChanged = true");
			}
		}
		
		return hasChanged;
	}
	
	/*
	 *  Does not check for completeness of ladder. Assumes false.
	 */
	public boolean runLaddersOnce() {
		if (ladderList.size() == 0) {	// early termination due to no ladder
			return false;
		}
		
		boolean[] hasChanged = new boolean[1];	// to be mutable without implementing wrapper
		boolean[][] boolMap = new boolean[ladderList.size()][LadderRow.colSize];
		boolean[][] checkedMap = new boolean[ladderList.size()][LadderRow.colSize];
		
		// Initialise maps
		for (int i = 0; i < ladderList.size(); i++) {
			boolMap[i][0] = true;		// all leftEdge are true
			checkedMap[i][0] = true;	// leftEdge checked
			checkedMap[i][LadderRow.colSize-1] = true;	// rightEdge checked
		}
		
		// update checkedMap and boolMap
		for (int i = 0; i < ladderList.size(); i++) {
			LadderRow lr = ladderList.get(i);
			for (int j = 1; j < lr.getChildren().size()-1; j++) {
				checkCell(i,j,checkedMap,boolMap,hasChanged);	// always
				//System.out.println("boolMap at: " + i + " " + j);
				//printArray(boolMap, ladderList.size(), LadderRow.colSize);
			}
		}
		//System.out.println("boolMap after once: ");
		//printArray(boolMap, ladderList.size(), LadderRow.colSize);
		
		return hasChanged[0];
	}
	
	/*
	 *  Check sources of true values from the left
	 *  Recursive parts, but not expected to be significant in normal usages
	 *  Recursive call may go up, left, right, or down.
	 *  updates checkedMap and boolMap.
	 */
	private void checkCell(int i, int j, boolean[][] checkedMap, boolean[][] boolMap, boolean[] hasChanged) {
		//System.out.println("Checking cell: " + i + " " + j);
		//printArray(checkedMap, ladderList.size(), LadderRow.colSize);
		if (checkedMap[i][j] == true) {		// checked
			return;
		} else if (i >= 0 && i < ladderList.size() && j >= 1 && j < LadderRow.colSize) {	// not checked and within bounds
			Node node = ladderList.get(i).getChildren().get(j);
			if (node instanceof Middle || node instanceof SecondFromRight) {
				LadderComponent lc = (LadderComponent) node;
				CheckMenuItem cmiSelected = lc.getSelected();
				Options option;
				boolean vert = lc.getVert(),
						vertUp = lc.getVertUp(),
						source = false, 
						newValue = false;
				
				if (checkedMap[i][j-1]) {
					source = source || boolMap[i][j-1];
				} else {
					checkCell(i,j-1,checkedMap,boolMap,hasChanged);
					source = source || boolMap[i][j-1];
				}
				
				int k=1;
				while (vert && i+k < ladderList.size()) {
					checkCell(i+k,j-1,checkedMap,boolMap,hasChanged);
					source = source || boolMap[i+k][j-1];
					vert = ladderList.get(i+k).getLadderComponent(j).getVert();
					k++;
				}

				k = 1;
				while (vertUp && i-k >=0) {
					checkCell(i-k,j-1,checkedMap,boolMap,hasChanged);
					source = source || boolMap[i-k][j-1];
					vertUp = ladderList.get(i-k).getLadderComponent(j).getVertUp();
					k++;
				}

				if (cmiSelected == null) {	// considered BLANK
					newValue = false;
				} else {
					option = Options.valueOf(cmiSelected.getText());

					switch (option) {
					case BLANK:
						newValue = false;
						break;
					case HORI:
						newValue = source;
						break;
					case NO:
						Boolean isClosed = solenoidList.get(lc.getNameString());
						if (isClosed != null && isClosed) {
							newValue = source;
						} else {
							newValue = false;
						}
						break;
					case NC:
						Boolean isOpened = solenoidList.get(lc.getNameString());
						if (isOpened != null && !isOpened) {
							newValue = source;
						} else {
							newValue = false;
						}
						break;
					case PUSH_NO:
						newValue = false;
						break;
					case PUSH_NO_ACTIV:
						newValue = source;
						break;
					case PUSH_NC:
						newValue = source;
						break;
					case PUSH_NC_ACTIV:
						newValue = false;
						break;
					case VERT:
						// Nothing to do here, selected should not return VERT
						System.out.println("Should not have VERT?");
						break;
					case OUT:
						newValue = source;
						Boolean sole = solenoidList.get(lc.getNameString());
						if (sole == null || newValue != sole) {
							solenoidList.put(lc.getNameString(), newValue);
							hasChanged[0] = true;
							System.out.println("ladder hasChanged = true");
						}
						break;
					case NA:
						System.out.println("Should not have NA?");
						break;
					default:
						Main.popUp(this.toString() + LadderComponent.ERROR_ILLEGAL_OPTION);
					}
				}
				
				boolMap[i][j] = newValue;	// update cell based on source and options
			} else if (node instanceof LeftEdge) {
				System.out.println("checkCell LeftEdge");
				boolMap[i][j] = true;
			} else if (node instanceof RightEdge) {
				System.out.println("checkCell RightEdge");
				boolMap[i][j] = false;
			} else {
				Main.popUp("checkCell error!");
			}
		} else {
			System.out.println("checkCell out of bounds.");
		}

		checkedMap[i][j] = true;
		return;
	}

	public void addSequence(String str) {
		sequence += str;
		sequence += " ";
		System.out.println("Adding to sequence: " + str);
	}
	public void showSequence() {
		Main.popUp("Sequence", sequence);
	}
	public void resetSequence() {
		sequence = new String("");
	}
	
	/*
	 *  For debugging.
	 */
	@SuppressWarnings("unused")
	private void printArray(boolean[][] array, int row, int col) {
		for (int i = 0; i < row; i ++) {
			for (int j = 0; j < col; j++) {
				if (array[i][j]) {
					System.out.printf("%2d", 1);
				} else {
					System.out.printf("%2d", 0);
				}
			}
			System.out.printf("\n");
		}
	}
}