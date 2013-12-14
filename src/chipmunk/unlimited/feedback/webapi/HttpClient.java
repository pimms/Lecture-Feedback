package chipmunk.unlimited.feedback.webapi;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;


class HttpClient extends AsyncHttpClient {
    @Override
    public RequestHandle get(String url, ResponseHandlerInterface callback) {
        return super.get(url, callback);
    }
}
