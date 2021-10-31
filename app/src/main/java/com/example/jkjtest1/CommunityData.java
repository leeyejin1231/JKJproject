package com.example.jkjtest1;

import java.io.Serializable;

public class CommunityData implements Serializable {

    private String member_id;
    private String member_name;
    private int likeCount;

    public String getMember_id() {
        return member_id;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
