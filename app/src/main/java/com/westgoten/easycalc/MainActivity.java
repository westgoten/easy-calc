package com.westgoten.easycalc;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.gridlayout.widget.GridLayout;
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
    private ScrollView scrollView;
    private Map<String, Integer[]> buttonsInfo;

    private InvalidOperationDialogFragment invalidOperationDialogFragment;
    private LimitExceededDialogFragment limitExceededDialogFragment;
    private DivisionByZeroDialogFragment divisionByZeroDialogFragment;
    private FragmentManager fragmentManager;

    private static final int buttonTextSizeinSp = 18;
    private static final int buttonMarginInDps = 2;

    private static final int maxNumberOfCharacters = 100;

    private static final String RESULT_VIEW_TEXT = "com.westgoten.MainActivity.RESULT_VIEW_TEXT";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        Toolbar toolbar = findViewById(R.id.my_action_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
        && actionBar != null) {
            actionBar.hide();
        }

        invalidOperationDialogFragment = new InvalidOperationDialogFragment();
        limitExceededDialogFragment = new LimitExceededDialogFragment();
        divisionByZeroDialogFragment = new DivisionByZeroDialogFragment();

        scrollView = findViewById(R.id.scroll_view);
        grid = findViewById(R.id.grid);
        resultView = findViewById(R.id.result_text_view);
        if (savedInstanceState != null)
            resultView.setText(savedInstanceState.getCharSequence(RESULT_VIEW_TEXT));

        resultView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                       int oldRight, int oldBottom) {
                if (bottom > scrollView.getHeight())
                    scrollView.scrollTo(0, bottom);
            }
        });

        setButtonsInformation();
        addRemainingButtonsToGrid();

        ImageButton backspaceButton = findViewById(R.id.backspace_button);
        backspaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String expression = resultView.getText().toString();
                int expressionLength = expression.length();
                if (expressionLength > 0)
                    resultView.setText(expression.substring(0, expressionLength - 1));
            }
        });
        backspaceButton.setBackgroundColor(ContextCompat.getColor(this, R.color.nonDigitButtonColor));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence(RESULT_VIEW_TEXT, resultView.getText());

        super.onSaveInstanceState(outState);
    }

    private void addRemainingButtonsToGrid() {
        for (String label : buttonsInfo.keySet()) {
            Button button = new Button(this);
            button.setText(label);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonTextSizeinSp);
            button.setTypeface(null, 1);
            setButtonColor(button);

            GridLayout.Spec rowSpec, columnSpec;
            Integer[] position = buttonsInfo.get(label);

            if (label.equals(getString(R.string.equal))) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String expression = resultView.getText().toString();
                        Pattern pattern = Pattern.compile("[\\+\\-÷×\\.]{2,}|^[÷×\\.]|[÷\\+\\-\\.×]$|\\D\\.[\\d\\D]|\\d\\.\\D|\\..\\.|E[÷\\.×]\\d|E\\d+\\.|E$");
                        Matcher matcher = pattern.matcher(expression);

                        if (matcher.find()) {
                            invalidOperationDialogFragment.show(fragmentManager,
                                    InvalidOperationDialogFragment.TAG);
                        } else {
                            pattern = Pattern.compile("^[\\+\\-]\\d+\\.\\d+E[\\+\\-]?\\d+|^[\\+\\-]\\d+E[\\+\\-]?\\d+|\\d+\\.\\d+E[\\+\\-]?\\d+|\\d+E[\\+\\-]?\\d+|^[\\+\\-]\\d+\\.\\d+|^[\\+\\-]\\d+|\\d+\\.\\d+|\\d+");
                            matcher.usePattern(pattern);
                            matcher.reset();

                            List<String> valuesAndOperationsList = new ArrayList<>();
                            int expressionLength = expression.length();
                            while (matcher.find()) {
                                valuesAndOperationsList.add(matcher.group());
                                int end = matcher.end();
                                if (end < expressionLength)
                                    valuesAndOperationsList.add(expression.substring(end, end + 1));
                                else
                                    break;
                            }

                            while (valuesAndOperationsList.size() > 1) {
                                if (valuesAndOperationsList.contains(getString(R.string.times)) ||
                                        valuesAndOperationsList.contains(getString(R.string.division))) {
                                    for (int i = 1; i < valuesAndOperationsList.size();) {
                                        String operation = valuesAndOperationsList.get(i);
                                        double leftValue, rightValue, parcialResult;

                                        if (operation.equals(getString(R.string.times))) {
                                            leftValue = Double.parseDouble(valuesAndOperationsList.get(i-1));
                                            rightValue = Double.parseDouble(valuesAndOperationsList.get(i+1));
                                            parcialResult = leftValue * rightValue;
                                            addParcialResultToList(parcialResult, i, valuesAndOperationsList);
                                            continue;

                                        } else if (operation.equals(getString(R.string.division))) {
                                            leftValue = Double.parseDouble(valuesAndOperationsList.get(i-1));
                                            rightValue = Double.parseDouble(valuesAndOperationsList.get(i+1));
                                            if (rightValue == 0.0) {
                                                valuesAndOperationsList.clear();
                                                divisionByZeroDialogFragment.show(fragmentManager, DivisionByZeroDialogFragment.TAG);
                                            } else {
                                                parcialResult = leftValue / rightValue;
                                                addParcialResultToList(parcialResult, i, valuesAndOperationsList);
                                            }
                                            continue;
                                        }

                                        i += 2;
                                    }
                                }

                                if (valuesAndOperationsList.contains(getString(R.string.plus)) ||
                                        valuesAndOperationsList.contains(getString(R.string.minus))) {
                                    for (int i = 1; i < valuesAndOperationsList.size();) {
                                        String operation = valuesAndOperationsList.get(i);
                                        double leftValue, rightValue, parcialResult;

                                        if (operation.equals(getString(R.string.plus))) {
                                            leftValue = Double.parseDouble(valuesAndOperationsList.get(i-1));
                                            rightValue = Double.parseDouble(valuesAndOperationsList.get(i+1));
                                            parcialResult = leftValue + rightValue;
                                            addParcialResultToList(parcialResult, i, valuesAndOperationsList);
                                            continue;
                                        } else if (operation.equals(getString(R.string.minus))) {
                                            leftValue = Double.parseDouble(valuesAndOperationsList.get(i-1));
                                            rightValue = Double.parseDouble(valuesAndOperationsList.get(i+1));
                                            parcialResult = leftValue - rightValue;
                                            addParcialResultToList(parcialResult, i, valuesAndOperationsList);
                                            continue;
                                        }

                                        i += 2;
                                    }
                                }
                            }

                            if (!valuesAndOperationsList.isEmpty()) {
                                String result = valuesAndOperationsList.get(0);
                                int afterPointIndex = result.indexOf(getString(R.string.point)) + 1;
                                String afterPointSequence = result.substring(afterPointIndex);
                                if (afterPointSequence.equals("0"))
                                    resultView.setText(result.substring(0, afterPointIndex-1));
                                else
                                    resultView.setText(result);
                            }
                        }
                    }

                    private void addParcialResultToList(double parcialResult, int index, List<String> list) {
                        if (Double.isInfinite(parcialResult)) {
                            list.clear();
                            limitExceededDialogFragment.show(fragmentManager, LimitExceededDialogFragment.TAG);
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
                        else {
                            int inputLength = resultView.getText().length();
                            if (inputLength < maxNumberOfCharacters) {
                                resultView.append(text);
                            } else
                                Toast.makeText(v.getContext(), R.string.max_char_number_toast, Toast.LENGTH_SHORT)
                                        .show();
                        }
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

            final float scale = getResources().getDisplayMetrics().density;
            int marginInPixels = (int) (buttonMarginInDps * scale + 0.5f);
            layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);

            grid.addView(button, layoutParams);
        }
    }

    private void setButtonColor(Button button) {
        String buttonLabel = button.getText().toString();

        if (buttonLabel.equals(getString(R.string.clear))) {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.clearButtonColor));
        } else if (buttonLabel.equals(getString(R.string.equal))) {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.equalButtonColor));
        } else if (Character.isDigit(buttonLabel.charAt(0))) {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.digitButtonColor));
        } else {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.nonDigitButtonColor));
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
