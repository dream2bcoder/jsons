package com.jsons.data;

/**
 *
 * @author Subhankar
 */
public class Error {
    
    public int errno = 0;
    public String error = "";

    public Error() {
    }

    public Error(int errno, String errMsg) {
        this.errno = errno;
        this.error = errMsg;
    }
}
