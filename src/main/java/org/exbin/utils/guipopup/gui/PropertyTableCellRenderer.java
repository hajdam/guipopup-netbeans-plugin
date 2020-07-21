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
package org.exbin.utils.guipopup.gui;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Property Table Cell Renderer.
 *
 * @version 0.1.0 2019/07/22
 * @author ExBin Project (http://exbin.org)
 */
public class PropertyTableCellRenderer implements TableCellRenderer {

    public PropertyTableCellRenderer() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Object fieldValue = ((PropertyTableItem) value).asBasicType();
        if (fieldValue == null || fieldValue instanceof String) {
            JComponent component = new JLabel(fieldValue == null ? "<null>" : (String) fieldValue);
            component.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return component;
        }

        JComponent component = new JLabel(((PropertyTableItem) value).getTypeName());
        PropertyTableCellPanel cellPanel = new PropertyTableCellPanel(component, null, null);
        cellPanel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        cellPanel.getCellComponent().setBorder(null);
        return cellPanel;
    }
}
