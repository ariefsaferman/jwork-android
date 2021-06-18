package ariefsaferman.jwork_android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ariefsaferman.jwork_android.request.JobBatalRequest;
import ariefsaferman.jwork_android.request.JobFetchRequest;
import ariefsaferman.jwork_android.request.JobSelesaiRequest;
import ariefsaferman.jwork_android.R;

public class SelesaiJobActivity extends AppCompatActivity
{
    private int jobseekerId;
    private TextView tvJobseekerName, tvInvoiceDate, tvPaymentType, tvInvoiceStatus, tvReferralCode, tvJobName, tvFeeJob, tvTotalFee;
    private Button btnCancel, btnFinished;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selesai_job);

        tvJobseekerName = findViewById(R.id.name_jobseeker);
        tvInvoiceDate = findViewById(R.id.invoice_date);
        tvPaymentType = findViewById(R.id.payment_type);
        tvInvoiceStatus = findViewById(R.id.invoice_status);
        tvReferralCode = findViewById(R.id.code_referral);
        tvJobName = findViewById(R.id.name_job);
        tvFeeJob = findViewById(R.id.fee_job);
        tvTotalFee = findViewById(R.id.fee_total);
        btnCancel = findViewById(R.id.btnCancel);
        btnFinished = findViewById(R.id.btnFinish);

        tvJobseekerName.setVisibility(View.INVISIBLE);
        tvInvoiceDate.setVisibility(View.INVISIBLE);
        tvPaymentType .setVisibility(View.INVISIBLE);
        tvInvoiceStatus.setVisibility(View.INVISIBLE);
        tvReferralCode.setVisibility(View.INVISIBLE);
        tvJobName.setVisibility(View.INVISIBLE);
        tvFeeJob.setVisibility(View.INVISIBLE);
        tvTotalFee.setVisibility(View.INVISIBLE);
        btnCancel.setVisibility(View.INVISIBLE);
        btnFinished.setVisibility(View.INVISIBLE);


        Intent intent = getIntent();
        jobseekerId = intent.getIntExtra("jobseekerId", 0);
        fetchJob();

        //Button batal
        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Response.Listener<String> responseListener = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject != null) {
                                Toast.makeText(SelesaiJobActivity.this, "Cancel Apply", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SelesaiJobActivity.this, JobBatalRequest.class);
                                intent.putExtra("jobseekerId", jobseekerId);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(SelesaiJobActivity.this, "Cancel Apply Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                };
                //untuk melakukan request JobBatalRequest
                JobBatalRequest jobBatalRequest = new JobBatalRequest(String.valueOf(jobseekerId), responseListener);
                RequestQueue queue = Volley.newRequestQueue(SelesaiJobActivity.this);
                queue.add(jobBatalRequest);
            }
        });

        //Button Finish
        btnFinished.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Response.Listener<String> responseListener = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject != null) {
                                Toast.makeText(SelesaiJobActivity.this, "Finish Apply is success", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SelesaiJobActivity.this, JobSelesaiRequest.class);
                                intent.putExtra("jobseekerId", jobseekerId);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(SelesaiJobActivity.this, "Finish Apply is failed", Toast.LENGTH_LONG).show();
                        }
                    }
                };
                //Melakukan request JobSelesaiRequest
                JobSelesaiRequest jobSelesaiRequest = new JobSelesaiRequest(String.valueOf(jobseekerId), responseListener);
                RequestQueue queue = Volley.newRequestQueue(SelesaiJobActivity.this);
                queue.add(jobSelesaiRequest);
            }
        });
    }

    public void fetchJob()
    {
        Response.Listener<String> responseListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    System.out.println(jsonResponse.length());
                    if (jsonResponse != null) {
                        for(int i = 0; i < jsonResponse.length(); i++) {
                            //Object Invoice dan set text jobseekername
                            JSONObject invoice = jsonResponse.getJSONObject(i);
                            String jobseekerName = invoice.getJSONObject("jobseeker").getString("name");
                            tvJobseekerName.setText(jobseekerName);

                            //Object Invoice Date
                            String date = invoice.getString("date");
                            tvInvoiceDate.setText(date);

                            //Payment Type
                            String paymentType = invoice.getString("paymentType");
                            tvPaymentType.setText(paymentType);

                            //Invoice Status
                            String invoiceStatus = invoice.getString("status");
                            tvPaymentType.setText(invoiceStatus);

                            //referral Code
                            if (paymentType.equals("EwalletPayment")){
                                tvReferralCode.setText(paymentType);
                            }

                            //Job Name
                            JSONArray jobList = invoice.getJSONArray("jobs");
                            JSONObject job = jobList.getJSONObject(0);
                            String jobName = job.getString("name");
                            tvJobName.setText(jobName);

                            int jobFee = job.getInt("fee");
                            tvFeeJob.setText(String.valueOf(jobFee));
//                            for (int j = 0; i < jobList.length(); j++) {
//                                JSONObject jobs = jobList.getJSONObject(j);
//                                String jobName = jobs.getString("name");
//                                tvJobName.setText(jobName);
//                                int jobFee = jobs.getInt("fee");
//                                tvFeeJob.setText(Integer.toString(jobFee));
//                            }

                            //Total Fee
                            int totalFee = invoice.getInt("totalFee");
                            tvTotalFee.setText(Integer.toString(totalFee));

                            System.out.println(tvJobseekerName);
                        }
                        tvJobseekerName.setVisibility(View.VISIBLE);
                        tvInvoiceDate.setVisibility(View.VISIBLE);
                        tvPaymentType .setVisibility(View.VISIBLE);
                        tvInvoiceStatus.setVisibility(View.VISIBLE);
                        tvReferralCode.setVisibility(View.VISIBLE);
                        tvJobName.setVisibility(View.VISIBLE);
                        tvFeeJob.setVisibility(View.VISIBLE);
                        tvTotalFee.setVisibility(View.VISIBLE);
                        btnCancel.setVisibility(View.VISIBLE);
                        btnFinished.setVisibility(View.VISIBLE);


                    }
                } catch (JSONException e) {
                    Intent intent = new Intent(SelesaiJobActivity.this, MainActivity.class);
                    intent.putExtra("jobseekerId", jobseekerId);
                    startActivity(intent);

                }

            }
        };
        // Melakukan request pada FetchJobRequest
        JobFetchRequest jobFetchRequest = new JobFetchRequest(String.valueOf(jobseekerId), responseListener);
        RequestQueue queue = Volley.newRequestQueue(SelesaiJobActivity.this);
        queue.add(jobFetchRequest);
    }
}