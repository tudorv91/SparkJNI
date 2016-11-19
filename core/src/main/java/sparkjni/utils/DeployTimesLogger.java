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
