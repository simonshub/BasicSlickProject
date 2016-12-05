/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editors.toolbars;

import engine.environment.ResMgr;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Emil Simon
 */
public class MapEditorToolbar extends javax.swing.JFrame {
    public enum EditMode { TILESET, ENTITIES };
    public enum TilesetTool { PAINT, FILL };
    
    public TileLabel tilePreview;
    public EditMode editMode;
    public TilesetTool tilesetTool;
    
    // Custom JPanel for drawing a preview of the selected tileset
    public class TileLabel extends JLabel {
        public TileLabel (String tileset) {
            setImg(tileset);
        }
        
        public final void setImg (String tileset) {
            if (tileset.isEmpty()) return;
            ImageIcon icon = new ImageIcon (ResMgr.getTileset(tileset).sheet.getResourceReference());
            setIcon(icon);
            setIconTextGap(0);
            setBorder(null);
            setText(null);
            setSize(icon.getImage().getWidth(null), icon.getImage().getHeight(null));
            repaint();
        }
    }
    
    
    
    // Custom ComboBox (drop down list) model class
    public class MapEditModeDropDown extends AbstractListModel implements ComboBoxModel {
        public Object[] data = { "Tiles", "Entities" };
        public Object selection = null;
        
        @Override
        public int getSize() {
            return data.length;
        }
        @Override
        public Object getElementAt(int index) {
            return data[index];
        }
        @Override
        public void setSelectedItem(Object anItem) {
            selection = anItem;
        }
        @Override
        public Object getSelectedItem() {
            return selection;
        }
    }
    
    // Custom ComboBox (drop down list) model class
    public class TilesetDropDown extends AbstractListModel implements ComboBoxModel {
        public Object[] data = ResMgr.tileset_lib.keySet().toArray();
        public Object selection = null;
        
        @Override
        public int getSize() {
            return data.length;
        }
        @Override
        public Object getElementAt(int index) {
            return data[index];
        }
        @Override
        public void setSelectedItem(Object anItem) {
            selection = anItem;
        }
        @Override
        public Object getSelectedItem() {
            return selection;
        }
    }
    
    
    
