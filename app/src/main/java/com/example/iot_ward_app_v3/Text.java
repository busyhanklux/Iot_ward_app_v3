package com.example.iot_ward_app_v3;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;

public class Text extends AppCompatActivity {

    private EditText edt;
    private TextView txt;
    String name_all = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        edt = findViewById(R.id.editText);
        txt = findViewById(R.id.textView);

    }

    FirebaseDatabase database_get_name = FirebaseDatabase.getInstance();

    public void operate(View v) {
        File file = new File(getFilesDir(), "names.txt");
        switch (v.getId()) {
            case R.id.save_btn:
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    /*
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(edt.getText().toString().getBytes());
                    fos.close();
                     */
                    String name_temp = "";
                    for(int iaa = 1 ; iaa<=2 ; iaa++){
                        DatabaseReference get_name = database_get_name.getReference("environment").child(String.valueOf(iaa));
                        get_name.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    //得到"大型空間"
                                    String name = dataSnapshot.getValue(String.class);
                                    name_all = name_all + name;

                                    Toast hint = Toast.makeText(Text.this,"1_"+name_all,Toast.LENGTH_SHORT);
                                    hint.show();

                                } catch (Exception RSSI_not_found) {

                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });
                    }
                    //前面執行中的name_all是空的
                    //從對話框修改的name_all從有文字
                    AlertDialog.Builder icon_check = new AlertDialog.Builder(Text.this);
                    icon_check.setTitle("ABC");
                    icon_check.setIcon(R.drawable.logo4);
                    icon_check.setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast hint = Toast.makeText(Text.this,""+name_all,Toast.LENGTH_SHORT);
                            hint.show();
                            //找到那個輸出的檔案
                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(file);
                                //寫入
                                fos.write(name_all.toString().getBytes());
                                //fos關閉(結尾時必要)
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).show();

                    name_all = ""; //結束後，清空他

                } catch (Exception e) {
                    txt.setText("錯誤");
                }
                break;
            case R.id.read_btn:
                try {
                    FileInputStream fis = new FileInputStream(file);
                    byte[] b = new byte[1024];
                    int len = fis.read(b);
                    String str2 = new String(b, 0, len);
                    txt.setText(str2);
                } catch (Exception e) {
                    e.printStackTrace();
                    txt.setText("錯誤");
                }
                break;
        }
    }
}