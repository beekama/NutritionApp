package com.example.nutritionapp.deprecated;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Database;

public class updateAge extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deprecated_update_age);

        /* get database connection */
        final Database db = new Database(this);

        /* set current values */
        final TextView tv = (TextView) findViewById(R.id.updateAge_et);
        Integer age = db.getPersonAge();
        if(age != -1){
            tv.setText(age.toString());
        }

        //cancel update age:
        final ImageButton backHome = (ImageButton) findViewById(R.id.updateAge_ib_backToHome);
        backHome.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));

        /* submit new age */
        final ImageButton submit = (ImageButton) findViewById(R.id.updateAge_submit);
        submit.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* get setting from text view */
                int age = Integer.parseInt(tv.getText().toString());

                try {

                    /* write to database class */
                    db.setPersonAge(age);

                    /* return to previous activity */
                    finish();

                }catch(IllegalArgumentException e){

                    /* show a notification */
                    Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                    toast.show();

                }
            }
        }));

    }}
