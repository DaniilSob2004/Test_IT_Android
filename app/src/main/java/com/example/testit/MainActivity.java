package com.example.testit;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.testit.utils.AppConstant;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText fioInputText;
    private EditText ageInputText;
    private SeekBar salarySeekBar;
    private TextView selectedSalaryText;
    private List<RadioGroup> answerRadioGroups;
    private List<CheckBox> answerCheckBoxes;
    private TextView resultTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // находим виджеты
        findAllWidgets();

        // установка значений в UI
        setDefaultValue();

        // обработка изменения значения SeekBar для salary
        seekBarSalaryListener();
    }

    public void takeTestClick(View view) {
        // проверка данных
        if (!checkData()) {
            return;
        }

        // считаем кол-во балов
        int userPoints = getUserPoints();

        // вывод сообщения
        String message = (userPoints >= 10)
                ? AppConstant.TEST_SUCCESSFULLY_TEXT
                : AppConstant.TEST_FAILED_TEXT;
        resultTextView.setText(message);
        resultTextView.setVisibility(View.VISIBLE);
    }


    private int getUserPoints() {
        int userPoints = 0;

        // проходим по всем RadioGroups и проверяем правильный ли ответ
        for (RadioGroup group : answerRadioGroups) {
            if (isCorrectAnswerToQuestion(group)) {
                userPoints += 2;
            }
        }

        // проходим по всем CheckBoxes и проверяем правильный ли ответ
        for (CheckBox checkBox : answerCheckBoxes) {
            userPoints += getPointsFromCheckBox(checkBox);
        }

        return userPoints;
    }


    private boolean isCorrectAnswerToQuestion(RadioGroup group) {
        int selectedOptionId = group.getCheckedRadioButtonId();

        if (selectedOptionId == -1) {
            return false;
        }

        RadioButton selectedBtn = findViewById(selectedOptionId);
        return selectedBtn.getTag().equals("true");
    }

    private int getPointsFromCheckBox(CheckBox checkBox) {
        // получаем кол-во балов за выбор чекбокса
        if (checkBox.isChecked()) {
            return Integer.parseInt(checkBox.getTag().toString());  // кол-во балов указанно в tag
        }
        return 0;
    }


    private void getAllRadioGroups() {
        // все RadioGroups

        LinearLayout rootLayout = findViewById(R.id.root_answers_linear_layout);
        answerRadioGroups = new ArrayList<>();

        // находим все LinearLayout
        List<LinearLayout> linearLayouts = new ArrayList<>();

        for (int i = 0; i < rootLayout.getChildCount(); i++) {
            View child = rootLayout.getChildAt(i);
            if (child instanceof LinearLayout) {
                linearLayouts.add((LinearLayout) child);
            }
        }

        // находим RadioGroup в каждом LinearLayout
        for (LinearLayout layout : linearLayouts) {
            for (int j = 0; j < layout.getChildCount(); j++) {
                View childInLayout = layout.getChildAt(j);
                if (childInLayout instanceof RadioGroup) {
                    answerRadioGroups.add((RadioGroup) childInLayout);
                }
            }
        }
    }

    private void getAllCheckBoxes() {
        // все checkboxes

        LinearLayout layout = findViewById(R.id.checkbox_linear_layout);
        answerCheckBoxes = new ArrayList<>();

        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof CheckBox) {
                answerCheckBoxes.add((CheckBox) child);
            }
        }
    }


    private void findAllWidgets() {
        fioInputText = findViewById(R.id.fio_input_text);
        ageInputText = findViewById(R.id.age_input_text);
        salarySeekBar = findViewById(R.id.salary_seekbar);
        selectedSalaryText = findViewById(R.id.salary_text_view);
        resultTextView = findViewById(R.id.result_text_view);
        getAllRadioGroups();  // находим все RadioGroup (для вопросов)
        getAllCheckBoxes();  // находим все CheckBox (для ответов)
    }

    private void seekBarSalaryListener() {
        salarySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedSalaryText.setText(progress + "");  // обновляем значение
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setDefaultValue() {
        // установка значений min и max у SeekBar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            salarySeekBar.setMin(AppConstant.MIN_SALARY);
        }
        salarySeekBar.setMax(AppConstant.MAX_SALARY);
    }

    private boolean checkData() {
        // проверка данных на валидность

        // фио
        if (fioInputText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "ФИО не указано", Toast.LENGTH_SHORT).show();
            return false;
        }

        // возраст
        int age = 0;
        try {
            age = Integer.parseInt(ageInputText.getText().toString().trim());
        }
        catch (NumberFormatException e) {}
        if (age < AppConstant.MIN_AGE || age > AppConstant.MAX_AGE) {
            Toast.makeText(
                    this,
                    "Возраст от " + AppConstant.MIN_AGE + " до " + AppConstant.MAX_AGE,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
