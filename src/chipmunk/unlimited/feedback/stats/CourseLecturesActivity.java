package chipmunk.unlimited.feedback.stats;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.revfeed.FeedActivity;
import chipmunk.unlimited.feedback.webapi.WebAPI.*;

/**
 * Required intent parameter:
 * PARAM_COURSE_CODE:      The HiG course code.
 */
public class CourseLecturesActivity extends Activity implements OnItemClickListener {
    public static final String PARAM_COURSE_CODE = "course_code";

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
        listView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        LectureVote vote = (LectureVote)mAdapter.getItem(i);
        if (vote != null) {
            String hash = vote.getLectureItem().getLectureHash();

            Intent intent = new Intent(this, FeedActivity.class);
            intent.putExtra(FeedActivity.PARAM_SINGLE_LECTURE, true);
            intent.putExtra(FeedActivity.PARAM_LECTURE_HASH, hash);
            startActivity(intent);
        }
    }
}
