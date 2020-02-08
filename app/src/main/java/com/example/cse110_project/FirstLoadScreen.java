package com.example.cse110_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cse110_project.fitness.FitnessService;
import com.example.cse110_project.fitness.FitnessServiceFactory;
import com.example.cse110_project.fitness.GoogleFitAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FirstLoadScreen extends AppCompatActivity {

    private Button getStartedBtn;
    private EditText firstName;
    private EditText lastName;
    private EditText heightFt;
    private EditText heightInch;
    private String fitnessServiceKey = "GOOGLE_FIT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FitnessServiceFactory.put(fitnessServiceKey, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(HomeScreen stepCountActivity) {
                return new GoogleFitAdapter(stepCountActivity);
            }
        });

        SharedPreferences sharedpreference_value = getSharedPreferences("user_info",MODE_PRIVATE);
        String name = sharedpreference_value.getString("firstname", "");

        if(name.length() == 0) {
            Toast.makeText(FirstLoadScreen.this, "NOTHING IN SHARED PREF" + name, Toast.LENGTH_SHORT).show();
            setContentView(R.layout.first_load_form);
        } else {
            Toast.makeText(FirstLoadScreen.this, "PREF FOUND " + name, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, HomeScreen.class);
            intent.putExtra(HomeScreen.FITNESS_SERVICE_KEY, fitnessServiceKey);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.first_load_form);

        getStartedBtn = (Button) findViewById(R.id.getStartedBtn);


        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = (EditText) findViewById(R.id.userFirstName);
                lastName = (EditText) findViewById(R.id.userLastName);
                heightFt = (EditText) findViewById(R.id.userHeightFt);
                heightInch = (EditText) findViewById(R.id.userHeightInch);


                boolean res = validateFormInput(firstName, lastName, heightFt, heightInch);

                if (!res) {
                    Context context = getApplicationContext();
                    CharSequence text = "Invalid Form Input!";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    Intent intent = new Intent(FirstLoadScreen.this, HomeScreen.class);
                    startActivity(intent);
                }
            }
        });


    }

    public void setFitnessServiceKey(String fitnessServiceKey) {
        this.fitnessServiceKey = fitnessServiceKey;
    }

    private boolean validateFormInput(EditText firstName, EditText lastName, EditText heightFt, EditText heightInch){

        if(firstName == null || lastName == null || heightFt == null || heightInch == null) {
            return false;
        }

        String fName;
        String lName;
        int ft;
        int inch;

        try {

            fName = firstName.getText().toString();
            lName = firstName.getText().toString();

            ft = Integer.parseInt(heightFt.getText().toString());
            inch = Integer.parseInt(heightInch.getText().toString());

            if (fName.length() == 0) { return false; }
            if (lName.length() == 0) { return false; }

            if (ft <= 0 || ft > 8) { return false; }
            if (inch < 0 || ft > 11 ) { return false; }


        } catch (Exception e){
            return false;
        }

        //update to SharedPreference
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("firstname", fName);
        editor.putString("lastname", lName);
        editor.putInt("heightFt", ft);
        editor.putInt("heightInch", ft);
        editor.putBoolean("STORED", false);

        editor.apply();
        Toast.makeText(FirstLoadScreen.this, "Saved", Toast.LENGTH_SHORT).show();

        return true;

    }

}
