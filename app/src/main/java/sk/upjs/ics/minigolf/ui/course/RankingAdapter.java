package sk.upjs.ics.minigolf.ui.course;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.upjs.ics.minigolf.R;
import sk.upjs.ics.minigolf.models.Game;
import sk.upjs.ics.minigolf.models.Player;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankedPlayerViewHolder> {

    class RankedPlayerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.playerNameEditText)  EditText playerName;
        @BindView(R.id.pointSpinner)        Spinner pointSpinner;

        RankedPlayerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ArrayAdapter<Integer> integerArrayAdapter = new ArrayAdapter<>(RankingAdapter.this.context,
                    R.layout.spinner_item_main,
                    game.getPossibleScores());
            integerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            pointSpinner.setAdapter(integerArrayAdapter);

            pointSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    Log.i("PointSpinner", "Setting score at: " + holeIdx + " to");
                    Player player = game.findPlayerByPosition(getAdapterPosition());
                    player.setScoreAtHole(holeIdx, (int)pointSpinner.getItemAtPosition(position));
                    playerName.setText(player.getNameWithScore());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
        }
    }

    private Game game;
    private Context context;
    private int holeIdx;

    public RankingAdapter(Game game, Context context, int holeIdx) {
        this.game = game;
        this.context = context;
        this.holeIdx = holeIdx;
    }

    @Override
    public RankingAdapter.RankedPlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View otherItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_player, parent, false);

        return new RankedPlayerViewHolder(otherItemView);
    }

    @Override
    public void onBindViewHolder(RankingAdapter.RankedPlayerViewHolder holder, int position) {
            Player player = game.findPlayerByPosition(position);
            holder.playerName.setText(player.getNameWithScore());
            holder.pointSpinner.setSelection(player.getScoreAtHole(holeIdx));
    }

    @Override
    public int getItemCount() {
        return game.getPlayers().size();
    }
}
