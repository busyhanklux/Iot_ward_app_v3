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

public class change_page__environment extends AppCompatActivity {

    private Button BT_CPE_back;
    TextView Hint,synchronize;
    TextView L_N_L,L_N_R,L_Nu_L,L_Nu_R,L_D_L,L_D_R,L_S_L,L_S_R;
    TextView D_N_L,D_N_R,D_Nu_L,D_Nu_R,D_D_L,D_D_R,D_S_L,D_S_R;
    Spinner sp_look_CPE_name,sp_change_CPE_name,sp_delete_CPE_name;
    Button  BT_look_CPE,BT_add_CPE,BT_add_CPE_Decide,BT_change_CPE,BT_change_CPE_Decide,BT_delete_CPE,BT_Dlook_CPE,BT_delete_CPE_decide;

    Button  CPE_look_explain;

    EditText ed_add_CPE_name,ed_add_CPE_number;
    Spinner  sp_add_CPE_door,sp_add_CPE_strength;
    String   Add_door,Add_strength,new_door,new_strength;
    int      Add_number = 0,Add_reapet = 0;
    String   FB_ADD_list,FB_ADD_door,FB_ADD_strength;
    int      FB_ADD_number,ADD_lock = 0;
    long     FB_ADD_time;

    EditText ed_change_CPE_name;
    Spinner  sp_change_CPE_door,sp_change_CPE_strength;
    String   Change_door,Change_strength;
    String   FB_CHANGE_name,FB_CHANGE_door,FB_CHANGE_strength;
    int      Change_name_lock = 0,Change_door_lock = 0,Change_strength_lock = 0;

    String   minus_door,minus_strength;
    String   FB_DELETE_list,FB_DELETE_door,FB_DELETE_strength;
    int      Delete_number = 0;
    int      FB_DELETE_number,Delete_lock = 0;

    String e_time = "";
    String e_name = "";
    String e_door = "";
    String e_number = "";
    String e_list = "";
    String format = "";

    String add_door_SP[]  = {"??????????????????","1.???","2.???"};
    String add_Strength[] = {"??????????????????","1.???","2.???","3.???"};
    String change_door_SP[] = {"????????????????????????\n(??????????????????????????????)","1.???","2.???"};
    String change_Strength[] = {"??????????????????\n(??????????????????????????????)","1.???","2.???","3.???"};

    Toast hint;

    String room_place_number,change_room_place_number,delete_room_place_number; //????????????
    int    room_place_list,change_room_place_list = 0,delete_room_place_list;
    int    list_rank_before,list_rank_after;
    int    delete_list_rank_before,delete_list_rank_after;

    ArrayList E_room_name = new ArrayList();

    long environment_Time; //?????????????????????????????????
    int environment_number; //?????????????????????????????????????????????
    int check_for_environment_text;

