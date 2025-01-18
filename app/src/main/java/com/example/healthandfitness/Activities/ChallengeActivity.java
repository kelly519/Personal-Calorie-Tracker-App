package com.example.healthandfitness.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthandfitness.Adapters.ChallengeAdapter;
import com.example.healthandfitness.Database.PreferenceHelper;
import com.example.healthandfitness.Models.Challenge;
import com.example.healthandfitness.R;

import java.util.ArrayList;
import java.util.List;

public class ChallengeActivity extends AppCompatActivity {

    private List<Challenge> challengeList;
    private ChallengeAdapter adapter;
    private DatabaseHelperC dbHelper;
    private PreferenceHelper preferenceHelper;

   String USER_ID ; // Static user ID for testing; replace with dynamic ID later

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        dbHelper = new DatabaseHelperC(this);
        preferenceHelper = new PreferenceHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        USER_ID  = sharedPreferences.getString("email", null);
        // Initialize calories for the user
        dbHelper.initializeTotalCalories(USER_ID);

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.challengesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load challenges
        loadChallenges();

        // Setup Adapter
        adapter = new ChallengeAdapter(challengeList, this, new ChallengeAdapter.OnChallengeActionListener() {
            @Override
            public void onCompleteChallenge(Challenge challenge) {
                showCompletionDialog(challenge);
            }

            @Override
            public void onConsumeSnack(Challenge challenge) {
                Toast.makeText(ChallengeActivity.this, "Snack already added!", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void loadChallenges() {
        challengeList = new ArrayList<>();
        PreferenceHelper preferenceHelper = new PreferenceHelper(this);
        // Week 1 Challenge
        addChallengeIfNotCompleted(1, new Challenge(
                    R.drawable.c1,
                    "Week 1 Challenge",
                    "Start Your Fitness Journey",
                    "1. Walk 3 km on any 3 days this week.\n2. Drink at least 2 liters of water daily.\n3. Do 2 sessions of yoga.",
                    "Apple (80 kcal)",
                    "Pending",
                    new Challenge.Snack("Apple", 80)
            ));


        // Week 2 Challenge
        addChallengeIfNotCompleted(2, new Challenge(
                    R.drawable.c2,
                    "Week 2 Challenge",
                    "Build Your Strength",
                    "1. Complete 3 strength training sessions.\n2. Include a protein-rich meal daily.\n3. Stretch for 10 minutes post-workout.",
                    "Banana (100 kcal)",
                    "Pending",
                    new Challenge.Snack("Banana", 100)
            ));


            // Week 3 Challenge
        addChallengeIfNotCompleted(3, new Challenge(
                    R.drawable.c3,
                    "Week 3 Challenge",
                    "Improve Endurance",
                    "1. Run or walk 5 km on 2 days this week.\n2. Drink 3 liters of water daily.\n3. Perform 2 cardio HIIT sessions.",
                    "Protein Bar (150 kcal)",
                    "Pending",
                    new Challenge.Snack("Protein Bar", 150)
            ));


            // Week 4 Challenge
        addChallengeIfNotCompleted(4, new Challenge(
                    R.drawable.c1,
                    "Week 4 Challenge",
                    "Core Strength & Flexibility",
                    "1. Complete 3 core workouts (abs, planks).\n2. Meditate for 10 minutes daily.\n3. Walk 4 km on 2 days.",
                    "Orange (70 kcal)",
                    "Pending",
                    new Challenge.Snack("Orange", 70)
            ));


            // Week 5 Challenge
        addChallengeIfNotCompleted(5, new Challenge(
                    R.drawable.c2,
                    "Week 5 Challenge",
                    "Total Body Focus",
                    "1. Do full-body workouts for 3 days.\n2. Add 10,000 steps daily.\n3. Eat 3 balanced meals a day.",
                    "Almonds (120 kcal)",
                    "Pending",
                    new Challenge.Snack("Almonds", 120)
            ));


            // Week 6 Challenge
        addChallengeIfNotCompleted(6, new Challenge(
                    R.drawable.c3,
                    "Week 6 Challenge",
                    "Mindful Eating & Fitness",
                    "1. Avoid sugar for 5 days.\n2. Perform yoga/stretching for 20 minutes.\n3. Drink 2.5 liters of water daily.",
                    "Yogurt (90 kcal)",
                    "Pending",
                    new Challenge.Snack("Yogurt", 90)
            ));


            // Week 7 Challenge
        addChallengeIfNotCompleted(7, new Challenge(
                    R.drawable.c1,
                    "Week 7 Challenge",
                    "Strength + Endurance Combo",
                    "1. Mix strength and cardio for 3 days.\n2. Include 3 servings of greens.\n3. Walk 6 km this week.",
                    "Walnuts (100 kcal)",
                    "Pending",
                    new Challenge.Snack("Walnuts", 100)
            ));


            // Week 8 Challenge
        addChallengeIfNotCompleted(8, new Challenge(
                    R.drawable.c2,
                    "Week 8 Challenge",
                    "Challenge Yourself",
                    "1. Run 5 km without breaks.\n2. Avoid processed foods for 5 days.\n3. Add flexibility training for 20 minutes.",
                    "Smoothie (150 kcal)",
                    "Pending",
                    new Challenge.Snack("Smoothie", 150)
            ));

    }
    private void addChallengeIfNotCompleted(int challengeId, Challenge challenge) {
        // Challenge tamamlanmamışsa listeye ekle
        if (!preferenceHelper.isChallengeCompleted(challengeId)) {
            challengeList.add(challenge);
        }
    }
    private void showCompletionDialog(Challenge challenge) {
        new AlertDialog.Builder(this)
                .setTitle("Complete Challenge")
                .setMessage("You have completed " + challenge.getTitle())
                .setPositiveButton("Yes", (dialog, which) -> {
                     // Add snack calories


                    markChallengeComplete(challenge);
                    showCompleteDialog();
                })
                .setNegativeButton("No", null)
                .show();
    }
    private void showCompleteDialog() {
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
        okButton.setOnClickListener(v -> finish());
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void markChallengeComplete(Challenge challenge) {
        // Challenge ID'sini başlıktan çıkar (örn: "Week 1 Challenge" -> 1)
        int challengeId = Integer.parseInt(challenge.getTitle().split(" ")[1]);

        // Challenge'ı tamamlandı olarak işaretle
        preferenceHelper.markChallengeAsCompleted(challengeId);

        // Listeden kaldır
        challengeList.remove(challenge);
        adapter.notifyDataSetChanged();
    }

}
