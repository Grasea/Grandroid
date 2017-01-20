package grandroid.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import grandroid.action.GoAction;
import grandroid.view.Face;
import grandroid.view.fragment.DataEvent;

public class MainActivity extends Face implements Observer, View.OnClickListener {
    UISetting currentUISetting;
    TextView titleBar;
    Button btn1, btn2, btn3,btn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleBar = (TextView) findViewById(R.id.title);
        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(this);

        btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(this);

        btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(this);

        btn4 = (Button) findViewById(R.id.btn4);
        btn4.setOnClickListener(this);
        new GoAction(this, ComponentTest1.class, R.id.frame).goDirection(GoAction.Direction.Right).execute();
        registerDataEvent("UI_CHANGE", this);
    }

    public synchronized void update(Observable observable, Object data) {
        DataEvent de = (DataEvent) data;
        if (de.getKey().equals("UI_CHANGE")) {
            UISetting setting = (UISetting) de.getData();
            if (currentUISetting == null) {
                //產生一個完全相反的UISetting，為的是讓下面的UI設定全都執行
                currentUISetting = new UISetting(!setting.showTitleBar, "");
            }
            if (currentUISetting.showTitleBar != setting.showTitleBar) {
                if (setting.showTitleBar) {
                    titleBar.setVisibility(View.VISIBLE);
                } else {
                    titleBar.setVisibility(View.GONE);
                }
            }
            if (setting.title != null) {
                titleBar.setText(setting.title);
                if(setting.title.equals("Title:Page1")){
                    btn1.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    btn2.setTextColor(getResources().getColor(android.R.color.black));
                    btn3.setTextColor(getResources().getColor(android.R.color.black));
                    btn4.setTextColor(getResources().getColor(android.R.color.black));
                }else  if(setting.title.equals("Title:Page2")){
                    btn1.setTextColor(getResources().getColor(android.R.color.black));
                    btn2.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    btn3.setTextColor(getResources().getColor(android.R.color.black));
                    btn4.setTextColor(getResources().getColor(android.R.color.black));
                }else  if(setting.title.equals("Title:Page3")){
                    btn1.setTextColor(getResources().getColor(android.R.color.black));
                    btn2.setTextColor(getResources().getColor(android.R.color.black));
                    btn3.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    btn4.setTextColor(getResources().getColor(android.R.color.black));
                }else  if(setting.title.equals("Title:Page4")){
                    btn1.setTextColor(getResources().getColor(android.R.color.black));
                    btn2.setTextColor(getResources().getColor(android.R.color.black));
                    btn3.setTextColor(getResources().getColor(android.R.color.black));
                    btn4.setTextColor(getResources().getColor(android.R.color.darker_gray));

                }
            }

//            if (setting.funcButton != null) {
//                if (setting.funcButton.getParent() != null) {
//                    ((ViewGroup) setting.funcButton.getParent()).removeView(setting.funcButton);
//                }
//                frameTitleBar.addView(setting.funcButton, maker.layFrameAbsolute(0, 0, setting.getFuncW(), setting.getFuncH(), Gravity.RIGHT | Gravity.CENTER));
//                this.btnFunc = setting.funcButton;
//            }
            currentUISetting = setting;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn1:
//                new GoAction(this, ComponentTest1.class, R.id.frame).removeOldFace().goDirection(GoAction.Direction.Right).execute();
                break;
            case R.id.btn2:
                new GoAction(this, ComponentTest2.class, R.id.frame).removeOldFace().goDirection(GoAction.Direction.Right).execute();
                break;
            case R.id.btn3:
                new GoAction(this, ComponentTest3.class, R.id.frame).removeOldFace().goDirection(GoAction.Direction.Right).execute();
                break;
            case R.id.btn4:
                new GoAction(this, ComponentTest4.class, R.id.frame).removeOldFace().goDirection(GoAction.Direction.Right).execute();
                break;
        }
    }

    public static class UISetting {
        public boolean showTitleBar = true;
        public String title = "";

        public UISetting(boolean showTitleBar, String title) {
            this.showTitleBar = showTitleBar;
            this.title = title;
        }
    }
}
