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
package rationals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Maintain mapping between labels and abstract {@link State} objects.
 * <p>
 *     This is a disguised bi-directional {@link Map} between {@link State} instances 
 *     and arbitrary {@link Object} instances as labels.
 * </p>
 */
class StateLabels {
    
    private Map<State, Object> stateToLabels = new HashMap<State, Object>(); 
    private Map<Object, State> labelToStates = new HashMap<Object, State>();


    public State state(Object label) {
        return labelToStates.get(label);
    }

    public Set<Object> labels(Set<State> states) {
        Set<Object> ret = new HashSet<Object>();
        for (State state : states) {
            Object label = stateToLabels.get(state);
            if(label != null)
                ret.add(label);
            else
                ret.add(state);
        }
        return ret;
    }

    /**
     * Register a unique binding betwee a {@link State} object and an arbitrary label.
     * <p>
     *     Both must be unique within their respective collection.
     * </p>
     * 
     * @param state a state. Must not exist in this set.
     * @param label a lable. Must not exist in this set.
     * @return this object for chaining purpose.
     */
    public StateLabels bind(State state, Object label) {
        if(state == null) {
            throw new NullPointerException("cannot bind a null state");
        }
        
        if(label == null) {
            label = state;
        }
        
        stateToLabels.put(state,label);
        labelToStates.put(label,state);
        
        return this;
    }
}
