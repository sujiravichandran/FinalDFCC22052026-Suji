package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.stateMachine.StateMachine;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class BlsMainContainer {

    private final GridPane blsMainContainerGridPane = new GridPane();
    private final BlsTemperatureController blsTemperatureController =
            new BlsTemperatureController();

    private final Button blsBuildConfigurationButton =
            new Button("BLS Build Config");

    private Stage popupStage;

    public GridPane createBlsMainContainerGridPane() {

        blsMainContainerGridPane.getStylesheets().add(
                getClass().getResource(
                        DFCCConstant.JARSTRING +
                        "/com/teclever/dfcc/ui/css/ManualTesting.css"
                ).toExternalForm()
        );
        blsMainContainerGridPane.getStyleClass()
                .add("dashboard-main-container");

        blsMainContainerGridPane.getColumnConstraints().clear();
        blsMainContainerGridPane.getRowConstraints().clear();
        blsMainContainerGridPane.getChildren().clear();

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(100);

        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(50);

        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(50);

        blsMainContainerGridPane.getColumnConstraints().add(col);
        blsMainContainerGridPane.getRowConstraints().addAll(row1, row2);

        blsMainContainerGridPane.add(createDashBoardBuildButtonGridPane(), 0, 0);
        blsMainContainerGridPane.add(
                blsTemperatureController.createBlsTempMainContainerGridPane(),
                0, 1
        );

        return blsMainContainerGridPane;
    }

    private GridPane createDashBoardBuildButtonGridPane() {

        GridPane grid = new GridPane();

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(100);

        RowConstraints row = new RowConstraints();
        row.setPercentHeight(100);
        
        

        grid.getColumnConstraints().add(col);
        grid.getRowConstraints().add(row);
        grid.getStyleClass().add("selfTest-top-container");

        HBox hbox = new HBox(blsBuildConfigurationButton);
        grid.add(hbox, 0, 0);

        blsBuildConfigurationButton.setOnAction(e -> 
        
        openPopup());

        return grid;
    }

    private void openPopup() {
    	 StateMachine.setDashboardBuildConfig(true);
        if (popupStage != null && popupStage.isShowing()) {
            popupStage.toFront();
            return;
        }

        // ✅ NEW controller EVERY TIME
        BuildConfigurationController controller =
                new BuildConfigurationController();

        GridPane root =
                controller.createBuildConfigurationMainContainerGridPane();
        popupStage = new Stage();
        popupStage.setTitle("Build Configuration");
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setMaximized(true);
        popupStage.setScene(new Scene(root));
        popupStage.initStyle(StageStyle.UNDECORATED);

        popupStage.setOnHidden(e -> popupStage = null);
        popupStage.show();
    }
}
