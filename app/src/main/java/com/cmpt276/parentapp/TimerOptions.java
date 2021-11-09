package com.cmpt276.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Contains options for the timer and starts the TimerActivity based on the option selected
 */
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
        setUpBackBtn();

    }

    private void setUpBackBtn() {
        Button backBtn = findViewById(R.id.backBtn_timerOptions);
        backBtn.setText(R.string.backTxt);
        backBtn.setOnClickListener((view) -> {
            finish();
        });
    }

    private void setUpStartButton() {
        Button startButton = findViewById(R.id.timer_start_button);
        startButton.setOnClickListener(view -> {
            if (selected > 0) {
                Intent i = TimerActivity.getIntent(this, selected * 60000L);
                startActivity(i);
                finish();
            }
            else {
                Toast.makeText(this, R.string.error_negative_input, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpRadioButtons() {

        RadioGroup timerOptions = findViewById(R.id.timer_options_radio_group);

        EditText customMinutes = findViewById(R.id.custom_minutes_edit);

        int []minutes = getResources().getIntArray(R.array.minutes_array);

        Typeface font = getResources().getFont(R.font.moon_bold_font);

        for (int minute_option : minutes){
            RadioButton minuteButton = new RadioButton(this);
            minuteButton.setTypeface(font);
            minuteButton.setTextColor(Color.BLACK);
            minuteButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            setButtonGraphics(minuteButton, minute_option + ((minute_option == 1) ? " minute" : " minutes"));
            timerOptions.addView(minuteButton);

            minuteButton.setOnClickListener(view -> {
                customMinutes.setVisibility(View.INVISIBLE);
                selected = minute_option;
            });

        }

        RadioButton customButton = new RadioButton(this);

        customButton.setTypeface(font);
        customButton.setTextColor(Color.BLACK);
        customButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        customButton.setHighlightColor(getColor(R.color.mid_blue));
        timerOptions.addView(customButton);
        setButtonGraphics(customButton, getString(R.string.custom));

        customButton.setOnClickListener(view -> customMinutes.setVisibility(View.VISIBLE));
        setUpTextWatcher(customMinutes);

    }

    private void setUpTextWatcher(EditText customMinutes){
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