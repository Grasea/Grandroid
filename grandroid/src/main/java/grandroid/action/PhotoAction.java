/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.action;

import grandroid.image.PhotoAgent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import grandroid.image.ImageUtil;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * 需搭配Face，方可正常運作
 *
 * @author Rovers
 */
public abstract class PhotoAction extends PendingAction {

    /**
     *
     */
    protected Uri outputFileUri;
    /**
     *
     */
    protected String tempFileName;
    protected final static String PATH_KEY = "PhotoAction.FilePath";

    /**
     *
     * @param context
     * @param actionName
     */
    public PhotoAction(Context context, String actionName) {
        super(context, actionName, 65011);
        tempFileName = "tmp" + System.currentTimeMillis();
    }

    /**
     *
     * @param context
     */
    public PhotoAction(Context context) {
        super(context, 65011);
        tempFileName = "tmp" + System.currentTimeMillis();
    }

    /**
     *
     * @return
     */
    @Override
    public Intent getActionIntent() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(context.getExternalCacheDir(), tempFileName);
        outputFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        return intent;
    }

    @Override
    public void restoreState(Bundle b) {
        super.restoreState(b);
        if (b != null && b.containsKey(PATH_KEY)) {
            tempFileName = b.getString(PATH_KEY);
            File file = new File(context.getExternalCacheDir(), tempFileName);
            outputFileUri = Uri.fromFile(file);
        }
    }

    @Override
    public void saveState(Bundle b) {
        super.saveState(b);
        if (b != null) {
            b.putString(PATH_KEY, tempFileName);
        }
    }

    /**
     *
     * @param result
     * @param data
     * @return
     */
    @Override
    public boolean callback(boolean result, Intent data) {
        try {
            /*
             * 資料夾不在就先建立
             */

            Bitmap bmp = null;// = (Bitmap) data.getExtras().get("data");
            ExifInterface exif = null;
            try {
                bmp = ImageUtil.loadBitmap(outputFileUri.getPath());
                exif = new ExifInterface(outputFileUri.getPath());
                new File(outputFileUri.getPath()).delete();
            } catch (FileNotFoundException e) {
                Log.e("grandroid", null, e);
            }
            if (bmp == null) {
                return false;
            } else {
//                    Matrix mtx = null;
//                    if (new DisplayAgent((Activity) context).getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT && bmp.getWidth() > bmp.getHeight()) {
//                        mtx = new Matrix();
//                        mtx.postRotate(90);
//                    }

//                    byte[] photo = data.getExtras().getByteArray("PHOTO");
//                    bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                //Log.d("Starbucks","brand="+android.os.Build.BRAND+","+android.os.Build.DEVICE);
                Log.d("grandroid", "android.os.Build.BRAND=" + android.os.Build.BRAND);
//                    if (android.os.Build.BRAND.equals("lge")) {
//                        if (mtx == null) {
//                            mtx = new Matrix();
//                        }
//                        mtx.postScale(-1, -1);
//                    }
//                    if (mtx != null) {
//                        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mtx, false);
//                    }

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                int rotate = 0;
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    default:
                }
                if (rotate > 0) {
                    Matrix mtx = new Matrix();
                    mtx.postRotate(rotate);
                    bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mtx, false);
                }
                PhotoAgent pa = new PhotoAgent(bmp, exif);
                //pa.setStoredFile(new File(outputFileUri.getPath()));
                execute(context, pa);
            }
        } catch (Exception ex) {
            Log.d("grandroid", null, ex);
        }
        return true;
    }

    /**
     *
     * @param context
     * @param photoAgent
     */
    protected abstract void execute(Context context, PhotoAgent photoAgent);
}
