package cvsi.com.updatemaster.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import cvsi.com.updatemaster.R;

/**
 * Created by rivasyshyn on 17.09.2014.
 */
public class SettingsDialog extends android.support.v4.app.DialogFragment {

    EditText etUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_settings, null);
        etUrl = (EditText) v.findViewById(R.id.et_url);
        v.findViewById(R.id.btn_apply).setOnClickListener(mPositiveClickListener);
        v.findViewById(R.id.btn_cancel).setOnClickListener(mNegativeClickListener);

        etUrl.setText(getArguments().getString("url"));

        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getString(R.string.action_settings));
        return dialog;
    }

    private View.OnClickListener mPositiveClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Activity activity = getActivity();
            if (activity instanceof OnSettingsChangedListener) {
                ((OnSettingsChangedListener) activity).onSettingsChanged(etUrl.getText().toString());
            } else {
                throw new IllegalStateException(String.format("activity %s should implement %s", activity, OnSettingsChangedListener.class.getCanonicalName()));
            }
            dismissAllowingStateLoss();
        }
    };

    private View.OnClickListener mNegativeClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            dismissAllowingStateLoss();
        }
    };

    public static interface OnSettingsChangedListener {
        public void onSettingsChanged(String url);
    }
}
