package roni.shlomi.TodoList;

import com.google.android.gms.location.Geofence;

public final class Constants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
        "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
        ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
        ".LOCATION_DATA_EXTRA";
	public static final float GEOFENCE_RADIUS_IN_METERS = 0.000001f;
	public static final  long GEOFENCE_EXPIRATION_IN_MILLISECONDS = Geofence.NEVER_EXPIRE;
}