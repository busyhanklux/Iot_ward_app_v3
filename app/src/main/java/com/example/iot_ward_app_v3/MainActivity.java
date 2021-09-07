package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView tvrssi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvrssi = (TextView)findViewById(R.id.tvrssi);


        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //命名 myRef 的 DatabaseReference。路徑，child為路徑的分支
        DatabaseReference myRef = database.getReference("esp32 no_0 ").child("distance");

        //測試用，寫資料至資料庫
        //myRef.setValue("-35");

        // Read from the database(從資料庫讀資料)

        //使用 myRef 的 DatabaseReference
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //注意資料型態、注意資料型態、注意資料型態，不然會崩潰

                //String value = dataSnapshot.getValue(String.class);
                double num = dataSnapshot.getValue(double.class); //distance用浮點數(也可以用double)
                //Log.d("TAG", "Value is: " + value);
                Log.d("TAG", "Value is: " + num);
                tvrssi.setText(Double.toString(num));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
                }
            });
        }
    }

//來源https://mnya.tw/cc/word/1495.html