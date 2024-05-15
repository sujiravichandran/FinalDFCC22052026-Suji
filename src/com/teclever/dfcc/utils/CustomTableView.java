package com.teclever.dfcc.utils;

import java.lang.reflect.Field;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class CustomTableView<T> extends TableView<T> {
	private final ObservableList<T> selectedItems = FXCollections.observableArrayList();
	private static final double MIN_COLUMN_WIDTH = 50;

	public ObservableList<T> getSelectedItems() {
		return selectedItems;
	}

	public static final EventType<Event> EDIT_BUTTON_CLICKED_EVENT = new EventType<>(Event.ANY, "EDIT_BUTTON_CLICKED");
	public static final EventType<Event> DELETE_BUTTON_CLICKED_EVENT = new EventType<>(Event.ANY, "DELETE_BUTTON_CLICKED");
    public static final EventType<Event> COLUMN_BUTTON_CLICKED_EVENT = new EventType<>(Event.ANY, "COLUMN_BUTTON_CLICKED");

	String classname;

	@SuppressWarnings("deprecation")
	public CustomTableView(ObservableList<T> items, Class<T> clazz, boolean addUserColumn, boolean addCheckColumn) {
		super(items);
		classname = clazz.getSimpleName();
		setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		getStylesheets().add(getClass().getResource("/com/teclever/dfcc/ui/css/CustomTableView.css").toExternalForm());
		setTableMenuButtonVisible(false); // Hide the table menu button
		if (addCheckColumn) {
			addCheckboxColumn();
		}
		initializeColumns(clazz);
		if (addUserColumn) {
			addNewUserColumn();
		}
	}

	private void initializeColumns(Class<T> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			String name = field.getName();
			name = name.replaceAll("([a-z])([A-Z])", "$1 $2");
			TableColumn<T, String> column = new TableColumn<>(name.toUpperCase());
			column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
			column.setReorderable(false);
			getColumns().add(column);
		}
		resizeColumnsToFitContent();
	}

	public void addNewUserColumn() {

		Button addButton = new Button("+ Add " + classname);
		addButton.setStyle("-fx-font-size: 18px;-fx-background-color: #169BD5; -fx-text-fill: white;-fx-margin:5px");
		addButton.setOnAction(event -> {
			fireEvent(new Event(CustomTableView.COLUMN_BUTTON_CLICKED_EVENT));
		});

		TableColumn<T, Void> actionCol = new TableColumn<>();
		if (classname.equals("User")) {
			actionCol.setGraphic(addButton);
		}

		actionCol.setReorderable(false);
		actionCol.setPrefWidth(20);
		actionCol.setCellFactory(col -> new NewTableCellCheck<>(this));
		getColumns().add(actionCol);
	}

	private void addCheckboxColumn() {
		TableColumn<T, Boolean> checkboxColumn = new TableColumn<>("");
		checkboxColumn.setCellValueFactory(cellData -> {
			T item = cellData.getValue();
			SimpleBooleanProperty booleanProp = new SimpleBooleanProperty();
			booleanProp.addListener((observable, oldValue, newValue) -> {
				// No need to do anything here, handling selection in CheckBoxTableCell
			});
			return booleanProp;
		});

		checkboxColumn.setCellFactory(p -> new CheckBoxTableCell<>(this));
		checkboxColumn.setReorderable(false);
		checkboxColumn.setPrefWidth(30);
		getColumns().add(0, checkboxColumn);
	}

	private void resizeColumnsToFitContent() {
		for (TableColumn<T, ?> column : getColumns()) {
			column.setPrefWidth(TableView.USE_COMPUTED_SIZE);
			double maxWidth = 0;
			for (int i = 0; i < getItems().size(); i++) {
				Object cellData = column.getCellData(i);
				double cellWidth = 0;
				if (cellData != null) {
					cellWidth = measureTextWidth(cellData.toString(), column);
				} else {
					// Handle null cellData - use a generic placeholder text if appropriate
					cellWidth = measureTextWidth("Empty", column);
				}
				maxWidth = Math.max(maxWidth, cellWidth);
			}
			// Ensure the column width is not less than a minimum width
			column.setPrefWidth(Math.max(MIN_COLUMN_WIDTH, maxWidth + 10));
		}
	}

	private double measureTextWidth(String text, TableColumn<T, ?> column) {
		javafx.scene.text.Text helper = new javafx.scene.text.Text();
		helper.setText(text);
		return helper.getLayoutBounds().getWidth();
	}

}

