package com.catan.model;

import java.util.Objects;

public class RoundedPoint2D {
    public final double x;
    public final double y;

    public RoundedPoint2D(double x, double y) {
        double epsilon = 1e-9;
        this.x = Math.round(x + epsilon);
        this.y = Math.round(y + epsilon);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // gleiche Referenz = gleich
        if (!(o instanceof RoundedPoint2D)) return false;
        RoundedPoint2D other = (RoundedPoint2D) o;
        return Double.compare(this.x, other.x) == 0 && Double.compare(this.y, other.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}


