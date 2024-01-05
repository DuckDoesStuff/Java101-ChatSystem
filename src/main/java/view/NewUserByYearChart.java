package view;

import Database.DB;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.data.category.*;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

import User.UserService;

public class NewUserByYearChart extends JFrame {
    int year;
    UserService userService;

    public NewUserByYearChart(int year, Connection conn) {
        this.year = year;
        userService = new UserService(conn);
        setTitle("Biểu đồ số lượng người đăng ký mới theo năm " + year);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        CategoryDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(560, 370));
        setContentPane(chartPanel);
    }

    private CategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int[] numberOfNewUserByYear = userService.numberOfNewUserByYear(year);
        for (int i = 0; i < 12; i++){
            dataset.addValue(numberOfNewUserByYear[i], "Số lượng", "Tháng " + (i + 1));
        }
        return dataset;
    }

    private JFreeChart createChart(CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Biểu đồ số lượng người đăng ký mới theo năm " + year,
                "Tháng",
                "Số lượng người đăng kí mới",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRenderer().setSeriesPaint(0, new Color(65, 134, 206, 255));
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());

        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        return chart;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DB db = new DB();
            new NewUserByYearChart(2023, db.getConnection());
        });
    }
}
