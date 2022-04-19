package com.example.doggo_mobil;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class FeedbackActivity extends AppCompatActivity {

    private Button buttonFeedbackSend, buttonFeedbackCancel;
    private EditText editTextFeedback;

    private String comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        init();

        buttonFeedbackCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonFeedbackSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment = editTextFeedback.getText().toString();
                if(comment.isEmpty() || comment.length() > 255) {
                    Toast.makeText(FeedbackActivity.this, "A visszajelzés minimum 1, maximum 255 karakterből állhat!", Toast.LENGTH_SHORT).show();
                }
                else {
                    new RequestTaskFeedback().execute();
                }
            }
        });
    }

    private void init(){
        buttonFeedbackSend = findViewById(R.id.buttonFeedbackSend);
        buttonFeedbackCancel = findViewById(R.id.buttonFeedbackCancel);
        editTextFeedback = findViewById(R.id.editTextFeedback);
    }

    private class RequestTaskFeedback extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
                String data = String.format("{\"comment\":\"%s\"}", comment);
                response = RequestHandler.post(MapsActivity.URL + "feedbacks", data);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response == null || response.getResponseCode() >= 400){
                Toast.makeText(FeedbackActivity.this, "Hiba történt a visszajelzés küldése során!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(FeedbackActivity.this, "Sikeres visszajelzés!", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }
}