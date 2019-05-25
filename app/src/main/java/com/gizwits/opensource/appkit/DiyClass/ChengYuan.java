package com.gizwits.opensource.appkit.DiyClass;


public class ChengYuan {
    private String aName;
    private String aPhone;
    private int aIcon;
    private int aEdit;

    public ChengYuan() {
    }

    public ChengYuan(String aName, String aPhone, int aIcon, int aEdit) {
        this.aName = aName;
        this.aPhone = aPhone;
        this.aIcon = aIcon;
        this.aEdit = aEdit;
    }

    public String getaName() {
        return aName;
    }

    public String getaPhone() {
        return aPhone;
    }

    public int getaIcon() {
        return aIcon;
    }

    public int getaEdit() {
        return aEdit;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public void setaPhone(String aPhone) {
        this.aPhone = aPhone;
    }

    public void setaIcon(int aIcon) {
        this.aIcon = aIcon;
    }
    public void setaEdit(int aEdit) {
        this.aIcon = aEdit;
    }

}
