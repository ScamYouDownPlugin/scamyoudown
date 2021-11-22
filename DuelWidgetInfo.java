package net.runelite.client.plugins.ScamYouDownsyndrome;

public enum DuelWidgetInfo {
    DUEL_CONTAINER(DuelWidgets.DUEL_GROUP_ID, 2),
    DUEL_AMOUNT_CONTAINER(DuelWidgets.DUEL_GROUP_ID, 4),
    DUEL_MIDDLEGROUP(DuelWidgets.DUEL_GROUP_ID, 5),
    DUEL_NAME(DuelWidgets.DUEL_GROUP_ID, 34),
    DUEL_LOAD_PRESET(DuelWidgets.DUEL_GROUP_ID, 114),
    DUEL_LOAD_LAST_DUEL(DuelWidgets.DUEL_GROUP_ID, 115),
    DUEL_RING_SLOT(DuelWidgets.DUEL_GROUP_ID, 101),
    DUEL_BOOTS_SLOT(DuelWidgets.DUEL_GROUP_ID, 100),
    DUEL_CHECKBOX_CONTAINER(DuelWidgets.DUEL_GROUP_ID, 36),
    DUEL_CHECKBOX_RANGED(DuelWidgets.DUEL_GROUP_ID, 54),
    DUEL_CHECKBOX_PRAYER(DuelWidgets.DUEL_GROUP_ID, 59),
    DUEL_CHECKBOX_MAGIC(DuelWidgets.DUEL_GROUP_ID, 56),
    DUEL_CHECKBOX_FOOD(DuelWidgets.DUEL_GROUP_ID, 58),
    DUEL_CHECKBOX_SPECIALATTACK(DuelWidgets.DUEL_GROUP_ID, 62),
    DUEL_DISABLED_EQUIPMENT(DuelWidgets.DUEL_GROUP_ID, 91),
    DUEL_PRESET_CONTAINER(DuelWidgets.DUEL_GROUP_ID, 107),
    DUEL_BLINKING_CONTAINER_LEFT(DuelWidgets.DUEL_GROUP_ID, 116),
    DUEL_AMOUNT_CONTAINER_INNER(DuelWidgets.DUEL_OPTIONS_2ND, 4),
    DUEL_AMOUNT_EQUIPMENT_CONTAINER(DuelWidgets.DUEL_OPTIONS_2ND, 35),
    DUEL_AMOUNT_EQUIPMENT_CONTAINER_SPRITES(DuelWidgets.DUEL_OPTIONS_2ND, 34),
    DUEL_AMOUNT_EQUIPMENT_INVENTORY(DuelWidgets.DUEL_OPTIONS_2ND, 31),
    DUEL_AMOUNT_NAME(DuelWidgets.DUEL_OPTIONS_2ND, 24),
    DUEL_CONFIRMATION_CONTAINER_INNER(DuelWidgets.DUEL_OPTIONS_3RD, 2),
    DUEL_CONFIRMATION_CONTAINER_RIGHTSIDE(DuelWidgets.DUEL_OPTIONS_3RD, 62),
    DUEL_CONFIRMATION_CONTAINER_RIGHTSIDE_INNER(DuelWidgets.DUEL_OPTIONS_3RD, 63),
    DUEL_CONFIRMATION_NAME(DuelWidgets.DUEL_OPTIONS_3RD, 66),
    DUEL_CONFIRMATION_EQUIPMENT(DuelWidgets.DUEL_OPTIONS_3RD, 39),
    DUEL_CONFIRMATION_PRESET_DUEL(DuelWidgets.DUEL_OPTIONS_3RD, 71),
    DUEL_CONFIRMATION_SCROLL_BAR(DuelWidgets.DUEL_OPTIONS_3RD, 64),
    DUEL_CONFIRMATION_CONTAINER_BEFORE(DuelWidgets.DUEL_OPTIONS_3RD, 67),
    DUEL_CONFIRMATION_OPPONENT_DETAILS(DuelWidgets.DUEL_OPTIONS_3RD, 65),
    DUEL_CONFIRMATION_CONTAINER_BEFORE_SETTINGS(DuelWidgets.DUEL_OPTIONS_3RD, 68),
    DUEL_CONFIRMATION_CONTAINER_DURING(DuelWidgets.DUEL_OPTIONS_3RD, 69),
    DUEL_CONFIRMATION_CONTAINER_DURING_SETTINGS(DuelWidgets.DUEL_OPTIONS_3RD, 70);

    private final int groupId;
    private final int childId;

    DuelWidgetInfo(final int groupId, final int childId) {
        this.groupId = groupId;
        this.childId = childId;
    }

    public int getId() {
        return childId;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getChildId() {
        return this.childId;
    }

    public int getPackedId() {
        return this.groupId << 16 | this.childId;
    }

    public static int TO_GROUP(int id) {
        return id >>> 16;
    }

    public static int TO_CHILD(int id) {
        return id & '\uffff';
    }

    public static int PACK(int groupId, int childId) {
        return groupId << 16 | childId;
    }
}
