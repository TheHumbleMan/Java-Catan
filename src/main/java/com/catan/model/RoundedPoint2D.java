package com.catan.model;

import java.util.Objects;


public class RoundedPoint2D {
    public final double x;
    public final double y;
    private static final double EPSILON = 0.01; // Toleranz 1/100 pixel

    public RoundedPoint2D(double x, double y) {
        this.x = Math.round(x / EPSILON) * EPSILON;
        this.y = Math.round(y / EPSILON) * EPSILON;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RoundedPoint2D)) return false;
        RoundedPoint2D other = (RoundedPoint2D) o;
        return Math.abs(this.x - other.x) < EPSILON &&
               Math.abs(this.y - other.y) < EPSILON;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Math.round(x / EPSILON), Math.round(y / EPSILON));
    }
}
