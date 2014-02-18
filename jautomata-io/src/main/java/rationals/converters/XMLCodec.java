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
