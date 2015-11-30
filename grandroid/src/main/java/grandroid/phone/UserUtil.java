/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.phone;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rovers
 */
public class UserUtil {

//    /**
//     * 取得使用者的帳號，若不存在則從AccountManager裡抓第一個email
//     * 需Android SDK 2.0以上
//     * @param context
//     * @param prefKey
//     * @return
//     */
//    public static String getUserAccount(Context context, String prefKey) {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//        String nickName = sp.getString(prefKey, "");
//        if (!nickName.equals("")) {
//            return nickName;
//        } else {
//            try {
//                Account[] accounts = AccountManager.get(context).getAccounts();
//                for (Account account : accounts) {
//                    if (account.name.contains("@")) {
//                        return account.name;
//                    }
//                }
//                if (accounts.length > 0) {
//                    return accounts[0].name;
//                }
//            } catch (Exception e) {
//                Log.e(UserUtil.class.getName(), null, e);
//            }
//            return "Unknown";
//        }
//    }
//
//    /**
//     * 取得使用者所有的帳號/id，可指定需包含什麼文字，如傳"@"可取得所有email
//     * 需Android SDK 2.0以上
//     * @param context
//     * @param contains
//     * @return
//     */
//    public static List<String> getAllUserAccount(Context context, String contains) {
//        Account[] accounts = AccountManager.get(context).getAccounts();
//        List<String> list = new ArrayList<String>();
//        for (Account account : accounts) {
//            if (contains != null) {
//                if (account.name.contains(contains)) {
//                    list.add(account.name);
//                }
//            } else {
//                list.add(account.name);
//            }
//        }
//        return list;
//    }
//    public static List<Contact> getContactEmails(Context context) {
//        ArrayList<Contact> list = new ArrayList<Contact>();
//        Cursor emailCur = context.getContentResolver().query(
//                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
//                null,
//                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " is not null",
//                null, "lower(" + ContactsContract.CommonDataKinds.Email.DATA + ")");
//
//        while (emailCur.moveToNext()) {
//            Contact contact = new Contact();
//            int id = emailCur.getInt(
//                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID));
//            String email = emailCur.getString(
//                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//            //System.out.println("id=" + id + ", email=" + email);
//            contact.set_id(id);
//            contact.setName(email);
//            contact.setEmail(email);
//            list.add(contact);
//        }
//        // close while finish
//        if (emailCur != null) {
//            emailCur.close();
//        }
//          //fill contact name
//                Cursor cc = context.getContentResolver().query(
//                        ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//                while (cc.moveToNext()) {
//                    Contact friend = mapContacts.get(cc.getInt(cc.getColumnIndex(ContactsContract.Contacts._ID)));
//                    if (friend != null) {
//                        friend.setName(cc.getString(cc.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
//                    }
//                }
//                cc.close();
//        return list;
//    }
    /**
     * 取得使用者的帳號，若不存在則從AccountManager裡抓第一個email 需Android SDK 1.5以上
     *
     * @param context
     * @param prefKey
     * @return
     */
    public static String getUserAccount(Context context, String prefKey) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String nickName = sp.getString(prefKey, "");
        if (!nickName.equals("")) {
            return nickName;
        } else {
            try {
                Class c = Class.forName("android.accounts.AccountManager");

                Object accountManager = c.getMethod("get", Context.class).invoke(null, context);
                Object[] accounts = (Object[]) c.getMethod("getAccounts").invoke(accountManager);
                Class ca = Class.forName("android.accounts.Account");
                Field f = ca.getField("name");
                f.setAccessible(true);
                for (Object obj : accounts) {
                    return (String) f.get(obj);
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 取得使用者所有的帳號/id，可指定需包含什麼文字，如傳"@"可取得所有email 需Android SDK 2.0以上
     *
     * @param context
     * @param prefKey
     * @param contains
     * @return
     */
    public static List<String> getAllUserAccount(Context context, String prefKey, String contains) {
        ArrayList<String> list = new ArrayList<String>();

        if (prefKey != null && prefKey.length() > 0) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String accounts = sp.getString(prefKey, "");
            if (!accounts.equals("")) {
                String[] accarr = accounts.split(",");
                for (String acc : accarr) {
                    list.add(acc);
                }
                return list;
            }
        }
        try {
            Class c = Class.forName("android.accounts.AccountManager");

            Object accountManager = c.getMethod("get", Context.class).invoke(null, context);
            Object[] accounts = (Object[]) c.getMethod("getAccounts").invoke(accountManager);
            Class ca = Class.forName("android.accounts.Account");
            Field f = ca.getField("name");
            f.setAccessible(true);
            for (Object obj : accounts) {
                if (contains == null || ((String) f.get(obj)).contains(contains)) {
                    list.add((String) f.get(obj));
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return list.isEmpty() ? null : list;
    }

    /**
     *
     * @param list
     * @return
     */
    public static String getArrayString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        if (list != null) {
            for (String str : list) {
                if (sb.length() == 0) {
                    sb.append(str);
                } else {
                    sb.append(",").append(str);
                }
            }
        }
        return sb.toString();
    }

    public static List<Contact> getContacts(Context context, boolean withPhone, boolean withEmail) {
        List<Contact> list = new ArrayList<Contact>();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, Phone.DISPLAY_NAME + " ASC");

        int i = 0;
        if (cur.moveToFirst()) {
            while (cur.moveToNext()) {
                String[] id = new String[]{cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))};
                Contact personContact = new Contact();
                /*
                 * Name
                 */
                personContact.setName(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                if (withPhone) {
                    Cursor phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,},
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                            + " = ?", id,
                            null);

                    if (phoneCursor.moveToFirst()) {
                        /*
                         * Number
                         */
                        personContact.setNumber(phoneCursor.getString(0));
                        //
                    }

                    phoneCursor.close();
                }
                if (withEmail) {
                    Cursor emailCur = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID
                            + " = ?", id, null);
                    if (emailCur.moveToFirst()) {
                        String emailContact = emailCur
                                .getString(emailCur
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                    String emailType = emailCur
//                            .getString(emailCur
//                                    .getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                        personContact.setEmail(emailContact);
                    }
                    emailCur.close();
                }
                list.add(personContact);
            }
        }

        cur.close();
        return list;
    }

