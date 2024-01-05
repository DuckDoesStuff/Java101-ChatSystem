package AdminUI.MainView;

        import Database.DB;
        import org.jfree.chart.*;
        import org.jfree.chart.axis.*;
        import org.jfree.chart.plot.*;
        import org.jfree.chart.renderer.category.*;
        import org.jfree.data.category.*;

        import javax.swing.*;
        import java.awt.*;
        import java.awt.event.ActionEvent;
        import java.awt.event.ActionListener;
        import java.sql.Connection;

        import User.UserService;

public class ActiveUsersByYearChart extends JFrame {
    UserService userService;

    public ActiveUsersByYearChart(Connection conn) {
        userService = new UserService(conn);
        setTitle("Chart of number of active people by year");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setVisible(true);

        JTextField yearTextField = new JTextField(10);
        JButton drawChart = new JButton("View Chart");
        drawChart.setBackground(new Color(117, 184, 190));
        JButton back = new JButton("Back to menu");
        drawChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String yearString = yearTextField.getText();
                try {
                    int year = Integer.parseInt(yearString);
                    CategoryDataset dataset = createDataset(year);
                    JFreeChart chart = createChart(dataset, year);
                    ChartPanel chartPanel = new ChartPanel(chart);
                    chartPanel.setPreferredSize(new Dimension(800, 600));
                    setContentPane(chartPanel);
                    pack();
                    repaint();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid year.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter year: "));
        inputPanel.add(yearTextField);
        inputPanel.add(drawChart);
        inputPanel.add(back);

        // Đặt JPanel vào JFrame
        add(inputPanel, BorderLayout.NORTH);
    }

    private CategoryDataset createDataset(int year) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int[] numberOfActiveUserByYear = userService.numberOfActiveUserByYear(year);
        for (int i = 0; i < 12; i++){
            dataset.addValue(numberOfActiveUserByYear[i], "Quantity", "Month " + (i + 1));
        }
        return dataset;
    }

    private JFreeChart createChart(CategoryDataset dataset, int year) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Chart of number of active people by year " + year,
                "Month",
                "Number of active people",
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
            new ActiveUsersByYearChart(db.getConnection());
        });
    }
}
