package com.example.camerag14;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    //EditText name = findViewById(R.id.Name);
    //EditText age = findViewById(R.id.Age);
     //Button submit = (Button) findViewById(R.id.submit);
    //RadioButton male = (RadioButton) findViewById(R.id.Male);
    //RadioButton female = (RadioButton) findViewById(R.id.Female);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button submit = findViewById(R.id.submit);
        RadioGroup gender = (RadioGroup) findViewById(R.id.Gender);

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.Male) {
                    Toast.makeText(getApplicationContext(), "Male",
                            Toast.LENGTH_SHORT).show();
                } else if(checkedId == R.id.Female) {
                    Toast.makeText(getApplicationContext(), "Female",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,CameraFocusActivity.class);
                startActivity(i);
            }
        });

    }
}
