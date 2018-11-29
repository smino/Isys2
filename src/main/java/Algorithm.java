import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

class Algorithm {

    static List<PointType> execute(String dataPath, List<Point> points, ParameterSet parameterSet) throws Exception {
        List<PointType> calculatedTypes = new ArrayList<>();
        List<Point> sectoredPoints = getSectors(dataPath, points, parameterSet);
        for (Point center : sectoredPoints) {
            if (isB(center.getSector(), parameterSet)) {
                calculatedTypes.add(PointType.B);
            } else {
                calculatedTypes.add(PointType.A);
            }
        }
        return calculatedTypes;
    }

    private static boolean isB(Point[][] sector, ParameterSet parameterSet) {
        List<List<Point>> lanes = getLanes(sector, parameterSet);
        List<Double> gradients = new ArrayList<>();
        for (List<Point> lane : lanes) {
            gradients.add(getAverage(getGradients(lane)));
        }
        if (Collections.min(gradients) < getAverage(gradients) * parameterSet.getGRADIENT_FACTOR()) {
            return true;
        }
        return false;
    }

    private static List<Point> swapList(List<Point> toSwap) {
        List<Point> swappedList = new ArrayList<>();
        for (int i = toSwap.size() - 1; i >= 0; i--) {
            swappedList.add(toSwap.get(i));
        }
        return swappedList;
    }

    private static List<Double> getGradients(List<Point> lane) {
        List<Double> gradients = new ArrayList<>();
        double gradient;
        Point second;
        for (Point first : lane) {
            if (lane.indexOf(first) != lane.size() - 1) {
                second = lane.get(lane.indexOf(first) + 1);
                gradient = (first.getValue() - second.getValue()) / (Math.sqrt(Math.pow(first.getX() - second.getX(), 2) + Math.pow(first.getY() - second.getY(), 2)));
                gradients.add(gradient);
            }
        }
        return gradients;
    }

    private static double getAverage(List<Double> values) {
        double counter = 0;
        for (Double value : values) {
            counter += value;
        }
        return counter / values.size();
    }

