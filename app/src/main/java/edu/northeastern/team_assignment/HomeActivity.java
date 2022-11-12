package edu.northeastern.team_assignment;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.team_assignment.chatDirectory.SendActivity;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> list = new ArrayList<>();
    private String userName;
    private DatabaseReference dataRef;
    private NotificationManager manager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.AllUserRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(this,list);
        adapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String receiver = list.get(position).getUserName();
                Intent intent = new Intent(HomeActivity.this, SendActivity.class);
                intent.putExtra("receiver", receiver);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        SharedPreferences sp = getSharedPreferences("sender", MODE_PRIVATE);
        userName =  sp.getString("name","");
        Toast.makeText(getApplicationContext(), "Welcome, " + userName +"!", Toast.LENGTH_SHORT).show();
        getUser();
        checkNotice();
    }


    boolean isFirst ;
    private void checkNotice() {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("notice","chat",NotificationManager.IMPORTANCE_HIGH);

            manager.createNotificationChannel(notificationChannel);
        }
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("messageList");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Message chat=null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    chat = snapshot.getValue(Message.class);
                }
                if (chat!=null){
                    noticeMsg(chat);
                }
                isFirst = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private long latTime;
    private void noticeMsg(Message chat) {
        if (latTime>chat.getTime()){
            return;
        }
        if (isFirst){
            if (latTime<chat.getTime()){
                latTime = chat.getTime();
            }
            return;
        }
        Intent intent = new Intent(this,HomeActivity.class);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 123, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 123, intent, PendingIntent.FLAG_ONE_SHOT);
        }
        Notification notification = new NotificationCompat.Builder(this,"notice")
                .setContentTitle(chat.getReceiverName() + " is getting message (notification) !")
                .setContentText("Emoji")
                .setWhen(System.currentTimeMillis())
                .setLargeIcon(getBit(chat.imgId))
                .setSmallIcon(R.drawable.message_icon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        manager.notify(1,notification);
    }

    private Bitmap getBit(String msgType){
        switch (msgType){
            case "smile":
                return getBitmapFromVectorDrawable(this,R.drawable.png_smile);
            case "roll":
                return getBitmapFromVectorDrawable(this,R.drawable.png_roll);
            case "wink":
                return getBitmapFromVectorDrawable(this,R.drawable.png_wink);
            case "anger":
                return getBitmapFromVectorDrawable(this,R.drawable.emoji_anger);
            case "hh":
                return getBitmapFromVectorDrawable(this,R.drawable.png_hh);
        }
        return getBitmapFromVectorDrawable(this,R.drawable.png_hh);
    }

    public  Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void getUser() {
        FirebaseDatabase.getInstance().getReference().child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    for(DataSnapshot child : ds.getChildren()) {
                        User user = child.getValue(User.class);
                        list.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
