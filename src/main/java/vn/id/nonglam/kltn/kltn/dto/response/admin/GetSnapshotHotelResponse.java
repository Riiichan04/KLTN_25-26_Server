package vn.id.nonglam.kltn.kltn.dto.response.admin;

import java.util.UUID;

public interface GetSnapshotHotelResponse {
    UUID getId();
    String getName();
    String getThumbnail();
    String getStreet();
    String getWard();
    String getProvince();
    int getViewCount();
    boolean getIsActive();
}