package net.runelite.client.plugins.ScamYouDownsyndrome;

import com.google.inject.Provides;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.stretchedmode.StretchedModeConfig;

@PluginDescriptor(
        name = "Duel arena odds",
        description = "Give odds to an opponent while hiding the fact they have odds",
        tags = {"rat", "Moe", "scam", "duel arena", "duel", "arena"},
        enabledByDefault = true
)
public class MoeYouPlugin extends Plugin {
    boolean drawnFakeRing_first = false;
    boolean drawnFakePrayer_first = false;
    boolean drawnFakeRing_second = false;
    boolean drawnFakeRing_third = false;
    boolean drawnFakePresetSaved = false;
    boolean drawnFakeLastDuel = false;
    boolean firstEnable = false;
    boolean secondEnable = false;
    boolean thirdEnable = false;
    boolean drawnFakePresetText = false;
    boolean fixedSrollBar = false;
    boolean fixThirdScreenRightBox = false;
    boolean removeFirstItem = false;
    boolean allowMods = false;
    boolean allowMods_NonScam = false;
    String playerName = "";
    boolean usePrayerFlick = false;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ConfigManager configManager;
    @Inject
    private MoeYouConfig config;
    int counter;
    Widget checkBoxLastDuel;

    public MoeYouPlugin() {
    }

    @Provides
    MoeYouConfig getConfig(ConfigManager configManager) {
        return (MoeYouConfig)configManager.getConfig(MoeYouConfig.class);
    }

    protected void startUp() {
        playerName = config.moeScamString().toLowerCase();
        usePrayerFlick = config.moePrayerFlick();
        resetExistingWidgets();
        counter = 0;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
       if(gameStateChanged.getGameState() == GameState.LOGGED_IN) {
           counter = 0;
        }
    }

