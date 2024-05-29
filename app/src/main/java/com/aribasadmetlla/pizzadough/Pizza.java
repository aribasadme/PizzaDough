package com.aribasadmetlla.pizzadough;

public class Pizza {

    private PizzaDough dough;
    private PizzaRecipe recipe;

    public Pizza(PizzaDough dough, PizzaRecipe recipe) {
        this.dough = dough;
        this.recipe = recipe;
    }

    public PizzaDough getDough() {
        return dough;
    }

    public PizzaRecipe getRecipe() {
        return recipe;
    }
}
