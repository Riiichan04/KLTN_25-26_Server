package vn.id.nonglam.kltn.kltn.dto.request.auth;

import vn.id.nonglam.kltn.kltn.common.enums.Gender;

public record UpdateRequest(String avatarUrl, String displayName, String phone, Gender gender) { }
