package com.example.healthandfitness.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthandfitness.Models.Challenge;
import com.example.healthandfitness.R;

import java.util.List;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> {

    private final List<Challenge> challengeList;
    private final Context context;
    private final OnChallengeActionListener listener;


    public interface OnChallengeActionListener {
        void onCompleteChallenge(Challenge challenge);
        void onConsumeSnack(Challenge challenge);
    }

    public ChallengeAdapter(List<Challenge> challengeList, Context context, OnChallengeActionListener listener) {
        this.challengeList = challengeList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.challenge_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Challenge challenge = challengeList.get(position);

        holder.imageView.setImageResource(challenge.getImageResource());
        holder.title.setText(challenge.getTitle());
        holder.description.setText(challenge.getDescription());
        holder.steps.setText(challenge.getSteps());
        holder.status.setText(challenge.getStatus()); // Pending, Completed, Expired
        holder.snack.setText(challenge.getSnack() != null ? "Reward: " + challenge.getSnack().getName() : "No Reward");

        // Mark Challenge as Complete
        holder.completeButton.setOnClickListener(v -> listener.onCompleteChallenge(challenge));

        // Snack Consumption
        holder.consumeButton.setOnClickListener(v -> listener.onConsumeSnack(challenge));

        // Show/Hide Snack Button
        if (challenge.getStatus().equals("Completed")) {
            holder.consumeButton.setVisibility(View.VISIBLE);
        } else {
            holder.consumeButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return challengeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, description, steps, status, snack;
        Button completeButton, consumeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.challenge_image);
            title = itemView.findViewById(R.id.challenge_title);
            description = itemView.findViewById(R.id.challenge_description);
            steps = itemView.findViewById(R.id.challenge_steps);
            status = itemView.findViewById(R.id.challenge_status);
            snack = itemView.findViewById(R.id.challenge_snack);
            completeButton = itemView.findViewById(R.id.btn_complete_challenge);
            consumeButton = itemView.findViewById(R.id.btn_consume_snack);
        }
    }
}
