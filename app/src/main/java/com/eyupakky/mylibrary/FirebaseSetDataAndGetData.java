package com.eyupakky.mylibrary;

import android.content.Context;
import android.util.Log;
import com.eyupakky.mylibrary.Pojo.SetBookData;
import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by eyupakkaya on 6.01.2018.
 */

public class FirebaseSetDataAndGetData {

    String TAG="FirebaseSetDataAndGetData";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    private int userId;
    //Firebase kitap ekleme
    public FirebaseSetDataAndGetData(SetBookData object){
        myRef = database.getReference("user");
        myRef.child(MainActivity.userId).child(object.getBookId()).setValue(object);
    }
    //Kullanıcı ekleme
    //Kullanıcı Kontrolü
    public FirebaseSetDataAndGetData(final String userId){
        myRef = database.getReference("user/"+userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
                if (value==null){
                    myRef.setValue(userId);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

    }
    }
