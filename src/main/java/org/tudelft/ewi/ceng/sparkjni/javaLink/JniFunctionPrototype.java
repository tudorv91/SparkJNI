/**
 * Copyright 2016 Tudor Alexandru Voicu and Zaid Al-Ars, TUDelft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tudelft.ewi.ceng.sparkjni.javaLink;

/**
 * Created by root on 9/18/16.
 */
public class JniFunctionPrototype {
    private String fullyQualifiedJavaClass;
    private String jniFuncName;
    private String definingJavaMethodName = null;

    public JniFunctionPrototype(String fullyQualifiedJavaClass, String jniFuncName) {
        this.fullyQualifiedJavaClass = fullyQualifiedJavaClass;
        this.jniFuncName = jniFuncName;
    }

    public String getDefiningJavaMethodName(){
        if(definingJavaMethodName == null) {
            String[] aux = jniFuncName.split("_");
            definingJavaMethodName = aux[aux.length - 1];
        }
        return definingJavaMethodName;
    }

    public String getFullyQualifiedJavaClass() {
        return fullyQualifiedJavaClass;
    }

    public String getJniFuncName() {
        return jniFuncName;
    }
}
