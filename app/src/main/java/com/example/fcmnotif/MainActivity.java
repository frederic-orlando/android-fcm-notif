package com.example.fcmnotif;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey = "key=AAAAGmof8ak:APA91bH4IDXLKtwfLIuJHOrTDBi31mbxPnrshmTybrPp_-19FhDvENH2ZZ6-CNiorJdhHxr2wMhGoEnUh6kvA-dFlQKt76Z0Nbh4qaZ2g8Oo5P6g-JuSvh7IRys8RoD_5o8JZwZVSOhh";
    private String contentType = "application/json";

    Button sendBtn;

    private RequestQueue requestQueue() {
        return Volley.newRequestQueue(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


        // function untuk hapus ID, jadi gabisa terima token / clear semua subscription
        // dipanggil waktu log out
        // clearSubscription();

        // Setelah clear, harus get ID baru lagi, biar bisa subscribe ke notif baru
        // Jadi waktu login, get instance id, trus subscribe
        FirebaseInstanceId.getInstance().getInstanceId();

        // untuk follow notif tertentu, jadi habis login, subscribe ke semua room id yang ada punya user tersebut
        FirebaseMessaging.getInstance().subscribeToTopic("testing");
    }

    private void init() {
        sendBtn = findViewById(R.id.sendBtn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
    }

    private String getTitleText() {
        return ((EditText) findViewById(R.id.titleTxt)).getText().toString();
    }
    private String getMessageText() {
        return ((EditText) findViewById(R.id.messageTxt)).getText().toString();
    }

    private void clearSubscription() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void send() {
        if (!getTitleText().isEmpty() && !getMessageText().isEmpty()) {
            // ganti jadi topic_room_id
            String topic = "/topics/testing";

            JSONObject notification = new JSONObject();
            JSONObject notificationBody = new JSONObject();

            try {
                notificationBody.put("title", getTitleText());
                notificationBody.put("message", getMessageText());
                notification.put("to", topic);
                notification.put("data", notificationBody);
            }
            catch (JSONException e) {
                System.out.println("ON CREATE NOTIFICATION : " + e.getMessage());
            }

            sendNotification(notification);
        }
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(MainActivity.this, "Request Success", Toast.LENGTH_SHORT).show();
                    System.out.println("Success");
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Request Error", Toast.LENGTH_SHORT).show();
                    System.out.println("Error");
                }
            }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();

                params.put("Authorization", serverKey);
                params.put("Content-type",  contentType);

                return params;
            }
        };

        requestQueue().add(jsonObjectRequest);
    }
}