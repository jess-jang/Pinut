package com.mindlinksw.schoolmeals.interfaces;


import android.app.Activity;
import android.net.Uri;

import com.microsoft.appcenter.distribute.Distribute;
import com.microsoft.appcenter.distribute.ReleaseDetails;
import com.microsoft.appcenter.distribute.UpdateAction;
import com.mindlinksw.schoolmeals.dialog.AlertDialog;

public class DistributeListener implements com.microsoft.appcenter.distribute.DistributeListener {

    public DistributeListener() {

    }

    @Override
    public boolean onReleaseAvailable(Activity activity, ReleaseDetails releaseDetails) {

        // Look at releaseDetails public methods to get version information, release notes text or release notes URL
        String versionName = releaseDetails.getShortVersion();
        int versionCode = releaseDetails.getVersion();
        String releaseNotes = releaseDetails.getReleaseNotes();
        Uri releaseNotesUrl = releaseDetails.getReleaseNotesUrl();

        // Build our own dialog title and message
        final AlertDialog dialogBuilder = new AlertDialog(activity);
        dialogBuilder.setMessage("Version " + versionName + " available!"); // you should use a string resource instead of course, this is just to simplify example

        // Mimic default SDK buttons
        dialogBuilder.setPositiveButton(activity.getString(com.microsoft.appcenter.distribute.R.string.appcenter_distribute_update_dialog_download), new AlertDialog.OnDialogClickListener() {

            @Override
            public void onClick() {
                // This method is used to tell the SDK what button was clicked
                Distribute.notifyUpdateAction(UpdateAction.UPDATE);
            }

        });

        if (!releaseDetails.isMandatoryUpdate()) {
            dialogBuilder.setNegativeButton(activity.getString(com.microsoft.appcenter.distribute.R.string.appcenter_distribute_update_dialog_postpone), new AlertDialog.OnDialogClickListener() {
                @Override
                public void onClick() {
                    Distribute.notifyUpdateAction(UpdateAction.POSTPONE);
                }
            });
        }
        dialogBuilder.show();

        return true;
    }
}
