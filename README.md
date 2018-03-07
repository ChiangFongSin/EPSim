# EPSim
##### Electro-Pneumatic Simlator
At this stage, users are able to create electro-pneumatic circuits with a ladder diagram similar to a Programmable Logic Controller(PLC). 
Wholly pneumatic circuits are not supported.

Lab 2 and Lab 3 csv files good indicators of the current capabilities. Generally 'run with step' is used together with the START and/or STOP push buttons.

# How to use:
### Components
**Double click** on components from the list on the left and a copy will be created in the working area. Enter names as necessary.
**Click and drag** the components to position.
**Ctrl + left click** to delete components

### How to connect components
**Click** on the dots from different components to connect them. **Right clicking** on empty space will cancel the first selection.
**Right click** on the lines to delete the lines.

### Ladder Diagram
The interface is similar to a programming PLC. It is a grid system.

Add or remove rows from the "File" Menu.

**Right click** to show the menu to select options for the grid cell. Enter names as necessary.

### Import/Export or Load/Save
Save your files and load them for future use (**always save your file before running**)

### Running simulation
The options in the "Run" Menu are:

"Run" will execute the circuit as in without input from user.

"Run with step" will allow user to edit ladder diagram's push button to be activated or deactivated to simulate momentary pressing of push buttons.
This is useful in the case of START and STOP push buttons.

The keyboard shortcuts are: **Ctrl+1** (start run with steps; **Ctrl+2** (stepping); **Ctrl+3** (immediate termination of current run with steps).

**Ctrl+4** shows the sequence of the current/last simulation run.
