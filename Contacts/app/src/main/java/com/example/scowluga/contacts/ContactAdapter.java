package com.example.scowluga.contacts;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.example.scowluga.contacts.NewContact.loadImageFromStorage;

public class ContactAdapter extends BaseAdapter{

    // The list passed in from the "provider"
    private List<Contact> contactList;
    // Context
    private Context ctx;

    // Creating the adapter
    public ContactAdapter(List<Contact> contacts, Context context) {
        contactList = contacts;
        ctx = context;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Creating the convertView
        if (convertView == null) { // If it doesn't exist
            convertView = LayoutInflater.from(ctx).inflate(R.layout.contact_list_item,
                    parent, false);
        }

        // Getting the contact
        Contact contact = contactList.get(position);

        // Finding the widgets from the view
        TextView nameText = (TextView)convertView.findViewById(R.id.contact_name);
        ImageView profileImage = (ImageView)convertView.findViewById(R.id.contact_profile);


        // Setting the widget's information
        nameText.setText(contact.getName());

        profileImage.setImageBitmap(loadImageFromStorage(ctx, contact.getProfile()));

        return convertView;
    }

}
