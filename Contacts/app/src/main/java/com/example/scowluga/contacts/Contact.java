package com.example.scowluga.contacts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Contact {

    // Initial data displayed in the list
    private String name;
    private String profile;

    // Details
    private String email;
    private String phonenumber;

    // Constructor
    public Contact (String name, String profile, String email, String phonenumber) {
        this.name = name;
        this.profile = profile;
        this.email = email;
        this.phonenumber = phonenumber;
    }

    // Delimiter between contacts
    public static String grandDelimiter = "-----";
    // Delimiter between contact attributes
    public static String delimiter = "XXXXX";

    // Encoding contact to string form
    public static String encode(Contact contact) {
        // String list of all attributes
        List<String> attributes = contact.getStringList();

        String s = "";
        for (String attr : attributes) {
            // Add attribute to string, then separate by delimiter
            s = s + attr + delimiter;
        }
        // Add a grandDelimiter at the end
        s += grandDelimiter;
        return s;
    }

    // Decoding the contact in string form
    public static Contact decode (String coded) {
        // Split by delimiter
        List<String> strings = Arrays.asList(coded.split(delimiter));
        // Creating new Contact
        Contact contact = new Contact(strings.get(0),
                strings.get(1),
                strings.get(2),
                strings.get(3));
        return contact;

    }
    // Get list of all attributes in contact
    public List<String> getStringList() {
        List<String> strings = new ArrayList<>();
        strings.add(this.name);
        strings.add(this.profile);
        strings.add(this.email);
        strings.add(this.phonenumber);
        return strings;
    }
    @Override
    public boolean equals(Object obj) { // Check if a contact equals another one
        if (obj instanceof  Contact) { // If object is a Contact
            Contact c = (Contact)obj; // The contact
            boolean yes = (c.getName().equals(this.name) && c.getEmail().equals(this.email) && c.getPhonenumber().equals(this.phonenumber));
            if (yes) { return true; }
            else {return false;}
        }
        return false; // Not a contact

    }

    // To String. For debugging clarity
    public String toString() {
        return "Contact: " + name;
    }

    // Getters for all 4 attributes

    // Getters
    public String getProfile() {
        return profile;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
