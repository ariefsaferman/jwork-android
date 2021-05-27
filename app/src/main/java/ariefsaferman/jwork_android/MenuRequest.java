package ariefsaferman.jwork_android;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MenuRequest extends StringRequest
{
    private static final String URL = "http://10.0.2.2:8080/job";
    private Map<String, String> params;

    public MenuRequest(Response.Listener<String> listener)
    {
        super(Request.Method.GET, URL, listener, null);
        params = new HashMap<>();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError
    {
        return params;
    }
}
