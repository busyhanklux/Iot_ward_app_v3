package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Dictionary;
import java.util.Hashtable;

public class Splash_Screen extends AppCompatActivity {

    Toast hint;

    String e_name = "";
    String e_door = "";
    String e_number = "";
    String e_list = "";
    String e_strength = "";

    String d_name = "";
    String d_number = "";
    String d_list = "";

    int check_time_for_text;
    int check_time_for_device_text;
    int check_time_for_environment_text;
    //1需要被更動，2是改完了
    int run_count, run_count_D;

    long time_now = System.currentTimeMillis() / 1000;

    //把資料預載放這
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        //流程，先檢查 device_txt_number 和 environment_txt_number  是否存在

        File device_txt_name = new File(getFilesDir(), "device_name.txt");   //設備名稱
        File device_txt_number = new File(getFilesDir(), "device_number.txt"); //設備數量
        File device_txt_list = new File(getFilesDir(), "device_list.txt");   //設備的數字代碼

        File environment_txt_door = new File(getFilesDir(), "environment_door.txt");   //門口
        File environment_txt_name = new File(getFilesDir(), "environment_name.txt");   //環境名稱
        File environment_txt_number = new File(getFilesDir(), "environment_number.txt"); //環境總數
        File environment_txt_list = new File(getFilesDir(), "environment_list.txt");   //環境的數字代碼
        File environment_txt_strength = new File(getFilesDir(), "environment_strength.txt");   //環境的強度代碼

