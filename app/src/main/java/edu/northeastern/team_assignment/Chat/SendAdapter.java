package edu.northeastern.team_assignment.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.northeastern.team_assignment.Message;
import edu.northeastern.team_assignment.R;

public class SendAdapter extends RecyclerView.Adapter<SendAdapter.ViewHolder> {
    public List<Message> messageList;
    public Context context;

    public SendAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public SendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SendAdapter.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.senderName.setText(message.getSenderName());
        holder.sendTime.setText(transferTime(message.getTime()));

        switch (message.getImgId()){
            case "hh":
                holder.sendImage.setImageResource(R.drawable.png_hh);
                break;
            case "roll":
                holder.sendImage.setImageResource(R.drawable.png_roll);
                break;
            case "anger":
                holder.sendImage.setImageResource(R.drawable.emoji_anger);
                break;
            case "smile":
                holder.sendImage.setImageResource(R.drawable.png_smile);
                break;
            case "wink":
                holder.sendImage.setImageResource(R.drawable.png_wink);
                break;
            default:
                break;
        }
    }


    @Override
    public int getItemCount() {
        return getMessageList().size();
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public Context getContext() {
        return context;
    }

    public String transferTime(long time){
        return new SimpleDateFormat("MM-dd-yyyy HH:mm").format(new Date(time));
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView senderName;
        TextView sendTime;
        ImageView sendImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            sendTime = itemView.findViewById(R.id.senderTime);
            sendImage = itemView.findViewById(R.id.sendImage);
        }
    }
}
