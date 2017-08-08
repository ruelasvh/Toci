package com.timemachine.toci;

/**
 * Created by victorhugo on 1/28/15.
 */
public class Config {
    public static final String SERVER_ROOT = "http://104.152.64.96/AppCrowdZeeker/";
    // Image upload
    public static final String FILE_UPLOAD_URL = SERVER_ROOT + "AndroidFileUpload/fileUpload.php";
    // Fetch crowds by city
    public static final String FETCH_CROWDS_BY_CITY_URL = SERVER_ROOT + "fetchcrowds.php";
    // Fetch list of cities for search bar autocomplete
    public static final String FETCH_ALL_CITIES_URL = SERVER_ROOT + "fetchcities.php";
    // Fetch crowds by id
    public static final String FETCH_CROWDS_BY_ID_URL = SERVER_ROOT + "fetchcrowdsbyid.php";
    // Fetch latest crowd images
    public static final String FETCH_LATEST_CROWD_PICS_URL = SERVER_ROOT + "fetchlatestcrowdpics.php";
    // Default crowd image
    public static final String DEFAULT_CROWD_PIC_URL = SERVER_ROOT + "AndroidFileUpload/placeholders/add_picture_place_holder_xxxhdpi.png";
    // User registration url
    public static final String USER_REGISTRATION_URL = SERVER_ROOT + "registration.php";
    // User authentication url
    public static final String USER_AUTHENTICATION_URL = SERVER_ROOT + "authentication.php";
    // Add crowd to database url
    public static final String ADD_CROWD_TO_DATABASE_URL = SERVER_ROOT + "AndroidFileUpload/upload2database.php";
    // Delete crowd from database url
    public static final String REMOVE_CROWD_TO_DATABASE_URL = SERVER_ROOT + "AndroidFileUpload/removeFromDatabase.php";
    // Check city for crowds url
    public static final String CHECK_CITY_FOR_CROWDS_URL = SERVER_ROOT + "checkcity.php";
    // Directory name to store capture images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
}
