package com.aribasadmetlla.pizzadough;

public class PizzaDough {
    private int mFlour;
    private int mWater;
    private int mSalt;
    private float mYeast;
    private int mOil;
    private WeightUnit mWeightUnit;
    private VolumeUnit mVolumeUnit;
    public PizzaDough(int flour, int water, int salt, float yeast, int oil) {
        this.mFlour = flour;
        this.mWater = water;
        this.mSalt = salt;
        this.mYeast = yeast;
        this.mOil = oil;
        this.mWeightUnit = WeightUnit.GRAM;
        this.mVolumeUnit = VolumeUnit.MILLILITRE;

    }
    public PizzaDough(int flour, int water, int salt, float yeast) {
        this(flour, water, salt, yeast, 0);
    }

    public int getFlour() {
        return mFlour;
    }

    public void setFlour(int mFlour) {
        this.mFlour = mFlour;
    }

    public int getWater() {
        return mWater;
    }

    public void setWater(int mWater) {
        this.mWater = mWater;
    }

    public int getSalt() {
        return mSalt;
    }

    public void setSalt(int mSalt) {
        this.mSalt = mSalt;
    }

    public float getYeast() {
        return mYeast;
    }

    public void setYeast(float mYeast) {
        this.mYeast = mYeast;
    }

    public int getOil() {
        return mOil;
    }

    public void setOil(int mOil) {
        this.mOil = mOil;
    }

    public WeightUnit getWeightUnit() {
        return mWeightUnit;
    }

    public void setWeightUnit(WeightUnit weightUnit) {
        this.mWeightUnit = weightUnit;
    }

    public VolumeUnit getVolumeUnit() {
        return mVolumeUnit;
    }

    public void setVolumeUnit(VolumeUnit volumeUnit) {
        this.mVolumeUnit = volumeUnit;
    }

    public int calcDoughWeight() {
        return mFlour + mWater;
    }

    public float calcHydration() {
        return ((float) mWater / mFlour) * 100;
    }

    public enum WeightUnit {
        GRAM,
        OUNCE
    }

    public enum VolumeUnit {
        MILLILITRE,
        FLUID_OUNCE
    }
}
