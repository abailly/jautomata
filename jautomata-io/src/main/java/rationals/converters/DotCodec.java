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
 * Created on 31 mai 2005
 *
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
