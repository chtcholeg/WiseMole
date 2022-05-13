/*
 * Copyright (C) 2022 The Java Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package utils;

import java.util.Locale;

/**
 * The {@SystemUtils} is helper to receive
 *
 * @author olegshchepilov
 *
 */
public class SystemUtils {

    public enum OSFamily {
        WINDOWS, MACOS, LINUX, UNKNOWN
    }

    public static OSFamily getOSFamily() {
        if (osFamily == null) {
            osFamily = calcOSFamily();
        }
        return osFamily;
    }

    private static OSFamily calcOSFamily() {
        // Code taken from here:
        // https://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
            return OSFamily.MACOS;
        } else if (OS.indexOf("win") >= 0) {
            return OSFamily.WINDOWS;
        } else if (OS.indexOf("nux") >= 0) {
            return OSFamily.LINUX;
        }
        return OSFamily.UNKNOWN;
    }

    private static OSFamily osFamily = null;

}
