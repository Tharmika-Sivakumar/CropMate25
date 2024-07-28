package com.example.cropmate25;

public class UserData {
    private static String userName;
    private static String userId;
    private static String userCounty;
    private static String userProvince;
    private static String userDistrict;
    private static String userCity;

    public static void setName(String name){
        userName = name;
    }

    public static void setId(String id){
        userId = id;
    }

    public static void setLocation(String county, String province, String district, String city){
        userCounty = county;
        userProvince = province;
        userDistrict = district;
        userCity = city;
    }

    public static String getName(){
        return(userName != null && !userName.isEmpty()) ? userName : "No Name Found!";
    }

    public static String getId(){
        return(userId != null && !userId.isEmpty()) ? userId : "No ID Found!";
    }

    public static String getProvince(){
        return(userProvince != null && !userProvince.isEmpty()) ? userProvince : "No Province Found!";
    }

    public static String getDistrict(){
        return(userDistrict != null && !userDistrict.isEmpty()) ? userDistrict : "No District Found!";
    }

    public static String getCity(){
        return(userCity != null && !userCity.isEmpty()) ? userCity : "No City Found!";
    }

    public static String getCounty(){
        return(userCounty != null && !userCounty.isEmpty()) ? userCounty : "No County Found!";
    }
}