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

import org.tudelft.ewi.ceng.sparkjni.javaLink.JniHeader;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class which parses native header functions.
 */
public class JniDirAccessor {
    private String path;
    private ArrayList<JniHeader> jniHeaders = null;

    public JniDirAccessor(String path) {
        this.path = path;
    }

    public ArrayList<JniHeader> getJniHeaders() {
        if(jniHeaders == null)
            findJniHeaderFiles(path);
        return jniHeaders;
    }

    private void findJniHeaderFiles(@Nonnull String path) {
        File nativeDir = new File(path);
        JniUtils.checkNativePath(nativeDir);
        jniHeaders = new ArrayList<>();
        for (File file : new File(path).listFiles())
            processFile(file);
    }

    private void processFile(File file) {
        try {
            if(JniUtils.isJniNativeFunction(file.toPath()))
                jniHeaders.add(new JniHeader(file, jniHeaders));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}