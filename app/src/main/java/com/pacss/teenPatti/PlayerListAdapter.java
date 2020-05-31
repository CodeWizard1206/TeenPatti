package com.pacss.teenPatti;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.pacss.teenPatti.gameHandler.Card;
import com.pacss.teenPatti.gameHandler.PlayerDataHolder;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.listViewHolder>{

    private Context context;
    private PlayerDataHolder[] data;

    public PlayerListAdapter(Context context, PlayerDataHolder[] data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public PlayerListAdapter.listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.player_card, parent, false);
        return new PlayerListAdapter.listViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PlayerListAdapter.listViewHolder holder, final int position) {
        holder.rowNumber.setText(Integer.toString(position + 1));
        System.out.println(data[position].getPlayerName());
        holder.playerName.setText(data[position].getPlayerName());
        holder.tokenValue.setText(data[position].getPlayerTokens());
        holder.seenStatus.setText(data[position].getPlayerSeenStatus());
        Card[] cards = data[position].getPlayerCards();
        holder.playerCardOne.setBackground(context.getDrawable(context.getResources().getIdentifier("c" + cards[0].toString()
                , "drawable", context.getPackageName())));
        holder.playerCardTwo.setBackground(context.getDrawable(context.getResources().getIdentifier("c" + cards[1].toString()
                , "drawable", context.getPackageName())));
        holder.playerCardThree.setBackground(context.getDrawable(context.getResources().getIdentifier("c" + cards[2].toString()
                , "drawable", context.getPackageName())));

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    class listViewHolder extends RecyclerView.ViewHolder {
        TextView rowNumber;
        TextView playerName;
        TextView tokenValue;
        TextView seenStatus;
        AppCompatImageView playerCardOne, playerCardTwo, playerCardThree;

        listViewHolder(View itemView) {
            super(itemView);
            rowNumber = itemView.findViewById(R.id.rowNumber);
            playerName = itemView.findViewById(R.id.playerName);
            tokenValue = itemView.findViewById(R.id.totalToken);
            seenStatus = itemView.findViewById(R.id.seenStatus);
            playerCardOne = itemView.findViewById(R.id.playerCardOne);
            playerCardTwo = itemView.findViewById(R.id.playerCardTwo);
            playerCardThree = itemView.findViewById(R.id.playerCardThree);
        }
    }
}
