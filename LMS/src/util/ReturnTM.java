package util;

import java.util.Date;

public class ReturnTM {
    private String issueid;
    private String memberid;
    private String membername;
    private Date date;

    public ReturnTM() {
    }

    public ReturnTM(String issueid, String memberid, String membername, Date date) {
        this.issueid = issueid;
        this.memberid = memberid;
        this.membername = membername;
        this.date = date;
    }

    public String getIssueid() {
        return issueid;
    }

    public void setIssueid(String issueid) {
        this.issueid = issueid;
    }

    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }

    public String getMembername() {
        return membername;
    }

    public void setMembername(String membername) {
        this.membername = membername;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ReturnTM{" +
                "issueid='" + issueid + '\'' +
                ", memberid='" + memberid + '\'' +
                ", membername='" + membername + '\'' +
                ", date=" + date +
                '}';
    }
}