    int run_count,trun_count = 0;
    long time_now = System.currentTimeMillis() / 1000;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_page__environment);

        File environment_txt_door  = new File(getFilesDir(), "environment_door.txt");   //??????
        File environment_txt_name  = new File(getFilesDir(), "environment_name.txt");   //????????????
        File environment_txt_number= new File(getFilesDir(), "environment_number.txt"); //????????????
        File environment_txt_list  = new File(getFilesDir(), "environment_list.txt");   //?????????????????????


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
        ed_add_CPE_name = findViewById(R.id.ed_add_CPE_name);
        ed_add_CPE_number = findViewById(R.id.ed_add_CPE_number);
        //??????
        ed_change_CPE_name = findViewById(R.id.ed_change_CPE_name);

        //?????????
        BT_CPE_back = findViewById(R.id.BT_CPE_back);
        BT_CPE_back.setOnClickListener(BT_CPE_back_L);
        //??????
        BT_look_CPE = findViewById(R.id.BT_look_CPE);
        BT_look_CPE.setOnClickListener(BT_look_CPE_L);
        CPE_look_explain = findViewById(R.id.CPE_look_explain);
        CPE_look_explain.setOnClickListener(CPE_look_explain_L);
        //??????
        BT_add_CPE = findViewById(R.id.BT_add_CPE);
        BT_add_CPE.setOnClickListener(BT_add_CPE_L);
        BT_add_CPE_Decide = findViewById(R.id.BT_add_CPE_Decide);
        BT_add_CPE_Decide.setOnClickListener(BT_add_CPE_Decide_L);
        BT_add_CPE_Decide.setVisibility(View.INVISIBLE);
        //??????
        BT_change_CPE = findViewById(R.id.BT_change_CPE);
        BT_change_CPE.setOnClickListener(BT_change_CPE_L);
        BT_change_CPE_Decide = findViewById(R.id.BT_change_CPE_Decide);
        BT_change_CPE_Decide.setOnClickListener(BT_change_CPE_Decide_L);
        BT_change_CPE_Decide.setVisibility(View.INVISIBLE);
        //??????
        BT_delete_CPE = findViewById(R.id.BT_delete_CPE);
        BT_delete_CPE.setOnClickListener(BT_delete_CPE_L);
        BT_Dlook_CPE = findViewById(R.id.BT_Dlook_CPE);
        BT_Dlook_CPE.setOnClickListener(BT_Dlook_CPE_L);
        BT_delete_CPE_decide = findViewById(R.id.BT_delete_CPE_Decide);
        BT_delete_CPE_decide.setOnClickListener(BT_delete_CPE_Decide_L);
        BT_delete_CPE_decide.setVisibility(View.INVISIBLE);

        sp_look_CPE_name       = (Spinner)findViewById(R.id.sp_look_CPE_name);
        sp_add_CPE_door        = (Spinner)findViewById(R.id.sp_add_CPE_door);
        sp_add_CPE_strength    = (Spinner)findViewById(R.id.sp_add_CPE_strength);
        sp_change_CPE_name     = (Spinner)findViewById(R.id.sp_change_CPE_name);
        sp_change_CPE_door     = (Spinner)findViewById(R.id.sp_change_CPE_door);
        sp_change_CPE_strength = (Spinner)findViewById(R.id.sp_change_CPE_strength);
        sp_delete_CPE_name     = (Spinner)findViewById(R.id.sp_delete_CPE_name);

        ArrayAdapter<String> sp_add_CPE_door_S =
                new ArrayAdapter<String>(change_page__environment.this,R.layout.spinner_value_choice_color,add_door_SP);
        sp_add_CPE_door_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_add_CPE_door.setAdapter(sp_add_CPE_door_S); //??????????????????
        sp_add_CPE_door.setOnItemSelectedListener(sp_add_CPE_door_L);

        ArrayAdapter<String> sp_add_CPE_strength_S =
                new ArrayAdapter<String>(change_page__environment.this,R.layout.spinner_value_choice_color,add_Strength);
        sp_add_CPE_strength_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_add_CPE_strength.setAdapter(sp_add_CPE_strength_S); //??????????????????
        sp_add_CPE_strength.setOnItemSelectedListener(sp_add_CPE_strength_L);

        ArrayAdapter<String> sp_change_CPE_door_S =
                new ArrayAdapter<String>(change_page__environment.this,R.layout.spinner_value_choice_color,change_door_SP);
        sp_change_CPE_door_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_change_CPE_door.setAdapter(sp_change_CPE_door_S); //??????????????????
        sp_change_CPE_door.setOnItemSelectedListener(sp_change_CPE_door_L);

        ArrayAdapter<String> sp_change_CPE_strength_S =
                new ArrayAdapter<String>(change_page__environment.this,R.layout.spinner_value_choice_color,change_Strength);
        sp_change_CPE_strength_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_change_CPE_strength.setAdapter(sp_change_CPE_strength_S); //??????????????????
        sp_change_CPE_strength.setOnItemSelectedListener(sp_change_CPE_strength_L);

        //??????
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

        //????????????????????????
        DatabaseReference firebase_time_check = database_environment_name.getReference("environment").child("change_time");
        firebase_time_check.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot_E) {

                //E??????????????????????????????
                long E_change_time = dataSnapshot_E.getValue(long.class);

                //?????????
                e_time = e_time + time_now;

                //??????
                /*
                FileOutputStream ETT = null;
                ETT = new FileOutputStream(environment_txt_time);
                ETT.write(e_time.getBytes());
                ETT.close();

                 */

                //Toast hint = Toast.makeText(change_page__environment.this, e_time+"",Toast.LENGTH_SHORT);
                //hint.show();
                check_for_environment_text = 1;

                //????????????
                if (check_for_environment_text == 1) {
                    //Toast hint = Toast.makeText(Splash_Screen.this, "??????????????????",Toast.LENGTH_SHORT);
                    //hint.show();

                    FileOutputStream fos = null;

                    //????????????
                    FirebaseDatabase database_environment_name = FirebaseDatabase.getInstance();
                    DatabaseReference environment_number_check = database_environment_name.getReference("environment").child("number");
                    environment_number_check.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot Data_E_number) {

                            int E_number = Data_E_number.getValue(int.class);

                            //?????????String????????????int
                            e_number = e_number + E_number;

                            //????????????list(1 2 3)
                            DatabaseReference environment_list_check = database_environment_name.getReference("environment").child("list");
                            environment_list_check.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot Data_E_list) {

                                    String E_list = Data_E_list.getValue(String.class);
                                    String []E_multilist = E_list.split(" ");

                                    run_count = 0;

                                    ArrayList<String> room_name = new ArrayList<String>();
                                    room_name.add("???????????????");

                                    for(int i = 0; i < E_multilist.length; i++){

                                        run_count++;

                                        //????????????
                                        DatabaseReference environment_name_check = database_environment_name.getReference("environment").child(String.valueOf(E_multilist[i]));

                                        String add_number = E_multilist[i]; //??????????????????list?????????

                                        environment_name_check.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot Data_E_name) {

                                                String E_name = Data_E_name.getValue(String.class);
                                                e_name = e_name + E_name + " ";

                                                room_name.add(add_number+". "+E_name); //(???)?????????(???)?????????1. ____ 2. ____

                                                //Toast hint = Toast.makeText(change_page__environment.this, add_number +"",Toast.LENGTH_SHORT);
                                                //hint.show();

                                                if (run_count == E_number)
                                                {
                                                    FileOutputStream E_N_N = null;
                                                    /*
                                                    E_N_N = new FileOutputStream(environment_txt_name);
                                                    E_N_N.write(e_name.getBytes());
                                                    E_N_N.close();

                                                     */

                                                    //spinner????????????????????????xml???????????????(???android??????
                                                    //Spinner(sp_look_CPE_name_S)
                                                    ArrayAdapter<String> sp_look_CPE_name_S =
                                                            new ArrayAdapter<String>(change_page__environment.this,R.layout.spinner_value_choice_color,room_name);

                                                    sp_look_CPE_name_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                    sp_look_CPE_name.setAdapter(sp_look_CPE_name_S); //??????????????????
                                                    sp_look_CPE_name.setOnItemSelectedListener(sp_look_CPE_name_L);

                                                    //Spinner(sp_change_CPE_name_S)
                                                    ArrayAdapter<String> sp_change_CPE_name_S =
                                                            new ArrayAdapter<String>(change_page__environment.this,R.layout.spinner_value_choice_color,room_name);

                                                    sp_change_CPE_name_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                    sp_change_CPE_name.setAdapter(sp_change_CPE_name_S); //??????????????????
                                                    sp_change_CPE_name.setOnItemSelectedListener(sp_change_CPE_name_L);

                                                    //Spinner(sp_delete_CPE_name_S)
                                                    ArrayAdapter<String> sp_delete_CPE_name_S =
                                                            new ArrayAdapter<String>(change_page__environment.this,R.layout.spinner_value_choice_color,room_name);

                                                    sp_delete_CPE_name_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                    sp_delete_CPE_name.setAdapter(sp_delete_CPE_name_S); //??????????????????
                                                    sp_delete_CPE_name.setOnItemSelectedListener(sp_delete_CPE_name_L);

                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError error) { }
                                        });
                                    }

                                    //????????????
                                    DatabaseReference environment_door_check = database_environment_name.getReference("environment").child("door");
                                    environment_door_check.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot Data_E_door) {

                                            String E_door = Data_E_door.getValue(String.class);
                                            e_door = e_door + E_door;

                                            /*
                                            if (run_count == E_number) {
                                                FileOutputStream E_D_D = null; //Let them come(X)
                                                try {
                                                    E_D_D = new FileOutputStream(environment_txt_door);
                                                    E_D_D.write(e_door.getBytes());
                                                    E_D_D.close();

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }*/
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

                    check_for_environment_text = 2;

                    long time_now_fix = time_now*1000;
                    Date day_month_year = new Date(time_now_fix);
                    String format = new SimpleDateFormat("yyyy/MM/dd ahh:mm:ss").format(day_month_year);
                    synchronize.setText(format);

                }

                //????????????
                //???????????? ??? ???????????????
                if (check_for_environment_text == 2) {

                }

            }
            @Override
            public void onCancelled(DatabaseError error) { }
        });

    }

    //??????????????????txt??????
    public View.OnClickListener BT_CPE_back_L = view ->
    {

        Intent intent = new Intent();
        intent.setClass(change_page__environment.this,adminster_page.class);
        startActivity(intent);
        finish();
    };

    //????????????????????????
    Spinner.OnItemSelectedListener sp_look_CPE_name_L = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int look_CPE, long id) {
            //Toast hint = Toast.makeText(change_page__environment.this, parent.getItemAtPosition(look_CPE).toString()+"",Toast.LENGTH_SHORT);
            //hint.show();

            //??????????????????1. 2. 3. ... 11.
            //???????????????????????????3????????????????????????4???

            String room_place = parent.getItemAtPosition(look_CPE).toString(); //????????????

            //?????????????????????
            if (look_CPE != 0)
            {
                //String.indexOf -> ????????????????????????????????????
                //???????????????"."???index = 1??????????????????2


                int room_place_number_dot = room_place.indexOf('.');
                room_place_number = room_place.substring(0,room_place_number_dot);
                room_place_list   = look_CPE;

                //hint = Toast.makeText(change_page__environment.this, room_place.substring(0,room_place_number_dot)+"",Toast.LENGTH_SHORT);
                //hint.show();
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};

    //??????
    public View.OnClickListener BT_look_CPE_L = view ->
    {
        if(room_place_list != 0 )
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

                            //hint = Toast.makeText(change_page__environment.this, room_place_list+"",Toast.LENGTH_SHORT);
                            //hint.show();

                            for(int i = 0; i < E_door.length(); i++)
                            {
                                if((room_place_list-1) == i)
                                {
                                    //hint = Toast.makeText(change_page__environment.this, E_door.substring(i,i+1)+"",Toast.LENGTH_SHORT);
                                    //hint.show();

                                    try {

                                        if(E_door.substring(i, i + 1).equals("1")) { L_D_R.setText("??????"); } //???
                                        if(E_door.substring(i, i + 1).equals("2")) { L_D_R.setText("??????"); } //???

                                    }catch (Exception e){

                                    }

                                }
                            }

                            DatabaseReference firebase_number_check = database_environment_name.getReference("environment").child("strength");
                            firebase_number_check.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot_3) {

                                    String E_strength = dataSnapshot_3.getValue(String.class);

                                    for(int i = 0; i < E_door.length(); i++)
                                    {
                                        try {
                                            if((room_place_list-1) == i)
                                            {
                                                if(E_strength.substring(i, i + 1).equals("1")) { L_S_R.setText("???"); } //???
                                                if(E_strength.substring(i, i + 1).equals("2")) { L_S_R.setText("???"); } //???
                                                if(E_strength.substring(i, i + 1).equals("3")) { L_S_R.setText("???"); } //???
                                            }
                                        }catch (Exception e){

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
        }else{

            hint = Toast.makeText(change_page__environment.this, "????????????",Toast.LENGTH_SHORT);
            hint.show();
        }

    };

    public View.OnClickListener CPE_look_explain_L = view ->
    {
        AlertDialog.Builder explain = new AlertDialog.Builder(change_page__environment.this);
        explain.setTitle("??????");
        explain.setIcon(R.drawable.logo4);
        explain.setMessage("??????????????????????????????????????????????????????" +"\n" + "??????????????????????????????????????????????????????");
        explain.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    };

    // -----------------------ADD -------------------------

    Spinner.OnItemSelectedListener sp_add_CPE_door_L = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int add_CPE_D, long id) {

            //???????????????
            if (add_CPE_D != 0)
            {
                Add_door  = String.valueOf(add_CPE_D);


                //hint = Toast.makeText(change_page__environment.this, Add_door+"",Toast.LENGTH_SHORT);
                //hint.show();
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};

    Spinner.OnItemSelectedListener sp_add_CPE_strength_L = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int add_CPE_S, long id) {

            //???????????????
            if (add_CPE_S != 0)
            {
                Add_strength = String.valueOf(add_CPE_S);

                //hint = Toast.makeText(change_page__environment.this, Add_strength+"",Toast.LENGTH_SHORT);
                //hint.show();
            }

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};


    public View.OnClickListener BT_add_CPE_L = view ->
    {
        String ed_add_CPE_name_C = ed_add_CPE_name.getText().toString();
        String ed_add_CPE_number_C = ed_add_CPE_number.getText().toString();

            if ((Add_door != null) && (Add_strength != null) && (!ed_add_CPE_name_C.equals("")) && (!ed_add_CPE_number_C.equals(""))) {

                if(ed_add_CPE_number_C.charAt(0) == '0')
                {
                    hint = Toast.makeText(change_page__environment.this, "????????????????????????????????? 0", Toast.LENGTH_SHORT);
                    hint.show();

                    ADD_lock = 0;

                    return;
                }

                for (int i = 0; i < ed_add_CPE_name_C.length(); i++) {
                    if(ed_add_CPE_name_C.charAt(i) == ' ')
                    {
                        hint = Toast.makeText(change_page__environment.this, "????????????????????????", Toast.LENGTH_SHORT);
                        hint.show();

                        ADD_lock = 0;

                        return;
                    }
                }

                    ADD_lock = 1;
                    BT_add_CPE_Decide.setVisibility(View.VISIBLE);

                    //hint = Toast.makeText(change_page__environment.this, "??????", Toast.LENGTH_SHORT);
                    //hint.show();

                    FirebaseDatabase database_environment_ADD = FirebaseDatabase.getInstance();

                    DatabaseReference firebase_name_ADD = database_environment_ADD.getReference("environment"); //???????????????

                    DatabaseReference environment_list_check = database_environment_ADD.getReference("environment").child("list");
                    environment_list_check.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot ADD_1) {

                            String E_list = ADD_1.getValue(String.class);
                            String[] E_multilist = E_list.split(" ");

                            //????????????????????? -> Toast????????????
                            for (int i = 0; i < E_multilist.length; i++) {

                                if (ed_add_CPE_number_C.equals(E_multilist[i])) {
                                    //dialog.dismiss();

                                    ADD_lock = 0;
                                    BT_add_CPE_Decide.setVisibility(View.INVISIBLE);
                                    return;
                                }
                            }

                            //hint = Toast.makeText(change_page__environment.this, "????????????", Toast.LENGTH_SHORT);
                            //hint.show();

                            list_rank_before = 0;
                            list_rank_after = 0;

                            //?????????????????????????

                            for (int i = 0; i < E_multilist.length; i++) {
                                //??????????????????
                                if (Integer.parseInt(ed_add_CPE_number_C) > (Integer.parseInt(E_multilist[i]))) {
                                    list_rank_before++;
                                }
                                //??????????????????
                                if (Integer.parseInt(ed_add_CPE_number_C) < (Integer.parseInt(E_multilist[i]))) {
                                    list_rank_after++;
                                }
                            }

                            //????????? 1 2 3 25?????????24???before + after = 4
                            //??????????????? 1 2 3 24 25

                            String new_list = "";

                            for (int i = 0; i < list_rank_before + list_rank_after + 1; i++) {
                                if (i < list_rank_before) {
                                    new_list = new_list + E_multilist[i] + " ";
                                }
                                if (i == list_rank_before) {
                                    new_list = new_list + ed_add_CPE_number_C + " ";
                                }
                                if (i > list_rank_before) {
                                    new_list = new_list + E_multilist[i - 1] + " ";
                                }
                            }

                            //hint = Toast.makeText(change_page__environment.this, new_list, Toast.LENGTH_SHORT);
                            //hint.show(); //?????????????????????

                            //firebase_name_ADD.child("list").setValue(new_list); //?????????list?????????
                            FB_ADD_list = new_list;

                            //new_list = E_list + " " + ed_add_CPE_number_C ;

                            DatabaseReference environment_door_check = database_environment_ADD.getReference("environment").child("door");
                            environment_door_check.addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot ADD_2) {

                                    String E_door = ADD_2.getValue(String.class);

                                    new_door = "";

                                    try {
                                        for (int i = 0; i < list_rank_before + list_rank_after + 1; i++) {

                                            if (i < list_rank_before) {
                                                new_door = new_door + E_door.substring(i, i + 1) + "";
                                            }
                                            if (i == list_rank_before) {
                                                new_door = new_door + Add_door + "";
                                            }
                                            if (i > list_rank_before) {
                                                new_door = new_door + E_door.substring(i - 1, i) + "";
                                            }
                                        }
                                    }catch (Exception e){

                                    }

                                    //hint = Toast.makeText(change_page__environment.this, new_door, Toast.LENGTH_SHORT); //?????????
                                    //hint.show();

                                    //firebase_name_ADD.child("door").setValue(new_door);
                                    FB_ADD_door = new_door;

                                    DatabaseReference firebase_door_add = database_environment_ADD.getReference("environment").child("strength");
                                    firebase_door_add.addValueEventListener(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot ADD_3) {

                                            String E_strength = ADD_3.getValue(String.class);

                                            new_strength = "";

                                            try {
                                                for (int i = 0; i < list_rank_before + list_rank_after + 1; i++) {

                                                    if (i < list_rank_before) {
                                                        new_strength = new_strength + E_strength.substring(i, i + 1) + "";
                                                    }
                                                    if (i == list_rank_before) {
                                                        new_strength = new_strength + Add_strength + "";
                                                    }
                                                    if (i > list_rank_before) {
                                                        new_strength = new_strength + E_strength.substring(i - 1, i) + "";
                                                    }
                                                }

                                            }catch (Exception e){

                                            }

                                            FB_ADD_strength = new_strength;

                                            Add_number = Integer.parseInt(Hint.getText().toString()) + 1;
                                            FB_ADD_number = Add_number;

                                            //FB_ADD_time = System.currentTimeMillis() / 1000; ?????????????????????bug????????????????????????????????????


                                            //hint = Toast.makeText(change_page__environment.this, "??????", Toast.LENGTH_SHORT); //?????????
                                            //hint.show();

                                            return;
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }

            if ((Add_door == null) || (Add_strength == null) || (ed_add_CPE_name_C.equals("")) || (ed_add_CPE_number_C.equals(""))) {

                if (ed_add_CPE_name_C.equals("")) {

                    hint = Toast.makeText(change_page__environment.this, "???????????????????????????", Toast.LENGTH_SHORT);
                    hint.show();
                }

                if (ed_add_CPE_number_C.equals("")) {

                    hint = Toast.makeText(change_page__environment.this, "???????????????????????????", Toast.LENGTH_SHORT);
                    hint.show();
                }

                if (Add_door == null) {

                    hint = Toast.makeText(change_page__environment.this, "???????????????????????????", Toast.LENGTH_SHORT);
                    hint.show();
                }

                if (Add_strength == null) {

                    hint = Toast.makeText(change_page__environment.this, "???????????????????????????", Toast.LENGTH_SHORT);
                    hint.show();
                }

            }
    };

    public View.OnClickListener BT_add_CPE_Decide_L = view ->
    {
        if (ADD_lock == 1)
        {
            /*
            hint = Toast.makeText(change_page__environment.this, FB_ADD_list,Toast.LENGTH_SHORT);
            hint.show();
            hint = Toast.makeText(change_page__environment.this, FB_ADD_door,Toast.LENGTH_SHORT);
            hint.show();
            hint = Toast.makeText(change_page__environment.this, FB_ADD_strength,Toast.LENGTH_SHORT);
            hint.show();
            hint = Toast.makeText(change_page__environment.this, FB_ADD_number+"",Toast.LENGTH_SHORT);
            hint.show();
             */

            hint = Toast.makeText(change_page__environment.this, "??????????????????????????????????????????????????????????????????", Toast.LENGTH_SHORT);
            hint.show();

            FirebaseDatabase database_environment_ADD = FirebaseDatabase.getInstance();
            DatabaseReference firebase_name_ADD = database_environment_ADD.getReference("environment"); //???????????????

            firebase_name_ADD.child("list").setValue(FB_ADD_list); //?????????????????????
            firebase_name_ADD.child("door").setValue(FB_ADD_door); //?????????????????????
            firebase_name_ADD.child("strength").setValue(FB_ADD_strength); //?????????????????????
            firebase_name_ADD.child("number").setValue(FB_ADD_number); //?????????????????????
            //firebase_name_ADD.child("change_time").setValue(FB_ADD_time);
            firebase_name_ADD.child(ed_add_CPE_number.getText().toString()).setValue(ed_add_CPE_name.getText().toString()); //?????????????????????

            Intent CPE_turn = new Intent();
            CPE_turn.setClass(change_page__environment.this,change_page__environment.class);
            startActivity(CPE_turn);
            finish();
        }

        if (ADD_lock == 0)
        {
            hint = Toast.makeText(change_page__environment.this, "?????????????????????", Toast.LENGTH_SHORT);
            hint.show();
        }
        ADD_lock = 0;
        BT_add_CPE_Decide.setVisibility(View.INVISIBLE);
    };

    //-----------------CHANGE-----------------

    public View.OnClickListener BT_change_CPE_L = view ->
    {
        if(change_room_place_list != 0)
        {
            String ed_change_CPE_name_C = ed_change_CPE_name.getText().toString();

            //??????????????????
            if(!ed_change_CPE_name_C.equals(""))
            {
                for (int i = 0; i < ed_change_CPE_name_C.length(); i++) {
                    if(ed_change_CPE_name_C.charAt(i) == ' ')
                    {
                        hint = Toast.makeText(change_page__environment.this, "????????????????????????", Toast.LENGTH_SHORT);
                        hint.show();

                        Change_name_lock = 0;

                        return;
                    }
                }

                FB_CHANGE_name = ed_change_CPE_name_C;
                //hint = Toast.makeText(change_page__environment.this, "????????????", Toast.LENGTH_SHORT);
                //hint.show();

                Change_name_lock = 1;
            }

            //????????????????????????
            if (Change_door != null)
            {
                FirebaseDatabase database_environment_CHANGE = FirebaseDatabase.getInstance();

                DatabaseReference environment_CHANGE_check = database_environment_CHANGE.getReference("environment").child("door");
                environment_CHANGE_check.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot CHANGE_1) {

                        String E_door = CHANGE_1.getValue(String.class);

                        //hint = Toast.makeText(change_page__environment.this, "????????????", Toast.LENGTH_SHORT);
                        //hint.show();

                        String another_door_before = "";

                        if (change_room_place_list != 1)
                        {
                            try {

                                another_door_before = E_door.substring(0,change_room_place_list-1);
                                //hint = Toast.makeText(change_page__environment.this, another_door_before+"A", Toast.LENGTH_SHORT);
                                //hint.show();

                            }catch (Exception e){

                            }

                        }

                        String another_door_change = Change_door;

                        //hint = Toast.makeText(change_page__environment.this, another_door_change+"B", Toast.LENGTH_SHORT);
                        //hint.show();

                        String another_door_after = "";

                        try {
                            another_door_after = E_door.substring(change_room_place_list);

                            //hint = Toast.makeText(change_page__environment.this, another_door_after+"C", Toast.LENGTH_SHORT);
                            //hint.show();

                        }catch (Exception e) {

                        }

                        String another_door = another_door_before + another_door_change + another_door_after;

                        FB_CHANGE_door = another_door;
                        //hint = Toast.makeText(change_page__environment.this, another_door, Toast.LENGTH_SHORT);
                        //hint.show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Change_door_lock = 1;
            }

            //??????????????????????????????
            if(Change_strength != null)
            {

                //hint = Toast.makeText(change_page__environment.this, "????????????", Toast.LENGTH_SHORT);
                //hint.show();

                FirebaseDatabase database_environment_CHANGE = FirebaseDatabase.getInstance();

                DatabaseReference environment_CHANGE_check = database_environment_CHANGE.getReference("environment").child("strength");
                environment_CHANGE_check.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot CHANGE_2) {

                        String E_strength = CHANGE_2.getValue(String.class);

                        //hint = Toast.makeText(change_page__environment.this, "????????????", Toast.LENGTH_SHORT);
                        //hint.show();

                        String another_strength_before = "";

                        if (change_room_place_list != 1)
                        {
                            another_strength_before = E_strength.substring(0,change_room_place_list-1);
                            //hint = Toast.makeText(change_page__environment.this, another_strength_before+"A", Toast.LENGTH_SHORT);
                            //hint.show();

                        }

                        String another_strength_change = Change_strength;

                        //hint = Toast.makeText(change_page__environment.this, another_strength_change+"B", Toast.LENGTH_SHORT);
                        //hint.show();

                        String another_strength_after = "";

                        try {
                            another_strength_after = E_strength.substring(change_room_place_list);

                            //hint = Toast.makeText(change_page__environment.this, another_strength_after+"C", Toast.LENGTH_SHORT);
                            //hint.show();

                        }catch (Exception e) {

                        }

                        String another_strength = another_strength_before + another_strength_change + another_strength_after;

                        FB_CHANGE_strength = another_strength;
                        //hint = Toast.makeText(change_page__environment.this, another_strength, Toast.LENGTH_SHORT);
                        //hint.show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Change_strength_lock = 1;
            }

            BT_change_CPE_Decide.setVisibility(View.VISIBLE);

            //???????????????
            if (ed_change_CPE_name_C.equals("") && (Change_door == null) && (Change_strength == null))
            {
                hint = Toast.makeText(change_page__environment.this, "????????????????????????", Toast.LENGTH_SHORT);
                hint.show();

                BT_change_CPE_Decide.setVisibility(View.INVISIBLE);
                return;
            }
        }else{
            hint = Toast.makeText(change_page__environment.this, "???????????????????????????", Toast.LENGTH_SHORT);
            hint.show();
        }

    };

    public View.OnClickListener BT_change_CPE_Decide_L = view ->
    {

        FirebaseDatabase database_environment_CHANGE = FirebaseDatabase.getInstance();
        DatabaseReference firebase_name_CHANGE = database_environment_CHANGE.getReference("environment"); //???????????????

        if (Change_name_lock == 1)
        {
            //hint = Toast.makeText(change_page__environment.this, FB_CHANGE_name,Toast.LENGTH_SHORT);
            //hint.show();

            firebase_name_CHANGE.child(change_room_place_number).setValue(FB_CHANGE_name); //?????????????????????
            Change_name_lock = 0;
        }

        if (Change_door_lock == 1)
        {
            //hint = Toast.makeText(change_page__environment.this, FB_CHANGE_door,Toast.LENGTH_SHORT);
            //hint.show();

            firebase_name_CHANGE.child("door").setValue(FB_CHANGE_door); //?????????????????????
            Change_door_lock = 0;
        }

        if (Change_strength_lock == 1)
        {
            //hint = Toast.makeText(change_page__environment.this, FB_CHANGE_strength,Toast.LENGTH_SHORT);
            //hint.show();

            firebase_name_CHANGE.child("strength").setValue(FB_CHANGE_strength); //?????????????????????
            Change_door_lock = 0;
        }


        hint = Toast.makeText(change_page__environment.this, "??????????????????", Toast.LENGTH_SHORT);
        hint.show();

        Intent CPE_turn = new Intent();
        CPE_turn.setClass(change_page__environment.this,change_page__environment.class);
        startActivity(CPE_turn);
        finish();

        BT_change_CPE_Decide.setVisibility(View.INVISIBLE);
    };

    Spinner.OnItemSelectedListener sp_change_CPE_name_L = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int chagne_CPE, long id) {

            //Toast hint = Toast.makeText(change_page__environment.this, parent.getItemAtPosition(look_CPE).toString()+"",Toast.LENGTH_SHORT);
            //hint.show();

            //??????????????????1. 2. 3. ... 11.
            //???????????????????????????3????????????????????????4???

            String room_place = parent.getItemAtPosition(chagne_CPE).toString(); //????????????

            //?????????????????????

            if (chagne_CPE != 0)
            {
                int room_place_number_dot = room_place.indexOf('.');
                change_room_place_number = room_place.substring(0,room_place_number_dot);
            }

            change_room_place_list = chagne_CPE;

            //hint = Toast.makeText(change_page__environment.this, change_room_place_list+"",Toast.LENGTH_SHORT);
            //hint.show();

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};


    Spinner.OnItemSelectedListener sp_change_CPE_door_L = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int chagne_CPE_S, long id) {

            //???????????????
            if (chagne_CPE_S != 0)
            {
                Change_door = String.valueOf(chagne_CPE_S);

                //hint = Toast.makeText(change_page__environment.this, Add_strength+"",Toast.LENGTH_SHORT);
                //hint.show();
            }else{
                Change_door = null;
            }

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};

    Spinner.OnItemSelectedListener sp_change_CPE_strength_L = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int chagne_CPE_S, long id) {

            //???????????????
            if (chagne_CPE_S != 0)
            {
                Change_strength = String.valueOf(chagne_CPE_S);

                //hint = Toast.makeText(change_page__environment.this, Add_strength+"",Toast.LENGTH_SHORT);
                //hint.show();
            }else{
                Change_strength = null;
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};


    //---------------------delete---------------------

    Spinner.OnItemSelectedListener sp_delete_CPE_name_L = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int delete_CPE_S, long id) {

            String room_place = parent.getItemAtPosition(delete_CPE_S).toString(); //????????????

            //?????????????????????
            if (delete_CPE_S != 0)
            {
                //String.indexOf -> ????????????????????????????????????
                //???????????????"."???index = 1??????????????????2

                int room_place_number_dot = room_place.indexOf('.');
                delete_room_place_number = room_place.substring(0,room_place_number_dot);
                delete_room_place_list   = delete_CPE_S;

                //hint = Toast.makeText(change_page__environment.this, room_place.substring(0,room_place_number_dot)+"",Toast.LENGTH_SHORT);
                //hint.show();
            }

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};

    public View.OnClickListener BT_Dlook_CPE_L = view ->
    {
        try {
            if(delete_room_place_list != 0 )
            {
                FirebaseDatabase database_environment_name = FirebaseDatabase.getInstance();
                DatabaseReference firebase_name_find = database_environment_name.getReference("environment").child(delete_room_place_number);
                firebase_name_find.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot_1) {

                        String name = dataSnapshot_1.getValue(String.class);
                        D_N_R.setText(name);
                        D_Nu_R.setText(delete_room_place_number);

                        DatabaseReference firebase_list_find = database_environment_name.getReference("environment").child("door");
                        firebase_list_find.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot_2) {

                                String E_door = dataSnapshot_2.getValue(String.class);

                                //hint = Toast.makeText(change_page__environment.this, room_place_list+"",Toast.LENGTH_SHORT);
                                //hint.show();

                                for(int i = 0; i < E_door.length(); i++)
                                {
                                    if((delete_room_place_list-1) == i)
                                    {
                                        //hint = Toast.makeText(change_page__environment.this, E_door.substring(i,i+1)+"",Toast.LENGTH_SHORT);
                                        //hint.show();

                                        try {
                                            if(E_door.substring(i, i + 1).equals("1")) { D_D_R.setText("??????"); } //???
                                            if(E_door.substring(i, i + 1).equals("2")) { D_D_R.setText("??????"); } //???

                                        }catch (Exception e){
                                            D_D_R.setText("");
                                        }
                                    }
                                }

                                DatabaseReference firebase_number_check = database_environment_name.getReference("environment").child("strength");
                                firebase_number_check.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot_3) {

                                        String E_strength = dataSnapshot_3.getValue(String.class);

                                        for(int i = 0; i < E_door.length(); i++)
                                        {
                                            if((delete_room_place_list-1) == i)
                                            {
                                                try {
                                                    if(E_strength.substring(i, i + 1).equals("1")) { D_S_R.setText("???"); } //???
                                                    if(E_strength.substring(i, i + 1).equals("2")) { D_S_R.setText("???"); } //???
                                                    if(E_strength.substring(i, i + 1).equals("3")) { D_S_R.setText("???"); } //???
                                                }catch (Exception e){

                                                    D_S_R.setText("");
                                                }
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
            }else{

                hint = Toast.makeText(change_page__environment.this, "??????????????????",Toast.LENGTH_SHORT);
                hint.show();
            }

        }catch (Exception e){

        }


    };

    public View.OnClickListener BT_delete_CPE_L = view ->
    {
        Delete_lock = 1;
        BT_delete_CPE_decide.setVisibility(View.VISIBLE);

        try {
            FirebaseDatabase database_environment_DELETE = FirebaseDatabase.getInstance();
            DatabaseReference firebase_name_DELETE = database_environment_DELETE.getReference("environment"); //???????????????
            DatabaseReference environment_list_check = database_environment_DELETE.getReference("environment").child("list");

            environment_list_check.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot DELETE_1) {

                    String E_list = DELETE_1.getValue(String.class);
                    String[] E_multilist = E_list.split(" ");

                    delete_list_rank_before = 0;
                    delete_list_rank_after = 0;

                    //????????? 1 2 3 24 25?????????24
                    //??????????????? 1 2 3 25

                    String new_list = "";

                    for (int i = 0; i < E_multilist.length; i++) {

                        if(!delete_room_place_number.equals(E_multilist[i]))
                        {
                            new_list = new_list + E_multilist[i] + " ";
                        }
                    }

                    //hint = Toast.makeText(change_page__environment.this, new_list, Toast.LENGTH_SHORT);
                    //hint.show(); //?????????????????????

                    FB_DELETE_list = new_list;

                    DatabaseReference environment_door_check = database_environment_DELETE.getReference("environment").child("door");
                    environment_door_check.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot DELETE_2) {

                            String E_door = DELETE_2.getValue(String.class);

                            minus_door = "";

                            try {
                                for (int i = 0; i < E_multilist.length; i++) {

                                    if(!delete_room_place_number.equals(E_multilist[i]))
                                    {
                                        minus_door = minus_door + E_door.substring(i,i+1) + "";
                                    }
                                }
                            }catch (Exception e){

                            }

                            //hint = Toast.makeText(change_page__environment.this, minus_door, Toast.LENGTH_SHORT); //?????????
                            //hint.show();

                            FB_DELETE_door = minus_door;

                            DatabaseReference firebase_strength_delete = database_environment_DELETE.getReference("environment").child("strength");
                            firebase_strength_delete.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot DELETE_3) {

                                    String E_strength = DELETE_3.getValue(String.class);

                                    minus_strength = "";

                                    try {
                                        for (int i = 0; i < E_multilist.length; i++) {

                                            if(!delete_room_place_number.equals(E_multilist[i]))
                                            {
                                                minus_strength = minus_strength + E_strength.substring(i,i+1) + "";
                                            }
                                        }

                                    }catch (Exception e) {

                                    }

                                    FB_DELETE_strength = minus_strength;
                                    Delete_number = Integer.parseInt(Hint.getText().toString()) - 1;
                                    FB_DELETE_number = Delete_number;

                                    return;

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });

        }catch (Exception e) {

        }

    };

    public View.OnClickListener BT_delete_CPE_Decide_L = view ->
    {
        if (Delete_lock == 1)
        {
            /*
            hint = Toast.makeText(change_page__environment.this, FB_DELETE_list,Toast.LENGTH_SHORT);
            hint.show();
            hint = Toast.makeText(change_page__environment.this, FB_DELETE_door,Toast.LENGTH_SHORT);
            hint.show();
            hint = Toast.makeText(change_page__environment.this, FB_DELETE_strength,Toast.LENGTH_SHORT);
            hint.show();
            hint = Toast.makeText(change_page__environment.this, FB_DELETE_number+"",Toast.LENGTH_SHORT);
            hint.show();
             */

            hint = Toast.makeText(change_page__environment.this, "??????????????????", Toast.LENGTH_SHORT);
            hint.show();

            FirebaseDatabase database_environment_DELETE = FirebaseDatabase.getInstance();
            DatabaseReference firebase_name_DELETE = database_environment_DELETE.getReference("environment"); //???????????????


            firebase_name_DELETE.child("list").setValue(FB_DELETE_list); //?????????????????????
            firebase_name_DELETE.child("door").setValue(FB_DELETE_door); //?????????????????????
            firebase_name_DELETE.child("strength").setValue(FB_DELETE_strength); //?????????????????????
            firebase_name_DELETE.child("number").setValue(FB_DELETE_number); //?????????????????????
            firebase_name_DELETE.child(delete_room_place_number).removeValue(); //?????????????????????

            Intent CPE_turn = new Intent();
            CPE_turn.setClass(change_page__environment.this,change_page__environment.class);
            startActivity(CPE_turn);
            finish();

        }

        if (Delete_lock == 0)
        {
            hint = Toast.makeText(change_page__environment.this, "?????????????????????", Toast.LENGTH_SHORT);
            hint.show();
        }
        Delete_lock = 0;
        BT_delete_CPE_decide.setVisibility(View.INVISIBLE);
    };
}
