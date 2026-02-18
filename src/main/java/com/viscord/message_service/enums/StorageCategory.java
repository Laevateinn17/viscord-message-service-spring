package com.viscord.message_service.enums;

public enum StorageCategory {
    AVATAR("avatars"),
    GUILD_ICON("icons"),
    ATTACHMENT("attachments"),
    ASSET("assets");

    private final String path;

    StorageCategory(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
