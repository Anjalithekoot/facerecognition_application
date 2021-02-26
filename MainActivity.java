package com.example.getattendence;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Notification;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;


public class MainActivity extends AppCompatActivity {

    LinearLayout parent;
    private List<Bitmap> bitmaps;
    private ImageView imageView;
    String names[];
    ListView my_list;

    @Override
    public <T extends View> T findViewById(int id) {
        return super.findViewById(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button picImage = (Button) findViewById(R.id.pickImage);
        picImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageView = (ImageView) findViewById(R.id.image_view);
            bitmaps = new ArrayList<>();
            ClipData clipData = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                clipData = data.getClipData();
            }
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    try {
                        InputStream is = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                       // Uri uri = data.getData();
                        bitmaps.add(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Uri imageUri = data.getData();
                try {
                    InputStream is = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                   // Uri uri = data.getData();
                    bitmaps.add(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
           new Thread(new Runnable() {
                @Override
                public void run() {
                    for (final Bitmap b : bitmaps) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(b);
                            }
                        });
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }
    public void connectServer(View v) {
        String postUrl= "http://192.168.1.12:5000/";

        MultipartBody.Builder buildernew = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        int i=0;
        for (Bitmap file : bitmaps) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            file.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            buildernew.addFormDataPart("image", "image"+(i++)+".jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));
        }

        RequestBody requestBody = buildernew.build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(postUrl)
                .post(requestBody)
                .build();

        TextView responseText = findViewById(R.id.responseText);
        responseText.setText("Please wait ...");


        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        responseText.setText("FAILED CONNECTION TO SERVER");
                    }
                });
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        responseText.setText("ABSENTEES LIST:");
                        parent =(LinearLayout) findViewById(R.id.linear_main);
                        my_list=(ListView) findViewById(R.id.listview);

                        try {
                            String str=response.body().string();
                            str = str.replaceAll("[^a-zA-Z,]","");
                            names=str.split(",");
                            final ArrayList<String> al = new ArrayList<String>();
                            my_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                            my_list.setAdapter(new ArrayAdapter<String>(MainActivity.this,R.layout.rowlayout,R.id.txt_lan,names));
                            Helper.getListViewSize(my_list);
                            my_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                    String selecteditem =((TextView)view).getText().toString();
                                    if(al.contains(selecteditem)){
                                        al.remove(selecteditem);//uncheck item
                                    }
                                    else {
                                        al.add(selecteditem);
                                    }
                                }
                            });
                            Button done = new Button(getApplicationContext());
                            done.setText("Done with Attendence");
                            parent.addView(done);
                            Button sub=new Button(getApplicationContext());
                            sub.setText("Update the Attendence");
                            parent.addView(sub);

                            done.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v)
                                {
                                    Toast.makeText(getApplicationContext(),"Attendance marked succesfully",Toast.LENGTH_LONG).show();
                                    ((ViewGroup) parent.getParent()).removeView(parent);
                                    TextView responseText = findViewById(R.id.responseText);
                                    responseText.setText("First pick the images and then connect to server");
                                }
                            });

                            sub.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    connectServer1(al);

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    public void connectServer1(ArrayList<String> str)  {
        JSONArray jsonArray = new JSONArray(str);
        JSONObject obj = new JSONObject();
        try {
            obj.put("x",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String mystring = obj.toString();

        String myurl1= "http://192.168.1.12:5001/";

        postRequest1(myurl1,mystring);
    }
    void postRequest1(final String posturl1, String mystring)  {


        OkHttpClient client = new OkHttpClient();
        final String string1 = mystring;

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON,string1);

        Request request = new Request.Builder()
                .addHeader("Content-Type","application/json")
                .url(posturl1)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        responseText.setText("something went wrong");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), string1+ " marked present", Toast.LENGTH_LONG).show();
                    }

                });
            }
        });

    }
}
