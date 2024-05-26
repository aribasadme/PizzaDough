package com.aribasadmetlla.pizzadough;

public class PizzaRecipe {

    private final WeightUnit mWeightUnit;
    private final VolumeUnit mVolumeUnit;
    private int mFlour;
    private float mHydration;
    private float mYeast;
    private int mSalt;
    private int mOil;
    public PizzaRecipe(int flour, float hydration) {
        this.mFlour = flour;
        this.mHydration = hydration;
        this.mWeightUnit = WeightUnit.GRAM;
        this.mVolumeUnit = VolumeUnit.MILLILITRE;

    }

    public int getFlour() {
        return mFlour;
    }

    public void setFlour(int mFlour) {
        this.mFlour = mFlour;
    }

    public float getHydration() {
        return mHydration;
    }

    public void setHydration(float mHydration) {
        this.mHydration = mHydration;
    }

    public float getYeast() {
        return mYeast;
    }

    public void setYeast(float mYeast) {
        this.mYeast = mYeast;
    }

    public int getSalt() {
        return mSalt;
    }

    public void setSalt(int mSalt) {
        this.mSalt = mSalt;
    }

    public int getOil() {
        return mOil;
    }

    public void setOil(int mOil) {
        this.mOil = mOil;
    }

    public double convertWeightUnits(double quantity, WeightUnit unitFrom, WeightUnit unitTo) {
        switch (unitFrom) {
            case GRAM:
                switch (unitTo) {
                    case KILOGRAM:
                        return quantity / 1000;
                    case OUNCE:
                        return quantity * 0.035274;
                    default:
                        return quantity;
                }
            case KILOGRAM:
                switch (unitTo) {
                    case GRAM:
                        return quantity * 1000;
                    case OUNCE:
                        return quantity * 35.274;
                    default:
                        return quantity;
                }
            case OUNCE:
                switch (unitTo) {
                    case GRAM:
                        return quantity * 28.3495;
                    case KILOGRAM:
                        return quantity * 0.0283495;
                    default:
                        return quantity;
                }
            default:
                return quantity;
        }
    }

    public enum WeightUnit {
        GRAM,
        KILOGRAM,
        OUNCE
    }

    public enum VolumeUnit {
        MILLILITRE,
        LITRE,
        FLUID_OUNCE
    }
}
