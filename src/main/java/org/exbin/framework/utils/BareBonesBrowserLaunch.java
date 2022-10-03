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
package org.exbin.framework.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JOptionPane;

@ParametersAreNonnullByDefault
public class BareBonesBrowserLaunch {

    private BareBonesBrowserLaunch() {
    }

    private static final String ERROR_MESSAGE = "Error attempting to launch web browser";

    /////////////////////////////////////////////////////////
    //  Bare Bones Browser Launch                          //
    //  Version 1.5 (December 10, 2005)                    //
    //  By Dem Pilafian                                    //
    //  Supports: Mac OS X, GNU/Linux, Unix, Windows XP    //
    //  Example Usage:                                     //
    //     String url = "http://www.centerkey.com/";       //
    //     BareBonesBrowserLaunch.openURL(url);            //
    //  Public Domain Software -- Free to Use as You Like  //
    /////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    public static void openURL(String url) {
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Mac OS")) {
                Class fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[]{String.class});
                openURL.invoke(null, new Object[]{url});
            } else if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else { //assume Unix or Linux
                String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
                String browser = null;
                for (int count = 0; count < browsers.length && browser == null; count++) {
                    if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]}).waitFor() == 0) {
                        browser = browsers[count];
                    }
                }
                if (browser == null) {
                    throw new Exception("Could not find web browser");
                } else {
                    Runtime.getRuntime().exec(new String[]{browser, url});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, ERROR_MESSAGE + ":\n" + e.getLocalizedMessage());
        }
    }

    public static void openDesktopURL(final String url) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        try {
                            java.net.URI uri = new java.net.URI(url);
                            desktop.browse(uri);
                            return;
                        } catch (IOException | URISyntaxException ex) {
                            Logger.getLogger(BareBonesBrowserLaunch.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                BareBonesBrowserLaunch.openURL(url);
            }
        });
    }

    public static void openDesktopURL(final URI uri) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        try {
                            desktop.browse(uri);
                            return;
                        } catch (IOException ex) {
                            Logger.getLogger(BareBonesBrowserLaunch.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                BareBonesBrowserLaunch.openURL(uri.toString());
            }
        });
    }

    public static void openDesktopURL(final URL url) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        try {
                            java.net.URI uri = url.toURI();
                            desktop.browse(uri);
                            return;
                        } catch (IOException | URISyntaxException ex) {
                            Logger.getLogger(BareBonesBrowserLaunch.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                BareBonesBrowserLaunch.openURL(url.toString());
            }
        });
    }
}
