package com.tongminhnhut.orderfood_demo.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import com.tongminhnhut.orderfood_demo.Remote.APIService;
import com.tongminhnhut.orderfood_demo.Remote.RetrofitClient;
import com.tongminhnhut.orderfood_demo.model.Foods;
import com.tongminhnhut.orderfood_demo.model.Requests;
import com.tongminhnhut.orderfood_demo.model.Table_Status;
import com.tongminhnhut.orderfood_demo.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhut on 2/17/2018.
 */

public class Common {
    public static User curentUser ;
    public static Requests currentRequest ;
    public static List<Foods> currentListFood = new ArrayList<>();

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";
    public static final String CASH = "Cash";
    public static final String USER_KEY = "User";
    public static final String PMW_KEY = "Password";

    private static final String BASE_URL = "https://fcm.googleapis.com/";
    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static String convertCodeStatus (String code){
        if (code.equals("0"))
            return "Đang làm" ;
        if (code.equals("1"))
            return "Hoàn thành" ;
        else
            return "Giao tận bàn" ;
    }

    public static boolean isConnectedInternet (Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager !=null){
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo() ;
            if (infos !=null){
                for (int i = 0; i<infos.length;i++)
                {
                    if (infos[i].getState()== NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false ;
    }

    public static String convertStatusTable (String code){
        if (code.equals("0"))
            return "Bàn còn trống";
        if (code.equals("1"))
            return "Bàn đã đặt";
        else
            return "Bàn VIP";
    }
}
