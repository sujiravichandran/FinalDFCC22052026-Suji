package com.teclever.dfcc.model;

public class Macros {

	private int buttonNo;

	private String buttonName;

	private String buttonCommand;

	public Macros(int buttonNo, String buttonName, String buttonCommand) {
		super();
		this.buttonNo = buttonNo;
		this.buttonName = buttonName;
		this.buttonCommand = buttonCommand;
	}

	public int getButtonNo() {
		return buttonNo;
	}

	public void setButtonNo(int buttonNo) {
		this.buttonNo = buttonNo;
	}

	public String getButtonName() {
		return buttonName;
	}

	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}

	public String getButtonCommand() {
		return buttonCommand;
	}

	public void setButtonCommand(String buttonCommand) {
		this.buttonCommand = buttonCommand;
	}

}
