package com.example.scowluga.contacts;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.scowluga.contacts.ContactDisplay.deleteContact;
import static com.example.scowluga.contacts.ContactProvider.baseProfileName;
import static com.example.scowluga.contacts.DetailActivity.BOOL_KEY;
import static com.example.scowluga.contacts.DetailActivity.EMAIL_KEY;
import static com.example.scowluga.contacts.DetailActivity.NAME_KEY;
import static com.example.scowluga.contacts.DetailActivity.PHONE_KEY;
import static com.example.scowluga.contacts.DetailActivity.PROFILE_KEY;
import static com.example.scowluga.contacts.DetailActivity.emailText;
import static com.example.scowluga.contacts.DetailActivity.nameText;
import static com.example.scowluga.contacts.DetailActivity.phoneText;
import static com.example.scowluga.contacts.DetailActivity.profileView;

public class NewContact extends AppCompatActivity {
    // Finding the widgets
    public EditText nameET;
    public ImageView profileIV;
    public EditText phoneET;
    public EditText emailET;

    private boolean customPic; // If a new picture must be saved into internal storage
    private boolean isEdit; // If this 'new contact' is actually an edit

    // If edit, then this contact is the old contact (to edit)
    private Contact contact = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        // Title of the activity. Set as either 'edit' or 'new'
        TextView titleText = (TextView)findViewById(R.id.new_title_text);

        // Finding the 4 widgets
        nameET = (EditText)findViewById(R.id.new_name);
        profileIV = (ImageView)findViewById(R.id.new_profile);
        emailET = (EditText)findViewById(R.id.new_email);
        phoneET = (EditText)findViewById(R.id.new_phone);

