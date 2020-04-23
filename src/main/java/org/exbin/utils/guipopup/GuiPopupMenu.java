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
package org.exbin.utils.guipopup;

import org.exbin.utils.guipopup.handler.TextComponentClipboardHandler;
import org.exbin.utils.guipopup.handler.ListClipboardHandler;
import org.exbin.utils.guipopup.handler.TableClipboardHandler;
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
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import org.exbin.utils.guipopup.panel.InspectComponentPanel;
import org.openide.windows.WindowManager;

/**
 * Default menu generation.
 *
 * @version 0.1.0 2019/07/22
 * @author ExBin Project (http://exbin.org)
 */
public class GuiPopupMenu {

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(GuiPopupMenu.class);

    private ActionMap defaultTextActionMap;
    private JPopupMenu defaultPopupMenu;
    private JPopupMenu defaultEditPopupMenu;
    private DefaultPopupClipboardAction defaultCutAction;
    private DefaultPopupClipboardAction defaultCopyAction;
    private DefaultPopupClipboardAction defaultPasteAction;
    private DefaultPopupClipboardAction defaultDeleteAction;
    private DefaultPopupClipboardAction defaultSelectAllAction;
    private DefaultPopupClipboardAction[] defaultTextActions;

    private static GuiPopupMenu instance = null;
    private boolean registered = false;
    private boolean inspectMode = false;
    private EventQueue systemEventQueue;
    private EventQueue overriddenQueue;

    private GuiPopupMenu() {
    }

    public static synchronized GuiPopupMenu getInstance() {
        if (instance == null) {
            instance = new GuiPopupMenu();
        }

        return instance;
    }

