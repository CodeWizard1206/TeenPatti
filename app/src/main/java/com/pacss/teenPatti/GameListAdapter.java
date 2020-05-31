package com.pacss.teenPatti;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.pacss.teenPatti.gameHandler.GameDataHandler;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.listViewHolder>{

    private GameDataHandler[] data;
    private Context context;

    GameListAdapter(GameDataHandler[] data, Context context) {
        this.data = data;
        this.context = context;
    }


    @NonNull
    @Override
    public GameListAdapter.listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.game_list, parent, false);
        return new listViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GameListAdapter.listViewHolder holder, final int position) {
        holder.rowNumber.setText(Integer.toString(position + 1));
        holder.gameType.setText(data[position].getGameType());
        holder.potValue.setText(data[position].getPotValue());
        holder.gameStatus.setText(data[position].getGameStatus());

        holder.listCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!data[position].getGameStatus().equals("WAITING")) {
                    Intent intent = new Intent(context, GameInfoActivity.class);
                    intent.putExtra("ID", data[position].getGameID());
                    intent.putExtra("BOT", data[position].getBotAmount());
                    intent.putExtra("STATUS", data[position].getGameStatus());
                    intent.putExtra("POT", data[position].getPotValue());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    class listViewHolder extends RecyclerView.ViewHolder {
        TextView rowNumber;
        TextView gameType;
        TextView potValue;
        TextView gameStatus;
        MaterialCardView listCard;

        listViewHolder(View itemView) {
            super(itemView);
            rowNumber = itemView.findViewById(R.id.rowNumber);
            gameType = itemView.findViewById(R.id.gameType);
            potValue = itemView.findViewById(R.id.potValue);
            gameStatus = itemView.findViewById(R.id.gameStatus);
            listCard = itemView.findViewById(R.id.listCard);
        }
    }
}
