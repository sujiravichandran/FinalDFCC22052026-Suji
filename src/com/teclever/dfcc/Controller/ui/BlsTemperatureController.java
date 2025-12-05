package com.teclever.dfcc.Controller.ui;

import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.stateMachine.StateMachine;

import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class BlsTemperatureController {

    private GridPane blsTempGraphMainContainerGridPane = new GridPane();
    private LineChart<Number, Number> graph;

    private final int WINDOW_SIZE = 50;
    private final int WINDOW = 500;// visible window
    private final int WIDTH_MULTIPLIER = 5;   // graph width grows 5x
    private int timeIndex = 0;

    private XYChart.Series<Number, Number> ch1Series = new XYChart.Series<>();
    private XYChart.Series<Number, Number> ch2Series = new XYChart.Series<>();
    private XYChart.Series<Number, Number> ch3Series = new XYChart.Series<>();
    private XYChart.Series<Number, Number> ch4Series = new XYChart.Series<>();


    public GridPane createBlsTempMainContainerGridPane() {

        blsTempGraphMainContainerGridPane.getStylesheets().add(getClass()
                .getResource(DFCCConstant.JARSTRING + "/com/teclever/dfcc/ui/css/Dashboard.css").toExternalForm());
        blsTempGraphMainContainerGridPane.getStyleClass().add("dashboard-main-container");

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(100);

        RowConstraints row = new RowConstraints();
        row.setPercentHeight(100);

        blsTempGraphMainContainerGridPane.setPadding(new Insets(10));
        blsTempGraphMainContainerGridPane.getColumnConstraints().add(col);
        blsTempGraphMainContainerGridPane.getRowConstraints().add(row);

        // X-Axis (wide)
        NumberAxis xAxis = new NumberAxis(0, WINDOW_SIZE * WIDTH_MULTIPLIER, 10);
        xAxis.setLabel("Time (Updates)");

        // Y-Axis (0 - 110°C)
        NumberAxis yAxis = new NumberAxis(0, 110, 10);
        yAxis.setLabel("Temperature (°C)");
        yAxis.setAutoRanging(false);

        graph = new LineChart<>(xAxis, yAxis);
        graph.setTitle("BLS Temperature Graph");

        // Make graph very wide (initial)
        graph.setPrefWidth(WINDOW_SIZE * WIDTH_MULTIPLIER * 10);
        graph.setMinWidth(WINDOW_SIZE * WIDTH_MULTIPLIER * 10);

        // Series names
        ch1Series.setName("CH1");
        ch2Series.setName("CH2");
        ch3Series.setName("CH3");
        ch4Series.setName("CH4");

        graph.getData().addAll(ch1Series, ch2Series, ch3Series, ch4Series);

        // Attach listeners
        attachListeners();

        // Wrap graph inside ScrollPane for horizontal scrolling
        ScrollPane scrollPane = new ScrollPane(graph);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(false);
        scrollPane.setPannable(true);  // allows dragging the graph left/right
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        blsTempGraphMainContainerGridPane.add(scrollPane, 0, 0);

        return blsTempGraphMainContainerGridPane;
    }


    private void attachListeners() {

        StateMachine.channel1BlsTemperatureProperty().addListener((obs, oldVal, newVal) ->
                addPoint(ch1Series, newVal));

        StateMachine.channel2BlsTemperatureProperty().addListener((obs, oldVal, newVal) ->
                addPoint(ch2Series, newVal));

        StateMachine.channel3BlsTemperatureProperty().addListener((obs, oldVal, newVal) ->
                addPoint(ch3Series, newVal));

        StateMachine.channel4BlsTemperatureProperty().addListener((obs, oldVal, newVal) ->
                addPoint(ch4Series, newVal));
    }


    private void addPoint(XYChart.Series<Number, Number> series, String value) {
        try {
            double temp = Double.parseDouble(value);

            // Add new point
            series.getData().add(new XYChart.Data<>(timeIndex, temp));

            NumberAxis xAxis = (NumberAxis) graph.getXAxis();

            // Dynamic graph width grows as timeIndex increases
            graph.setPrefWidth(Math.max(graph.getPrefWidth(), timeIndex * 10));

            // Always show last WINDOW range (sliding window)
            if (timeIndex >= WINDOW) {
                xAxis.setLowerBound(timeIndex - WINDOW);
                xAxis.setUpperBound(timeIndex);
            }

            // Keep dataset pruning low
            if (series.getData().size() > 20000) {
                series.getData().remove(0, 15000);
            }

            timeIndex++;

        } catch (Exception e) {
            System.out.println("Invalid Temperature: " + value);
        }
    }




}
