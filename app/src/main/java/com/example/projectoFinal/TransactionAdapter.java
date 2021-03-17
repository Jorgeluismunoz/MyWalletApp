package com.example.projectoFinal;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recycleviewlist.R;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Transaction> mItemTrans;
    private ViewHolder LastViewHolder = null;
    private boolean editMode = false; //Check when is eddint an existent item in transaction arayList.

    public TransactionAdapter(Context context, ArrayList<Transaction> listItem) {
        this.context = context;
        this.mItemTrans = listItem;
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_trans, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Transaction balance = mItemTrans.get(position);
        holder.mType.setText(balance.getType());
        if (balance.getType() == "Income") holder.mType.setTextColor(Color.parseColor("#427605"));
        if (balance.getType() == "Expense")holder.mType.setTextColor(Color.parseColor("#6A0202"));


        holder.mDescription.setText(balance.getDescription());
        if (balance.getMoviment() < 0)  holder.mTransaction.setText("-$" +balance.getMoviment() * -1 +"");
        else holder.mTransaction.setText(" $" +balance.getMoviment()+"");
        String date = balance.getDate();
        holder.mDate.setText(date.substring(3, 6) +"\n " + date.substring(0, 2));

        holder.mDelateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((DetailsActivity)context).removeTrans(position);
            }
        });

        holder.mLArrowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LastViewHolder != null && LastViewHolder.mLArrowIcon.getVisibility() == View.GONE)
                {
                    LastViewHolder.mEditIcon.setVisibility(View.GONE);
                    LastViewHolder.mDelateIcon.setVisibility(View.GONE);
                    LastViewHolder.mRArrowIcon.setVisibility(View.GONE);
                    LastViewHolder.mLArrowIcon.setVisibility(View.VISIBLE);
                    if (editMode) {
                        LastViewHolder.mRelativeHolder.setCardBackgroundColor(Color.WHITE);
                        LastViewHolder.mDescription.setTextColor(Color.BLACK);
                        LastViewHolder.mTransaction.setTextColor(Color.BLACK);
                        LastViewHolder.mDate.setTextColor(Color.parseColor("#FFAAAAAA"));
                        editMode = false;
                        ((DetailsActivity)context).EditTrans(position, editMode, holder);
                    }
                }
                holder.mEditIcon.setVisibility(View.VISIBLE);
                holder.mDelateIcon.setVisibility(View.VISIBLE);
                holder.mLArrowIcon.setVisibility(View.GONE);
                holder.mRArrowIcon.setVisibility(View.VISIBLE);
                LastViewHolder = holder;
            }
        });

        holder.mRArrowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.mEditIcon.setVisibility(View.GONE);
                holder.mDelateIcon.setVisibility(View.GONE);
                holder.mLArrowIcon.setVisibility(View.VISIBLE);
                holder.mRArrowIcon.setVisibility(View.GONE);

            }
        });


        holder.mEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMode = true; //Star mode edit existent item.
                holder.mRArrowIcon.setVisibility(View.GONE);
                ((DetailsActivity)context).EditTrans(position, editMode, holder);
                if (holder.mEditIcon.getVisibility() == View.GONE) holder.mEditIcon.setVisibility(View.VISIBLE);
                holder.mEditIcon.setVisibility(View.GONE);
                holder.mSaveIcon.setVisibility(View.GONE);
                holder.mDelateIcon.setVisibility(View.GONE);

                //Change color of addPanel to editMode (light blue)
                holder.mRelativeHolder.setCardBackgroundColor(Color.parseColor("#B8D5FC"));
                holder.mDescription.setTextColor(Color.WHITE);
                holder.mTransaction.setTextColor(Color.WHITE);
                holder.mDate.setTextColor(Color.WHITE);
                //---------------------
            }
        });
        holder.mSaveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DetailsActivity)context).SaveTrans(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mItemTrans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mType;
        public TextView mDescription;
        public EditText mTransaction;
        public TextView mDate;

        public ImageView mDelateIcon;
        public ImageView mEditIcon;
        public ImageView mSaveIcon;
        public ImageView mLArrowIcon;
        public ImageView mRArrowIcon;
        public CardView mRelativeHolder;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mType = itemView.findViewById(R.id.typeID);
            mDescription = itemView.findViewById(R.id.descID);
            mTransaction = itemView.findViewById(R.id.transactionID);
            mDelateIcon = itemView.findViewById(R.id.deleteID);
            mLArrowIcon = itemView.findViewById(R.id.rigthArrowID);
            mRArrowIcon = itemView.findViewById(R.id.RigthArrowID);
            mEditIcon = itemView.findViewById(R.id.editID);
            mSaveIcon = itemView.findViewById(R.id.saveID);
            mDate = itemView.findViewById(R.id.dateID);
            mRelativeHolder = itemView.findViewById(R.id.cardview);


        }


        @Override
        public void onClick(View v) {

//            int position = getAdapterPosition();
//            Transaction item = mItemTrans.get(position);
//            Intent intent = new Intent(context, DetailsActivity.class);
//            //intent.putExtra("myObject", item);
//
//            context.startActivity(intent);
        }
    }
}
