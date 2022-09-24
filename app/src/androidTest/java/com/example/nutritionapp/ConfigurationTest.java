package com.example.nutritionapp;

import android.app.Application;

import androidx.test.core.app.ActivityScenario;

import com.example.nutritionapp.other.Database;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigurationTest {

    @Before
    public void purgeDB(){
        ActivityScenario.launch(MainActivity.class).onActivity(activity -> {
            Database db = new Database(activity);
            Application app = activity.getApplication();
            db.purgeDatabase();
        });
    }

    @Test
    public void config_01_testDbGettersAndSetters(){
        ActivityScenario.launch(MainActivity.class).onActivity(activity -> {
            Database db = new Database(activity);

            int age = 18;
            int energyReq = 2000;
            String male = "male";
            String female = "female";
            int height = 180;
            int weight = 100;

            db.setPersonAge(age);
            db.setPersonEnergyReq(energyReq, null);
            db.setPersonGender(male);
            db.setPersonGender(female);
            db.setPersonHeight(height);
            db.setPersonWeight(weight);

            assertEquals("Person age get did not match set value", age, db.getPersonAge());
            assertEquals("Person energy requirements get did not match set value", energyReq, db.getPersonEnergyReq(null));
            assertEquals("Person Gender get did not match set value", female, db.getPersonGender());
            assertEquals("Person height get did not match set value", height, db.getPersonHeight());
            assertEquals("Person weight get did not match set value", weight, db.getPersonWeight());

        });
    }

    @Test
    public void config_01_testBmiCalculation(){
        ActivityScenario.launch(MainActivity.class).onActivity(activity -> {

            Database db = new Database(activity);

            int age = 18;
            int height = 180;
            int weight = 100;

            db.setPersonAge(age);
            db.setPersonHeight(height);
            db.setPersonWeight(weight);

            assertTrue("BMI not expected range (was: " + db.getPersonBmi() + " )", 15 < db.getPersonBmi() && db.getPersonBmi() < 35);

        });
    }


    @Test
    public void config_01_testEnergyReqGetterCalculator(){
        ActivityScenario.launch(MainActivity.class).onActivity(activity -> {

            Database db = new Database(activity);

            int age = 18;
            String male = "male";
            String female = "female";
            int height = 180;
            int weight = 100;

            db.setPersonAge(age);
            db.setPersonGender(male);
            db.setPersonHeight(height);
            db.setPersonWeight(weight);

            int energyReqMale = db.getPersonEnergyReq(null);
            db.setPersonGender(female);
            int energyReqFemale = db.getPersonEnergyReq(null);

            assertTrue("EnergyReq (male) seems off the chars", energyReqMale > 1000 && energyReqMale < 3000);
            assertTrue("EnergyReq (female) seems off the chars", energyReqFemale > 1000 && energyReqFemale < 3000);
            //assertNotEquals("Woman and male energy requirements shouldn't be the same", energyReqMale, energyReqFemale);

        });
    }
}
