package com.lbf.magic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lbf.com.terrymagic.R;

/**
 * Created by terry on 2017/5/6.
 */

public class NewGuideActivity extends Activity {
    ImageView imageView=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newguidlayout);

        
        imageView= (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NewGuideActivity.this,FullscreenActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