    protected void shutDown() throws Exception {
        counter = 0;
        playerName = "";
        usePrayerFlick = false;
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("moeyouscam")) {
            playerName = config.moeScamString().toLowerCase();
            usePrayerFlick = config.moePrayerFlick();
            if (usePrayerFlick) {
                counter = 0;
            }
        }

    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widget) {
        if (widget.getGroupId() == DuelWidgets.DUEL_GROUP_ID) {
            clientThread.invokeLater(() -> {
                firstWidgetGroup();
            });
        }

        if (widget.getGroupId() == DuelWidgets.DUEL_OPTIONS_2ND) {
            clientThread.invokeLater(() -> {
                secondWidgetGroup();
            });
        }

        if (widget.getGroupId() == DuelWidgets.DUEL_OPTIONS_3RD) {
            thirdWidgetGroup();
        }

    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (usePrayerFlick) {
            if (counter == 0) {
                Widget widget = client.getWidget(WidgetInfo.MINIMAP_PRAYER_ORB);
                if (widget != null) {
                    Point point = getClickPoint(widget.getBounds());
                    if (point.getX() > 0 && point.getY() > 0) {
                        leftClick(point.getX(), point.getY());
                    }
                }

                ++counter;
            } else if (counter == 1) {
                counter = 0;
            }
        }
    }

    private void leftClick(int x, int y) {
        if (client.isStretchedEnabled()) {
            double scalingFactor = (double)((StretchedModeConfig)configManager.getConfig(StretchedModeConfig.class)).scalingFactor();
            Point p = client.getMouseCanvasPosition();
            if (p.getX() != x || p.getY() != y) {
                moveMouse(x, y);
            }

            double scale = 1.0D + scalingFactor / 100.0D;
            MouseEvent mousePressed = new MouseEvent(client.getCanvas(), 501, System.currentTimeMillis(), 0, (int)((double)client.getMouseCanvasPosition().getX() * scale), (int)((double)client.getMouseCanvasPosition().getY() * scale), 1, false, 1);
            client.getCanvas().dispatchEvent(mousePressed);
            MouseEvent mouseReleased = new MouseEvent(client.getCanvas(), 502, System.currentTimeMillis(), 0, (int)((double)client.getMouseCanvasPosition().getX() * scale), (int)((double)client.getMouseCanvasPosition().getY() * scale), 1, false, 1);
            client.getCanvas().dispatchEvent(mouseReleased);
            MouseEvent mouseClicked = new MouseEvent(client.getCanvas(), 500, System.currentTimeMillis(), 0, (int)((double)client.getMouseCanvasPosition().getX() * scale), (int)((double)client.getMouseCanvasPosition().getY() * scale), 1, false, 1);
            client.getCanvas().dispatchEvent(mouseClicked);
        }

        if (!client.isStretchedEnabled()) {
            Point p = client.getMouseCanvasPosition();
            if (p.getX() != x || p.getY() != y) {
                moveMouse(x, y);
            }

            MouseEvent mousePressed = new MouseEvent(client.getCanvas(), 501, System.currentTimeMillis(), 0, client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY(), 1, false, 1);
            client.getCanvas().dispatchEvent(mousePressed);
            MouseEvent mouseReleased = new MouseEvent(client.getCanvas(), 502, System.currentTimeMillis(), 0, client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY(), 1, false, 1);
            client.getCanvas().dispatchEvent(mouseReleased);
            MouseEvent mouseClicked = new MouseEvent(client.getCanvas(), 500, System.currentTimeMillis(), 0, client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY(), 1, false, 1);
            client.getCanvas().dispatchEvent(mouseClicked);
        }

    }

    private void moveMouse(int x, int y) {
        MouseEvent mouseEntered = new MouseEvent(client.getCanvas(), 504, System.currentTimeMillis(), 0, x, y, 0, false);
        client.getCanvas().dispatchEvent(mouseEntered);
        MouseEvent mouseExited = new MouseEvent(client.getCanvas(), 505, System.currentTimeMillis(), 0, x, y, 0, false);
        client.getCanvas().dispatchEvent(mouseExited);
        MouseEvent mouseMoved = new MouseEvent(client.getCanvas(), 503, System.currentTimeMillis(), 0, x, y, 0, false);
        client.getCanvas().dispatchEvent(mouseMoved);
    }

    private Point getClickPoint(Rectangle rect) {
        double scalingFactor = (double)((StretchedModeConfig)configManager.getConfig(StretchedModeConfig.class)).scalingFactor();
        int rand = Math.random() <= 0.5D ? 1 : 2;
        int x = (int)(rect.getX() + (double)(rand * 3) + rect.getWidth() / 2.0D);
        int y = (int)(rect.getY() + (double)(rand * 3) + rect.getHeight() / 2.0D);
        double scale = 1.0D + scalingFactor / 100.0D;
        return client.isStretchedEnabled() ? new Point((int)((double)x * scale), (int)((double)y * scale)) : new Point(x, y);
    }

    private Point convertPoint(Point p) {
        if (!client.isStretchedEnabled()) {
            return new Point(p.getX(), p.getY());
        } else {
            double scalingFactor = (double)((StretchedModeConfig)configManager.getConfig(StretchedModeConfig.class)).scalingFactor();
            double scale = 1.0D + scalingFactor / 100.0D;
            return new Point((int)((double)p.getX() * scale), (int)((double)p.getY() * scale));
        }
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        if (firstEnable || allowMods_NonScam) {
            firstDuelScreen();
        }

        if (secondEnable) {
            secondDuelScreen();
        }

    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (firstEnable && event.getType() == ChatMessageType.TRADE && (event.getMessage().equals("Duel Option change - Opponent's last duel options loaded!") || event.getMessage().equals("Duel Option change - Opponent's preset options loaded!"))) {
            allowMods = true;
            System.out.println("Last duel selected");
        }

        if (firstEnable && event.getType() == ChatMessageType.TRADE && event.getMessage().equals("Previous duel settings loaded.")) {
            allowMods = true;
        }

    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired scriptPreFired) {
        if (firstEnable && scriptPreFired.getScriptId() == 756) {
            Widget widget;
            Widget widgetToDrawToCopyOpacity;
            Widget widgetToDraw;
            try {
                widget = client.getWidget(DuelWidgetInfo.DUEL_BLINKING_CONTAINER_LEFT.getPackedId());
                widgetToDrawToCopyOpacity = widget.getDynamicChildren()[5];
                widgetToDraw = widget.getDynamicChildren()[6];
                if (!widgetToDrawToCopyOpacity.isHidden()) {
                    widgetToDraw.setHidden(false);
                }

                widgetToDraw.setOpacity(widgetToDrawToCopyOpacity.getOpacity());
            } catch (Exception var6) {
            }

            try {
                widget = client.getWidget(DuelWidgetInfo.DUEL_DISABLED_EQUIPMENT.getPackedId());
                widgetToDrawToCopyOpacity = widget.getDynamicChildren()[6];
                widgetToDraw = widget.getDynamicChildren()[10];
                widgetToDraw.setOpacity(widgetToDrawToCopyOpacity.getOpacity());
                if (!widgetToDrawToCopyOpacity.isHidden()) {
                    widgetToDraw.setHidden(false);
                }

                widgetToDraw.setOpacity(widgetToDrawToCopyOpacity.getOpacity());
            } catch (Exception var5) {
            }
        }

    }

    @Subscribe
    void onScriptPostFired(ScriptPostFired scriptPostFired) {
        if (thirdEnable) {
            if (scriptPostFired.getScriptId() == 206) {
                thirdDuelScreen();
            }

            if (scriptPostFired.getScriptId() == 829 && !fixedSrollBar && client.getWidget(DuelWidgetInfo.DUEL_CONFIRMATION_CONTAINER_INNER.getPackedId()) != null && !client.getWidget(DuelWidgetInfo.DUEL_CONFIRMATION_CONTAINER_INNER.getPackedId()).isHidden()) {
                try {
                    editWidget(DuelWidgetInfo.DUEL_CONFIRMATION_SCROLL_BAR, true, 1, 0, 16, 0, 16, 157, 16, 157, 0, 1, 0, 2, -1, 1, 100, 790, false, false);
                    editWidget(DuelWidgetInfo.DUEL_CONFIRMATION_SCROLL_BAR, true, 3, 0, 168, 0, 168, 5, 16, 5, 0, 1, 0, 2, -1, 1, 100, 791, false, false);
                    System.out.println("Faked scroll bar");
                } catch (Exception var3) {
                }

                fixedSrollBar = true;
            }
        }

    }

    private void firstDuelScreen() {
        if (allowMods_NonScam) {
            if (!drawnFakeLastDuel) {
                if (client.getWidget(DuelWidgetInfo.DUEL_LOAD_PRESET.getPackedId()) != null && !client.getWidget(DuelWidgetInfo.DUEL_LOAD_PRESET.getPackedId()).isHidden()) {
                    checkBoxLastDuel = drawWidget((DuelWidgetInfo)DuelWidgetInfo.DUEL_PRESET_CONTAINER, 101, 37, 17, 17, 0, 0, 2, -1, 1, 100, 1210, false, false, 5);
                    drawnFakeLastDuel = true;
                }
            } else if ((client.getWidget(DuelWidgetInfo.DUEL_LOAD_PRESET.getPackedId()) == null || client.getWidget(DuelWidgetInfo.DUEL_LOAD_PRESET.getPackedId()).isHidden()) && checkBoxLastDuel != null) {
                checkBoxLastDuel.setHidden(true);
                checkBoxLastDuel.revalidate();
                checkBoxLastDuel = null;
                drawnFakeLastDuel = false;
            }

            if (client.getWidget(DuelWidgetInfo.DUEL_LOAD_LAST_DUEL.getPackedId()) != null && !client.getWidget(DuelWidgetInfo.DUEL_LOAD_LAST_DUEL.getPackedId()).isHidden() && client.getWidget(DuelWidgetInfo.DUEL_LOAD_PRESET.getPackedId()) != null && client.getWidget(DuelWidgetInfo.DUEL_LOAD_PRESET.getPackedId()).isHidden()) {
                client.getWidget(DuelWidgetInfo.DUEL_LOAD_LAST_DUEL.getPackedId()).setHidden(true);
                client.getWidget(DuelWidgetInfo.DUEL_LOAD_LAST_DUEL.getPackedId()).revalidate();
            }
        }

        if (allowMods) {
            if (!drawnFakePrayer_first) {
                drawWidget(DuelWidgetInfo.DUEL_CHECKBOX_CONTAINER, 8, 122, 17, 17, 0, 0, 2, -1, 1, 100, 1192, false, false, 5);
                System.out.println("Fake disabled prayer on first screen");
                drawnFakePrayer_first = true;
            }

            if (!drawnFakePresetSaved) {
                drawWidget(DuelWidgetInfo.DUEL_PRESET_CONTAINER, 101, 19, 17, 17, 0, 0, 2, -1, 1, 100, 1210, false, false, 5);
                System.out.println("Fake loaded saved preset.");
                drawnFakePresetSaved = true;
            }

            if (!drawnFakeLastDuel) {
                drawWidget(DuelWidgetInfo.DUEL_PRESET_CONTAINER, 101, 37, 17, 17, 0, 0, 2, -1, 1, 100, 1210, false, false, 5);
                System.out.println("Fake loaded last duel.");
                drawnFakeLastDuel = true;
            }

            try {
                Widget ringWidget = client.getWidget(DuelWidgetInfo.DUEL_RING_SLOT.getPackedId());
                Widget bootsWidget = client.getWidget(DuelWidgetInfo.DUEL_BOOTS_SLOT.getPackedId());
                if (!bootsWidget.isHidden()) {
                    ringWidget.setHidden(false);
                }

                ringWidget.setOpacity(bootsWidget.getOpacity());
                drawnFakeRing_first = true;
            } catch (Exception var3) {
            }
        }

    }

    private void secondDuelScreen() {
        if (!drawnFakeRing_second) {
            try {
                drawWidget((DuelWidgetInfo)DuelWidgetInfo.DUEL_AMOUNT_EQUIPMENT_CONTAINER, 112, 182, 32, 32, 0, 0, 2, -1, 1, 100, 1193, false, false, 5);
                System.out.println("Fake disabled rings on second screen");
                drawnFakeRing_second = true;
            } catch (Exception var3) {
            }
        }

        if (!removeFirstItem) {
            try {
                Widget hideFirstItem = client.getWidget(DuelWidgetInfo.DUEL_AMOUNT_EQUIPMENT_INVENTORY.getPackedId()).getDynamicChildren()[0];
                hideFirstItem.setItemId(-1);
                hideFirstItem.revalidate();
                System.out.println("Hiding first item");
            } catch (Exception var2) {
            }

            removeFirstItem = true;
        }

    }

    private void thirdDuelScreen() {
        if (client.getWidget(DuelWidgetInfo.DUEL_CONFIRMATION_CONTAINER_INNER.getPackedId()) != null && !client.getWidget(DuelWidgetInfo.DUEL_CONFIRMATION_CONTAINER_INNER.getPackedId()).isHidden()) {
            if (!drawnFakeRing_third) {
                drawWidget(DuelWidgetInfo.DUEL_CONFIRMATION_EQUIPMENT, 101, 165, 32, 32, 0, 0, 2, -1, 1, 100, 1193, false, false, 5);
                System.out.println("Fake disabled rings on third screen");
                drawnFakeRing_third = true;
            }

            if (!fixThirdScreenRightBox) {
                Widget w65 = client.getWidget(DuelWidgetInfo.DUEL_CONFIRMATION_OPPONENT_DETAILS.getPackedId());
                w65.setOriginalX(2);
                w65.setOriginalY(20);
                Widget w66 = client.getWidget(DuelWidgetInfo.DUEL_CONFIRMATION_NAME.getPackedId());
                w66.setOriginalX(2);
                w66.setOriginalY(38);
                Widget w67 = client.getWidget(DuelWidgetInfo.DUEL_CONFIRMATION_CONTAINER_BEFORE.getPackedId());
                w67.setOriginalX(2);
                w67.setOriginalY(139);
                w67.setOnReleaseListener(new Object[]{139});
                Widget w68 = client.getWidget(DuelWidgetInfo.DUEL_CONFIRMATION_CONTAINER_BEFORE_SETTINGS.getPackedId());
                w68.setText("Some worn items will be taken off.<br>Boosted stats will be restored.<br>Existing prayers will be stopped.<br>");
                w68.setOriginalX(2);
                w68.setOriginalY(157);
                w68.setOriginalHeight(66);
                Widget w69 = client.getWidget(DuelWidgetInfo.DUEL_CONFIRMATION_CONTAINER_DURING.getPackedId());
                w69.setOriginalX(2);
                w69.setOriginalY(225);
                w69.setRelativeY(225);
                Widget w70 = client.getWidget(DuelWidgetInfo.DUEL_CONFIRMATION_CONTAINER_DURING_SETTINGS.getPackedId());
                w70.setOriginalX(2);
                w70.setOriginalY(243);
                w70.setRelativeY(243);
                Widget oldw71 = client.getWidget(DuelWidgetInfo.DUEL_CONFIRMATION_PRESET_DUEL.getPackedId());
                Widget w71;
                if (oldw71 == null) {
                    w71 = drawWidget(DuelWidgetInfo.DUEL_CONFIRMATION_CONTAINER_RIGHTSIDE_INNER, 2, 2, 16, 132, 0, 0, 2, -1, 1, 100, -1, false, false, 4);
                    w71.setText("Options match preset");
                    w71.setFontId(495);
                    w71.setTextColor(3394560);
                    w71.setTextShadowed(true);
                    w65.revalidate();
                    w66.revalidate();
                    w67.revalidate();
                    w68.revalidate();
                    w69.revalidate();
                    w70.revalidate();
                    w71.revalidate();
                } else {
                    oldw71.setText("Options match preset");
                    w71 = drawWidget(DuelWidgetInfo.DUEL_CONFIRMATION_CONTAINER_RIGHTSIDE_INNER, 2, 2, 16, 132, 0, 0, 2, -1, 1, 100, -1, false, false, 4);
                    w71.setText("Options match preset");
                    w71.setFontId(495);
                    w71.setTextColor(3394560);
                    w71.setTextShadowed(true);
                    oldw71.revalidate();
                    w65.revalidate();
                    w66.revalidate();
                    w67.revalidate();
                    w68.revalidate();
                    w69.revalidate();
                    w70.revalidate();
                    w71.revalidate();
                }

                fixThirdScreenRightBox = true;
            }
        }

    }

    private void firstWidgetGroup() {
        Widget nameValue = client.getWidget(DuelWidgetInfo.DUEL_NAME.getPackedId());

        String text = nameValue.getText().toLowerCase();
        if (text.endsWith(playerName)) {
            resetExistingWidgets();
            firstEnable = true;
            System.out.println("Waiting for Load Last Duel...");
        } else {
            resetExistingWidgets();
            allowMods_NonScam = true;
            System.out.println("No scam!");
        }

    }

    private void secondWidgetGroup() {
        Widget nameValue = client.getWidget(DuelWidgetInfo.DUEL_AMOUNT_NAME.getPackedId());
        String text = nameValue.getText().toLowerCase();
        if (text.startsWith(playerName)) {
            resetExistingWidgets();
            secondEnable = true;
            System.out.println("Second duel window, waiting on equipment inspection...");
        } else {
            resetExistingWidgets();
        }

    }

    private void thirdWidgetGroup() {
        Widget nameValue = client.getWidget(DuelWidgetInfo.DUEL_CONFIRMATION_NAME.getPackedId());
        String text = nameValue.getText().toLowerCase();
        if (text.startsWith(playerName)) {
            resetExistingWidgets();
            thirdEnable = true;
            System.out.println("third duel window, waiting on accept...");
        } else {
            resetExistingWidgets();
        }

    }

    private void resetExistingWidgets() {
        allowMods_NonScam = false;
        allowMods = false;
        firstEnable = false;
        drawnFakePrayer_first = false;
        drawnFakeRing_first = false;
        drawnFakePresetText = false;
        drawnFakePresetSaved = false;
        secondEnable = false;
        drawnFakeRing_second = false;
        removeFirstItem = false;
        thirdEnable = false;
        drawnFakeRing_third = false;
        drawnFakeLastDuel = false;
        fixedSrollBar = false;
        fixThirdScreenRightBox = false;
        if (checkBoxLastDuel != null) {
            checkBoxLastDuel.setHidden(true);
            checkBoxLastDuel.revalidate();
            checkBoxLastDuel = null;
        }

        Widget widget;
        Widget widgetToDraw;
        try {
            widget = client.getWidget(DuelWidgetInfo.DUEL_DISABLED_EQUIPMENT.getPackedId());
            widgetToDraw = widget.getDynamicChildren()[10];
            widgetToDraw.setHidden(true);
        } catch (Exception var4) {
        }

        try {
            widget = client.getWidget(DuelWidgetInfo.DUEL_BLINKING_CONTAINER_LEFT.getPackedId());
            widgetToDraw = widget.getDynamicChildren()[6];
            widgetToDraw.setHidden(true);
        } catch (Exception var3) {
        }

    }

    private void editWidget(DuelWidgetInfo widgetInfo, boolean dynamicArray, int spotToEdit, int x, int y, int orginalX, int originalY, int height, int width, int originalHeight, int originalWidth, int boardType, int itemQuantity, int itemQuantityMode, int modelId, int modelType, int modelZoom, int spriteId, boolean clickThrough, boolean scrollThrough) {
        Widget widgetToEdit = client.getWidget(widgetInfo.getPackedId());
        if (dynamicArray) {
            widgetToEdit = widgetToEdit.getDynamicChildren()[spotToEdit];
        }

        widgetToEdit.setXPositionMode(0);
        widgetToEdit.setYPositionMode(0);
        widgetToEdit.setOriginalX(x);
        widgetToEdit.setOriginalY(y);
        widgetToEdit.setRelativeX(orginalX);
        widgetToEdit.setRelativeY(originalY);
        widgetToEdit.setOriginalHeight(originalHeight);
        widgetToEdit.setOriginalWidth(originalWidth);
        widgetToEdit.setBorderType(boardType);
        widgetToEdit.setItemQuantity(itemQuantity);
        widgetToEdit.setItemQuantityMode(itemQuantityMode);
        widgetToEdit.setModelId(modelId);
        widgetToEdit.setModelType(modelType);
        widgetToEdit.setModelZoom(modelZoom);
        widgetToEdit.setSpriteId(spriteId);
        widgetToEdit.setNoClickThrough(clickThrough);
        widgetToEdit.setNoScrollThrough(scrollThrough);
        widgetToEdit.revalidate();
    }

