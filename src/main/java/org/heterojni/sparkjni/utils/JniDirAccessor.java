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
package org.heterojni.sparkjni.utils;

import org.heterojni.sparkjni.jniLink.linkHandlers.JniHeaderHandler;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class which parses native header functions.
 */
public class JniDirAccessor {
    private String path;
    volatile private CopyOnWriteArrayList<JniHeaderHandler> jniHeaderHandlers = null;

    public JniDirAccessor(String path) {
        this.path = path;
    }

    public CopyOnWriteArrayList<JniHeaderHandler> getJniHeaderHandlers() {
        if(jniHeaderHandlers == null)
            findJniHeaderFiles(path);
        return jniHeaderHandlers;
    }

    private void findJniHeaderFiles(@Nonnull String path) {
        File nativeDir = new File(path);
        JniUtils.checkNativePath(nativeDir);
        jniHeaderHandlers = new CopyOnWriteArrayList<>();
        for (File file : new File(path).listFiles())
            processFile(file);
    }

    private void processFile(File file) {
        try {
            if(JniUtils.isJniNativeFunction(file.toPath()))
                jniHeaderHandlers.add(new JniHeaderHandler(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}