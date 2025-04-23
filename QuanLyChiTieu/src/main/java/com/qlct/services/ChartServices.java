package com.qlct.services;

import com.qlct.pojo.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.util.List;


public class ChartServices {

    public void loadPieChart(PieChart chart, List<Expense> expenses) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

        for (Expense expense : expenses) {
            data.add(new PieChart.Data(expense.getCategoryName(), expense.getAmount()));
        }

        chart.setData(data);
        chart.setTitle("Biểu đồ chi tiêu");
        chart.setLabelsVisible(false);
        chart.setLegendVisible(true);
        chart.setMinSize(280, 280);
        chart.setPrefSize(280, 280);

        for (PieChart.Data d : chart.getData()) {
            double percent = (d.getPieValue() / total) * 100;
            d.nameProperty().set(String.format("%s - %.1f%%", d.getName(), percent));
        }
    }

    public void loadBarChart(BarChart<String, Number> chart, List<Expense> expenses) {
        chart.getData().clear();
        chart.setMinSize(600, 300);
        chart.setPrefSize(600, 300);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        String[] colors = {
            "#F44336", "#2196F3", "#4CAF50", "#FFC107", "#9C27B0", "#009688", "#795548"
        };

        int i = 0;
        for (Expense expense : expenses) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(expense.getCategoryName(), expense.getAmount());
            series.getData().add(data);

            final int colorIndex = i % colors.length;
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: " + colors[colorIndex] + ";");
                }
            });

            i++;
        }

        chart.getData().add(series);
    }


    public void loadLineChart(LineChart<String, Number> chart, List<Expense> expenses) {
        chart.getData().clear();
        chart.setTitle("Chi tiêu theo danh mục");
        chart.setPrefSize(850, 350);
        chart.setMinSize(850, 350);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Expense expense : expenses) {
            series.getData().add(new XYChart.Data<>(expense.getCategoryName(), expense.getAmount()));
        }

        chart.getData().add(series);
    }
    
    
}
