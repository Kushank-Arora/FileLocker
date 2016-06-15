package com.kushank.filelocker;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Kushank on 13-Jun-16.
 */
public class UnLockFiles extends Activity implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    ListView v;
    String result[][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.unlockfiles);
        v = (ListView) findViewById(R.id.lvUnlockFiles);
        findViewById(R.id.Unblock).setOnClickListener(this);

        refreshListAndDisplay(true);

        v.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        v.setOnItemLongClickListener(this);

    }

    private void refreshListAndDisplay(boolean first) {
        try {
            LockedEntryDb info = new LockedEntryDb(this);
            info.open();
            result = info.getData();
            info.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String sourceFileNames[] = new String[result.length];
        for (int i = 0; i < result.length; i++)
            sourceFileNames[i] = new File(result[i][1]).getName();

        v.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, sourceFileNames));

        if (!first)
            v.refreshDrawableState();
    }

    @Override
    public void onClick(View view) {

        Toast.makeText(this, "Unlocked Selected Files", Toast.LENGTH_SHORT).show();
        SparseBooleanArray a = v.getCheckedItemPositions();

        int numChecked = 0;
        for (int i = 0; i < result.length; i++)
            if (a != null && a.get(i)) {
                numChecked++;
            }
        String forUpdating[] = new String[2 * numChecked];
        int countForUpdating = 0;

        LockedEntryDb entry = new LockedEntryDb(this);
        entry.open();
        for (int i = 0; i < result.length; i++)
            if (a != null && a.get(i)) {

                File from = new File(result[i][0]);

                forUpdating[countForUpdating++] = result[i][0];
                File to;
                String temp;

                temp = result[i][0].replace("_kush_", ".");
                entry.delete(temp);

                to = new File(temp);
                from.renameTo(to);
                //Log.d("Selected: ", options[i]+ " "+temp);

                forUpdating[countForUpdating++] = temp;
            }
        entry.close();
        MediaScannerConnection.scanFile(this,
                forUpdating,
                // new String[]{"image/*"},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                        Log.d("scan: ", "ScanFinished ");
                    }
                });
        refreshListAndDisplay(false);
    }

    String lastCreated;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1111) {
            Log.d("UnLockFiles", (resultCode == RESULT_OK) + " ");
            Log.d("UnLockFiles", "Here");
            File dst = new File(lastCreated);
            if(!dst.delete())
                Log.d("Error!","Can't delete");
            MediaScannerConnection.scanFile(this, new String[]{dst.getAbsolutePath()}, null, null);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            File cur = new File(result[position][0]);
            File dst = new File(Environment.getExternalStorageDirectory() + "/"+cur.getName()+".jpg");
            lastCreated = dst.getAbsolutePath();
            copy(cur, dst);
            MediaScannerConnection.scanFile(this, new String[]{dst.getAbsolutePath()}, null,null);

            //for(int i=0;i<10000;i++);

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + dst.getAbsolutePath()), "image/*");
            startActivityForResult(intent, 1111);
            //startActivity(intent);

            Log.d("onLongClick: ", cur.getName());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void copy(File src, File dst) {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
