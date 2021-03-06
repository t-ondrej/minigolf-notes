package sk.upjs.ics.minigolf.ui.mainmenu.newgame;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;

import com.warkiz.widget.IndicatorSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sk.upjs.ics.minigolf.GameHolder;
import sk.upjs.ics.minigolf.R;
import sk.upjs.ics.minigolf.models.Game;
import sk.upjs.ics.minigolf.models.Player;
import sk.upjs.ics.minigolf.ui.course.CourseActivity;

import static android.content.Context.LOCATION_SERVICE;
import static sk.upjs.ics.minigolf.Utils.verifyLocationPermissions;

public class NewGameFragment extends Fragment {

    @BindView(R.id.newgameRecyclerView)             RecyclerView recyclerView;
    @BindView(R.id.addPlayerFloatingActionButton)   FloatingActionButton addPlayerButton;
    @BindView(R.id.startGameImageButton)            ImageButton startGameImageButton;
    @BindView(R.id.saveLocationSwitch)              Switch saveLocationSwitch;
    @BindView(R.id.hitCountSeekBar)                 IndicatorSeekBar hitCountSeekBar;
    @BindView(R.id.holeCountSeekBar)                IndicatorSeekBar holesCountSeekBar;

    private NewGamePlayersRecyclerAdapter newGamePlayersRecyclerAdapter;
    private Game game;

    public static NewGameFragment createNewInstance() {
        return new NewGameFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.newgame_fragment, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            game = GameHolder.INSTANCE.initExistingGame(savedInstanceState);
        }
        else {
            game = GameHolder.INSTANCE.initNewGame(getResources());
            newGamePlayersRecyclerAdapter = new NewGamePlayersRecyclerAdapter(game, this.getContext());
            recyclerView.setAdapter(newGamePlayersRecyclerAdapter);
        }
        return view;
    }


    @OnClick(R.id.addPlayerFloatingActionButton)
    public void onAddPlayerButtonClicked(View view) {
        game.addPlayer(Player.createWithDefaultName(game.getPlayerCount() + 1, getResources()));
        newGamePlayersRecyclerAdapter.notifyItemInserted(game.getPlayerCount() - 1);
        newGamePlayersRecyclerAdapter.notifyItemRangeChanged(game.getPlayerCount() - 1, game.getPlayerCount());
    }

    @OnClick(R.id.startGameImageButton)
    public void onStartGameImageButtonClicked(View view) {
        game.setHoleCount(holesCountSeekBar.getProgress());
        game.setHitCountMax(hitCountSeekBar.getProgress());

        for (Player player : game.getPlayers())
            player.createScoreArray(game.getHoleCount());

        if (saveLocationSwitch.isEnabled()) {
            LocationManager locationManager = (LocationManager) this.getContext()
                    .getSystemService(LOCATION_SERVICE);

            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    verifyLocationPermissions(this.getActivity());
                }
                Location location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {
                    game.setLatitude(location.getLatitude());
                    game.setLongitude(location.getLongitude());
                }
            }
        }

        Intent intent = new Intent(getActivity(), CourseActivity.class);
        startActivity(intent);
    }
}
