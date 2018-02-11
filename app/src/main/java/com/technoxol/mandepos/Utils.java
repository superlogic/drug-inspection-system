package com.technoxol.mandepos;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ArdSoft on 4/10/2017.
 */

public class Utils {
    private Context mContext;

    public Utils(Context mContext) {
        this.mContext = mContext;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static File getVideoCacheDir(Context context) {
        return new File(context.getExternalCacheDir(), "video-cache");
    }

    public static void cleanVideoCacheDir(Context context) throws IOException {
        File videoCacheDir = getVideoCacheDir(context);
        cleanDirectory(videoCacheDir);
    }

    private static void cleanDirectory(File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        File[] contentFiles = file.listFiles();
        if (contentFiles != null) {
            for (File contentFile : contentFiles) {
                delete(contentFile);
            }
        }
    }

    private static void delete(File file) throws IOException {
        if (file.isFile() && file.exists()) {
            deleteOrThrow(file);
        } else {
            cleanDirectory(file);
            deleteOrThrow(file);
        }
    }

    private static void deleteOrThrow(File file) throws IOException {
        if (file.exists()) {
            boolean isDeleted = file.delete();
            if (!isDeleted) {
                throw new IOException(String.format("File %s can't be deleted", file.getAbsolutePath()));
            }
        }
    }

    public void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

    }

    public void startNewActivity(Class activityToStart, Bundle extras, boolean isFinish) {
        Intent intent = new Intent(mContext, activityToStart);
        if (extras != null)
            intent.putExtras(extras);
        mContext.startActivity(intent);

        if (isFinish)
            ((Activity) mContext).finish();
    }

    public void printLog(String tag, String s) {
        Log.e(tag, s);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public File resizeImageFile(File file, int reqSize) {

        if (file != null) {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);


            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqSize, reqSize);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return convertBitmapToFile(BitmapFactory.decodeFile(file.getAbsolutePath(), options), file.getName());
        }

        return null;
    }

    public File convertBitmapToFile(Bitmap bitmap, String filename) {
        //create a file to write bitmap data
        File f = new File(mContext.getCacheDir(), filename);
        try {
            f.createNewFile();
            //Convert bitmap to byte array

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 5 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }

    public boolean validName(String s) {
        String validNumber = "^([a-zA-Z -]+)$";
        Pattern pattern = Pattern.compile(validNumber);
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {
            return false;
        } else {
            return true;
        }
    }

    //Cache functions

    public boolean validPassword(String s) {

        String validPass = "^([a-zA-Z0-9@*#]{8,15})$";
        Pattern pattern = Pattern.compile(validPass);
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validNumber(String s) {
        String validNumber = "^((\\+92)|(0092))-{0,1}\\d{3}-{0,1}\\d{7}$|^\\d{11}$|^\\d{4}-\\d{7}$";
        Pattern pattern = Pattern.compile(validNumber);
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validEmail(String email) {
        String emailpattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern pattern = Pattern.compile(emailpattern);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.find()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validCNIC(String s) {
        String validNumber = "^\\d{5}[- .]?\\d{7}[- .]?\\d{1}$";
        Pattern pattern = Pattern.compile(validNumber);
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {
            return false;
        } else {
            return true;
        }
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null) && netInfo.isConnected();
    }

    public void showAlertDialoge() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("No Internet Connection")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showAlertDialoge(String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void setOnKeyListener(final EditText editText) {
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            hideKeyboard(v);
                            editText.setClickable(false);
                            editText.setFocusable(false);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }


}
