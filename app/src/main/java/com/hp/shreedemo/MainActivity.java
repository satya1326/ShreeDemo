package com.hp.shreedemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hp.shreedemo.LocationUtil.LocationHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ActivityCompat.OnRequestPermissionsResultCallback {
    //TODO: views initialisation
    private ImageView imageUpload;
    private TextView locationTxt;
    AlertDialog.Builder pictureDialog;
    //TODO: this is for the image upload
    private File mImageFile;
    private Bitmap currentImage;
    public final int GALLERY = 1;
    public final int CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/satyaImg";
    public final String APP_TAG = "MyCustomApp";
    public String photoFileName = "photo.jpg";
    File photoFile;

    //private FusedLocationProviderClient mFusedLocationClient;

    private Location mLastLocation;

    double latitude;
    double longitude;

    LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseViews();
    }

    private void initialiseViews() {
        imageUpload = findViewById(R.id.imageUpload);
       // mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationTxt = findViewById(R.id.locationTxt);
        locationTxt.setVisibility(View.GONE);
        imageUpload.setOnClickListener(this);
        locationHelper=new LocationHelper(this);
        locationHelper.checkpermission();

        // check availability of play services
        if (locationHelper.checkPlayServices()) {

            // Building the GoogleApi client
            locationHelper.buildGoogleApiClient();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationHelper.checkPlayServices();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageUpload:
                //TODO: calling the image upload method here
                mLastLocation=locationHelper.getLocation();

                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                    getAddress();

                } else {
                    MyToast.toastLong(this,"Couldn't get the location. Make sure location is enabled on the device");
                }
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                mFusedLocationClient.getLastLocation()
//                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                            @Override
//                            public void onSuccess(Location location) {
//
//                                Log.d("location_found",String.valueOf(location));
//                                // Got last known location. In some rare situations this can be null.
//                                if (location != null) {
//                                    // Logic to handle location object
//                                }
//                            }
//                        });
                break;
        }
    }

    public void getAddress()
    {
        openingImageAlert();

        Address locationAddress;

        locationAddress=locationHelper.getAddress(latitude,longitude);

        if(locationAddress!=null)
        {

            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();


            String currentLocation;

            if(!TextUtils.isEmpty(address))
            {
                currentLocation=address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation+="\n"+address1;

                if (!TextUtils.isEmpty(city))
                {
                    currentLocation+="\n"+city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+=" - "+postalCode;
                }
                else
                {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+="\n"+postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation+="\n"+state;

                if (!TextUtils.isEmpty(country))
                    currentLocation+="\n"+country;

                locationTxt.setText(currentLocation);
                locationTxt.setVisibility(View.VISIBLE);

            }

        }
        else
            MyToast.toastLong(this,"Something went wrong");
    }

    private void openingImageAlert() {
        //TODO: my alert dialog
//        AlertDialog.Builder imgDialog  = new AlertDialog.Builder(this);
//        imgDialog.setMessage("Select image from Camera or Gallery");
//        imgDialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        imgDialog.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        imgDialog.create();
        pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Add photo");
        String[] pictureDialogItems = {
                "From Gallery",
                "From Camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
                                    //TODO: enters here in case Permission is not granted
                                    Log.d("entered","here0");
                                    //TODO: showing an explanation to user
                                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                                        MyToast.toastLong(MainActivity.this,"Application needs storage permission to upload image");
                                        Log.d("entered","here1");
                                    }
                                    else {
                                        Log.d("entered","here2");
                                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},GALLERY);
                                    }}
                                else {
                                    choosePhotoFromGallery();
                                }
                                break;
                            case 1:
                                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){
                                    //TODO: enters here in case Permission is not granted
                                    Log.d("entered","here0");
                                    //TODO: showing an explanation to user
                                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.CAMERA)){
                                        MyToast.toastLong(MainActivity.this,"Application needs camera permission to upload image");
                                        Log.d("entered","here1");
                                    }
                                    else {
                                        Log.d("entered","here2");
                                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA);
                                    }}
                                else {
                                    takePhotoFromCamera();
                                }
                                break;}
                    }
                });
        pictureDialog.show();
    }

    //TODO: this is used to handle the response of the request permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // redirects to utils
        try{
            locationHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
        catch (NullPointerException e){
           e.printStackTrace();
        }

        switch (requestCode){


            //TODO: if gallery permission is granted
            case GALLERY: {
                // TODO: If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO: permission was granted, yay! Do the respective task
                    choosePhotoFromGallery();
                } else {
                    // TODO: permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    MyToast.toastLong(MainActivity.this,"Application needs camera permission to upload image");
                }
                return;
            }
            case CAMERA:{
                // TODO: If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO: permission was granted, yay! Do the respective task
                    takePhotoFromCamera();
                } else {
                    //TODO: permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    MyToast.toastLong(MainActivity.this,"Application needs camera permission to upload image");
                }
                return;
            }

        }
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }
//    private void takePhotoFromCamera() {
//        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, CAMERA);
//    }
    private void takePhotoFromCamera() {

//        mImageFile= new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "temp.png");
//
//        Uri tempURI = Uri.fromFile(mImageFile);
//        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        i.putExtra(MediaStore.EXTRA_OUTPUT, tempURI);
//        startActivityForResult(i, CAMERA);
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAMERA);
        }
    }
    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationHelper.onActivityResult(requestCode,resultCode,data);
        if (resultCode == this.RESULT_CANCELED) {
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (resultCode == RESULT_OK) {

            if (requestCode == GALLERY) {
                if (data != null) {
                    Uri contentURI = data.getData();
                    try {
                        currentImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                        String path = saveImage(currentImage);
                        Log.d("pathGallery",path);
                        Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                        imageUpload.setImageBitmap(currentImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == CAMERA) {

                // See code above
                File takenPhotoUri = getPhotoFileUri(photoFileName);
// by this point we have the camera photo on disk
                Bitmap rawTakenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
// See BitmapScaler.java: https://gist.github.com/nesquena/3885707fd3773c09f1bb
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, 700);
                // Configure byte output stream
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
// Compress the image further
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
// Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
                File resizedUri = getPhotoFileUri(photoFileName + "_resized");
                File resizedFile = new File(resizedUri.getPath());
                try {
                    resizedFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(resizedFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
// Write the bytes of the bitmap to file
                try {
                    fos.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }




//            //TODO : this is the solution to make the image not blurred
//            // here you image has been save to the path mImageFile.
//            Log.d("ImagePath", "Image saved to path : " + mImageFile.getAbsolutePath());
                currentImage = BitmapFactory.decodeFile(resizedFile.getAbsolutePath());
//
//            //TODO: used to compress
//            Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(currentImage, 120);
//
//
                imageUpload.setImageBitmap(currentImage);
                saveImage(currentImage);
                Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();


                //TODO: this method is previously used but it results in blurred image
//            currentImage = (Bitmap) data.getExtras().get("data");
//            Log.d("pathCamera",currentImage.toString());
//            imageUpload.setImageBitmap(currentImage);
//            saveImage(currentImage);
//            Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
            }

        } else { // Result was a failure
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
        }
    }
    private String saveImage(Bitmap thumbnail) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    //TODO: My onActivity Result
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && resultCode == RESULT_OK) {
//            Uri photoUri = data.getData();
//            if (photoUri != null) {
//                try {
//                    currentImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
//                    profilePic.setImageBitmap(currentImage);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
    //TODO: used to recycle the bitmap and to free the memory to getOver of an exception (OutOfMemory) Exception
    @Override
    protected void onStop() {
        super.onStop();
        if (currentImage != null) {
            currentImage.recycle();
            currentImage = null;
            System.gc();
        }
    }

    //this is used to open the exit app alert dialog
    boolean doubleBackToExitPressedOnce = false;
    @SuppressLint("SetTextI18n")
    @Override
    public void onBackPressed() {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            //Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            //TODO : using a customized toast in android
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast,
                    (ViewGroup) findViewById(R.id.custom_toast_container));
            TextView text =  layout.findViewById(R.id.text);
            text.setText(R.string.exit_msg);
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 50);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            //TODO : this is used to make the exit condition false after the two seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }


    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("Connection failed:", " ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        mLastLocation=locationHelper.getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        locationHelper.connectApiClient();
    }

    public void showToast(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
