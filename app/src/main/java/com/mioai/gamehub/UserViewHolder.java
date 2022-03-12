package com.mioai.gamehub;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserViewHolder extends RecyclerView.ViewHolder
{
    CircleImageView profileImageUrl;
    TextView username;
    Button btnShowProfile;
    Button btnDeleteFriend;

    public UserViewHolder(@NonNull View itemView)
    {
        super(itemView);
        profileImageUrl = itemView.findViewById(R.id.circle_profile_icon);
        username = itemView.findViewById(R.id.textview_username);
        btnShowProfile = itemView.findViewById(R.id.btn_show_profile);
        btnDeleteFriend = itemView.findViewById(R.id.btn_delete_friend);

    }
}
