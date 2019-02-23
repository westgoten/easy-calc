package com.westgoten.easycalc;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DivisionByZeroDialogFragment extends DialogFragment {
    public static final String TAG = "division by zero";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.division_by_zero_dialog_title)
                .setMessage(R.string.division_by_zero_dialog_message)
                .setPositiveButton(R.string.division_by_zero_dialog_button, null);

        return builder.create();
    }
}
