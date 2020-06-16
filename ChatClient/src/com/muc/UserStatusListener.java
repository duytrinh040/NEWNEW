package com.muc;

public interface UserStatusListener {
    public void online(String userName);
    public void offline(String userName);

}
