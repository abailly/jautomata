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

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author nono
 * @version $Id: StateFactoryTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public abstract class StateFactoryTest extends TestCase {

    private Automaton a;

    /**
     * Constructor for DefaultStateFactoryTest.
     * 
     * @param arg0
     */
    public StateFactoryTest(String arg0,StateFactory sf) {
        super(arg0);
        this.a = new Automaton(sf);
    }

    protected void setUp() throws Exception {
        super.setUp();
        /* create lots of states */
        for (int i = 0; i < 5000; i++)
            a
                    .addState(i % 500 == 0 ? true : false, i % 600 == 0 ? true
                            : false);
    }

    public void testIterator() {
        Set s = a.initials();
        int i = 0;
        for (Iterator it = s.iterator(); it.hasNext(); i++) {
            State st = (State) it.next();
            assertTrue(st.isInitial());
        }
        assertEquals(10, i);
    }

    public void testIteratorConcur() {
        Set s = a.initials();
        int i = 0;
        for (Iterator it = s.iterator(); it.hasNext(); i++) {
            try {
                State st = (State) it.next();
            } catch (ConcurrentModificationException ccm) {
                return;
            }
            if (i == 5)
                a.addState(true, false);
        }
        fail("Should have thrown concurrent modification exception");
    }

    public void testIteratorNoSuchElement() {
        Set s = a.initials();
        int i = 0;
        Iterator it;
        for (it = s.iterator(); it.hasNext(); i++) {
            State st = (State) it.next();
        }
        try {
            State s1 = (State) it.next();
            fail("Should have thrown no such element exception");
        } catch (NoSuchElementException nse) {
        }
    }

    public void testSetAdd() {
        Set s = a.getStateFactory().stateSet();
        State st = a.addState(true, true);
        State s2 = a.addState(false, false);
        s.add(st);
        assertTrue(s.contains(st));
        assertTrue(!s.contains(s2));
    }

    public void testSetAddAll() {
        Set s = a.getStateFactory().stateSet();
        Set i = a.initials();
        s.addAll(i);
        for (Iterator it = i.iterator(); it.hasNext();)
            assertTrue(s.contains(it.next()));
    }

    public void testClear() {
        Set s = a.terminals();
        s.clear();
        assertTrue(s.isEmpty());
    }

    public void testContainsAll() {
        Set s = a.getStateFactory().stateSet();
        Set i = a.initials();
        s.addAll(i);
        s.addAll(a.terminals());
        assertTrue(s.containsAll(a.initials()) && s.containsAll(a.terminals()));
    }

    public void testEquals() {
        Set s = a.getStateFactory().stateSet();
        Set i = a.initials();
        s.addAll(i);
        assertTrue(s.equals(i));
    }

    public void testRemove() {
        Set s = a.getStateFactory().stateSet();
        Set i = a.initials();
        s.addAll(i);
        Iterator it = i.iterator();
        State st = (State) it.next();
        s.remove(st);
        while (it.hasNext())
            assertTrue(s.contains(it.next()));
        assertTrue(!s.contains(st));
    }
    
    public void testRemoveAll() {
        Set s = a.states();
        s.removeAll(a.initials());
        for(Iterator it= s.iterator();it.hasNext();) 
            if(((State)it.next()).isInitial())
                fail("Not removed all initial states");
    }
    
    public void testRetainAll() {
        Set s = a.initials();
        s.retainAll(a.terminals());
        /* should contain state 0 and 3000 */
        assertTrue(s.size() == 2);
    }
    
}