    // Custom ComboBox (drop down list) listener class
    public class MapEditModeDropDownListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            setMode(modeDropDown.getSelectedItem().toString());
        }
    }
    
    // Custom ComboBox (drop down list) listener class
    public class TilesetDropDownListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tilesetDropDown.getSelectedItem()!=null) {
                tilePreview.setImg(tilesetDropDown.getSelectedItem().toString());
            }
        }
    }
    
    
    
    public void setMode (String mode) {
        switch (mode) {
            case "Tiles" :
                tilesetFrame.setSize(205, 340);
                tilesetFrame.setVisible(true);
                editMode = EditMode.TILESET;
                setTilesetTool (TilesetTool.PAINT);
                
                break;
            default :
                break;
        }
    }
    
    public String getCurrentTileset () {
        String result = "";
        
        if (tilesetDropDown == null)
            return result;
        if (tilesetDropDown.getSelectedItem() == null)
            return result;
            
        result=tilesetDropDown.getSelectedItem().toString();
        return result;
    }
    
    public String getCurrentBackgroundTileset () {
        String result = "";
        
        if (backgroundTilesetDropDown == null)
            return result;
        if (backgroundTilesetDropDown.getSelectedItem() == null)
            return result;
            
        result=backgroundTilesetDropDown.getSelectedItem().toString();
        return result;
    }
    
    public void setTilesetTool (TilesetTool tool) {
        tilesetTool = tool;
        tilesetCurrentToolLabel.setText("Current Tool: " + tilesetTool.toString());
    }
    
    
    
    /**
     * Creates new form MapEditorToolbar
     */
    public MapEditorToolbar() {
        editMode = null;
        initComponents();
        
        tilePreview = new TileLabel ("");
        tilesetPanel.add(tilePreview);
        
        modeDropDown.addActionListener(new MapEditModeDropDownListener ());
        modeDropDown.setModel(new MapEditModeDropDown ());
        
        tilesetDropDown.addActionListener(new TilesetDropDownListener ());
        tilesetDropDown.setModel(new TilesetDropDown ());
        
        backgroundTilesetDropDown.setModel(new TilesetDropDown ());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tilesetFrame = new javax.swing.JFrame();
        tilesetLabel = new javax.swing.JLabel();
        tilesetDropDown = new javax.swing.JComboBox();
        separator2 = new javax.swing.JSeparator();
        tilesetPanel = new javax.swing.JPanel();
        tilesetPaintToolBtn = new javax.swing.JButton();
        tilesetFillToolBtn = new javax.swing.JButton();
        tilesetCurrentToolLabel = new javax.swing.JLabel();
        backgroundTilesetDropDown = new javax.swing.JComboBox();
        backgroundTilesetLabel = new javax.swing.JLabel();
        modeDropDown = new javax.swing.JComboBox();
        saveMapBtn = new javax.swing.JButton();
        newMapBtn = new javax.swing.JButton();
        loadMapBtn = new javax.swing.JButton();
        separator1 = new javax.swing.JSeparator();

        tilesetLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        tilesetLabel.setText("Tileset:");

        tilesetDropDown.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        tilesetDropDown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        tilesetPanel.setBackground(new java.awt.Color(0, 0, 0));
        tilesetPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout tilesetPanelLayout = new javax.swing.GroupLayout(tilesetPanel);
        tilesetPanel.setLayout(tilesetPanelLayout);
        tilesetPanelLayout.setHorizontalGroup(
            tilesetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        tilesetPanelLayout.setVerticalGroup(
            tilesetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 111, Short.MAX_VALUE)
        );

        tilesetPaintToolBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/editors/res/paint_tool_1.png"))); // NOI18N
        tilesetPaintToolBtn.setToolTipText("Paint");
        tilesetPaintToolBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tilesetPaintToolBtnActionPerformed(evt);
            }
        });

        tilesetFillToolBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/editors/res/fill_tool_1.png"))); // NOI18N
        tilesetFillToolBtn.setToolTipText("Fill");
        tilesetFillToolBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tilesetFillToolBtnActionPerformed(evt);
            }
        });

        tilesetCurrentToolLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        tilesetCurrentToolLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tilesetCurrentToolLabel.setText("Current Tool: PAINT");

        backgroundTilesetDropDown.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        backgroundTilesetDropDown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        backgroundTilesetLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        backgroundTilesetLabel.setText("Background:");

        javax.swing.GroupLayout tilesetFrameLayout = new javax.swing.GroupLayout(tilesetFrame.getContentPane());
        tilesetFrame.getContentPane().setLayout(tilesetFrameLayout);
        tilesetFrameLayout.setHorizontalGroup(
            tilesetFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tilesetFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tilesetFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tilesetPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tilesetCurrentToolLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(tilesetFrameLayout.createSequentialGroup()
                        .addComponent(tilesetPaintToolBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tilesetFillToolBtn))
                    .addComponent(separator2)
                    .addGroup(tilesetFrameLayout.createSequentialGroup()
                        .addComponent(tilesetLabel)
                        .addGap(18, 18, 18)
                        .addComponent(tilesetDropDown, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(tilesetFrameLayout.createSequentialGroup()
                        .addComponent(backgroundTilesetLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(backgroundTilesetDropDown, 0, 91, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tilesetFrameLayout.setVerticalGroup(
            tilesetFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tilesetFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tilesetFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backgroundTilesetDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(backgroundTilesetLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tilesetFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tilesetDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tilesetLabel))
                .addGap(18, 18, 18)
                .addComponent(separator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tilesetFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tilesetFillToolBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tilesetPaintToolBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tilesetCurrentToolLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tilesetPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Map Editor");

        modeDropDown.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N

        saveMapBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        saveMapBtn.setText("Save");

        newMapBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        newMapBtn.setText("New");

        loadMapBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        loadMapBtn.setText("Load");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(separator1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(newMapBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(loadMapBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(saveMapBtn))
                    .addComponent(modeDropDown, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadMapBtn)
                    .addComponent(saveMapBtn)
                    .addComponent(newMapBtn))
                .addGap(18, 18, 18)
                .addComponent(separator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(modeDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tilesetPaintToolBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tilesetPaintToolBtnActionPerformed
        setTilesetTool(TilesetTool.PAINT);
    }//GEN-LAST:event_tilesetPaintToolBtnActionPerformed

    private void tilesetFillToolBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tilesetFillToolBtnActionPerformed
        setTilesetTool(TilesetTool.FILL);
    }//GEN-LAST:event_tilesetFillToolBtnActionPerformed

    
     
    public static void main(String args[]) {
        /* Set the Nimbus look and feel 
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MapEditorToolbar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MapEditorToolbar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MapEditorToolbar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MapEditorToolbar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MapEditorToolbar().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox backgroundTilesetDropDown;
    private javax.swing.JLabel backgroundTilesetLabel;
    private javax.swing.JButton loadMapBtn;
    private javax.swing.JComboBox modeDropDown;
    private javax.swing.JButton newMapBtn;
    private javax.swing.JButton saveMapBtn;
    private javax.swing.JSeparator separator1;
    private javax.swing.JSeparator separator2;
    private javax.swing.JLabel tilesetCurrentToolLabel;
    private javax.swing.JComboBox tilesetDropDown;
    private javax.swing.JButton tilesetFillToolBtn;
    private javax.swing.JFrame tilesetFrame;
    private javax.swing.JLabel tilesetLabel;
    private javax.swing.JButton tilesetPaintToolBtn;
    private javax.swing.JPanel tilesetPanel;
    // End of variables declaration//GEN-END:variables
}
