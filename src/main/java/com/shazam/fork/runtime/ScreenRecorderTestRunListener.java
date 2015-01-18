/*
 * Copyright 2015 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.shazam.fork.runtime;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.ScreenRecorderOptions;
import com.android.ddmlib.testrunner.ITestRunListener;
import com.android.ddmlib.testrunner.TestIdentifier;
import com.shazam.fork.Utils;
import com.shazam.fork.io.FileManager;
import com.shazam.fork.io.RemoteFileManager;
import com.shazam.fork.model.Device;
import com.shazam.fork.system.CancellableShellOutputReceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

import static com.shazam.fork.Utils.millisSinceNanoTime;
import static com.shazam.fork.io.FileType.SCREENRECORD;
import static com.shazam.fork.io.RemoteFileManager.*;
import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ScreenRecorderTestRunListener implements ITestRunListener {
    private static final Logger logger = LoggerFactory.getLogger(ScreenRecorderTestRunListener.class);
    private static final int DURATION = 30;
    private static final int BIT_RATE_MBPS = 1;
    private static final ScreenRecorderOptions RECORDER_OPTIONS = new ScreenRecorderOptions.Builder()
            .setTimeLimit(DURATION, SECONDS)
            .setBitRate(BIT_RATE_MBPS)
            .build();

    private final FileManager fileManager;
    private final String pool;
    private final Device device;
    private final IDevice deviceInterface;
    private final String remoteDirectory;

    private boolean hasFailed;
    private CancellableShellOutputReceiver cancellableReceiver;

    public ScreenRecorderTestRunListener(FileManager fileManager, String pool, Device device) {
        this.fileManager = fileManager;
        this.pool = pool;
        this.device = device;
        deviceInterface = device.getDeviceInterface();
        remoteDirectory = RemoteFileManager.FORK_DIRECTORY;
    }

    @Override
    public void testRunStarted(String runName, int testCount) {
    }

    @Override
    public void testStarted(TestIdentifier test) {
        cancellableReceiver = new CancellableShellOutputReceiver();
        hasFailed = false;
        try {
            String remoteFilePath = remoteVideoForTest(remoteDirectory, test);
            logger.debug("Started recording video at: {}", remoteFilePath);
            long startNanos = nanoTime();
            deviceInterface.startScreenRecorder(remoteFilePath, RECORDER_OPTIONS, cancellableReceiver);
            logger.debug("Recording finished in {}ms {}", millisSinceNanoTime(startNanos), remoteFilePath);
        } catch (Exception e) {
            logger.error("Could not start screen recorder", e);
        }
    }

    @Override
    public void testFailed(TestIdentifier test, String trace) {
        hasFailed = true;
    }

    @Override
    public void testAssumptionFailure(TestIdentifier test, String trace) {
    }

    @Override
    public void testIgnored(TestIdentifier test) {
    }

    @Override
    public void testEnded(TestIdentifier test, Map<String, String> testMetrics) {
        String remoteFilePath = remoteVideoForTest(remoteDirectory, test);

        try {
            File localVideoFile = fileManager.createFile(SCREENRECORD, pool, device.getSerial(), test);
            logger.debug("Started pulling file \n {} \n {}", remoteFilePath, localVideoFile);
            long startNanos = nanoTime();
            deviceInterface.pullFile(remoteFilePath, localVideoFile.toString());
            logger.debug("Pulling finished in {}ms {}", millisSinceNanoTime(startNanos), remoteFilePath);
//            if (hasFailed) {
//            }
        } catch (Exception e) {
            logger.warn("Failed to pull video file: " + e.getMessage());
        }
    }

    @Override
    public void testRunFailed(String errorMessage) {
    }

    @Override
    public void testRunStopped(long elapsedTime) {
    }

    @Override
    public void testRunEnded(long elapsedTime, Map<String, String> runMetrics) {
        removeRemotePath(deviceInterface, videosIn(remoteDirectory));
    }
}
