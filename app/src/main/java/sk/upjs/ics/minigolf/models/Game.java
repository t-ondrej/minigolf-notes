package sk.upjs.ics.minigolf.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sk.upjs.ics.minigolf.R;
import sk.upjs.ics.minigolf.dataaccess.Contract;

import static sk.upjs.ics.minigolf.dataaccess.Constants.AUTOGENERATED_ID;

public class Game implements PlayerManager {

    private Long id;
    private int hitCountMax;
    private int holeCount;
    private long timestamp;
    private double longitude;
    private double latitude;
    private String photoPath;

    private List<Player> players = new ArrayList<>();

    /**
     * Constructors
     */
    public Game() {
        id = -1L;
        timestamp = System.currentTimeMillis();
    }

    public Game(Long id, int hitCountMax, int holeCount, long timestamp, double longitude,
                double latitude, String photoPath) {
        this.id = id;
        this.hitCountMax = hitCountMax;
        this.holeCount = holeCount;
        this.timestamp = timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
        this.photoPath = photoPath;
    }

    public Game(Long id, int hitCountMax, int holeCount, long timestamp, double longitude,
                double latitude, String photoPath, List<Player> players) {
        this(id, hitCountMax, holeCount, timestamp, longitude, latitude, photoPath);
        this.players = players;
    }

    public static Game createFreshGame(Resources resources) {
        Game game = new Game();
        game.addPlayer(Player.createWithDefaultName(1, resources));
        return game;
    }

    /**
     * Bundles
     */
    // Called during onSaveInstanceState
    public Bundle toBundle(Bundle bundle) {
        bundle.putLong("id", id);
        bundle.putInt("hitCountMax", hitCountMax);
        bundle.putInt("holeCount", holeCount);
        bundle.putLong("timestamp", timestamp);
        bundle.putDouble("longitude", longitude);
        bundle.putDouble("latitude", latitude);
        bundle.putString("photopath", photoPath);

        for (int i = 0; i < players.size(); i++) {
            bundle.putBundle("Player " + i, players.get(i).toBundle());
        }

        return bundle;
    }

