package com.westgoten.easycalc;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.gridlayout.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private GridLayout grid;
    private TextView resultView;
    private Map<String, Integer[]> buttonsInfo;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.my_action_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
        && actionBar != null) {
            actionBar.hide();
        }

        grid = findViewById(R.id.grid);
        resultView = findViewById(R.id.result_text_view);

        setButtonsInformation();
        addRemainingButtonsToGrid();
    }

    private void addRemainingButtonsToGrid() {
        for (String label : buttonsInfo.keySet()) {
            Button button = new Button(this);
            button.setText(label);
            button.setTypeface(null, 1);

            GridLayout.Spec rowSpec, columnSpec;
            Integer[] position = buttonsInfo.get(label);

            if (label.equals(getString(R.string.equal))) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String expression = resultView.getText().toString();
                        Pattern pattern = Pattern.compile("\\+{2,}|\\-{2,}|×{2,}|\\.{2,}|÷{2,}|^[÷×\\.]|[÷\\+\\-\\.×]$|\\D\\.[\\d\\D]|\\d\\.\\D|E[÷\\.×]\\d|E\\d+\\.|E$");
                        Matcher matcher = pattern.matcher(expression);

                        if (matcher.find()) {
                            //TO DO: Invalid Operation Report
                        } else {
                            pattern = Pattern.compile("^[\\+\\-]\\d+\\.\\d+E[\\+\\-]?\\d+|^[\\+\\-]\\d+E[\\+\\-]?\\d+|\\d+\\.\\d+E[\\+\\-]?\\d+|\\d+E[\\+\\-]?\\d+|^[\\+\\-]\\d+\\.\\d+|^[\\+\\-]\\d+|\\d+\\.\\d+|\\d+");
                            matcher.reset();
                            matcher.usePattern(pattern);

                            List<String> valuesAndOperationsList = new ArrayList<>();
                            while (matcher.find()) {
                                valuesAndOperationsList.add(matcher.group());
                                int end = matcher.end();
                                if (end < valuesAndOperationsList.size())
                                    valuesAndOperationsList.add(expression.substring(end, end+1));
                            }

                            while (valuesAndOperationsList.size() > 1) {
                                if (valuesAndOperationsList.contains(getString(R.string.times)) ||
                                        valuesAndOperationsList.contains(getString(R.string.division))) {
                                    // TO DO: Synchronize loop increment and list shrinking
                                    for (int i = 1; i < valuesAndOperationsList.size(); i += 2) {
                                        String operation = valuesAndOperationsList.get(i);
                                        double leftValue, rightValue, parcialResult;

                                        if (operation.equals(getString(R.string.times))) {
                                            leftValue = Double.parseDouble(valuesAndOperationsList.get(i-1));
                                            rightValue = Double.parseDouble(valuesAndOperationsList.get(i+1));
                                            parcialResult = leftValue * rightValue;
                                            addParcialResultToList(parcialResult, i, valuesAndOperationsList);

                                        } else if (operation.equals(getString(R.string.division))) {
                                            leftValue = Double.parseDouble(valuesAndOperationsList.get(i-1));
                                            rightValue = Double.parseDouble(valuesAndOperationsList.get(i+1));
                                            if (rightValue == 0.0) {
                                                // TO DO: Invalid Operation Report (Division by 0)
                                                valuesAndOperationsList.clear();
                                            } else {
                                                parcialResult = leftValue / rightValue;
                                                addParcialResultToList(parcialResult, i, valuesAndOperationsList);
                                            }
                                        }
                                    }
                                }

                                if (valuesAndOperationsList.contains(getString(R.string.plus)) ||
                                        valuesAndOperationsList.contains(getString(R.string.minus))) {
                                    // TO DO: Sum and subtraction calculations
                                }
                            }
                        }
                    }

                    private void addParcialResultToList(double parcialResult, int index, List<String> list) {
                        if (Double.isInfinite(parcialResult)) {
                            //TO DO: Limit Exceeded Report
                            list.clear();
                        } else {
                            list.remove(index + 1);
                            list.remove(index - 1);
                            list.set(index - 1, String.valueOf(parcialResult));
                        }
                    }
                });

                rowSpec = GridLayout.spec(position[0], 2, 2.0f);
                columnSpec = GridLayout.spec(position[1], 1.0f);
            } else {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = ((Button) v).getText().toString();
                        if (text.equals(getString(R.string.clear)))
                            resultView.setText("");
                        else
                            resultView.append(text);
                    }
                });

                if (label.equals(getString(R.string.zero))) {
                    rowSpec = GridLayout.spec(position[0], 1.0f);
                    columnSpec = GridLayout.spec(position[1], 2, 2.0f);
                } else {
                    rowSpec = GridLayout.spec(position[0], 1.0f);
                    columnSpec = GridLayout.spec(position[1], 1.0f);
                }
            }

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
            layoutParams.width = 0;
            layoutParams.height = 0;

            grid.addView(button, layoutParams);
        }
    }

    private void setButtonsInformation() {
        buttonsInfo = new HashMap<>();
        buttonsInfo.put(getString(R.string.clear), new Integer[]{0, 0});
        buttonsInfo.put(getString(R.string.division), new Integer[]{0, 1});
        buttonsInfo.put(getString(R.string.times), new Integer[]{0, 2});
        buttonsInfo.put(getString(R.string.seven), new Integer[]{1, 0});
        buttonsInfo.put(getString(R.string.eight), new Integer[]{1, 1});
        buttonsInfo.put(getString(R.string.nine), new Integer[]{1, 2});
        buttonsInfo.put(getString(R.string.minus), new Integer[]{1, 3});
        buttonsInfo.put(getString(R.string.four), new Integer[]{2, 0});
        buttonsInfo.put(getString(R.string.five), new Integer[]{2, 1});
        buttonsInfo.put(getString(R.string.six), new Integer[]{2, 2});
        buttonsInfo.put(getString(R.string.plus), new Integer[]{2, 3});
        buttonsInfo.put(getString(R.string.one), new Integer[]{3, 0});
        buttonsInfo.put(getString(R.string.two), new Integer[]{3, 1});
        buttonsInfo.put(getString(R.string.three), new Integer[]{3, 2});
        buttonsInfo.put(getString(R.string.equal), new Integer[]{3, 3});
        buttonsInfo.put(getString(R.string.zero), new Integer[]{4, 0});
        buttonsInfo.put(getString(R.string.point), new Integer[]{4, 2});
    }
}
