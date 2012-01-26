package ead.engine.core.input.states;

import ead.engine.core.input.InputState;

public class KeyboardState implements InputState {
	
	private char key;
	
	public KeyboardState(char key ){
		this.key = key;
	}
	
	public char getKey( ){
		return key;
	}

}