class CheckBoxTableCell<T> extends TableCell<T, Boolean> {
	private final CheckBox checkBox;
	private final CustomTableView<T> tableView;

	public CheckBoxTableCell(CustomTableView<T> tableView) {
		this.tableView = tableView;
		checkBox = new CheckBox();
		checkBox.setAlignment(Pos.CENTER);
		setAlignment(Pos.CENTER);
		setGraphic(checkBox);

		checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (!isEmpty()) {
				T item = getTableView().getItems().get(getIndex());
				if (newValue) {
					if (!tableView.getSelectedItems().contains(item)) {
						tableView.getSelectedItems().add(item);
					}
				} else {
					tableView.getSelectedItems().remove(item);
				}
				tableView.getSelectionModel().select(getIndex());
			}
		});
	}

	@Override
	protected void updateItem(Boolean item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setGraphic(null);
		} else {
			setGraphic(checkBox);
			checkBox.setSelected(item);
			final TableRow<T> row = getTableRow();
			if (row != null) {
				row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
					checkBox.setSelected(isNowSelected);
				});

				checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
					if (isNowSelected != row.isSelected()) {
						if (isNowSelected) {
							getTableView().getSelectionModel().select(getIndex());
						} else {
							getTableView().getSelectionModel().clearSelection(getIndex());
						}
					}
				});
			}
		}
	}
}

class NewTableCellCheck<T> extends TableCell<T, Void> {
	private final HBox hBox;
	private final Button editBtn;
	private final Button delBtn;
	private final CustomTableView<T> tableView;

	public NewTableCellCheck(CustomTableView<T> tableView) {
		this.tableView = tableView;
		hBox = new HBox(5);
		hBox.setAlignment(Pos.CENTER);

		// Initialize the delete button with its icon
		Image deleteImage = new Image(getClass().getResourceAsStream("/Resources/Images/DeleteIcon.png"));
		ImageView deleteImageView = new ImageView(deleteImage);
		deleteImageView.setFitWidth(20);
		deleteImageView.setFitHeight(20);
		delBtn = new Button();
		delBtn.setGraphic(deleteImageView);
		delBtn.setStyle(
				"-fx-background-color: transparent; -fx-border-color: transparent;-fx-padding: 0; -fx-margin: 0;");
		delBtn.setOnAction(event -> {
			tableView.getSelectedItems().clear();
			T rowData = getTableView().getItems().get(getIndex());
			tableView.getSelectionModel().select(rowData);
			tableView.getSelectedItems().add(rowData);
			fireEvent(new Event(CustomTableView.DELETE_BUTTON_CLICKED_EVENT));
		});

		// Initialize the edit button with its icon
		Image editImage = new Image(getClass().getResourceAsStream("/Resources/Images/EditIcon.png"));
		ImageView editImageView = new ImageView(editImage);
		editImageView.setFitWidth(20);
		editImageView.setFitHeight(20);
		editBtn = new Button();
		editBtn.setGraphic(editImageView);
		editBtn.setStyle(
				"-fx-background-color: transparent; -fx-border-color: transparent;-fx-padding: 0; -fx-margin: 0;");
		editBtn.setOnAction(event -> {
			tableView.getSelectedItems().clear();
			T rowData = getTableView().getItems().get(getIndex());
			tableView.getSelectionModel().select(rowData);
			tableView.getSelectedItems().add(rowData);
			fireEvent(new Event(CustomTableView.EDIT_BUTTON_CLICKED_EVENT));
		});

		// Initially add only the delete button
		hBox.getChildren().add(delBtn);
	}

	@Override
	protected void updateItem(Void item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setGraphic(null);
		} else {
			// Dynamically add the edit button if the class name is 'User'
			T rowData = getTableView().getItems().get(getIndex());
			if (rowData != null && rowData.getClass().getSimpleName().equals("User")) {
				if (!hBox.getChildren().contains(editBtn)) {
					hBox.getChildren().add(0, editBtn); // Add edit button before delete button
				}
			} else {
				hBox.getChildren().remove(editBtn); // Remove edit button if not 'User'
			}
			setGraphic(hBox);
		}
	}
}
