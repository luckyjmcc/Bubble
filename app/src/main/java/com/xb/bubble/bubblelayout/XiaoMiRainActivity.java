package com.xb.bubble.bubblelayout;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.example.bubblelayout.R;

/**
 * Created by admin on 2015/5/21.
 */
public class XiaoMiRainActivity extends Activity implements SeekBar.OnSeekBarChangeListener ,RadioGroup.OnCheckedChangeListener{
    RainLayout rainLayout;
    SeekBar seekBar1, seekBar2, seekBar3, seekBar4, seekBar5 ;
    RadioGroup radioGroup1,radioGroup2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiaomi);
        rainLayout = (RainLayout) findViewById(R.id.rainLayout);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
        seekBar4 = (SeekBar) findViewById(R.id.seekBar4);
        seekBar5 = (SeekBar) findViewById(R.id.seekBar5);
        radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
        radioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
        seekBar1.setOnSeekBarChangeListener(this);
        seekBar2.setOnSeekBarChangeListener(this);
        seekBar1.setOnSeekBarChangeListener(this);
        seekBar4.setOnSeekBarChangeListener(this);
        seekBar5.setOnSeekBarChangeListener(this);
        radioGroup1.setOnCheckedChangeListener(this);
        radioGroup2.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        rainLayout.setPause(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        rainLayout.setPause(false);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekBar1:
                if(progress<0){
                    return ;
                }
                rainLayout.setRainwidth(progress);
                break;
            case R.id.seekBar2:
                rainLayout.setRainlength(progress);
                break;
            case R.id.seekBar4:
                rainLayout.setGradient(progress);
                break;
            case R.id.seekBar5:
                rainLayout.setSpeedY(progress);
                break;

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId){
            case R.id.radioButton1:
                rainLayout.setFrequence(RainLayout.Fequence.Level1);
                break;
            case R.id.radioButton2:
                rainLayout.setFrequence(RainLayout.Fequence.Level2);
                break;
            case R.id.radioButton3:
                rainLayout.setFrequence(RainLayout.Fequence.Level3);
                break;
            case R.id.radioButton4:
                rainLayout.setFrequence(RainLayout.Fequence.Level4);
                break;
            case R.id.radioButton5:
                rainLayout.setFrequence(RainLayout.Fequence.Level5);
                break;
            case R.id.radioButton6:
                rainLayout.setDirection(RainLayout.Direction.RIGHT2LEFT);
                break;
            case R.id.radioButton7:
                rainLayout.setDirection(RainLayout.Direction.LEFT2RIGHT);
                break;
        }
    }
}
