package com.forgestorm.bitmasking.demo;

import com.badlogic.gdx.math.Vector2;
import lombok.Getter;

import java.util.Map;

public abstract class AbstractWangTile {

    /**
     * Get class where the game map is stored.
     */
    @Getter
    private final MapRenderer mapRenderer;

    /**
     * The name of the texture region to use. This region
     * is defined inside the tiles.atlas file.
     */
    @Getter
    private final String regionName;

    /**
     * Maps a calculated auto tile value (ID) to a draw coordinate.
     * These draw coordinates are based on the image template that
     * is being used in the tiles.atlas file.
     */
    private final Map<Integer, Vector2> regionCoordinates;

    public AbstractWangTile(MapRenderer mapRenderer, String regionName, Map<Integer, Vector2> regionCoordinates) {
        this.mapRenderer = mapRenderer;
        this.regionName = regionName;
        this.regionCoordinates = regionCoordinates;
    }

    /**
     * Used to auto update tiles around the supplied X,Y.
     *
     * @param x The X coordinate that needs to be updated.
     * @param y The Y coordinate that needs to be updated.
     */
    public abstract void updateAroundTile(int x, int y);

    /**
     * An implementation used to calculate what tile is
     * being updated. This checks tiles around the given
     * coordinates to see what this X,Y needs to change
     * to.
     *
     * @param x The X coordinate to check.
     * @param y The Y coordinate to check.
     * @return A index total used to see what image needs
     * to be used.
     */
    public abstract int autoTile(int x, int y);

    /**
     * Sets a new tile image at the give X,Y coordinate.
     *
     * @param x     The X coordinate of the tile to update.
     * @param y     The Y coordinate of the tile to update.
     * @param drawl True to drawl, false to erase.
     */
    public void setTile(int x, int y, boolean drawl) {
        Tile tile = mapRenderer.getTile(x, y);
        if (tile == null) return;
        if (drawl) {
            // New tile drawn. Update the tile.
            int auto = autoTile(x, y);
            tile.setAutoTileID(auto);
        } else {

            // Erase tile
            tile.setAutoTileID(MapRenderer.BLANK_TILE_ID);
        }

        // Check if tiles around need to be updated
        updateAroundTile(x, y);
    }

    /**
     * Checks the HashMap to get the region of the image to be drawn.
     *
     * @param autoTileID The tile index that was calculated in autoTile(x,y).
     * @return A {@link Vector2} with the X,Y coordinates of a region to drawl.
     */
    public Vector2 getImageDrawRegion(int autoTileID) {
        return regionCoordinates.get(autoTileID);
    }

    /**
     * Updates a specific tile as specified.
     *
     * @param autoTileID The new calculated auto tile id.
     * @param tile       The tile that needs to be updated.
     */
    protected void updateTile(int autoTileID, Tile tile) {
        if (tile == null) return;
        if (tile.getAutoTileID() == MapRenderer.BLANK_TILE_ID) return;
        if (autoTileID == tile.getAutoTileID()) return;
        tile.setAutoTileID(autoTileID);
    }

    /**
     * Converts a boolean to a numerical value.
     *
     * @param b the boolean to test.
     * @return 1 if true or 0 if false.
     */
    protected int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    /**
     * Detect if this tile position is using the same type of tile as the main one being tested
     *
     * @param x X axis to test.
     * @param y Y axis to test.
     * @return True if the tile type is the same or False if it is not.
     */
    protected boolean detectSameTileType(int x, int y) {
        if (mapRenderer.isOutOfBounds(x, y)) return false; // out of bounds
        return mapRenderer.getAutoTileID(x, y) > MapRenderer.BLANK_TILE_ID;
    }
}
