package com.lbf.magic;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.lbf.com.terrymagic.R;

import java.util.ArrayList;
import java.util.Date;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends Activity implements AdapterView.OnItemClickListener,View.OnClickListener{
    RecyclerView recyclerView=null;
    RelativeLayout recy_layout;
    ArrayList<String> list=null;
    Myadapter myadapter=null;
    ImageView magic;
    ConstraintLayout cl=null;
    private int _xDelta;
    private int _yDelta;
    int bigwidth;//屏幕宽度
    int bigheight;//屏幕高度
    SensorManager sensorManager;
    Vibrator vibrator;
    int currentindex=-1;//当前选择的索引
    Bitmap bitmap;

    int WRITE_EXTERNAL_STORAGE_REQUEST_CODE=111;

    ShowcaseView button_add_showcaseView;
    ShowcaseView button_totaobao_showcaseView;
    ShowcaseView public_showcaseView;
    ShowcaseView set_showcaseView;
    ShowcaseView mainshowcase;
    ShowcaseView second_showcase;


    boolean showcaseset=false;


    Button sv_add_button=null;
    Button totaobao_button=null;
    Button public_button=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//remove title bar  即隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notification bar  即全屏
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_fullscreen);

        int hasWriteContactsPermission = FullscreenActivity.this.getPackageManager().checkPermission("android.permission.READ_EXTERNAL_STORAGE",getApplicationContext().getPackageName());
        if (PackageManager.PERMISSION_DENIED==hasWriteContactsPermission){
            Toast.makeText(this,"请打开应用的文件读取权限",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }

        recy_layout= (RelativeLayout) findViewById(R.id.recy_layout);
        recyclerView= (RecyclerView) findViewById(R.id.recy);
        magic= (ImageView) findViewById(R.id.magic);
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

        bigwidth = wm.getDefaultDisplay().getWidth();
        bigheight = wm.getDefaultDisplay().getHeight();
        findViewById(R.id.simple1).setOnClickListener(this);
        findViewById(R.id.simple2).setOnClickListener(this);
        findViewById(R.id.simple3).setOnClickListener(this);
        findViewById(R.id.simple4).setOnClickListener(this);
        findViewById(R.id.button_add).setOnClickListener(this);
        findViewById(R.id.button_totaobao).setOnClickListener(this);


        magic.setOnTouchListener(new View.OnTouchListener() {
            int lastx=0;
            int lasty=0;
            int imgwidth=0;
            int imgheight=0;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                RelativeLayout.LayoutParams lp= (RelativeLayout.LayoutParams) magic.getLayoutParams();

                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    lastx= (int) event.getRawX();
                    lasty= (int) event.getRawY();
                    imgwidth=lp.width;
                    imgheight=lp.height;
                    System.out.println("按下去");
                }else if (event.getAction()==MotionEvent.ACTION_MOVE){
                    if (((int)event.getRawX()<=bigwidth/10)||
                            ((int)event.getRawX()>=(bigwidth*9)/10)||
                            ((int)event.getRawY()<=bigheight/10)||
                            ((int)event.getRawY()>=(bigheight*9)/10)) {
                        magic.setVisibility(View.INVISIBLE);
                        System.out.println("消失");
                    }
                    int diffx=(int) event.getRawX()-lastx;
                    int diffy=(int) event.getRawY()-lasty;
                    lastx= (int) event.getRawX();
                    lasty= (int) event.getRawY();

                    int top=magic.getTop();
                    int bottom=magic.getBottom();
                    int left=magic.getLeft();
                    int right=magic.getRight();
                    top+=diffy;
                    bottom+=diffy;
                    left+=diffx;
                    right+=diffx;
                    magic.layout(left,top,right,bottom);
                    magic.invalidate();

                    System.out.println("移动");
                }else if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
                    System.out.println("移出");
                }
                return true;
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        list=new ArrayList<String>();
        readlistfile();
        myadapter=new Myadapter(list);
        recyclerView.setAdapter(myadapter);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (currentindex!=-1) {
            tihuanimage(list.get(currentindex));
        }
        isInitSet();
        initshowcase();
        String sss=read1load();
        if (sss==null){
            mainshowcase();
        }

    }


    public void mainshowcase(){
        Button movetip_button=new Button(this);
        movetip_button.setText("下一步");
        mainshowcase=new ShowcaseView.Builder(this).setTarget(new ViewTarget(findViewById(R.id.magic))).setContentText("手指按住屏幕上的魔术道具图片，移动到屏幕边缘时，图片消失").setContentTitle("滑动图片").replaceEndButton(movetip_button).build();

        movetip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainshowcase.hide();
                second_showcase();
            }
        });
    }

    public void second_showcase(){
        Button second_showcase_button=new Button(this);
        second_showcase_button.setText("下一步");
        second_showcase=new ShowcaseView.Builder(this).setTarget(new ViewTarget(findViewById(R.id.magic))).setContentText("摇动手机时，魔术道具图片重新显示在手机上，再次摇动手机将会使图片消失").setContentTitle("摇一摇手机").replaceEndButton(second_showcase_button).build();

        second_showcase_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                second_showcase.hide();
                set_showcase();
            }
        });
    }
    public void set_showcase(){
        Button set_showcase_button=new Button(this);
        set_showcase_button.setText("下一步");
        set_showcaseView=new ShowcaseView.Builder(this).setTarget(new ViewTarget(findViewById(R.id.magic))).setContentText("按音量加按键打开设置菜单，再次单音量加按键将会使菜单消失").setContentTitle("打开设置菜单").replaceEndButton(set_showcase_button).build();

        set_showcase_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeload1();
            }
        });
    }

    public void closeload1(){
        set_showcaseView.hide();
        write1load();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this,"无法读取文件，请打开应用的文件读取权限",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager !=null){
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    private SensorEventListener sensorEventListener = new SensorEventListener(){
        Date date=null;
        int i=1;
        @Override
        public void onSensorChanged(SensorEvent event) {


            float[] values = event.values;
            int medumValue = 15;
            if (Math.abs(values[0]) > medumValue || Math.abs(values[1]) > medumValue || Math.abs(values[2]) > medumValue) {
                Date date1=new Date();

                if (date!=null){
                    int aaaa= (int) ((date1.getTime()-date.getTime())/1000);
                    if (aaaa<1){
                        return;
                    }
                }
                date=date1;
                if(magic.getVisibility()==View.VISIBLE){
                    magic.setVisibility(View.INVISIBLE);
                }else {
                    magic.setVisibility(View.VISIBLE);
                }
                RelativeLayout.LayoutParams lp= (RelativeLayout.LayoutParams) magic.getLayoutParams();
                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                magic.setLayoutParams(lp);
                magic.invalidate();
                i++;
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void totaobao(){
        if (checkpackage()){
            Toast.makeText(this,"有淘宝",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            String url = "taobao://shop105107462.taobao.com/?spm=a230r.7195193.1997079397.2.w4saNH";
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            startActivity(intent);
        }else Toast.makeText(this,"没有淘宝",Toast.LENGTH_SHORT).show();
    }

    public boolean checkpackage(){
        try {
            this.getPackageManager().getApplicationInfo("com.taobao.taobao", PackageManager.GET_UNINSTALLED_PACKAGES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.simple1:{
                currentindex=-1;
                recy_layout.setVisibility(View.INVISIBLE);
                RelativeLayout.LayoutParams lp= (RelativeLayout.LayoutParams) magic.getLayoutParams();
                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                magic.setLayoutParams(lp);
                magic.setVisibility(View.VISIBLE);
                magic.invalidate();
                magic.setImageResource(R.drawable.money);
                break;
            }
            case R.id.simple2:{
                currentindex=-1;
                recy_layout.setVisibility(View.INVISIBLE);
                RelativeLayout.LayoutParams lp= (RelativeLayout.LayoutParams) magic.getLayoutParams();
                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                magic.setLayoutParams(lp);
                magic.setVisibility(View.VISIBLE);
                magic.invalidate();
                magic.setImageResource(R.drawable.example);
                break;
            }
            case R.id.simple3:{
                currentindex=-1;
                recy_layout.setVisibility(View.INVISIBLE);
                RelativeLayout.LayoutParams lp= (RelativeLayout.LayoutParams) magic.getLayoutParams();
                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                magic.setLayoutParams(lp);
                magic.setVisibility(View.VISIBLE);
                magic.invalidate();
                magic.setImageResource(R.drawable.simple3);
                break;
            }
            case R.id.simple4:{
                currentindex=-1;
                recy_layout.setVisibility(View.INVISIBLE);
                RelativeLayout.LayoutParams lp= (RelativeLayout.LayoutParams) magic.getLayoutParams();
                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                magic.setLayoutParams(lp);
                magic.setVisibility(View.VISIBLE);
                magic.invalidate();
                magic.setImageResource(R.drawable.simple4);
                break;
            }
            case R.id.button_add:{
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.putExtra("crop", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, 2);
                break;
            }
            case R.id.button_totaobao:{
                totaobao();
                break;
            }
        }
    }

    class Myadapter extends RecyclerView.Adapter<Myadapter.MyViewHolder>{

        int currtindex;
        ArrayList<String> list=null;
        public Myadapter(ArrayList<String> list){
            this.list=list;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    FullscreenActivity.this).inflate(R.layout.image, parent,
                    false));

            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds =true;
            Bitmap  bitmap=null;
            try {
                bitmap=BitmapFactory.decodeFile(list.get(position));
                if(bitmap==null){
                    bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.delete);
                }
            } catch (Exception e) {
                e.printStackTrace();
                bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.delete);
            }

            holder.imageView.setImageBitmap(bitmap);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recy_layout.setVisibility(View.INVISIBLE);
                    tihuanimage(list.get(position));
                    Toast.makeText(FullscreenActivity.this,"替换图片",Toast.LENGTH_SHORT).show();
                    RelativeLayout.LayoutParams lp= (RelativeLayout.LayoutParams) magic.getLayoutParams();
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    magic.setLayoutParams(lp);
                    magic.setVisibility(View.VISIBLE);
                    magic.invalidate();
                    currentindex=position;
                }
            });
            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (currentindex==position){
                        currentindex=-1;
                    }
                    list.remove(position);
                    myadapter.notifyDataSetChanged();
                    return false;
                }
            });
            currtindex=position;
        }



        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void onViewAttachedToWindow(MyViewHolder holder) {
            super.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(MyViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {

            ImageView imageView;

            public MyViewHolder(View view)
            {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.image);
            }
        }
    }
    public void tihuanimage(String path){
//            BitmapFactory.decodeFile(path);
        Bitmap bitmap2=null;

        try {
            bitmap2=BitmapFactory.decodeFile(path);
            if (bitmap2==null){
                bitmap2=BitmapFactory.decodeResource(getResources(),R.drawable.delete);
            }
        } catch (Exception e) {
            e.printStackTrace();
            bitmap2=BitmapFactory.decodeResource(getResources(),R.drawable.delete);
        }

        magic.setImageBitmap(bitmap2);
        if (bitmap!=null){
            bitmap.recycle();;
        }
        bitmap=bitmap2;
        magic.invalidate();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode==2){
                int hasWriteContactsPermission = FullscreenActivity.this.getPackageManager().checkPermission("android.permission.READ_EXTERNAL_STORAGE",getApplicationContext().getPackageName());
//                        FullscreenActivity.this.getApplicationContext().checkCallingPermission("android.permission.READ_EXTERNAL_STORAGE");

                if (PackageManager.PERMISSION_DENIED==hasWriteContactsPermission){
                    Toast.makeText(this,"请打开应用的文件读取权限",Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                }
                String path=getPath(FullscreenActivity.this,data.getData());
                list.add(path);
                myadapter.notifyDataSetChanged();
                System.out.println();
            }else {
                System.out.println();
            }
        }


    }

    public  String getPath( Context context,  Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
            if (read1load()==null){
                if (set_showcaseView!=null){
                    if (set_showcaseView.isShowing()){
                        closeload1();
                    }
                }
            }
            if (read1load()==null)return super.onKeyDown(keyCode,event);
            if (showcaseset)return super.onKeyDown(keyCode,event);
            Toast.makeText(this,"音量加",Toast.LENGTH_SHORT).show();
            if (recy_layout.getVisibility()==View.INVISIBLE){
                recy_layout.setVisibility(View.VISIBLE);
                if (readload()==null){
                    showcase_set();
                    showcaseset=true;
                    writeload();
                }
            }
            else {
                recy_layout.setVisibility(View.INVISIBLE);
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    public void readlistfile(){
        SharedPreferences sharedPreferences = getSharedPreferences("list", Context.MODE_PRIVATE); //私有数据
        String paths=sharedPreferences.getString("list","");
        currentindex=sharedPreferences.getInt("sharedPreferences",-1);
        if (paths.equals("")){
            return;
        }
        list=JSONObject.parseObject(paths,list.getClass());
        System.out.println();
    }

    public void writelistfile(){
        SharedPreferences sharedPreferences = getSharedPreferences("list", Context.MODE_PRIVATE); //私有数据
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("currentthread",currentindex);
        String paths=JSONArray.toJSONString(list);
        editor.putString("list",paths);
        editor.commit();
    }

    public void isInitSet(){
        SharedPreferences sharedPreferences = getSharedPreferences("showcase", Context.MODE_PRIVATE); //私有数据
        showcaseset=sharedPreferences.getBoolean("set",false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        writelistfile();
        if (sensorManager != null) {// 取消监听器
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    public void initshowcase(){
        sv_add_button=new Button(this);
        totaobao_button=new Button(this);
        public_button=new Button(this);

        sv_add_button.setText("下一步");
        totaobao_button.setText("下一步");
        public_button.setText("确定");
        TextPaint tp=new TextPaint();
        tp.setColor(Color.BLUE);
        tp.setTextSize(60);

        button_add_showcaseView=new ShowcaseView.Builder(this).setTarget(new ViewTarget(findViewById(R.id.button_add))).setContentText("您将进入到图库，添加个人专享魔术道具图片").setContentTitle("添加新魔术道具").replaceEndButton(sv_add_button).build();
        button_totaobao_showcaseView=new ShowcaseView.Builder(this).setTarget(new ViewTarget(findViewById(R.id.button_totaobao))).setContentText("您将进入到淘宝App，寻找专业设计师，设计您个人专享的魔术道具图片").replaceEndButton(totaobao_button).setContentTitle("寻找设计师").build();
        public_showcaseView=new ShowcaseView.Builder(this).setTarget(new ViewTarget(findViewById(R.id.simple_layout))).setContentText("此处为魔术道具库，点击道具即可替换魔术道具").replaceEndButton(public_button).setContentTitle("魔术道具库").build();

        button_add_showcaseView.hide();
        button_totaobao_showcaseView.hide();
        public_showcaseView.hide();
        sv_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_add_showcaseView.hide();
                button_totaobao_showcaseView.show();
            }
        });
        totaobao_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_totaobao_showcaseView.hide();
                public_showcaseView.show();
                showcaseset=false;
            }
        });
        public_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                public_showcaseView.hide();
            }
        });
    }

    public void showcase_set(){

        button_add_showcaseView.show();
        button_totaobao_showcaseView.hide();
        public_showcaseView.hide();


    }
    public String read1load(){
            SharedPreferences sharedPreferences=getSharedPreferences("load",Context.MODE_PRIVATE);
        return sharedPreferences.getString("load1",null);
    }
    public String readload(){
        SharedPreferences sharedPreferences=getSharedPreferences("load",Context.MODE_PRIVATE);
        return sharedPreferences.getString("load",null);
    }
    public void write1load(){
        SharedPreferences sharedPreferences=getSharedPreferences("load",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("load1","load");
        editor.commit();
    }
    public void writeload(){
        SharedPreferences sharedPreferences=getSharedPreferences("load",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("load","load");
        editor.commit();
    }
}
