package ariefsaferman.jwork_android.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ariefsaferman.jwork_android.Job;
import ariefsaferman.jwork_android.Location;
import ariefsaferman.jwork_android.MainListAdapter;
import ariefsaferman.jwork_android.request.MenuRequest;
import ariefsaferman.jwork_android.R;
import ariefsaferman.jwork_android.Recruiter;

/**
 *
 *
 * @author Arief Saferman
 * @version  18 Juni 2021
 *
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // Membuat variabel global
    private ArrayList<Recruiter> listRecruiter = new ArrayList<>();
    private ArrayList<Job> jobIdList = new ArrayList<>();
    private HashMap<Recruiter, ArrayList<Job>> childMapping = new HashMap<>();
    private int jobseekerId;

    MainListAdapter listAdapter;
    ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_main);

        // Membuat objek untuk navigation view drawer
        NavigationView navigationView = findViewById(R.id.nav_view);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ImageView imageView = findViewById(R.id.menu_drawer);

        //membuat variabel button dan menerima jobseekerId dari login activity
        Button btnApplyJob = findViewById(R.id.btnApplyJob);
        Intent intent = getIntent();
        jobseekerId = intent.getIntExtra("jobseekerId", 0);
        expandableListView = (ExpandableListView) findViewById(R.id.lvExp);

        navigationView.setNavigationItemSelectedListener(this);

        // set drawer di sebelah kiri
        imageView.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                refreshList();
            }
        });

        // Membuat expandable list untuk listing job dan recruiter
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            Intent intent12 = new Intent(MainActivity.this, ApplyJobActivity.class);
            Job selectedJob = childMapping.get(listRecruiter.get(groupPosition)).get(childPosition);
            int jobId = childMapping.get(listRecruiter.get(groupPosition)).get(childPosition).getId();
            String jobName = childMapping.get(listRecruiter.get(groupPosition)).get(childPosition).getName();
            String jobCategory = childMapping.get(listRecruiter.get(groupPosition)).get(childPosition).getCategory();
            double jobFee = childMapping.get(listRecruiter.get(groupPosition)).get(childPosition).getFee();

            intent12.putExtra("jobseekerId", jobseekerId);
            intent12.putExtra("jobId", jobId);
            intent12.putExtra("jobName", jobName);
            intent12.putExtra("jobCategory", jobCategory);
            intent12.putExtra("jobFee", jobFee);


            startActivity(intent12);
            return true;
        });

        // Mengirim jobseekerId apabila selesai apply
        btnApplyJob.setOnClickListener(v -> {
            Intent intent1 = new Intent(MainActivity.this, SelesaiJobActivity.class);
            intent1.putExtra("jobseekerId", jobseekerId);
            startActivity(intent1);
        });
    }

    protected void refreshList(){
        Response.Listener<String> responseListener = response -> {
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
        };
        MenuRequest menuRequest = new MenuRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(menuRequest);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        SharedPreferences sharedPref = MainActivity.this.getSharedPreferences("JWORK_PREF", Context.MODE_PRIVATE);
        switch (item.getItemId()) {
            case R.id.nav_slideshow: {
                Intent intent = new Intent(this, LoginActivity.class);
                SharedPreferences.Editor edit = sharedPref.edit();
                edit.putString("email", "");
                edit.putString("password", "");
                edit.apply();
                startActivity(intent);
                finish();
                break;
            }
        }
        return true;
    }
}