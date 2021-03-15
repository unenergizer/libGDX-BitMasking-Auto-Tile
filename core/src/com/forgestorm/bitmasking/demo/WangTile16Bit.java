package com.forgestorm.bitmasking.demo;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class WangTile16Bit extends AbstractWangTile {

    public WangTile16Bit(MapRenderer mapRenderer) {
        super(mapRenderer, "dirt-grass-48", new HashMap<Integer, Vector2>() {
            {
                put(0, new Vector2(48, 48));
                put(2, new Vector2(48, 0));
                put(8, new Vector2(32, 48));
                put(10, new Vector2(96, 0));
                put(11, new Vector2(32, 0));
                put(16, new Vector2(0, 48));
                put(18, new Vector2(64, 0));
                put(22, new Vector2(0, 0));
                put(24, new Vector2(16, 48));
                put(26, new Vector2(80, 0));
                put(27, new Vector2(64, 48));
                put(30, new Vector2(112, 48));
                put(31, new Vector2(16, 0));
                put(64, new Vector2(48, 32));
                put(66, new Vector2(48, 16));
                put(72, new Vector2(96, 32));
                put(74, new Vector2(96, 16));
                put(75, new Vector2(112, 64));
                put(80, new Vector2(64, 32));
                put(82, new Vector2(64, 16));
                put(86, new Vector2(64, 64));
                put(88, new Vector2(80, 32));
                put(90, new Vector2(80, 16));
                put(91, new Vector2(128, 64));
                put(94, new Vector2(144, 64));
                put(95, new Vector2(128, 32));
                put(104, new Vector2(32, 32));
                put(106, new Vector2(80, 48));
                put(107, new Vector2(32, 16));
                put(120, new Vector2(96, 64));
                put(122, new Vector2(128, 48));
                put(123, new Vector2(112, 16));
                put(126, new Vector2(32, 64));
                put(127, new Vector2(112, 32));
                put(208, new Vector2(0, 32));
                put(210, new Vector2(96, 48));
                put(214, new Vector2(0, 16));
                put(216, new Vector2(80, 64));
                put(218, new Vector2(144, 48));
                put(219, new Vector2(48, 64));
                put(222, new Vector2(144, 16));
                put(223, new Vector2(144, 32));
                put(248, new Vector2(16, 32));
                put(250, new Vector2(128, 0));
                put(251, new Vector2(112, 0));
                put(254, new Vector2(144, 0));
                put(255, new Vector2(16, 16));
            }
        });
    }

    @Override
    public void updateAroundTile(int x, int y) {
        // Calculate the auto tile ID that's need
        int northWest = autoTile(x - 1, y + 1);
        int north = autoTile(x, y + 1);
        int northEast = autoTile(x + 1, y + 1);

        int west = autoTile(x - 1, y);
        int east = autoTile(x + 1, y);

        int southWest = autoTile(x - 1, y - 1);
        int south = autoTile(x, y - 1);
        int southEast = autoTile(x + 1, y - 1);

        // Get current auto tile ID
        Tile northWestTile = getMapRenderer().getTile(x - 1, y + 1);
        Tile northTile = getMapRenderer().getTile(x, y + 1);
        Tile northEastTile = getMapRenderer().getTile(x + 1, y + 1);

        Tile westTile = getMapRenderer().getTile(x - 1, y);
        Tile eastTile = getMapRenderer().getTile(x + 1, y);

        Tile southWestTile = getMapRenderer().getTile(x - 1, y - 1);
        Tile southTile = getMapRenderer().getTile(x, y - 1);
        Tile southEastTile = getMapRenderer().getTile(x + 1, y - 1);

        // Update each tile as needed
        updateTile(northWest, northWestTile);
        updateTile(north, northTile);
        updateTile(northEast, northEastTile);

        updateTile(west, westTile);
        updateTile(east, eastTile);

        updateTile(southWest, southWestTile);
        updateTile(south, southTile);
        updateTile(southEast, southEastTile);
    }

    @Override
    public int autoTile(int x, int y) {
        // Directional Check, including corners, returns int
        boolean northTile = detectSameTileType(x, y - 1);
        boolean southTile = detectSameTileType(x, y + 1);
        boolean westTile = detectSameTileType(x - 1, y);
        boolean eastTile = detectSameTileType(x + 1, y);
        boolean northWestTile = detectSameTileType(x - 1, y - 1) && westTile && northTile;
        boolean northEastTile = detectSameTileType(x + 1, y - 1) && northTile && eastTile;
        boolean southWestTile = detectSameTileType(x - 1, y + 1) && southTile && westTile;
        boolean southEastTile = detectSameTileType(x + 1, y + 1) && southTile && eastTile;

        // 8 bit bit masking calculation using directional check booleans values
        return boolToInt(northWestTile) // * 1
                + boolToInt(northTile) * 2
                + boolToInt(northEastTile) * 4
                + boolToInt(westTile) * 8
                + boolToInt(eastTile) * 16
                + boolToInt(southWestTile) * 32
                + boolToInt(southTile) * 64
                + boolToInt(southEastTile) * 128;
    }
}
