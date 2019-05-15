package io.rocketbase.commons.service.avatar;

public interface AvatarService {

    String getAvatar(String email);

    boolean isEnabled();

}
