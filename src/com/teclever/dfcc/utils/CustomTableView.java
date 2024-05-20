package com.teclever.dfcc.utils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Blob;
import java.sql.SQLException;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Insets;
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
	public static final EventType<Event> VIEW_BUTTON_CLICKED_EVENT = new EventType<>(Event.ANY, "VIEW_BUTTON_CLICKED");
	public static final EventType<Event> DELETE_BUTTON_CLICKED_EVENT = new EventType<>(Event.ANY,
			"DELETE_BUTTON_CLICKED");
	public static final EventType<Event> COLUMN_BUTTON_CLICKED_EVENT = new EventType<>(Event.ANY,
			"COLUMN_BUTTON_CLICKED");

	String classname;
	String digitalSignature;

	@SuppressWarnings("deprecation")
	public CustomTableView(ObservableList<T> items, Class<T> clazz, boolean addUserColumn, boolean addCheckColumn) {
		super(items);
		classname = clazz.getSimpleName();
		setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		getStylesheets().add(getClass().getResource("/com/teclever/dfcc/ui/css/CustomTableView.css").toExternalForm());
		setTableMenuButtonVisible(false);
		setPadding(new Insets(10));
		setStyle("-fx-background-color:#222831;");

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

			TableColumn<T, Object> column;

			if (field.getType() == Blob.class) {
				column = new TableColumn<>(name.toUpperCase());
				column.setCellValueFactory(cellData -> {
					T value = cellData.getValue();
					try {
						Blob blob = (Blob) field.get(value);
						if (blob != null) {
							InputStream inputStream = blob.getBinaryStream();
							Image image = new Image(inputStream);
							return new SimpleObjectProperty<>(image);
						} else {
							return new SimpleObjectProperty<>(null);
						}
					} catch (SQLException | IllegalAccessException e) {
						e.printStackTrace();
						return new SimpleObjectProperty<>(null);
					}
				});
				column.setCellFactory(e -> new TableCell<T, Object>() {
					private final ImageView imageView = new ImageView();

					@Override
					protected void updateItem(Object item, boolean empty) {
						super.updateItem(item, empty);
						if (empty || item == null) {
							setGraphic(null);
						} else {
							Image image = (Image) item;
							imageView.setImage(image);
							imageView.setFitWidth(100);
							imageView.setFitHeight(60);
							setGraphic(imageView);
						}
					}
				});
			} else {
				column = new TableColumn<>(name.toUpperCase());
				column.setReorderable(false);
				column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
				if (field.getName().equals("fileName")) {
					column.setCellFactory(e -> new TableCell<T, Object>() {
						@Override
						protected void updateItem(Object item, boolean empty) {
							super.updateItem(item, empty);
							if (empty || item == null) {
								setText(null);
								setGraphic(null);
							} else {
								setText(item.toString());
								setGraphic(null);
								setStyle("-fx-alignment: CENTER-LEFT; -fx-padding: 0 0 0 150px;");
							}
						}
					});
				}
			}

			getColumns().add(column);
		}
		resizeColumnsToFitContent();
	}

	public void addNewUserColumn() {
		TableColumn<T, Void> actionCol = new TableColumn<>();
		actionCol.setReorderable(false);
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
					cellWidth = measureTextWidth("Empty", column);
				}
				maxWidth = Math.max(maxWidth, cellWidth);
			}
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
	private final Button viewBtn;
	private final CustomTableView<T> tableView;

	public NewTableCellCheck(CustomTableView<T> tableView) {
		this.tableView = tableView;
		hBox = new HBox(10);
		hBox.setAlignment(Pos.CENTER);

		delBtn = createButton("/Resources/Images/DeleteIcon.png", CustomTableView.DELETE_BUTTON_CLICKED_EVENT);
		editBtn = createButton("/Resources/Images/EditIcon.png", CustomTableView.EDIT_BUTTON_CLICKED_EVENT);
		viewBtn = createButton("/Resources/Images/View.png", CustomTableView.VIEW_BUTTON_CLICKED_EVENT);

		hBox.getChildren().add(delBtn);
	}

	private Button createButton(String iconPath, EventType<Event> eventType) {
		Image image = new Image(getClass().getResourceAsStream(iconPath));
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(20);
		imageView.setFitHeight(20);
		Button button = new Button();
		button.setGraphic(imageView);
		button.setStyle(
				"-fx-background-color: transparent; -fx-border-color: transparent;-fx-padding: 0; -fx-margin: 0;-fx-cursor:hand;");
		button.setOnAction(event -> {
			tableView.getSelectedItems().clear();
			T rowData = getTableView().getItems().get(getIndex());
			tableView.getSelectionModel().select(rowData);
			tableView.getSelectedItems().add(rowData);
			fireEvent(new Event(eventType));
		});
		return button;
	}

	@Override
	protected void updateItem(Void item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setGraphic(null);
		} else {
			T rowData = getTableView().getItems().get(getIndex());
			if (rowData != null) {
				String className = rowData.getClass().getSimpleName();
				hBox.getChildren().clear();
				hBox.getChildren().add(delBtn);
				switch (className) {
				case "User":
					hBox.getChildren().add(0, editBtn);
					break;
				case "AitessMacroFiles":
				case "AitessSymbolFiles":
					hBox.getChildren().add(0, viewBtn);
					break;
				default:
					break;
				}
			}
			setGraphic(hBox);
		}
	}
}
