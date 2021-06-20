package ariefsaferman.jwork_android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ariefsaferman.jwork_android.request.ApplyJobRequest;
import ariefsaferman.jwork_android.request.BonusRequest;
import ariefsaferman.jwork_android.R;
/**
 *
 *
 * @author Arief Saferman
 * @version  18 Juni 2021
 *
 */
public class ApplyJobActivity extends AppCompatActivity
{
    private int jobseekerId;
    private int jobId;
    private String jobName;
    private String jobCategory;
    private double jobFee;
    private int bonus;
    private int selectedPayment;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job);

        // Menerima parameter yang dipassing dari main activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            jobseekerId = extras.getInt("jobseekerId");
            jobId = extras.getInt("jobId");
            jobName = extras.getString("jobName");
            jobCategory = extras.getString("jobCategory");
            jobFee = extras.getDouble("jobFee");
            System.err.println("JobseekerId: " + jobseekerId);
        }

        // Declare setiap variabel pada xml
        TextView tvPesanan = findViewById(R.id.invoiceID);
        TextView tvJobName = findViewById(R.id.job_name);
        TextView tvJobcategory = findViewById(R.id.job_category);
        TextView tvJobFee = findViewById(R.id.job_fee);
        Button btnEwallet = findViewById(R.id.ewallet);
        Button btnBank = findViewById(R.id.bank);
        TextView tvCode = findViewById(R.id.textCode);
        EditText etReferralCode = findViewById(R.id.referral_code);
        TextView tvTotalFee = findViewById(R.id.total_fee);
        RadioGroup rgGroup = findViewById(R.id.radioGroup);
        Button btnApply = findViewById(R.id.btnFinish);
        Button btnHitung = findViewById(R.id.btnCancel);

        // set visibility agar tidak terlihat
        btnApply.setVisibility(Button.INVISIBLE);
        tvCode.setVisibility(TextView.INVISIBLE);
        etReferralCode.setVisibility(EditText.INVISIBLE);

        // mengganti text view dengan menggunakan fungsi setText sesuai paramter yang diberikan
        tvJobName.setText(jobName);
        tvJobcategory.setText(jobCategory);
        tvJobFee.setText("Rp. " + String.valueOf(jobFee));
        tvTotalFee.setText("Rp. 0");

        // Membuat fungsi radio group untuk mencari button yang di click di ApplyJobActivity
        rgGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedPayment = checkedId;
            if (btnHitung.getVisibility() == View.INVISIBLE) {
                btnHitung.setVisibility(View.VISIBLE);
            }
            switch (checkedId){
            case R.id.ewallet:
                tvCode.setVisibility(TextView.VISIBLE);
                etReferralCode.setVisibility(EditText.VISIBLE);
                break;
            case R.id.bank:
                tvCode.setVisibility(TextView.INVISIBLE);
                etReferralCode.setVisibility(EditText.INVISIBLE);
                break;
        }

        });

        // membuat setOnClick agar mendapatkan respon untuk button hitung
        btnHitung.setOnClickListener(v -> {

            if (selectedPayment == R.id.bank) {
                tvTotalFee.setText("Rp. " + jobFee);
            } else if (selectedPayment == R.id.ewallet) {
                if(etReferralCode.getText().toString() != null) {
                    Response.Listener<String> responseListener = new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                if (!jsonResponse.equals(null)) {
                                    bonus = jsonResponse.getInt("extraFee");
                                    boolean active = jsonResponse.getBoolean("active");
                                    int minTotalFee = jsonResponse.getInt("minTotalFee");

                                    if (active == true && jobFee > minTotalFee) {
                                        tvTotalFee.setText(String.valueOf(jobFee + bonus));
                                    } else {
                                        tvTotalFee.setText(String.valueOf(jobFee));
                                    }
                                } else {
                                    tvTotalFee.setText(String.valueOf(jobFee));
                                }
                            } catch (JSONException e) {
                                tvTotalFee.setText(String.valueOf(jobFee));
                                System.out.println("Response: " + e.getMessage());
                            }
                        }
                    };

                    // Membuat request ke class BonusRequest
                    BonusRequest bonusRequest = new BonusRequest( responseListener, etReferralCode.getText().toString());
                    RequestQueue queue = Volley.newRequestQueue(ApplyJobActivity.this);
                    queue.add(bonusRequest);
                }

            }
            //saat request selesai hilangkan button hitung dan munculkan button apply
            btnHitung.setVisibility(Button.INVISIBLE);
            btnApply.setVisibility(Button.VISIBLE);
        });

        // Membuat respon di button apply
        btnApply.setOnClickListener(v -> {
            Response.Listener<String> responseListener = response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject != null) {
                        Snackbar.make(findViewById(R.id.view_apply), "Apply is success", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } catch (JSONException e) {
                    Snackbar.make(findViewById(R.id.view_apply), "Apply is failed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
        }
    };

            ArrayList<Integer> jobs = new ArrayList<>();
            jobs.add(jobId);

            // memilih request sesuai dengan metode yang dipilih berdaasarkan selectedPayment
            if (selectedPayment == R.id.ewallet) {
                ApplyJobRequest applyJobRequest = new ApplyJobRequest(jobs, jobseekerId, etReferralCode.getText().toString(), responseListener);
                RequestQueue queue = Volley.newRequestQueue(ApplyJobActivity.this);
                queue.add(applyJobRequest);
            } else {
                ApplyJobRequest applyJobRequest = new ApplyJobRequest(jobs, jobseekerId, 0, responseListener);
                RequestQueue queue = Volley.newRequestQueue(ApplyJobActivity.this);
                queue.add(applyJobRequest);
            }

        });


    }
}