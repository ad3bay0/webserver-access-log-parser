package com.ef;

/**
 *Variable Object representing blocked ips data that would be persisted in the database
 * 
 * @author Adebayo Adenyan
 */
public class BlockedIpObj {
    
    private String requestIp;
    private String blockMessage;
    
    public BlockedIpObj(String requestIp,String blockMessage){
    
        this.requestIp = requestIp;
        this.blockMessage = blockMessage;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public String getBlockMessage() {
        return blockMessage;
    }

    public void setBlockMessage(String blockMessage) {
        this.blockMessage = blockMessage;
    }
    
    
    
}
