package com.example.scowluga.contacts;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_APPEND;
import static com.example.scowluga.contacts.Contact.decode;
import static com.example.scowluga.contacts.Contact.grandDelimiter;

/**
 * Created by scowluga on 11/30/2016.
 */

public class ContactProvider {

    // Filenames
    public static final String contactFileName = "contactFileName.txt";
    public static final String baseProfileName = "baseprofile.jpg";

    // Reading from a file and getting a list
    public static List<Contact> getContacts(Context context) {
        List<Contact> contacts = new ArrayList<>();

        // Getting the entire string of the file
        String string = getString(context, contactFileName);
        if (TextUtils.isEmpty(string)) { // If there is no text in the file (empty)
            return contacts;
        }
        // Splitting by grandDelimiter (into separate contacts)
        List<String> strings = new ArrayList<>(Arrays.asList(string.split(grandDelimiter)));

        for (String s : strings) { // Decode them then add to list
            contacts.add(decode(s));
        }
        return contacts;
    };

    public static void rewriteContacts(Context context) { // rewriting the file
        // Clears file
        clearFile(context, contactFileName);

        for (Contact contact : ContactDisplay.contacts) { // Write a contact to file
            String write = Contact.encode(contact); // Encode the contact
            FileOutputStream outputStream;
            try {
                outputStream = context.openFileOutput(contactFileName, MODE_APPEND);
                OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                writer.write(write);
                writer.close();
                outputStream.close();
            } catch (Exception e) {
                Toast.makeText(context, "Error saving contacts.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void clearFile(Context context, String fileName) { // Clears file
        // Writes empty string to file
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            writer.write("");
            writer.close();
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(context, "Error writing to file.", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getString(Context context, String fileName) {
        // Get entire string of the file
        FileInputStream inputStream;
        try {
            inputStream = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            char[] inputBuffer = new char[3000];
            String s="";
            int charRead;

            while ((charRead=inputStreamReader.read(inputBuffer))>0) {
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            inputStream.close();
            inputStreamReader.close();
            return s;
        } catch (Exception e) {
            Toast.makeText(context, "Error reading from file.", Toast.LENGTH_SHORT).show();
        }
        return "";
    }
}

