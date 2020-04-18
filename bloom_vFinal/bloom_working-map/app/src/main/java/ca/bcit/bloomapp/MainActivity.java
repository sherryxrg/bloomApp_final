package ca.bcit.bloomapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    // Irene testing gitlab
    public ArrayList<Float> coordsArrayLong = new ArrayList<Float>();
    public ArrayList<Float> coordsArrayLat = new ArrayList<Float>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OkHttpClient client = new OkHttpClient();

        // using the full API: allows max up to 10,000 rows
        String url = "https://opendata.vancouver.ca/api/records/1.0/search//?dataset=street-trees&rows=1000&refine.cultivar_name=KWANZAN";


        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject json = new JSONObject(myResponse);

                                JSONArray recordsArray = json.getJSONArray("records");
                                for (int i=0; i <recordsArray.length(); i++) {
                                    JSONObject records = recordsArray.getJSONObject(i);
                                    JSONObject fields = records.getJSONObject("fields");

                                    if (fields.has("geom")) {
                                        JSONObject geom = fields.getJSONObject("geom");
                                        JSONArray coord = geom.getJSONArray("coordinates");

                                        String longitude = coord.getString(0);
                                        Float longitude_float = Float.parseFloat(longitude);

                                        String latitude = coord.getString(1);
                                        Float latitude_float = Float.parseFloat(latitude);

                                        coordsArrayLong.add(longitude_float);
                                        coordsArrayLat.add(latitude_float);
                                        //System.out.println("Point added at: " + coordsArrayLong.get(i) + ", " + coordsArrayLat.get(i));
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

    }

    /**
     * Button function from launcher page.
     * @param view
     */
    public void startMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);

        float[] latitudeFloatArray = new float[coordsArrayLat.size()];
        float[] longitudeFloatArray = new float[coordsArrayLong.size()];

        System.out.println("array length " + coordsArrayLat.size());

        for (int i = 0; i < coordsArrayLat.size(); i++) {
            latitudeFloatArray[i] = coordsArrayLat.get(i);
            longitudeFloatArray[i] = coordsArrayLong.get(i);
        }

        intent.putExtra("latitude", latitudeFloatArray);
        intent.putExtra("longitude", longitudeFloatArray);

        startActivity(intent);
    }
}
