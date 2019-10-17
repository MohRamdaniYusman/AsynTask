package com.example.asyntask;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button button;
    ImageView imageView;
    String image_url = "https://scontent.fcgk12-1.fna.fbcdn.net/v/t1.0-9/38744575_2121476364561337_7852206184328069120_o.jpg?_nc_cat=104&_nc_oc=AQktZj1IVa9abyveHdTgjYI1YDKyy34FtDsrCJ6VJs37fbcNnfJCGxH9g9XwPebAKII&_nc_ht=scontent.fcgk12-1.fna&oh=cc8bd15a8434b32bcda6464722787fdb&oe=5E62B59F";

    public void downloadImage(View view) {
        Log.i("Info", "Button pressed");
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(image_url);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        button = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.image_view);
    }

    private void initPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void
            onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {

            }

        }).check();
    }

    class DownloadTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog;

        /**
         * Prepare Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Download in progress...");

            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        /**
         * Jalankan Background task
         */
        @Override
        protected String doInBackground(String... params) {
            String path = params[0];
            int file_length;
            Log.i("Info: path", path);
            try {
                URL url = new URL(path);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                file_length = urlConnection.getContentLength();
                /**
                 * Buat folder di direktori
                 */
                File new_folder = new
                        File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                        "PrakMobpro");
                if (!new_folder.exists()) {
                    if (new_folder.mkdir()) {
                        Log.i("Info", "Sukses Buat Folder");
                    } else {
                        Log.i("Info", "Gagal Buat Folder");
                    }
                } else {
                    Log.i("Info", "Folder already exists");
                }
                /**
                 * membuat File dari hasil Download
                 */
                File output_file = new File(new_folder,
                        "prakmobpro.jpg");
                OutputStream outputStream = new
                        FileOutputStream(output_file);
                InputStream inputStream = new
                        BufferedInputStream(url.openStream(), 8192);
                byte[] data = new byte[1024];
                int total = 0;
                int count;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    outputStream.write(data, 0, count);
                    int progress = 100 * total / file_length;
                    publishProgress(progress);
                    Log.i("Info", "Progress: " +
                            Integer.toString(progress));
                }
                inputStream.close();
                outputStream.close();
                Log.i("Info", "file_length: " +
                        Integer.toString(file_length));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Download complete.";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.hide();
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();
            File folder = new
                    File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                    "PrakMobpro");
            File output_file = new File(folder, "prakmobpro.jpg");
            String path = output_file.toString();

            imageView.setImageDrawable(Drawable.createFromPath(path));
            Log.i("Info", "Path: " + path);
        }
    }
}