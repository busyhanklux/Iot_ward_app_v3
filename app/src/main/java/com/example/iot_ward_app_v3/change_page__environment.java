package com.example.iot_ward_app_v3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class change_page__environment extends AppCompatActivity {

    private Button BT_CPE_back;
    TextView Hint,synchronize;
    TextView L_N_L,L_N_R,L_Nu_L,L_Nu_R,L_D_L,L_D_R,L_S_L,L_S_R;
    Spinner sp_look_CPE_name,sp_add_CPE_name,sp_change_CPE_name,sp_delete_CPE_name;
    Button  BT_look_CPE,BT_add_CPE,BT_change_CPE,BT_delete_CPE;

    Button  CPE_look_explain;

    EditText ed_add_CPE_name,ed_add_CPE_number;
    Spinner  sp_add_CPE_door,sp_add_CPE_strength;
    String   door_SP[]  = {"主要門口方向","1.左","2.右"};
    String   Strength[] = {"環境訊號概況","1.強","2.中","3.弱"};
    String   Add_door,Add_strength;

    String e_time = "";
    String e_name = "";
    String e_door = "";
    String e_number = "";
    String e_list = "";
    String format = "";

    Toast hint;

    String room_place_number; //房間編號
    int    room_place_list;

    ArrayList E_room_name = new ArrayList();

    long environment_Time; //時間比較，減少傳輸浪費
    int environment_number; //確認有幾個環境，會影響迴圈次數
    int check_for_environment_text;

    int run_count;
    long time_now = System.currentTimeMillis() / 1000;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_page__environment);

        File environment_txt_time  = new File(getFilesDir(), "environment_time.txt");   //時間
        File environment_txt_door  = new File(getFilesDir(), "environment_door.txt");   //門口
        File environment_txt_name  = new File(getFilesDir(), "environment_name.txt");   //環境名稱
        File environment_txt_number= new File(getFilesDir(), "environment_number.txt"); //環境總數
        File environment_txt_list  = new File(getFilesDir(), "environment_list.txt");   //環境的數字代碼


        Hint = (TextView)findViewById(R.id.Hint);
        synchronize = (TextView)findViewById(R.id.synchronize);

        //查看的所有Text
        L_N_L = findViewById(R.id.L_N_L);
        L_N_R = findViewById(R.id.L_N_R);
        L_Nu_L = findViewById(R.id.L_Nu_L);
        L_Nu_R = findViewById(R.id.L_Nu_R);
        L_D_L = findViewById(R.id.L_D_L);
        L_D_R = findViewById(R.id.L_D_R);
        L_S_L = findViewById(R.id.L_S_L);
        L_S_R = findViewById(R.id.L_S_R);

        //新增
        ed_add_CPE_name = findViewById(R.id.ed_add_CPE_name);
        ed_add_CPE_number = findViewById(R.id.ed_add_CPE_number);

        //回上頁
        BT_CPE_back = findViewById(R.id.BT_CPE_back);
        BT_CPE_back.setOnClickListener(BT_CPE_back_L);
        //查看
        BT_look_CPE = findViewById(R.id.BT_look_CPE);
        BT_look_CPE.setOnClickListener(BT_look_CPE_L);
        CPE_look_explain = findViewById(R.id.CPE_look_explain);
        CPE_look_explain.setOnClickListener(CPE_look_explain_L);
        //新增
        BT_add_CPE = findViewById(R.id.BT_add_CPE);
        BT_add_CPE.setOnClickListener(BT_add_CPE_L);
        //修改
        BT_change_CPE = findViewById(R.id.BT_change_CPE);
        BT_change_CPE.setOnClickListener(BT_change_CPE_L);
        //刪除
        BT_delete_CPE = findViewById(R.id.BT_delete_CPE);
        BT_delete_CPE.setOnClickListener(BT_delete_CPE_L);

        sp_look_CPE_name   = (Spinner)findViewById(R.id.sp_look_CPE_name);
        sp_add_CPE_door    = (Spinner)findViewById(R.id.sp_add_CPE_door);
        sp_add_CPE_strength= (Spinner)findViewById(R.id.sp_add_CPE_strength);
        sp_change_CPE_name = (Spinner)findViewById(R.id.sp_change_CPE_name);
        sp_delete_CPE_name = (Spinner)findViewById(R.id.sp_delete_CPE_name);

        ArrayAdapter<String> sp_add_CPE_door_S =
                new ArrayAdapter<String>(change_page__environment.this,R.layout.spinner_value_choice_color,door_SP);
        sp_add_CPE_door_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_add_CPE_door.setAdapter(sp_add_CPE_door_S); //設定資料來源
        sp_add_CPE_door.setOnItemSelectedListener(sp_add_CPE_door_L);

        ArrayAdapter<String> sp_add_CPE_strength_S =
                new ArrayAdapter<String>(change_page__environment.this,R.layout.spinner_value_choice_color,Strength);
        sp_add_CPE_strength_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_add_CPE_strength.setAdapter(sp_add_CPE_strength_S); //設定資料來源
        sp_add_CPE_strength.setOnItemSelectedListener(sp_add_CPE_strength_L);

        //減少傳輸浪費
        try {
            //讀檔案
            FileInputStream fis = new FileInputStream(environment_txt_time);
            byte[] b = new byte[1024];
            int len = fis.read(b);

            //從文字檔獲取時間(環境)
            environment_Time = Long.parseLong(new String(b, 0, len));

        } catch (Exception e) {
            e.printStackTrace();
        }

        //數量
        FirebaseDatabase database_environment_name = FirebaseDatabase.getInstance();
        DatabaseReference firebase_number_check = database_environment_name.getReference("environment").child("number");
        firebase_number_check.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int number = dataSnapshot.getValue(int.class);
                environment_number = number;
                Hint.setText(number+"");

            }
            @Override
            public void onCancelled(DatabaseError error) { }
        });

        //抓取資料庫的資料
        DatabaseReference firebase_time_check = database_environment_name.getReference("environment").child("change_time");
        firebase_time_check.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot_E) {

                //E，從資料庫抓取的數值
                long E_change_time = dataSnapshot_E.getValue(long.class);

                //改動他
                e_time = e_time + time_now;

                //寫入
                try {
                    FileOutputStream ETT = null;
                    ETT = new FileOutputStream(environment_txt_time);
                    ETT.write(e_time.getBytes());
                    ETT.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast hint = Toast.makeText(change_page__environment.this, e_time+"",Toast.LENGTH_SHORT);
                //hint.show();
                check_for_environment_text = 1;

                //需要改動
                if (check_for_environment_text == 1) {
                    //Toast hint = Toast.makeText(Splash_Screen.this, "他需要被變動",Toast.LENGTH_SHORT);
                    //hint.show();

                    FileOutputStream fos = null;
                    try {
                        //寫入
                        fos = new FileOutputStream(environment_txt_time);
                        fos.write(e_time.getBytes());
                        fos.close();

                        //環境數量
                        FirebaseDatabase database_environment_name = FirebaseDatabase.getInstance();
                        DatabaseReference environment_number_check = database_environment_name.getReference("environment").child("number");
                        environment_number_check.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot Data_E_number) {

                                int E_number = Data_E_number.getValue(int.class);

                                //小的是String，大的是int
                                e_number = e_number + E_number;

                                //寫入有幾個環境
                                try {
                                    FileOutputStream E_T_T = null;

                                    E_T_T = new FileOutputStream(environment_txt_number);
                                    E_T_T.write(e_number.getBytes());
                                    E_T_T.close();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast hint = Toast.makeText(change_page__environment.this, "E_number錯誤",Toast.LENGTH_SHORT);
                                    hint.show();
                                }

                                //處理編號list(1 2 3)
                                DatabaseReference environment_list_check = database_environment_name.getReference("environment").child("list");
                                environment_list_check.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot Data_E_list) {

                                        String E_list = Data_E_list.getValue(String.class);
                                        String []E_multilist = E_list.split(" ");

                                        //寫入有幾個環境
                                        try {
                                            FileOutputStream E_L = null;

                                            E_L = new FileOutputStream(environment_txt_list);
                                            E_L.write(E_list.getBytes());
                                            E_L.close();

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Toast hint = Toast.makeText(change_page__environment.this, "E_list錯誤",Toast.LENGTH_SHORT);
                                            hint.show();
                                        }

                                        run_count = 0;

                                        ArrayList<String> room_name = new ArrayList<String>();
                                        room_name.add("請選擇環境");

                                        for(int i = 0; i < E_multilist.length; i++){

                                            run_count++;

                                            //環境名稱
                                            DatabaseReference environment_name_check = database_environment_name.getReference("environment").child(String.valueOf(E_multilist[i]));

                                            String add_number = E_multilist[i]; //取得資料庫之list的數字

                                            environment_name_check.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot Data_E_name) {

                                                    String E_name = Data_E_name.getValue(String.class);
                                                    e_name = e_name + E_name + " ";

                                                    room_name.add(add_number+". "+E_name); //(左)位置，(右)名稱，1. ____ 2. ____

                                                    //Toast hint = Toast.makeText(change_page__environment.this, add_number +"",Toast.LENGTH_SHORT);
                                                    //hint.show();

                                                    if (run_count == E_number)
                                                    {
                                                        FileOutputStream E_N_N = null;
                                                        try {
                                                            E_N_N = new FileOutputStream(environment_txt_name);
                                                            E_N_N.write(e_name.getBytes());
                                                            E_N_N.close();

                                                            //spinner相關，你需要一個xml來調整大小(把android拿掉
                                                            //Spinner(sp_look_CPE_name_S)
                                                            ArrayAdapter<String> sp_look_CPE_name_S =
                                                                    new ArrayAdapter<String>(change_page__environment.this,R.layout.spinner_value_choice_color,room_name);

                                                            sp_look_CPE_name_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                            sp_look_CPE_name.setAdapter(sp_look_CPE_name_S); //設定資料來源
                                                            sp_look_CPE_name.setOnItemSelectedListener(sp_look_CPE_name_L);

                                                            //Spinner(sp_change_CPE_name_S)
                                                            ArrayAdapter<String> sp_change_CPE_name_S =
                                                                    new ArrayAdapter<String>(change_page__environment.this,R.layout.spinner_value_choice_color,room_name);

                                                            sp_change_CPE_name_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                            sp_change_CPE_name.setAdapter(sp_look_CPE_name_S); //設定資料來源

                                                            //Spinner(sp_delete_CPE_name_S)
                                                            ArrayAdapter<String> sp_delete_CPE_name_S =
                                                                    new ArrayAdapter<String>(change_page__environment.this,R.layout.spinner_value_choice_color,room_name);

                                                            sp_delete_CPE_name_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                            sp_delete_CPE_name.setAdapter(sp_look_CPE_name_S); //設定資料來源

                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError error) { }
                                            });
                                        }

                                        //門的方向
                                        DatabaseReference environment_door_check = database_environment_name.getReference("environment").child("door");
                                        environment_door_check.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot Data_E_door) {

                                                String E_door = Data_E_door.getValue(String.class);
                                                e_door = e_door + E_door;

                                                if (run_count == E_number) {
                                                    FileOutputStream E_D_D = null; //Let them come(X)
                                                    try {
                                                        E_D_D = new FileOutputStream(environment_txt_door);
                                                        E_D_D.write(e_door.getBytes());
                                                        E_D_D.close();

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                            }
                                        });
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError error) { }
                                });
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                    check_for_environment_text = 2;

                    long time_now_fix = time_now*1000;
                    Date day_month_year = new Date(time_now_fix);
                    String format = new SimpleDateFormat("yyyy/MM/dd ahh:mm:ss").format(day_month_year);
                    synchronize.setText(format);

                }

                //全數完成
                //檢查完畢 或 不需要改動
                if (check_for_environment_text == 2) {

                }

            }
            @Override
            public void onCancelled(DatabaseError error) { }
        });

    }

    public View.OnClickListener BT_CPE_back_L = view ->
    {

        Intent intent = new Intent();
        intent.setClass(change_page__environment.this,adminster_page.class);
        startActivity(intent);
        finish();
    };

    //看看該環境的設定
    Spinner.OnItemSelectedListener sp_look_CPE_name_L = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int look_CPE, long id) {
            //Toast hint = Toast.makeText(change_page__environment.this, parent.getItemAtPosition(look_CPE).toString()+"",Toast.LENGTH_SHORT);
            //hint.show();

            //剛才的設定：1. 2. 3. ... 11.
            //個位數時，空白是第3位，十位數則是第4位

            String room_place = parent.getItemAtPosition(look_CPE).toString(); //取得文字

            //剔除請選擇環境
            if (look_CPE != 0)
            {
                //String.indexOf -> 回傳字串第一次出現的位置
                //個位數時，"."的index = 1，十位數則是2


                int room_place_number_dot = room_place.indexOf('.');
                room_place_number = room_place.substring(0,room_place_number_dot);
                room_place_list   = look_CPE;

                hint = Toast.makeText(change_page__environment.this, room_place.substring(0,room_place_number_dot)+"",Toast.LENGTH_SHORT);
                hint.show();
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};

    //查看
    public View.OnClickListener BT_look_CPE_L = view ->
    {
        FirebaseDatabase database_environment_name = FirebaseDatabase.getInstance();
        DatabaseReference firebase_name_find = database_environment_name.getReference("environment").child(room_place_number);
        firebase_name_find.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot_1) {

                String name = dataSnapshot_1.getValue(String.class);
                L_N_R.setText(name);
                L_Nu_R.setText(room_place_number);

                DatabaseReference firebase_list_find = database_environment_name.getReference("environment").child("door");
                firebase_list_find.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot_2) {

                        String E_door = dataSnapshot_2.getValue(String.class);

                        hint = Toast.makeText(change_page__environment.this, room_place_list+"",Toast.LENGTH_SHORT);
                        hint.show();

                        for(int i = 0; i < E_door.length(); i++)
                        {
                            if((room_place_list-1) == i)
                            {
                                hint = Toast.makeText(change_page__environment.this, E_door.substring(i,i+1)+"",Toast.LENGTH_SHORT);
                                hint.show();

                                if(E_door.substring(i, i + 1).equals("1")) { L_D_R.setText("左側"); } //左
                                if(E_door.substring(i, i + 1).equals("2")) { L_D_R.setText("右側"); } //右
                            }
                        }

                        DatabaseReference firebase_number_check = database_environment_name.getReference("environment").child("strength");
                        firebase_number_check.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot_3) {

                                String E_strength = dataSnapshot_3.getValue(String.class);

                                for(int i = 0; i < E_door.length(); i++)
                                {
                                    if((room_place_list-1) == i)
                                    {
                                        if(E_strength.substring(i, i + 1).equals("1")) { L_S_R.setText("強"); } //強
                                        if(E_strength.substring(i, i + 1).equals("2")) { L_S_R.setText("中"); } //中
                                        if(E_strength.substring(i, i + 1).equals("3")) { L_S_R.setText("弱"); } //弱
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });
                    }
                    @Override
                    public void onCancelled(DatabaseError error) { }
                });

            }
            @Override
            public void onCancelled(DatabaseError error) { }
        });
    };

    public View.OnClickListener CPE_look_explain_L = view ->
    {
        AlertDialog.Builder explain = new AlertDialog.Builder(change_page__environment.this);
        explain.setTitle("說明");
        explain.setIcon(R.drawable.logo4);
        explain.setMessage("門口方向：人從走道朝向房間的門之方向" +"\n" + "環境訊號：根據房間的不同，訊號應不同");
        explain.setPositiveButton("關閉", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    };

    Spinner.OnItemSelectedListener sp_add_CPE_door_L = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int add_CPE_D, long id) {

            //剔除請選擇
            if (add_CPE_D != 0)
            {
                Add_door  = String.valueOf(add_CPE_D);

                hint = Toast.makeText(change_page__environment.this, Add_door+"",Toast.LENGTH_SHORT);
                hint.show();
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};

    Spinner.OnItemSelectedListener sp_add_CPE_strength_L = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int add_CPE_S, long id) {

            //剔除請選擇
            if (add_CPE_S != 0)
            {
                Add_strength = String.valueOf(add_CPE_S);

                hint = Toast.makeText(change_page__environment.this, Add_strength+"",Toast.LENGTH_SHORT);
                hint.show();
            }

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};


    public View.OnClickListener BT_add_CPE_L = view ->
    {
        String ed_add_CPE_name_C = ed_add_CPE_name.getText().toString();
        String ed_add_CPE_number_C = ed_add_CPE_number.getText().toString();

        if ((Add_door != null) && (Add_strength != null) && (!ed_add_CPE_name_C.equals("")) && (!ed_add_CPE_number_C.equals(""))) {

            AlertDialog.Builder ADD = new AlertDialog.Builder(change_page__environment.this);
            ADD.setCancelable(false);
            ADD.setTitle("再次確認");
            ADD.setIcon(R.drawable.logo4);
            ADD.setMessage("是否確認新增?"+"\n"+"新增後如有輸入失誤，請藉由修改調整" + "\n"+ "或使用刪除處理失誤之輸入");

            ADD.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    FirebaseDatabase database_environment_ADD = FirebaseDatabase.getInstance();
                    DatabaseReference firebase_name_ADD = database_environment_ADD.getReference("environment"); //選擇母位置

                    firebase_name_ADD.child(ed_add_CPE_number_C).setValue(ed_add_CPE_name_C); //在分支加入資料

                    hint = Toast.makeText(change_page__environment.this, "確定",Toast.LENGTH_SHORT);
                    hint.show();
                    dialog.dismiss();
                }
            });
            ADD.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    hint = Toast.makeText(change_page__environment.this, "取消",Toast.LENGTH_SHORT);
                    hint.show();
                    dialog.dismiss();
                }
            });

            AlertDialog dialog_ADD = ADD.create();
            dialog_ADD.show();
        }

        if ((Add_door == null) || (Add_strength == null) || (ed_add_CPE_name_C.equals("")) || (ed_add_CPE_number_C.equals(""))) {

            if (ed_add_CPE_name_C.equals("")){

                hint = Toast.makeText(change_page__environment.this, "你尚未輸入環境名稱",Toast.LENGTH_SHORT);
                hint.show();
            }

            if (ed_add_CPE_number_C.equals("")){

                hint = Toast.makeText(change_page__environment.this, "你尚未輸入環境編號",Toast.LENGTH_SHORT);
                hint.show();
            }

            if (Add_door == null){

                hint = Toast.makeText(change_page__environment.this, "你尚未選擇門口方向",Toast.LENGTH_SHORT);
                hint.show();
            }

            if (Add_strength == null){

                hint = Toast.makeText(change_page__environment.this, "你尚未選擇訊號概況",Toast.LENGTH_SHORT);
                hint.show();
            }

        }
    };

    public View.OnClickListener BT_change_CPE_L = view ->
    {

    };

    public View.OnClickListener BT_delete_CPE_L = view ->
    {

    };
}
