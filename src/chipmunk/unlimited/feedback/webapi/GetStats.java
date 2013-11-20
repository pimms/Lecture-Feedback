package chipmunk.unlimited.feedback.webapi;

import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import chipmunk.unlimited.feedback.subscription.SubscriptionItem;
import chipmunk.unlimited.feedback.webapi.WebAPI.*;

/**
 * Class managing the web API call "getStats".
 */
class GetStats extends WebAPICall {
    private static final String TAG = "GetStats";
    private static final int ACTION_NONE = -1;
    private static final int ACTION_LECTURE_VOTES_ALL = 0;
    private static final int ACTION_COURSE_VOTES = 1;


    private int mAction = ACTION_NONE;
    private GetLectureVotesAllCallback mLectureVotesCallback;
    private GetCourseVotesCallback mCourseVotesCallback;


    /**
     * Api call for the action "course_votes"
     */
    public void apiCallCourseVotes(GetCourseVotesCallback callback,
                                   String baseUrl,
                                   List<SubscriptionItem> subs) {
        mAction = ACTION_COURSE_VOTES;
        mCourseVotesCallback = callback;

        baseUrl += "/getStats.php?action=course_votes";
        baseUrl += "&courses=" + getCourseCodeCSV(subs);

        new AsyncHttpClient().get(baseUrl, this);
    }

    /**
     * Api call for the action "lecture_votes_all"
     */
    public void apiCallLectureVotesAll(GetLectureVotesAllCallback callback,
                                       String baseUrl, String course,
                                       int first, int count) {
        mAction = ACTION_LECTURE_VOTES_ALL;
        mLectureVotesCallback = callback;

        baseUrl += "/getStats.php?action=lecture_votes_all";
        baseUrl += "&course=" + course;
        baseUrl += "&first=" + first;
        baseUrl += "&count=" + count;

        new AsyncHttpClient().get(baseUrl, this);
    }


    private String getCourseCodeCSV(List<SubscriptionItem> subs) {
        String str = "";

        if (subs.size() > 0) {
            str += subs.get(0).getHigCode();

            for (int i=1; i<subs.size(); i++) {
                str += "," + subs.get(i).getHigCode();
            }
        }

        return str;
    }

    private void notifyCallbackBad(String errorMessage) {
        if (mAction == ACTION_COURSE_VOTES) {
            mCourseVotesCallback.onGetCourseVotesFailure(errorMessage);
        } else if (mAction == ACTION_LECTURE_VOTES_ALL) {
            mLectureVotesCallback.onGetLectureVotesAllFailure(errorMessage);
        }
    }


    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        String response;

        try {
            response = new String(responseBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            notifyCallbackBad("Returned content is not UTF-8 encoded");
            return;
        }

        JSONObject jsonObject = getJsonRoot(response);
        if (jsonObject == null) {
            notifyCallbackBad("Invalid JSON returned");
        } else {
            handleResponseJson(jsonObject);
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        String bodyString;
        String errorMessage = "HTTP Error with code " + statusCode;

        try {
            if (responseBody != null) {
                bodyString = new String(responseBody, "UTF-8");
                errorMessage += " (" + bodyString + ")";
            }
        } catch (UnsupportedEncodingException e) {
            notifyCallbackBad("HTTP error with code " + statusCode);
        }

        notifyCallbackBad(errorMessage);
    }


    private void handleResponseJson(JSONObject json) {
        try {
            if (mAction == ACTION_LECTURE_VOTES_ALL) {
                handleLectureVotesAllResponseJson(json);
            } else if (mAction == ACTION_COURSE_VOTES) {
                handleCourseVotesResponseJson(json);
            } else {
                notifyCallbackBad("Internal error: no action defined");
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            notifyCallbackBad("JSON Exception: " + ex.getMessage());
        }
    }

    private void handleCourseVotesResponseJson(JSONObject json) throws JSONException {
        ArrayList<CourseVote> courseVotes = new ArrayList<CourseVote>();

        int itemCount = json.getInt("item_count");
        JSONArray jsonItems = json.getJSONArray("items");

        for (int i=0; i<itemCount; i++) {
            JSONObject item = jsonItems.getJSONObject(i);
            CourseVote vote = new CourseVote(item);
            courseVotes.add(vote);
        }

        mCourseVotesCallback.onGetCourseVotesSuccess(courseVotes);
    }

    private void handleLectureVotesAllResponseJson(JSONObject json) throws JSONException {
        ArrayList<LectureVote> lectureVotes = new ArrayList<LectureVote>();

        int itemCount = json.getInt("item_count");
        JSONArray jsonItems = json.getJSONArray("items");

        for (int i=0; i<itemCount; i++) {
            JSONObject item = jsonItems.getJSONObject(i);
            LectureVote vote = new LectureVote(item);
            lectureVotes.add(vote);
        }

        mLectureVotesCallback.onGetLectureVotesAllSuccess(lectureVotes);
    }
}

















