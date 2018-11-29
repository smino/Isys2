class ParameterSet {

    private final String SEPERATOR;
    private final int MAX_DATA_POINTS;
    private final int SECTOR_RADIUS;
    private final double GRADIENT_FACTOR;

    ParameterSet(int sectorRadius, String seperator, int maxDataPoints, double gradientFactor) {
        this.SECTOR_RADIUS = sectorRadius;
        this.SEPERATOR = seperator;
        this.MAX_DATA_POINTS = maxDataPoints;
        this.GRADIENT_FACTOR = gradientFactor;
    }

    int getSECTOR_RADIUS() {
        return this.SECTOR_RADIUS;
    }

    String getSEPERATOR() {
        return this.SEPERATOR;
    }

    int getMAX_DATA_POINTS() {
        return this.MAX_DATA_POINTS;
    }

    public double getGRADIENT_FACTOR() {
        return this.GRADIENT_FACTOR;
    }

}
