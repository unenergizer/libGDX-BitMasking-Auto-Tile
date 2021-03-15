package com.forgestorm.bitmasking.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class MapRenderer extends ApplicationAdapter {

    private static final int MAP_SIZE = 16;
    private static final int TILE_SIZE = 16;

    public static final int WINDOW_SIZE = TILE_SIZE * TILE_SIZE * 2;
    public static final int BLANK_TILE_ID = -1;

    @Getter
    private final List<Tile> gameMap = new ArrayList<>();
    private final AssetManager assetManager = new AssetManager();

    private int mouseX, mouseY;

    private AbstractWangTile wangTile;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private TextureAtlas textureAtlas;

    @Override
    public void create() {
        // Initialize starting wang tile here.
        wangTile = new WangTile16Bit(this);

        camera = new OrthographicCamera(WINDOW_SIZE, WINDOW_SIZE);
        camera.zoom = .25f; // Zoom in so it's easy to see the map.
        camera.setToOrtho(false, WINDOW_SIZE, WINDOW_SIZE);

        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();

        // The tile images are loaded inside an atlas.
        assetManager.load("tiles.atlas", TextureAtlas.class);
        assetManager.update();
        assetManager.finishLoading();
        textureAtlas = assetManager.get("tiles.atlas", TextureAtlas.class);

        Gdx.input.setInputProcessor(new GameInput(this));

        // Initialize a basic game map.
        for (int i = 0; i < MAP_SIZE * MAP_SIZE; i++) {
            gameMap.add(new Tile(BLANK_TILE_ID));
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(112 / 255f, 161 / 255f, 94 / 255f, 1);

        // Drawl map tiles.
        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);
        for (int x = 0; x < MAP_SIZE; x++) {
            for (int y = 0; y < MAP_SIZE; y++) {

                // Get tile image of this x,y location.
                Tile tile = getTile(x, y);

                // Check for Blank tile space! If blank, skip rendering.
                if (tile.getAutoTileID() == BLANK_TILE_ID) continue;

                // Get atlas region to use and the draw coordinates of that region.
                TextureAtlas.AtlasRegion atlasRegion = textureAtlas.findRegion(wangTile.getRegionName());
                Vector2 drawCoordinates = wangTile.getImageDrawRegion(tile.getAutoTileID());

                // Draw sprite
                spriteBatch.draw(
                        atlasRegion.getTexture(),
                        x * TILE_SIZE,
                        y * TILE_SIZE,
                        atlasRegion.getRegionX() + (int) drawCoordinates.x,
                        atlasRegion.getRegionY() + (int) drawCoordinates.y,
                        TILE_SIZE,
                        TILE_SIZE);
            }
        }
        spriteBatch.end();

        // Draw red selection square.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.rect(mouseX * TILE_SIZE, mouseY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        assetManager.dispose();
    }

    /**
     * Test to see if supplied X,Y is out of the map bounds.
     *
     * @param x The X coordinate to test.
     * @param y The Y coordinate to test.
     * @return True if out of bounds, false otherwise.
     */
    public boolean isOutOfBounds(int x, int y) {
        return x < 0 || x >= MAP_SIZE || y < 0 || y >= MAP_SIZE;
    }

    /**
     * Gets a {@link Tile} from the game map.
     *
     * @param x The X coordinate of the game map.
     * @param y The Y coordinate of the game map.
     * @return A {@link Tile} in the game map.
     */
    public Tile getTile(int x, int y) {
        if (isOutOfBounds(x, y)) return null; // out of bounds
        return gameMap.get(x + y * MAP_SIZE);
    }

    /**
     * Gets the Value saved for a particular {@link Tile}.
     *
     * @param x The X coordinate of the tile.
     * @param y The Y coordinate of the tile.
     * @return A int of the calculated tile id.
     */
    public int getAutoTileID(int x, int y) {
        return getTile(x, y).getAutoTileID();
    }

    /**
     * Need to reset the game map when changing the wang tiles to be used.
     */
    private void resetGameMap() {
        for (Tile tile : gameMap) {
            tile.setAutoTileID(BLANK_TILE_ID);
        }
    }

    class GameInput extends InputAdapter {

        /**
         * Instance of {@link MapRenderer} used to setup a new WangTile.
         */
        private final MapRenderer mapRenderer;

        /**
         * A {@link Vector3} that is used to transform screen coordinates
         * into game map coordinates.
         */
        private final Vector3 tempVec = new Vector3();

        /**
         * The button that was pressed on the mouse.
         */
        private int button;


        public GameInput(MapRenderer mapRenderer) {
            this.mapRenderer = mapRenderer;
        }

        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.F1) {
                resetGameMap();
                wangTile = new WangTile4Bit(mapRenderer);
                return true;
            } else if (keycode == Input.Keys.F2) {
                resetGameMap();
                wangTile = new WangTile16Bit(mapRenderer);
                return true;
            }

            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (button == Input.Buttons.LEFT) {
                wangTile.setTile(mouseX, mouseY, true);
                this.button = button;
                return true;
            } else if (button == Input.Buttons.RIGHT) {
                wangTile.setTile(mouseX, mouseY, false);
                this.button = button;
                return true;
            }

            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            setMouseCoordinates(screenX, screenY);

            if (button == Input.Buttons.LEFT) {
                wangTile.setTile(mouseX, mouseY, true);
                return true;
            } else if (button == Input.Buttons.RIGHT) {
                wangTile.setTile(mouseX, mouseY, false);
                return true;
            }

            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            setMouseCoordinates(screenX, screenY);
            return false;
        }

        /**
         * Updates the main mouseX and mouseY of the {@link MapRenderer}.
         *
         * @param screenX The X coordinate of a mouse click or movement.
         * @param screenY The Y coordinate of a mouse click or movement.
         */
        private void setMouseCoordinates(int screenX, int screenY) {
            Vector3 vector3 = camera.unproject(tempVec.set(screenX, screenY, 0));
            mouseX = (int) vector3.x / TILE_SIZE;
            mouseY = (int) vector3.y / TILE_SIZE;
        }
    }
}
