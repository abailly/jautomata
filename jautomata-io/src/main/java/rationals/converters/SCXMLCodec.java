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
 * Created on 29 mars 2005
 *
 */
package rationals.converters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.scxml.io.SCXMLDigester;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.TransitionTarget;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;

import rationals.Automaton;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;
import fr.lifl.parsing.ParsedObject;
import fr.lifl.parsing.ParserError;
import fr.lifl.parsing.ParserEvent;
import fr.lifl.parsing.ParserException;
import fr.lifl.parsing.ParserListener;
import fr.lifl.parsing.ParserListenerDelegate;
import fr.lifl.parsing.ParserObjectEvent;
import fr.lifl.parsing.ParserPosition;
import fr.lifl.parsing.ParserWarning;
import fr.lifl.xmlutils.SAXGen;

/**
 * Encode/decode an Automaton according to SCXML format. This Codec uses <a
 * href="http://commons.apache.org/scxml">commons-scxml</a> digester and model
 * to read/write Automaton description. Only basic structural information of the
 * StateChart model is used to construct State and Transition objects, the rest
 * is kept as objects encapsulated either as states or transitions. Parallel
 * states are unfolded with join states being used as synchronisation points.
 * 
 * @author nono
 * @version $Id: SCXMLCodec.java 2 2006-08-24 14:41:48Z oqube $  @see <a href="http://www.w3.org/TR/scxml/">SCXML</a> W3C
 *          proposal
 */
public class SCXMLCodec implements StreamDecoder, StreamEncoder, ErrorHandler {

  private ParserListenerDelegate listeners = new ParserListenerDelegate();
  
  class StateInfoObject implements ParsedObject {

    
    private State state;
    private String id;

    public StateInfoObject(State q0, String id) {
      this.state = q0;
      this.id = id;
    }

    public Object getTag(String name) {
      if("name".equals(name))
        return id;
      else if("state".equals(name))
        return state;
      else
      return null;
    }

    public void setTag(String name, Object tag) {
    }
    
  }
  /*
   * (non-Javadoc)
   * 
   * @see rationals.converters.StreamDecoder#input(java.io.InputStream)
   */
  public Automaton input(InputStream is) throws IOException {
    try {
      SCXML scxml = SCXMLDigester.digest(new InputSource(is), this);
      return convert(scxml);
    } catch (Exception e) {
      e.printStackTrace();
      throw new IOException("Caught exception while parsing document :" + e);
    }
  }

  /**
   * Convert the given SCXML model to an "equivalent" Automaton object. This
   * method does the job of converting from scxml model to simpler Automaton
   * model. At present time, it only handles a small subset of the SCXML syntax:
   * State, Transition, InitialState.
   * <p>
   * The automaton is constructed by traversing the SCXML model and adding
   * states and transitions along the way. Vars are added the FIDL way.
   * </p>
   */
  public Automaton convert(SCXML scxml) {
    /* the automaton */
    Automaton a = new Automaton();
    /* map scxml states to automaton states */
    Map stm = new HashMap();
    org.apache.commons.scxml.model.State scs = scxml.getInitialState();
    State q0 = a.addState(true, false);
    notifyState(scs, q0);
    stm.put(scs, q0);
    /* traverse */
    Stack todo = new Stack();
    Set done = new HashSet();
    todo.push(scs);
    while (!todo.isEmpty()) {
      scs = (org.apache.commons.scxml.model.State) todo.pop();
      done.add(scs);
      q0 = (State)stm.get(scs);
      List l = scs.getTransitionsList();
      for (Iterator i = l.iterator(); i.hasNext();) {
        org.apache.commons.scxml.model.Transition sctr = (org.apache.commons.scxml.model.Transition) i
            .next();
        TransitionTarget tt = sctr.getTarget();
        State q = (State) stm.get(tt);
        if (q == null) {
          q = a.addState(false,
              (tt instanceof org.apache.commons.scxml.model.State)
                  && ((org.apache.commons.scxml.model.State) tt).getIsFinal());
          notifyState((org.apache.commons.scxml.model.State)tt,q);
          stm.put(tt, q);
          if (!done.contains(q))
            todo.push(tt);
        }
        /* create transition label */
        Object lbl = null;
        try {
          lbl = makeLabelFrom(sctr);
        }catch(ParserException  pe){
          // lbl is set to null
          ParserError er =new ParserError();
          er.setMessage(pe.getLocalizedMessage());
          listeners.notify(er);
        }
        if(lbl == null) {
          ParserWarning w= new ParserWarning();
          w.setMessage("Setting empty transition from "+sctr.getEvent());
          listeners.notify(w);          
        }
        Transition tr = new Transition(q0, lbl, q);
        try {
          a.addTransition(tr);
        } catch (NoSuchStateException e) {
          // NEVER HAPPENS
        }
      }
    }
    return a;
  }

  /**
   * @param scs
   * @param q0
   */
  private void notifyState(org.apache.commons.scxml.model.State scs, State q0) {
    ParserObjectEvent ns = new ParserObjectEvent();
    ns.setBound(new StateInfoObject(q0,scs.getId()));
    ns.setName(scs.getId());
    listeners.notify(ns);
  }

  /**
   * Produce the label to stand for the given transition. This method may - and
   * should - be overriden by subclasses to handle complex and applications
   * specific transitions labels.
   * 
   * @param sctr
   *          the SCXML transition object
   * @return label for transition. MAy be null.
   * @exception ParserException if the transition content cannot be formatted into 
   * a correct label.
   */
  protected Object makeLabelFrom(org.apache.commons.scxml.model.Transition sctr) throws ParserException {
    String ev = sctr.getEvent();
    String cond = sctr.getCond();
    return (cond == null) ? ev : "[" + cond + "] " + ev;
  }

