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
package rationals.converters;
import rationals.Automaton;
import rationals.Builder;
import rationals.Transition;
import rationals.converters.analyzers.Parser;

public class Expression<Tr extends Transition<String>, T extends Builder<String, Tr, T>> implements FromString<Tr, T> {
  public Automaton<String, Tr, T> fromString(String s) throws ConverterException {
    return new Parser<String, Tr, T>(s).analyze() ;
  }
    
}

