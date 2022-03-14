package com.pollapp.pollapp.payload.response;

public class JWTAuthResponse {
    private String JWT;
    private String type = "Bearer";

    public JWTAuthResponse() {
    }

    public JWTAuthResponse(String JWT) {
        this.JWT = JWT;
    }

    public JWTAuthResponse(String JWT, String type) {
        this.JWT = JWT;
        this.type = type;
    }

    public String getJWT() {
        return JWT;
    }

    public void setJWT(String JWT) {
        this.JWT = JWT;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
