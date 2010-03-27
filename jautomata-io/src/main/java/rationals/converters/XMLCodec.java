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

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import rationals.Automaton;

/**
 * Encode/decode an Automaton according to an XML format. <![CDATA[ <!ELEMENT
 * grxl (attr*, nodetype*, edgetype*, hostgraph*, transformation*)> <!ATTLIST
 * grxl id ID #IMPLIED>
 * 
 * <!ELEMENT nodetype (attr*)> <!ATTLIST nodetype id ID #REQUIRED parent IDREF
 * #IMPLIED shape CDATA #IMPLIED height CDATA #IMPLIED width CDATA #IMPLIED>
 * 
 * <!ELEMENT edgetype (attr*)> <!ATTLIST edgetype id ID #REQUIRED parent IDREF
 * #IMPLIED directed (true | false) "true">
 * 
 * <!ELEMENT hostgraph (attr*, node*, edge*)> <!ATTLIST hostgraph id ID
 * #REQUIRED>
 * 
 * <!ELEMENT transformation (attr*, rewrite*)> <!ATTLIST transformation id ID
 * #REQUIRED>
 * 
 * <!ELEMENT rewrite (attr*, lhsgraph, rhsgraph)> <!ATTLIST rewrite id ID
 * #REQUIRED>
 * 
 * <!ELEMENT lhsgraph (attr*, node*, edge*)> <!ATTLIST lhsgraph id ID #REQUIRED>
 * 
 * <!ELEMENT rhsgraph (attr*, node*, edge*)> <!ATTLIST rhsgraph id ID #REQUIRED>
 * 
 * <!-- match allows morphisms to be encoded -->
 * 
 * <!ELEMENT node (attr*)> <!ATTLIST node id ID #REQUIRED type IDREF #IMPLIED
 * match IDREF #IMPLIED label CDATA #IMPLIED xpos CDATA #IMPLIED ypos CDATA
 * #IMPLIED variable (true | false) "false" negative (true | false) "false">
 * 
 * <!ELEMENT edge (attr*)> <!ATTLIST edge type IDREF #IMPLIED match IDREF
 * #IMPLIED begin IDREF #REQUIRED end IDREF #REQUIRED label CDATA #IMPLIED
 * variable (true | false) "false" negative (true | false) "false">
 * 
 * <!-- attr has both a singleton attribute, plus attrelement to allow
 * collections -->
 * 
 * <!ELEMENT attr (attrelement)*> <!ATTLIST attr name CDATA #REQUIRED value
 * CDATA #IMPLIED>
 * 
 * <!ELEMENT attrelement EMPTY> <!ATTLIST attrelement name CDATA #REQUIRED value
 * CDATA #IMPLIED> ]]>
 * 
 * @author nono
 * @version $Id: XMLCodec.java 2 2006-08-24 14:41:48Z oqube $
 */
public class XMLCodec extends DefaultHandler implements StreamDecoder,
        StreamEncoder {

    private StringBuffer buffer;
    
    private Automaton auto;
    
    /*
     * (non-Javadoc)
     * 
     * @see rationals.converters.StreamDecoder#input(java.io.InputStream)
     */
    public Automaton input(InputStream is) throws IOException {
        try {
            XMLReader reader = SAXParserFactory.newInstance().newSAXParser()
                .getXMLReader();
            reader.setFeature("http://xml.org/sax/features/namespaces", true);
            reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            reader.setContentHandler(this);
            reader.parse(new InputSource(is));
            return auto;
          } catch (Exception e) {
            throw new IOException("Error in handling XMI file : "+ e.getMessage());
          }
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if(buffer != null) 
            buffer.append(ch,start,length);
    }
    
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // TODO Auto-generated method stub
        super.endElement(uri, localName, qName);
    }
    
    
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        	if("http://lifl.fr/jauto".equals(uri)) {
        	    if("automaton".equals(localName)) {
        	        
        	    }else if("transitions".equals(localName)) {
        	        
        	    }else if("transition".equals(localName)) {
        	        
        	    }else if("etats".equals(localName)) {
        	        
        	    }else if("etat".equals(localName)) {
        	        
        	    }
        	}
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see rationals.converters.StreamEncoder#output(rationals.Automaton,
     *      java.io.OutputStream)
     */
    public void output(Automaton a, OutputStream stream) throws IOException {
        // TODO Auto-generated method stub

    }

}
