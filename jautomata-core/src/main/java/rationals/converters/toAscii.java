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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class toAscii<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements ToString<L, Tr, T> {
    public String toString(Automaton<L, Tr, T> a) {
    	StringBuilder sb = new StringBuilder();
        sb.append("A = ").append(a.alphabet().toString()).append("\n");
        sb.append("Q = ").append(a.states().toString()).append("\n");
        sb.append("I = ").append(a.initials().toString()).append("\n");
        sb.append("T = ").append(a.terminals().toString()).append("\n");
        sb.append("delta = [\n");
        List<String> list = new ArrayList<>();
        Iterator<Transition<L>> i = a.delta().iterator();
        while (i.hasNext())
        	list.add(i.next().toString());
        java.util.Collections.sort(list);
        for (String s : list)
        	sb.append(s).append("\n");
        sb.append("]\n");
        return sb.toString();
    }
}
