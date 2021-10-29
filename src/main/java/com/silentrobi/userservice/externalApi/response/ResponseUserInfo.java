package com.silentrobi.userservice.externalApi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseUserInfo {
    private String name;
    private int age;
    private int count;
}