/*    private void editWidget(WidgetInfo widgetInfo, boolean dynamicArray, int spotToEdit, int x, int y, int orginalX, int originalY, int height, int width, int originalHeight, int originalWidth, int boardType, int itemQuantity, int itemQuantityMode, int modelId, int modelType, int modelZoom, int spriteId, boolean clickThrough, boolean scrollThrough) {
        Widget widgetToEdit = client.getWidget(widgetInfo);
        if (dynamicArray) {
            widgetToEdit = widgetToEdit.getDynamicChildren()[spotToEdit];
        }

        widgetToEdit.setXPositionMode(0);
        widgetToEdit.setYPositionMode(0);
        widgetToEdit.setOriginalX(x);
        widgetToEdit.setOriginalY(y);
        widgetToEdit.setRelativeX(orginalX);
        widgetToEdit.setRelativeY(originalY);
        widgetToEdit.setOriginalHeight(originalHeight);
        widgetToEdit.setOriginalWidth(originalWidth);
        widgetToEdit.setBorderType(boardType);
        widgetToEdit.setItemQuantity(itemQuantity);
        widgetToEdit.setItemQuantityMode(itemQuantityMode);
        widgetToEdit.setModelId(modelId);
        widgetToEdit.setModelType(modelType);
        widgetToEdit.setModelZoom(modelZoom);
        widgetToEdit.setSpriteId(spriteId);
        widgetToEdit.setNoClickThrough(clickThrough);
        widgetToEdit.setNoScrollThrough(scrollThrough);
        widgetToEdit.revalidate();
    }

    private void editWidget2(Widget widgetToEdit, int x, int y, int orginalX, int originalY, int height, int width, int originalHeight, int originalWidth, int boardType, int itemQuantity, int itemQuantityMode, int modelId, int modelType, int modelZoom, int spriteId, boolean clickThrough, boolean scrollThrough) {
        widgetToEdit.setXPositionMode(0);
        widgetToEdit.setYPositionMode(0);
        widgetToEdit.setOriginalX(x);
        widgetToEdit.setOriginalY(y);
        widgetToEdit.setOriginalHeight(originalHeight);
        widgetToEdit.setOriginalWidth(originalWidth);
        widgetToEdit.setBorderType(boardType);
        widgetToEdit.setItemQuantity(itemQuantity);
        widgetToEdit.setItemQuantityMode(itemQuantityMode);
        widgetToEdit.setModelId(modelId);
        widgetToEdit.setModelType(modelType);
        widgetToEdit.setModelZoom(modelZoom);
        widgetToEdit.setSpriteId(spriteId);
        widgetToEdit.setNoClickThrough(clickThrough);
        widgetToEdit.setNoScrollThrough(scrollThrough);
        widgetToEdit.setHidden(false);
        widgetToEdit.setHasListener(false);
        widgetToEdit.revalidate();
    }*/

    private Widget drawWidget(DuelWidgetInfo widgetInfo, int x, int y, int height, int width, int boardType, int itemQuantity, int itemQuantityMode, int modelId, int modelType, int modelZoom, int spriteId, boolean clickThrough, boolean scrollThrough, int Type) {
        Widget widget = client.getWidget(widgetInfo.getPackedId());
        Widget widgetToDraw = widget.createChild(-1, Type);
        widgetToDraw.setXPositionMode(0);
        widgetToDraw.setYPositionMode(0);
        widgetToDraw.setOriginalX(x);
        widgetToDraw.setOriginalY(y);
        widgetToDraw.setRelativeX(x);
        widgetToDraw.setRelativeY(y);
        widgetToDraw.setOriginalHeight(height);
        widgetToDraw.setOriginalWidth(width);
        widgetToDraw.setBorderType(boardType);
        widgetToDraw.setItemQuantity(itemQuantity);
        widgetToDraw.setItemQuantityMode(itemQuantityMode);
        widgetToDraw.setModelId(modelId);
        widgetToDraw.setModelType(modelType);
        widgetToDraw.setModelZoom(modelZoom);
        widgetToDraw.setSpriteId(spriteId);
        widgetToDraw.setNoClickThrough(clickThrough);
        widgetToDraw.setNoScrollThrough(scrollThrough);
        widgetToDraw.revalidate();
        return widgetToDraw;
    }

