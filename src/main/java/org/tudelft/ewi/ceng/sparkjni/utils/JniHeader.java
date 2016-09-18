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
package org.tudelft.ewi.ceng.sparkjni.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Container which parses the javah-generated jni function header files and retrieves JNI prototypes.
 */
public class JniHeader {
    private File jniHeaderFile;
    private String fullyQualifiedJavaClassName = null;
    private List<JniFunctionPrototype> jniFunctions = null;

    public JniHeader(File jniHeaderFile) {
        this.jniHeaderFile = jniHeaderFile;
    }

    public String getFullyQualifiedJavaClassName() {
        if(fullyQualifiedJavaClassName == null)
            parse();
        return fullyQualifiedJavaClassName;
    }

    public List<JniFunctionPrototype> getJniFunctions() {
        if(jniFunctions == null)
            parse();
        return jniFunctions;
    }

    private void parse(){
        String fn = jniHeaderFile.getName();
        fullyQualifiedJavaClassName = fn.replaceAll("_",".");
        fullyQualifiedJavaClassName = fullyQualifiedJavaClassName.substring(0, fullyQualifiedJavaClassName.length() - 2);
        try {
            registerJniFunctions();
        } catch(IOException exception){}
    }

    private void registerJniFunctions() throws IOException{
        jniFunctions = new ArrayList<>();
        for(String line: Files.readAllLines(jniHeaderFile.toPath(), Charset.defaultCharset())){
            if(line.contains("JNIEXPORT")){
                String[] tokens = line.split("\\s");
                try {
                    jniFunctions.add(new JniFunctionPrototype(fullyQualifiedJavaClassName, tokens[JniUtils.PROTOTYPE_WORD_NO_IN_LINE]));
                } catch(IndexOutOfBoundsException e){
                    throw new RuntimeException(String.format(JniUtils.ERR_INVALID_FORMATTING_FOR_FILE_AT_LINE, jniHeaderFile.getName(), line));
                }
            }
        }
    }
}
