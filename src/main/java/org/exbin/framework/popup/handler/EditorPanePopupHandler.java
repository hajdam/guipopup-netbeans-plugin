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
package org.exbin.framework.popup.handler;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TextUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import org.exbin.framework.popup.ImageActionsHandler;
import org.exbin.framework.popup.LinkActionsHandler;
import org.exbin.framework.popup.PositionImageActionsHandler;
import org.exbin.framework.popup.PositionLinkActionsHandler;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.DesktopUtils;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.utils.ClipboardActionsUpdateListener;
import org.exbin.framework.utils.ClipboardUtils;

/**
 * Popup handler for JEditorPane.
 *
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditorPanePopupHandler implements ClipboardActionsHandler, LinkActionsHandler, PositionLinkActionsHandler, ImageActionsHandler, PositionImageActionsHandler {

    private static String MAP_PROPERTY = "__MAP__";
    private static String IMAGE_CACHE_PROPERTY = "imageCache";

    private final JEditorPane editorPane;

    public EditorPanePopupHandler(JEditorPane editorPane) {
        this.editorPane = editorPane;
    }

    @Override
    public void performCut() {
        editorPane.cut();
    }

    @Override
    public void performCopy() {
        editorPane.copy();
    }

    @Override
    public void performPaste() {
        editorPane.paste();
    }

    @Override
    public void performDelete() {
        ActionUtils.invokeTextAction(editorPane, DefaultEditorKit.deleteNextCharAction);
    }

    @Override
    public void performSelectAll() {
        editorPane.requestFocusInWindow();
        editorPane.selectAll();
    }

    @Override
    public boolean isSelection() {
        return editorPane.isEnabled() && editorPane.getSelectionStart() != editorPane.getSelectionEnd();
    }

    @Override
    public boolean isEditable() {
        return editorPane.isEnabled() && editorPane.isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return editorPane.isEnabled() && !editorPane.getText().isEmpty();
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
        // Ignore
    }

    @Override
    public boolean canPaste() {
        return isEditable();
    }

    @Override
    public boolean canDelete() {
        return isEditable();
    }

    @Override
    public boolean isLinkSelected() {
        return EditorPanePopupHandler.getLinkUrl(editorPane, editorPane.getCaretPosition()) != null;
    }

    @Override
    public boolean isLinkSelected(Point locationOnScreen) {
        SwingUtilities.convertPointFromScreen(locationOnScreen, editorPane);
        return EditorPanePopupHandler.getLinkUrl(editorPane, locationOnScreen) != null;
    }

    @Override
    public boolean isImageSelected() {
        return EditorPanePopupHandler.hasImageSrc(editorPane, editorPane.getCaretPosition()) != null;
    }

    @Override
    public boolean isImageSelected(Point locationOnScreen) {
        SwingUtilities.convertPointFromScreen(locationOnScreen, editorPane);
        return EditorPanePopupHandler.hasImageSrc(editorPane, locationOnScreen) != null;
    }

    @Override
    public void performCopyLink() {
        String url = EditorPanePopupHandler.getLinkUrl(editorPane, editorPane.getCaretPosition());
        StringSelection stringSelection = new StringSelection(url);
        ClipboardUtils.getClipboard().setContents(stringSelection, stringSelection);
    }

    @Override
    public void performOpenLink() {
        String url = EditorPanePopupHandler.getLinkUrl(editorPane, editorPane.getCaretPosition());
        DesktopUtils.openDesktopURL(url);
    }

    @Override
    public void performCopyLink(Point locationOnScreen) {
        SwingUtilities.convertPointFromScreen(locationOnScreen, editorPane);
        String url = EditorPanePopupHandler.getLinkUrl(editorPane, locationOnScreen);
        StringSelection stringSelection = new StringSelection(url);
        ClipboardUtils.getClipboard().setContents(stringSelection, stringSelection);
    }

    @Override
    public void performOpenLink(Point locationOnScreen) {
        SwingUtilities.convertPointFromScreen(locationOnScreen, editorPane);
        String url = EditorPanePopupHandler.getLinkUrl(editorPane, locationOnScreen);
        DesktopUtils.openDesktopURL(url);
    }

    @Override
    public void performCopyImage() {
        String imageSrc = EditorPanePopupHandler.hasImageSrc(editorPane, editorPane.getCaretPosition());
        if (imageSrc != null) {
            EditorPanePopupHandler.copyImageToClipboard((HTMLDocument) editorPane.getDocument(), imageSrc);
        }
    }

    @Override
    public void performCopyImage(Point locationOnScreen) {
        SwingUtilities.convertPointFromScreen(locationOnScreen, editorPane);
        String imageSrc = EditorPanePopupHandler.hasImageSrc(editorPane, locationOnScreen);
        if (imageSrc != null) {
            copyImageToClipboard((HTMLDocument) editorPane.getDocument(), imageSrc);
        }
    }

    @Nullable
    public static String getLinkUrl(JEditorPane editorPane, int caretPosition) {
        return getLinkUrl(editorPane, caretPosition, 0, 0);
    }

    @Nullable
    public static String getLinkUrl(JEditorPane editorPane, Point position) {
        // Note: From HTMLEditorKit.LinkController.mouseMoved
        Document document = editorPane.getDocument();
        if (document instanceof HTMLDocument) {
            int pos = editorPane.viewToModel(position);
            if (pos >= 0) {
                return getLinkUrl(editorPane, pos, position.x, position.y);
            }
        }

        return null;
    }

    @Nullable
    public static String getLinkUrl(JEditorPane editorPane, int caretPosition, int offsetX, int offsetY) {
        Document document = editorPane.getDocument();
        if (document instanceof HTMLDocument) {
            HTMLDocument htmlDocument = (HTMLDocument) document;
            // Note: From HTMLEditorKit.activateLink
            Element e = htmlDocument.getCharacterElement(caretPosition);
            AttributeSet a = e.getAttributes();
            AttributeSet anchor = (AttributeSet) a.getAttribute(HTML.Tag.A);
            String href;
            if (anchor == null) {
                Object useMap = a.getAttribute(HTML.Attribute.USEMAP);
                if (useMap != null && (useMap instanceof String)) {
                    Object map = null;
                    Object maps = htmlDocument.getProperty(MAP_PROPERTY);

                    if (maps != null && (maps instanceof Hashtable)) {
                        map = ((Hashtable) maps).get((String) useMap);
                    }

                    if (map != null && caretPosition < htmlDocument.getLength()) {
                        Rectangle bounds;
                        TextUI ui = editorPane.getUI();
                        try {
                            Shape lBounds = ui.modelToView(editorPane, caretPosition, Position.Bias.Forward);
                            Shape rBounds = ui.modelToView(editorPane, caretPosition + 1, Position.Bias.Backward);
                            bounds = lBounds.getBounds();
                            bounds.add((rBounds instanceof Rectangle) ? (Rectangle) rBounds : rBounds.getBounds());
                        } catch (BadLocationException ble) {
                            bounds = null;
                        }
                        if (bounds != null) {
                            // Use reflection because javax.swing.text.html.Map is package protected
                            Class[] paramTypes = {int.class, int.class, int.class, int.class};
                            Method method;
                            try {
                                method = map.getClass().getMethod("getArea", paramTypes);
                                AttributeSet area = (AttributeSet) method.invoke(null, bounds.x + offsetX, bounds.y + offsetY, bounds.width, bounds.height);
                                if (area != null) {
                                    return (String) area.getAttribute(HTML.Attribute.HREF);
                                }
                            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                Logger.getLogger(EditorPanePopupHandler.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
                return null;
            } else {
                href = (String) anchor.getAttribute(HTML.Attribute.HREF);
            }

            return href;
        }

        return null;
    }

    @Nullable
    public static String hasImageSrc(JEditorPane editorPane, int caretPosition) {
        return EditorPanePopupHandler.hasImageSrc(editorPane, caretPosition, 0, 0);
    }

    @Nullable
    public static String hasImageSrc(JEditorPane editorPane, Point position) {
        Document document = editorPane.getDocument();
        if (document instanceof HTMLDocument) {
            int pos = editorPane.viewToModel(position);
            if (pos >= 0) {
                return EditorPanePopupHandler.hasImageSrc(editorPane, pos, position.x, position.y);
            }
        }

        return null;
    }

    @Nullable
    public static String hasImageSrc(JEditorPane editorPane, int caretPosition, int offsetX, int offsetY) {
        Document document = editorPane.getDocument();
        if (document instanceof HTMLDocument) {
            HTMLDocument htmlDocument = (HTMLDocument) document;
            Element e = htmlDocument.getCharacterElement(caretPosition);
            AttributeSet a = e.getAttributes();
            Object tagName = a.getAttribute(StyleConstants.NameAttribute);
            if (tagName instanceof HTML.Tag) {
                HTML.Tag tag = (HTML.Tag) tagName;
                if (tag == HTML.Tag.IMG) {
                    return (String) a.getAttribute(HTML.Attribute.SRC);
                }
            }
        }

        return null;
    }

    private static void copyImageToClipboard(HTMLDocument document, String imageSrc) {
        try {
            // From ImageView.loadImage
            URL reference = document.getBase();
            URL imageUrl = new URL(reference, imageSrc);
            Image image;
            Dictionary<URL, Image> cache = (Dictionary<URL, Image>) document.getProperty(IMAGE_CACHE_PROPERTY);
            if (cache != null) {
                image = cache.get(imageUrl);
            } else {
                image = Toolkit.getDefaultToolkit().createImage(imageUrl);
                if (image != null) {
                    // Force the image to be loaded by using an ImageIcon.
                    ImageIcon ii = new ImageIcon();
                    ii.setImage(image);
                }
            }

            if (image != null) {
                ClipboardUtils.pasteImage(image);
            }
        } catch (MalformedURLException ex) {

        }
    }
}