    // Called when passing state to another activity
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        toBundle(bundle);
        return bundle;
    }

    public static Game fromBundle(Bundle bundle) {
        List<Player> players = new ArrayList<>();

        for (String string : bundle.keySet()) {
            if (string.startsWith("Player")) {
                players.add(Player.fromBundle(bundle.getBundle(string)));
            }
        }

        return new Game(bundle.getLong("id"),
                bundle.getInt("hitCountMax"),
                bundle.getInt("holeCount"),
                bundle.getLong("timestamp"),
                bundle.getDouble("longitude"),
                bundle.getDouble("latitude"),
                bundle.getString("photopath"),
                players);
    }


    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Game._ID, AUTOGENERATED_ID);
        contentValues.put(Contract.Game.HITCOUNTMAX, getHitCountMax());
        contentValues.put(Contract.Game.HOLECOUNT, getHoleCount());
        contentValues.put(Contract.Game.TIMESTAMP, getTimestamp());
        contentValues.put(Contract.Game.LATITUDE, getLatitude());
        contentValues.put(Contract.Game.LONGITUDE, getLongitude());
        contentValues.put(Contract.Game.PHOTOURI, getPhotoPath());

        return contentValues;
    }

    public static Game fromCursor(Cursor cursor) {
        // Prolly move to next position here

        long id = cursor.getLong(cursor.getColumnIndex(Contract.Game._ID));
        int hitCountMax = cursor.getInt(cursor.getColumnIndex(Contract.Game.HITCOUNTMAX));
        int holeCount = cursor.getInt(cursor.getColumnIndex(Contract.Game.HOLECOUNT));
        long timestamp = cursor.getLong(cursor.getColumnIndex(Contract.Game.TIMESTAMP));
        double longitude = cursor.getDouble(cursor.getColumnIndex(Contract.Game.LONGITUDE));
        double latitude = cursor.getDouble(cursor.getColumnIndex(Contract.Game.LATITUDE));
        String photouri = cursor.getString(cursor.getColumnIndex(Contract.Game.PHOTOURI));

        List<Player> players = Player.allFromCursor(cursor);

        Game game = new Game(id, hitCountMax, holeCount, timestamp, longitude, latitude, photouri, players);
        cursor.close();

        return game;
    }

    public static Game fromCursorWithoutPlayers(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(Contract.Game._ID));
        int hitCountMax = cursor.getInt(cursor.getColumnIndex(Contract.Game.HITCOUNTMAX));
        int holeCount = cursor.getInt(cursor.getColumnIndex(Contract.Game.HOLECOUNT));
        long timestamp = cursor.getLong(cursor.getColumnIndex(Contract.Game.TIMESTAMP));
        double longitude = cursor.getDouble(cursor.getColumnIndex(Contract.Game.LONGITUDE));
        double latitude = cursor.getDouble(cursor.getColumnIndex(Contract.Game.LATITUDE));
        String photouri = cursor.getString(cursor.getColumnIndex(Contract.Game.PHOTOURI));

        Game game = new Game(id, hitCountMax, holeCount, timestamp, longitude, latitude, photouri);
     //   cursor.close();

        return game;
    }

    public static List<Game> allFromCursor(Cursor cursor) {
        List<Game> games = new ArrayList<>();

        if (cursor.moveToFirst()){
            do {
                long id = cursor.getLong(cursor.getColumnIndex(Contract.Game._ID));
                int hitCountMax = cursor.getInt(cursor.getColumnIndex(Contract.Game.HITCOUNTMAX));
                int holeCount = cursor.getInt(cursor.getColumnIndex(Contract.Game.HOLECOUNT));
                long timestamp = cursor.getLong(cursor.getColumnIndex(Contract.Game.TIMESTAMP));
                double longitude = cursor.getDouble(cursor.getColumnIndex(Contract.Game.LONGITUDE));
                double latitude = cursor.getDouble(cursor.getColumnIndex(Contract.Game.LATITUDE));
                String photouri = cursor.getString(cursor.getColumnIndex(Contract.Game.PHOTOURI));

                games.add(new Game(id, hitCountMax, holeCount, timestamp, longitude, latitude, photouri));

            } while(cursor.moveToNext());
        }

       // cursor.close();
        return games;
    }

    @Override
    public int getPlayerCount() {
        return players.size();
    }

    @Override
    public void addPlayer(Player player) {
        players.add(player);
        player.setGame(this);
    }

    @Override
    public Player findPlayerByPosition(int idx) {
        return players.get(idx);
    }

    @Override
    public Player findPlayerByName(String name) {
        Player player = null;
        for (Player playerr : players)
            if (playerr.getName().equals(name)) {
                player = playerr;
                break;
            }

        return player;
    }

    @Override
    public void removePlayerByPosition(int position) {
        players.remove(position);
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public List<Player> getPlayersSortedByScore() {
        List<Player> playersCopy = new ArrayList<>(players);
        Collections.sort(playersCopy, (p1, p2) -> Integer.compare(p1.getScore(), p2.getScore()));
        return playersCopy;
    }

    public List<Integer> getPossibleScores() {
        List<Integer> scores = new ArrayList<>();
        for (int i = 0; i <= hitCountMax; i++) {
            scores.add(i);
        }

        return scores;
    }

    public float[] getAverageScoresAtHoles() {
        float[] scores = new float[holeCount];

        for (int i = 0; i < holeCount; i++) {
            float sum = 0;

            for (Player player : players) {
                sum += player.getScoreAtHole(i);
            }

            scores[i] = sum / players.size();
        }

        return scores;
    }

    public int getHoleCount() {
        return holeCount;
    }

    public void setHoleCount(int holeCount) {
        this.holeCount = holeCount;

        for (Player player : players) {
            player.createScoreArray(this.holeCount);
        }

    }

    public Long getId() {
        return id;
    }

    public int getHitCountMax() {
        return hitCountMax;
    }

    public void setHitCountMax(int hitCountMax) {
        this.hitCountMax = hitCountMax;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Date getDate() {
        Date resultdate = new Date(timestamp);
        return resultdate;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress(Context context) {
        Geocoder geocoder;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses.size() < 1) {
            return context.getResources().getString(R.string.no_adress);
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();

        return address;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
