package com.aribasadmetlla.pizzadough;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import android.icu.util.Measure;
import android.icu.util.MeasureUnit;
import org.mockito.Mockito;


public class PizzaDoughUnitTest {

    @Test
    public void testConstructor() {
        // Create a new PizzaDough object
        PizzaDough pizzaDough = new PizzaDough(100, 10, 5, 50, 1.5f);

        // Assert that the properties of the PizzaDough object are set correctly
        assertEquals(100, pizzaDough.getFlour().getNumber().intValue());
        assertEquals(10, pizzaDough.getOil().getNumber().intValue());
        assertEquals(5, pizzaDough.getSalt().getNumber().intValue());
        assertEquals(50, pizzaDough.getWater().getNumber().intValue());
        assertEquals(1.5f, pizzaDough.getYeast().getNumber().floatValue(), 0.01f);
    }

    @Test
    public void testGettersAndSetters() {
        Measure mockWaterMeasure = Mockito.mock(Measure.class);
        Mockito.when(mockWaterMeasure.getNumber()).thenReturn(50f);

        // Create a new PizzaDough object
        PizzaDough pizzaDough = new PizzaDough();

        // Set the properties of the PizzaDough object
        pizzaDough.setFlour(100, MeasureUnit.GRAM);
        pizzaDough.setWater(50, MeasureUnit.MILLILITER);
        pizzaDough.setSalt(5, MeasureUnit.GRAM);
        pizzaDough.setYeast(1.5f, MeasureUnit.GRAM);
        pizzaDough.setOil(10, MeasureUnit.GRAM);

        // Get the properties of the PizzaDough object
        assertEquals(100, pizzaDough.getFlour().getNumber().intValue());
        assertEquals(50, pizzaDough.getWater().getNumber().intValue());
        assertEquals(5, pizzaDough.getSalt().getNumber().intValue());
        assertEquals(1.5f, pizzaDough.getYeast().getNumber().floatValue(), 0.01f);
        assertEquals(10, pizzaDough.getOil().getNumber().intValue());
    }

    @Test
    public void testCalcHydration() {
        // Create a mock Measure object for the water
        Measure mockWaterMeasure = Mockito.mock(Measure.class);
        Mockito.when(mockWaterMeasure.getNumber()).thenReturn(50f);

        // Create a new PizzaDough object
        PizzaDough pizzaDough = new PizzaDough(100, 0, 0, 50, 0f);

        // Calculate the hydration of the PizzaDough object
        float hydration = pizzaDough.calcHydration();

        // Assert that the hydration is calculated correctly
        assertEquals(50f, hydration, 0.01f);
    }

    @Test
    public void testConvertToPounds() {
        // Create a new PizzaDough object
        PizzaDough pizzaDough = new PizzaDough(100, 10, 5, 50, 1.5f);

        // Convert the weight of the PizzaDough object to pounds
        pizzaDough.convertToPounds();

        // Assert that the weight is converted correctly
        assertEquals(0.367069667, pizzaDough.getWeight().getNumber().doubleValue(), 0.000001);
    }

    @Test
    public void testConvertToGrams() {
        // Create a new PizzaDough object
        PizzaDough pizzaDough = new PizzaDough(100, 10, 5, 50, 1.5f);

        // Convert the weight of the PizzaDough object to grams
        pizzaDough.convertToGrams();

        // Assert that the weight is converted correctly
        assertEquals(75523.1296, pizzaDough.getWeight().getNumber().doubleValue(), 0.00001);
    }
}