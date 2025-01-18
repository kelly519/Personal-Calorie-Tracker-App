package com.example.healthandfitness.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.healthandfitness.Models.Challenge;
import com.example.healthandfitness.R;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

public class ChallengeDetailsActivity extends AppCompatActivity {

    private ImageView challengeImage;
    private TextView challengeTitle, challengeDescription, challengeSteps;
    private Button completeChallengeButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_details);

        // Initialize views
        challengeImage = findViewById(R.id.challenge_image);
        challengeTitle = findViewById(R.id.challenge_title);
        challengeDescription = findViewById(R.id.challenge_description);
        challengeSteps = findViewById(R.id.challenge_steps);
        completeChallengeButton = findViewById(R.id.btn_complete_challenge);

        // Get challenge details from intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String steps = getIntent().getStringExtra("steps");
        int imageResource = getIntent().getIntExtra("image", R.drawable.challenge1);

        // Set data to views
        challengeImage.setImageResource(imageResource);
        challengeTitle.setText(title);
        challengeDescription.setText(description);
        challengeSteps.setText(steps);

        // Complete challenge button logic
        completeChallengeButton.setOnClickListener(view -> showConfirmationDialog());
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Completion")
                .setMessage("Are you sure you want to complete this challenge?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    showCompletionDialog(); // Show the completion dialog if confirmed
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Dismiss the dialog on 'No'
                .create()
                .show();
    }

    private void showCompletionDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_challenge_completed, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Load the GIF into the ImageView
        ImageView gifImageView = dialogView.findViewById(R.id.icon_check);
        Glide.with(this)
                .asGif()
                .load(R.drawable.verified) // Replace with your GIF
                .into(gifImageView);

        Button okButton = dialogView.findViewById(R.id.ok_button);
        okButton.setOnClickListener(v ->
                finish()               );


        dialog.show();
    }

}
