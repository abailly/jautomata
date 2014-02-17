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
package rationals.converters;

import rationals.Automaton;
import rationals.Transition;

import java.io.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Input/output an automaton in DOT format.
 * 
 * @author nono
 * @version $Id: DotCodec.java 2 2006-08-24 14:41:48Z oqube $
 */
public class DotCodec implements StreamEncoder, StreamDecoder {

    /* (non-Javadoc)
     * @see rationals.converters.StreamEncoder#output(rationals.Automaton, java.io.OutputStream)
     */
    public void output(Automaton a, OutputStream stream) throws IOException {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(stream));
        /* header */
        String name = a.getId() == null ? "jauto" : a.getId().toString();
        pw.println("digraph "+ name+" {");
        /* output states */
        Set initials = a.labelledInitials();

        pw.println("node [shape=circle, color=black, fontcolor=black, fixedsize= \"true\", width=\"1\"];");
        
        for(Iterator i = initials.iterator();i.hasNext();) {
            pw.print(stateLabel(i.next()) + " [shape=circle, style=\"filled\", fillcolor=black, fontcolor=white];");
        }
        
        Set terminals = a.labelledTerminals();
        terminals.removeAll(initials);
        for(Iterator i = terminals.iterator();i.hasNext();) {
            pw.print(stateLabel(i.next()) + " [shape=doublecircle,style=\"filled\", fillcolor=black, fontcolor=white];");
        }
        
        Set states = a.labelledStates();
        states.removeAll(initials);
        states.removeAll(terminals);
        for(Iterator i = states.iterator();i.hasNext();) {
            pw.print(stateLabel(i.next()));
            pw.println(";");
        }
        /* edges */
        for(Iterator i = a.delta().iterator();i.hasNext();) {
            Transition tr =(Transition)i.next();
            pw.println(stateLabel(tr.start())+" -> "+ stateLabel(tr.end()) +" [ label=\""+tr.label()+"\" ];");            
        }
        pw.println("}");
        pw.flush();
        pw.close();
    }

    private String stateLabel(Object st) {
        String rawLabel = st.toString();
        return (Character.isDigit(rawLabel.charAt(0)) ? "s"+rawLabel : rawLabel);
    }

    /* (non-Javadoc)
     * @see rationals.converters.StreamDecoder#input(java.io.InputStream)
     */
    public Automaton input(InputStream is) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

}
