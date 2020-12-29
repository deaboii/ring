package com.deeaboi.ring.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.deeaboi.ring.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class userListViewHolder extends RecyclerView.ViewHolder
{

   public   TextView number;
   public CircleImageView dp;
   public CardView cardView;

    public userListViewHolder(@NonNull View itemView)
    {
        super(itemView);

        number=itemView.findViewById(R.id.phonenumber);
        dp=itemView.findViewById(R.id.user_profile_image);
        cardView=itemView.findViewById(R.id.card_view);

    }

}
