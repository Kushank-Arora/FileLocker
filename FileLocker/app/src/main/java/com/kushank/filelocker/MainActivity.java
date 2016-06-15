package com.kushank.filelocker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.changePass).setOnClickListener(this);
        findViewById(R.id.lockFiles).setOnClickListener(this);
        findViewById(R.id.unlockFiles).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.changePass:
                startActivity(new Intent("COM.KUSHANK.CHANGEPASS"));
                break;
            case R.id.lockFiles:
                startActivity(new Intent("COM.KUSHANK.LOCKFILES"));
                break;
            case R.id.unlockFiles:
                startActivity(new Intent("COM.KUSHANK.UNLOCKFILES"));
                break;
        }
    }
}
