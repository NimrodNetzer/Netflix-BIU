package com.example.netflix_android.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_android.Entities.Category;
import com.example.netflix_android.R;
import com.example.netflix_android.View.AddCategoryActivity;
import com.example.netflix_android.View.AdminActivity;
import com.example.netflix_android.ViewModel.CategoryViewModel;

import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.CategoryViewHolder> {

    private final AdminActivity activity;
    private final List<Category> categories;
    private final CategoryViewModel categoryViewModel;

    public AdminCategoryAdapter(AdminActivity activity, List<Category> categories, CategoryViewModel categoryViewModel) {
        this.activity = activity;
        this.categories = categories;
        this.categoryViewModel = categoryViewModel;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryTitle.setText(category.getName());
        holder.categoryType.setText(category.isPromoted() ? "🌟 Promoted" : "📂 Regular");

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity, AddCategoryActivity.class);
            intent.putExtra("category_id", category.getId());
            intent.putExtra("category_name", category.getName());
            intent.putExtra("category_promoted", category.isPromoted());
            activity.startActivityForResult(intent, 1);
        });

        holder.deleteButton.setOnClickListener(v -> {
            categoryViewModel.deleteCategory(category.getId());

            // Delay reload slightly to allow LiveData update or server sync
            holder.itemView.postDelayed(() -> {
                Toast.makeText(activity, "🗑️ Category deleted. Reloading...", Toast.LENGTH_SHORT).show();
                activity.loadCategories();
            }, 500); // short delay
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle, categoryType;
        ImageButton editButton, deleteButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.category_title);
            categoryType = itemView.findViewById(R.id.category_type);
            editButton = itemView.findViewById(R.id.button_edit);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}
