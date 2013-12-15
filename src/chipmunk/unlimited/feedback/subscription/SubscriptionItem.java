package chipmunk.unlimited.feedback.subscription;

import android.widget.Toast;


/**
 * Contains the information required to identify
 * a subscription. 
 */
public class SubscriptionItem {
	/* The unique identifier used by TimeEdit (retrieved when searching for subscriptions) */
	private String mTeCode;
	
	/* The unique identified used by HiG, i.e. IMT3672. */
	private String mHigCode;
	
	/* The name of the subscription. I.e., "IMT3672" */
	private String mName;
	
	
	public SubscriptionItem(String timeEditCode, String higCode, String name) {
		mTeCode = timeEditCode;
		mHigCode = higCode;
		mName = name;
	}
	
	public String getTimeEditCode() {
		return mTeCode;
	}
	
	public String getHigCode() {
		return mHigCode;
	}
	
	public String getName() {
		return mName;
	}
}
