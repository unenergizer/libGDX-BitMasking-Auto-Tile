package com.forgestorm.bitmasking.demo;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class WangTile4Bit extends AbstractWangTile {

    public WangTile4Bit(MapRenderer mapRenderer) {
        super(mapRenderer, "dirt-grass-4", new HashMap<Integer, Vector2>() {
            {
                put(0, new Vector2(48, 48));
                put(1, new Vector2(48, 0));
                put(2, new Vector2(32, 48));
                put(3, new Vector2(32, 0));
                put(4, new Vector2(0, 48));
                put(5, new Vector2(0, 0));
                put(6, new Vector2(16, 48));
                put(7, new Vector2(16, 0));
                put(8, new Vector2(48, 32));
                put(9, new Vector2(48, 16));
                put(10, new Vector2(32, 32));
                put(11, new Vector2(32, 16));
                put(12, new Vector2(0, 32));
                put(13, new Vector2(0, 16));
                put(14, new Vector2(16, 32));
                put(15, new Vector2(16, 16));
            }
        });
    }

    @Override
    public void updateAroundTile(int x, int y) {
        // Calculate the auto tile ID that's need
        int north = autoTile(x, y + 1);
        int west = autoTile(x - 1, y);
        int east = autoTile(x + 1, y);
        int south = autoTile(x, y - 1);

        // Get current auto tile ID
        Tile northTile = getMapRenderer().getTile(x, y + 1);
        Tile westTile = getMapRenderer().getTile(x - 1, y);
        Tile eastTile = getMapRenderer().getTile(x + 1, y);
        Tile southTile = getMapRenderer().getTile(x, y - 1);

        // Update each tile as needed
        updateTile(north, northTile);
        updateTile(west, westTile);
        updateTile(east, eastTile);
        updateTile(south, southTile);
    }

    @Override
    public int autoTile(int x, int y) {
        // Directional Check
        boolean northTile = detectSameTileType(x, y - 1);
        boolean southTile = detectSameTileType(x, y + 1);
        boolean westTile = detectSameTileType(x - 1, y);
        boolean eastTile = detectSameTileType(x + 1, y);

        // 8 bit bit masking calculation using directional check booleans values
        return boolToInt(northTile) //* 1
                + boolToInt(westTile) * 2
                + boolToInt(eastTile) * 4
                + boolToInt(southTile) * 8;
    }
}
