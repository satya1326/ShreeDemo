package com.hp.shreedemo;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MyToast extends AppCompatActivity {

    //TODO: this is used for the long toast display
    public static void toastLong (Activity activity , String msg){
        if(!msg.isEmpty()){
            Toast.makeText(activity,msg, Toast.LENGTH_LONG).show();
        }}
    //TODO: this is used for the short toast display
        public static void toastShort(Activity activity, String msg){
        if(!msg.isEmpty()){
            Toast.makeText(activity,msg, Toast.LENGTH_SHORT).show();
        }}
}
