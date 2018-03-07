/*
 *  Copyright (c) 2017-2018 Chiang Fong Sin
 *  @author Chiang Fong Sin
 */
package application;

public class EPException extends Exception {
	EPException(String message) {
		super(message);
	}
}

class ComponentInfoException extends EPException {
	public ComponentInfoException(String message) {
        super(message);
    }
}

class LineInfoException extends EPException {
	public LineInfoException(String message) {
        super(message);
    }
}

class LadderInfoException extends EPException {
	public LadderInfoException(String message) {
        super(message);
    }
}

class PointNotFoundException extends EPException {
    public PointNotFoundException(String message) {
        super(message);
    }
}

class SolenoidAlreadyExistsException extends EPException {
	public SolenoidAlreadyExistsException(String message) {
		super(message);
	}
}