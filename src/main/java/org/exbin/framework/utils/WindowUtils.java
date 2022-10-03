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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 * Utility static methods usable for windows and dialogs.
 *
 * @author ExBin Project (http://exbin.org)
 * @version 0.1.0 2019/07/22
 */
@ParametersAreNonnullByDefault
public final class WindowUtils {

    private static final int BUTTON_CLICK_TIME = 150;

    private WindowUtils() {
    }

    public static void invokeWindow(final Window window) {
        java.awt.EventQueue.invokeLater(() -> {
            if (window instanceof JDialog) {
                ((JDialog) window).setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            }
            window.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            window.setVisible(true);
        });
    }

    @Nonnull
    public static DialogWrapper createDialog(final JComponent component, Component parent, String dialogTitle, Dialog.ModalityType modalityType) {
        DialogDescriptor dialogDescriptor = new DialogDescriptor(component, dialogTitle, modalityType != Dialog.ModalityType.MODELESS, new Object[0], null, 0, null, null);
        final Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
        Dimension size = component.getPreferredSize();
        dialog.add(component);
        dialog.setSize(size.width + 8, size.height + 24);
        dialog.setTitle(dialogTitle);
        return new DialogWrapper() {
            @Override
            public void show() {
                dialog.setVisible(true);
            }

            @Override
            public void showCentered(@Nullable Component component) {
                center(component);
                show();
            }

            @Override
            public void close() {
                closeWindow(dialog);
            }

            @Override
            public void dispose() {
                dialog.dispose();
            }

            @Nonnull
            @Override
            public Window getWindow() {
                return dialog;
            }

            @Nonnull
            @Override
            public Container getParent() {
                return dialog.getParent();
            }

            @Override
            public void center(@Nullable Component component) {
                if (component == null) {
                    center();
                } else {
                    dialog.setLocationRelativeTo(component);
                }
            }

            @Override
            public void center() {
                dialog.setLocationByPlatform(true);
            }
        };
    }

    @Nonnull
    public static JDialog createDialog(final JComponent component) {
        JDialog dialog = new JDialog();
        Dimension size = component.getPreferredSize();
        dialog.add(component);
        dialog.setSize(size.width + 8, size.height + 24);
        return dialog;
    }

    public static void invokeDialog(final JComponent component) {
        JDialog dialog = createDialog(component);
        invokeWindow(dialog);
    }

    public static void closeWindow(Window window) {
        window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Find frame component for given component.
     *
     * @param component instantiated component
     * @return frame instance if found
     */
    @Nullable
    public static Frame getFrame(Component component) {
        Window parentComponent = SwingUtilities.getWindowAncestor(component);
        while (!(parentComponent == null || parentComponent instanceof Frame)) {
            parentComponent = SwingUtilities.getWindowAncestor(parentComponent);
        }
        if (parentComponent == null) {
            parentComponent = JOptionPane.getRootFrame();
        }
        return (Frame) parentComponent;
    }

    /**
     * Assign ESCAPE/ENTER key for all focusable components recursively.
     *
     * @param component   target component
     * @param closeButton button which will be used for closing operation
     */
    public static void assignGlobalKeyListener(Component component, final JButton closeButton) {
        assignGlobalKeyListener(component, closeButton, closeButton);
    }

    /**
     * Assign ESCAPE/ENTER key for all focusable components recursively.
     *
     * @param component target component
     */
    public static void assignGlobalKeyListener(Component component, final JButton okButton, final JButton cancelButton) {
        final KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    boolean performOkAction = true;

                    if (evt.getSource() instanceof JButton) {
                        ((JButton) evt.getSource()).doClick(BUTTON_CLICK_TIME);
                        performOkAction = false;
                    } else if (evt.getSource() instanceof JTextArea) {
                        performOkAction = !((JTextArea) evt.getSource()).isEditable();
                    } else if (evt.getSource() instanceof JTextPane) {
                        performOkAction = !((JTextPane) evt.getSource()).isEditable();
                    } else if (evt.getSource() instanceof JEditorPane) {
                        performOkAction = !((JEditorPane) evt.getSource()).isEditable();
                    }

                    if (performOkAction) {
                        doButtonClick(okButton);
                    }
                } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    boolean performCancelAction = true;
                    if (evt.getSource() instanceof JComboBox) {
                        performCancelAction = !((JComboBox) evt.getSource()).isPopupVisible();
                    } else if (evt.getSource() instanceof JRootPane) {
                        // Ignore in popup menus
                        performCancelAction = false;
                    }

                    if (performCancelAction) {
                        doButtonClick(cancelButton);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        };

        RecursiveLazyComponentListener componentListener = new RecursiveLazyComponentListener(new LazyComponentListener() {
            @Override
            public void componentCreated(Component component) {
                if (component.isFocusable()) {
                    component.addKeyListener(keyListener);
                }
            }
        });
        componentListener.fireListener(component);
    }

    /**
     * Performs visually visible click on the button component.
     *
     * @param button button component
     */
    public static void doButtonClick(JButton button) {
        button.doClick(BUTTON_CLICK_TIME);
    }

    @ParametersAreNonnullByDefault
    public interface DialogWrapper {

        void show();

        void showCentered(@Nullable Component window);

        void close();

        void dispose();

        @Nonnull
        Window getWindow();

        @Nonnull
        Container getParent();

        void center(@Nullable Component window);

        void center();
    }
}