    /**
     * Registers default popup menu to AWT.
     */
    public static void register() {
        GuiPopupMenu defaultPopupMenu = getInstance();
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
        GuiPopupMenu defaultPopupMenu = getInstance();
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
        GuiPopupMenu defaultPopupMenu = getInstance();
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

    private void initDefaultPopupMenu(ResourceBundle resourceBundle, Class resourceClass) {
        defaultTextActionMap = new ActionMap();
        defaultCutAction = new DefaultPopupClipboardAction(DefaultEditorKit.cutAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performCut();
            }

            @Override
            protected void postTextComponentInitialize() {
                setEnabled(clipboardHandler.isEditable() && clipboardHandler.isSelection());
            }
        };
        ActionUtils.setupAction(defaultCutAction, resourceBundle, resourceClass, "editCutAction");
        defaultCutAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, ActionUtils.getMetaMask()));
        defaultCutAction.setEnabled(false);
        defaultTextActionMap.put(TransferHandler.getCutAction().getValue(Action.NAME), defaultCutAction);

        defaultCopyAction = new DefaultPopupClipboardAction(DefaultEditorKit.copyAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performCopy();
            }

            @Override
            protected void postTextComponentInitialize() {
                setEnabled(clipboardHandler.isSelection());
            }
        };
        ActionUtils.setupAction(defaultCopyAction, resourceBundle, resourceClass, "editCopyAction");
        defaultCopyAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, ActionUtils.getMetaMask()));
        defaultCopyAction.setEnabled(false);
        defaultTextActionMap.put(TransferHandler.getCopyAction().getValue(Action.NAME), defaultCopyAction);

        defaultPasteAction = new DefaultPopupClipboardAction(DefaultEditorKit.pasteAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performPaste();
            }

            @Override
            protected void postTextComponentInitialize() {
                setEnabled(clipboardHandler.isEditable());
            }
        };
        ActionUtils.setupAction(defaultPasteAction, resourceBundle, resourceClass, "editPasteAction");
        defaultPasteAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, ActionUtils.getMetaMask()));
        defaultPasteAction.setEnabled(false);
        defaultTextActionMap.put(TransferHandler.getPasteAction().getValue(Action.NAME), defaultPasteAction);

        defaultDeleteAction = new DefaultPopupClipboardAction(DefaultEditorKit.deleteNextCharAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performDelete();
            }

            @Override
            protected void postTextComponentInitialize() {
                setEnabled(clipboardHandler.isEditable() && clipboardHandler.isSelection());
            }
        };
        ActionUtils.setupAction(defaultDeleteAction, resourceBundle, resourceClass, "editDeleteAction");
        defaultDeleteAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        defaultDeleteAction.setEnabled(false);
        defaultTextActionMap.put("delete", defaultDeleteAction);

        defaultSelectAllAction = new DefaultPopupClipboardAction(DefaultEditorKit.selectAllAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performSelectAll();
            }

            @Override
            protected void postTextComponentInitialize() {
                setEnabled(clipboardHandler.canSelectAll());
            }
        };
        ActionUtils.setupAction(defaultSelectAllAction, resourceBundle, resourceClass, "editSelectAllAction");
        defaultSelectAllAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, ActionUtils.getMetaMask()));
        defaultTextActionMap.put("selectAll", defaultSelectAllAction);

        DefaultPopupClipboardAction[] actions = {defaultCutAction, defaultCopyAction, defaultPasteAction, defaultDeleteAction, defaultSelectAllAction};
        defaultTextActions = actions;

        buildDefaultPopupMenu();
        buildDefaultEditPopupMenu();
    }

    private void buildDefaultPopupMenu() {
        defaultPopupMenu = new JPopupMenu();

        defaultPopupMenu.setName("defaultPopupMenu"); // NOI18N
        fillDefaultPopupMenu(defaultPopupMenu, -1);
    }

    private void buildDefaultEditPopupMenu() {
        defaultEditPopupMenu = new JPopupMenu();

        defaultEditPopupMenu.setName("defaultEditPopupMenu"); // NOI18N
        fillDefaultEditPopupMenu(defaultEditPopupMenu, -1);
    }

    public void fillDefaultPopupMenu(JPopupMenu popupMenu, int position) {
        JMenuItem basicPopupCopyMenuItem = new javax.swing.JMenuItem();
        JMenuItem basicPopupSelectAllMenuItem = new javax.swing.JMenuItem();

        basicPopupCopyMenuItem.setAction(defaultCopyAction);
        basicPopupCopyMenuItem.setName("basicEditPopupCopyMenuItem"); // NOI18N
        basicPopupSelectAllMenuItem.setAction(defaultSelectAllAction);
        basicPopupSelectAllMenuItem.setName("basicEditPopupSelectAllMenuItem"); // NOI18N

        if (position >= 0) {
            popupMenu.insert(basicPopupCopyMenuItem, position);
            popupMenu.insert(new JPopupMenu.Separator(), position + 1);
            popupMenu.insert(basicPopupSelectAllMenuItem, position + 2);
        } else {
            popupMenu.add(basicPopupCopyMenuItem);
            popupMenu.addSeparator();
            popupMenu.add(basicPopupSelectAllMenuItem);
        }
    }

    public void fillDefaultEditPopupMenu(JPopupMenu popupMenu, int position) {
        JMenuItem basicPopupCutMenuItem = new javax.swing.JMenuItem();
        JMenuItem basicPopupCopyMenuItem = new javax.swing.JMenuItem();
        JMenuItem basicPopupPasteMenuItem = new javax.swing.JMenuItem();
        JMenuItem basicPopupDeleteMenuItem = new javax.swing.JMenuItem();
        JMenuItem basicPopupSelectAllMenuItem = new javax.swing.JMenuItem();

        basicPopupCutMenuItem.setAction(defaultCutAction);
        basicPopupCutMenuItem.setName("basicPopupCutMenuItem");
        basicPopupCopyMenuItem.setAction(defaultCopyAction);
        basicPopupCopyMenuItem.setName("basicPopupCopyMenuItem");
        basicPopupPasteMenuItem.setAction(defaultPasteAction);
        basicPopupPasteMenuItem.setName("basicPopupPasteMenuItem");
        basicPopupDeleteMenuItem.setAction(defaultDeleteAction);
        basicPopupDeleteMenuItem.setName("basicPopupDeleteMenuItem");
        basicPopupSelectAllMenuItem.setAction(defaultSelectAllAction);
        basicPopupSelectAllMenuItem.setName("basicPopupSelectAllMenuItem");

        if (position >= 0) {
            popupMenu.insert(basicPopupCutMenuItem, position);
            popupMenu.insert(basicPopupCopyMenuItem, position + 1);
            popupMenu.insert(basicPopupPasteMenuItem, position + 2);
            popupMenu.insert(basicPopupDeleteMenuItem, position + 3);
            popupMenu.insert(new JPopupMenu.Separator(), position + 4);
            popupMenu.insert(basicPopupSelectAllMenuItem, position + 5);
        } else {
            popupMenu.add(basicPopupCutMenuItem);
            popupMenu.add(basicPopupCopyMenuItem);
            popupMenu.add(basicPopupPasteMenuItem);
            popupMenu.add(basicPopupDeleteMenuItem);
            popupMenu.addSeparator();
            popupMenu.add(basicPopupSelectAllMenuItem);
        }
    }

    // TODO extend org.netbeans.core.TimableEventQueue
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
                inspectComponentPanel.setCloseActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.close();
                    }
                });
                dialog.show();
                return;
            }
            super.dispatchEvent(event);

            if (event.getID() == MouseEvent.MOUSE_RELEASED || event.getID() == MouseEvent.MOUSE_PRESSED) {
                MouseEvent mouseEvent = (MouseEvent) event;

                if (mouseEvent.isPopupTrigger()) {
                    if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0) {
                        // Menu was already created
                        return;
                    }

                    Component component = getSource(mouseEvent);
                    if (component instanceof JViewport) {
                        component = ((JViewport) component).getView();
                    }

                    if (component instanceof JTextComponent) {
                        activateMousePopup(mouseEvent, component, new TextComponentClipboardHandler((JTextComponent) component));
                    } else if (component instanceof JList) {
                        activateMousePopup(mouseEvent, component, new ListClipboardHandler((JList) component));
                    } else if (component instanceof JTable) {
                        activateMousePopup(mouseEvent, component, new TableClipboardHandler((JTable) component));
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
                        Logger.getLogger(GuiPopupMenu.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (keyEvent.getKeyCode() == KeyEvent.VK_CONTEXT_MENU || (keyEvent.getKeyCode() == KeyEvent.VK_F10 && keyEvent.isShiftDown())) {
                    if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0) {
                        // Menu was already created
                        return;
                    }

                    Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

                    if (component instanceof JTextComponent) {
                        Point point;
                        try {
                            Rectangle relativeRect = ((JTextComponent) component).modelToView(((JTextComponent) component).getCaretPosition());
                            point = relativeRect == null ? null : new Point(relativeRect.x + relativeRect.width, relativeRect.y + relativeRect.height);
                        } catch (BadLocationException ex) {
                            point = null;
                        }
                        activateKeyPopup(component, point, new TextComponentClipboardHandler((JTextComponent) component));
                    } else if (component instanceof JList) {
                        Point point = null;
                        int selectedIndex = ((JList) component).getSelectedIndex();
                        if (selectedIndex >= 0) {
                            Rectangle cellBounds = ((JList) component).getCellBounds(selectedIndex, selectedIndex);
                            point = new Point(component.getWidth() / 2, cellBounds.y);
                        }
                        activateKeyPopup(component, point, new ListClipboardHandler((JList) component));
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
                        activateKeyPopup(component, point, new TableClipboardHandler((JTable) component));
                    }
                }
            }
        }

