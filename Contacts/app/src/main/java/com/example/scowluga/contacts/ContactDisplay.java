package com.example.scowluga.contacts;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.scowluga.contacts.ContactProvider.baseProfileName;
import static com.example.scowluga.contacts.ContactProvider.contactFileName;
import static com.example.scowluga.contacts.ContactProvider.rewriteContacts;
import static com.example.scowluga.contacts.DetailActivity.BOOL_KEY;
import static com.example.scowluga.contacts.NewContact.saveToInternalStorage;

public class ContactDisplay extends AppCompatActivity {

    // List of all contacts
    public static List<Contact> contacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_display);

        // Finding toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contacts");

        // Adds a contact
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open add
                Bundle args = new Bundle();
                args.putBoolean(BOOL_KEY, false);
                Intent intent = new Intent(ContactDisplay.this, NewContact.class);
                intent.putExtras(args);
                startActivity(intent);
            }
        });
        // Setting up the listView
        setUpList();
    }

    public void setUpList() {
        // Getting reference to the ListView
        ListView lv = (ListView)findViewById(R.id.contact_list);
        // Getting the list of contacts from a provider
        contacts = ContactProvider.getContacts(getApplicationContext());
        // Creating an adapter
        ContactAdapter adapter = new ContactAdapter(contacts, getApplicationContext());
        // Setting the adapter
        lv.setAdapter(adapter);

        // On click for a contact. Open new activity
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Finding the contact
                Contact contact = contacts.get(position);
                // Creating intent to open new activity
                Intent intent = new Intent(ContactDisplay.this, DetailActivity.class);
                // Creating bundle of information
                Bundle args = new Bundle();
                args.putString(DetailActivity.NAME_KEY, contact.getName());
                args.putString(DetailActivity.PROFILE_KEY, contact.getProfile());
                args.putString(DetailActivity.EMAIL_KEY, contact.getEmail());
                args.putString(DetailActivity.PHONE_KEY, contact.getPhonenumber());

                intent.putExtras(args);
                // Start activity
                startActivity(intent);
            }
        });
    }


    public static void addContact(Contact c, Context context) { // Alphabetically insert
        boolean inserted = false;
        if (contacts.size() > 0) { // If not empty
            for (Contact contact : new ArrayList<>(contacts)) { // For contacts
                // Compare alphabetically ignoring case
                int comp = c.getName().compareToIgnoreCase(contact.getName());
                if (comp < 0) { // If before
                    contacts.add(contacts.indexOf(contact), c);
                    inserted = true;
                    break;
                }
            }
            if (!inserted) { // If it's the last one alphabetically
                contacts.add(contacts.size(), c);
            }
        } else { // If empty
            contacts.add(0, c);
        }
        // Rewrite the file
        rewriteContacts(context);
    }

    public static void deleteContact(Contact c, Context context) { // Delete Contact
        for (Contact contact : new ArrayList<>(contacts)) {
            if (c.equals(contact)) { // If equals
                contacts.remove(contacts.indexOf(contact));
                rewriteContacts(context);
            }
        }
    }

    //--------------------------------------------------------------------------------------------

    @Override
    protected void onResume() {
        setUpList();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Bundle args = new Bundle();
            args.putBoolean(BOOL_KEY, false);
            Intent intent = new Intent(ContactDisplay.this, NewContact.class);
            intent.putExtras(args);
            startActivity(intent);
            // Open the same thing as FAB
            return true;
        } else if (id == R.id.action_clear) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ContactDisplay.this);

            builder.setMessage("Clear all contacts?")
                    .setTitle("Clear Contacts");

            builder.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    contacts = new ArrayList<>();
                    rewriteContacts(getApplicationContext());
                    setUpList();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Cancel. Nothing needs to be done
                }
            });

            android.app.AlertDialog dialog = builder.create();
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