    private static List<List<Point>> getLanes(Point[][] sector, ParameterSet parameterSet) {
        List<List<Point>> lanes = new ArrayList<>();
        Point center = sector[parameterSet.getSECTOR_RADIUS()][parameterSet.getSECTOR_RADIUS()];
        List<Point> lane;

        //0° Nord
        lane = new LinkedList<>();
        outerloop:
        for (int i = 0; i < sector.length; i++) {
            for (int j = 0; j < sector[i].length; j++) {
                if (sector[i][j] != null) {
                    if (sector[i][j].getX() == center.getX() && sector[i][j].getY() < center.getY()) {
                        lane.add(sector[i][j]);
                        if (lane.size() == parameterSet.getSECTOR_RADIUS()) {
                            break outerloop;
                        }
                    }
                }
            }
        }
        if (lane.size() == parameterSet.getSECTOR_RADIUS()) {
            lanes.add(swapList(lane));
        }


        //45° Nord-Ost
        lane = new LinkedList<>();
        outerloop:
        for (int i = 1; i <= parameterSet.getSECTOR_RADIUS(); i++) {
            for (int j = 0; j < sector.length; j++) {
                for (int k = 0; k < sector[j].length; k++) {
                    if (sector[i][j] != null) {
                        if (sector[j][k].getX() == center.getX() + i && sector[j][k].getY() == center.getY() - i) {
                            lane.add(sector[j][k]);
                            if (lane.size() == parameterSet.getSECTOR_RADIUS()) {
                                break outerloop;
                            }
                        }
                    }
                }
            }
        }
        if (lane.size() == parameterSet.getSECTOR_RADIUS()) {
            lanes.add(lane);
        }

        //90° Ost
        lane = new LinkedList<>();
        outerloop:
        for (int i = 0; i < sector.length; i++) {
            for (int j = 0; j < sector[i].length; j++) {
                if (sector[i][j] != null) {
                    if (sector[i][j].getY() == center.getY() && sector[i][j].getX() > center.getX()) {
                        lane.add(sector[i][j]);
                        if (lane.size() == parameterSet.getSECTOR_RADIUS()) {
                            break outerloop;
                        }
                    }
                }
            }
        }
        if (lane.size() == parameterSet.getSECTOR_RADIUS()) {
            lanes.add(lane);
        }

        //135° Süd-Ost
        lane = new LinkedList<>();
        outerloop:
        for (int i = 1; i <= parameterSet.getSECTOR_RADIUS(); i++) {
            for (int j = 0; j < sector.length; j++) {
                for (int k = 0; k < sector[j].length; k++) {
                    if (sector[i][j] != null) {
                        if (sector[j][k].getX() == center.getX() + i && sector[j][k].getY() == center.getY() + i) {
                            lane.add(sector[j][k]);
                            if (lane.size() == parameterSet.getSECTOR_RADIUS()) {
                                break outerloop;
                            }
                        }
                    }
                }
            }
        }
        if (lane.size() == parameterSet.getSECTOR_RADIUS()) {
            lanes.add(lane);
        }

        //180° Süd
        lane = new LinkedList<>();
        outerloop:
        for (int i = 0; i < sector.length; i++) {
            for (int j = 0; j < sector[i].length; j++) {
                if (sector[i][j] != null) {
                    if (sector[i][j].getX() == center.getX() && sector[i][j].getY() > center.getY()) {
                        lane.add(sector[i][j]);
                        if (lane.size() == parameterSet.getSECTOR_RADIUS()) {
                            break outerloop;
                        }
                    }
                }
            }
        }
        if (lane.size() == parameterSet.getSECTOR_RADIUS()) {
            lanes.add(lane);
        }

        //225° Süd-West
        lane = new LinkedList<>();
        outerloop:
        for (int i = 1; i <= parameterSet.getSECTOR_RADIUS(); i++) {
            for (int j = 0; j < sector.length; j++) {
                for (int k = 0; k < sector[j].length; k++) {
                    if (sector[i][j] != null) {
                        if (sector[j][k].getX() == center.getX() - i && sector[j][k].getY() == center.getY() + i) {
                            lane.add(sector[j][k]);
                            if (lane.size() == parameterSet.getSECTOR_RADIUS()) {
                                break outerloop;
                            }
                        }
                    }
                }
            }
        }
        if (lane.size() == parameterSet.getSECTOR_RADIUS()) {
            lanes.add(lane);
        }

        //270° West
        lane = new LinkedList<>();
        outerloop:
        for (int i = 0; i < sector.length; i++) {
            for (int j = 0; j < sector[i].length; j++) {
                if (sector[i][j] != null) {
                    if (sector[i][j].getY() == center.getY() && sector[i][j].getX() < center.getX()) {
                        lane.add(sector[i][j]);
                        if (lane.size() == parameterSet.getSECTOR_RADIUS()) {
                            break outerloop;
                        }
                    }
                }
            }
        }
        if (lane.size() == parameterSet.getSECTOR_RADIUS()) {
            lanes.add(swapList(lane));
        }

        //315° Nord-West
        lane = new LinkedList<>();
        outerloop:
        for (int i = 1; i <= parameterSet.getSECTOR_RADIUS(); i++) {
            for (int j = 0; j < sector.length; j++) {
                for (int k = 0; k < sector[j].length; k++) {
                    if (sector[i][j] != null) {
                        if (sector[j][k].getX() == center.getX() - i && sector[j][k].getY() == center.getY() - i) {
                            lane.add(sector[j][k]);
                            if (lane.size() == parameterSet.getSECTOR_RADIUS()) {
                                break outerloop;
                            }
                        }
                    }
                }
            }
        }
        if (lane.size() == parameterSet.getSECTOR_RADIUS()) {
            lanes.add(lane);
        }

        return lanes;
    }

    private static List<Point> getSectors(String dataPath, List<Point> centers, ParameterSet parameterSet) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(new File(dataPath)));
        String line;
        String[] lineValues;
        int column, row = 0;
        while ((line = reader.readLine()) != null) {
            lineValues = line.split(parameterSet.getSEPERATOR());
            column = 0;
            row++;
            for (String value : lineValues) {
                column++;
                for (Point center : centers) {
                    if (isBetween(center.getX() - parameterSet.getSECTOR_RADIUS(), column, center.getX() + parameterSet.getSECTOR_RADIUS())) {
                        if (isBetween(center.getY() - parameterSet.getSECTOR_RADIUS(), row, center.getY() + parameterSet.getSECTOR_RADIUS())) {
                            outerloop:
                            for (int i = 0; i < center.getSector().length; i++) {
                                for (int j = 0; j < center.getSector()[i].length; j++) {
                                    if (center.getSector()[i][j] == null) {

                                        if (i == center.getSector().length - 1 && j == center.getSector()[i].length - 1) {
                                            System.out.println("(" + column + ", " + row + ")");
                                        }

                                        center.setSectorValue(i, j, new Point(column, row, Double.parseDouble(value)));
                                        break outerloop;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return centers;
    }

    public static boolean isBetween(int a, int b, int c) {
        return a <= b && b <= c;
    }

}
