package visualization;

import java.awt.Color;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.jdbc.JDBCCategoryDataset;

public class BarChartCompanyPerformance extends JFrame {

  public BarChartCompanyPerformance(Connection conn) {

    initUI(conn);
  }

  private void initUI(Connection conn) {

    CategoryDataset dataset = createDataset(conn);

    JFreeChart chart = createChart(dataset);
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    chartPanel.setBackground(Color.white);
    add(chartPanel);

    pack();
    setTitle("Bar chart");
    setLocationRelativeTo(null);
  }

  private CategoryDataset createDataset(Connection conn) {
    String query = "select c.name, coalesce(sum(fss.sold_seats),0) as sold_seats, coalesce(sum(sf.seats - sold_seats),0) vacant_seats from company c "
        + "left join scheduled_flight sf on sf.company_id = c.cid "
        + "left join flight_sold_seats fss on fss.flight_id = sf.flight_id "
        + "where fss.sold_seats IS NOT NULL and sf.seats IS NOT NULL "
        + "group by c.name;";
    try {
      return new JDBCCategoryDataset(conn,query);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }

  private JFreeChart createChart(CategoryDataset dataset) {

    JFreeChart barChart = ChartFactory.createStackedBarChart(
        "Airline Company-wise Performance",
        "Airline Company",
        "Seats",
        dataset,
        PlotOrientation.VERTICAL,
        false, true, false);

    CategoryPlot plot = barChart.getCategoryPlot();
    NumberAxis range = (NumberAxis) plot.getRangeAxis();
    range.setTickUnit(new NumberTickUnit(100));
    return barChart;
  }

}