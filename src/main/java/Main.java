import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main extends ApplicationFrame {

    private static final String a0PointsPath = "/home/smino/Documents/Projects/IdeaProjects/ISys-Aufg2/src/main/resources/A0.csv";
    private static final String b0PointsPath = "/home/smino/Documents/Projects/IdeaProjects/ISys-Aufg2/src/main/resources/B0.csv";
    private static final String a1PointsPath = "/home/smino/Documents/Projects/IdeaProjects/ISys-Aufg2/src/main/resources/A1.csv";
    private static final String b1PointsPath = "/home/smino/Documents/Projects/IdeaProjects/ISys-Aufg2/src/main/resources/B1.csv";
    private static final String dataPath = "/home/smino/Documents/Projects/IdeaProjects/ISys-Aufg2/src/main/resources/data.csv";
    private static final String resultPath = "/home/smino/Documents/Projects/IdeaProjects/ISys-Aufg2/src/main/resources/algorithmResults.csv";

    private static List<Point> a0Points;
    private static List<Point> b0Points;
    private static List<Point> a1Points;
    private static List<Point> b1Points;


    private static ParameterSet parameterSet;

    private Main(String applicationTitle, String chartTitle) throws Exception {
        super(applicationTitle);
        DefaultXYDataset data = new DefaultXYDataset();
        double[][] rocPoints = getRocPoints(resultPath);
        data.addSeries("ROC", rocPoints);
        ValueAxis truePositiveAxis = new NumberAxis("False Positive Rate [%]");
        ValueAxis falsePositiveAxis = new NumberAxis("True Positive Rate [%]");
        truePositiveAxis.setRange(new Range(0.0, 100.0));
        falsePositiveAxis.setRange(new Range(0.0, 100.0));
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        XYPlot plot = new XYPlot(data, truePositiveAxis, falsePositiveAxis, renderer);
        JFreeChart chart = new JFreeChart(chartTitle, plot);
        chart.removeLegend();
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(500, 500));
        setContentPane(panel);
    }

    private static double[][] getRocPoints (String resultPath) throws  Exception {
        BufferedReader reader = new BufferedReader(new FileReader(new File(resultPath)));
        reader.readLine();
        String[] lineValues;
        double[][] rocPoints = new double[2][parameterSet.getMAX_DATA_POINTS()];
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            lineValues = line.split(parameterSet.getSEPERATOR());
            for (int i = 0; i < rocPoints[0].length; i++) {
                if (rocPoints[0][i] == 0.0) {
                    rocPoints[0][i] = Double.parseDouble(lineValues[0]) * 100;
                    break;
                }
            }
            for (int i = 0; i < rocPoints[1].length; i++) {
                if (rocPoints[1][i] == 0.0) {
                    rocPoints[1][i] = Double.parseDouble(lineValues[1]) * 100;
                    break;
                }
            }
        }
        reader.close();
        return rocPoints;
    }

    public static void main(String[] args) throws Exception {
        parameterSet = new ParameterSet(20, ",", 100, 0.225);
        if (args.length < 1) {
            System.err.println("Kein korrekter Startparameter angegeben:\n1. 'roc' zum plotten der ROC-Kurve\n2. 'algo' zum ausführen des Algorithmuses");
            System.exit(0);
        } else if (args[0].equals("roc")) {
            plotRoc();
        } else if (args[0].equals("algo")) {
            a0Points = generatePointList(a0PointsPath, PointType.A, parameterSet);
            b0Points = generatePointList(b0PointsPath, PointType.B, parameterSet);
            a1Points = generatePointList(a1PointsPath, PointType.A, parameterSet);
            b1Points = generatePointList(b1PointsPath, PointType.B, parameterSet);
            List<Point> toExecute = new ArrayList<>();
            for (int i = 45; i < 90; i++) {
                toExecute.add(a1Points.get(i));
                toExecute.add(b1Points.get(i));
            }
            runAlgorithm(dataPath, toExecute, parameterSet);
        }
    }

    private static void plotRoc () throws Exception {
        Main roc = new Main("ISys Aufgabe 2", "ROC-Kurve (Datenpunkte)");
        roc.pack();
        RefineryUtilities.centerFrameOnScreen(roc);
        roc.setVisible(true);
    }

    private static void runAlgorithm (String dataPath, List<Point> points, ParameterSet parameterSet) throws Exception {
        List<PointType> typeList = Algorithm.execute(dataPath, points, parameterSet);
        int truePositive = 0, falsePositive = 0, trueNegative = 0, falseNegative = 0;
        if (typeList.size() != points.size()) {
            System.err.println("Listen nicht gleich groß Typenliste: " + typeList.size() + ", Punktliste. " + points.size());
            System.exit(0);
        }
        for (int i = 0; i < typeList.size(); i++) {
            if (typeList.get(i) == PointType.A && points.get(i).getType() == PointType.A) {
                truePositive++;
            } else if (typeList.get(i) == PointType.B && points.get(i).getType() == PointType.A) {
                falseNegative++;
            } else if (typeList.get(i) == PointType.B && points.get(i).getType() == PointType.B) {
                trueNegative++;
            } else if (typeList.get(i) == PointType.A && points.get(i).getType() == PointType.B) {
                falsePositive++;
            } else {
                System.err.println("FEHLER!!!");
                System.exit(0);
            }
        }
        FileWriter writer = new FileWriter(resultPath, true);
        writer.append("\n");
        writer.append(String.format("%.2f", (double) falsePositive / (trueNegative + truePositive)).replaceAll(",", ".")).append(",");
        writer.append(String.format("%.2f", (double) truePositive / (truePositive + falseNegative)).replaceAll(",", ".")).append(",");
        writer.append(String.valueOf(truePositive)).append(",");
        writer.append(String.valueOf(falsePositive)).append(",");
        writer.append(String.valueOf(trueNegative)).append(",");
        writer.append(String.valueOf(falseNegative)).append(",");
        writer.append(String.valueOf(parameterSet.getSECTOR_RADIUS())).append(",");
        writer.append(String.valueOf(parameterSet.getGRADIENT_FACTOR()).replaceAll(",", "."));
        writer.flush();
        writer.close();
    }

    private static List<Point> generatePointList (String path, PointType pointType, ParameterSet parameterSet) throws  Exception {
        List<Point> pointList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
        int x, y;
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            x = Integer.parseInt(line.substring(0, line.indexOf(",")));
            y = Integer.parseInt(line.substring(line.indexOf(",") + 1));
            if (Algorithm.isBetween(parameterSet.getSECTOR_RADIUS() + 1, x, 3000 - (parameterSet.getSECTOR_RADIUS() + 1))) {
                if (Algorithm.isBetween(parameterSet.getSECTOR_RADIUS() + 1, y, 4943 - parameterSet.getSECTOR_RADIUS() + 1)) {
                    pointList.add(new Point(x, y, pointType, new Point[(parameterSet.getSECTOR_RADIUS() * 2) + 1][(parameterSet.getSECTOR_RADIUS() * 2) + 1]));
                }
            }
        }
        return pointList;
    }

}
