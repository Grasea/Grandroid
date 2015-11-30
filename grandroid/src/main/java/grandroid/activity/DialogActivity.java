/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import grandroid.action.Action;
import grandroid.action.GoAction;
import grandroid.dialog.DialogMask;
import grandroid.dialog.GDialog.Builder;
import grandroid.dialog.GDialog.DialogStyle;
import grandroid.view.Face;
import grandroid.view.LayoutMaker;

/**
 *
 * @author Rovers
 */
public class DialogActivity extends Face {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DialogMask(this) {
            @Override
            public boolean setupMask(Context context, Builder builder, LayoutMaker maker) throws Exception {
                builder.setTitle(getIntent().getExtras().getString("TITLE"));
                maker.getLastLayout().setPadding(10, 10, 10, 10);
                maker.addTextView(getIntent().getExtras().getString("CONTENT")).setTextColor(Color.WHITE);
                maker.escape();
                String textOk = DialogActivity.this.getIntent().getStringExtra("STR_OK");
                builder.setPositiveButton(new Action(textOk == null ? "關閉" : textOk));
                if (getIntent().getExtras().containsKey("TARGET")) {
                    String textGoto = DialogActivity.this.getIntent().getStringExtra("STR_GOTO");
                    builder.setNegativeButton(new GoAction(context, textGoto == null ? "前往應用程式" : textGoto, Class.forName(getIntent().getExtras().getString("TARGET"))).setBundle(getIntent().getExtras()));
                }
                return true;
            }

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }

            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        }.show(DialogStyle.Android);
    }
}
