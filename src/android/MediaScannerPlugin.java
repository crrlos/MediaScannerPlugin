package br.com.brunogrossi.MediaScannerPlugin;


import static androidx.core.content.ContentProviderCompat.requireContext;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.json.JSONArray;
import org.json.JSONException;

import android.media.MediaScannerConnection;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/*
The MIT License (MIT)

Copyright (c) 2014

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


/**
 * MediaScannerPlugin.java
 *
 * @author Bruno E. Grossi <bruno@grossi.com.br>
 */
public class MediaScannerPlugin extends CordovaPlugin {
    private static final String TAG = "MediaScannerPlugin";

    private void copyFile(File sourceFile, File destinationFile) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(sourceFile);
             FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            String fileUri = args.optString(0);
            String source = fileUri.replace("file://", "");
            String fileName = source.split("/")[source.split("/").length - 1];
            String destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + fileName;
            File destinationFile = new File(destinationPath);
            File sourceFile = new File(source);
            try {
                copyFile(sourceFile, destinationFile);
            } catch (IOException e) {
                callbackContext.error("No se pudo copiar imagen a destino");
            }
            MediaScannerConnection.scanFile(cordova.getContext(), new String[]{destinationFile.getAbsolutePath()}, null, null);
            callbackContext.success();
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
            return false;
        }
    }

}