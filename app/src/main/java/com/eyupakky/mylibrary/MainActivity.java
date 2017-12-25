package com.eyupakky.mylibrary;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.shaz.library.erp.RuntimePermissionHandler;
import com.shaz.library.erp.RuntimePermissionUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{
    private ImageView addBook,login;
    public static String BASE_URL="";
    private static int IMG_RESULT = 1;
    private final int REQ_CODE_CAMERA_PERMISSION = 1001;
    RetrofitInterface retrofitInterface;
    Retrofit retrofit;
    RecyclerView recyclerView;
    private GoogleApiClient googleApi;
    private SignInButton signButton;
    public static final int SIGN_IN_CODE=777;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        /*
        retrofit =new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory( GsonConverterFactory.create())
                .build();
        retrofitInterface=retrofit.create(RetrofitInterface.class);
        */
        setListData();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RuntimePermissionHandler.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
    private void showDialog(){
        final Dialog mBottomSheetDialog = new Dialog (MainActivity.this,
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
        final Dialog dialog = new Dialog (MainActivity.this,
                R.style.MaterialDialogSheet);
        dialog.setContentView (R.layout.add_book);
        dialog.setCancelable (true);
        dialog.getWindow ().setLayout (LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow ().setGravity (Gravity.BOTTOM);
        dialog.findViewById(R.id.cameraOpen).setOnClickListener(this);
        dialog.show ();
    }
    private void setListData() {
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager manager =new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        List<String>list =new ArrayList<>();
        list.add("Olasılıksız");
        list.add("Kurtlarla Dans");
        ItemAdapter itemAdapter = new ItemAdapter(list, new ItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {

            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(itemAdapter);
    }
    private void getData(){
        Call<List<String>>call=retrofitInterface.getData();
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()){
                }
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Logger.e(t.getMessage());
            }
        });
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==SIGN_IN_CODE ){
            GoogleSignInResult result= Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignResult(result);
        }
    }
    private void handleSignResult(GoogleSignInResult result) {
        if (result.isSuccess()){
            Log.e(String.valueOf(result.getSignInAccount().getPhotoUrl()),result.getSignInAccount().getEmail()
            +" *** "+ result.getSignInAccount().getId()+" *** " + result.getSignInAccount().getDisplayName());
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
                showAddDialog();
                break;
            case R.id.signButton:
                Intent intent=Auth.GoogleSignInApi.getSignInIntent(googleApi);
                startActivityForResult(intent,SIGN_IN_CODE );
                break;
            case R.id.cameraOpen:
                openCamera(view);
                break;
        }
    }
    public void openCamera(View view) {
        RuntimePermissionHandler.requestPermission(REQ_CODE_CAMERA_PERMISSION, this, mPermissionListener, RuntimePermissionUtils.StoragePermission);
    }

    private RuntimePermissionHandler.PermissionListener mPermissionListener = new RuntimePermissionHandler.PermissionListener() {
        @Override
        public void onRationale(final @NonNull RuntimePermissionHandler.PermissionRequest permissionRequest, final Activity target, final int requestCode, @NonNull final String[] permissions) {
        }

        @Override
        public void onAllowed(int requestCode, @NonNull String[] permissions) {
            switch (requestCode) {
                case REQ_CODE_CAMERA_PERMISSION:
                    openCamera();
                    break;
            }
        }

        @Override
        public void onDenied(final @NonNull RuntimePermissionHandler.PermissionRequest permissionRequest, Activity target, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, RuntimePermissionHandler.DENIED_REASON deniedReason) {
            if (deniedReason == RuntimePermissionHandler.DENIED_REASON.USER) {
                switch (requestCode) {
                    case REQ_CODE_CAMERA_PERMISSION:
                        Toast.makeText(target, "Hata", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }

        @Override
        public void onNeverAsk(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                case REQ_CODE_CAMERA_PERMISSION:
                    new AlertDialog.Builder(MainActivity.this)
                            .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(@NonNull DialogInterface dialog, int which) {
                                    RuntimePermissionUtils.openAppSettings(MainActivity.this);
                                }
                            })
                            .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(@NonNull DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false)
                            .setMessage("İzin")
                            .show();
                    break;
            }
        }
    };

    private void openCamera() {
        Intent intent;
        intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMG_RESULT);
    }
}
