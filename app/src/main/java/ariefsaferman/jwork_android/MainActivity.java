package ariefsaferman.jwork_android;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{
    private ArrayList<Recruiter> listRecruiter = new ArrayList<>();
    private ArrayList<Job> jobIdList = new ArrayList<>();
    private HashMap<Recruiter, ArrayList<Job>> childMapping = new HashMap<>();
    private int jobseekerId;

    MainListAdapter listAdapter;
    ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnApplyJob = findViewById(R.id.btnApplyJob);
        Intent intent = getIntent();
        jobseekerId = intent.getIntExtra("jobseekerId", 0);
        expandableListView = (ExpandableListView) findViewById(R.id.lvExp);

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                refreshList();
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                Intent intent = new Intent(MainActivity.this, ApplyJobActivity.class);
                Job selectedJob = childMapping.get(listRecruiter.get(groupPosition)).get(childPosition);
                jobseekerId = childMapping.get(listRecruiter.get(groupPosition)).get(childPosition).getId();
                int jobId = childMapping.get(listRecruiter.get(groupPosition)).get(childPosition).getId();
                String jobName = childMapping.get(listRecruiter.get(groupPosition)).get(childPosition).getName();
                String jobCategory = childMapping.get(listRecruiter.get(groupPosition)).get(childPosition).getCategory();
                double jobFee = childMapping.get(listRecruiter.get(groupPosition)).get(childPosition).getFee();

                intent.putExtra("jobseekerId", jobseekerId);
                intent.putExtra("jobId", jobId);
                intent.putExtra("jobName", jobName);
                intent.putExtra("jobCategory", jobCategory);
                intent.putExtra("jobFee", jobFee);


                startActivity(intent);
                return true;
            }
        });

        btnApplyJob.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, SelesaiJobActivity.class);
                intent.putExtra("jobseekerId", jobseekerId);
                startActivity(intent);
//                Response.Listener<String> responseListener = new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response)
//                    {
//                        try {
//                            JSONArray jsonResponse = new JSONArray(response);
//                            if (jsonResponse != null) {
//                                Intent intent = new Intent(MainActivity.this, SelesaiJobActivity.class);
//                                for (int i = 0; i<jsonResponse.length(); i++) {
//                                    JSONObject invoice = jsonResponse.getJSONObject(i);
//                                    jobseekerId = invoice.getInt("jobseekerId");
//                                }
//                                intent.putExtra("jobseekerId", jobseekerId);
//                                startActivity(intent);
//                            }
//                        } catch (JSONException e) {
//                            Toast.makeText(MainActivity.this, "JSON FAILED", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                };
//
//                JobFetchRequest jobFetchRequest = new JobFetchRequest(String.valueOf(jobseekerId), responseListener);
//                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
//                queue.add(jobFetchRequest);
            }
        });
    }

    protected void refreshList(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    if (jsonResponse != null) {
                        for (int i = 0; i < jsonResponse.length(); i++){
                            JSONObject job = jsonResponse.getJSONObject(i);
                            JSONObject recruiter = job.getJSONObject("recruiter");
                            JSONObject location = recruiter.getJSONObject("location");

                            String city = location.getString("city");
                            String province = location.getString("province");
                            String description = location.getString("description");

                            Location location1 = new Location(province, city, description);

                            int recruiterId = recruiter.getInt("id");
                            String recruiterName = recruiter.getString("name");
                            String recruiterEmail = recruiter.getString("email");
                            String recruiterPhoneNumber = recruiter.getString("phoneNumber");

                            Recruiter newRecruiter = new Recruiter(recruiterId, recruiterName, recruiterEmail, recruiterPhoneNumber, location1);
                            if (listRecruiter.size() > 0) {
                                boolean success = true;
                                for (Recruiter rec : listRecruiter)
                                    if (rec.getId() == newRecruiter.getId())
                                        success = false;
                                if (success) {
                                    listRecruiter.add(newRecruiter);
                                }
                            } else {
                                listRecruiter.add(newRecruiter);
                            }

                            int jobId = job.getInt("id");
                            int jobFee = job.getInt("fee");
                            String jobName = job.getString("name");
                            String jobCategory = job.getString("category");

                            Job newJob = new Job(jobId, jobFee, jobName, jobCategory, newRecruiter);
                            jobIdList.add(newJob);
                        }

                        for (Recruiter rec : listRecruiter) {
                            ArrayList<Job> temp = new ArrayList<>();
                            for (Job jobs : jobIdList) {
                                if (jobs.getRecruiter().getName().equals(rec.getName()) ||
                                        jobs.getRecruiter().getEmail().equals(rec.getEmail()) ||
                                        jobs.getRecruiter().getPhoneNumber().equals(rec.getPhoneNumber())) {
                                    temp.add(jobs);
                                }
                            }
                            childMapping.put(rec, temp);
                        }

                        listAdapter = new MainListAdapter(MainActivity.this, listRecruiter, childMapping);
                        expandableListView.setAdapter(listAdapter);
                    }
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }
        };
        MenuRequest menuRequest = new MenuRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(menuRequest);
    }
}