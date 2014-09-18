package cvsi.com.updatemaster.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cvsi.com.updatemaster.R;

/**
 * Created by rivasyshyn on 18.09.2014.
 */
public class ErrorDialog extends DialogFragment {

    private String mTitle;
    private String mMessage;
    private Action mPositive, mNegative;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_error, null);

        try {
            ((TextView) v.findViewById(R.id.tv_message)).setText(mMessage);
            Button btnApply = (Button) v.findViewById(R.id.btn_apply);
            Button btnCancel = (Button) v.findViewById(R.id.btn_cancel);
            if (mPositive != null) {
                btnApply.setText(mPositive.getLabel());
                btnApply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAction(mPositive);
                    }
                });
            } else {
                btnApply.setVisibility(View.GONE);
            }
            if (mNegative != null) {
                btnApply.setText(mNegative.getLabel());
                btnApply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAction(mNegative);
                    }
                });
            } else {
                btnCancel.setVisibility(View.GONE);
            }

        } catch (Exception e) {

        }

        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(mTitle);
        return dialog;
    }

    private void setmPositive(Action mPositive) {
        this.mPositive = mPositive;
    }

    private void setmNegative(Action mNegative) {
        this.mNegative = mNegative;
    }

    public static void show(FragmentActivity context, String title, String message, Action positive, Action negative) {
        ErrorDialog fragment = (ErrorDialog) Fragment.instantiate(context, ErrorDialog.class.getName());
        fragment.mTitle = title;
        fragment.mMessage = message;
        fragment.mPositive = positive;
        fragment.mNegative = negative;
        fragment.show(context.getSupportFragmentManager(), "error");
    }

    public void onAction(Action action) {
        dismissAllowingStateLoss();
        Activity activity = getActivity();
        if (activity instanceof OnActionListener) {
            ((OnActionListener) activity).onAction(action);
        } else {
            throw new IllegalStateException(String.format("activity %s should implement %s", activity, OnActionListener.class.getCanonicalName()));
        }
    }

    public static class Action {
        private int id;
        private String label;

        public Action(int id, String label) {
            this.id = id;
            this.label = label;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public static interface OnActionListener {
        public void onAction(Action action);
    }
}
