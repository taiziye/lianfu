package com.tangpo.lianfu.entity;

/**
 * Created by 果冻 on 2016/4/10.
 */
public class InvitedMessage {
    private String from;
    private long time;
    private String reason;
    private InviteMessageStatus status;
    private String groupId;
    private String groupName;
    private String groupInviter;
    private int id;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupInviter() {
        return groupInviter;
    }

    public void setGroupInviter(String groupInviter) {
        this.groupInviter = groupInviter;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public InviteMessageStatus getStatus() {
        return status;
    }

    public void setStatus(InviteMessageStatus status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public enum InviteMessageStatus{
        BEINVITEED,
        BEREFUSED,
        BEAGREED,

        BEAPPLYED,
        AGREED,
        REFUSED,

        GROUPINVITATION,
        GROUPINVITATION_ACCEPTED,
        GROUPINVITATION_DECLINED
    }
}
