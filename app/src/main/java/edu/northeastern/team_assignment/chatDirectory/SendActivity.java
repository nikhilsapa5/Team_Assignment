package edu.northeastern.team_assignment.chatDirectory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import edu.northeastern.team_assignment.Message;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.northeastern.team_assignment.R;

public class SendActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SendActivity";
    public static String senderName = "";
    public static String receiverName = "";

    private DatabaseReference databaseReference;
    private SendAdapter sendAdapter;
    private RecyclerView recyclerView;
    private List<Message> messageList = new ArrayList<>();

    public TextView hh_tv;
    public TextView roll_tv;
    public TextView anger_tv;
    public TextView smile_tv;
    public TextView wink_tv;

    public int hh_cnt;
    public int roll_cnt;
    public int anger_cnt;
    public int smile_cnt;
    public int wink_cnt;

    public ImageView hh_iv;
    public ImageView roll_iv;
    public ImageView smile_iv;
    public ImageView wink_iv;
    public ImageView anger_iv;

    public String chosenSticker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        hh_tv = findViewById(R.id.countHH);
        roll_tv = findViewById(R.id.countRoll);
        anger_tv = findViewById(R.id.countAnger);
        smile_tv = findViewById(R.id.countSmile);
        wink_tv = findViewById(R.id.countWink);

        hh_iv = findViewById(R.id.icHH);
        roll_iv = findViewById(R.id.icRoll);
        smile_iv = findViewById(R.id.icSmile);
        wink_iv = findViewById(R.id.icWink);
        anger_iv = findViewById(R.id.icAnger);

        findViewById(R.id.imageBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        hh_iv.setOnClickListener(this);
        roll_iv.setOnClickListener(this);
        smile_iv.setOnClickListener(this);
        wink_iv.setOnClickListener(this);
        anger_iv.setOnClickListener(this);

        // connect to the database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        receiverName = getIntent().getStringExtra("receiver");
        SharedPreferences sharedPreferences = getSharedPreferences("sender", MODE_PRIVATE);
        senderName = sharedPreferences.getString("name", "");

        // init recycleview
        recyclerView = findViewById(R.id.recycleViewSend);
        sendAdapter = new SendAdapter(messageList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(sendAdapter);

        // send click
        transferFromSenderToReceiver(senderName, receiverName);
        findViewById(R.id.layoutSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendStickerToDB(chosenSticker);
            }
        });

        // watch history
        Query chatQuery = databaseReference.child("messageList").limitToLast(5);
        chatQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    messageList.add(message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendStickerToDB(String message){
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sendName", senderName);
            hashMap.put("imgId", message);
            hashMap.put("toName", receiverName);
            hashMap.put("timestamp", System.currentTimeMillis());
            databaseReference.child("messageList").push().setValue(hashMap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void transferFromSenderToReceiver(String fromSender, String toReceiver){
        DatabaseReference chatDatabaseReference = FirebaseDatabase.getInstance().getReference("messageList");
        chatDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                smile_cnt = anger_cnt = hh_cnt = roll_cnt = wink_cnt = 0;
                Log.w("test", "HELLO");
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Log.w("test1", "HELLO_AGAIN");
                    Message message = dataSnapshot.getValue(Message.class);
                    updateCount(message, senderName);
                    if(message.getSenderName().equals(fromSender) && message.getReceiverName().equals(toReceiver)){
                        messageList.add(message);
                        Log.w("test", message.toString());
                    }
                }

                smile_tv.setText(new StringBuilder().append("count: ").append(smile_cnt).toString());
                hh_tv.setText(new StringBuilder().append("count: ").append(hh_cnt).toString());
                roll_tv.setText(new StringBuilder().append("count: ").append(roll_cnt).toString());
                wink_tv.setText(new StringBuilder().append("count: ").append(wink_cnt).toString());
                anger_tv.setText(new StringBuilder().append("count: ").append(anger_cnt).toString());
                sendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getDetails());
            }
        });
    }

    @Override
    public void onClick(View view) {
        initSelect();
        int theId = view.getId();
        if(theId == R.id.icHH){
            chosenSticker = "hh";
            hh_iv.setBackgroundColor(getColor(R.color.white));
        }else if(theId == R.id.icAnger){
            chosenSticker = "anger";
            anger_iv.setBackgroundColor(getColor(R.color.white));
        }else if(theId == R.id.icRoll){
            chosenSticker = "roll";
            roll_iv.setBackgroundColor(getColor(R.color.white));
        }else if(theId == R.id.icWink){
            chosenSticker = "wink";
            wink_iv.setBackgroundColor(getColor(R.color.white));
        }else if(theId == R.id.icSmile){
            chosenSticker = "smile";
            smile_iv.setBackgroundColor(getColor(R.color.white));
        }
    }

    // update img count after sending
    public void updateCount(Message message, String sender){
        if(message.getSenderName().endsWith(sender)){
            String img = message.getImgId();
            if( img.equals("hh")){
                hh_cnt++;
            }else if(img.equals("smile")){
                smile_cnt++;
                System.out.println(smile_cnt);
            }else if(img.equals("roll")){
                roll_cnt++;
            }else if(img.equals("wink")){
                wink_cnt++;
            }else if(img.equals("anger")){
                anger_cnt++;
            }
        }
    }

    // set all img background to origin color
    public void initSelect() {
        hh_iv.setBackgroundColor(R.drawable.background_chat_input);
        roll_iv.setBackgroundColor(R.drawable.background_chat_input);
        wink_iv.setBackgroundColor(R.drawable.background_chat_input);
        anger_iv.setBackgroundColor(R.drawable.background_chat_input);
        smile_iv.setBackgroundColor(R.drawable.background_chat_input);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}