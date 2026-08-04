package vn.id.nonglam.kltn.kltn.dto.request.auth;

import vn.id.nonglam.kltn.kltn.common.enums.Gender;
import vn.id.nonglam.kltn.kltn.common.enums.UserType;

public record RegisterOwnerRequest(
        String username,
        String password,
        String email,
        String displayName,
        String phone,
        Gender gender,
        UserType userType,

        // Payment detail info
        String bankAccount,
        String bankCode,
        String bankOwnerName

) {
}
