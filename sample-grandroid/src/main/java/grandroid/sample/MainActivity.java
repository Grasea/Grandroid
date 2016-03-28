package grandroid.sample;

import android.content.Context;
import android.os.Bundle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import grandroid.action.AsyncAction;
import grandroid.action.ToastAction;
import grandroid.net.FilePoster;
import grandroid.view.Face;

public class MainActivity extends Face {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new AsyncAction<File>(this) {
            @Override
            public boolean execute(Context context) {
                InputStream inputStream = null;
                OutputStream outputStream = null;
                File result = new File(getExternalFilesDir(null).getPath() + "/userfile.xml");
                try {
                    inputStream = getAssets().open("userfile.xml");
                    outputStream =
                            new FileOutputStream(result);
                    int read = 0;
                    byte[] bytes = new byte[1024];

                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (outputStream != null) {
                        try {
                            // outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
                setResult(result);
                return true;
            }

            @Override
            public void afterExecution(final File file) {
                new AsyncAction<String>(context) {
                    @Override
                    public boolean execute(Context context) {
                        FilePoster fp = new FilePoster();
                        fp.put("Key", "83B955B96CFD925EEB83DF2290CCA5C3A6325EECA5DCA1C6896B3F4D211892AB78CD4CA76F3D4D5B7485F74D5804D1A76C33D4A2D4303B32464BC299BAD14957AE2CBC");
                        fp.setPostParamName("userfile");
                        try {
                            setResult(fp.post("http://www.broadfast.com.tw/BroadFast/AgentQueryCategory2.php", file));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }

                    @Override
                    public void afterExecution(String result) {
                        new ToastAction(context).setMessage(result).execute();
                    }
                }.execute();


            }
        }.execute();

    }
}
