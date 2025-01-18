package com.example.healthandfitness.Database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class PreferenceHelper {
    private static final String PREF_NAME = "challenge_prefs";
    private static final String PROGRESS_KEY_PREFIX = "challenge_progress_";

    private SharedPreferences sharedPreferences;

    public PreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("ChallengePrefs", Context.MODE_PRIVATE);
    }

    // Challenge'ı tamamlandı olarak işaretle
    public void markChallengeAsCompleted(int challengeId) {
        sharedPreferences.edit()
                .putBoolean("challenge_" + challengeId, true)
                .apply();
    }

    // Challenge'ın tamamlanıp tamamlanmadığını kontrol et
    public boolean isChallengeCompleted(int challengeId) {
        return sharedPreferences.getBoolean("challenge_" + challengeId, false);
    }

    public void saveChallengeProgress(int challengeId, int progress) {
        sharedPreferences.edit()
                .putInt(PROGRESS_KEY_PREFIX + challengeId, progress)
                .apply();
    }

    public int getChallengeProgress(int challengeId) {
        return sharedPreferences.getInt(PROGRESS_KEY_PREFIX + challengeId, 0);
    }

    public void resetChallengeProgress(int challengeId) {
        sharedPreferences.edit()
                .putInt(PROGRESS_KEY_PREFIX + challengeId, 0)
                .apply();
    }
}
