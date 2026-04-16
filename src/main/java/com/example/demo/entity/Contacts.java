package com.example.demo.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Contacts {

    private String phone;
    private String email;
}