package com.hp.shreedemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //TODO: views initialisation
    private ImageView imageUpload;
    //TODO: this is for the image upload
    private Bitmap currentImage;
    public final int GALLERY = 1;
    public final int CAMERA= 2;
    private static final String IMAGE_DIRECTORY = "/satyaImg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseViews();
    }

    private void initialiseViews() {
        imageUpload = findViewById(R.id.imageUpload);
        imageUpload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageUpload:
                //TODO: calling the image upload method here
                openingImageAlert();
                break;
        }
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
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Profile photo");
        String[] pictureDialogItems = {
                "Gallery",
                "Camera" };
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
                                    if(ActivityCompat.shouldShowRequestPermissionRationale(MyProfileActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                                        MyToast.toastLong(MyProfileActivity.this,"Application needs storage permission to upload image");
                                        Log.d("entered","here1");
                                    }
                                    else {
                                        Log.d("entered","here2");
                                        ActivityCompat.requestPermissions(MyProfileActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},GALLERY);
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
                                    if(ActivityCompat.shouldShowRequestPermissionRationale(MyProfileActivity.this,Manifest.permission.CAMERA)){
                                        MyToast.toastLong(MyProfileActivity.this,"Application needs camera permission to upload image");
                                        Log.d("entered","here1");
                                    }
                                    else {
                                        Log.d("entered","here2");
                                        ActivityCompat.requestPermissions(MyProfileActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA);
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
                    MyToast.toastLong(MyProfileActivity.this,"Application needs camera permission to upload image");
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
                    MyToast.toastLong(MyProfileActivity.this,"Application needs camera permission to upload image");
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
    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
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
            currentImage = (Bitmap) data.getExtras().get("data");
            Log.d("pathCamera",currentImage.toString());
            imageUpload.setImageBitmap(currentImage);
            saveImage(currentImage);
            Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
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
}
