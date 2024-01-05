package AdminUI.MainView;

import Database.DB;
import User.UserService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class NewUserByYearChart {
    private JFrame frame;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private UserService userService;
    private int year;
    private DefaultCategoryDataset dataset;
    private JFreeChart chart;
    public static void main(String[] args) {
        new NewUserByYearChart((new DB()).getConnection());
    }

    public NewUserByYearChart(Connection conn) {
        userService = new UserService(conn);
        frame = new JFrame("Chart of new registers by year");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createInputPanel(), "inputPanel");
        cardPanel.add(createChartPanel(), "chartPanel");

        cardLayout.show(cardPanel, "inputPanel");

        frame.getContentPane().add(cardPanel);

        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        JTextField yearTextField = new JTextField(10);
        JButton viewChart_btn = new JButton("View Chart");
        JButton back_btn = new JButton("Close");
        back_btn.setBackground(new Color(171, 195, 234));
        back_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        viewChart_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String yearString = yearTextField.getText();
                try {
                    int year = Integer.parseInt(yearString);
                    updateChartPanel(year);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid year.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        back_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Quay về menu chính
            }
        });

        inputPanel.add(new JLabel("Enter year: "));
        inputPanel.add(yearTextField);
        inputPanel.add(viewChart_btn);
        inputPanel.add(back_btn);

        return inputPanel;
    }
    private JPanel createChartPanel() {
        JPanel chartPanel = new JPanel();
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "inputPanel");
            }
        });

        dataset = new DefaultCategoryDataset();

        chart = ChartFactory.createBarChart(
                "Chart of new registers by year " + this.year,
                "Month",
                "Number of new registers",
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

        ChartPanel chartPanelComponent = new ChartPanel(chart);
        chartPanelComponent.setPreferredSize(new Dimension(800, 520));
        chartPanel.add(chartPanelComponent);
        chartPanel.add(backButton);
        return chartPanel;
    }

    private void updateChartPanel(int year) {
        this.year = year;
        int[] numberOfActiveUserByYear = userService.numberOfNewUserByYear(year);
        for (int i = 0; i < 12; i++){
            dataset.addValue(numberOfActiveUserByYear[i], "Quantity", "Month " + (i + 1));
        }
        chart.setTitle("Chart of new registers by year " + this.year);
        cardLayout.show(cardPanel, "chartPanel");
    }
}
