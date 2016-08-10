package com.fernandocs.firebase.quickstart.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fernandocs.firebase.quickstart.CircleTransform;
import com.fernandocs.firebase.quickstart.R;
import com.fernandocs.firebase.quickstart.models.Post;
import com.squareup.picasso.Picasso;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public ImageView authorPhotoView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;
    public ImageView postPhoto;

    public PostViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.post_title);
        authorPhotoView = (ImageView) itemView.findViewById(R.id.post_author_photo);
        authorView = (TextView) itemView.findViewById(R.id.post_author);
        starView = (ImageView) itemView.findViewById(R.id.star);
        numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);
        postPhoto = (ImageView) itemView.findViewById(R.id.post_photo);
    }

    public void bindToPost(Context context, Post post, View.OnClickListener starClickListener) {
        titleView.setText(post.title);
        if (!TextUtils.isEmpty(post.authorPhotoUrl)) {
            Picasso.with(context).load(post.authorPhotoUrl).transform(new CircleTransform())
                    .placeholder(R.drawable.ic_action_account_circle_40)
                    .error(R.drawable.ic_action_account_circle_40).into(authorPhotoView);
        } else {
            authorPhotoView.setImageResource(R.drawable.ic_action_account_circle_40);
        }
        authorView.setText(post.author);
        numStarsView.setText(String.valueOf(post.starCount));
        bodyView.setText(post.body);
        if (!TextUtils.isEmpty(post.photoUrl)) {
            postPhoto.setVisibility(View.VISIBLE);
            Picasso.with(context).load(post.photoUrl)
                    .placeholder(R.drawable.img_default)
                    .resize(100, 100).onlyScaleDown()
                    .error(R.drawable.img_default).into(postPhoto);
        } else {
            postPhoto.setVisibility(View.GONE);
        }

        starView.setOnClickListener(starClickListener);
    }
}
