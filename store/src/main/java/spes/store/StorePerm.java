package spes.store;

public class StorePerm {
    public static final int STORE_PERM_NONE     = 0x0;
    public static final int STORE_PERM_READ     = 0x1;
    public static final int STORE_PERM_WRITE    = 0x2;
    private String permStr;
    private int perm;

    public StorePerm() {
        permStr = "";
        perm = STORE_PERM_NONE;
    }

    public StorePerm(String permStr) {
        fromPermStr(permStr);
    }
    public void fromPermStr(String str){
        permStr = "";
        perm = STORE_PERM_NONE;
        if(str.indexOf('r') >= 0){
            perm |= STORE_PERM_READ;
            permStr += 'r';
        }
        if(str.indexOf('w') >= 0){
            perm |= STORE_PERM_WRITE;
            permStr += 'w';
        }
    }

    public boolean readable(){return (perm & STORE_PERM_READ) > 0;}
    public boolean writable(){return (perm & STORE_PERM_WRITE) > 0;}

    public String getPermStr() {
        return permStr;
    }

    public void setPermStr(String permStr) {
        this.permStr = permStr;
    }

    public int getPerm() {
        return perm;
    }

    public void setPerm(int perm) {
        this.perm = perm;
    }

    public String toString(){return permStr;}
}
