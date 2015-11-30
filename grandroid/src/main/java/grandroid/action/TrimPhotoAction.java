/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.action;

import grandroid.image.PhotoAgent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
public abstract class TrimPhotoAction extends PendingAction {

    /**
     *
     */
    protected Uri outputFileUri;
    protected File sourceImage;
    /**
     *
     */
    protected String tempFileName;
    protected final static String PATH_KEY = "TrimPhotoAction.FilePath";
    protected int aspectX;
    protected int aspectY;
    protected int outputWidth;
    protected int outputHeight;
    protected boolean asBitmap;

    public TrimPhotoAction(Context context, String actionName, File sourceImage, int aspectX, int aspectY, int outputWidth, int outputHeight) {
        this(context, actionName, sourceImage, aspectX, aspectY, outputWidth, outputHeight, false);
    }

    public TrimPhotoAction(Context context, String actionName, File sourceImage, int aspectX, int aspectY, int outputWidth, int outputHeight, boolean asBitmap) {
        this(context, sourceImage, aspectX, aspectY, outputWidth, outputHeight, false);
        this.setActionName(actionName);
    }

    public TrimPhotoAction(Context context, File sourceImage, int aspectX, int aspectY, int outputWidth, int outputHeight) {
        this(context, sourceImage, aspectX, aspectY, outputWidth, outputHeight, false);
    }

    public TrimPhotoAction(Context context, File sourceImage, int aspectX, int aspectY, int outputWidth, int outputHeight, boolean asBitmap) {
        super(context, 65013);
        tempFileName = "tmp" + System.currentTimeMillis();
        this.sourceImage = sourceImage;
        this.aspectX = aspectX;
        this.aspectY = aspectY;
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;
        this.asBitmap = asBitmap;
        if (debug) {
            Log.d("grandroid", "create TrimPhotoAction...");
        }
    }

    /**
     *
     * @return
     */
    @Override
    public Intent getActionIntent() {
        if (debug) {
            Log.d("grandroid", this.getClass().getSimpleName() + " return new Action Intent");
        }
        File outputFile = new File(context.getExternalCacheDir(), tempFileName);
        Uri inputFileUri = Uri.fromFile(sourceImage);
        outputFileUri = Uri.fromFile(outputFile);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputFileUri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪  
        intent.putExtra("crop", true);
        // aspectX aspectY 是宽高的比例   这兩項為裁剪框的比例.
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        // outputX outputY 是裁剪图片宽高   这兩項為裁剪框的比例.
        intent.putExtra("outputX", outputWidth);
        intent.putExtra("outputY", outputHeight);
        intent.putExtra("scale", false);
        // true to return a Bitmap, false to directly save the cropped iamge
        intent.putExtra("return-data", asBitmap);
        intent.putExtra("noFaceDetection", true);
        //save output image in uri
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
            try {
                if (new File(outputFileUri.getPath()).exists()) {
                    bmp = ImageUtil.loadBitmap(outputFileUri.getPath());
                    new File(outputFileUri.getPath()).delete();
                } else if (data.getExtras() != null) {
                    bmp = (Bitmap) data.getExtras().getParcelable("data");
                }
            } catch (FileNotFoundException e) {
                Log.e("grandroid", null, e);
            }
            if (bmp == null) {
                if (debug) {
                    Log.e("grandroid", "cant load result bmp from " + outputFileUri.getPath());
                }
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
//                    Log.d("grandroid", "android.os.Build.BRAND=" + android.os.Build.BRAND);
//                    if (android.os.Build.BRAND.equals("lge")) {
//                        if (mtx == null) {
//                            mtx = new Matrix();
//                        }
//                        mtx.postScale(-1, -1);
//                    }
//                    if (mtx != null) {
//                        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mtx, false);
//                    }
                execute(context, new PhotoAgent(bmp));
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
    public abstract void execute(Context context, PhotoAgent photoAgent);
}
