/*
 * (C) Copyright $YEAR Arnaud Bailly (arnaud.oqube@gmail.com),
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
package rationals.transductions.testing;

import java.util.Set;

/**
 * An exception to notify failure of a test.
 * An object of this class contains the failed test cases as 
 * a Set of input letters.
 * 
 * @author nono
 * @version $Id: TestFailure.java 2 2006-08-24 14:41:48Z oqube $
 */
public class TestFailure extends Exception {

    private Set failureSet;
    
    public TestFailure(Set failure) {
        this.failureSet = failure;
    }
    
    public Set getFailureSet() {
        return failureSet;
    }
    public void setFailureSet(Set failureSet) {
        this.failureSet = failureSet;
    }
}
