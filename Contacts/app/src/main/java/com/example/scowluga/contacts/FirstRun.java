package com.example.scowluga.contacts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import static com.example.scowluga.contacts.ContactDisplay.addContact;
import static com.example.scowluga.contacts.ContactDisplay.contacts;
import static com.example.scowluga.contacts.ContactProvider.baseProfileName;
import static com.example.scowluga.contacts.ContactProvider.contactFileName;
import static com.example.scowluga.contacts.NewContact.saveToInternalStorage;

public class FirstRun extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);

        // If this is the first run, then 'first' will not exist. Therefore defaulted to true
        boolean first = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirst", true);
        if (first) { // First run
            // Executing the set up of the application
            AsyncTask task = new ProgressTask(getApplicationContext(), FirstRun.this).execute();

            // Set 'first' as false so the setup doesn't happen every time
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isFirst", false).apply();

        } else {
            // Just start the activity
            Intent intent = new Intent(FirstRun.this, ContactDisplay.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    // Setting up the application
    private class ProgressTask extends AsyncTask<String, Void, Boolean> {
        private Context context;
        private ProgressDialog dialog;
        public ProgressTask(Context ctx, Activity act) {
            context = ctx;
            dialog = new ProgressDialog(act);
        }
        protected void onPreExecute() {
            dialog.setMessage("Setting up Application...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (success) {
                // Start activity because successfuly set up
                Intent intent = new Intent(FirstRun.this, ContactDisplay.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show();
            }
        }

        protected Boolean doInBackground(final String... args) {
            initializeContacts(context);
            return true;
        }
    }

    private static void initializeContacts(Context ctx) {
        contacts = new ArrayList<>();

        // Setting up the file in which the contacts are read from
        File file = new File(ctx.getFilesDir(), contactFileName);

        // Setting up the base profile image (outline of person)
        Bitmap baseProfile = BitmapFactory.decodeResource(ctx.getResources(),
                R.drawable.person);
        saveToInternalStorage(ctx, baseProfile, baseProfileName);

        // Setting up the 3 base profiles
        Bitmap circle = BitmapFactory.decodeResource(ctx.getResources(),
                R.drawable.circle);
        saveToInternalStorage(ctx, circle, "circle.png");
        Bitmap square = BitmapFactory.decodeResource(ctx.getResources(),
                R.drawable.square);
        saveToInternalStorage(ctx, square, "square.png");
        Bitmap triangle = BitmapFactory.decodeResource(ctx.getResources(),
                R.drawable.triangle);
        saveToInternalStorage(ctx, triangle, "triangle.png");

        // Creating the 3 basic profiles
        Contact circleContact = new Contact("Circle", "circle.png"
                , "circle@gmail.com", "555-555-5555");
        addContact(circleContact, ctx);

        Contact squareContact = new Contact("Square", "square.png"
                , "square@gmail.com", "555-555-5555");
        addContact(squareContact, ctx);

        Contact triContact = new Contact("Triangle", "triangle.png"
                , "triangle@gmail.com", "555-555-5555");
        addContact(triContact, ctx);
    };
}