        Intent intent = getIntent();
        isEdit = intent.getBooleanExtra(BOOL_KEY, false); // Finding if it's edit or not
        if (isEdit) {
            // Creating contact
            contact = new Contact(intent.getStringExtra(NAME_KEY),
                    intent.getStringExtra(PROFILE_KEY),
                    intent.getStringExtra(EMAIL_KEY),
                    intent.getStringExtra(PHONE_KEY));
            // Prepopulating widgets
            nameET.setText(contact.getName());
            emailET.setText(contact.getEmail());
            phoneET.setText(contact.getPhonenumber());
            profileIV.setImageBitmap(loadImageFromStorage(getApplicationContext(),
                    contact.getProfile()));
            customPic = false;
            titleText.setText("Edit Contact"); // set title
        } else {
            customPic = false;
            titleText.setText("Create Contact"); // set tiel
        }
        // Take a picture
        Button takePic = (Button)findViewById(R.id.new_take_pic);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture(NewContact.this);
            }
        });

        // Select a picture from gallery
        Button selecPic = (Button)findViewById(R.id.new_select_pic);
        selecPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture(NewContact.this);
            }
        });

        // Create the contact
        Button create = (Button)findViewById(R.id.new_confirm);
        if (isEdit) {create.setText("Apply");};
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Required fields
                List<EditText> required = new ArrayList<EditText>(Arrays.asList(
                        nameET, emailET, phoneET));
                boolean creatable = true;
                for (EditText et : required) { // If either of the required fields are empty
                    if (TextUtils.isEmpty(et.getText())) {
                        et.setError("Field Required");
                        creatable = false; // Cannot be created
                    }
                }
                if (creatable) { // If all required fields are filled
                    String name = nameET.getText().toString();
                    String email = emailET.getText().toString();
                    String phone = phoneET.getText().toString();
                    String fileName;
                    // Creating the fileName.
                    if (isEdit) { // is an edit
                        if (customPic) { // new / custom picture
                            fileName = contact.getProfile(); // Getting the old fileName
                            if (fileName.equals(baseProfileName)) {
                                // If the old fileName was just the base
                                fileName = name + phone + ".jpg"; // create custom name
                            }
                            // Save the bitmap with the fileName
                            Bitmap bitmap = ((BitmapDrawable) profileIV.getDrawable()).getBitmap();
                            saveToInternalStorage(getApplicationContext(), bitmap, fileName);
                        } else { // is Edit, not custom picture.
                            // So the fileName is the old fileName
                            fileName = contact.getProfile();
                        }
                        deleteContact(contact, getApplicationContext()); // delete the old contact
                    } else { // New Contact
                        if (customPic) { // custom picture
                            fileName = name + phone + ".jpg"; // creating name
                            // saving bitmap to internal storage
                            Bitmap bitmap = ((BitmapDrawable) profileIV.getDrawable()).getBitmap();
                            saveToInternalStorage(getApplicationContext(), bitmap, fileName);
                        } else { // new contact with base profile
                            fileName = baseProfileName;
                        }
                    }
                    // Creating the contact with the filename
                    Contact newContact = new Contact(name, fileName, email, phone);
                    // Adding the contact
                    ContactDisplay.addContact(newContact, getApplicationContext());
                    if (isEdit) {
                        // setting the widgets of the past activity (details of the old contact)
                        nameText.setText(newContact.getName());
                        emailText.setText(newContact.getEmail());
                        phoneText.setText(newContact.getPhonenumber());
                        profileView.setImageBitmap(loadImageFromStorage(getApplicationContext(), newContact.getProfile()));
                        Toast.makeText(NewContact.this, "Contact Edited", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewContact.this, "Contact Created", Toast.LENGTH_SHORT).show();
                    }
                    finish(); // finish the 'new contact' creator
                } else { // one of the required fields are missing
                    Toast.makeText(NewContact.this, "One or more fields are missing.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Just cancel the edit / creation
        ImageButton cancel = (ImageButton)findViewById(R.id.new_go_back);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        })
        ;
    }

    public static Bitmap loadImageFromStorage(Context ctx, String fileName) {
        try {
            ContextWrapper cw = new ContextWrapper(ctx);
            // Path to the image directory
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f=new File(directory, fileName);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e) { // Should be handled by the logic
            Toast.makeText(ctx, "File not found", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
    public static String saveToInternalStorage(Context ctx, Bitmap bitmapImage, String fileName){
        /*
        The bitmap is resized here to a maximum height and width. This is to prevent the memory
        error created from loading larger bitmaps.

        A better solution would be to resize the image properly, or create a better way of storing
        and retrieving pictures.
         */
        int maxHeight = 500;
        int maxWidth = 500;
        float scale = Math.min(((float)maxHeight / bitmapImage.getWidth()),
                ((float)maxWidth / bitmapImage.getHeight()));
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // Create bitmap with resized proportions
        Bitmap bitmap = Bitmap.createBitmap(bitmapImage, 0, 0, bitmapImage.getWidth(),
                bitmapImage.getHeight(), matrix, true);

        ContextWrapper cw = new ContextWrapper(ctx);
        // Path to directory with images
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create the file
        File mypath=new File(directory, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Compressing bitmap to write into file
            bitmap.compress(Bitmap.CompressFormat.PNG, 10
                    , fos);
        } catch (Exception e) {
            Toast.makeText(cw, "Error saving file ", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                Toast.makeText(cw, "Error saving file ", Toast.LENGTH_SHORT).show();
            }
        }
        return directory.getAbsolutePath();
    }

    // Take a picture
    public static void takePicture(Activity act) {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        act.startActivityForResult(takePicture, 0); // zero can be replaced with any action code
    }
    // Select from gallery
    public static void selectPicture(Activity act) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        act.startActivityForResult(pickPhoto , 1); // one can be replaced with any action code
    }
    // On having a picture taken / chosen
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0: // Picture was taken
                if(resultCode == RESULT_OK){ // Creating bitmap and setting to the imageView
                    Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    ImageView iv = (ImageView)findViewById(R.id.new_profile);
                    iv.setImageBitmap(bitmap);
                    customPic = true;
                }
                break;
            case 1: // Picture was chosen
                if(resultCode == RESULT_OK && null != imageReturnedIntent){
                    Uri selectImage = imageReturnedIntent.getData();
                    try { // Creating bitmap and setting it to the imageView
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectImage);
                        ImageView iv = (ImageView)findViewById(R.id.new_profile);
                        iv.setImageBitmap(bitmap);
                        customPic = true;
                    } catch (IOException e) {
                        Toast.makeText(this, "Error picking photo", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }
}