        //如果兩個檔案都存在，跑這段
        if (device_txt_number.exists() && environment_txt_number.exists()) {
            //D
            //設備號碼的檢查
            FirebaseDatabase database_device_name = FirebaseDatabase.getInstance();
            //環境數量
            DatabaseReference device_number_check = database_device_name.getReference("device").child("number");
            device_number_check.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot Data_D_number) {

                    int D_number = Data_D_number.getValue(int.class);

                    //小的是String，大的是int
                    d_number = d_number + D_number;

                    //寫入有幾個環境
                    try {
                        FileOutputStream D_N_N = null;

                        D_N_N = new FileOutputStream(device_txt_number);
                        D_N_N.write(d_number.getBytes());
                        D_N_N.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast hint = Toast.makeText(Splash_Screen.this, "D_number錯誤", Toast.LENGTH_SHORT);
                        hint.show();
                    }

                    //處理編號list(1 2 3)
                    DatabaseReference device_list_check = database_device_name.getReference("device").child("list");
                    device_list_check.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot Data_D_list) {

                            String D_list = Data_D_list.getValue(String.class);
                            String[] D_multilist = D_list.split(" ");

                            //寫入有幾個環境
                            try {
                                FileOutputStream D_L = null;

                                D_L = new FileOutputStream(device_txt_list);
                                D_L.write(D_list.getBytes());
                                D_L.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast hint = Toast.makeText(Splash_Screen.this, "D_list錯誤", Toast.LENGTH_SHORT);
                                hint.show();
                            }

                            run_count_D = 0;
                            for (int i = 0; i < D_multilist.length; i++) {

                                run_count_D++;

                                //環境名稱
                                DatabaseReference device_name_check = database_device_name.getReference("device").child(String.valueOf(D_multilist[i]));
                                device_name_check.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot Data_D_name) {

                                        String D_name = Data_D_name.getValue(String.class);
                                        d_name = d_name + D_name + " ";

                                        if (run_count_D == D_number) {
                                            FileOutputStream D_N_N = null;
                                            try {
                                                D_N_N = new FileOutputStream(device_txt_name);
                                                D_N_N.write(d_name.getBytes());
                                                D_N_N.close();

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError error) { }});
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError error) { }});
                }
                @Override
                public void onCancelled(DatabaseError error) { }});

            //改動他
            check_time_for_device_text = 2;

            //---------------------------------------|-----------------------------
            //E
            //環境的檢查
            FirebaseDatabase database_environment_name = FirebaseDatabase.getInstance();

            //改動他
            check_time_for_environment_text = 1;

            //環境數量
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
                        Toast hint = Toast.makeText(Splash_Screen.this, "E_number錯誤", Toast.LENGTH_SHORT);
                        hint.show();
                    }

                    //處理編號list(1 2 3)
                    DatabaseReference environment_list_check = database_environment_name.getReference("environment").child("list");
                    environment_list_check.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot Data_E_list) {

                            String E_list = Data_E_list.getValue(String.class);
                            String[] E_multilist = E_list.split(" ");

                            //寫入有幾個環境
                            try {
                                FileOutputStream E_L = null;

                                E_L = new FileOutputStream(environment_txt_list);
                                E_L.write(E_list.getBytes());
                                E_L.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast hint = Toast.makeText(Splash_Screen.this, "E_list錯誤", Toast.LENGTH_SHORT);
                                hint.show();
                            }

                            run_count = 0;
                            for (int i = 0; i < E_multilist.length; i++) {

                                run_count++;

                                //環境名稱
                                DatabaseReference environment_name_check = database_environment_name.getReference("environment").child(String.valueOf(E_multilist[i]));
                                environment_name_check.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot Data_E_name) {

                                        String E_name = Data_E_name.getValue(String.class);
                                        e_name = e_name + E_name + " ";

                                        if (run_count == E_number) {
                                            FileOutputStream E_N_N = null;
                                            try {
                                                E_N_N = new FileOutputStream(environment_txt_name);
                                                E_N_N.write(e_name.getBytes());
                                                E_N_N.close();

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError error) { }});
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
                                public void onCancelled(DatabaseError error) { }});

                            //強度
                            DatabaseReference environment_strength_check = database_environment_name.getReference("environment").child("strength");
                            environment_strength_check.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot Data_E_strength) {

                                    String E_strength = Data_E_strength.getValue(String.class);
                                    e_strength = e_strength + E_strength;

                                    if (run_count == E_number) {
                                        FileOutputStream E_S_S = null; //Let them come(X)
                                        try {
                                            E_S_S = new FileOutputStream(environment_txt_strength);
                                            E_S_S.write(e_strength.getBytes());
                                            E_S_S.close();

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError error) { }});
                        }
                        @Override
                        public void onCancelled(DatabaseError error) { }});
                }
                @Override
                public void onCancelled(DatabaseError error) { }});

            check_time_for_environment_text = 2;

            //全數完成
            //檢查完畢 或 不需要改動
            if ((check_time_for_environment_text == 2) && (check_time_for_device_text == 2)) {

                Intent splash_to_main = new Intent();
                splash_to_main.setClass(Splash_Screen.this, MainActivity.class);
                startActivity(splash_to_main);
                finish();
            }
        }


        //如果 device_txt_number (存在) 但 environment_txt_number (不存在)，跑這段

        if (device_txt_number.exists() && !environment_txt_number.exists()) {
            //D
            //改動D
            //設備號碼的檢查
            FirebaseDatabase database_device_name = FirebaseDatabase.getInstance();
            //環境數量
            DatabaseReference device_number_check = database_device_name.getReference("device").child("number");
            device_number_check.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot Data_D_number) {

                    int D_number = Data_D_number.getValue(int.class);

                    //小的是String，大的是int
                    d_number = d_number + D_number;

                    //寫入有幾個環境
                    try {
                        FileOutputStream D_N_N = null;

                        D_N_N = new FileOutputStream(device_txt_number);
                        D_N_N.write(d_number.getBytes());
                        D_N_N.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast hint = Toast.makeText(Splash_Screen.this, "D_number錯誤", Toast.LENGTH_SHORT);
                        hint.show();
                    }

                    //處理編號list(1 2 3)
                    DatabaseReference device_list_check = database_device_name.getReference("device").child("list");
                    device_list_check.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot Data_D_list) {

                            String D_list = Data_D_list.getValue(String.class);
                            String[] D_multilist = D_list.split(" ");

                            //寫入有幾個環境
                            try {
                                FileOutputStream D_L = null;

                                D_L = new FileOutputStream(device_txt_list);
                                D_L.write(D_list.getBytes());
                                D_L.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast hint = Toast.makeText(Splash_Screen.this, "D_list錯誤", Toast.LENGTH_SHORT);
                                hint.show();
                            }

                            run_count_D = 0;
                            for (int i = 0; i < D_multilist.length; i++) {

                                run_count_D ++;

                                //環境名稱
                                DatabaseReference device_name_check = database_device_name.getReference("device").child(String.valueOf(D_multilist[i]));
                                device_name_check.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot Data_D_name) {

                                        String D_name = Data_D_name.getValue(String.class);
                                        d_name = d_name + D_name + " ";

                                        if (run_count_D == D_number) {
                                            FileOutputStream D_N_N = null;
                                            try {
                                                D_N_N = new FileOutputStream(device_txt_name);
                                                D_N_N.write(d_name.getBytes());
                                                D_N_N.close();

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError error) { }});
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError error) { }});
                }
                @Override
                public void onCancelled(DatabaseError error) { }});

            //改動他
            check_time_for_device_text = 2;
            //------------------------------------------------------------------------

                //檢查完畢 或 不需要改動 ，E
                if (check_time_for_device_text == 2) {

                    //寫入環境
                    try {

                        environment_txt_number.createNewFile(); //環境數量
                        environment_txt_list.createNewFile();
                        environment_txt_name.createNewFile();   //環境名稱
                        environment_txt_door.createNewFile();
                        environment_txt_strength.createNewFile();

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
                                    Toast hint = Toast.makeText(Splash_Screen.this, "E_number錯誤", Toast.LENGTH_SHORT);
                                    hint.show();
                                }

                                //處理編號list(1 2 3)
                                DatabaseReference environment_list_check = database_environment_name.getReference("environment").child("list");
                                environment_list_check.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot Data_E_list) {

                                        String E_list = Data_E_list.getValue(String.class);
                                        String[] E_multilist = E_list.split(" ");

                                        //寫入有幾個環境
                                        try {
                                            FileOutputStream E_L = null;

                                            E_L = new FileOutputStream(environment_txt_list);
                                            E_L.write(E_list.getBytes());
                                            E_L.close();

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Toast hint = Toast.makeText(Splash_Screen.this, "E_list錯誤", Toast.LENGTH_SHORT);
                                            hint.show();
                                        }

                                        run_count = 0;
                                        for (int i = 0; i < E_multilist.length; i++) {

                                            run_count++;

                                            //環境名稱
                                            DatabaseReference environment_name_check = database_environment_name.getReference("environment").child(String.valueOf(E_multilist[i]));
                                            environment_name_check.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot Data_E_name) {

                                                    String E_name = Data_E_name.getValue(String.class);
                                                    e_name = e_name + E_name + " ";

                                                    if (run_count == E_number) {
                                                        FileOutputStream E_N_N = null;
                                                        try {
                                                            E_N_N = new FileOutputStream(environment_txt_name);
                                                            E_N_N.write(e_name.getBytes());
                                                            E_N_N.close();

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

                                        //強度
                                        DatabaseReference environment_strength_check = database_environment_name.getReference("environment").child("strength");
                                        environment_strength_check.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot Data_E_strength) {

                                                String E_strength = Data_E_strength.getValue(String.class);
                                                e_strength = e_strength + E_strength;

                                                if (run_count == E_number) {
                                                    FileOutputStream E_S_S = null; //Let them come(X)
                                                    try {
                                                        E_S_S = new FileOutputStream(environment_txt_strength);
                                                        E_S_S.write(e_strength.getBytes());
                                                        E_S_S.close();

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError error) { }});
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) { }});
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                    Intent splash_to_main = new Intent();
                    splash_to_main.setClass(Splash_Screen.this, MainActivity.class);
                    startActivity(splash_to_main);
                    finish();
                }
            }

            //如果 device_txt_number (不存在) 但 environment_txt_number (存在)，跑這段

            if (!device_txt_number.exists() && environment_txt_number.exists()) {

                try {
                    device_txt_number.createNewFile();
                    device_txt_list.createNewFile();
                    device_txt_name.createNewFile();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //設備號碼的檢查
                FirebaseDatabase database_device_name = FirebaseDatabase.getInstance();
                //環境數量
                DatabaseReference device_number_check = database_device_name.getReference("device").child("number");
                device_number_check.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot Data_D_number) {

                        int D_number = Data_D_number.getValue(int.class);

                        //小的是String，大的是int
                        d_number = d_number + D_number;

                        //寫入有幾個環境
                        try {
                            FileOutputStream D_N_N = null;

                            D_N_N = new FileOutputStream(device_txt_number);
                            D_N_N.write(d_number.getBytes());
                            D_N_N.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast hint = Toast.makeText(Splash_Screen.this, "D_number錯誤", Toast.LENGTH_SHORT);
                            hint.show();
                        }

                        //處理編號list(1 2 3)
                        DatabaseReference device_list_check = database_device_name.getReference("device").child("list");
                        device_list_check.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot Data_D_list) {

                                String D_list = Data_D_list.getValue(String.class);
                                String[] D_multilist = D_list.split(" ");

                                //寫入有幾個環境
                                try {
                                    FileOutputStream D_L = null;

                                    D_L = new FileOutputStream(device_txt_list);
                                    D_L.write(D_list.getBytes());
                                    D_L.close();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast hint = Toast.makeText(Splash_Screen.this, "D_list錯誤", Toast.LENGTH_SHORT);
                                    hint.show();
                                }

                                run_count_D = 0;
                                for (int i = 0; i < D_multilist.length; i++) {

                                    run_count_D++;

                                    //環境名稱
                                    DatabaseReference device_name_check = database_device_name.getReference("device").child(String.valueOf(D_multilist[i]));
                                    device_name_check.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot Data_D_name) {

                                            String D_name = Data_D_name.getValue(String.class);
                                            d_name = d_name + D_name + " ";

                                            if (run_count_D == D_number) {
                                                FileOutputStream D_N_N = null;
                                                try {
                                                    D_N_N = new FileOutputStream(device_txt_name);
                                                    D_N_N.write(d_name.getBytes());
                                                    D_N_N.close();

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError error) { }});
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }});
                    }
                    @Override
                    public void onCancelled(DatabaseError error) { }});

                //改動他
                check_time_for_device_text = 2;

                //E
                FirebaseDatabase database_environment_name = FirebaseDatabase.getInstance();

                //改動他
                check_time_for_environment_text = 1;

                //需要改動
                if (check_time_for_environment_text == 1) {

                    //環境數量
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
                                Toast hint = Toast.makeText(Splash_Screen.this, "E_number錯誤", Toast.LENGTH_SHORT);
                                hint.show();
                            }

                            //處理編號list(1 2 3)
                            DatabaseReference environment_list_check = database_environment_name.getReference("environment").child("list");
                            environment_list_check.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot Data_E_list) {

                                    String E_list = Data_E_list.getValue(String.class);
                                    String[] E_multilist = E_list.split(" ");

                                    //寫入有幾個環境
                                    try {
                                        FileOutputStream E_L = null;

                                        E_L = new FileOutputStream(environment_txt_list);
                                        E_L.write(E_list.getBytes());
                                        E_L.close();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Toast hint = Toast.makeText(Splash_Screen.this, "E_list錯誤", Toast.LENGTH_SHORT);
                                        hint.show();
                                    }

                                    run_count = 0;
                                    for (int i = 0; i < E_multilist.length; i++) {

                                        run_count++;

                                        //環境名稱
                                        DatabaseReference environment_name_check = database_environment_name.getReference("environment").child(String.valueOf(E_multilist[i]));
                                        environment_name_check.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot Data_E_name) {

                                                String E_name = Data_E_name.getValue(String.class);
                                                e_name = e_name + E_name + " ";

                                                if (run_count == E_number) {
                                                    FileOutputStream E_N_N = null;
                                                    try {
                                                        E_N_N = new FileOutputStream(environment_txt_name);
                                                        E_N_N.write(e_name.getBytes());
                                                        E_N_N.close();

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

                                    //強度
                                    DatabaseReference environment_strength_check = database_environment_name.getReference("environment").child("strength");
                                    environment_strength_check.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot Data_E_strength) {

                                            String E_strength = Data_E_strength.getValue(String.class);
                                            e_strength = e_strength + E_strength;

                                            if (run_count == E_number) {
                                                FileOutputStream E_S_S = null; //Let them come(X)
                                                try {
                                                    E_S_S = new FileOutputStream(environment_txt_strength);
                                                    E_S_S.write(e_strength.getBytes());
                                                    E_S_S.close();

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
                                public void onCancelled(DatabaseError error) {
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });

                    check_time_for_environment_text = 2;

                }

                //檢查完畢 或 不需要改動，D
                if (check_time_for_environment_text == 2) {

                    Intent splash_to_main = new Intent();
                    splash_to_main.setClass(Splash_Screen.this, MainActivity.class);
                    startActivity(splash_to_main);
                    finish();
                }
            }

            //如果兩個檔案都不存在，跑這段
            if (!device_txt_number.exists() && !environment_txt_number.exists()) {
                try {
                    //D

                    device_txt_number.createNewFile();
                    device_txt_list.createNewFile();
                    device_txt_name.createNewFile();

                    //設備號碼的檢查
                    FirebaseDatabase database_device_name = FirebaseDatabase.getInstance();
                    //環境數量
                    DatabaseReference device_number_check = database_device_name.getReference("device").child("number");
                    device_number_check.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot Data_D_number) {

                            int D_number = Data_D_number.getValue(int.class);

                            //小的是String，大的是int
                            d_number = d_number + D_number;

                            //寫入有幾個環境
                            try {
                                FileOutputStream D_N_N = null;

                                D_N_N = new FileOutputStream(device_txt_number);
                                D_N_N.write(d_number.getBytes());
                                D_N_N.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast hint = Toast.makeText(Splash_Screen.this, "D_number錯誤", Toast.LENGTH_SHORT);
                                hint.show();
                            }

                            //處理編號list(1 2 3)
                            DatabaseReference device_list_check = database_device_name.getReference("device").child("list");
                            device_list_check.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot Data_D_list) {

                                    String D_list = Data_D_list.getValue(String.class);
                                    String[] D_multilist = D_list.split(" ");

                                    //寫入有幾個環境
                                    try {
                                        FileOutputStream D_L = null;

                                        D_L = new FileOutputStream(device_txt_list);
                                        D_L.write(D_list.getBytes());
                                        D_L.close();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Toast hint = Toast.makeText(Splash_Screen.this, "D_list錯誤", Toast.LENGTH_SHORT);
                                        hint.show();
                                    }

                                    run_count_D = 0;
                                    for (int i = 0; i < D_multilist.length; i++) {

                                        run_count_D++;

                                        //環境名稱
                                        DatabaseReference device_name_check = database_device_name.getReference("device").child(String.valueOf(D_multilist[i]));
                                        device_name_check.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot Data_D_name) {

                                                String D_name = Data_D_name.getValue(String.class);
                                                d_name = d_name + D_name + " ";

                                                //hint = Toast.makeText(Splash_Screen.this, run_count + "" + D_number, Toast.LENGTH_SHORT);
                                                //hint.show();

                                                if (run_count_D == D_number) {
                                                    FileOutputStream D_N_N = null;
                                                    try {
                                                        hint = Toast.makeText(Splash_Screen.this, d_name, Toast.LENGTH_SHORT);
                                                        hint.show();

                                                        D_N_N = new FileOutputStream(device_txt_name);
                                                        D_N_N.write(d_name.getBytes());
                                                        D_N_N.close();

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                        hint = Toast.makeText(Splash_Screen.this, "D_name錯誤", Toast.LENGTH_SHORT);
                                                        hint.show();
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError error) { }});
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError error) { }});
                        }
                        @Override
                        public void onCancelled(DatabaseError error) { }});

                    //改動他
                    check_time_for_device_text = 2;

                    //E

                    environment_txt_number.createNewFile(); //環境數量
                    environment_txt_list.createNewFile();
                    environment_txt_name.createNewFile();   //環境名稱
                    environment_txt_door.createNewFile();
                    environment_txt_strength.createNewFile();

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
                                Toast hint = Toast.makeText(Splash_Screen.this, "E_number錯誤", Toast.LENGTH_SHORT);
                                hint.show();
                            }

                            //處理編號list(1 2 3)
                            DatabaseReference environment_list_check = database_environment_name.getReference("environment").child("list");
                            environment_list_check.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot Data_E_list) {

                                    String E_list = Data_E_list.getValue(String.class);
                                    String[] E_multilist = E_list.split(" ");

                                    //寫入有幾個環境
                                    try {
                                        FileOutputStream E_L = null;

                                        E_L = new FileOutputStream(environment_txt_list);
                                        E_L.write(E_list.getBytes());
                                        E_L.close();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Toast hint = Toast.makeText(Splash_Screen.this, "E_list錯誤", Toast.LENGTH_SHORT);
                                        hint.show();
                                    }

                                    run_count = 0;
                                    for (int i = 0; i < E_multilist.length; i++) {

                                        run_count++;

                                        //環境名稱
                                        DatabaseReference environment_name_check = database_environment_name.getReference("environment").child(String.valueOf(E_multilist[i]));
                                        environment_name_check.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot Data_E_name) {

                                                String E_name = Data_E_name.getValue(String.class);
                                                e_name = e_name + E_name + " ";

                                                if (run_count == E_number) {
                                                    FileOutputStream E_N_N = null;
                                                    try {
                                                        E_N_N = new FileOutputStream(environment_txt_name);
                                                        E_N_N.write(e_name.getBytes());
                                                        E_N_N.close();

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

                                    //強度
                                    DatabaseReference environment_strength_check = database_environment_name.getReference("environment").child("strength");
                                    environment_strength_check.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot Data_E_strength) {

                                            String E_strength = Data_E_strength.getValue(String.class);
                                            e_strength = e_strength + E_strength;

                                            if (run_count == E_number) {
                                                FileOutputStream E_S_S = null; //Let them come(X)
                                                try {
                                                    E_S_S = new FileOutputStream(environment_txt_strength);
                                                    E_S_S.write(e_strength.getBytes());
                                                    E_S_S.close();

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
                                public void onCancelled(DatabaseError error) {
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });

                    check_time_for_text = 1;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //如果它存在了，這段不會跑
            if (check_time_for_text == 1)
            {
                check_time_for_text = 2;
            }

            //檢查完畢後(2)，傳送
            if (check_time_for_text == 2) {
                Intent splash_to_main = new Intent();
                splash_to_main.setClass(Splash_Screen.this, MainActivity.class);
                startActivity(splash_to_main);
                finish();
            }
        }
    }