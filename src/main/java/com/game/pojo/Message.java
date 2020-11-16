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
        private Double LocationX;
        @SerializedName("LocationY")
        private Double LocationY;
        @SerializedName("Size")
        private Double Size;
        @SerializedName("ID")
        private Integer ID;

        public Double getLocationX() {
            return LocationX;
        }

        public Double getLocationY() {
            return LocationY;
        }

        public Double getSize() {
            return Size;
        }

        public Integer getID() {
            return ID;
        }
    }
}
