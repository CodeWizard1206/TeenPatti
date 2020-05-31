package com.pacss.teenPatti;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class chipBuyerAdapter extends RecyclerView.Adapter<chipBuyerAdapter.listViewHolder> {
    private String[] chipAmount;
    private String[] rupeeAmount;
    private Context context;

    chipBuyerAdapter(String[] chipAmount, String[] rupeeAmount, Context context) {
        this.chipAmount = chipAmount;
        this.rupeeAmount = rupeeAmount;
        this.context = context;
    }

    @NonNull
    @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.chip_selector_block, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull listViewHolder holder, final int position) {
        holder.Chips.setText(chipAmount[position]);
        holder.Money.setText(rupeeAmount[position]);
        holder.chipCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, paymentGateway.class);
                intent.putExtra("CHIPS", chipAmount[position]);
                intent.putExtra("MONEY", rupeeAmount[position]);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chipAmount.length;
    }

    class listViewHolder extends RecyclerView.ViewHolder {
        TextView Chips;
        TextView Money;
        CardView chipCard;

        listViewHolder(View itemView) {
            super(itemView);
            Chips = itemView.findViewById(R.id.chipCount);
            Money = itemView.findViewById(R.id.payeeAmount);
            chipCard = itemView.findViewById(R.id.chipCard);
        }
    }
}