//        private Component findComponentByMousePosition() {
//            SwingUtilities.getDeepestComponentAt(mainWindow, mouseLocation.x, mouseLocation.y);
//            Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
////            Frame mainWindow = WindowManager.getDefault().getMainWindow();
//            Frame mainWindow = WindowManager.getDefault().getMainWindow();
//            Component.findUnderMouseInWindow
//            List<Window> windows = Window.getWindows();
//            for (Window window : windows) {
//                Window owner = window.getOwner();
//                SwingUtilities.getDeepestComponentAt(window., mouseLocation.x, mouseLocation.y);
//            }
//
//        }
        private void activateMousePopup(MouseEvent mouseEvent, Component component, ClipboardActionsHandler clipboardHandler) {
            for (Object action : defaultTextActions) {
                ((DefaultPopupClipboardAction) action).setClipboardHandler(clipboardHandler);
            }

            Point point = mouseEvent.getLocationOnScreen();
            Point locationOnScreen = component.getLocationOnScreen();
            point.translate(-locationOnScreen.x, -locationOnScreen.y);

            showPopupMenu(component, point, clipboardHandler.isEditable());
        }

        private void activateKeyPopup(Component component, Point point, ClipboardActionsHandler clipboardHandler) {
            for (Object action : defaultTextActions) {
                ((DefaultPopupClipboardAction) action).setClipboardHandler(clipboardHandler);
            }

            if (point == null) {
                if (component.getParent() instanceof ScrollPane) {
                    // TODO
                    point = new Point(component.getWidth() / 2, component.getHeight() / 2);
                } else {
                    point = new Point(component.getWidth() / 2, component.getHeight() / 2);
                }
            }

            showPopupMenu(component, point, clipboardHandler.isEditable());
        }

        private void showPopupMenu(Component component, Point point, boolean editable) {
            if (editable) {
                defaultEditPopupMenu.show(component, (int) point.getX(), (int) point.getY());
            } else {
                defaultPopupMenu.show(component, (int) point.getX(), (int) point.getY());
            }
        }
    }

    private static Component getSource(MouseEvent e) {
        return SwingUtilities.getDeepestComponentAt(e.getComponent(), e.getX(), e.getY());
    }

    /**
     * Clipboard action for default popup menu.
     */
    private static abstract class DefaultPopupClipboardAction extends AbstractAction {

        protected ClipboardActionsHandler clipboardHandler;

        public DefaultPopupClipboardAction(String name) {
            super(name);
        }

        public void setClipboardHandler(ClipboardActionsHandler clipboardHandler) {
            this.clipboardHandler = clipboardHandler;
            postTextComponentInitialize();
        }

        protected abstract void postTextComponentInitialize();
    }
}
