/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.utils.defaultpopup;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.ResourceBundle;

/**
 * Clipboard utility methods
 *
 * @version 0.1.0 2019/07/18
 * @author ExBin Project (http://exbin.org)
 */
public class ClipboardUtils {

    private static Clipboard clipboard = null;

    private ClipboardUtils() {
    }

    /**
     * A shared {@code Clipboard}.
     *
     * @return clipboard clipboard instance
     */
    public static Clipboard getClipboard() {
        if (clipboard == null) {
            try {
                clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            } catch (SecurityException e) {
                clipboard = new Clipboard("sandbox");
            }
        }

        return clipboard;
    }

    /**
     * Registers popup menu show for various supported components accross all
     * AWT popup menu events.
     */
    public static void registerDefaultClipboardPopupMenu() {
        DefaultPopupMenu.register();
    }

    /**
     * Registers popup menu show for various supported components accross all
     * AWT popup menu events.
     *
     * @param resourceBundle resource bundle
     * @param resourceClass resource class
     */
    public static void registerDefaultClipboardPopupMenu(ResourceBundle resourceBundle, Class resourceClass) {
        DefaultPopupMenu.register(resourceBundle, resourceClass);
    }
}
