package com.example.aiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout dotsLayout;
    private Button skipBtn, nextBtn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        sharedPreferences = getSharedPreferences("aiapp_prefs", MODE_PRIVATE);

        initializeUI();
        setupOnboardingPages();
    }

    private void initializeUI() {
        viewPager = findViewById(R.id.viewpager_onboarding);
        dotsLayout = findViewById(R.id.dots_layout);
        skipBtn = findViewById(R.id.btn_skip);
        nextBtn = findViewById(R.id.btn_next_onboarding);

        skipBtn.setOnClickListener(v -> completeOnboarding());
        nextBtn.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < 4) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                completeOnboarding();
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
                nextBtn.setText(position == 4 ? "Get Started" : "Next");
            }
        });
    }

    private void setupOnboardingPages() {
        List<OnboardingPage> pages = new ArrayList<>();
        pages.add(new OnboardingPage(
            "Welcome to CreativeAI",
            "Create stunning visual content with AI-powered tools",
            R.drawable.ic_onboarding_welcome
        ));
        pages.add(new OnboardingPage(
            "Generate Beautiful Images",
            "Turn your imagination into reality using Stable Diffusion",
            R.drawable.ic_onboarding_image
        ));
        pages.add(new OnboardingPage(
            "Create Videos & Content",
            "From text to video in seconds. Perfect for social media",
            R.drawable.ic_onboarding_video
        ));
        pages.add(new OnboardingPage(
            "Analyze & Understand",
            "Get insights from images, videos, and text instantly",
            R.drawable.ic_onboarding_analyze
        ));
        pages.add(new OnboardingPage(
            "Ready to Create?",
            "Let's start building amazing content together!",
            R.drawable.ic_onboarding_rocket
        ));

        OnboardingAdapter adapter = new OnboardingAdapter(pages);
        viewPager.setAdapter(adapter);

        createDots(pages.size());
    }

    private void createDots(int size) {
        ImageView[] dots = new ImageView[size];
        for (int i = 0; i < size; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getDrawable(i == 0 ? 
                R.drawable.dot_active : R.drawable.dot_inactive));
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dotsLayout.addView(dots[i], params);
        }
    }

    private void updateDots(int position) {
        int childCount = dotsLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView dot = (ImageView) dotsLayout.getChildAt(i);
            dot.setImageDrawable(getDrawable(i == position ? 
                R.drawable.dot_active : R.drawable.dot_inactive));
        }
    }

    private void completeOnboarding() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("onboarding_complete", true);
        editor.apply();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public static class OnboardingPage {
        String title, description;
        int imageResId;

        public OnboardingPage(String title, String description, int imageResId) {
            this.title = title;
            this.description = description;
            this.imageResId = imageResId;
        }
    }
}
