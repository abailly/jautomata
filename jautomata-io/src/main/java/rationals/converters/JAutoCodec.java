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
package rationals.converters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rationals.Automaton;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;

/**
 * A codec for rationals that stores an automaton in a specific format 
 * similar to the {@see ToString} class output.
 * <strong>Note </strong> : the alphabet may not contains the 
 * reserved characters '[', ',' , ']', '(', ')'.
 * 
 * <pre> 
    A = [ <comma separated alphabet> ]\n
    Q = [ <comma separated list of statess > ] \n
    I = [ <comma separated list of initials > ] \n 
    T = [ <comma separated list of terminals > ] \n 
    delta = [ \n 
    ( to, label ,from ) \n
    ( to, label ,from ) \n
     .... \n
        ] \n
 * </pre>
 * @author nono
 * @version $Id: JAutoCodec.java 2 2006-08-24 14:41:48Z oqube $
 * @see toAscii
 */
public class JAutoCodec implements StreamEncoder,StreamDecoder {

    /* (non-Javadoc)
     * @see rationals.converters.StreamEncoder#output(rationals.Automaton, java.io.OutputStream)
     */
    public void output(Automaton a, OutputStream stream) throws IOException {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(stream));
        pw.print(new toAscii().toString(a));
        pw.flush();
    }

    /* (non-Javadoc)
     * @see rationals.converters.StreamDecoder#input(java.io.InputStream)
     */
    public Automaton input(InputStream is) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        Automaton a = new Automaton();
        Map /* < String, State > */ smap = new HashMap();
        /* regexes for various parts */
        Pattern set = Pattern.compile("\\[\\s*(.*)\\s*\\]");
        Pattern trans = Pattern.compile("\\((.*)[ ]*,(.*),[ ]*([^)]*)\\)");
        /* read the alphabet - and discard it */
        String alph = rd.readLine();
        /* read states  */
        String states = rd.readLine();
        Matcher stm = set.matcher(states.substring(4));
        if(!stm.find())
            throw new IOException("Failed to parse states set in input stream "+states);
        String tmp = stm.group(0);
        states = stm.group(1);
        StringTokenizer st = new StringTokenizer(states,",");
        Set /* < String > */ sset = new HashSet(); /* set of all states */
        while(st.hasMoreTokens()) {
            String s = st.nextToken().trim();
            sset.add(s);
        }
        /* read initials */ 
        states = rd.readLine();
        stm = set.matcher(states.substring(4));
        if(!stm.find())
            throw new IOException("Failed to parse initial states set in input stream "+states);
        states = stm.group(1);
        st = new StringTokenizer(states,",");
        Set /* < String > */ iset = new HashSet(); /* set of all states */
        while(st.hasMoreTokens()) {
            String s = st.nextToken().trim();
            iset.add(s);
        }
        /* read terminals */ 
        states = rd.readLine();
        stm = set.matcher(states.substring(4));
        if(!stm.find())
           throw new IOException("Failed to parse terminals set in string "+states);
        states = stm.group(1);
        st = new StringTokenizer(states,",");
        Set /* < String > */ tset = new HashSet(); /* set of all states */
        while(st.hasMoreTokens()) {
            String s = st.nextToken().trim();
            tset.add(s);
        }
        /* create states */
        Iterator it= sset.iterator();
        while(it.hasNext()) {
            boolean init,fini;
            String s = (String)it.next();
            if(iset.contains(s))
                init = true;
            else
                init =false;
            if(tset.contains(s))
                fini = true;
            else
                fini = false;
            smap.put(s,a.addState(init,fini));
        }
        /* transitions */
        rd.readLine();
        String trs = null;
        while(!(trs = rd.readLine()).equals("]")) {
            stm = trans.matcher(trs);
            if(!stm.find())
                throw new IOException("Failed to parse transition in "+trs);
            State from = (State)smap.get(stm.group(1).trim());
            String lbl = stm.group(2).trim();
            if("1".equals(lbl))
                lbl = null;
            State to = (State)smap.get(stm.group(3).trim());
            if(from == null || to==null )
                throw new IOException("Failed to parse states in transition "+trs);
            try {
                a.addTransition(new Transition(from,lbl,to));
            } catch (NoSuchStateException e) {
                e.printStackTrace();
            }
        }
        return a;
    }

}
