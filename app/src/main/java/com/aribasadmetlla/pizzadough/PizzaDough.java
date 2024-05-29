package com.aribasadmetlla.pizzadough;

import android.icu.util.Measure;
import android.icu.util.MeasureUnit;

import static android.icu.util.MeasureUnit.GRAM;
import static android.icu.util.MeasureUnit.POUND;
import static android.icu.util.MeasureUnit.MILLILITER;

public class PizzaDough {
    private Measure flour, oil, salt, water, yeast;
    private Measure weight;

    public PizzaDough(int flour, int oil, int salt, int water, float yeast) {
        this.flour = new Measure(flour, GRAM);
        this.water = new Measure(water, MILLILITER);
        this.salt = new Measure(salt, GRAM);
        this.yeast = new Measure(yeast, GRAM);
        this.oil = new Measure(oil, GRAM);
        this.weight = new Measure(flour + water + salt + oil + yeast, GRAM);
    }

    public Measure getFlour() {
        return flour;
    }

    public void setFlour(int flour, MeasureUnit unit) {
        this.flour = new Measure(flour, unit);
    }

    public Measure getWater() {
        return water;
    }

    public void setWater(int water, MeasureUnit unit) {
        this.water = new Measure(water, unit);
    }

    public Measure getSalt() {
        return salt;
    }

    public void setSalt(int salt, MeasureUnit unit) {
        this.salt = new Measure(salt, unit);
    }

    public Measure getYeast() {
        return yeast;
    }

    public void setYeast(float yeast, MeasureUnit unit) {
        this.yeast = new Measure(yeast, unit);
    }

    public Measure getOil() {
        return oil;
    }

    public void setOil(int oil, MeasureUnit unit) {
        this.oil = new Measure(oil, unit);
    }

    public Measure getWeight() { return weight; }

    public float calcHydration() {
        return ((float) water.getNumber().intValue() / flour.getNumber().intValue()) * 100;
    }

    public void convertToPounds() {
        if (weight.getUnit() != POUND && weight.getUnit() == GRAM) {
            this.weight = new Measure(weight.getNumber().doubleValue() * 0.002204623, POUND);
        }
    }

    public void convertToGrams() {
        if (weight.getUnit() != GRAM && weight.getUnit() == POUND) {
            this.weight = new Measure(weight.getNumber().doubleValue() * 453.59237, GRAM);
        }
    }
}
