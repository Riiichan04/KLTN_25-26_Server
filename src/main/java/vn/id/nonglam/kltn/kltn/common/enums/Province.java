package vn.id.nonglam.kltn.kltn.common.enums;

import lombok.Getter;

@Getter
public enum Province {
    HO_CHI_MINH("Hồ Chí Minh"),
    HA_NOI("Hà Nội"),
    GIA_LAI("Binh Dinh"),
    LAM_DONG("Lâm Đồng");

    private String value;
    Province(String value) {}
}
