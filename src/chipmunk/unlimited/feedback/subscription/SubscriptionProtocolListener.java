package chipmunk.unlimited.feedback.subscription;

/**
 * The Subscription Protocol is the chain of events happening
 * from when the SubscriptionFragment is displayed until the
 * AddSubscriptionFragment is dismissed. The protocol can be
 * cancelled at any time.
 *
 * The protocol has four steps:
 * 1. The SubscriptionFragment is displayed
 *    This step is initiated externally. No callback
 *    happens in this step.
 *
 * 2. The AddSubscriptionFragment should be displayed
 *    The SubscriptionFragment notifies the Subscription Protocol
 *    Listener that the AddSubscriptionFragment should be created
 *    and displayed. The SubscriptionFragment should also be dismissed.
 *    The callback is notified through "onAddSubscriptionRequest()".
 *
 * 3. The subscription changed
 *    The callback is notified through "onSubscriptionChanged()".
 *
 * 4. The AddSubscriptionFragment should be dismissed.
 *    The callback is notified through "onAddSubscriptionFragmentDismiss()". The
 *    callback is responsible for dismissing the fragment.
 */
public interface SubscriptionProtocolListener {
    public void onAddSubscriptionRequest(SubscriptionFragment toDismiss);
    public void onSubscriptionsChanged();
    public void onAddSubscriptionFragmentDismiss(AddSubscriptionFragment toDismiss);
}
