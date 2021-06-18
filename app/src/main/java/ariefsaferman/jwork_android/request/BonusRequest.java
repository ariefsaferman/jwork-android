package ariefsaferman.jwork_android.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class BonusRequest extends StringRequest
{
    private static final String URL = "http://10.0.2.2:8080/bonus";
    private Map<String, String> params;

    public BonusRequest(Response.Listener<String> listener, String referralCode)
    {
        super(Request.Method.GET, URL, listener, null);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError
    {
        return null;
    }



}
