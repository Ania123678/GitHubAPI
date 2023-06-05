package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Commit {
    @JsonProperty("sha")
    private String sha;

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }
}
