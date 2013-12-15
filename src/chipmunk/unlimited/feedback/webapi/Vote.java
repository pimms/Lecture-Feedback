package chipmunk.unlimited.feedback.webapi;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import chipmunk.unlimited.feedback.webapi.WebAPI.*;

/**
 * Wrapper around the web API call "vote.php".
 */
class Vote extends WebAPICall {
    private static final String TAG = "Vote";

    public static final String VOTE_POSITIVE = "up";
    public static final String VOTE_NEGATIVE = "down";


    private VoteCallback mCallback;


    /**
     * @param callback
     * The callback to receive the status notification.
     */
    public Vote(VoteCallback callback) {
        mCallback = callback;
    }

    /**
     * Vote a lecture review up or down.
     *
     * @param baseUrl
     * The base URL of the web API.
     *
     * @param reviewId
     * The ID of the review in question.
     *
     * @param type
     * String holding either VOTE_POSITIVE or VOTE_NEGATIVE.
     */
    public void apiCall(String baseUrl,
                        int reviewId, String type) {
        assert(type.equals(VOTE_POSITIVE) || type.equals(VOTE_NEGATIVE));

        Log.v(TAG, "Vote -->");

        baseUrl += "/vote.php";
        baseUrl += "?review_id=" + reviewId;
        baseUrl += "&type=" + type;

        (new AsyncHttpClient()).get(baseUrl, this);
    }


    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        Log.v(TAG, "Vote <-- SUCCESS");

        String response;

        try {
            response = new String(responseBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            mCallback.onVoteFailure("Returned content is not UTF-8 encoded");
            return;
        }

        JSONObject json = getJsonRoot(response);
        try {
            if (json != null && json.getString("status").equals("ok")) {
                mCallback.onVoteSuccess();
            } else {
                mCallback.onVoteFailure("bad JSON status");
            }
        } catch (JSONException ex) {
            mCallback.onVoteFailure("Failed to parse json: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Log.v(TAG, "Vote <-- FAILURE");

        String bodyString;
        String errorMessage = "HTTP Error with code " + statusCode;

        try {
            if (responseBody != null) {
                bodyString = new String(responseBody, "UTF-8");
                errorMessage += " (" + bodyString + ")";
            }
        } catch (UnsupportedEncodingException e) {
            mCallback.onVoteFailure(errorMessage);
            return;
        }

        mCallback.onVoteFailure(errorMessage);
    }
}
