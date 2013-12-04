package chipmunk.unlimited.feedback;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;

import java.util.Date;

import chipmunk.unlimited.feedback.database.ReviewedLectureDatabase;

/**
 * Class tracking the number of times the
 * application has been launched. After a certain number
 * of times, a dialog prompting the user to rate the
 * application will be displayed.
 */
public class LaunchPrompt {
    private static final String SHPREF_LAUNCH_TRACKER = "launchTracker";
    public static final String SHPREF_KEY_LAUNCH_COUNT = "launch_count";
    public static final String SHPREF_KEY_NEVER_RATE = "never_rate";

    /* Initially prompt the user at the 20th launch */
    private static final int PROMPT_INITIAL_COUNT = 20;

    /* After the 20th launch, prompt every 5 launches */
    private static final int PROMPT_INTERVAL = 5;


    private Context mContext;


    public LaunchPrompt(Context context) {
        mContext = context;
    }


    public void onLaunch() {
        increment();
        displayPrompt();
    }

    public void onPause() {
        decrement();
    }


    public SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(SHPREF_LAUNCH_TRACKER, 0);
    }

    private int getLaunchCount() {
        SharedPreferences shpref = mContext.getSharedPreferences(SHPREF_LAUNCH_TRACKER, 0);

        if (shpref.getBoolean(SHPREF_KEY_NEVER_RATE, false)) {
            return 0;
        }

        return shpref.getInt(SHPREF_KEY_LAUNCH_COUNT, 0);
    }

    private String getAlertMessage() {
        ReviewedLectureDatabase db = new ReviewedLectureDatabase(mContext);

        String message = mContext.getResources().getString(R.string.rating_prompt_desc);
        return String.format(message, db.getNumberOfItems());
    }


    private void increment() {
        SharedPreferences pref = getSharedPreferences();
        int count = pref.getInt(SHPREF_KEY_LAUNCH_COUNT, 0);
        pref.edit().putInt(SHPREF_KEY_LAUNCH_COUNT, count + 1).commit();
    }

    private void decrement() {
        SharedPreferences pref = getSharedPreferences();
        int count = pref.getInt(SHPREF_KEY_LAUNCH_COUNT, 0);
        pref.edit().putInt(SHPREF_KEY_LAUNCH_COUNT, count - 1).commit();
    }


    private void displayPrompt() {
        if (shouldDisplayPrompt()) {
            getAlertDialogBuilder().show();
        }
    }

    private boolean shouldDisplayPrompt() {
        int count = getLaunchCount();

        if (count >= PROMPT_INITIAL_COUNT) {
            count -= PROMPT_INITIAL_COUNT;
            if ((count % PROMPT_INTERVAL) == 0) {
                SharedPreferences pref = getSharedPreferences();
                boolean neverRate = pref.getBoolean(SHPREF_KEY_NEVER_RATE, false);
                return !neverRate;
            }
        }

        return false;
    }

    private AlertDialog.Builder getAlertDialogBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        Resources r = mContext.getResources();



        builder.setTitle(r.getString(R.string.rating_prompt_title));
        builder.setMessage(getAlertMessage());
        builder.setNeutralButton(r.getString(R.string.rating_prompt_not_now), null);

        /* Marketplace button */
        builder.setPositiveButton(r.getString(R.string.rating_prompt_yes),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    goToMarketplace();
                }
            });

        /* Never ask me again button */
        builder.setNegativeButton(r.getString(R.string.rating_prompt_never),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    neverAskAgain();
                }
            });

        return builder;
    }


    private void neverAskAgain() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(SHPREF_KEY_NEVER_RATE, true).commit();
    }

    private void goToMarketplace() {
        neverAskAgain();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=chipmunk.unlimited.feedback"));
        mContext.startActivity(intent);
    }
}
