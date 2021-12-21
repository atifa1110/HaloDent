package com.dentist.halodent.Model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preference {
    //pendeklarasian key-data berupa String, untuk sebagai wadah penyimpanan data
    //jadi setiap data mempunyai key yang berbeda satu sama lain
    private static final String KEY_GROUP_ID    ="group_id";
    private static final String KEY_GROUP_NAME  ="group_name";
    private static final String KEY_GROUP_PHOTO ="group_photo";

    private static final String KEY_KONSELOR_ID = "konselor_id";

    private static final String KEY_DOKTER_ID = "dokter_id";
    private static final String KEY_DOKTER_NAMA = "dokter_nama";
    private static final String KEY_DOKTER_EMAIL = "dokter_email";
    private static final String KEY_DOKTER_PHOTO = "dokter_photo";
    private static final String KEY_DOKTER_NIP = "dokter_nip";
    private static final String KEY_DOKTER_SIP = "dokter_sip";
    private static final String KEY_DOKTER_STR = "dokter_str";

    private static final String KEY_USER_AGE ="user_umur";

    private static final String KEY_STEP_1 ="step_1";
    private static final String KEY_STEP_2 ="step_2";
    private static final String KEY_STEP_3 ="step_3";
    private static final String KEY_STEP_4 ="step_4";

    private static final String KEY_QUIZ_OPEN   = "isQuizOpen";
    private static final String KEY_QUIZ_SCORE  = "QuizScore";

    private static final String KEY_BUTTON_CLICK = "isButtonClick";

    //deklarasi shared preference berdasarkan context
    private static SharedPreferences getSharedPreference(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setKeyStep1(Context context, String question1){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_STEP_1, question1);
        editor.apply();
    }

    public static String getKeyStep1(Context context){
        return getSharedPreference(context).getString(KEY_STEP_1,"");
    }

    public static void removeStep1(Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.remove(KEY_STEP_1);
        editor.apply();
    }

    public static void setKeyStep2(Context context, String question2){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_STEP_2, question2);
        editor.apply();
    }

    public static String getKeyStep2(Context context){
        return getSharedPreference(context).getString(KEY_STEP_2,"");
    }

    public static void removeStep2(Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.remove(KEY_STEP_2);
        editor.apply();
    }

    public static void setKeyStep3(Context context, String question3){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_STEP_3, question3);
        editor.apply();
    }

    public static void removeStep3(Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.remove(KEY_STEP_3);
        editor.apply();
    }

    public static String getKeyStep3(Context context){
        return getSharedPreference(context).getString(KEY_STEP_3,"");
    }

    public static void setKeyStep4(Context context, String question4){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_STEP_4, question4);
        editor.apply();
    }

    public static void removeStep4(Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.remove(KEY_STEP_4);
        editor.apply();
    }

    public static String getKeyStep4(Context context){
        return getSharedPreference(context).getString(KEY_STEP_4,"");
    }

    public static void setKeyUserAge(Context context, int UserAge){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putInt(KEY_USER_AGE, UserAge);
        editor.apply();
    }

    public static int getKeyUserAge(Context context){
        return getSharedPreference(context).getInt(KEY_USER_AGE,0);
    }

    public static void setKeyButtonClick(Context context, Boolean isButtonClickBefore){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putBoolean(KEY_BUTTON_CLICK, isButtonClickBefore);
        editor.apply();
    }

    public static Boolean getKeyButtonClick(Context context){
        return getSharedPreference(context).getBoolean(KEY_BUTTON_CLICK,false);
    }

    public static void setKeyQuizScore(Context context, int QuizScore){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putInt(KEY_QUIZ_SCORE, QuizScore);
        editor.apply();
    }

    public static int getKeyQuizScore(Context context){
        return getSharedPreference(context).getInt(KEY_QUIZ_SCORE,0);
    }

    public static void setKeyQuizOpen(Context context, Boolean isQuizOpenBefore){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putBoolean(KEY_QUIZ_OPEN, isQuizOpenBefore);
        editor.apply();
    }

    public static Boolean getKeyQuizOpen(Context context){
        return getSharedPreference(context).getBoolean(KEY_QUIZ_OPEN,false);
    }

    public static void setKeyKonselorId(Context context, String konselorId){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_KONSELOR_ID, konselorId);
        editor.apply();
    }

    public static String getKeyKonselorId(Context context){
        return getSharedPreference(context).getString(KEY_KONSELOR_ID,"");
    }

    public static void setKeyGroupId(Context context, String groupId){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_GROUP_ID, groupId);
        editor.apply();
    }

    public static String getKeyGroupId(Context context){
        return getSharedPreference(context).getString(KEY_GROUP_ID,"");
    }

    public static void setKeyGroupName(Context context, String groupName){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_GROUP_NAME, groupName);
        editor.apply();
    }

    public static String getKeyGroupName(Context context){
        return getSharedPreference(context).getString(KEY_GROUP_NAME,"");
    }

    public static void setKeyGroupPhoto(Context context, String groupPhoto){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_GROUP_PHOTO, groupPhoto);
        editor.apply();
    }

    public static String getKeyGroupPhoto(Context context){
        return getSharedPreference(context).getString(KEY_GROUP_PHOTO,"");
    }

    public static void setKeyDokter(Context context, String dokterId,String dokterNama,String dokterEmail,String dokterPhoto,String dokterNip,String dokterStr,String dokterSip){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_DOKTER_ID, dokterId);
        editor.putString(KEY_DOKTER_NAMA, dokterNama);
        editor.putString(KEY_DOKTER_EMAIL, dokterEmail);
        editor.putString(KEY_DOKTER_PHOTO, dokterPhoto);
        editor.putString(KEY_DOKTER_NIP, dokterNip);
        editor.putString(KEY_DOKTER_STR, dokterStr);
        editor.putString(KEY_DOKTER_SIP, dokterSip);
        editor.apply();
    }

    public static String getKeyDokterId(Context context){
        return getSharedPreference(context).getString(KEY_DOKTER_ID,"");
    }

    public static String getKeyDokterNama(Context context){
        return getSharedPreference(context).getString(KEY_DOKTER_NAMA,"");
    }

    public static String getKeyDokterEmail(Context context){
        return getSharedPreference(context).getString(KEY_DOKTER_EMAIL,"");
    }

    public static String getKeyDokterPhoto(Context context){
        return getSharedPreference(context).getString(KEY_DOKTER_PHOTO,"");
    }

    public static String getKeyDokterNip(Context context){
        return getSharedPreference(context).getString(KEY_DOKTER_NIP,"");
    }

    public static String getKeyDokterStr(Context context){
        return getSharedPreference(context).getString(KEY_DOKTER_STR,"");
    }

    public static String getKeyDokterSip(Context context){
        return getSharedPreference(context).getString(KEY_DOKTER_SIP,"");
    }

    //Deklarasi Edit Preferences dan menghapus data, sehingga menjadikannya bernilai default
    public static void removeGroupData(Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.remove(KEY_GROUP_ID);
        editor.remove(KEY_GROUP_NAME);
        editor.remove(KEY_GROUP_PHOTO);
        editor.apply();
    }

    public static void removeQuizData(Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.remove(KEY_QUIZ_OPEN);
        editor.remove(KEY_QUIZ_SCORE);
        editor.apply();
    }
}
