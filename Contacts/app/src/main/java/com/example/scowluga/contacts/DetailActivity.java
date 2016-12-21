package com.example.scowluga.contacts;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.scowluga.contacts.ContactDisplay.deleteContact;
import static com.example.scowluga.contacts.NewContact.loadImageFromStorage;

public class DetailActivity extends AppCompatActivity {

    // Keys for bundles
    public static final String NAME_KEY = "NAME";
    public static final String PROFILE_KEY = "PROFILE";
    public static final String EMAIL_KEY = "EMAIL";
    public static final String PHONE_KEY = "PHONE";
    public static final String BOOL_KEY = "BOOL";

    // Contact
    private Contact contact;

    public static TextView nameText;
    public static ImageView profileView;
    public static Button emailText;
    public static Button phoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Finding the intent with information passed in
        final Intent intent = this.getIntent();

        // Creating the contact from information
        contact = new Contact(intent.getStringExtra(NAME_KEY),
                intent.getStringExtra(PROFILE_KEY),
                intent.getStringExtra(EMAIL_KEY),
                intent.getStringExtra(PHONE_KEY));

        setUp(); // Setup the activity
    }
    private void setUp() {
        // Finding the widgets used to display information
        nameText = (TextView) findViewById(R.id.detail_name);
        profileView = (ImageView) findViewById(R.id.detail_profile);
        emailText = (Button) findViewById(R.id.detail_email);
        phoneText = (Button) findViewById(R.id.detail_phone);

        // Setting information
        nameText.setText(contact.getName());
        profileView.setImageBitmap(loadImageFromStorage(getApplicationContext(), contact.getProfile()));
        emailText.setText(contact.getEmail());
        phoneText.setText(contact.getPhonenumber());

        // Populate an email
        emailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{contact.getEmail()});
                // Nothing to populate subject and title with
//                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
//                i.putExtra(Intent.EXTRA_TEXT, "body of email");
                try { // If there are email clients installed
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(DetailActivity.this, "No email client installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Populate a phone call
        // Option to use Intent.ACTION_CALL, but them permissions are required.
        phoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCall = new Intent(Intent.ACTION_DIAL);
                intentCall.setData(Uri.parse("tel:" + contact.getPhonenumber()));
                startActivity(intentCall);
            }
        });

        // Edit the contact
        Button editButton = (Button)findViewById(R.id.detail_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start new activity to edit it (same as create new contact)
                Bundle args = new Bundle();
                Intent intent = new Intent(DetailActivity.this, NewContact.class);
                args.putBoolean(BOOL_KEY, true);
                args.putString(NAME_KEY, contact.getName());
                args.putString(PROFILE_KEY, contact.getProfile());
                args.putString(EMAIL_KEY, contact.getEmail());
                args.putString(PHONE_KEY, contact.getPhonenumber());
                intent.putExtras(args);
                startActivity(intent);
                // Open "new contact creator" with pre populated fields
            }
        });

        // Delete the contact
        Button deleteButton = (Button) findViewById(R.id.detail_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);

                builder.setMessage("Are you sure you want to delete this contact?")
                        .setTitle("Delete Contact");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        deleteContact(contact, getApplicationContext());
                        Toast.makeText(DetailActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing.
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }
}
