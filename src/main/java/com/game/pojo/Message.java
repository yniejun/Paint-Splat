package com.game.pojo;

import com.google.gson.annotations.SerializedName;

public class Message {

    /**
     * eventType : Hit
     * detail : {"LocationX":1,"LocationY":2,"Size":30,"ID":1234}
     */

    @SerializedName("eventType")
    private String eventType;
    @SerializedName("detail")
    private DetailDTO detail;

    public String getEventType() {
        return eventType;
    }

    public DetailDTO getDetail() {
        return detail;
    }

    public static class DetailDTO {
        /**
         * LocationX : 1
         * LocationY : 2
         * Size : 30
         * ID : 1234
         */

        @SerializedName("LocationX")
        private Integer LocationX;
        @SerializedName("LocationY")
        private Integer LocationY;
        @SerializedName("Size")
        private Integer Size;
        @SerializedName("ID")
        private Integer ID;

        public Integer getLocationX() {
            return LocationX;
        }

        public Integer getLocationY() {
            return LocationY;
        }

        public Integer getSize() {
            return Size;
        }

        public Integer getID() {
            return ID;
        }
    }
}
