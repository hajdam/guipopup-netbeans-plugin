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
package org.exbin.framework.popup;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JMenu;
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
import org.exbin.framework.popup.handler.EditorPanePopupHandler;
import org.exbin.framework.popup.handler.ListPopupHandler;
import org.exbin.framework.popup.handler.TablePopupHandler;
import org.exbin.framework.popup.handler.TextComponentPopupHandler;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.utils.ComponentPopupEventDispatcher;
import org.exbin.framework.utils.LanguageUtils;

/**
 * Utilities for default menu generation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultPopupMenu {

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(DefaultPopupMenu.class);

    public static final String DELETE_ACTION = "delete";
    public static final String SELECT_ALL_ACTION = "selectAll";

    public static final String POPUP_COPY_ACTION_ID = "popupCopyAction";
    public static final String POPUP_COPY_TEXT_ACTION_ID = "popupCopyTextAction";
    public static final String POPUP_COPY_LINK_ACTION_ID = "popupCopyLinkAction";
    public static final String POPUP_COPY_IMAGE_ACTION_ID = "popupCopyImageAction";
    public static final String POPUP_CUT_ACTION_ID = "popupCutAction";
    public static final String POPUP_PASTE_ACTION_ID = "popupPasteAction";
    public static final String POPUP_DELETE_ACTION_ID = "popupDeleteAction";
    public static final String POPUP_SELECT_ALL_ACTION_ID = "popupSelectAllAction";
    public static final String POPUP_OPEN_LINK_ACTION_ID = "popupOpenLinkAction";
    public static final String POPUP_COPY_TEXT_ACTION_NAME = "copy-text";
    public static final String POPUP_COPY_LINK_ACTION_NAME = "copy-link";
    public static final String POPUP_COPY_IMAGE_ACTION_NAME = "copy-image";
    public static final String POPUP_OPEN_LINK_ACTION_NAME = "open-link";

    protected ActionMap defaultTextActionMap;
    protected DefaultPopupClipboardAction[] defaultTextActions;
    protected DefaultPopupClipboardAction defaultCutAction;
    protected DefaultPopupClipboardAction defaultCopyAction;
    protected DefaultPopupClipboardAction defaultPasteAction;
    protected DefaultPopupClipboardAction defaultDeleteAction;
    protected DefaultPopupClipboardAction defaultSelectAllAction;
    protected DefaultPopupClipboardAction copyTextAction;
    protected DefaultPopupClipboardAction copyLinkAction;
    protected DefaultPopupClipboardAction openLinkAction;
    protected DefaultPopupClipboardAction copyImageAction;

    protected final List<ComponentPopupEventDispatcher> clipboardEventDispatchers = new ArrayList<>();

    private static DefaultPopupMenu instance = null;

    protected DefaultPopupMenu() {
    }

    @Nonnull
    public static synchronized DefaultPopupMenu getInstance() {
        if (instance == null) {
            instance = new DefaultPopupMenu();
        }

        return instance;
    }

    /**
     * Registers default popup menu to AWT.
     */
    public static void register() {
        DefaultPopupMenu defaultPopupMenu = getInstance();
        defaultPopupMenu.initDefaultPopupMenu();
        defaultPopupMenu.registerToEventQueue();
    }

    /**
     * Registers default popup menu to AWT.
     *
     * @param resourceBundle resource bundle
     * @param resourceClass resource class
     */
    public static void register(ResourceBundle resourceBundle, Class<?> resourceClass) {
        DefaultPopupMenu defaultPopupMenu = getInstance();
        defaultPopupMenu.initDefaultPopupMenu(resourceBundle, resourceClass);
        defaultPopupMenu.registerToEventQueue();
    }

    public void appendTextMenu(JMenu menu, final TextActionsHandler handler) {
        menu.add(createCopyTextMenuAction(handler));
    }

    public void appendTextMenu(JPopupMenu menu, final TextActionsHandler handler) {
        menu.add(createCopyTextMenuAction(handler));
    }

    @Nonnull
    public Action createCopyTextMenuAction(final TextActionsHandler handler) {
        Class<? extends DefaultPopupMenu> resourceClass = DefaultPopupMenu.class;
        Action copyImageMenuAction = new AbstractAction(POPUP_COPY_TEXT_ACTION_NAME) {
            @Override
            public void actionPerformed(ActionEvent e) {
                handler.performCopyText();
            }
        };
        ActionUtils.setupAction(copyImageMenuAction, resourceBundle, resourceClass, POPUP_COPY_TEXT_ACTION_ID);
        copyImageMenuAction.setEnabled(handler.isTextSelected());
        return copyImageMenuAction;
    }

    public void appendLinkMenu(JMenu menu, final LinkActionsHandler handler) {
        menu.add(createCopyLinkMenuAction(handler));
        menu.add(createOpenLinkMenuAction(handler));
    }

    public void appendLinkMenu(JPopupMenu menu, final LinkActionsHandler handler) {
        menu.add(createCopyLinkMenuAction(handler));
        menu.add(createOpenLinkMenuAction(handler));
    }

    @Nonnull
    public Action createCopyLinkMenuAction(final LinkActionsHandler handler) {
        Class<? extends DefaultPopupMenu> resourceClass = DefaultPopupMenu.class;
        Action copyLinkMenuAction = new AbstractAction(POPUP_COPY_LINK_ACTION_NAME) {
            @Override
            public void actionPerformed(ActionEvent e) {
                handler.performCopyLink();
            }
        };
        ActionUtils.setupAction(copyLinkMenuAction, resourceBundle, resourceClass, POPUP_COPY_LINK_ACTION_ID);
        copyLinkMenuAction.setEnabled(handler.isLinkSelected());
        return copyLinkMenuAction;
    }

    @Nonnull
    public Action createOpenLinkMenuAction(final LinkActionsHandler handler) {
        Class<? extends DefaultPopupMenu> resourceClass = DefaultPopupMenu.class;
        Action openLinkMenuAction = new DefaultPopupClipboardAction(POPUP_OPEN_LINK_ACTION_NAME) {
            @Override
            public void actionPerformed(ActionEvent e) {
                handler.performOpenLink();
            }
        };
        ActionUtils.setupAction(openLinkMenuAction, resourceBundle, resourceClass, POPUP_OPEN_LINK_ACTION_ID);
        openLinkMenuAction.setEnabled(handler.isLinkSelected());
        return openLinkMenuAction;
    }

    public void appendImageMenu(JMenu menu, final ImageActionsHandler handler) {
        menu.add(createCopyImageMenuAction(handler));
    }

    public void appendImageMenu(JPopupMenu menu, final ImageActionsHandler handler) {
        menu.add(createCopyImageMenuAction(handler));
    }

    @Nonnull
    public Action createCopyImageMenuAction(final ImageActionsHandler handler) {
        Class<? extends DefaultPopupMenu> resourceClass = DefaultPopupMenu.class;
        Action copyImageMenuAction = new AbstractAction(POPUP_COPY_IMAGE_ACTION_NAME) {
            @Override
            public void actionPerformed(ActionEvent e) {
                handler.performCopyImage();
            }
        };
        ActionUtils.setupAction(copyImageMenuAction, resourceBundle, resourceClass, POPUP_COPY_IMAGE_ACTION_ID);
        copyImageMenuAction.setEnabled(handler.isImageSelected());
        return copyImageMenuAction;
    }

    private void registerToEventQueue() {
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new PopupEventQueue());
    }

    private void initDefaultPopupMenu() {
        initDefaultPopupMenu(resourceBundle, this.getClass());
    }

    protected void initDefaultPopupMenu(ResourceBundle resourceBundle, Class<?> resourceClass) {
        defaultTextActionMap = new ActionMap();
        defaultCutAction = new DefaultPopupClipboardAction(DefaultEditorKit.cutAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performCut();
            }

            @Override
            public void updateFor(ClipboardActionsHandler clipboardHandler, @Nullable MouseEvent mouseEvent) {
                super.updateFor(clipboardHandler, mouseEvent);
                setEnabled(clipboardHandler.isEditable() && clipboardHandler.isSelection());
            }
        };
        ActionUtils.setupAction(defaultCutAction, resourceBundle, resourceClass, POPUP_CUT_ACTION_ID);
        defaultCutAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, ActionUtils.getMetaMask()));
        defaultCutAction.setEnabled(false);
        defaultTextActionMap.put(TransferHandler.getCutAction().getValue(Action.NAME), defaultCutAction);

        defaultCopyAction = new DefaultPopupClipboardAction(DefaultEditorKit.copyAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performCopy();
            }

            @Override
            public void updateFor(ClipboardActionsHandler clipboardHandler, @Nullable MouseEvent mouseEvent) {
                super.updateFor(clipboardHandler, mouseEvent);
                setEnabled(clipboardHandler.isSelection());
            }
        };
        ActionUtils.setupAction(defaultCopyAction, resourceBundle, resourceClass, POPUP_COPY_ACTION_ID);
        defaultCopyAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, ActionUtils.getMetaMask()));
        defaultCopyAction.setEnabled(false);
        defaultTextActionMap.put(TransferHandler.getCopyAction().getValue(Action.NAME), defaultCopyAction);

        defaultPasteAction = new DefaultPopupClipboardAction(DefaultEditorKit.pasteAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performPaste();
            }

            @Override
            public void updateFor(ClipboardActionsHandler clipboardHandler, @Nullable MouseEvent mouseEvent) {
                super.updateFor(clipboardHandler, mouseEvent);
                setEnabled(clipboardHandler.isEditable());
            }
        };
        ActionUtils.setupAction(defaultPasteAction, resourceBundle, resourceClass, POPUP_PASTE_ACTION_ID);
        defaultPasteAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, ActionUtils.getMetaMask()));
        defaultPasteAction.setEnabled(false);
        defaultTextActionMap.put(TransferHandler.getPasteAction().getValue(Action.NAME), defaultPasteAction);

        defaultDeleteAction = new DefaultPopupClipboardAction(DefaultEditorKit.deleteNextCharAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performDelete();
            }

            @Override
            public void updateFor(ClipboardActionsHandler clipboardHandler, @Nullable MouseEvent mouseEvent) {
                super.updateFor(clipboardHandler, mouseEvent);
                setEnabled(clipboardHandler.canDelete() && clipboardHandler.isSelection());
            }
        };
        ActionUtils.setupAction(defaultDeleteAction, resourceBundle, resourceClass, POPUP_DELETE_ACTION_ID);
        defaultDeleteAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        defaultDeleteAction.setEnabled(false);
        defaultTextActionMap.put(DELETE_ACTION, defaultDeleteAction);

        defaultSelectAllAction = new DefaultPopupClipboardAction(DefaultEditorKit.selectAllAction) {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardHandler.performSelectAll();
            }

            @Override
            public void updateFor(ClipboardActionsHandler clipboardHandler, @Nullable MouseEvent mouseEvent) {
                super.updateFor(clipboardHandler, mouseEvent);
                setEnabled(clipboardHandler.canSelectAll());
            }
        };
        ActionUtils.setupAction(defaultSelectAllAction, resourceBundle, resourceClass, POPUP_SELECT_ALL_ACTION_ID);
        defaultSelectAllAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, ActionUtils.getMetaMask()));
        defaultTextActionMap.put(SELECT_ALL_ACTION, defaultSelectAllAction);

        defaultTextActions = new DefaultPopupClipboardAction[]{defaultCutAction, defaultCopyAction, defaultPasteAction, defaultDeleteAction, defaultSelectAllAction};

        copyTextAction = new DefaultPopupClipboardAction(POPUP_COPY_TEXT_ACTION_NAME) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mouseEvent != null && clipboardHandler instanceof PositionTextActionsHandler) {
                    ((PositionTextActionsHandler) clipboardHandler).performCopyText(mouseEvent.getLocationOnScreen());
                } else {
                    ((TextActionsHandler) clipboardHandler).performCopyText();
                }
            }

            @Override
            public void updateFor(ClipboardActionsHandler clipboardHandler, @Nullable MouseEvent mouseEvent) {
                super.updateFor(clipboardHandler, mouseEvent);
                boolean updateEnabled;
                if (mouseEvent != null && clipboardHandler instanceof PositionTextActionsHandler) {
                    updateEnabled = ((PositionTextActionsHandler) clipboardHandler).isTextSelected(mouseEvent.getLocationOnScreen());
                } else {
                    updateEnabled = clipboardHandler instanceof TextActionsHandler && ((TextActionsHandler) clipboardHandler).isTextSelected();
                }
                setEnabled(updateEnabled);
            }
        };
        ActionUtils.setupAction(copyTextAction, resourceBundle, resourceClass, POPUP_COPY_TEXT_ACTION_ID);
        copyLinkAction = new DefaultPopupClipboardAction(POPUP_COPY_LINK_ACTION_NAME) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mouseEvent != null && clipboardHandler instanceof PositionLinkActionsHandler) {
                    ((PositionLinkActionsHandler) clipboardHandler).performCopyLink(mouseEvent.getLocationOnScreen());
                } else {
                    ((LinkActionsHandler) clipboardHandler).performCopyLink();
                }
            }

            @Override
            public void updateFor(ClipboardActionsHandler clipboardHandler, @Nullable MouseEvent mouseEvent) {
                super.updateFor(clipboardHandler, mouseEvent);
                boolean updateEnabled;
                if (mouseEvent != null && clipboardHandler instanceof PositionLinkActionsHandler) {
                    updateEnabled = ((PositionLinkActionsHandler) clipboardHandler).isLinkSelected(mouseEvent.getLocationOnScreen());
                } else {
                    updateEnabled = clipboardHandler instanceof LinkActionsHandler && ((LinkActionsHandler) clipboardHandler).isLinkSelected();
                }
                setEnabled(updateEnabled);
            }
        };
        ActionUtils.setupAction(copyLinkAction, resourceBundle, resourceClass, POPUP_COPY_LINK_ACTION_ID);
        openLinkAction = new DefaultPopupClipboardAction(POPUP_OPEN_LINK_ACTION_NAME) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mouseEvent != null && clipboardHandler instanceof PositionLinkActionsHandler) {
                    ((PositionLinkActionsHandler) clipboardHandler).performOpenLink(mouseEvent.getLocationOnScreen());
                } else {
                    ((LinkActionsHandler) clipboardHandler).performOpenLink();
                }
            }

            @Override
            public void updateFor(ClipboardActionsHandler clipboardHandler, @Nullable MouseEvent mouseEvent) {
                super.updateFor(clipboardHandler, mouseEvent);
                boolean updateEnabled;
                if (mouseEvent != null && clipboardHandler instanceof PositionLinkActionsHandler) {
                    updateEnabled = ((PositionLinkActionsHandler) clipboardHandler).isLinkSelected(mouseEvent.getLocationOnScreen());
                } else {
                    updateEnabled = clipboardHandler instanceof LinkActionsHandler && ((LinkActionsHandler) clipboardHandler).isLinkSelected();
                }
                setEnabled(updateEnabled);
            }
        };
        ActionUtils.setupAction(openLinkAction, resourceBundle, resourceClass, POPUP_OPEN_LINK_ACTION_ID);
        copyImageAction = new DefaultPopupClipboardAction(POPUP_COPY_IMAGE_ACTION_NAME) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mouseEvent != null && clipboardHandler instanceof PositionImageActionsHandler) {
                    ((PositionImageActionsHandler) clipboardHandler).performCopyImage(mouseEvent.getLocationOnScreen());
                } else {
                    ((ImageActionsHandler) clipboardHandler).performCopyImage();
                }
            }

            @Override
            public void updateFor(ClipboardActionsHandler clipboardHandler, @Nullable MouseEvent mouseEvent) {
                super.updateFor(clipboardHandler, mouseEvent);
                boolean updateEnabled;
                if (mouseEvent != null && clipboardHandler instanceof PositionImageActionsHandler) {
                    updateEnabled = ((PositionImageActionsHandler) clipboardHandler).isImageSelected(mouseEvent.getLocationOnScreen());
                } else {
                    updateEnabled = clipboardHandler instanceof ImageActionsHandler && ((ImageActionsHandler) clipboardHandler).isImageSelected();
                }
                setEnabled(updateEnabled);
            }
        };
        ActionUtils.setupAction(copyImageAction, resourceBundle, resourceClass, POPUP_COPY_IMAGE_ACTION_ID);
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

    public void addClipboardEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        clipboardEventDispatchers.add(dispatcher);
    }

    public void removeClipboardEventDispatcher(ComponentPopupEventDispatcher dispatcher) {
        clipboardEventDispatchers.remove(dispatcher);
    }

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
            if (keyEvent.getKeyCode() == KeyEvent.VK_CONTEXT_MENU || (keyEvent.getKeyCode() == KeyEvent.VK_F10 && keyEvent.isShiftDown())) {
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

    protected void activateMousePopup(MouseEvent mouseEvent, Component component, ClipboardActionsHandler clipboardHandler) {
        for (DefaultPopupClipboardAction action : defaultTextActions) {
            action.updateFor(clipboardHandler, mouseEvent);
            copyTextAction.updateFor(clipboardHandler, mouseEvent);
            copyLinkAction.updateFor(clipboardHandler, mouseEvent);
            openLinkAction.updateFor(clipboardHandler, mouseEvent);
            copyImageAction.updateFor(clipboardHandler, mouseEvent);
        }

        Point point = mouseEvent.getLocationOnScreen();
        Point locationOnScreen = component.getLocationOnScreen();
        point.translate(-locationOnScreen.x, -locationOnScreen.y);

        showPopupMenu(component, point, clipboardHandler);
    }

    protected void activateKeyPopup(Component component, @Nullable Point point, ClipboardActionsHandler clipboardHandler) {
        for (DefaultPopupClipboardAction action : defaultTextActions) {
            action.updateFor(clipboardHandler, null);
            copyTextAction.updateFor(clipboardHandler, null);
            copyLinkAction.updateFor(clipboardHandler, null);
            openLinkAction.updateFor(clipboardHandler, null);
            copyImageAction.updateFor(clipboardHandler, null);
        }

        if (point == null) {
            if (component.getParent() instanceof ScrollPane) {
                // TODO
                point = new Point(component.getWidth() / 2, component.getHeight() / 2);
            } else {
                point = new Point(component.getWidth() / 2, component.getHeight() / 2);
            }
        }

        showPopupMenu(component, point, clipboardHandler);
    }

    protected void showPopupMenu(Component component, Point point, ClipboardActionsHandler handler) {
        boolean editable = handler.isEditable();

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setName("defaultPopupMenu");

        boolean hasExtra = false;
        if (handler instanceof TextActionsHandler) {
            if (copyTextAction.isEnabled()) {
                JMenuItem copyTextMenuItem = new JMenuItem();
                copyTextMenuItem.setAction(copyTextAction);
                copyTextMenuItem.setName("basicCopyTextMenuItem");
                popupMenu.add(copyTextMenuItem);
                hasExtra = true;
            }
        }

        if (handler instanceof ImageActionsHandler) {
            if (copyImageAction.isEnabled()) {
                JMenuItem copyImageMenuItem = new JMenuItem();
                copyImageMenuItem.setAction(copyImageAction);
                copyImageMenuItem.setName("basicCopyImageMenuItem");
                popupMenu.add(copyImageMenuItem);
                hasExtra = true;
            }
        }

        if (handler instanceof LinkActionsHandler) {
            if (openLinkAction.isEnabled()) {
                JMenuItem openLinkMenuItem = new JMenuItem();
                openLinkMenuItem.setAction(openLinkAction);
                openLinkMenuItem.setName("basicOpenLinkMenuItem");
                popupMenu.add(openLinkMenuItem);
                hasExtra = true;
            }

            if (copyLinkAction.isEnabled()) {
                JMenuItem copyLinkMenuItem = new JMenuItem();
                copyLinkMenuItem.setAction(copyLinkAction);
                copyLinkMenuItem.setName("basicCopyLinkMenuItem");
                popupMenu.add(copyLinkMenuItem);
                hasExtra = true;
            }
        }

        if (hasExtra) {
            popupMenu.addSeparator();
        }

        if (editable) {
            fillDefaultEditPopupMenu(popupMenu, -1);
        } else {
            fillDefaultPopupMenu(popupMenu, -1);
        }

        popupMenu.show(component, (int) point.getX(), (int) point.getY());
        popupMenu.grabFocus();
    }

    @Nullable
    protected Component getSource(MouseEvent e) {
        return SwingUtilities.getDeepestComponentAt(e.getComponent(), e.getX(), e.getY());
    }

    @ParametersAreNonnullByDefault
    public class PopupEventQueue extends EventQueue {

        @Override
        protected void dispatchEvent(AWTEvent event) {
            super.dispatchEvent(event);

            processAWTEvent(event);
        }
    }

    /**
     * Clipboard action for default popup menu.
     */
    @ParametersAreNonnullByDefault
    protected static abstract class DefaultPopupClipboardAction extends AbstractAction {

        protected ClipboardActionsHandler clipboardHandler;
        protected MouseEvent mouseEvent;

        public DefaultPopupClipboardAction(String name) {
            super(name);
        }

        public void updateFor(ClipboardActionsHandler clipboardHandler, @Nullable MouseEvent mouseEvent) {
            this.clipboardHandler = clipboardHandler;
            this.mouseEvent = mouseEvent;
        }
    }
}
