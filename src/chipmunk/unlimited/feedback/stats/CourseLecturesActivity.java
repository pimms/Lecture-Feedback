package chipmunk.unlimited.feedback.stats;


import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import chipmunk.unlimited.feedback.R;

public class CourseLecturesActivity extends Activity {
    private CourseLectureAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_course_lectures);

        mAdapter = new CourseLectureAdapter(this);

        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(mAdapter);
    }



}
