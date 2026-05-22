package com.teclever.dfcc.Controller.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.dashboard.ChannelValuesDTO;
import com.teclever.dfcc.dashboard.DashboardManagement;
import com.teclever.dfcc.dashboard.TemperatureDashBoardDTO;
import com.teclever.dfcc.stateMachine.StateMachine;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BlsTemperatureController {

    private GridPane mainGrid = new GridPane();
    private LineChart<Number, Number> graph;

    private DashboardManagement dashboardManagement = new DashboardManagement();

    private static final int WINDOW = 750;
    private static final int WIDTH_MULTIPLIER = 1;

    private int timeIndex = 0;

    private XYChart.Series<Number, Number> ch1Series = new XYChart.Series<>();
    private XYChart.Series<Number, Number> ch2Series = new XYChart.Series<>();
    private XYChart.Series<Number, Number> ch3Series = new XYChart.Series<>();
    private XYChart.Series<Number, Number> ch4Series = new XYChart.Series<>();

    public BlsTemperatureController() {
    	initialize();
    }
    
    public void initialize() {
        StateMachine.tempLastCheckedTimeProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Platform.runLater(this::loadHistoricalTemperature);
                StateMachine.setTempUpdate(false);
            }
        });
    }

    public GridPane createBlsTempMainContainerGridPane() {

        mainGrid.getStylesheets().add(
                getClass().getResource(
                        DFCCConstant.JARSTRING +
                        "/com/teclever/dfcc/ui/css/Dashboard.css"
                ).toExternalForm()
        );

        mainGrid.setPadding(new Insets(10));

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(100);

        RowConstraints rowLegend = new RowConstraints();
        rowLegend.setMinHeight(40);

        RowConstraints rowGraph = new RowConstraints();
        rowGraph.setPercentHeight(100);

        mainGrid.getColumnConstraints().add(col);
        mainGrid.getRowConstraints().addAll(rowLegend, rowGraph);

        HBox legendBar = createFixedLegend();
        mainGrid.add(legendBar, 0, 0);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (Iterations)");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(WINDOW);
        xAxis.setTickUnit(10);
        xAxis.setMinorTickCount(0);
        xAxis.setForceZeroInRange(true);

        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        yAxis.setLabel("Temperature (°C)");
        yAxis.setAutoRanging(false);

        graph = new LineChart<>(xAxis, yAxis);
        graph.setTitle("BLS Temperature Graph");
        graph.setAnimated(false);
        graph.setLegendVisible(false);

        // ⚡ Enable symbols so tooltips can work
        graph.setCreateSymbols(true);

        graph.setPrefWidth(WINDOW * 2);
        graph.setMinWidth(WINDOW * 2);

        // Set colors for series
        ch1Series.setName("CH1");
        ch2Series.setName("CH2");
        ch3Series.setName("CH3");
        ch4Series.setName("CH4");

        graph.getData().addAll(ch1Series, ch2Series, ch3Series, ch4Series);

        loadHistoricalTemperature();

        ScrollPane scrollPane = new ScrollPane(graph);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(false);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        mainGrid.add(scrollPane, 0, 1);

        return mainGrid;
    }

	public void loadHistoricalTemperature() {
		// //System.out.println("GRAPH SESSION ID Check::" +
		// StateMachine.currentSessionDetails.getSessionId());

		TemperatureDashBoardDTO dashboardDTO = dashboardManagement
				.getTemparatureBySessionId(StateMachine.currentSessionDetails.getSessionId());

		if (dashboardDTO == null || dashboardDTO.getBlsChannelsValues() == null
				|| dashboardDTO.getBlsChannelsValues().isEmpty()) {
			return;
		}
		
//		//System.out.println("dashboardDTO.getTimestamp();" + dashboardDTO.getTimestamp());

		Platform.runLater(() -> {

			// Clear previous data
			ch1Series.getData().clear();
			ch2Series.getData().clear();
			ch3Series.getData().clear();
			ch4Series.getData().clear();

			timeIndex = 0;

			List<ChannelValuesDTO> valuesList = dashboardDTO.getBlsChannelsValues();
			java.util.Collections.reverse(valuesList);
			if (valuesList.size() > 0) {

				if (graph != null) {
					NumberAxis xAxis = (NumberAxis) graph.getXAxis();

					for (ChannelValuesDTO dto : valuesList) {
						if (dto == null)
							continue;
//						//System.out.println("Check bls time stamp ::" + dto.getTimestamp());
						
						
						

//						addIfValid(ch1Series, dto.getCh1SCValue(), "CH1", sdf.format(dto.getTimestamp()));
//						addIfValid(ch2Series, dto.getCh2SCValue(), "CH2", sdf.format(dto.getTimestamp()));
//						addIfValid(ch3Series, dto.getCh3SCValue(), "CH3", sdf.format(dto.getTimestamp()));
//						addIfValid(ch4Series, dto.getCh4SCValue(), "CH4", sdf.format(dto.getTimestamp()));
						addIfValid(ch1Series, dto.getCh1SCValue(), "CH1", dto.getTimestamp());
						addIfValid(ch2Series, dto.getCh2SCValue(), "CH2", dto.getTimestamp());
						addIfValid(ch3Series, dto.getCh3SCValue(), "CH3", dto.getTimestamp());
						addIfValid(ch4Series, dto.getCh4SCValue(), "CH4", dto.getTimestamp());
						
						timeIndex++;

					}
					// Adjust chart width
					graph.setMinWidth(graph.getPrefWidth());

					if (timeIndex > WINDOW) {
						xAxis.setLowerBound(timeIndex - WINDOW);
						xAxis.setUpperBound(timeIndex);
					}
				}
			}
		});
	}

	private void addIfValid(
	        XYChart.Series<Number, Number> series,
	        double value,
	        String channelName,
	        Date date
	) {
	    if (value == 0.0) return;

	    XYChart.Data<Number, Number> data = new XYChart.Data<>(timeIndex, value);
	    series.getData().add(data);

	    Platform.runLater(() -> {

	        Node node = data.getNode();

	        if (node != null) {

	            node.setPickOnBounds(true);
	            node.setMouseTransparent(false);
	            node.toFront();

	            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	            String formattedTime = (date != null) ? sdf.format(date) : "N/A";

	            Tooltip tooltip = new Tooltip(
	                    channelName +
	                    "\nTemp: " + value + " °C\nTime: " + formattedTime
	            );

	            tooltip.setShowDelay(javafx.util.Duration.ZERO);
	            Tooltip.install(node, tooltip);
	        }
	    });
	}




    private HBox createFixedLegend() {

        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER_LEFT);
        legend.setPadding(new Insets(5, 10, 5, 10));

        legend.getChildren().addAll(
                legendItem("CH1", Color.web("#f44336")),
                legendItem("CH2", Color.web("#ff9800")),
                legendItem("CH3", Color.web("#4caf50")),
                legendItem("CH4", Color.web("#2196f3")),
                legendItem("Temperature Tracking for Analog-2 Module", null)
        );

        return legend;
    }

    private HBox legendItem(String text, Color color) {

        Rectangle rect = color != null ? new Rectangle(15, 15, color) : new Rectangle(0, 0);
        Label label = new Label(text);

        HBox box = new HBox(5, rect, label);
        box.setAlignment(Pos.CENTER_LEFT);

        return box;
    }
}




