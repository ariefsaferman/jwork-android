package ariefsaferman.jwork_android;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class JobSelesaiRequest extends StringRequest
{
    private static final String URL = "http://10.0.2.2:8080/invoice/invoiceStatus/";
    private Map<String, String> params;
    private String invoiceStatus = "Finished";

    public JobSelesaiRequest (String invoiceId, Response.Listener<String> listener)
    {
        super(Method.PUT, URL + invoiceId, listener, null);
        params = new HashMap<>();
        params.put("id", invoiceId);
        params.put("status", invoiceStatus);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError
    {
        return params;
    }

}
