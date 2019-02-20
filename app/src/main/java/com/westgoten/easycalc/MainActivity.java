package com.westgoten.easycalc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.gridlayout.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private GridLayout grid;
    private TextView resultView;
    private Map<String, Integer[]> buttonsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grid = findViewById(R.id.grid);
        resultView = findViewById(R.id.result_text_view);

        initializeButtonsInfoMap();
        addRemainingButtonsToGrid();
    }

    private void addRemainingButtonsToGrid() {
        for (String label : buttonsInfo.keySet()) {
            Button button = new Button(this);
            button.setText(label);
            button.setTypeface(null, 1);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence text = ((Button) v).getText();
                    if (!Character.isDigit(text.charAt(0)) && text.charAt(0) != '.')
                        resultView.append(" " + text + " ");
                    else
                        resultView.append(text);
                }
            });

            Integer[] position = buttonsInfo.get(label);
            GridLayout.Spec rowSpec = GridLayout.spec(position[0], 1.0f);
            GridLayout.Spec columnSpec = GridLayout.spec(position[1], 1.0f);
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);

            grid.addView(button, layoutParams);
        }
    }

    private void initializeButtonsInfoMap() {
        buttonsInfo = new HashMap<>();
        buttonsInfo.put("÷", new Integer[]{0, 1});
        buttonsInfo.put("×", new Integer[]{0, 2});
        buttonsInfo.put("7", new Integer[]{1, 0});
        buttonsInfo.put("8", new Integer[]{1, 1});
        buttonsInfo.put("9", new Integer[]{1, 2});
        buttonsInfo.put("-", new Integer[]{1, 3});
        buttonsInfo.put("4", new Integer[]{2, 0});
        buttonsInfo.put("5", new Integer[]{2, 1});
        buttonsInfo.put("6", new Integer[]{2, 2});
        buttonsInfo.put("+", new Integer[]{2, 3});
        buttonsInfo.put("1", new Integer[]{3, 0});
        buttonsInfo.put("2", new Integer[]{3, 1});
        buttonsInfo.put("3", new Integer[]{3, 2});
        buttonsInfo.put(".", new Integer[]{4, 2});
    }
}
