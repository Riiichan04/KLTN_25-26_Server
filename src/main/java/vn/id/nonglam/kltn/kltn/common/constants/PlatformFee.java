package vn.id.nonglam.kltn.kltn.common.constants;

import vn.id.nonglam.kltn.kltn.common.enums.UserType;

public class PlatformFee {
    public static final double DEFAULT_PLATFORM_FEE = 0.15;
    public static final double PREMIUM_PLATFORM_FEE = 0.2;
    public static final double ADVERTISE_PLATFORM_FEE = 0.25;

    public static double getPlatformFee(UserType userType) {
        return switch (userType) {
            case PREMIUM -> PREMIUM_PLATFORM_FEE;
            case ADVERTISEMENT -> ADVERTISE_PLATFORM_FEE;
            default -> DEFAULT_PLATFORM_FEE;
        };
    }
}
