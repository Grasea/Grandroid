/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.action;

import android.content.Context;
import grandroid.phone.PhoneAgent;

/**
 *
 * @author Rovers
 */
public class SendSMSAction extends ContextAction {

    /**
     *
     */
    protected String tel;
    protected String message;

    public SendSMSAction(Context context) {
        super(context);
    }

    public SendSMSAction(Context context, String actionName) {
        super(context, actionName);
    }

    public SendSMSAction(Context context, String tel, String message) {
        super(context);
        this.tel = tel;
        this.message = message;
    }

    public SendSMSAction(Context context, String actionName, String tel, String message) {
        super(context, actionName);
        this.tel = tel;
        this.message = message;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean execute(Context context) {
        if (tel == null) {
            tel = "";
        }
        if (message == null) {
            message = "";
        }
        new PhoneAgent().sendSMS(context, tel, message);
        return true;
    }
}
