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
package sparkjni.utils;

public class DeployTimesLogger {
    protected long start = 0L;
    protected long genTime = 0L;
    protected long buildTime = 0L;
    protected long libLoadTime = 0L;
    protected long javahTime = 0L;

    public long getStart() {
        return start;
    }

    public long getGenTime() {
        return genTime;
    }

    public long getBuildTime() {
        return buildTime;
    }

    public long getLibLoadTime() {
        return libLoadTime;
    }

    public long getJavahTime() {
        return javahTime;
    }
}
