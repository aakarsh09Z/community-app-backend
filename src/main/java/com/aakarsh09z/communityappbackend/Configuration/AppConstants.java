package com.aakarsh09z.communityappbackend.Configuration;

public class AppConstants {

    public static final String secret="6bc61f8260b0ee59efdd01c68ee1c6bf7434d5ae22e3ece9c3da16e9ce01e390";
    public static final long JWT_ACCESS_TOKEN_VALIDITY = 24 * 60 *60; //30 sec
    public static final long JWT_REFRESH_TOKEN_VALIDITY = 100 * 24 * 60 *60; //2 min
    public static final int OTP_EXPIRATION_MINUTE=10;
    public static final String path = "https://connectifystorage.s3.ap-south-1.amazonaws.com/resources/";
    public static final String avatarPath = "https://connectifystorage.s3.ap-south-1.amazonaws.com/resources/avatars/";
    public static final String GOOGLE_CLIENT_ID1 = "808423673405-ddji25pa6af6c4e2rhdrmreb8al9s677.apps.googleusercontent.com";  //app
    public static final String GOOGLE_CLIENT_ID2 = "286890073941-1pu4m1gvl3cle29jnpv6qvkf04mu97m7.apps.googleusercontent.com";  //web
}
