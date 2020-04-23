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

import java.awt.AWTEvent;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.text.JTextComponent;
import org.openide.util.ImageUtilities;

/**
 * Some simple static methods usable for actions, menus and toolbars.
 *
 * @version 0.1.0 2019/07/18
 * @author ExBin Project (http://exbin.org)
 */
public class ActionUtils {

    public static final String DIALOG_MENUITEM_EXT = "...";

    /**
     * Action type like or check, radio.
     *
     * Value is ActionType.
     */
    public static final String ACTION_TYPE = "type";
    /**
     * Radio group name value.
     *
     * Value is String.
     */
    public static final String ACTION_RADIO_GROUP = "radioGroup";
    /**
     * Action mode for actions opening dialogs.
     *
     * Value is Boolean.
     */
    public static final String ACTION_DIALOG_MODE = "dialogMode";

    public static final String ACTION_ID = "actionId";
    public static final String ACTION_NAME_POSTFIX = ".text";
    public static final String ACTION_SHORT_DESCRIPTION_POSTFIX = ".shortDescription";
    public static final String ACTION_SMALL_ICON_POSTFIX = ".smallIcon";
    public static final String ACTION_SMALL_LARGE_POSTFIX = ".largeIcon";

    private ActionUtils() {
    }

    /**
     * Sets action values according to values specified by resource bundle.
     *
     * @param action modified action
     * @param bundle source bundle
     * @param actionId action identifier and bundle key prefix
     */
    public static void setupAction(Action action, ResourceBundle bundle, String actionId) {
        setupAction(action, bundle, action.getClass(), actionId);
    }

    /**
     * Sets action values according to values specified by resource bundle.
     *
     * @param action modified action
     * @param bundle source bundle
     * @param resourceClass resourceClass
     * @param actionId action identifier and bundle key prefix
     */
    public static void setupAction(Action action, ResourceBundle bundle, Class resourceClass, String actionId) {
        action.putValue(Action.NAME, bundle.getString(actionId + ACTION_NAME_POSTFIX));
        action.putValue(ACTION_ID, actionId);

        // TODO keystroke from string with meta mask translation
        if (bundle.containsKey(actionId + ACTION_SHORT_DESCRIPTION_POSTFIX)) {
            action.putValue(Action.SHORT_DESCRIPTION, bundle.getString(actionId + ACTION_SHORT_DESCRIPTION_POSTFIX));
        }
        if (bundle.containsKey(actionId + ACTION_SMALL_ICON_POSTFIX)) {
            action.putValue(Action.SMALL_ICON, ImageUtilities.loadImageIcon(bundle.getString(actionId + ACTION_SMALL_ICON_POSTFIX), true));
        }
        if (bundle.containsKey(actionId + ACTION_SMALL_LARGE_POSTFIX)) {
            action.putValue(Action.LARGE_ICON_KEY, ImageUtilities.loadImageIcon(bundle.getString(actionId + ACTION_SMALL_LARGE_POSTFIX), true));
        }
    }

    /**
     * Returns platform specific down mask filter.
     *
     * @return down mask for meta keys
     */
    @SuppressWarnings("deprecation")
    public static int getMetaMask() {
        try {
            switch (java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) {
                case Event.CTRL_MASK:
                    return KeyEvent.CTRL_DOWN_MASK;
                case Event.META_MASK:
                    return KeyEvent.META_DOWN_MASK;
                case Event.SHIFT_MASK:
                    return KeyEvent.SHIFT_DOWN_MASK;
                case Event.ALT_MASK:
                    return KeyEvent.ALT_DOWN_MASK;
                default:
                    return KeyEvent.CTRL_DOWN_MASK;
            }
        } catch (java.awt.HeadlessException ex) {
            return KeyEvent.CTRL_DOWN_MASK;
        }
    }

    /**
     * Invokes action of given name on text component.
     *
     * @param textComponent component
     * @param actionName action name
     */
    public static void invokeTextAction(JTextComponent textComponent, String actionName) {
        ActionMap textActionMap = textComponent.getActionMap().getParent();
        long eventTime = EventQueue.getMostRecentEventTime();
        int eventMods = getCurrentEventModifiers();
        ActionEvent actionEvent = new ActionEvent(textComponent, ActionEvent.ACTION_PERFORMED, actionName, eventTime, eventMods);
        textActionMap.get(actionName).actionPerformed(actionEvent);
    }

    /**
     * This method was lifted from JTextComponent.java.
     */
    private static int getCurrentEventModifiers() {
        int modifiers = 0;
        AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof InputEvent) {
            modifiers = ((InputEvent) currentEvent).getModifiers();
        } else if (currentEvent instanceof ActionEvent) {
            modifiers = ((ActionEvent) currentEvent).getModifiers();
        }
        return modifiers;
    }

    /**
     * Enumeration of action types.
     */
    public enum ActionType {
        /**
         * Single click / activation action.
         */
        PUSH,
        /**
         * Checkbox type action.
         */
        CHECK,
        /**
         * Radion type checking, where only one item in radio group can be
         * checked.
         */
        RADIO
    }
}
