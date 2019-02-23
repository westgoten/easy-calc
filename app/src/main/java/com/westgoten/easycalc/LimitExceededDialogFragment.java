package com.westgoten.easycalc;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class LimitExceededDialogFragment extends DialogFragment {
    public static final String TAG = "limit exceeded";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.limit_exceeded_dialog_title)
                .setMessage(R.string.limit_exceeded_dialog_message)
                .setPositiveButton(R.string.invalid_operation_dialog_button, null);

        return builder.create();
    }
}
