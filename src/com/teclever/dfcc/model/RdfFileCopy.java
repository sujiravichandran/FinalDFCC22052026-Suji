package com.teclever.dfcc.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class RdfFileCopy {
	
	private String filePath;
	private String status;
	private String stageId;
	private String stagePath;
	private BooleanProperty selected = new SimpleBooleanProperty(false);

	public RdfFileCopy(String filePath, String status, boolean selected,String stageId,String stagePath) {
		this.filePath = filePath;
		this.status = status;
		this.selected.set(selected);
		this.stageId = stageId;
		this.stagePath = stagePath;
		
	}

	public String getStagePath() {
		return stagePath;
	}

	public void setStagePath(String stagePath) {
		this.stagePath = stagePath;
	}

	public String getStageId() {
		return stageId;
	}

	public void setStageId(String stageId) {
		this.stageId = stageId;
	}

	public BooleanProperty getSelected() {
		return selected;
	}

	public void setSelected(BooleanProperty selected) {
		this.selected = selected;
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
