package com.teclever.dfcc.Controller.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.dto.SessionDto;
import com.teclever.datastore.dto.SessionResponse;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.SessionTimingService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.UserData;
import com.teclever.dfcc.datastore.configurationmanagement.AitessConfigurationManagement;
import com.teclever.dfcc.datastore.dto.CopyFileDTO;
import com.teclever.dfcc.datastore.dto.LogOutFileCopyResponse;
import com.teclever.dfcc.datastore.dto.SessionList;
import com.teclever.dfcc.datastore.dto.SessionListResponse;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;
import com.teclever.dfcc.utils.Notifications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DataBackupPopupController {

	@FXML
	private AnchorPane dataBackupMainContainer;
	@FXML
	private VBox dataBackupVBox;
	@FXML
	private HBox dataBackupHeading;
	@FXML
	private Label headerLabel;
	@FXML
	private GridPane dataBackupGridPane;
	@FXML
	private HBox buttonHBox;

	private GridPane filterResultGridPane = new GridPane();
	private ComboBox<String> uutTypeField = new ComboBox<String>();
	private ComboBox<String> slNoField = new ComboBox<String>();
	private ComboBox<String> sessionNameField = new ComboBox<String>();
	private HBox selectionBoxSESSION = new HBox(10);
	private HBox selectionHBoxUUTSN = new HBox(10);
	private ObservableList<String> dfccSNList = FXCollections.observableArrayList();
	private List<SessionDto> sessionList = new ArrayList<SessionDto>();
	private ObservableList<String> sessionTypeList = FXCollections.observableArrayList();
	private AitessConfigurationManagement configManager = new AitessConfigurationManagement();
	private ObservableList<UUTMasterDetailsDto> uutDataList;
	private ObservableList<String> uutTypeList = FXCollections.observableArrayList();
	private String UUT_ID;
	private String session_ID;
	private String selectedUttId;
	private SessionService s = new SessionService();

	private Label selectSessionLabel = new Label("Select Session");
	private ObservableList<SessionList> sessionDataList;
	private ObservableList<String> sessionNameList = FXCollections.observableArrayList();
	private String SESSION_ID;

	private Label selectPathLabel = new Label("Select Path");
	private Button selectPathButton = new Button("Select");

	private Button copyButton = new Button("Backup");
	private Button closeButton = new Button("Close");

	private SessionManagement sessionManagement = new SessionManagement();
	private SessionFileManagement sessionFileManagement = new SessionFileManagement();

	public void initialize() {
		dataBackupMainContainer.getStylesheets().add(getClass()
				.getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/DataBackup.css").toExternalForm());
		createDataBackupPopupContent();
		initializeUUTTypeComboBox();
		SessionResponse s1 = s.getAllSession();
		sessionList = s1.getListOfSession();
	}

	private void createDataBackupPopupContent() {
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(33);
		col1.setHgrow(Priority.ALWAYS);

		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(33);
		col2.setHgrow(Priority.ALWAYS);

		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPercentWidth(33);
		col3.setHgrow(Priority.ALWAYS);

		dataBackupGridPane.getColumnConstraints().setAll(col1, col2, col3);

		dataBackupGridPane.setHgap(15); // Horizontal spacing between columns
		dataBackupGridPane.setVgap(15); // Vertical spacing between rows
		dataBackupGridPane.setPadding(new Insets(20)); // Padding around grid

		// ---------- ADD COMPONENTS ----------
		dataBackupGridPane.add(createUutBox(), 0, 0);
		dataBackupGridPane.add(createUUTSerialNoComboBox(), 1, 0);
		dataBackupGridPane.add(createSessionComboBox(), 2, 0);

		dataBackupGridPane.add(selectPathLabel, 0, 2);
		dataBackupGridPane.add(selectPathButton, 1, 2);

		// ---------- MAKE BUTTONS EXPAND ----------
		GridPane.setHgrow(selectPathButton, Priority.ALWAYS);
		selectPathButton.setMaxWidth(Double.MAX_VALUE);

		GridPane.setHgrow(sessionNameField, Priority.ALWAYS);
		sessionNameField.setMaxWidth(Double.MAX_VALUE);
		

		GridPane.setHgrow(selectPathButton, Priority.ALWAYS);
		selectPathButton.setMaxWidth(Double.MAX_VALUE);

		selectPathButton.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File selectedDirectory = directoryChooser.showDialog(dataBackupMainContainer.getScene().getWindow());

			if (selectedDirectory != null) {
				String absolutePath = selectedDirectory.getAbsolutePath();
				selectPathButton.setText(absolutePath);
				Tooltip tooltip = new Tooltip(selectPathButton.getText());
				Tooltip.install(selectPathButton, tooltip);
				tooltip.setShowDelay(Duration.ZERO);
				tooltip.setHideDelay(Duration.ZERO);
			}
		});

		copyButton.setOnAction(e -> {
//			Suji Added::
			SessionFileManagement session = new SessionFileManagement();
			LogOutFileCopyResponse response = session
					.copyingFileWhileDataBackup(StateMachine.currentSessionDetails.getSessionId());
			if (response.getCode() == 100) {
				SessionTimingService s = new SessionTimingService();

				int failedFiles = 0;
				for (CopyFileDTO copy : DFCCConstant.FailedStagesRdfPaths) {
					if (!copy.getStatus().equals("SUCCESS")) {
						failedFiles++;
					}
				}

				SessionTestStateObject.getIsLogoutFileCopyPopupOpened().set(true);
				SessionTestStateObject.getIsRdfFileCopyPopupStatus().set(true);

				StateMachine.setRdfCopy(false);
				StateMachine.setResettingProgressBar(true);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
//			Exit
			handleCopyFolder();
		});

		closeButton.setOnAction(e -> {
			handleClosePopup();
		});

		buttonHBox.getChildren().addAll(copyButton, closeButton);
	}


	private HBox createUutBox() {
		uutTypeField.setPromptText("UUT TYPE");

		HBox uutTypeHBox = new HBox(10);
		uutTypeHBox.setAlignment(Pos.CENTER);
		uutTypeHBox.getChildren().add(uutTypeField);

		return uutTypeHBox;
	}

	private void initializeUUTTypeComboBox() {
		uutDataList = FXCollections.observableArrayList(configManager.getAllUUT());

		for (UUTMasterDetailsDto uut : uutDataList) {
			uutTypeList.add(uut.getUutType());
		}

		uutTypeField.setItems(uutTypeList);

		uutTypeField.setOnAction((event) -> {

			String selectedUUTType = uutTypeField.getSelectionModel().getSelectedItem();
			String uutId = fetchUutId(selectedUUTType);
			selectedUttId = uutId;

			initializeDfccSNComboBox(uutId);
		});
	}

	private String fetchUutId(String uutType) {
		for (UUTMasterDetailsDto uut : uutDataList) {
			if (uut.getUutType().equals(uutType)) {
				return uut.getUutId();
			}
		}
		return null;
	}

	private String fetchSessionId(String sessionType) {
		for (SessionDto sessionId : sessionList) {
			if (sessionId.getSessionName().equals(sessionType)) {
				return sessionId.getSessionId();
			}
		}
		return null;
	}

	private void initializeDfccSNComboBox(String uutTypeId) {
		dfccSNList.clear();
		System.out.println("Check Session List Size " + sessionList.size() + "  " + uutTypeId);
		List<SessionDto> filterSessionList = sessionList.stream().filter(t -> t.getUutId().equals(uutTypeId))
				.collect(Collectors.toList());

		Set<String> seenDfccSNos = new HashSet<>();

		for (SessionDto dfccSn : filterSessionList) {
			String dfccSNo = dfccSn.getDfccSNo();
			if (seenDfccSNos.add(dfccSNo)) { // Only adds if not already in the set
				dfccSNList.add(dfccSNo);
			}
		}

		slNoField.setOnAction(event -> {
			String selectedSerialNo = slNoField.getSelectionModel().getSelectedItem();
			if (selectedSerialNo != null) {
				initializeSessionNameComboBox(selectedSerialNo);
			}
		});

		System.out.println("Check dfccSNList" + dfccSNList);
		slNoField.setItems(dfccSNList);
	}

	// UUT SERIAL NUMBER FIELD
	private HBox createUUTSerialNoComboBox() {
		slNoField.setPromptText("UUT S/N");
		selectionHBoxUUTSN.setPadding(new Insets(0, 0, 0, 18.5));
		selectionHBoxUUTSN.setAlignment(Pos.CENTER_LEFT);
		selectionHBoxUUTSN.getChildren().add(slNoField);

		return selectionHBoxUUTSN;
	}

	// SESSION FIELD
	private HBox createSessionComboBox() {
		sessionNameField.setPromptText("Session");
		selectionBoxSESSION.setPadding(new Insets(0, 0, 0, 18.5));
		selectionBoxSESSION.setAlignment(Pos.CENTER_LEFT);
		selectionBoxSESSION.getChildren().add(sessionNameField);

		return selectionBoxSESSION;
	}

	private void initializeSessionNameComboBox(String serialNo) {
		sessionNameField.getItems().clear();
		SESSION_ID = null;
		SessionListResponse response = sessionManagement.getAllSessionDataByRoleId(UserData.getRoleId());
		if (response.getResponse().getResponseCode() == 1) {
			sessionDataList = FXCollections.observableArrayList(response.getListOfSession());
			for (SessionList session : sessionDataList) {
				sessionNameList.add(session.getSessionName());
			}
			sessionNameField.setItems(sessionNameList);
			sessionNameField.setOnAction((event) -> {
				SESSION_ID = fetchSessionId(sessionNameField.getValue());
			});
		}
	}

	private void handleClosePopup() {
		Stage stage = (Stage) dataBackupMainContainer.getScene().getWindow();
		stage.close();
	}

	private void handleCopyFolder() {
		if (SESSION_ID == null) {
			Notifications.showWarningAlert("Please Select Session.");
		} else if (selectPathButton.getText().equalsIgnoreCase("Select")) {
			Notifications.showWarningAlert("Please Select Path.");
		} else {
			Response response = sessionFileManagement.backupData(SESSION_ID, selectPathButton.getText());
			if (response.getResponseCode() == 1) {
				handleClosePopup();
			} else {
				Notifications.showErrorAlert(response.getResponseMessage());
			}
		}
	}

}
