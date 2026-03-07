package com.stedfast.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCreateRequest {
    private String name;
    private String email;
}
