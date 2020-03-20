package com.example.myapplication;


class Food {
    public String name;
    public String id;
    public int energy;
    public int fiber;
    public Minerals minerals;
    public Vitamins vitamins;

    public Food(String name, int energy, int fiber, Minerals minerals, Vitamins vitamins){
        this.name = name;
        this.energy = energy;
        this.fiber = fiber;
        this.minerals = minerals;
        this.vitamins = vitamins;
    }

}

class Vitamins{
    public int A;
    public int C;
    public int D;
    public int E;
    public int K;
    public int B6;
    public int B12;
}

class Minerals {
    public int iron;
    public int magnesium;
    public int zinc;
    public int calcium;
    public int potassium;
}
