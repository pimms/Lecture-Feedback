package chipmunk.unlimited.feedback.stats;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

import java.security.InvalidParameterException;

import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.revfeed.FeedActivity;
import chipmunk.unlimited.feedback.webapi.WebAPI.*;

/**
 * Required intent parameter:
 * PARAM_COURSE_CODE:      The HiG course code.
 * PARAM_COURSE_NAME:      The course name
 */
public class CourseLecturesActivity extends Activity implements OnItemClickListener {
    public static final String PARAM_COURSE_CODE = "course_code";
    public static final String PARAM_COURSE_NAME = "course_name";

    private CourseLectureAdapter mAdapter;

    private String mCourseCode;
    private String mCourseName;


    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_listview);

        getIntentParameters();

        mAdapter = new CourseLectureAdapter(this, mCourseCode);
        mAdapter.reloadItems(0, 25);

        ListView listView = (ListView)findViewById(android.R.id.list);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        getActionBar().setTitle(mCourseName);
    }

    private void getIntentParameters() {
        mCourseCode = getIntent().getStringExtra(PARAM_COURSE_CODE);
        mCourseName = getIntent().getStringExtra(PARAM_COURSE_NAME);

        if (mCourseCode == null || mCourseCode.length() == 0) {
            throw new InvalidParameterException("PARAM_COURSE_CODE cannot be undefined");
        }

        if (mCourseName == null || mCourseName.length() == 0) {
            throw new InvalidParameterException("PARAM_COURSE_NAME cannot be undefined");
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        LectureVote vote = (LectureVote)mAdapter.getItem(i);
        if (vote != null) {
            // Open a FeedActivity displaying only the reviews
            // of the clicked lecture.
            String hash = vote.getLectureItem().getLectureHash();
            String title = vote.getLectureItem().getPrettyDateString(this);

            Intent intent = new Intent(this, FeedActivity.class);
            intent.putExtra(FeedActivity.PARAM_SINGLE_LECTURE, true);
            intent.putExtra(FeedActivity.PARAM_LECTURE_HASH, hash);
            intent.putExtra(FeedActivity.PARAM_ACTIONBAR_TITLE, title);
            startActivity(intent);
        }
    }
}
