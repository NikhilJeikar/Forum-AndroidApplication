package com.haggle.forum.Utilty;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;


public class FileSave {

    public Uri Download(Context context , Uri uri , String Name ,String Path){

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                DownloadManager.Request.NETWORK_MOBILE);

        request.setTitle("Image download");
        request.setDescription("Android Data download using DownloadManager.");

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        request.setDestinationInExternalPublicDir(Path , Name);

        request.setMimeType("image/*");

        assert downloadManager != null;
        return downloadManager.getUriForDownloadedFile(downloadManager.enqueue(request));

    }


    private static File createTempImageFile(Context context, String name) { ;
        File storageDir = context.getCacheDir();
        File file = new File(storageDir,name);
        return file;
    }

    public void createCustomFile(final Context context , final String fileName , StorageReference reference) {

            reference.getFile(createTempImageFile(context,fileName)).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                }
            });
    }
}
