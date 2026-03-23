package com.example.aiapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private List<OnboardingActivity.OnboardingPage> pages;

    public OnboardingAdapter(List<OnboardingActivity.OnboardingPage> pages) {
        this.pages = pages;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_onboarding_page, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        OnboardingActivity.OnboardingPage page = pages.get(position);
        holder.bind(page);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    public static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView titleView;
        private TextView descriptionView;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.onboarding_image);
            titleView = itemView.findViewById(R.id.onboarding_title);
            descriptionView = itemView.findViewById(R.id.onboarding_description);
        }

        public void bind(OnboardingActivity.OnboardingPage page) {
            imageView.setImageResource(page.imageResId);
            titleView.setText(page.title);
            descriptionView.setText(page.description);
        }
    }
}
