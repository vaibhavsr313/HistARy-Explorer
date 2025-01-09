package com.example.a;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class TriviaActivity extends AppCompatActivity {

    private static final String ARG_QUESTION = "question";
    private static final String ARG_OPTIONS = "options";
    private static final String ARG_ANSWER = "correct_answer";

    private String question;
    private List<String> options;
    private String correctAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);

        Intent intent = getIntent();
        question = intent.getStringExtra(ARG_QUESTION);
        options = intent.getStringArrayListExtra(ARG_OPTIONS);
        correctAnswer = intent.getStringExtra(ARG_ANSWER);

        // Initialize UI components
        TextView questionTextView = findViewById(R.id.question_text);
        RadioGroup optionsGroup = findViewById(R.id.options_group);
        RadioButton option1RadioButton = findViewById(R.id.option1_button);
        RadioButton option2RadioButton = findViewById(R.id.option2_button);
        RadioButton option3RadioButton = findViewById(R.id.option3_button);
        RadioButton option4RadioButton = findViewById(R.id.option4_button);
        Button submitButton = findViewById(R.id.submit_button);

        // Set question and options
        questionTextView.setText(question);
        option1RadioButton.setText(options.get(0));
        option2RadioButton.setText(options.get(1));
        option3RadioButton.setText(options.get(2));
        option4RadioButton.setText(options.get(3));

        // Handle submit button click
        submitButton.setOnClickListener(v -> {
            int selectedId = optionsGroup.getCheckedRadioButtonId();

            if (selectedId == -1) {
                // No option selected
                showToast("Please select an option first.");
                return;
            }

            RadioButton selectedRadioButton = findViewById(selectedId);
            String selectedOption = selectedRadioButton.getText().toString();

            if (selectedOption.equals(correctAnswer)) {
                selectedRadioButton.setBackgroundColor(getResources().getColor(R.color.green)); // Correct option
            } else {
                selectedRadioButton.setBackgroundColor(getResources().getColor(R.color.red)); // Incorrect option

                // Highlight the correct option
                if (option1RadioButton.getText().toString().equals(correctAnswer)) {
                    option1RadioButton.setBackgroundColor(getResources().getColor(R.color.green));
                } else if (option2RadioButton.getText().toString().equals(correctAnswer)) {
                    option2RadioButton.setBackgroundColor(getResources().getColor(R.color.green));
                } else if (option3RadioButton.getText().toString().equals(correctAnswer)) {
                    option3RadioButton.setBackgroundColor(getResources().getColor(R.color.green));
                } else if (option4RadioButton.getText().toString().equals(correctAnswer)) {
                    option4RadioButton.setBackgroundColor(getResources().getColor(R.color.green));
                }
            }

            // Disable all options to prevent further changes
            for (int i = 0; i < optionsGroup.getChildCount(); i++) {
                optionsGroup.getChildAt(i).setEnabled(false);
            }
            submitButton.setEnabled(false); // Disable the submit button
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
