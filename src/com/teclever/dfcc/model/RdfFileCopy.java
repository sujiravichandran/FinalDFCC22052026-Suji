package com.teclever.dfcc.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class RdfFileCopy {
	
	private String filePath;
	private String status;
	private BooleanProperty selected = new SimpleBooleanProperty(false);

	public RdfFileCopy(String filePath, String status, boolean selected) {
		this.filePath = filePath;
		this.status = status;
		this.selected.set(selected);
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isSelected() {
		return selected.get();
	}

	public void setSelected(boolean selected) {
		this.selected.set(selected);
	}

	public BooleanProperty selectedProperty() {
		return selected;
	}
}
