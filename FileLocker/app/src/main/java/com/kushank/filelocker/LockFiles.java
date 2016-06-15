package com.kushank.filelocker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;

/**
 * Created by Kushank on 13-Jun-16.
 * The Class to handle the operation of Locking The files and Browsing through the memory to find file.
 */
public class LockFiles extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemLongClickListener {
    ListView v;
    static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1024;
    String options[];
    String curDir;
    String forUpdating[];

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lockfiles);
        findViewById(R.id.block).setOnClickListener(this);

        v = (ListView) findViewById(R.id.lvLockFiles);

        // v.setItemChecked(0,true);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            //ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
            }
            options = new String[1];
            options[0] = "Kush1";
            v.setAdapter(new ArrayAdapter<>(LockFiles.this, android.R.layout.simple_list_item_multiple_choice, options));

        } else {
            curDir = Environment.getExternalStorageDirectory().toString();
            refreshListAndDisplayWithCurDir(true);
        }
        v.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        v.setOnItemClickListener(this);
        v.setOnItemLongClickListener(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void refreshListAndDisplayWithCurDir(Boolean first) {
        Log.d("LockFies", "Refreshing List with... " + curDir);
        File f = new File(curDir);
        File file[] = f.listFiles();

        int skip=0;
        for(int i=0;i<file.length-skip;i++)
        {
            if(file[i].getName().contains("_kush_")||(file[i].getName().charAt(0)=='.')) {
                file[i] = file[file.length - 1 - skip];
                file[file.length - 1 - skip] = null;
                skip++;
                i--;
            }
        }

        int uptoDir = sortForDir(file,skip);

        Log.d("LockFiles", "Size: " + file.length+ " uptoDir : "+uptoDir);
        options = new String[file.length-skip];
        for (int i = 0; i < file.length-skip; i++)
            options[i] = file[i].getName();

        try {
            sortNames(options, 0, uptoDir);
            sortNames(options, uptoDir + 1, file.length - skip - 1);
        }catch (Exception e){
        //    e.printStackTrace();
        }

        v.setAdapter(new ArrayAdapter<>(LockFiles.this, android.R.layout.simple_list_item_multiple_choice, options));
        if (!first)
            v.refreshDrawableState();
    }

    private void sortNames(String[] array, int s, int e) {
        int len=array.length;
        if(s>=e) return;
        if(s<0||e>=len) return;
        for(int i=s;i<=e;i++)
        {
            for(int j=s;j<i;j++)
            {
                if(array[i].compareToIgnoreCase(array[j])<0)
                {
                    String temp=array[i];
                    array[i]=array[j];
                    array[j]=temp;
                }
            }
        }
    }


    private int sortForDir(File[] file, int skip) {
        int front=0,end=file.length-skip-1,len=file.length-skip;
        while(front<len && end>=0 && front<end)
        {
            while(front<len && file[front].isDirectory() ) front++;
            while(end>=0 && !file[end].isDirectory()) end--;
            if(front>end){
                break;
            }
            else if(front==end){
                Log.d("LockFiles","Equality");
                if(file[front].isDirectory())
                    front++;
                break;
            }
            File temp;
            temp = file[front];
            file[front]=file[end];
            file[end]=temp;
            end--; front++;
        }
        if(front==end)
        {
            Log.d("LockFiles","Equality");

            if(file[front]!=null && file[front].isDirectory())
                front++;
        }
        return front-1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
            curDir = Environment.getExternalStorageDirectory().toString();

            refreshListAndDisplayWithCurDir(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        File temp = new File(curDir + "/" + options[position]);
        Log.d("OnItemSelected", "curDir : " + temp.toString());
        if (temp.isDirectory()) {
            v.setItemChecked(position, false);

            Log.d("OnItemSelected", "Is Dir : true ");

            curDir += "/" + options[position];

            refreshListAndDisplayWithCurDir(false);
        }
    }


    @Override
    public void onBackPressed() {
        //
        if ((Environment.getExternalStorageDirectory().toString()).equals(curDir))
            super.onBackPressed();
        else {
            File temp = new File(curDir);

            curDir = temp.getParentFile().getAbsolutePath();

            refreshListAndDisplayWithCurDir(false);
        }
    }

    @Override
    public void onClick(View v2) {
        Log.d("Selected1: ", " ");

        SparseBooleanArray a = v.getCheckedItemPositions();

        if (a == null || a.size() == 0)
            return;

        int numChecked = 0;
        for (int i = 0; i < options.length; i++)
            if (a.get(i)) {
                numChecked++;
            }
        Log.d("Selected1: ", a.size() + " num Checkd : " + v.getCheckedItemCount());
        Toast.makeText(this, v.getCheckedItemCount() + " items checked or " + numChecked, Toast.LENGTH_SHORT).show();

        forUpdating = new String[2 * numChecked];
        int countForUpdating = 0;

        LockedEntryDb entry = new LockedEntryDb(this);
        entry.open();
        for (int i = 0; i < options.length; i++)
            if (a.get(i)) {
                countForUpdating = LockAndAddForUpdating(curDir + "/" + options[i], countForUpdating, i, entry);
            }
        entry.close();

        UpdateFiles();

        refreshListAndDisplayWithCurDir(false);
    }

    private void UpdateFiles() {
        MediaScannerConnection.scanFile(LockFiles.this,
                forUpdating,
                null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.d("scan: ", "ScanFinished ");

                    }
                });
    }

    private int LockAndAddForUpdating(String addr, int countForUpdating, int pos, LockedEntryDb entry) {

        File from = new File(addr);

        forUpdating[countForUpdating++] = curDir + "/" + options[pos];
        File to;
        String temp;
        if (options[pos].contains("_kush_")) {
            temp = curDir + "/" + options[pos];
            entry.delete(temp.replace("_kush_", "."));
            forUpdating[countForUpdating-1] = temp.replace("_kush_", ".");
        } else {
            temp = (curDir + "/" + options[pos]).replace(".", "_kush_");
        }
        entry.createEntry(temp, forUpdating[countForUpdating - 1]);
        to = new File(temp);

        if (!from.renameTo(to)) {
            Toast.makeText(this, "Unable to Complete the Operation!", Toast.LENGTH_SHORT).show();
        }
        Log.d("Selected: ", options[pos] + " " + temp);

        forUpdating[countForUpdating++] = temp;

        return countForUpdating;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        File temp = new File(curDir + "/" + options[position]);
        if (temp.isDirectory())
            return false;
        String selImage = curDir + "/" + options[position];
        Log.d("onClick: ", selImage);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + selImage), "image/*");
        startActivity(intent);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "LockFiles Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.kushank.filelocker/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "LockFiles Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.kushank.filelocker/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