  /**
   * Converts an Automaton into an "equivalent" SCXML model. States are
   * converted to State objects, Transitions to transitions. Labels of of
   * transitions are analysed and split into guard/event pairs according to
   * standard UML syntax:
   * 
   * <pre>
   *    [guard] event / action1;action2;...;actionn
   * </pre>
   * 
   * <strong>NOTE:</strong> At present time, the action part is ignored.
   * 
   * @param a
   *          the Automaton to convert  @return an instance of SCXML
   */
  public SCXML convert(Automaton a) {
    SCXML scxml = new SCXML();
    /* map states */
    Map stm = new HashMap();
    for (Iterator i = a.states().iterator(); i.hasNext();) {
      State st = (State) i.next();
      org.apache.commons.scxml.model.State nst = new org.apache.commons.scxml.model.State();
      nst.setId(st.toString());
      if (st.isInitial())
        scxml.setInitialState(nst);
      if (st.isTerminal())
        nst.setIsFinal(true);
      stm.put(st, nst);
    }
    /* map transitions */
    for (Iterator i = a.delta().iterator(); i.hasNext();) {
      Transition tr = (Transition) i.next();
      String lbl = tr.label().toString();
      /* split guard/event */
      Pattern pat = Pattern.compile("[\\([^]]*\\)]\\(.*\\)");
      Matcher match = pat.matcher(lbl);
      String cond = match.group(1);
      String ev = match.group(2);
      org.apache.commons.scxml.model.Transition ntr = new org.apache.commons.scxml.model.Transition();
      ntr.setCond(cond);
      ntr.setEvent(ev);
      /* find source and target */
      org.apache.commons.scxml.model.State src = (org.apache.commons.scxml.model.State) stm
          .get(tr.start());
      org.apache.commons.scxml.model.State tgt = (org.apache.commons.scxml.model.State) stm
          .get(tr.end());
      ntr.setTarget(tgt);
      src.addTransition(ntr);
    }
    return scxml;
  }

  protected void xmlOutput(OutputStream stream, SCXML scxml) throws SAXException,
      IOException {
    SAXGen out = new SAXGen(stream);
    out.startDocument();
    out.startPrefixMapping("sc", "http://www.w3.org/2005/07/scxml");
    AttributesImpl attrs = new AttributesImpl();
    attrs.addAttribute(null, null, "sc:initialstate", "ID", ""
        + scxml.getInitialstate());
    out.startElement(null, null, "sc:scxml", attrs);
    /* states output */
    for (Iterator i = scxml.getStates().values().iterator(); i.hasNext();) {
      org.apache.commons.scxml.model.State st = (org.apache.commons.scxml.model.State) i
          .next();
      attrs = new AttributesImpl();
      attrs.addAttribute(null, null, "sc:ID", "ID", st.getId());
      attrs.addAttribute(null, null, "sc:final", "CDATA", "" + st.getIsFinal());
      out.startElement(null, null, "sc:state", attrs);
      /* transitions */
      for (Iterator j = st.getTransitionsList().iterator(); j.hasNext();) {
        org.apache.commons.scxml.model.Transition tr = (org.apache.commons.scxml.model.Transition) j
            .next();
        attrs = new AttributesImpl();
        attrs.addAttribute(null, null, "sc:cond", "CDATA", tr.getCond());
        attrs.addAttribute(null, null, "sc:event", "CDATA", tr.getEvent());
        attrs.addAttribute(null, null, "sc:target", "ID", tr.getTarget()
            .getId());
        out.startEndElement(null, null, "sc:transition", attrs);
      }
      out.endElement(null, null, "sc:state");
    }
    out.endElement(null, null, "sc:scxml");
    out.endDocument();
  }

  /**
   * (non-Javadoc)
   * 
   * @see rationals.converters.StreamEncoder#output(rationals.Automaton,
   *      java.io.OutputStream)
   */
  public void output(Automaton a, OutputStream stream) throws IOException {
    /* convert to SCXML */
    SCXML scxml = convert(a);
    /* output XML */
    try {
      xmlOutput(stream, scxml);
    } catch (SAXException e) {
      e.printStackTrace();
      throw new IOException("Caught SAX exception :" + e);
    }
  }
  
  public void addParserListener(ParserListener l){
    listeners.addParserListener(l);
  }

  public void fatalError(SAXParseException e) {
    ParserError er =new ParserError();
    er.setMessage(e.getLocalizedMessage());
    er.setPosition(new ParserPosition(e.getLineNumber(),0));
    listeners.notify(er);
  }

  public void error(SAXParseException e) {
    ParserError er =new ParserError();
    er.setMessage(e.getLocalizedMessage());
    er.setPosition(new ParserPosition(e.getLineNumber(),0));
    listeners.notify(er);
  }

  public void warning(SAXParseException e) {
    ParserWarning er =new ParserWarning();
    er.setMessage(e.getLocalizedMessage());
    er.setPosition(new ParserPosition(e.getLineNumber(),0));
    listeners.notify(er);
  }

  /**
   * @return Returns the listeners.
   */
  protected ParserListenerDelegate getListeners() {
    return listeners;
  }

  /**
   * @param listeners The listeners to set.
   */
  protected void setListeners(ParserListenerDelegate listeners) {
    this.listeners = listeners;
  }

}
