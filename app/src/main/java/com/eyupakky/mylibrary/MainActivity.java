package com.eyupakky.mylibrary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eyupakky.mylibrary.Pojo.BookPojo;
import com.eyupakky.mylibrary.Pojo.SetBookData;
import com.eyupakky.mylibrary.Pojo.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{
    private ImageView addBook,login;
    Dialog dialog,mBottomSheetDialog;
    public static boolean first=true;
    public static String userId;
    SharedPreferences preferences ;
    SharedPreferences.Editor editor;
    private int PICK_IMAGE_REQUEST = 1;
    private String baseUrl="https://www.googleapis.com/books/v1/";
    int PERMISSION_ALL = 1;
    RetrofitInterface retrofitInterface;
    Retrofit retrofit;
    RecyclerView recyclerView;
    private GoogleApiClient googleApi;
    private SignInButton signButton;
    public static final int SIGN_IN_CODE=777;
    FirebaseSetDataAndGetData fire;
    public ItemAdapter itemAdapter;
    SetBookData setBookData;
    List<SetBookData>bookDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bookDatas=new ArrayList<>();
        setBookData=new SetBookData();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();
        addBook=(ImageView)findViewById(R.id.addBook);
        login=(ImageView)findViewById(R.id.login);
        addBook.setOnClickListener(this);
        login.setOnClickListener(this);
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();
        googleApi=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();
        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        retrofit =new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory( GsonConverterFactory.create())
                .build();
        retrofitInterface=retrofit.create(RetrofitInterface.class);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Checking the request code of our request
        if(requestCode == PERMISSION_ALL){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            }else{
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void showDialog(){
        mBottomSheetDialog = new Dialog (MainActivity.this,
                R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView (R.layout.login);
        mBottomSheetDialog.setCancelable (true);
        mBottomSheetDialog.getWindow ().setLayout (LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow ().setGravity (Gravity.BOTTOM);
        mBottomSheetDialog.show ();
        signButton=mBottomSheetDialog.findViewById(R.id.signButton);
        signButton.setOnClickListener(this);
    }
    private void showAddDialog(){
        dialog = new Dialog (MainActivity.this,
                R.style.MaterialDialogSheet);
        dialog.setContentView (R.layout.add_book);
        dialog.setCancelable (true);
        dialog.getWindow ().setLayout (LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow ().setGravity (Gravity.BOTTOM);
        dialog.findViewById(R.id.cameraOpen).setOnClickListener(this);
        dialog.findViewById(R.id.newBookAdd).setOnClickListener(this);
        setBookData.setBookId("");
        dialog.show ();
    }
    private void showBookViewDialog(SetBookData data){
        dialog = new Dialog (MainActivity.this,
                R.style.MaterialDialogSheet);
        dialog.setContentView (R.layout.add_book);
        dialog.setCancelable (true);
        dialog.getWindow ().setLayout (LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow ().setGravity (Gravity.BOTTOM);
        dialog.findViewById(R.id.cameraOpen).setOnClickListener(this);
        dialog.findViewById(R.id.newBookAdd).setOnClickListener(this);
        ((EditText)dialog.findViewById(R.id.editTextExplanation)).setText(data.getBookExplanation());
        for (String author : data.getBookAuthorName())
        ((EditText)dialog.findViewById(R.id.textViewYazar)).setText(((EditText)dialog.findViewById(R.id.textViewYazar)).getText()+author);
        ((EditText)dialog.findViewById(R.id.bookName)).setText(data.getBookName());
        Picasso.with (this).load(data.getBookImageUri()).into((ImageView)dialog.findViewById(R.id.imageViewBook));
        setBookData.setBookId(data.getBookId());
        dialog.show ();
    }
    @Subscribe
    public void setListData(final List<SetBookData> data) {
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager manager =new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        itemAdapter = new ItemAdapter(data, new ItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                showBookViewDialog(data.get(position));
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(itemAdapter);
    }
    public void clearManager(){
        itemAdapter.notifyDataSetChanged();
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(final String isbn){
        Toast.makeText(this,isbn,Toast.LENGTH_LONG).show();
        Call<BookPojo>call=retrofitInterface.setData("isbn:"+isbn);
        call.enqueue(new Callback<BookPojo>() {
            @SuppressLint("WrongViewCast")
            @Override
            public void onResponse(Call<BookPojo> call, Response<BookPojo> response) {
                if (response.isSuccessful()){
                    setBookData.setBookId(isbn);
                    EditText bookName = dialog.findViewById(R.id.bookName);
                    TextView author=dialog.findViewById(R.id.textViewYazar);
                    ImageView imageView=dialog.findViewById(R.id.imageViewBook);
                    if (response.body().getItems()!=null) {
                        if (response.body().getItems().get(0).getVolumeInfo().getImageLinks()!=null) {
                            setBookData.setBookImageUri(response.body().getItems().get(0).getVolumeInfo().getImageLinks().getSmallThumbnail());
                            Picasso.with(MainActivity.this).load(setBookData.getBookImageUri()).into(imageView);
                        }
                        setBookData.setBookName(response.body().getItems().get(0).getVolumeInfo().getTitle());
                        bookName.setText(setBookData.getBookName());

                        if (response.body().getItems() != null) {
                            if (response.body().getItems().get(0).getVolumeInfo().getAuthors()!=null) {
                                setBookData.setBookAuthorName(response.body().getItems().get(0).getVolumeInfo().getAuthors());
                                for (int i = 0; i < setBookData.getBookAuthorName().size(); i++)
                                    author.setText(setBookData.getBookAuthorName().get(i));
                            }
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this,getString(R.string.bilgibulunamadi),Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<BookPojo> call, Throwable t) {
                Logger.e(t.getMessage());
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //google ile giriş yapmak için yazıldı.
        if (requestCode==SIGN_IN_CODE ){
            GoogleSignInResult result= Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignResult(result);
        }
        //Galeriden resimi alarak base64 formatına çeviriyorum.
        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void handleSignResult(GoogleSignInResult result) {
        if (result.isSuccess()){
            editor.putString("resim",String.valueOf(result.getSignInAccount().getPhotoUrl()));
            editor.putString("email",String.valueOf(result.getSignInAccount().getEmail()));
            editor.putString("myid",result.getSignInAccount().getId());
            editor.commit();
            User user = new User();
            user.setId(result.getSignInAccount().getId());
            user.setName(result.getSignInAccount().getDisplayName());
            fire=new FirebaseSetDataAndGetData(user);
            if (user.getId()!=null)
            fire.getData(user.getId());
            Picasso.with(this).load(String.valueOf(result.getSignInAccount().getPhotoUrl())).into(login);
            Log.e(String.valueOf(result.getSignInAccount().getPhotoUrl()),result.getSignInAccount().getEmail()
            +" *** "+ result.getSignInAccount().getId()+" *** " + result.getSignInAccount().getDisplayName());
            mBottomSheetDialog.dismiss();
        } else{
            Toast.makeText(this,getString(R.string.loginHatasi ),Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login:
                showDialog();
                break;
            case R.id.addBook:
                if (!preferences.getString("myid","-").equals("-"))
                showAddDialog();
                else {
                    showDialog();
                    Toast.makeText(this, getString(R.string.girisyap), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.signButton:
                Intent intent=Auth.GoogleSignInApi.getSignInIntent(googleApi);
                startActivityForResult(intent,SIGN_IN_CODE );
                break;
            case R.id.cameraOpen:
                Intent i=new Intent(this,ScannerActivity.class);
                startActivity(i);
                break;
            case R.id.newBookAdd:
                setBookData.setBookExplanation(((EditText)dialog.findViewById(R.id.editTextExplanation)).getText().toString());
                setBookData.setBookName(((EditText)dialog.findViewById(R.id.bookName)).getText().toString());
                List<String>ls=new ArrayList<>();
                ls.add(((EditText)dialog.findViewById(R.id.textViewYazar)).getText().toString());
                setBookData.setBookAuthorName(ls);
                fire=new FirebaseSetDataAndGetData(setBookData);
                break;
        }
    }
    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        userId=preferences.getString("myid","-1");
        Picasso.with(this).load(preferences.getString("resim","-1")).error(R.mipmap.human).into(login);
        if (!userId.equals("-1")) {
            fire = new FirebaseSetDataAndGetData();
            fire.getData(userId);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,R.string.loginHatasi,Toast.LENGTH_LONG).show();
    }
}
