package chipmunk.unlimited.feedback.webapi;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;


/**
 * Abstract superclass for all WebAPI calls.
 * 
 */
abstract public class WebAPICall extends AsyncHttpResponseHandler {
	private static final String TAG = "WebAPICall";
	
	public void apiCall(String baseUrl) {
		Log.d(TAG, "Dummy webAPI call to " + baseUrl);
	}
	
	
	/**
	 * @param response
	 * String received from the webAPI.
	 * 
	 * @return
	 * JSONObject if the response holds a valid JSON string,
	 * and {"status":"ok", ...}
	 */
	protected JSONObject getJsonRoot(String response) {
		try {
			JSONObject json = new JSONObject(response);
			if (json != null) {
				if (json.getString("status").equals("ok")) {
					return json;
				}
			}
			
		} catch (JSONException ex) {
			return null;
		}
		
		return null;
	}
}