    /**
     *
     * @param context
     * @return
     */
    @Deprecated
    public static List<Contact> getContactEmails(Context context) {
        ArrayList<Contact> list = new ArrayList<Contact>();
        String emailProjection[] = new String[]{Contacts.Phones.PERSON_ID, Contacts.Phones.DISPLAY_NAME, Contacts.ContactMethods.KIND, Contacts.ContactMethods.DATA};

        Cursor emailCursor = context.getContentResolver().query(Contacts.ContactMethods.CONTENT_URI,
                emailProjection, // select
                null, // where  //Contacts.ContactMethods.PERSON_ID + " = " + id
                null,
                Contacts.ContactMethods.DEFAULT_SORT_ORDER); // order
        if (emailCursor.moveToFirst()) {
            do {
                int kind = emailCursor.getInt(emailCursor.getColumnIndex(Contacts.ContactMethods.KIND));
                if (Contacts.KIND_EMAIL == kind) {
                    Contact contact = new Contact();
                    contact.set_id(emailCursor.getInt(emailCursor.getColumnIndex(Contacts.Phones.PERSON_ID)));
                    contact.setName(emailCursor.getString(emailCursor.getColumnIndex(Contacts.Phones.DISPLAY_NAME)));
                    contact.setEmail(emailCursor.getString(emailCursor.getColumnIndex(Contacts.ContactMethods.DATA)));
                    list.add(contact);
                }
            } while (emailCursor.moveToNext());
        } // close while finish
        if (emailCursor != null) {
            emailCursor.close();
        }
        return list;
    }

    /**
     *
     * @param context
     * @param email
     * @param subject
     * @param body
     * @return
     */
    public static boolean mailTo(Context context, String[] email, String subject, String body) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.putExtra(Intent.EXTRA_EMAIL, email);
        it.putExtra(Intent.EXTRA_SUBJECT, subject);
        it.putExtra(Intent.EXTRA_TEXT, body);
        it.setType("text/plain");
        context.startActivity(Intent.createChooser(it, "Choose Email Client"));
        return true;
    }
}
