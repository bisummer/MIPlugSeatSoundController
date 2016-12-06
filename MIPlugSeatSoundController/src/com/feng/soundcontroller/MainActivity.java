
package com.feng.soundcontroller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    private EditText db, time;
    private Button sure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        SoundRecorder recorder = new SoundRecorder();
        recorder.getNoiseLevel();

        TimeQueueTask task = new TimeQueueTask();
        Thread t = new Thread(task);
        t.start();

    }

    private void initUI() {
        db = (EditText) findViewById(R.id.db);
        time = (EditText) findViewById(R.id.time);
        sure = (Button) findViewById(R.id.sure);
        sure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SoundRecorder.dBValue = Double.parseDouble(db.getText().toString());
                MyAccessibility.min = Double.parseDouble(time.getText().toString());
            }
        });

    }

}
