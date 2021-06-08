package ariefsaferman.jwork_android;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApplyJobRequest extends StringRequest
{
    private static final String URL = "http://10.0.2.2:8080/invoice/";
    private Map<String, String> params;

    public ApplyJobRequest(ArrayList<Integer> jobIdList, int jobseekerId, int adminFee, Response.Listener<String> listener)
    {
        super(Method.POST, URL+"createBankPayment", listener, null);

        String jobIdListString = String.valueOf(jobIdList);
        jobIdListString = jobIdListString.replace("[", "");
        jobIdListString = jobIdListString.replace("]", "");
        System.out.println(jobseekerId);
        params = new HashMap<>();
        params.put("jobIdList", jobIdListString);
        params.put("jobseekerId", String.valueOf(jobseekerId));
        params.put("adminFee", String.valueOf(adminFee));

    }

    public ApplyJobRequest(ArrayList<Integer> jobIdList, int jobseekerId, String referralCode, Response.Listener<String> listener)
    {
        super(Method.POST, URL+"createEWalletPayment", listener, null);

        String jobIdListString = String.valueOf(jobIdList);
        jobIdListString = jobIdListString.replace("[", "");
        jobIdListString = jobIdListString.replace("]", "");

        System.out.println(jobseekerId);

        params = new HashMap<>();
        params.put("jobIdList", jobIdListString);
        params.put("jobseekerId", String.valueOf(jobseekerId));
        params.put("referralCode", referralCode);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError
    {
        return params;
    }
}