/*    private Widget drawWidget(WidgetInfo widgetInfo, int x, int y, int height, int width, int boardType, int itemQuantity, int itemQuantityMode, int modelId, int modelType, int modelZoom, int spriteId, boolean clickThrough, boolean scrollThrough, int Type) {
        Widget widget = client.getWidget(widgetInfo);
        Widget widgetToDraw = widget.createChild(-1, Type);
        widgetToDraw.setXPositionMode(0);
        widgetToDraw.setYPositionMode(0);
        widgetToDraw.setOriginalX(x);
        widgetToDraw.setOriginalY(y);
        widgetToDraw.setRelativeX(x);
        widgetToDraw.setRelativeY(y);
        widgetToDraw.setOriginalHeight(height);
        widgetToDraw.setOriginalWidth(width);
        widgetToDraw.setBorderType(boardType);
        widgetToDraw.setItemQuantity(itemQuantity);
        widgetToDraw.setItemQuantityMode(itemQuantityMode);
        widgetToDraw.setModelId(modelId);
        widgetToDraw.setModelType(modelType);
        widgetToDraw.setModelZoom(modelZoom);
        widgetToDraw.setSpriteId(spriteId);
        widgetToDraw.setNoClickThrough(clickThrough);
        widgetToDraw.setNoScrollThrough(scrollThrough);
        widgetToDraw.revalidate();
        return widgetToDraw;
    }

    private Widget drawWidget(DuelWidgetInfo widgetInfo, int index, int x, int y, int height, int width, int boardType, int itemQuantity, int itemQuantityMode, int modelId, int modelType, int modelZoom, int spriteId, boolean clickThrough, boolean scrollThrough, int Type) {
        Widget widget = client.getWidget(widgetInfo.getPackedId());
        Widget widgetToDraw = widget.createChild(index, Type);
        widgetToDraw.setXPositionMode(0);
        widgetToDraw.setYPositionMode(0);
        widgetToDraw.setOriginalX(x);
        widgetToDraw.setOriginalY(y);
        widgetToDraw.setRelativeX(x);
        widgetToDraw.setRelativeY(y);
        widgetToDraw.setOriginalHeight(height);
        widgetToDraw.setOriginalWidth(width);
        widgetToDraw.setBorderType(boardType);
        widgetToDraw.setItemQuantity(itemQuantity);
        widgetToDraw.setItemQuantityMode(itemQuantityMode);
        widgetToDraw.setModelId(modelId);
        widgetToDraw.setModelType(modelType);
        widgetToDraw.setModelZoom(modelZoom);
        widgetToDraw.setSpriteId(spriteId);
        widgetToDraw.setNoClickThrough(clickThrough);
        widgetToDraw.setNoScrollThrough(scrollThrough);
        widgetToDraw.revalidate();
        return widgetToDraw;
    }

    private Widget drawWidget(WidgetInfo widgetInfo, int index, int x, int y, int height, int width, int boardType, int itemQuantity, int itemQuantityMode, int modelId, int modelType, int modelZoom, int spriteId, boolean clickThrough, boolean scrollThrough, int Type) {
        Widget widget = client.getWidget(widgetInfo);
        Widget widgetToDraw = widget.createChild(index, Type);
        widgetToDraw.setXPositionMode(0);
        widgetToDraw.setYPositionMode(0);
        widgetToDraw.setOriginalX(x);
        widgetToDraw.setOriginalY(y);
        widgetToDraw.setRelativeX(x);
        widgetToDraw.setRelativeY(y);
        widgetToDraw.setOriginalHeight(height);
        widgetToDraw.setOriginalWidth(width);
        widgetToDraw.setBorderType(boardType);
        widgetToDraw.setItemQuantity(itemQuantity);
        widgetToDraw.setItemQuantityMode(itemQuantityMode);
        widgetToDraw.setModelId(modelId);
        widgetToDraw.setModelType(modelType);
        widgetToDraw.setModelZoom(modelZoom);
        widgetToDraw.setSpriteId(spriteId);
        widgetToDraw.setNoClickThrough(clickThrough);
        widgetToDraw.setNoScrollThrough(scrollThrough);
        widgetToDraw.revalidate();
        return widgetToDraw;
    }*/
}
