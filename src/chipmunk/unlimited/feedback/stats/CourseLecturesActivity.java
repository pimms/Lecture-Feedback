package chipmunk.unlimited.feedback.stats;


import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import chipmunk.unlimited.feedback.R;

/**
 * Required intent parameter:
 * PARAM_COURSE_CODE:      The HiG course code.
 */
public class CourseLecturesActivity extends Activity {
    private static final String PARAM_COURSE_CODE = "course_code";

    private CourseLectureAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_course_lectures);

        String course = getIntent().getStringExtra(PARAM_COURSE_CODE);
        mAdapter = new CourseLectureAdapter(this, course);
        mAdapter.reloadItems(0, 25);

        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(mAdapter);
    }
}
