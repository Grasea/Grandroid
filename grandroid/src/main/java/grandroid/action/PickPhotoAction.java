/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.action;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import grandroid.image.ImageUtil;
import grandroid.image.PhotoAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Rovers
 */
public abstract class PickPhotoAction extends PendingAction {
    public boolean circleMode = false;
    /**
     *
     */
    protected int bitmapWidth;
    /**
     *
     */
    protected int bitmapHeight;

    /**
     * @param context
     */
    public PickPhotoAction(Context context) {
        super(context, 65012);
    }

    /**
     * @param context
     * @param bitmapWidth
     * @param bitmapHeight
     */
    public PickPhotoAction(Context context, int bitmapWidth, int bitmapHeight) {
        super(context, 65012);
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;
    }

    /**
     * @param context
     * @param bitmapWidth
     * @param bitmapHeight
     */
    public PickPhotoAction(Context context, int bitmapWidth, int bitmapHeight, Boolean circleMode) {
        super(context, 65012);
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;
        this.circleMode = circleMode;
    }

    /**
     * @param context
     * @param actionName
     */
    public PickPhotoAction(Context context, String actionName) {
        super(context, actionName, 65012);
    }

    /**
     * @param context
     * @param actionName
     * @param bitmapWidth
     * @param bitmapHeight
     */
    public PickPhotoAction(Context context, String actionName, int bitmapWidth, int bitmapHeight, Boolean circleMode) {
        super(context, actionName, 65012);
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;
        this.circleMode = circleMode;
    }

    /**
     * @return
     */
    @Override
    public Intent getActionIntent() {
        if (bitmapWidth > 0 && bitmapHeight > 0) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", bitmapWidth);
            intent.putExtra("outputY", bitmapHeight);
            intent.putExtra("return-data", true);
            if (circleMode) {
                intent.putExtra("circleCrop", true);
            }
            return intent;
        } else {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            return i;
        }
    }

    /**
     * @param result
     * @param data
     * @return
     */
    @Override
    public boolean callback(boolean result, Intent data) {
        if (result) {
            try {
                Bitmap photo = null;
                if (bitmapWidth > 0 && bitmapHeight > 0) {
                    photo = data.getParcelableExtra("data");
                } else {
                    InputStream imageStream = null;
                    try {
                        Uri selectedImage = data.getData();
//                        imageStream = context.getContentResolver().openInputStream(selectedImage);
//                        photo = BitmapFactory.decodeStream(imageStream);
                        //photo = ImageUtil.sampling(context, selectedImage);
                        Log.d("grandroid", "android.os.Build.BRAND=" + android.os.Build.BRAND + ", android.os.Build.DEVICE=" + android.os.Build.DEVICE);

                        Cursor cursor = context.getContentResolver().query(selectedImage,
                                new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION},
                                null, null, null);
                        String filepath = null;
                        try {
                            if (cursor.moveToFirst()) {
                                filepath = cursor.getString(0);
                                photo = ImageUtil.loadBitmap(filepath);
                                int orientation = cursor.getInt(1);
                                int rotate = 0;
                                switch (orientation) {
                                    case 90:
                                    case ExifInterface.ORIENTATION_ROTATE_90:
                                        rotate = 90;
                                        break;
                                    case 180:
                                    case ExifInterface.ORIENTATION_ROTATE_180:
                                        rotate = 180;
                                        break;
                                    case 270:
                                    case ExifInterface.ORIENTATION_ROTATE_270:
                                        rotate = 270;
                                        break;
                                    default:
                                }
                                if (rotate > 0) {
                                    Matrix mtx = new Matrix();
                                    mtx.postRotate(rotate);
                                    photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), mtx, false);
                                }
                            } else {
                            }
                        } finally {
                            cursor.close();
                        }
                        if (filepath != null) {
                            Log.d("grandroid", "pick photo from " + filepath);
                            execute(context, new PhotoAgent(photo, new File(filepath)));
                        }
                        return true;
                    } catch (FileNotFoundException ex) {
                        Log.e("grandroid", null, ex);
                    } finally {
                        try {
                            if (imageStream != null) {
                                imageStream.close();
                            }
                        } catch (IOException ex) {
                            Log.e("grandroid", null, ex);
                        }
                    }
                }
//            if (android.os.Build.BRAND.equals("lge")) {
//                Matrix mtx = new Matrix();
//                mtx.postScale(-1, -1);
//                photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), mtx, true);
//            }
                execute(context, new PhotoAgent(photo));
                return true;
            } catch (Exception e) {
                Log.e("grandroid", null, e);
            }
        }
        return false;
    }

    public abstract void execute(Context context, PhotoAgent photoAgent);
}
