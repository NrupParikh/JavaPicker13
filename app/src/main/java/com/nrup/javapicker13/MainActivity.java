package com.nrup.javapicker13;

import static androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
import static java.lang.Math.min;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private AppCompatButton btnPickPDF;
    private AppCompatTextView tvPath;
    private AppCompatTextView tvRealPath;
    private AppCompatImageView imageView;

    // Registers a photo picker activity launcher in single-select mode.
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.

                if (uri != null) {

                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    tvPath.setText(uri.toString());
                    String realPathUri = getRealPathFromUri(uri, this);
                    tvRealPath.setText(realPathUri);
                    imageView.setImageURI(Uri.fromFile(new File(realPathUri)));
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        btnPickPDF = (AppCompatButton) findViewById(R.id.btnPickImage);

        tvPath = (AppCompatTextView) findViewById(R.id.tvPath);
        tvRealPath = (AppCompatTextView) findViewById(R.id.tvRealPath);
        imageView = (AppCompatImageView) findViewById(R.id.imageView);

        btnPickPDF.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(PickVisualMedia.ImageOnly.INSTANCE)
                .build()));
    }

    String getRealPathFromUri(Uri uri, Context context) {
        Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        String size = String.valueOf(returnCursor.getLong(sizeIndex));
        File file = new File(context.getFilesDir(), name);

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1024 * 1024;
            int bytesAvailable = inputStream.available();
            int bufferSize = min(bytesAvailable, maxBufferSize);
            byte[] buffers = new byte[bufferSize];

            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());

        } catch (Exception e) {
            Log.e("Exception", e.getLocalizedMessage());
        }
        return file.getPath();
    }

}