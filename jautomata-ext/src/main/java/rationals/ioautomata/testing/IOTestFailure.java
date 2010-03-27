/*______________________________________________________________________________
 * 
 * Copyright 2005 Arnaud Bailly - NORSYS/LIFL
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * (2) Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * (3) The name of the author may not be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on 14 avr. 2005
 *
 */
package rationals.ioautomata.testing;

import java.util.List;

/**
 * An exception that is thrown during testing of IOAutomaton.
 * This exception contains the failed execution trace. 
 * 
 * @author nono
 * @version $Id: IOTestFailure.java 2 2006-08-24 14:41:48Z oqube $
 */
public class IOTestFailure extends Exception {

    private List failureTrace;
    
    /**
     * 
     */
    public IOTestFailure() {
        super();
    }

    /**
     * @param message
     */
    public IOTestFailure(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public IOTestFailure(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public IOTestFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public List getFailureTrace() {
        return failureTrace;
    }
    
    public void setFailureTrace(List failureTrace) {
        this.failureTrace = failureTrace;
    }
}
