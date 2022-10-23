package edu.northeastern.team_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebService extends AppCompatActivity {

    private static final int RESPONSE_CODE = 200;
    public static String BaseUrl = "https://api.openweathermap.org/";
    public static String AppId = "1d599c28aa1b2465411bfa883f11c9da";
    public static String BaseIconUrl = "http://openweathermap.org/img/wn/";
    public static String IconSize = "@2x.png";
    public static String lat;
    public static String lon;
    private TextView weatherData;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webservice);
        imageView1 = (ImageView) findViewById(R.id.weatherIcon);
        imageView2 = (ImageView) findViewById(R.id.weatherIcon2);
        imageView3 = (ImageView) findViewById(R.id.weatherIcon3);

        weatherData = findViewById(R.id.textView);

        findViewById(R.id.getButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentData();
            }
        });

        Button buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainActivity();
            }
        });
    }

    void getCurrentData() {

        EditText editLat = findViewById(R.id.enterLatitude);
        lat = editLat.getText().toString();

        EditText editLon = findViewById(R.id.enterLongitude);
        lon = editLon.getText().toString();



        if (!lat.isEmpty() && !lon.isEmpty()) {
            progressDialog = new ProgressDialog(WebService.this);
            progressDialog.setMessage("getting data ...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            WeatherService service = retrofit.create(WeatherService.class);
            Call<WeatherResponse> call = service.getCurrentWeatherData(lat, lon, AppId);
            call.enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                    if (response.code() == RESPONSE_CODE) {
                        WeatherResponse weatherResponse = response.body();
                        assert weatherResponse != null;

                        // icon url
                        // todo: load icon to the activity
                        String iconUrl = BaseIconUrl + weatherResponse.weather.get(0).icon + IconSize;

                        String stringBuilder = "Country: " +
                                weatherResponse.sys.country +
                                "\n" +
                                "Temp (K): " +
                                weatherResponse.main.temp +
                                "\n" +
                                "Minimum Temp (K): " +
                                weatherResponse.main.temp_min +
                                "\n" +
                                "Maximum Temp (K): " +
                                weatherResponse.main.temp_max +
                                "\n" +
                                "Humidity (%): " +
                                weatherResponse.main.humidity +
                                "\n" +
                                "Pressure (Pa): " +
                                weatherResponse.main.pressure;

                        weatherData.setText(stringBuilder);
                        Picasso.get().load(R.drawable.defult).into(imageView1);
                        Picasso.get().load(R.drawable.temp).into(imageView2);
                        Picasso.get().load(R.drawable.country).into(imageView3);
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                    weatherData.setText(t.getMessage());
                }
            });
        } else {

            Toast.makeText(this, "Please enter correct coordinates!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void backToMainActivity () {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting Activity")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}
