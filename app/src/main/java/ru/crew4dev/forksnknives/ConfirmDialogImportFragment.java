package ru.crew4dev.forksnknives;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by elagin on 27.12.16.
 */

public class ConfirmDialogImportFragment extends DialogFragment {

    private static int uid;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //String name = getArguments().getString("name");
        uid = getArguments().getInt("id");
        String message = getArguments().getString("message");

        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mListener.onDialogImportPositiveClick(ConfirmDialogImportFragment.this, uid);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onDialogImportNegativeClick(ConfirmDialogImportFragment.this);
                    }
                });
        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ConfirmDialogImportListener {
        void onDialogImportPositiveClick(DialogFragment dialog, int id);

        void onDialogImportNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    private ConfirmDialogImportListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ConfirmDialogImportListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }
}

