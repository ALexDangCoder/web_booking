package com.example.fbooking.booking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fbooking.R;
import com.example.fbooking.retrofit.ApiService;
import com.example.fbooking.retrofit.RetrofitInstance;
import com.example.fbooking.utils.AlarmReceiver;
import com.example.fbooking.utils.PriceFormatUtils;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CheckAgainActivity extends AppCompatActivity {
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private TextView tvDateCheckInAgain, tvNightAgain, tvDateCheckOutAgain,
            tvRoomNumberAgain, tvRoomTypeAgain, tvRankAgain, tvPeopleAgain,
            tvNameAgain, tvPhoneNumberAgain, tvIdPersonAgain, tvEmailAgain, tvPriceAgain;
    private AppCompatButton btnCancelAgain, btnOpenPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_again);
        creatNotificationChanel();

        initUi();

        showRoomDetail();

        onClickButton();
    }

    private void showRoomDetail() {
        Booking booking = (Booking) getIntent().getExtras().get("booking");

        tvDateCheckInAgain.setText(booking.getNgaynhan());
        tvNightAgain.setText(String.valueOf(booking.getSodem()));
        tvDateCheckOutAgain.setText(booking.getNgayTra());
        tvRoomNumberAgain.setText(booking.getSophong());
        tvRoomTypeAgain.setText(booking.getLoaiPhong());
        tvRankAgain.setText(booking.getHangPhong());
        tvPeopleAgain.setText(String.valueOf(booking.getSoNguoi()));

        tvNameAgain.setText(booking.getHoten());
        tvPhoneNumberAgain.setText(booking.getSophong());
        tvIdPersonAgain.setText(String.valueOf(booking.getCccd()));
        tvEmailAgain.setText(booking.getEmail());

        tvPriceAgain.setText(CheckAgainActivity.this.getString(R.string.vnd, PriceFormatUtils.format(String.valueOf(booking.getGiaPhong()))));

        //Call API
        Retrofit retrofit = RetrofitInstance.getInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        btnOpenPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiService.createOrder(booking).enqueue(new Callback<Booking>() {
                    @Override
                    public void onResponse(Call<Booking> call, Response<Booking> response) {
                        Toast.makeText(CheckAgainActivity.this, "Success", Toast.LENGTH_SHORT).show();

                        Booking o = response.body();
                        Log.d("BOOKING", o.toString());

                        //Dat thoi gian
                        String currentDate = booking.getNgaynhan() + " " + booking.getGioNhanPhong();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                        Date parseDate = null;
                        try {
                            parseDate = format.parse(currentDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long milliseconds = parseDate.getTime();

                        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(CheckAgainActivity.this, AlarmReceiver.class);
                        pendingIntent = PendingIntent.getBroadcast(CheckAgainActivity.this, 0, intent, 0);
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, milliseconds,
                                AlarmManager.INTERVAL_DAY, pendingIntent);
                        Log.d("DATE", milliseconds + "");
                        Toast.makeText(CheckAgainActivity.this, "Đặt thời gian thành công!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Booking> call, Throwable t) {
                        Toast.makeText(CheckAgainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                startActivity(new Intent(CheckAgainActivity.this, ConfirmActivity.class));
            }
        });
    }

    private void creatNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "FbookingReminderChanel";
            String description = "Notify Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("fbookingid", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void onClickButton() {
        btnCancelAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initUi() {
        tvDateCheckInAgain = findViewById(R.id.tv_date_check_in_again);
        tvNightAgain = findViewById(R.id.tv_night_again);
        tvDateCheckOutAgain = findViewById(R.id.tv_date_check_out_again);
        tvRoomNumberAgain = findViewById(R.id.tv_room_number_again);
        tvRoomTypeAgain = findViewById(R.id.tv_room_type_again);
        tvRankAgain = findViewById(R.id.tv_rank_again);
        tvPeopleAgain = findViewById(R.id.tv_people_again);
        tvNameAgain = findViewById(R.id.tv_name_again);
        tvPhoneNumberAgain = findViewById(R.id.tv_phone_number_again);
        tvIdPersonAgain = findViewById(R.id.tv_id_person_again);
        tvEmailAgain = findViewById(R.id.tv_email_again);

        tvPriceAgain = findViewById(R.id.tv_price_again);

        btnCancelAgain = findViewById(R.id.btn_cancel_again);
        btnOpenPay = findViewById(R.id.btn_open_pay_again);
    }
}