package com.whocooler.app.Common.Helpers;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    public static Object getPath(final Context context, final Uri uri) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                String fileName = getFilePath(context, uri);
                if (fileName != null) {
                    return Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                }

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getColumnByName(context, "_data", contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getColumnByName(context, "_data", contentUri, selection, selectionArgs);
            }
            // Google Drive
            else if (isGoogleDrive(uri)) {
                return handleGoogleDrive(context, uri);
            }
        }
        // Google Drive
        else if (isGoogleDrive(uri)) {
            return handleGoogleDrive(context, uri);
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getColumnByName(context, "_data", uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static PendingDownloadContent handleGoogleDrive(Context context, Uri uri) {
        String remoteFileName = getColumnByName(context, "_display_name", uri, null, null);
        if (remoteFileName == null)
            return null;
        return new PendingDownloadContent(uri, remoteFileName);
    }

    public static File copyUriToFile(Context context, Uri uri, String fileName) {
        File fileOutput = null;
        InputStream remoteInputStream = null;
        try {
            remoteInputStream = context.getContentResolver().openInputStream(uri);
            if (remoteInputStream == null)
                return null;
            fileOutput = new File(context.getExternalCacheDir(), fileName);
            fileOutput.deleteOnExit();
            int read;
            byte[] buffer = new byte[8 * 1024];
            OutputStream outputStream = new FileOutputStream(fileOutput);
            while ((read = remoteInputStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, read);
            outputStream.flush();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } finally {
            try {
                if (remoteInputStream != null)
                    remoteInputStream.close();
            } catch (IOException e) {
            }
        }
        if (fileOutput != null)
            return fileOutput;
        return null;
    }

    public static String getColumnByName(Context context, final String columnName, Uri uri, String selection,
                                         String[] selectionArgs) {
        final String[] projection = {
                columnName
        };
        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(columnName);
                return cursor.getString(column_index);
            }
        } catch (IllegalArgumentException ignored) {
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Используется contains, потому-что для Google Drive добавляет в конец доп. части от разных условий выбора
     * Case 1: com.google.android.apps.docs.storage
     * Case 2: com.google.android.apps.docs.storage.legacy
     */
    private static boolean isGoogleDrive(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
    }

    public static String getFilePath(Context context, Uri uri) {
        Cursor cursor = null;
        final String[] projection = {
                MediaStore.MediaColumns.DISPLAY_NAME
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

}

