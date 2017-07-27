package com.example.android.habitus.data;

import android.provider.BaseColumns;

/**
 * Created by Martin on 8.7.2017 г..
 */

public final class HabitusContract {

    private HabitusContract() {
    }

    // Inner class for this table , which is called "habits"
    public static class HabitusEntry implements BaseColumns {

        public static final String TABLE_NAME = "habits";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_HABIT_NAME = "name";
        public static final String COLUMN_HABIT_TYPE = "type";
        public static final String COLUMN_HABIT_RANK = "rank";
        public static final String COLUMN_HABIT_MEASUREMENT = "measurement";

        // Possible values for the rank of the habit
        public static final int RANK_UNKNOWN = 0;
        public static final int RANK_GOOD = 1;
        public static final int RANK_BAD = 2;
    }
}