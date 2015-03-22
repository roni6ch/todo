package roni.shlomi.TodoList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

public class FetchAddressIntentService extends IntentService  {


	protected ResultReceiver mReceiver;
	private static final String TAG = "fetch-address-intent-service";
	public FetchAddressIntentService(String name) {
		super(name);
		Log.v("1", "1");
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		String errorMessage = "";
		Log.v("2", "2");
		// Get the location passed to this service through an extra.
		Location location = intent.getParcelableExtra(
				Constants.LOCATION_DATA_EXTRA);

		List<Address> addresses = null;

		try {
			addresses = geocoder.getFromLocation(
					location.getLatitude(),
					location.getLongitude(),
					// In this sample, get just a single address.
					1);
		} catch (IOException ioException) {
			// Catch network or other I/O problems.
			errorMessage = "a";
			Log.e(TAG, errorMessage, ioException);
		} catch (IllegalArgumentException illegalArgumentException) {
			// Catch invalid latitude or longitude values.
			errorMessage = "b";
			Log.e(TAG, errorMessage + ". " +
					"Latitude = " + location.getLatitude() +
					", Longitude = " +
					location.getLongitude(), illegalArgumentException);
		}

		// Handle case where no address was found.
		if (addresses == null || addresses.size()  == 0) {
			if (errorMessage.isEmpty()) {
				errorMessage = "c";
				Log.e(TAG, errorMessage);
			}
			deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
		} else {
			Address address = addresses.get(0);
			ArrayList<String> addressFragments = new ArrayList<String>();

			// Fetch the address lines using getAddressLine,
			// join them, and send them to the thread.
			for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
				addressFragments.add(address.getAddressLine(i));
			}
			Log.i(TAG, "d");
			deliverResultToReceiver(Constants.SUCCESS_RESULT,TextUtils.join(System.getProperty("line.separator"),addressFragments));
		}


	}
	private void deliverResultToReceiver(int resultCode, String message) {
		Bundle bundle = new Bundle();
		Log.v("Asd", "asd");
		bundle.putString(Constants.RESULT_DATA_KEY, message);
		mReceiver.send(resultCode, bundle);
	}


}
