package network;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FrequencyManager {
    private static final Set<Double> lockedFrequencies = Collections.synchronizedSet(new HashSet<>());
    private static final double[] AVAILABLE_FREQS = {
            1630.0, 1660.0, 1690.0, 1790.0, 1810.0, 1830.0, 2030.0, 2050.0,
            2070.0, 2090.0, 2205.0, 2215.0, 2225.0, 2240.0, 2260.0, 2280.0,
            2315.0, 2340.0, 2365.0, 2410.0, 2430.0, 2450.0, 2470.0, 2490.0, 2505.0
    };
    public static synchronized boolean lockFrequency(double freq) {
        if (lockedFrequencies.contains(freq)) {
            return false;
        }
        lockedFrequencies.add(freq);
        return true;
    }

    public static synchronized void releaseFrequency(double freq) {
        lockedFrequencies.remove(freq);
    }

    public static synchronized boolean isFrequencyAvailable(double freq) {
        return !lockedFrequencies.contains(freq);
    }

    public static double[] getAvailableFreqs() {
        return AVAILABLE_FREQS;
    }
}