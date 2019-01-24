package com.shawn.aspectjdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.shawn.aspectjdemo.aspect.Terminate;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TaskRunner runner = new TaskRunner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runner.run();
                    }
                }).run();
            }
        });

        findViewById(R.id.btn_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runner.setTerminate(!runner.isTerminate());
            }
        });
    }
}
