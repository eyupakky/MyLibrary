package com.eyupakky.mylibrary;

import android.content.Context;
import android.util.Log;
import com.eyupakky.mylibrary.Pojo.SetBookData;
import com.eyupakky.mylibrary.Pojo.User;
import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by eyupakkaya on 6.01.2018.
 */

public class FirebaseSetDataAndGetData {

    String TAG="FirebaseSetDataAndGetData";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    List<SetBookData>data=null;
    public FirebaseSetDataAndGetData(){}
    //Firebase kitap ekleme
    public FirebaseSetDataAndGetData(SetBookData object){
        myRef = database.getReference("user");
        if (object.getBookId()!=null)
        myRef.child(MainActivity.userId).child(object.getBookId()).setValue(object);
        else {
            myRef = FirebaseDatabase.getInstance().getReference("user");
            object.setBookId(myRef.push().getKey());
            myRef.child(MainActivity.userId).child(object.getBookId()).setValue(object);
        }
        myRef = database.getReference("books");
        myRef.child(MainActivity.userId).child(object.getBookId()).setValue(object);
    }
    public List<SetBookData> getData(String userId){
        myRef = database.getReference("user/"+userId);
        data=new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                data.add(dataSnapshot1.getValue(SetBookData.class));
                if (MainActivity.first){
                    EventBus.getDefault().post(data);
                    MainActivity.first=false;
                }
                return;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return null;
    }
    //Kullanıcı ekleme
    //Kullanıcı Kontrolü
    public FirebaseSetDataAndGetData(final User user){
        myRef = database.getReference("user/"+user.getId());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String value = dataSnapshot.getValue(String.class);
                    Log.d(TAG, "Value is: " + value);
                    if (value==null){
                        myRef = database.getReference("user");
                        myRef.child(user.getId()).setValue(user.getId());
                        myRef = database.getReference("users");
                        myRef.child(user.getId()).setValue(user);
                    }
                }
                catch (Exception e) {}
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

    }
    }
