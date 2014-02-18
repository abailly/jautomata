/*
 * (C) Copyright 2003 Arnaud Bailly (arnaud.oqube@gmail.com),
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
package rationals;

/**
 * An interface for objects running automatons
 * <p>
 * This interface essentially provide a way to communicate with {@link AutomatonRunListener}
 * objects for notifying run events
 * 
 * @author nono
 * @version $Id: AutomatonRunner.java 2 2006-08-24 14:41:48Z oqube $
 */
public interface AutomatonRunner {

    /**
     * Adds a listener to this runner
     * 
     * @param l the listener to add - may no be null
     */
    public void addRunListener(AutomatonRunListener l);
    
    /**
     * Remove a listener from this runner
     * 
     * @param l the listener to remove
     */
    public void removeRunListener(AutomatonRunListener l);
}

/* 
 * $Log: AutomatonRunner.java,v $
 * Revision 1.1  2004/07/23 11:59:17  bailly
 * added listener interfaces
 *
*/