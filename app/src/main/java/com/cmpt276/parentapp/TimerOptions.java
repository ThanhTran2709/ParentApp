package com.cmpt276.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class TimerOptions extends AppCompatActivity {

    private int selected;
    private static final int EMPTY_STRING = -1;

    public static Intent getIntent(Context context){
        return new Intent(context, TimerOptions.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_options);
        setUpRadioButtons();
        setUpStartButton();
    }

    private void setUpStartButton() {
        Button startButton = findViewById(R.id.timer_start_button);
        startButton.setOnClickListener(view -> {
            if(selected > 0) {
                Intent i = TimerActivity.getIntent(this, selected);
                startActivity(i);
            }
            else{
                Toast.makeText(this, "Cannot start! Please enter a number greater than 0", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpRadioButtons() {
        RadioGroup timerOptions = findViewById(R.id.timer_options_radio_group);
        EditText customMinutes = findViewById(R.id.custom_minutes_edit);
        int []minutes = getResources().getIntArray(R.array.minutes_array);
        for(int minute_option: minutes){
            RadioButton minuteButton = new RadioButton(this);
            setButtonGraphics(minuteButton, minute_option + ((minute_option == 1) ? " minute" : " minutes"));
            timerOptions.addView(minuteButton);
            minuteButton.setOnClickListener(view -> {
                customMinutes.setVisibility(View.INVISIBLE);
                selected = minute_option;
            });
        }
        RadioButton customButton = new RadioButton(this);
        timerOptions.addView(customButton);
        setButtonGraphics(customButton, "Custom");
        customButton.setOnClickListener(view -> customMinutes.setVisibility(View.VISIBLE));

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    selected = Integer.parseInt(customMinutes.getText().toString());
                }
                catch (Exception e){
                    selected = EMPTY_STRING;
                }
            }
        };
        customMinutes.addTextChangedListener(textWatcher);

    }

    private void setButtonGraphics(RadioButton button, String text){
        button.setText(text);
    }

}