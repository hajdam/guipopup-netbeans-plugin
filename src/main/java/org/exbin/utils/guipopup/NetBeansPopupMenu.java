/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.utils.guipopup;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.MenuSelectionManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.exbin.framework.popup.DefaultPopupMenu;
import org.exbin.framework.popup.handler.EditorPanePopupHandler;
import org.exbin.framework.popup.handler.ListPopupHandler;
import org.exbin.framework.popup.handler.TablePopupHandler;
import org.exbin.framework.popup.handler.TextComponentPopupHandler;
import org.exbin.framework.utils.ComponentPopupEventDispatcher;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.utils.guipopup.gui.InspectComponentPanel;
import org.openide.windows.WindowManager;

/**
 * Default popup menu for NetBeans.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class NetBeansPopupMenu extends DefaultPopupMenu {

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(NetBeansPopupMenu.class);

    private static NetBeansPopupMenu instance = null;

    private boolean registered = false;
    private boolean inspectMode = false;
    private EventQueue systemEventQueue;
    private EventQueue overriddenQueue;

    private NetBeansPopupMenu() {
    }

    @Nonnull
    public static synchronized NetBeansPopupMenu getInstance() {
        if (instance == null) {
            instance = new NetBeansPopupMenu();
        }

        return instance;
    }

    /**
     * Registers default popup menu to AWT.
     */
    public static void register() {
        NetBeansPopupMenu defaultPopupMenu = getInstance();
        if (!defaultPopupMenu.registered) {
            defaultPopupMenu.initDefaultPopupMenu();
            defaultPopupMenu.registerToEventQueue();
        }
    }

    /**
     * Registers default popup menu to AWT.
     *
     * @param resourceBundle resource bundle
     * @param resourceClass resource class
     */
    public static void register(ResourceBundle resourceBundle, Class resourceClass) {
        NetBeansPopupMenu defaultPopupMenu = getInstance();
        defaultPopupMenu.initDefaultPopupMenu(resourceBundle, resourceClass);
        defaultPopupMenu.registerToEventQueue();
    }

    private void registerToEventQueue() {
        overriddenQueue = new PopupEventQueue();
        systemEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        systemEventQueue.push(overriddenQueue);
        registered = true;
    }

    public static void unregister() {
        NetBeansPopupMenu defaultPopupMenu = getInstance();
        if (defaultPopupMenu.registered) {
            defaultPopupMenu.unregisterQueue();
        }
    }

    private void unregisterQueue() {
        overriddenQueue.push(systemEventQueue);
        registered = false;
    }

    private void initDefaultPopupMenu() {
        initDefaultPopupMenu(resourceBundle, this.getClass());
    }

    // TODO extend org.netbeans.core.TimableEventQueue
    @ParametersAreNonnullByDefault
    public class PopupEventQueue extends EventQueue {

        @Override
        protected void dispatchEvent(AWTEvent event) {
            if (event.getID() == MouseEvent.MOUSE_MOVED && inspectMode) {
                inspectMode = false;
                MouseEvent mouseEvent = (MouseEvent) event;
                Component component = getSource(mouseEvent);
                InspectComponentPanel inspectComponentPanel = new InspectComponentPanel();
                inspectComponentPanel.setComponent(component, null);
                Frame mainWindow = WindowManager.getDefault().getMainWindow();
                final WindowUtils.DialogWrapper dialog = WindowUtils.createDialog(inspectComponentPanel, mainWindow, "Inspect Component", Dialog.ModalityType.MODELESS);
                inspectComponentPanel.setCloseActionListener((ActionEvent e) -> {
                    dialog.close();
                });
                dialog.show();
                return;
            }
            super.dispatchEvent(event);

            processAWTEvent(event);
        }
    }

    @Override
    protected void processAWTEvent(AWTEvent event) {
        if (event.getID() == MouseEvent.MOUSE_RELEASED || event.getID() == MouseEvent.MOUSE_PRESSED) {
            MouseEvent mouseEvent = (MouseEvent) event;

            if (mouseEvent.isPopupTrigger()) {
                if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0) {
                    // Menu was already created
                    return;
                }

                for (ComponentPopupEventDispatcher dispatcher : clipboardEventDispatchers) {
                    if (dispatcher.dispatchMouseEvent(mouseEvent)) {
                        return;
                    }
                }

                Component component = getSource(mouseEvent);
                if (component instanceof JViewport) {
                    component = ((JViewport) component).getView();
                }

                if (component instanceof JEditorPane) {
                    activateMousePopup(mouseEvent, component, new EditorPanePopupHandler((JEditorPane) component));
                } else if (component instanceof JTextComponent) {
                    activateMousePopup(mouseEvent, component, new TextComponentPopupHandler((JTextComponent) component));
                } else if (component instanceof JList) {
                    activateMousePopup(mouseEvent, component, new ListPopupHandler((JList<?>) component));
                } else if (component instanceof JTable) {
                    activateMousePopup(mouseEvent, component, new TablePopupHandler((JTable) component));
                }
            }
        } else if (event.getID() == KeyEvent.KEY_PRESSED) {
            KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.getKeyCode() == KeyEvent.VK_F12 && keyEvent.isShiftDown() && keyEvent.isAltDown() && (keyEvent.isControlDown() || keyEvent.isMetaDown())) {
                // Unable to infer component from mouse position, so simulate click instead
                inspectMode = true;
                try {
                    Robot robot = new Robot();
                    Point location = MouseInfo.getPointerInfo().getLocation();
                    robot.mouseMove(location.x, location.y);
                } catch (AWTException ex) {
                    Logger.getLogger(NetBeansPopupMenu.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_CONTEXT_MENU || (keyEvent.getKeyCode() == KeyEvent.VK_F10 && keyEvent.isShiftDown())) {
                if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0) {
                    // Menu was already created
                    return;
                }

                for (ComponentPopupEventDispatcher dispatcher : clipboardEventDispatchers) {
                    if (dispatcher.dispatchKeyEvent(keyEvent)) {
                        return;
                    }
                }

                Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

                if (component instanceof JEditorPane) {
                    Point point;
                    try {
                        Rectangle relativeRect = ((JEditorPane) component).modelToView(((JTextComponent) component).getCaretPosition());
                        point = relativeRect == null ? null : new Point(relativeRect.x + relativeRect.width, relativeRect.y + relativeRect.height);
                    } catch (BadLocationException ex) {
                        point = null;
                    }
                    activateKeyPopup(component, point, new EditorPanePopupHandler((JEditorPane) component));
                } else if (component instanceof JTextComponent) {
                    Point point;
                    try {
                        Rectangle relativeRect = ((JTextComponent) component).modelToView(((JTextComponent) component).getCaretPosition());
                        point = relativeRect == null ? null : new Point(relativeRect.x + relativeRect.width, relativeRect.y + relativeRect.height);
                    } catch (BadLocationException ex) {
                        point = null;
                    }
                    activateKeyPopup(component, point, new TextComponentPopupHandler((JTextComponent) component));
                } else if (component instanceof JList) {
                    Point point = null;
                    int selectedIndex = ((JList<?>) component).getSelectedIndex();
                    if (selectedIndex >= 0) {
                        Rectangle cellBounds = ((JList<?>) component).getCellBounds(selectedIndex, selectedIndex);
                        point = new Point(component.getWidth() / 2, cellBounds.y);
                    }
                    activateKeyPopup(component, point, new ListPopupHandler((JList<?>) component));
                } else if (component instanceof JTable) {
                    Point point = null;
                    int selectedRow = ((JTable) component).getSelectedRow();
                    if (selectedRow >= 0) {
                        int selectedColumn = ((JTable) component).getSelectedColumn();
                        if (selectedColumn < -1) {
                            selectedColumn = 0;
                        }
                        Rectangle cellBounds = ((JTable) component).getCellRect(selectedRow, selectedColumn, false);
                        point = new Point(cellBounds.x, cellBounds.y);
                    }
                    activateKeyPopup(component, point, new TablePopupHandler((JTable) component));
                }
            }
        }
    }
}
