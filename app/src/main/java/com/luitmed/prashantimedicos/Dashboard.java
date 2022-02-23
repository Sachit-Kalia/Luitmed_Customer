package com.luitmed.prashantimedicos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.luitmed.prashantimedicos.Models.Common;
import com.luitmed.prashantimedicos.Models.FCMResponse;
import com.luitmed.prashantimedicos.Models.FCMSendData;
import com.luitmed.prashantimedicos.Remote.RetrofitFCMClient;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FirebaseAuth firebaseAuth;
    ImageSlider slider1, slider2;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    private ArrayList<Product> productList;
    private HomeRecyclerViewAdapter adapter;
    ShimmerFrameLayout shimmerFrameLayout;
    private TextView searchView, userName, userPhone;
    private ImageButton cartIcon;
    private Button uploadPrescription;
    RelativeLayout all, medicines, health, covid;
    public TextView cqTv;
    private RoomDB database;
    private ProgressDialog progressDialog;
    IFCMService ifcmService;
    CompositeDisposable compositeDisposable;
    private List<SlideModel> imgUrls;
    private List<SlideModel> imgUrls1;


    // permission constants
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    // image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    // permission arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //image uri
    private Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_PrashantiMedicos);
        setContentView(R.layout.activity_dashboard);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.sideNav);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.toolbar);
        cqTv = toolbar.findViewById(R.id.cartQuantity);
        cartIcon = toolbar.findViewById(R.id.homeCart);
        slider1 = findViewById(R.id.is1);
        slider2 = findViewById(R.id.is2);
        recyclerView = findViewById(R.id.homeProductsRV);
        all = findViewById(R.id.cAll);
        medicines = findViewById(R.id.cMedicines);
        health = findViewById(R.id.cHealth);
        covid = findViewById(R.id.cCovid);
        uploadPrescription = findViewById(R.id.uploadPrescription);
        shimmerFrameLayout = findViewById(R.id.shimmerHome);
        searchView = findViewById(R.id.searchView);
        productList = new ArrayList<>();
        database = RoomDB.getInstance(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);
        compositeDisposable = new CompositeDisposable();

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        Menu menu = navigationView.getMenu();
        if(firebaseAuth.getCurrentUser() != null){
            menu.findItem(R.id.mLogin).setVisible(false);
        }else{
            menu.findItem(R.id.mLogout).setVisible(false);
            menu.findItem(R.id.mAccount).setVisible(false);
            menu.findItem(R.id.mOrders).setVisible(false);
            menu.findItem(R.id.mPrescriptions).setVisible(false);
        }
        imgUrls = new ArrayList<>();
        imgUrls1 = new ArrayList<>();

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.navOpen, R.string.navClose);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.mHome);

        View headerView = navigationView.getHeaderView(0);

        userName = (TextView) headerView.findViewById(R.id.NameTV);
        userPhone = (TextView) headerView.findViewById(R.id.PhoneTV);

        LoadAllProducts();

        int cartQuantity = database.mainDao().getAll().size();
        if(cartQuantity > 0){
            cqTv.setText("" + cartQuantity);
            cqTv.setVisibility(View.VISIBLE);
        }


        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Cart.class);
                startActivity(intent);
            }
        });

        // Categories on click

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Categories.class));
            }
        });

        medicines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Products.class);
                intent.putExtra("type", "category");
                intent.putExtra("query", "Medicines");
                startActivity(intent);
            }
        });

        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Products.class);
                intent.putExtra("type", "category");
                intent.putExtra("query", "Health Care Products");
                startActivity(intent);
            }
        });

        covid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Products.class);
                intent.putExtra("type", "category");
                intent.putExtra("query", "COVID-19");
                startActivity(intent);
            }
        });


        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        uploadPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(getApplicationContext(), Register.class));
                    Toast.makeText(Dashboard.this, "You need to be logged in order to upload prescription", Toast.LENGTH_SHORT).show();
                }else{
                    showImagePickDialog();
                }
            }
        });

    }

    private void LoadAllProducts() {
        productList = new ArrayList<>();
        // get products from firestore

        shimmerFrameLayout.startShimmerAnimation();
        db.collection("products").orderBy("discount", Query.Direction.DESCENDING).limit(10).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        productList.clear();
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                            String productID = documentSnapshot.get("productID").toString();
                            String title = documentSnapshot.get("title").toString();
                            String description = documentSnapshot.get("description").toString();
                            String category = documentSnapshot.get("category").toString();
                            String quantity = documentSnapshot.get("quantity").toString();
                            String productIcon = documentSnapshot.get("productIcon").toString();
                            String price = documentSnapshot.get("price").toString();
                            String discountedPrice = documentSnapshot.get("discountedPrice").toString();
                            String timestamp = documentSnapshot.get("timestamp").toString();
                            String uid = documentSnapshot.get("uid").toString();
                            String stock = documentSnapshot.get("stock").toString();

//                            if(!stock.equals("0")){
                                Product homeProduct = new Product(productID, title, description, category, quantity, productIcon, price, discountedPrice, timestamp, uid, stock);
                                productList.add(homeProduct);
//                            }

                        }


                        adapter = new HomeRecyclerViewAdapter(Dashboard.this, productList);
                        recyclerView.setAdapter(adapter);
                        loadBanners();

                        if(firebaseAuth.getCurrentUser() != null) {
                            getToken();
                            getDetails();
                        }
                    }
                });
    }

    private void getDetails() {

        db.collection("users").document(firebaseAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                  String name = documentSnapshot.get("name").toString();
                  userName.setText(name);
                  userPhone.setText(firebaseAuth.getCurrentUser().getPhoneNumber());
            }
        });

    }

    private void loadBanners() {



        db.collection("upperBanners").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        imgUrls.clear();
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                              String url = documentSnapshot.get("url").toString();
                              imgUrls.add(new SlideModel(url, ScaleTypes.FIT));
                        }
                        slider1.setImageList(imgUrls);
                        db.collection("lowerBanners").get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        imgUrls1.clear();
                                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                            String url1 = documentSnapshot.get("url").toString();
                                            imgUrls1.add(new SlideModel(url1, ScaleTypes.FIT));
                                        }
                                        slider2.setImageList(imgUrls1);
                                        shimmerFrameLayout.stopShimmerAnimation();
                                        shimmerFrameLayout.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                    }
                                });
                    }
                });
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Log.i("msg", "Token fetch failed", task.getException());
                    return;
                }

                Common.currentToken = task.getResult();
                Common.currentUser = firebaseAuth.getCurrentUser();
                Common.updateToken(Dashboard.this, Common.currentToken);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        LoadAllProducts();
        cqTv.setText("" + database.mainDao().getAll().size());
        if(cqTv.getText().toString().equals("0")){
            cqTv.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mHome:
                break;
            case R.id.mCategories:
                startActivity(new Intent(getApplicationContext(), Categories.class));
                break;
            case R.id.mAccount:
                startActivity(new Intent(getApplicationContext(), UserAccount.class));
                break;
            case R.id.mCart:
                startActivity(new Intent(getApplicationContext(), Cart.class));
                break;
            case R.id.mOrders:
                startActivity(new Intent(getApplicationContext(), MyOrders.class));
                break;
            case R.id.mUpload:
                if(firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(getApplicationContext(), Register.class));
                }else{
                    startActivity(new Intent(getApplicationContext(), UploadPrescription.class));
                }
                break;
            case R.id.mPrescriptions:
                startActivity(new Intent(getApplicationContext(), Prescriptions.class));
                break;
            case R.id.mLogout:
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                finish();
                break;
            case R.id.mLogin:
                startActivity(new Intent(getApplicationContext(), Register.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showImagePickDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            if(checkCameraPermissions()) pickFromCamera();
                            else requestCameraPermissions();
                        }
                        else{
                            if(checkStoragePermissions()) pickFromGallery();
                            else requestStoragePermissions();
                        }
                    }
                }).show();
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_desc");

        imageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermissions(){
        boolean res = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return res;
    }

    private void requestStoragePermissions(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions(){
        boolean res = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean res1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return (res && res1);
    }

    private void requestCameraPermissions(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    // handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }else{
                        Toast.makeText(this, "Camera and Storage access is required. Please grant permissions!!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length > 0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickFromGallery();
                    }else{
                        Toast.makeText(this, "Storage access is required. Please grant permissions!!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // handle image pick results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                imageURI = data.getData();
                try {
                    sendPrescription();
                } catch (IOException e) {
                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
//                productIcon.setImageURI(imageURI);
            }else if(requestCode == IMAGE_PICK_CAMERA_CODE){
//                productIcon.setImageURI(imageURI);
                try {
                    sendPrescription();
                } catch (IOException e) {
                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendPrescription() throws IOException {

        progressDialog.setMessage("Sending your prescription.");
        progressDialog.show();

        String timestamp = String.valueOf(System.currentTimeMillis());
        String filePath = "prescriptions/" + "" + timestamp;
        String prId = "P" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePath);
        Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] data = baos.toByteArray();
//        storageReference.putFile(imageURI)
        storageReference.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        Uri downloadImageUri = uriTask.getResult();
                        String uri = downloadImageUri.toString();

                        if(uriTask.isSuccessful()){
                            // url of image received
                            HashMap<String, Object> prescription = new HashMap<>();
                            prescription.put("id", prId);
                            prescription.put("image", uri);
                            prescription.put("cId", firebaseAuth.getUid());
                            prescription.put("timestamp", timestamp);


                            db.collection("prescriptions").document(prId).set(prescription)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            prepareNotification(prId);
                                            Toast.makeText(Dashboard.this, "Prescription sent successfully!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Dashboard.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void prepareNotification(String oid) {
        Map<String, String> data = new HashMap<>();
        data.put(Common.NOT_TITLE, "New prescription recieved");
        data.put(Common.NOT_CONTENT, "You have a new prescription id: " + oid);
        data.put(Common.NOT_ID, oid);
        FCMSendData sendData = new FCMSendData(Common.createOrder(), data);

        compositeDisposable.add(ifcmService.sendNotification(sendData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FCMResponse>() {
                    @Override
                    public void accept(FCMResponse fcmResponse) throws Exception {
                        progressDialog.dismiss();
                    }
                }, throwable -> {
                    progressDialog.dismiss();
                }));

    }

}
