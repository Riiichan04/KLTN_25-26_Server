package vn.id.nonglam.kltn.kltn.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.id.nonglam.kltn.kltn.common.enums.Gender;
import vn.id.nonglam.kltn.kltn.common.enums.UserRole;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthDto {
    private UUID id;
    private String email;
    private String username;
    private String displayName;
    private Gender gender;
    private String avatar;
    private String phone;
    private String description;
    private boolean active;
    private boolean verified;
    private UserRole role;
    private String jwtToken;
    private List<String> permissions;

}
