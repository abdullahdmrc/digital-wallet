package com.example.digitalwallet.dto;

import com.example.digitalwallet.model.User;
import lombok.Data;


@Data
public class RegisterRequest {
    private String name;
    private String surname;
    private String tckn;
    private User user;

}
