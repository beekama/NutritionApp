package com.example.nutritionapp;

import com.example.nutritionapp.other.Conversions;
import com.example.nutritionapp.other.Utils;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UtilitiesTest {
    @Test
    public void checkConversion() {
        assert(Conversions.normalize("MG", 1)    == 1000);
        assert(Conversions.normalize("G", 1)     == 1_000_000);
        assert(Conversions.normalize("KCAL", 1)  == 4184);
        assert(Conversions.normalize("UG", 1)    == 1);
        assert(Conversions.normalize("JOULE", 1) == 1);
    }

    @Test(expected = RuntimeException.class)
    public void checkFailedConversion(){
        Conversions.normalize("LOL", 1);
    }

    @Test
    public void checkUtil(){
        assert(Utils.zeroIfNull(null) == 0);
        assert(Utils.zeroIfNull(new Integer(1)) == 1);
    }
}