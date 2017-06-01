package com.example.dawidmichalowicz.fitshaker;

/**
 * Created by Dawid Michałowicz on 01.06.2017.
 */

public class CaloriesCounter {
    public float countCals(long time, int weight){
        float calories = Math.round((weight*16)*time/(1000*60*60));
        return calories;
    }

}
