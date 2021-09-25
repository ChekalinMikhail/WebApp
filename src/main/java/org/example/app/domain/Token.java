package org.example.app.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class Token {
    private String token;
    private Date createTokenTime;
    private Date lastUseTokenTime;
}
