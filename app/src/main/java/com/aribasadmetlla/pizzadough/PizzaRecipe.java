package com.aribasadmetlla.pizzadough;

public class PizzaRecipe {
        private String name;
        private int numPortions;

        public PizzaRecipe(String name, int numPortions) {
            this.name = name;
            this.numPortions = numPortions;
        }

            public String getName() {
            return name;
        }

            public int getNumPortions() {
            return numPortions;
        }
    }
