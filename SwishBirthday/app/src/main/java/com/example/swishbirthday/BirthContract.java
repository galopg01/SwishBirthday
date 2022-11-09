package com.example.swishbirthday;

import android.provider.BaseColumns;

public class BirthContract{
    private BirthContract() {
    }

    public static class BirthEntry implements BaseColumns {
        public static final String TABLE_NAME = "Calendario";
        public static final String COLUMN_NAME_NOMBRE = "nombre";
        public static final String COLUMN_NAME_FECHA = "fecha";
        public static final String COLUMN_NAME_HORA = "hora";
    }
}
