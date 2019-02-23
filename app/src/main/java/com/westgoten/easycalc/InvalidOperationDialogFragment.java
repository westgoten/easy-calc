package com.westgoten.easycalc;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class InvalidOperationDialogFragment extends DialogFragment {
    public static final String TAG = "invalid operation";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.invalid_operation_dialog_title)
                .setMessage(R.string.invalid_operation_dialog_message);
        builder.setPositiveButton(R.string.invalid_operation_dialog_button, null);

        return builder.create();
    }
}
