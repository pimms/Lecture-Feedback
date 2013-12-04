package chipmunk.unlimited.feedback;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class tracking the number of times the
 * application has been launched.
 */
public class LaunchPrompt {
    private static final String SHPREF_LAUNCH_TRACKER = "launchTracker";
    private static final String SHPREF_KEY_LAUNCH_COUNT = "launch_count";
    private static final String SHPREF_KEY_NEVER_RATE = "never_rate";

    /* Initially prompt the user at the 20th launch */
    private static final int PROMPT_INITIAL_COUNT = 20;

    /* After the 20th launch, prompt every 5 launches */
    private static final int PROMPT_INTERVAL = 5;


    private Context mContext;


    public LaunchPrompt(Context context) {
        mContext = context;
    }


    public void onLaunch() {
        SharedPreferences shpref = mContext.getSharedPreferences(SHPREF_LAUNCH_TRACKER, 0);
        increment(shpref);
    }


    public int getLaunchCount() {
        SharedPreferences shpref = mContext.getSharedPreferences(SHPREF_LAUNCH_TRACKER, 0);

        if (shpref.getBoolean(SHPREF_KEY_NEVER_RATE, false)) {
            return 0;
        }

        return shpref.getInt(SHPREF_KEY_LAUNCH_COUNT, 0);
    }

    private void increment(SharedPreferences preferences) {
        int count = preferences.getInt(SHPREF_KEY_LAUNCH_COUNT, 0);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(SHPREF_KEY_LAUNCH_COUNT, count + 1);
    }
}
