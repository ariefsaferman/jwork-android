package ariefsaferman.jwork_android.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import ariefsaferman.jwork_android.request.LoginRequest;
import ariefsaferman.jwork_android.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        EditText etEmail = findViewById(R.id.editTextEmailAddress);
        EditText etPassword = findViewById(R.id.editTextTextPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvRegister);
        CheckBox ckRememberMe = findViewById(R.id.remember_me);

        SharedPreferences sharedPref = LoginActivity.this.getSharedPreferences("JWORK_PREF", Context.MODE_PRIVATE);
        Response.Listener<String> responseListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject != null) {
                        // Bikin thread baru untuk menampilkan snackbar
                        new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try {
                                    if (ckRememberMe.isChecked()){
                                        String email = etEmail.getText().toString();
                                        String password = etPassword.getText().toString();


                                        SharedPreferences.Editor edit = sharedPref.edit();
                                        edit.putString("email", email);
                                        edit.putString("password", password);
                                        edit.apply();
                                        Snackbar.make(findViewById(R.id.view_login), "Login Success and credential is saved", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    } else {
                                        Snackbar.make(findViewById(R.id.view_login), "Login Success", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }

                                    Thread.sleep(3000);
//                                            Toast.makeText(LoginActivity.this, "Login Sucessful", Toast.LENGTH_LONG).show();
                                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                                    int jobseekerId = jsonObject.getInt("id");
                                    loginIntent.putExtra("jobseekerId", jobseekerId);
                                    startActivity(loginIntent);
                                    finish();
                                } catch (InterruptedException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                } catch (JSONException e) {
//                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                    Snackbar.make(findViewById(R.id.view_login), "Login Failed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        };

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String email = sharedPref.getString("email", "");
                String password = sharedPref.getString("password", "");
                if (!(email.isEmpty() && password.isEmpty())){
                    LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    queue.add(loginRequest);

                }

            }
        }).start();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();



                LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);


            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}