package com.teclever.dfcc.Controller.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.itextpdf.io.util.SystemUtil;
import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ViewReportController {

    @FXML
    private Button cancelButton;

    @FXML
    private Pane endremarks;

    @FXML
    private Label headinglbl;

    @FXML
    private Label labelEndRemarks;

    @FXML
    private TextArea reportPath;

    @FXML
    private Button viewButton;
    
    private Response response;
    
	@FXML
	public void initialize() {
		viewButton.setOnAction(e -> ViewReport(response));
		cancelButton.setOnAction(e -> handleCancelButtonAction());
		
	
	}
    
    
    public void viewReportPopup(Response response) {
		try {
			FXMLLoader viewReportPopup = new FXMLLoader(
					getClass().getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/fxml/ViewReport.fxml"));
			Parent root = viewReportPopup.load();
			
			 ViewReportController controller = viewReportPopup.getController();
		        controller.setResponse(response);
		        
			
			Stage stage = new Stage();
			stage.setTitle("Edit User");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UNDECORATED);

			stage.setScene(new Scene(root));
			stage.showAndWait();


		} catch (IOException e) {
			e.printStackTrace();
		}

	}
    
    public void setResponse(Response response) {
        this.response = response;
        if (response != null) {
        	reportPath.setText(response.getDownloadPath());
            System.out.println("ViewReportController received response:");
            System.out.println("Download Path: " + response.getDownloadPath());
        } else {
            System.out.println("ViewReportController: Received null response!");
        }
    }
    
    private void ViewReport(Response response) {
        if (response == null || response.getDownloadPath() == null || response.getDownloadPath().isEmpty()) {
            System.out.println("No file path available.");
            return;
        }
        System.out.println("PATHE FOR PDF" +response.getDownloadPath() );
        File file = new File(response.getDownloadPath());
        if (!file.exists()) {
            System.out.println("File not found.");
            return;
        }

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(file);
            } else {
                // Fallback for Linux
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("linux")) {
                	System.out.println("Entred Linux Condition");
                    new ProcessBuilder("xdg-open", file.getAbsolutePath()).start();
                } else {
                    System.out.println("Opening files is not supported on this OS.");
                }
            }

            Stage stage = (Stage) viewButton.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    @FXML
	private void handleCancelButtonAction() {
		Stage stage = (Stage) cancelButton.getScene().getWindow();
		stage.close();

	}
    
    
    
    
    

}
