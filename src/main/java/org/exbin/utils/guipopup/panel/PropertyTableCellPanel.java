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
package org.exbin.utils.guipopup.panel;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import org.exbin.utils.guipopup.WindowUtils;
import org.openide.windows.WindowManager;

/**
 * Properties table cell panel.
 *
 * @version 0.1.0 2019/07/22
 * @author ExBin Project (http://exbin.org)
 */
public class PropertyTableCellPanel extends ComponentPropertyTableCellPanel {

    private final String name;
    private final Object value;

    public PropertyTableCellPanel(JComponent cellComponent, Object value, String name) {
        super(cellComponent);

        this.value = value;
        this.name = name;
        init();
    }

    public PropertyTableCellPanel(Object value, String name) {
        super();

        this.value = value;
        this.name = name;
        init();
    }

    private void init() {
        setEditorAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InspectComponentPanel inspectComponentPanel = new InspectComponentPanel();
                inspectComponentPanel.setComponent(value, name);
                Frame mainWindow = WindowManager.getDefault().getMainWindow();
                final WindowUtils.DialogWrapper dialog = WindowUtils.createDialog(inspectComponentPanel, mainWindow, "Inspect Component", Dialog.ModalityType.MODELESS);
                inspectComponentPanel.setCloseActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.close();
                    }
                });
                dialog.show();
            }
        });
    }
}
