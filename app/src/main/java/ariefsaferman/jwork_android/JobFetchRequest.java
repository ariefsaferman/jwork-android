package ariefsaferman.jwork_android;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class JobFetchRequest extends StringRequest
{
    private static final String URL = "http://10.0.2.2:8080/invoice/jobseeker/";
    private Map<String, String> params;

    public JobFetchRequest(String jobseekerId, Response.Listener<String> listener)
    {
        super(Method.GET, URL+jobseekerId, listener, null);
        System.out.println("JobseekerId: " + jobseekerId);
        params = new HashMap<>();
    }

    protected Map<String, String> getParams() throws AuthFailureError
    {
        return params;
    }
}
