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
package rationals.graph;

import rationals.Automaton;
import rationals.State;
import salvo.jesus.graph.Graph;
import salvo.jesus.graph.algorithm.AdjacencyMatrix;

/**
 * @author nono
 * @version $Id: WordDistanceMatrix.java 2 2006-08-24 14:41:48Z oqube $
 */
public class WordDistanceMatrix extends AdjacencyMatrix {

    /**
     * @param graph
     */
    public WordDistanceMatrix(Automaton a) {
        super(new AutomatonGraphAdapter(a));
        /* set infty coefficients to 0 */
        for(int i=0;i<matrix.length;i++)
            for(int j=0;j<matrix.length;j++)
                if(Double.isInfinite(matrix[i][j]))
                    matrix[i][j] = 0;
    }

    /**
     * Populate a matrix from a graph.
     * 
     * @param matrix
     *            the matrix where result is stored in.
     * @param g
     *            the graph to extract matrix from.
     */
    protected void populateMatrix(final double matrix[][], final Graph g) {
        Automaton a = ((AutomatonGraphAdapter)getGraph()).getAutomaton();
        int n = a.states().size();
        State[] sts = new State[n];
        sts = (State[])a.states().toArray(sts);
        for(int i=0;i<n;i++) {
            for(int j=0;j<n;j++){
                matrix[i][j] = (a.deltaFrom(sts[i],sts[j]).size() > 0) ? 1 : 0;
            }
        }
    }

}
