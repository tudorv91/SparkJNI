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
package sparkjni.dataLink;

import java.io.Serializable;

/**
 * Created by Tudor on 8/22/16.
 * Abstract to be inherited for all user-defined containers.
 */
public abstract class JavaBean implements Serializable {
    private static final int ONE_BILLION = 1000000000;
    long startRun;
    long endRun;
    long totalTime;

    public JavaBean(){}

    public long getTotalTimeNanos() {
        totalTime = endRun - startRun;
        return totalTime;
    }

    public double getTotalTimeSeconds() {
        double seconds = ((double)getTotalTimeNanos())/ ONE_BILLION;
        return seconds;
    }

    public long getStartRun() {
        return startRun;
    }

    public void setStartRun(long startRun) {
        this.startRun = startRun;
    }

    public long getEndRun() {
        return endRun;
    }

    public void setEndRun(long endRun) {
        this.endRun = endRun;
    }
}
