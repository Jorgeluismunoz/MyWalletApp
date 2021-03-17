package com.example.projectoFinal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recycleviewlist.R;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MonthAccounting> monthAccounting;

    public ItemAdapter(Context context, List listItem) {
        this.context = context;
        this.monthAccounting = (ArrayList<MonthAccounting>) listItem;


    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MonthAccounting item = monthAccounting.get(position);
        holder.mMonth.setText(item.getMonth());
        holder.mYear.setText(item.getYear()+"");
        holder.mBalance.setText("Total Balance: $" + item.getBalance());

        holder.mDelateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).removeItem(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return monthAccounting.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mMonth;
        public TextView mYear;
        public TextView mBalance;
        public ImageView mDelateImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mMonth = itemView.findViewById(R.id.monthID);
            mYear = itemView.findViewById(R.id.YearID);
            mDelateImage = itemView.findViewById(R.id.deleteID);
            mBalance = itemView.findViewById(R.id.BalanceID);

        }


        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            MonthAccounting item = monthAccounting.get(position);
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("myObject", item);

            context.startActivity(intent);
        }
    }
}
