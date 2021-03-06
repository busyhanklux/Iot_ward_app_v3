package com.example.iot_ward_app_v3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.renderscript.Sampler;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class change_page__device extends AppCompatActivity {

    private Button BT_CPD_back;
    TextView Hint,synchronize;
    TextView L_N_L,L_N_R,L_Nu_L,L_Nu_R,L_D_L,L_D_R,L_S_L,L_S_R;
    TextView D_N_L,D_N_R,D_Nu_L,D_Nu_R,D_D_L,D_D_R,D_S_L,D_S_R;
    Spinner sp_look_CPD_name,sp_change_CPD_name,sp_delete_CPD_name;
    Button  BT_look_CPD,BT_add_CPD,BT_add_CPD_Decide,BT_change_CPD,BT_change_CPD_Decide,BT_delete_CPD,BT_Dlook_CPD,BT_delete_CPD_decide;

    EditText ed_add_CPD_name,ed_add_CPD_number;
    int      Add_number = 0;
    String   FB_ADD_list;
    int      FB_ADD_number,ADD_lock = 0;

    EditText ed_change_CPD_name;
    String   FB_CHANGE_name;
    int      Change_name_lock = 0;

    String   FB_DELETE_list;
    int      Delete_number = 0;
    int      FB_DELETE_number,Delete_lock = 0;

    String d_time = "";
    String d_name = "";
    String d_number = "";

    Toast hint;

    String room_place_number,change_room_place_number,delete_room_place_number; //????????????
    int    room_place_list,change_room_place_list = 0,delete_room_place_list;
    int    list_rank_before,list_rank_after;
    int    delete_list_rank_before,delete_list_rank_after;

    ArrayList E_device_name = new ArrayList();

    int device_number; //?????????????????????????????????????????????
    int check_for_device_text;

    int run_count,trun_count = 0;
    long time_now = System.currentTimeMillis() / 1000;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_page__device);

        File device_txt_name  = new File(getFilesDir(), "device_name.txt");   //????????????
        File device_txt_number= new File(getFilesDir(), "device_number.txt"); //????????????
        File device_txt_list  = new File(getFilesDir(), "device_list.txt");   //?????????????????????

        Hint = (TextView)findViewById(R.id.Hint);
        synchronize = (TextView)findViewById(R.id.synchronize);

        //???????????????Text
        L_N_L = findViewById(R.id.L_N_L);
        L_N_R = findViewById(R.id.L_N_R);
        L_Nu_L = findViewById(R.id.L_Nu_L);
        L_Nu_R = findViewById(R.id.L_Nu_R);
        L_D_L = findViewById(R.id.L_D_L);
        L_D_R = findViewById(R.id.L_D_R);
        L_S_L = findViewById(R.id.L_S_L);
        L_S_R = findViewById(R.id.L_S_R);

        D_N_L = findViewById(R.id.D_N_L);
        D_N_R = findViewById(R.id.D_N_R);
        D_Nu_L = findViewById(R.id.D_Nu_L);
        D_Nu_R = findViewById(R.id.D_Nu_R);
        D_D_L = findViewById(R.id.D_D_L);
        D_D_R = findViewById(R.id.D_D_R);
        D_S_L = findViewById(R.id.D_S_L);
        D_S_R = findViewById(R.id.D_S_R);

        //??????
        ed_add_CPD_name = findViewById(R.id.ed_add_CPD_name);
        ed_add_CPD_number = findViewById(R.id.ed_add_CPD_number);
        //??????
        ed_change_CPD_name = findViewById(R.id.ed_change_CPD_name);

        //?????????
        BT_CPD_back = findViewById(R.id.BT_CPD_back);
        BT_CPD_back.setOnClickListener(BT_CPD_back_L);
        //??????
        BT_look_CPD = findViewById(R.id.BT_look_CPD);
        BT_look_CPD.setOnClickListener(BT_look_CPD_L);
        //??????
        BT_add_CPD = findViewById(R.id.BT_add_CPD);
        BT_add_CPD.setOnClickListener(BT_add_CPD_L);
        BT_add_CPD_Decide = findViewById(R.id.BT_add_CPD_Decide);
        BT_add_CPD_Decide.setOnClickListener(BT_add_CPD_Decide_L);
        BT_add_CPD_Decide.setVisibility(View.INVISIBLE);
        //??????
        BT_change_CPD = findViewById(R.id.BT_change_CPD);
        BT_change_CPD.setOnClickListener(BT_change_CPD_L);
        BT_change_CPD_Decide = findViewById(R.id.BT_change_CPD_Decide);
        BT_change_CPD_Decide.setOnClickListener(BT_change_CPD_Decide_L);
        BT_change_CPD_Decide.setVisibility(View.INVISIBLE);
        //??????
        BT_delete_CPD = findViewById(R.id.BT_delete_CPD);
        BT_delete_CPD.setOnClickListener(BT_delete_CPD_L);
        BT_Dlook_CPD = findViewById(R.id.BT_Dlook_CPD);
        BT_Dlook_CPD.setOnClickListener(BT_Dlook_CPD_L);
        BT_delete_CPD_decide = findViewById(R.id.BT_delete_CPD_Decide);
        BT_delete_CPD_decide.setOnClickListener(BT_delete_CPD_Decide_L);
        BT_delete_CPD_decide.setVisibility(View.INVISIBLE);

        sp_look_CPD_name       = (Spinner)findViewById(R.id.sp_look_CPD_name);
        sp_change_CPD_name     = (Spinner)findViewById(R.id.sp_change_CPD_name);
        sp_delete_CPD_name     = (Spinner)findViewById(R.id.sp_delete_CPD_name);

        //??????
        FirebaseDatabase database_device_name = FirebaseDatabase.getInstance();
        DatabaseReference firebase_number_check = database_device_name.getReference("device").child("number");
        firebase_number_check.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int number = dataSnapshot.getValue(int.class);
                device_number = number;
                Hint.setText(number+"");

            }
            @Override
            public void onCancelled(DatabaseError error) { }
        });

        //????????????????????????
        DatabaseReference firebase_time_check = database_device_name.getReference("device").child("change_time");
        firebase_time_check.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot_D) {

                //D??????????????????????????????
                long D_change_time = dataSnapshot_D.getValue(long.class);

                //?????????
                d_time = d_time + time_now;

                //??????
                /*
                FileOutputStream DTT = null;
                DTT = new FileOutputStream(device_txt_time);
                DTT.write(d_time.getBytes());
                DTT.close();

                 */

                //Toast hint = Toast.makeText(change_page__device.this, d_time+"",Toast.LENGTH_SHORT);
                //hint.show();
                check_for_device_text = 1;

                //????????????
                if (check_for_device_text == 1) {

                    FileOutputStream fos = null;

                    //????????????
                    FirebaseDatabase database_device_name = FirebaseDatabase.getInstance();
                    DatabaseReference device_number_check = database_device_name.getReference("device").child("number");
                    device_number_check.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot Data_D_number) {

                            int D_number = Data_D_number.getValue(int.class);

                            //?????????String????????????int
                            d_number = d_number + D_number;

                            //????????????list(1 2 3)
                            DatabaseReference device_list_check = database_device_name.getReference("device").child("list");
                            device_list_check.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot Data_D_list) {

                                    String D_list = Data_D_list.getValue(String.class);
                                    String []D_multilist = D_list.split(" ");

                                    run_count = 0;

                                    ArrayList<String> room_name = new ArrayList<String>();
                                    room_name.add("???????????????");

                                    for(int i = 0; i < D_multilist.length; i++){

                                        run_count++;

                                        //????????????
                                        DatabaseReference environment_name_check = database_device_name.getReference("device").child(String.valueOf(D_multilist[i]));

                                        String add_number = D_multilist[i]; //??????????????????list?????????

                                        environment_name_check.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot Data_D_name) {

                                                String D_name = Data_D_name.getValue(String.class);
                                                d_name = d_name + D_name + " ";

                                                room_name.add(add_number+". "+D_name); //(???)?????????(???)?????????1. ____ 2. ____

                                                //Toast hint = Toast.makeText(change_page__device.this, add_number +"",Toast.LENGTH_SHORT);
                                                //hint.show();

                                                if (run_count == D_number)
                                                {
                                                    FileOutputStream D_N_N = null;
                                                    /*
                                                    D_N_N = new FileOutputStream(device_txt_name);
                                                    D_N_N.write(d_name.getBytes());
                                                    D_N_N.close();

                                                     */

                                                    /*
                                                    ArrayList<String> real_device_name = new ArrayList<String>();

                                                    for (int j = 0; j < room_name.size()-1; j++) {

                                                        real_device_name.add(room_name.get(j));
                                                    }

                                                     */

                                                    //spinner????????????????????????xml???????????????(???android??????
                                                    //Spinner(sp_look_CPD_name_S)
                                                    ArrayAdapter<String> sp_look_CPD_name_S =
                                                            new ArrayAdapter<String>(change_page__device.this,R.layout.spinner_value_choice_color,room_name);
                                                    sp_look_CPD_name_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                    sp_look_CPD_name.setAdapter(sp_look_CPD_name_S); //??????????????????
                                                    sp_look_CPD_name.setOnItemSelectedListener(sp_look_CPD_name_L);

                                                    //Spinner(sp_change_CPD_name_S)
                                                    ArrayAdapter<String> sp_change_CPD_name_S =
                                                            new ArrayAdapter<String>(change_page__device.this,R.layout.spinner_value_choice_color,room_name);
                                                    sp_change_CPD_name_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                    sp_change_CPD_name.setAdapter(sp_change_CPD_name_S); //??????????????????
                                                    sp_change_CPD_name.setOnItemSelectedListener(sp_change_CPD_name_L);

                                                    //Spinner(sp_delete_CPD_name_S)
                                                    ArrayAdapter<String> sp_delete_CPD_name_S =
                                                            new ArrayAdapter<String>(change_page__device.this,R.layout.spinner_value_choice_color,room_name);
                                                    sp_delete_CPD_name_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                    sp_delete_CPD_name.setAdapter(sp_delete_CPD_name_S); //??????????????????
                                                    sp_delete_CPD_name.setOnItemSelectedListener(sp_delete_CPD_name_L);

                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError error) { }
                                        });
                                    }

                                }
                                @Override
                                public void onCancelled(DatabaseError error) { }
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError error) { }
                    });

                    check_for_device_text = 2;

                    long time_now_fix = time_now*1000;
                    Date day_month_year = new Date(time_now_fix);
                    String format = new SimpleDateFormat("yyyy/MM/dd ahh:mm:ss").format(day_month_year);
                    synchronize.setText(format);

                }

                //????????????
                //???????????? ??? ???????????????
                if (check_for_device_text == 2) {

                }

            }
            @Override
            public void onCancelled(DatabaseError error) { }
        });

    }

    //??????????????????txt??????
    public View.OnClickListener BT_CPD_back_L = view ->
    {

        Intent intent = new Intent();
        intent.setClass(change_page__device.this,adminster_page.class);
        startActivity(intent);
        finish();
    };

    //????????????????????????
    Spinner.OnItemSelectedListener sp_look_CPD_name_L = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int look_CPD, long id) {
            //Toast hint = Toast.makeText(change_page__device.this, parent.getItemAtPosition(look_CPD).toString()+"",Toast.LENGTH_SHORT);
            //hint.show();

            //??????????????????1. 2. 3. ... 11.
            //???????????????????????????3????????????????????????4???

            String room_place = parent.getItemAtPosition(look_CPD).toString(); //????????????

            //?????????????????????
            if (look_CPD != 0)
            {
                //String.indexOf -> ????????????????????????????????????
                //???????????????"."???index = 1??????????????????2


                int room_place_number_dot = room_place.indexOf('.');
                room_place_number = room_place.substring(0,room_place_number_dot);
                room_place_list   = look_CPD;

                //hint = Toast.makeText(change_page__device.this, room_place.substring(0,room_place_number_dot)+"",Toast.LENGTH_SHORT);
                //hint.show();
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};

    //??????
    public View.OnClickListener BT_look_CPD_L = view ->
    {
        if(room_place_list != 0 )
        {
            FirebaseDatabase database_device_name = FirebaseDatabase.getInstance();
            DatabaseReference firebase_name_find = database_device_name.getReference("device").child(room_place_number);
            firebase_name_find.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot D_1) {

                    String name = D_1.getValue(String.class);
                    if (!room_place_number.equals("100000"))
                    {
                        L_N_R.setText(name);
                        L_Nu_R.setText(room_place_number);
                    }else{

                        hint = Toast.makeText(change_page__device.this, "???????????????????????????",Toast.LENGTH_SHORT);
                        hint.show();
                    }

                }
                @Override
                public void onCancelled(DatabaseError error) { }
            });
        }else{

            hint = Toast.makeText(change_page__device.this, "????????????",Toast.LENGTH_SHORT);
            hint.show();
        }

    };

    // -----------------------ADD -------------------------

    public View.OnClickListener BT_add_CPD_L = view ->
    {
        String ed_add_CPD_name_C = ed_add_CPD_name.getText().toString();
        String ed_add_CPD_number_C = ed_add_CPD_number.getText().toString();

        if ((!ed_add_CPD_name_C.equals("")) && (!ed_add_CPD_number_C.equals(""))) {

            if(ed_add_CPD_number_C.charAt(0) == '0')
            {
                hint = Toast.makeText(change_page__device.this, "????????????????????????????????? 0", Toast.LENGTH_SHORT);
                hint.show();

                ADD_lock = 0;

                return;
            }

            if (Integer.parseInt(ed_add_CPD_number_C) > 65530)
            {

                hint = Toast.makeText(change_page__device.this, "????????????????????????????????????0~65529??????", Toast.LENGTH_SHORT);
                hint.show();

                ADD_lock = 0;

                return;
            }

            for (int i = 0; i < ed_add_CPD_name_C.length(); i++) {
                if(ed_add_CPD_name_C.charAt(i) == ' ')
                {
                    hint = Toast.makeText(change_page__device.this, "????????????????????????", Toast.LENGTH_SHORT);
                    hint.show();

                    ADD_lock = 0;

                    return;
                }
            }

            ADD_lock = 1;
            BT_add_CPD_Decide.setVisibility(View.VISIBLE);

            //hint = Toast.makeText(change_page__device.this, "??????", Toast.LENGTH_SHORT);
            //hint.show();

            FirebaseDatabase database_device_ADD = FirebaseDatabase.getInstance();

            DatabaseReference firebase_name_ADD = database_device_ADD.getReference("device"); //???????????????

            DatabaseReference device_list_check = database_device_ADD.getReference("device").child("list");
            device_list_check.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot ADD_1) {

                    String D_list = ADD_1.getValue(String.class);
                    String[] D_multilist = D_list.split(" ");

                    //????????????????????? -> Toast????????????
                    for (int i = 0; i < D_multilist.length; i++) {

                        if (ed_add_CPD_number_C.equals(D_multilist[i])) {
                            //dialog.dismiss();

                            ADD_lock = 0;
                            BT_add_CPD_Decide.setVisibility(View.INVISIBLE);
                            return;
                        }
                    }

                    //hint = Toast.makeText(change_page__device.this, "????????????", Toast.LENGTH_SHORT);
                    //hint.show();

                    list_rank_before = 0;
                    list_rank_after = 0;

                    //?????????????????????????

                    for (int i = 0; i < D_multilist.length; i++) {
                        //??????????????????
                        if (Integer.parseInt(ed_add_CPD_number_C) > (Integer.parseInt(D_multilist[i]))) {
                            list_rank_before++;
                        }
                        //??????????????????
                        if (Integer.parseInt(ed_add_CPD_number_C) < (Integer.parseInt(D_multilist[i]))) {
                            list_rank_after++;
                        }
                    }

                    //????????? 1 2 3 25?????????24???before + after = 4
                    //??????????????? 1 2 3 24 25

                    String new_list = "";

                    for (int i = 0; i < list_rank_before + list_rank_after + 1; i++) {
                        if (i < list_rank_before) {
                            new_list = new_list + D_multilist[i] + " ";
                        }
                        if (i == list_rank_before) {
                            new_list = new_list + ed_add_CPD_number_C + " ";
                        }
                        if (i > list_rank_before) {
                            new_list = new_list + D_multilist[i - 1] + " ";
                        }
                    }

                    //hint = Toast.makeText(change_page__device.this, new_list, Toast.LENGTH_SHORT);
                    //hint.show(); //?????????????????????

                    //firebase_name_ADD.child("list").setValue(new_list); //?????????list?????????
                    FB_ADD_list = new_list;

                    Add_number = Integer.parseInt(Hint.getText().toString()) + 1;
                    FB_ADD_number = Add_number;

                    //new_list = D_list + " " + ed_add_CPD_number_C ;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        if ((ed_add_CPD_name_C.equals("")) || (ed_add_CPD_number_C.equals(""))) {

            if (ed_add_CPD_name_C.equals("")) {

                hint = Toast.makeText(change_page__device.this, "???????????????????????????", Toast.LENGTH_SHORT);
                hint.show();
            }

            if (ed_add_CPD_number_C.equals("")) {

                hint = Toast.makeText(change_page__device.this, "???????????????????????????", Toast.LENGTH_SHORT);
                hint.show();
            }
        }
    };

    public View.OnClickListener BT_add_CPD_Decide_L = view ->
    {
        if (ADD_lock == 1)
        {
            /*
            hint = Toast.makeText(change_page__device.this, FB_ADD_list,Toast.LENGTH_SHORT);
            hint.show();
            hint = Toast.makeText(change_page__device.this, FB_ADD_number+"",Toast.LENGTH_SHORT);
            hint.show();
             */

            hint = Toast.makeText(change_page__device.this, "??????????????????????????????????????????????????????????????????", Toast.LENGTH_SHORT);
            hint.show();

            FirebaseDatabase database_device_ADD = FirebaseDatabase.getInstance();
            DatabaseReference firebase_name_ADD = database_device_ADD.getReference("device"); //???????????????

            firebase_name_ADD.child("list").setValue(FB_ADD_list); //?????????????????????
            firebase_name_ADD.child("number").setValue(FB_ADD_number); //?????????????????????
            //firebase_name_ADD.child("change_time").setValue(FB_ADD_time);
            firebase_name_ADD.child(ed_add_CPD_number.getText().toString()).setValue(ed_add_CPD_name.getText().toString()); //?????????????????????
        }

        if (ADD_lock == 0)
        {
            hint = Toast.makeText(change_page__device.this, "?????????????????????", Toast.LENGTH_SHORT);
            hint.show();
        }
        ADD_lock = 0;
        BT_add_CPD_Decide.setVisibility(View.INVISIBLE);
    };

    //-----------------CHANGE-----------------

    public View.OnClickListener BT_change_CPD_L = view ->
    {
        if(change_room_place_list != 0)
        {
            String ed_change_CPD_name_C = ed_change_CPD_name.getText().toString();

            //??????????????????
            if(!ed_change_CPD_name_C.equals(""))
            {
                if (Integer.parseInt(change_room_place_number) == 100000)
                {
                    hint = Toast.makeText(change_page__device.this, "?????????????????????????????????", Toast.LENGTH_SHORT);
                    hint.show();

                    Change_name_lock = 0;

                    return;
                }

                for (int i = 0; i < ed_change_CPD_name_C.length(); i++) {
                    if(ed_change_CPD_name_C.charAt(i) == ' ')
                    {
                        hint = Toast.makeText(change_page__device.this, "????????????????????????", Toast.LENGTH_SHORT);
                        hint.show();

                        Change_name_lock = 0;

                        return;
                    }
                }

                FB_CHANGE_name = ed_change_CPD_name_C;
                //hint = Toast.makeText(change_page__device.this, "????????????", Toast.LENGTH_SHORT);
                //hint.show();

                Change_name_lock = 1;
            }

            BT_change_CPD_Decide.setVisibility(View.VISIBLE);

            //???????????????
            if (ed_change_CPD_name_C.equals(""))
            {
                hint = Toast.makeText(change_page__device.this, "??????????????????", Toast.LENGTH_SHORT);
                hint.show();

                BT_change_CPD_Decide.setVisibility(View.INVISIBLE);
                return;
            }
        }else{
            hint = Toast.makeText(change_page__device.this, "???????????????????????????", Toast.LENGTH_SHORT);
            hint.show();
        }

    };

    public View.OnClickListener BT_change_CPD_Decide_L = view ->
    {

        FirebaseDatabase database_device_CHANGE = FirebaseDatabase.getInstance();
        DatabaseReference firebase_name_CHANGE = database_device_CHANGE.getReference("device"); //???????????????

        if (Change_name_lock == 1)
        {
            //hint = Toast.makeText(change_page__device.this, FB_CHANGE_name,Toast.LENGTH_SHORT);
            //hint.show();

            firebase_name_CHANGE.child(change_room_place_number).setValue(FB_CHANGE_name); //?????????????????????
            Change_name_lock = 0;
        }

        hint = Toast.makeText(change_page__device.this, "??????????????????", Toast.LENGTH_SHORT);
        hint.show();

        Intent CPD_turn = new Intent();
        CPD_turn.setClass(change_page__device.this,change_page__device.class);
        startActivity(CPD_turn);
        finish();

        BT_change_CPD_Decide.setVisibility(View.INVISIBLE);
    };

    Spinner.OnItemSelectedListener sp_change_CPD_name_L = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int chagne_CPD, long id) {

            //Toast hint = Toast.makeText(change_page__device.this, parent.getItemAtPosition(look_CPD).toString()+"",Toast.LENGTH_SHORT);
            //hint.show();

            //??????????????????1. 2. 3. ... 11.
            //???????????????????????????3????????????????????????4???

            String room_place = parent.getItemAtPosition(chagne_CPD).toString(); //????????????

            //?????????????????????

            if (chagne_CPD != 0)
            {
                int room_place_number_dot = room_place.indexOf('.');
                change_room_place_number = room_place.substring(0,room_place_number_dot);
            }

            change_room_place_list = chagne_CPD;

            //hint = Toast.makeText(change_page__device.this, change_room_place_list+"",Toast.LENGTH_SHORT);
            //hint.show();

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};

    //---------------------delete---------------------

    Spinner.OnItemSelectedListener sp_delete_CPD_name_L = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int delete_CPD_S, long id) {

            String room_place = parent.getItemAtPosition(delete_CPD_S).toString(); //????????????

            //?????????????????????
            if (delete_CPD_S != 0)
            {
                //String.indexOf -> ????????????????????????????????????
                //???????????????"."???index = 1??????????????????2

                int room_place_number_dot = room_place.indexOf('.');
                delete_room_place_number = room_place.substring(0,room_place_number_dot);
                delete_room_place_list   = delete_CPD_S;

                //hint = Toast.makeText(change_page__device.this, room_place.substring(0,room_place_number_dot)+"",Toast.LENGTH_SHORT);
                //hint.show();
            }

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};

    public View.OnClickListener BT_Dlook_CPD_L = view ->
    {

        if(delete_room_place_list != 0 )
        {
            if (Integer.parseInt(delete_room_place_number) == 100000)
            {

                hint = Toast.makeText(change_page__device.this, "?????????????????????????????????", Toast.LENGTH_SHORT);
                hint.show();

                Delete_lock = 0;

                return;

            }else {

                FirebaseDatabase database_environment_name = FirebaseDatabase.getInstance();
                DatabaseReference firebase_name_find = database_environment_name.getReference("device").child(delete_room_place_number);
                firebase_name_find.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot_1) {

                        String name = dataSnapshot_1.getValue(String.class);
                        D_N_R.setText(name);
                        D_Nu_R.setText(delete_room_place_number);

                    }
                    @Override
                    public void onCancelled(DatabaseError error) { }
                });
            }

        }else{

            hint = Toast.makeText(change_page__device.this, "??????????????????",Toast.LENGTH_SHORT);
            hint.show();
        }

    };

    public View.OnClickListener BT_delete_CPD_L = view ->
    {

        if (Integer.parseInt(delete_room_place_number) == 100000)
        {

            hint = Toast.makeText(change_page__device.this, "?????????????????????????????????", Toast.LENGTH_SHORT);
            hint.show();

            Delete_lock = 0;

            return;

        }

        Delete_lock = 1;
        BT_delete_CPD_decide.setVisibility(View.VISIBLE);

        try {
            FirebaseDatabase database_device_DELETE = FirebaseDatabase.getInstance();
            DatabaseReference firebase_name_DELETE = database_device_DELETE.getReference("device"); //???????????????
            DatabaseReference device_list_check = database_device_DELETE.getReference("device").child("list");

            device_list_check.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot DELETE_1) {

                    String D_list = DELETE_1.getValue(String.class);
                    String[] D_multilist = D_list.split(" ");

                    delete_list_rank_before = 0;
                    delete_list_rank_after = 0;

                    //????????? 1 2 3 24 25?????????24
                    //??????????????? 1 2 3 25

                    String new_list = "";

                    for (int i = 0; i < D_multilist.length; i++) {

                        if(!delete_room_place_number.equals(D_multilist[i]))
                        {
                            new_list = new_list + D_multilist[i] + " ";
                        }
                    }

                    //hint = Toast.makeText(change_page__device.this, new_list, Toast.LENGTH_SHORT);
                    //hint.show(); //?????????????????????

                    FB_DELETE_list = new_list;
                    Delete_number = Integer.parseInt(Hint.getText().toString()) - 1;
                    FB_DELETE_number = Delete_number;

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }});

        }catch (Exception e) {

        }

    };

    public View.OnClickListener BT_delete_CPD_Decide_L = view ->
    {
        if (Delete_lock == 1)
        {
            /*
            hint = Toast.makeText(change_page__device.this, FB_DELETE_list,Toast.LENGTH_SHORT);
            hint.show();
            hint = Toast.makeText(change_page__device.this, FB_DELETE_number+"",Toast.LENGTH_SHORT);
            hint.show();
             */

            hint = Toast.makeText(change_page__device.this, "??????????????????", Toast.LENGTH_SHORT);
            hint.show();

            FirebaseDatabase database_device_DELETE = FirebaseDatabase.getInstance();
            DatabaseReference firebase_name_DELETE = database_device_DELETE.getReference("device"); //???????????????

            firebase_name_DELETE.child("list").setValue(FB_DELETE_list); //?????????????????????
            firebase_name_DELETE.child("number").setValue(FB_DELETE_number); //?????????????????????
            firebase_name_DELETE.child(delete_room_place_number).removeValue(); //?????????????????????

        }

        if (Delete_lock == 0)
        {
            hint = Toast.makeText(change_page__device.this, "?????????????????????", Toast.LENGTH_SHORT);
            hint.show();
        }
        Delete_lock = 0;
        BT_delete_CPD_decide.setVisibility(View.INVISIBLE);
    };
}
