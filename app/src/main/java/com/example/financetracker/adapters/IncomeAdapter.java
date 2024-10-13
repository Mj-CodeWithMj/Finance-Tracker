// IncomeAdapter.java
package com.example.financetracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financetracker.R;
import com.example.financetracker.model.Data;

import java.util.ArrayList;
import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {

    private ArrayList<Data> incomeList;
    private OnItemClickListener onItemClickListener;

    public IncomeAdapter(ArrayList<Data> incomeList, OnItemClickListener onItemClickListener) {
        this.incomeList = incomeList;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income_card, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        Data income = incomeList.get(position);
        holder.dateTextView.setText(income.getDate());
        holder.noteTextView.setText(income.getNote());
        holder.typeTextView.setText(income.getType());
        holder.amountTextView.setText(String.valueOf(income.getAmount()));

        holder.updateImageView.setOnClickListener(v -> onItemClickListener.onUpdateClick(income));
        holder.deleteImageView.setOnClickListener(v -> onItemClickListener.onDeleteClick(income));
    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    public interface OnItemClickListener {
        void onUpdateClick(Data income);
        void onDeleteClick(Data income);
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, noteTextView, typeTextView, amountTextView;
        ImageView updateImageView, deleteImageView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.text_View_Income_Date);
            noteTextView = itemView.findViewById(R.id.text_View_Income_Note);
            typeTextView = itemView.findViewById(R.id.text_View_Income_Type);
            amountTextView = itemView.findViewById(R.id.text_View_Income_Amount);

            updateImageView = itemView.findViewById(R.id.image_View_Update_income);
            deleteImageView = itemView.findViewById(R.id.image_View_Delete_income);
        }
    }
}
