package com.lbf.magic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import com.lbf.com.terrymagic.R;

public class GuideActivity extends Activity {
    ArrayList<View> viewList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//remove title bar  即隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notification bar  即全屏
        setContentView(R.layout.activity_load);
        ViewPager viewpager= (ViewPager) findViewById(R.id.viewpager);
        View view1=getLayoutInflater().inflate(R.layout.loadaaa, null);
        View view2=getLayoutInflater().inflate(R.layout.loadbbb, null);
        View view3=getLayoutInflater().inflate(R.layout.inlayout, null);
        view3.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GuideActivity.this,FullscreenActivity.class);
                GuideActivity.this.startActivity(intent);
                GuideActivity.this.finish();
            }
        });

        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        viewpager.setAdapter(pagerAdapter);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int lastposition=-1;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                lastposition=position;
                System.out.println("onPageScrolled:"+position);
            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("onPageSelected:"+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                System.out.println("onPageScrollStateChanged:"+state);
                if (state==0){
//                    lastposition
                }
            }
        });
    }
    PagerAdapter pagerAdapter = new PagerAdapter() {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return viewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            // TODO Auto-generated method stub
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            container.addView(viewList.get(position));


            return viewList.get(position);
        }
    };
}
