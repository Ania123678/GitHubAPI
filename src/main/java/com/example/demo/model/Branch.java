package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Branch {

    @JsonProperty("name")
    private String name;

    @JsonProperty("commit")
    private Commit commit;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }
}
