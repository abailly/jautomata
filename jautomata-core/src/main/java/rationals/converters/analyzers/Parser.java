/*
 * (C) Copyright 2001 Arnaud Bailly (arnaud.oqube@gmail.com),
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
package rationals.converters.analyzers;

import rationals.Automaton;
import rationals.Builder;
import rationals.Transition;
import rationals.converters.ConverterException;
import rationals.transformations.Concatenation;
import rationals.transformations.Mix;
import rationals.transformations.Reducer;
import rationals.transformations.Shuffle;
import rationals.transformations.Star;
import rationals.transformations.Union;
// Grammar :
// E -> T E'
// E' -> + T E' | '/' '{' L '}' | eps
// T -> S T"
// T" -> '|' S T" | '#' S T" | eps
// S -> F T'
// T' -> F T' | eps
// F -> B B'
// B' -> * | int | ^ | eps
// B -> letter | 1 | 0 | ( E )
public class Parser<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> {
    
  private Lexer<L> lexico ;
  
  /**
   * Parse given string using standard grammar and lexical analyzer.
   * 
   * @param expression the expression to parse
   * @see Lexer
   * @see DefaultLexer
   */
  public Parser(String expression) {
    lexico = new DefaultLexer(expression) ;
  }

  /**
   * Parse using the given lexer. 
   * 
   * @param lexer the lexer to use for parsing.
   */
  public Parser(Lexer<L> lexer) {
      this.lexico = lexer;
  }
  
  private Automaton<L, Tr, T> error(String message) throws ConverterException {
    throw new ConverterException("line " + lexico.lineNumber() + " , " + lexico.label() + " : " + message);
  }
  
  public Automaton<L, Tr, T> analyze() throws ConverterException {
    lexico.read() ;
    Automaton<L, Tr, T> r = E() ;
    if (lexico.current() != Lexer.END) error("end of expression expected") ;
    return r ; 
  }
  
  private Automaton<L, Tr, T> E() throws ConverterException {
    switch(lexico.current()) {
      case Lexer.EPSILON :
      case Lexer.EMPTY :
      case Lexer.OPEN :
      case Lexer.LABEL : {
        Automaton<L, Tr, T> a = T() ;
        Automaton<L, Tr, T> b = EP() ;
        return new Reducer<L, Tr, T>().transform(new Union<L, Tr, T>().transform(a , b)) ;
      }       
      case Lexer.CLOSE :
      case Lexer.END :
      case Lexer.UNION :
      case Lexer.SHUFFLE :
	  case Lexer.MIX : 
      case Lexer.STAR :
      case Lexer.ITERATION :
      case Lexer.INT :
      default : return error("expression expected") ;
    }
  }

  private Automaton<L, Tr, T> EP() throws ConverterException {
    switch(lexico.current()) {
      case Lexer.EPSILON :
      case Lexer.EMPTY :
      case Lexer.OPEN :
      case Lexer.LABEL : return error("union expected") ; 
      case Lexer.CLOSE :
      case Lexer.END : return new Automaton<L, Tr, T>() ;
      case Lexer.UNION : {
        lexico.read() ;
        Automaton<L, Tr, T> a = T() ;
        Automaton<L, Tr, T> b = EP() ;
        return new Reducer<L, Tr, T>().transform(new Union().transform(a , b)) ;
      }
      case Lexer.SHUFFLE :
	  case Lexer.MIX : 
      case Lexer.STAR :
      case Lexer.ITERATION :
      case Lexer.INT :
      default : return error("union expected") ; 
    }
  }

  private Automaton<L, Tr, T> T() throws ConverterException {
    switch(lexico.current()) {
      case Lexer.EPSILON :
      case Lexer.EMPTY :
      case Lexer.OPEN :
      case Lexer.LABEL : {
        Automaton<L, Tr, T> a = S() ;
        Automaton<L, Tr, T> b = TS() ;
        return new Reducer<L, Tr, T>().transform(new Shuffle().transform(a , b)) ;
      }       
      case Lexer.CLOSE :
      case Lexer.END :
      case Lexer.UNION :
      case Lexer.SHUFFLE :
	  case Lexer.MIX : 
      case Lexer.STAR :
      case Lexer.ITERATION :
      case Lexer.INT :
      default : return error("expression expected") ;
    }
  }

  private Automaton<L, Tr, T> TS() throws ConverterException {
    switch(lexico.current()) {
      case Lexer.EPSILON :
      case Lexer.EMPTY :
      case Lexer.OPEN :
      case Lexer.LABEL :return error("concatenation expected") ;
      case Lexer.CLOSE :
      case Lexer.END : 
      case Lexer.UNION : return Automaton.epsilonAutomaton() ;
	  case Lexer.SHUFFLE : {
		lexico.read() ;
		Automaton<L, Tr, T> a = S() ;
		Automaton<L, Tr, T> b = TS() ;
		return new Reducer<L, Tr, T>().transform(new Shuffle<L, Tr, T>().transform(a , b)) ;
	  }
	  case Lexer.MIX : 
	  	{
		lexico.read() ;
		Automaton<L, Tr, T> a = S() ;
		Automaton<L, Tr, T> b = TS() ;
		return new Reducer<L, Tr, T>().transform(new Mix().transform(a , b)) ;
	  }
      case Lexer.STAR :
      case Lexer.ITERATION :
      case Lexer.INT :
      default : return error("concatenation expected") ; 
    }
  }

  private Automaton<L, Tr, T> S() throws ConverterException {
    switch(lexico.current()) {
      case Lexer.EPSILON :
      case Lexer.EMPTY :
      case Lexer.OPEN :
      case Lexer.LABEL : {
        Automaton<L, Tr, T> a = F() ;
        Automaton<L, Tr, T> b = TP() ;
        return new Reducer<L, Tr, T>().transform(new Concatenation<L, Tr, T>().transform(a , b)) ;
      }       
      case Lexer.CLOSE :
      case Lexer.END :
      case Lexer.UNION :
      case Lexer.SHUFFLE :
	  case Lexer.MIX : 
      case Lexer.STAR :
      case Lexer.ITERATION :
      case Lexer.INT :
      default : return error("expression expected") ;
    }
  }

  private Automaton<L, Tr, T> TP() throws ConverterException {
    switch(lexico.current()) {
      case Lexer.EPSILON :
      case Lexer.EMPTY :
      case Lexer.OPEN :
      case Lexer.LABEL :{
        Automaton<L, Tr, T> a = F() ;
        Automaton<L, Tr, T> b = TP() ;
        return new Reducer<L, Tr, T>().transform(new Concatenation<L, Tr, T>().transform(a , b)) ;
      }
      case Lexer.CLOSE :
      case Lexer.END : 
      case Lexer.UNION : 
	  case Lexer.MIX : 
      case Lexer.SHUFFLE :return Automaton.epsilonAutomaton() ;
      case Lexer.STAR :
      case Lexer.ITERATION :
      case Lexer.INT :
      default : return error("concatenation expected") ; 
    }
  }

  private Automaton<L, Tr, T> F() throws ConverterException {
    switch(lexico.current()) {
      case Lexer.EPSILON :
      case Lexer.EMPTY :
      case Lexer.OPEN :
      case Lexer.LABEL : {
        Automaton<L, Tr, T> a = BP(B()) ; 
        return a ;
      }       
      case Lexer.CLOSE :
      case Lexer.END :
      case Lexer.UNION :
	  case Lexer.MIX : 
      case Lexer.SHUFFLE :
      case Lexer.STAR :
      case Lexer.ITERATION :
      case Lexer.INT :
      default : return error("factor expected") ;
    }
  }

  private Automaton<L, Tr, T> B() throws ConverterException {
    switch(lexico.current()) {
      case Lexer.EPSILON : {
        Automaton<L, Tr, T> a = Automaton.epsilonAutomaton() ;
        lexico.read() ;
        return a ;
      }
      case Lexer.EMPTY : {
        Automaton<L, Tr, T> a = new Automaton<>() ;
        lexico.read() ;
        return a ;
      }
      case Lexer.OPEN : {
        lexico.read() ;
        Automaton<L, Tr, T> a = E() ;
        if (lexico.current() != Lexer.CLOSE) return error("( expected") ;
        lexico.read() ;
        return a ;
      }
      case Lexer.LABEL : {
        Automaton<L, Tr, T> a = Automaton.labelAutomaton(lexico.label()) ;
        lexico.read() ;
        return a ;
      }      
      case Lexer.CLOSE :
      case Lexer.END :
      case Lexer.SHUFFLE :
	  case Lexer.MIX : 
      case Lexer.UNION :
      case Lexer.STAR :
      case Lexer.ITERATION :
      case Lexer.INT :
      default : return error("factor expected") ;
    }
  }

  private Automaton<L, Tr, T> BP(Automaton<L, Tr, T> a) throws ConverterException {
    switch(lexico.current()) {
      case Lexer.OPEN :
      case Lexer.LABEL :
      case Lexer.CLOSE :
      case Lexer.END :
      case Lexer.UNION : 
	  case Lexer.MIX : 
      case Lexer.SHUFFLE :return a ;
      case Lexer.STAR : {
        lexico.read() ; 
        return new Reducer<L, Tr, T>().transform(new Star().transform(a)) ;
      }
      case Lexer.ITERATION :
        lexico.read() ; 
        return new Reducer<L, Tr, T>().transform(new Concatenation<L, Tr, T>().transform(a, new Star().transform(a))) ;
      case Lexer.EPSILON :
      case Lexer.EMPTY :
      case Lexer.INT : {
        int value = lexico.value() ;
        lexico.read() ;
        Automaton<L, Tr, T> b = Automaton.epsilonAutomaton();
        for (int i = 0 ; i < value ; i++) {
          b = new Reducer<L, Tr, T>().transform(new Concatenation<L, Tr, T>().transform(b , a)) ;
        }
        return b ;
      }
      default : return error("Unexpected character") ;
    }
  }
}
