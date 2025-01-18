package com.example.healthandfitness.Models;

public class Challenge {
    private int imageResource;
    private String title;
    private String description;
    private String steps;
    private String reward;  // We could rename it to 'reward' or keep 'snack'
    private String status;  // Status of the challenge (Pending, Completed, Expired)
    private Snack snack;    // Reward (snack) object

    // Constructor
    public Challenge(int imageResource, String title, String description, String steps, String reward, String status, Snack snack) {
        this.imageResource = imageResource;
        this.title = title;
        this.description = description;
        this.steps = steps;
        this.reward = reward;
        this.status = status;
        this.snack = snack;
    }

    // Getters and Setters
    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Snack getSnack() {
        return snack;
    }

    public void setSnack(Snack snack) {
        this.snack = snack;
    }

    // A placeholder Snack class to represent rewards (snack)
    public static class Snack {
        private String name;
        private int calories;

        public Snack(String name, int calories) {
            this.name = name;
            this.calories = calories;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCalories() {
            return calories;
        }

        public void setCalories(int calories) {
            this.calories = calories;
        }
    }
}
