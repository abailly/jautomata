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
package rationals.properties;

import rationals.Builder;
import rationals.Transition;

/**
 * @version $Id: Similar.java 2 2006-08-24 14:41:48Z oqube $
 */
public class Similar<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> extends AreEquivalent<L, Tr, T> {

    /**
     * @param r
     */
    public Similar() {
        super(new Simulation<L, Tr, T>());
    }

}
