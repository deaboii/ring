package com.deeaboi.ring.ViewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deeaboi.ring.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class messageListViewHolder extends RecyclerView.ViewHolder
{

      public RelativeLayout rv_receive,rv_send;
      public CircleImageView  dp_receive,dp_send;
      public   ImageButton     btn_receive,btn_send;



    public messageListViewHolder(@NonNull View itemView)
    {
        super(itemView);

        rv_receive=itemView.findViewById(R.id.rl_recieve);
        rv_send=itemView.findViewById(R.id.rl_send);

        dp_receive=itemView.findViewById(R.id.circular_image_recieve);
        dp_send=itemView.findViewById(R.id.circular_image_send);

        btn_receive=itemView.findViewById(R.id.btn_receive);
        btn_send=itemView.findViewById(R.id.btn_send);


    }



}
