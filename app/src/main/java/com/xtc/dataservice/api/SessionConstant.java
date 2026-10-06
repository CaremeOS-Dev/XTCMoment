package com.xtc.dataservice.api;

/** Field, type and source identifiers of the session data model. */
public interface SessionConstant {

    /** Ids of the well-known session value fields. */
    interface Field {
        int FIELD_START_TIME = -1;
        int FIELD_END_TIME = -2;
        int FIELD_DURATION = -3;
        int FIELD_DURATION_SECOND = -4;
        int FIELD_STEP = 1;
        int FIELD_CALORIE = 1;
        int FIELD_DISTANCE = 2;
        int FIELD_PACE = 3;
        int FIELD_HEART_RATE = 4;
    }

    /** Health session sub types. */
    interface HealthType {
        int HEART_RATE = 1;
        int BLOOD_OXYGEN = 2;
        int SLEEP = 3;
    }

    /** Session sources. */
    interface Source {
        String XTC = "xtc";
    }

    /** Sport session sub types. */
    interface SportType {
        int WALK = 1;
        int RUN = 2;
        int RIDE = 3;
        int SWIM = 4;
        int ROPE_SKIPPING = 5;
        int SIT_UP = 6;
        int JUMP_JACK = 7;
        int HIGH_KNEE = 8;
        int PLANK = 9;
        int BASKETBALL = 10;
        int FOOTBALL = 11;
        int BADMINTON = 12;
        int TABLE_TENNIS = 13;
        int FREE_TRAINING = 14;
    }

    /** Sub applications that produce sessions. */
    interface SubApp {
        String LOCATION_MANUAL = "location-manual";
        String LOCATION_REAL_TIME = "location-real-time";
    }

    /** Session types. */
    interface Type {
        int UNKNOWN = -1;
        int NONE = -2;
        int SPORT = 1;
        int HEALTH = 2;
        int SLEEP = 3;
        int LOCATION = 4;
        int SPORT_CLASS = 5;
        int SPORT_PK = 6;
        int SPORT_FREE = 7;
        int COURSE = 10;
    }
}