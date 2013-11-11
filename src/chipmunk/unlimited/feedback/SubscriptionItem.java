package chipmunk.unlimited.feedback;

import android.widget.Toast;


/**
 * @class SubscriptionItem
 * Contains the information required to identify
 * a subscription. 
 */
public class SubscriptionItem {
	/* The unique identifier used by TimeEdit (retrieved when searching for subscriptions) */
	private String mItemId;
	
	/* The name of the subscription. I.e., "IMT3672" */
	private String mName;
	
	
	public SubscriptionItem(String itemId, String name) {
		mItemId = itemId;
		mName = name;
	}
	
	public String getItemId() {
		return mItemId;
	}
	
	public String getName() {
		return mName;
	}
}
