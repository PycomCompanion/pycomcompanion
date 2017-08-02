package com.dayman.poiot.backend;

import android.content.Context;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by 25143j on 27/07/2017.
 */

public class APIManager {

    private Context mContext;

    private String filename;

    private File path;
    private File file;

    // TODO POSSIBLY REMOVE THE TWO METHODS FOR WRITING CREDS
    // TODO CLEANUP
    // TODO PERHAPS WRITE WITH EDIT MODE

    public APIManager(Context context) {
        this.mContext = context;
        this.filename = "APICreds.txt";

        setupFiles();
    }

    public APIManager(Context context, String filename) {
        this.mContext = context;
        this.filename = filename;
    }

    private void setupFiles() {
        this.path = mContext.getFilesDir();
        this.file = new File(path, filename);
    }

    public void writeCreds(String loginid, String password, String name) throws IOException {
        String contents = loginid + "," + password + "," + name + "\n";

        CharSink sink = Files.asCharSink(file, Charsets.UTF_8, FileWriteMode.APPEND);

        sink.write(contents);
    }

    private void writeCreds(String content) throws IOException {
        CharSink sink = Files.asCharSink(file, Charsets.UTF_8, FileWriteMode.APPEND);

        sink.write(content + "\n");
    }

    public void writeCreds(String... creds) throws IOException {
        CharSink sink = Files.asCharSink(file, Charsets.UTF_8, FileWriteMode.APPEND);

        String contents = "";

        for (String s : creds) {
            contents += s + ",";
        }

        // Sanity check, probably unnecessary but oh well
        if (contents.endsWith(","))
            contents = Util.removeLastChar(contents);

        Log.d("APIManager.java", contents);

        contents += "\n";

        sink.write(contents);

    }

    public ArrayList<String> getCreds() throws IOException {
        ArrayList<String> creds = new ArrayList<>();

        for (String l : Files.readLines(file, Charsets.UTF_8)) {
            creds.add(l);
        }

        return creds;
    }

    public void deleteCreds(int target) throws IOException {
        ArrayList<String> credsArray = getCreds();

        setupFiles();

        // Deleting credits
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();

        for (int i = 0; i < credsArray.size(); i++) {
            if (i != target) {
                writeCreds(credsArray.get(i));
            }
        }
    }

    private void deleteCreds() throws IOException {
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();

        setupFiles();
    }

//    public void editCreds(int target, String newLoginID, String newPassword, String newName) throws IOException {
//        // Backup creds
//        ArrayList<String> credsArray = getCreds();
//
//        // Deleting all creds
//        deleteCreds();
//
//        setupFiles();
//
//        // Rewriting all cred to file
//        for (int i = 0; i < credsArray.size(); i++) {
//            if (i == target) {
//                // Writing the new modified credits
//                writeCreds(newLoginID + "," + newPassword + "," + newName);
//            } else {
//                String[] creds = credsArray.get(i).split(",");
//                writeCreds(creds[0] + "," + creds[1] + "," + creds[2]);
//            }
//        }
//    }

    public void editCreds(int target, String... c) throws IOException {
        // Backup creds
        ArrayList<String> credsArray = getCreds();

        // Deleting all creds
        deleteCreds();

        setupFiles();

        String contents = "";

        for (int i = 0; i < credsArray.size(); i++) {
            if (i == target) {
                for (String s : c) {
                    contents += s + ",";
                    writeCreds(contents);

//                    Log.d("APIManager.java", contents);
                }
            } else {
                writeCreds(credsArray.get(i).split(","));
            }
        }
    }
}
