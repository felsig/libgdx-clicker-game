package net.emptycatchblocks.libgdxclickergame.screen.loading;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.emptycatchblocks.libgdxclickergame.ClickerGame;
import net.emptycatchblocks.libgdxclickergame.assets.AssetDescriptors;
import net.emptycatchblocks.libgdxclickergame.config.GameConfig;
import net.emptycatchblocks.libgdxclickergame.screen.game.GameScreen;
import net.emptycatchblocks.libgdxclickergame.util.GdxUtils;

public class LoadingScreen extends ScreenAdapter{

    private static final Logger log = new Logger(LoadingScreen.class.getName(), Logger.DEBUG);

    private static final float PROGRESS_BAR_WIDTH = GameConfig.HUD_WIDTH / 2f;
    private static final float PROGRESS_BAR_HEIGHT = 60; // world units

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer renderer;

    private float progress;
    private float waitTime = 0.75f;
    private boolean changeScreen = false;

    private final ClickerGame game;
    private final AssetManager assetManager;

    public LoadingScreen(ClickerGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT, camera);
        renderer = new ShapeRenderer();

        assetManager.load(AssetDescriptors.FONT);
        assetManager.load(AssetDescriptors.GAME_PLAY);
    }

    @Override
    public void render(float delta) {
        update(delta);

        GdxUtils.clearScreen();
        viewport.apply();
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        draw();

        renderer.end();

        if (changeScreen) {
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        renderer = null;
    }

    private void update(float delta) {
        progress = assetManager.getProgress();

        if (assetManager.update()) {
            waitTime -= delta;

            if (waitTime <= 0) {
                changeScreen = true;
            }
        }
    }

    private void draw() {
        float progressBarX = (GameConfig.HUD_WIDTH - PROGRESS_BAR_WIDTH) / 2f;
        float progressBarY = (GameConfig.HUD_HEIGHT - PROGRESS_BAR_HEIGHT) / 2f;

        renderer.rect(progressBarX, progressBarY,
                progress * PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);
    }
}
