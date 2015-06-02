package com.xb.bubble.bubblelayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.bubblelayout.R;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

    public void onClick(View view){
        Intent intent = new Intent(this,XiaoMiRainActivity.class);
        startActivity(intent);
    }
}
