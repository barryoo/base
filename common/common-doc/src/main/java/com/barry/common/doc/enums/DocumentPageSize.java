package com.barry.common.doc.enums;

/**
 * @author barry chen
 * @date 2023/8/29 15:02
 */
public enum DocumentPageSize {
    _A4(210, 297, 8.268, 11.693, 595, 842),
    _4X6(101.6, 152.4, 4.00, 6.00, 288, 432);
    private final double widthInMillimeters;
    private final double lengthMillimeters;
    private final double widthInInches;
    private final double lengthInInches;
    private final int widthInPixels;
    private final int lengthInPixels;

    DocumentPageSize(double widthInMillimeters, double lengthMillimeters, double widthInInches, double lengthInInches,
                     int widthInPixels, int lengthInPixels) {
        this.widthInMillimeters = widthInMillimeters;
        this.lengthMillimeters = lengthMillimeters;
        this.widthInInches = widthInInches;
        this.lengthInInches = lengthInInches;
        this.widthInPixels = widthInPixels;
        this.lengthInPixels = lengthInPixels;
    }

    public double getWidthInMillimeters() {
        return widthInMillimeters;
    }

    public double getLengthMillimeters() {
        return lengthMillimeters;
    }

    public double getWidthInInches() {
        return widthInInches;
    }

    public double getLengthInInches() {
        return lengthInInches;
    }

    public int getWidthInPixels() {
        return widthInPixels;
    }

    public int getLengthInPixels() {
        return lengthInPixels;
    }
}
