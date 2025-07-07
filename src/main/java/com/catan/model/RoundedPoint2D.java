package com.catan.model;

import java.util.Objects;

public class RoundedPoint2D {
    public final double x;
    public final double y;

    public RoundedPoint2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoundedPoint2D)) return false;
        RoundedPoint2D other = (RoundedPoint2D) o;
        // Unterschied bis zu 2 gilt als gleich
        return Math.abs(this.x - other.x) <= 2 && Math.abs(this.y - other.y) <= 2;
    }

    @Override
    public int hashCode() {
        // Hashcode muss konsistent mit equals sein
        // Grobe Gruppierung, da alle innerhalb von 2 gleiche equals haben
        long xBucket = Math.round(this.x / 2);
        long yBucket = Math.round(this.y / 2);
        return Objects.hash(xBucket, yBucket);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}



