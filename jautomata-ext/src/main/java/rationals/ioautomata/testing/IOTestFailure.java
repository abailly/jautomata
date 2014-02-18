/*
 * (C) Copyright 2005 Arnaud Bailly (arnaud.oqube@gmail.com),
 *     Yves Roos (yroos@lifl.fr) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
