package io.fuwafuwa.banjo.profile;

public enum BarJustify {
    DEFAULT,//CAN_JUSTIFIED
    //region After set the range
    FIXED,//like READONLY, can select
    FIXED_LEFT,
    FIXED_RIGHT,
    /**
     * not implement yet
     */
    FIXED_CAN_BOTH_MOVE,// fix range, set move handle
    FIXED_CAN_LEFT_MOVE,
    FIXED_CAN_RIGHT_MOVE,
    READONLY,
    // endregion
}