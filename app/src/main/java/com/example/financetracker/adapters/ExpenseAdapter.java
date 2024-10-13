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

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private ArrayList<Data> expenseList;
    private OnItemClickListener onItemClickListener;

    public ExpenseAdapter(ArrayList<Data> expenseList, OnItemClickListener onItemClickListener) {
        this.expenseList = expenseList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense_card, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Data expense = expenseList.get(position);
        holder.dateTextView.setText(expense.getDate());
        holder.noteTextView.setText(expense.getNote());
        holder.typeTextView.setText(expense.getType());
        holder.amountTextView.setText(String.valueOf(expense.getAmount()));

        holder.updateImageView.setOnClickListener(v -> onItemClickListener.onUpdateClick(expense));
        holder.deleteImageView.setOnClickListener(v -> onItemClickListener.onDeleteClick(expense));
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public interface OnItemClickListener {
        void onUpdateClick(Data expense);
        void onDeleteClick(Data expense);
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, noteTextView, typeTextView, amountTextView;
        ImageView updateImageView, deleteImageView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.text_View_expense_Date);
            noteTextView = itemView.findViewById(R.id.text_View_expense_Note);
            typeTextView = itemView.findViewById(R.id.text_View_expense_Type);
            amountTextView = itemView.findViewById(R.id.text_View_expense_Amount);

            updateImageView = itemView.findViewById(R.id.image_View_Update_expense);
            deleteImageView = itemView.findViewById(R.id.image_View_Delete_expense);
        }
    }
}
