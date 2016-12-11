/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editors.toolbars;

import engine.environment.Consts;
import engine.environment.ResMgr;
import engine.environment.StringRes;
import engine.game.entities.Entity;
import engine.game.entities.EntityType;
import engine.game.entities.EntityVar;
import engine.utils.FileUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Emil Simon
 */
public class MapEditorToolbar extends javax.swing.JFrame {
    public enum EditMode { TILES, ENTITIES };
    public enum TilesetTool { PAINT, FILL };
    public enum EntityTool { PLACE, EDIT, DELETE };
    
    public EditMode editMode;
    
    public EntityTool entityTool;
    public TilesetTool tilesetTool;
    public TileLabel tilePreview;
    
    public boolean changed = false;
    public boolean save = false;
    public String load = "";
    public String oldVarName = "";
    public String oldEntityName = "";
    public Entity currentlySelectedEntity = null;
    
    
    
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
    
    // Custom collider height/width/radius & origin x/y change listener
    public class FormattedFieldListener implements DocumentListener {
        public int id;
        public FormattedFieldListener (int id) { this.id=id; }
        
        @Override
        public void insertUpdate(DocumentEvent e) {
            int x=0,y=0;
            
            switch (id) {
                case 0 : //LOCATION X
                    try {
                        x=Integer.parseInt(locationXField.getText());
                        currentlySelectedEntity.location.x = x;
                    } catch (NumberFormatException ex) { }
                    break;
                case 1 : //LOCATION Y
                    try {
                        y=Integer.parseInt(locationYField.getText());
                        currentlySelectedEntity.location.y = y;
                    } catch (NumberFormatException ex) { }
                    break;
                default :
                    break;
            }
        }
        @Override
        public void removeUpdate(DocumentEvent e) {
        }
        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    }
    
    
    
    // Custom FileFilter for Maps
    public class MapFileFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            return f.getAbsolutePath().endsWith("."+Consts.MAP_FILE_EXTENSION);
        }
        @Override
        public String getDescription() {
            return "Slick Map Files (*.map)";
        }
    }
    
    // Custom ComboBox (drop down list) model class
    public class MapEditModeDropDown extends AbstractListModel implements ComboBoxModel {
//        public Object[] data = { "Tiles", "Entities" };
        public Object[] data = EditMode.values();
        public Object selection = data!=null ? data[0] : StringRes.EDITOR_TOOLBAR_NONE_FOUND;
        
        public MapEditModeDropDown () {
            setMode(EditMode.TILES.toString().toLowerCase());
        }
        
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
        public Object selection = data!=null ? data[0] : StringRes.EDITOR_TOOLBAR_NONE_FOUND;
        
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
    public class EntityTypeDropDown extends AbstractListModel implements ComboBoxModel {
        public Object[] data = ResMgr.entity_lib.keySet().toArray();
        public Object selection = data!=null ? data[0] : StringRes.EDITOR_TOOLBAR_NONE_FOUND;
        
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
    public class VarTypeDropDown extends AbstractListModel implements ComboBoxModel {
        public Object[] data = EntityVar.EntityVarType.values();
        public Object selection = data!=null ? data[0] : StringRes.EDITOR_TOOLBAR_NONE_FOUND;
        
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
            if (anItem == EntityVar.EntityVarType.NULL) {
                newVarValueField.setText("null");
                newVarValueField.setEnabled(false);
            } else {
                newVarValueField.setText("");
                newVarValueField.setEnabled(true);
            }
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
    
    // Custom ComboBox (drop down list) listener class
    public class EntityTypeDropDownListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (entityTypeDropDown.getSelectedItem()!=null) {
                // DO SOMETHING
            }
        }
    }
    
    
    
    public void setMode (String mode) {
        switch (mode.toLowerCase()) {
            case "tiles" :
                entityFrame.setVisible(false);
                tilesetFrame.setSize(240, 350);
                tilesetFrame.setVisible(true);
                tilesetFrame.setLocationRelativeTo(this);
                tilesetFrame.setLocation(250, 0);
                editMode = EditMode.TILES;
                setTilesetTool (TilesetTool.PAINT);
                break;
            case "entities" :
                tilesetFrame.setVisible(false);
                entityFrame.setSize(230, 200);
                entityFrame.setVisible(true);
                entityFrame.setLocationRelativeTo(this);
                entityFrame.setLocation(250, 0);
                editMode = EditMode.ENTITIES;
                setEntityTool (EntityTool.PLACE);
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
    
    
    
    public EntityType getSelectedEntityType () {
        if (ResMgr.hasEntityType(entityTypeDropDown.getSelectedItem().toString()))
            return ResMgr.getEntityType(entityTypeDropDown.getSelectedItem().toString());
        else
            return null;
    }
    
    
    
    public void setTilesetTool (TilesetTool tool) {
        tilesetTool = tool;
        tilesetCurrentToolLabel.setText("Current Tool: " + tilesetTool.toString());
    }
    
    public void setEntityTool (EntityTool tool) {
        entityTool = tool;
        entityCurrentToolLabel.setText("Current Tool: " + entityTool.toString());
    }
    
    
    
    public void setSelectedEntity (Entity ent) {
        if ((ent == null) || (ent == currentlySelectedEntity)) {
            currentlySelectedEntity = null;
            editEntityFrame.setVisible(false);
        } else {
            currentlySelectedEntity = ent;
            
            editEntityFrame.setTitle("Edit Entity - "+ent.name);
            editEntityFrame.setSize(420, 450);
            editEntityFrame.setLocationRelativeTo(this);
            editEntityFrame.setLocation(0, 200);
            entityNameField.setText(ent.name);
            System.out.println("loc: "+ent.location.toString());
            locationXField.setText(String.valueOf(ent.location.x));
            locationYField.setText(String.valueOf(ent.location.y));
            editEntityFrame.setVisible(true);
            
            oldEntityName = ent.name;
            updateVars();
        }
    }
    
    public String getNewName () {
        return entityNameField.getText();
    }
    
    
    
    public void setBackgroundTileset (String tileset) {
        backgroundTilesetDropDown.setSelectedItem(tileset);
        backgroundTilesetDropDown.updateUI();
    }
    
    
    
    public void updateVars () {
        DefaultTableModel model = (DefaultTableModel) varsTable.getModel();
        model.setRowCount(0);
        if (currentlySelectedEntity == null) { return; }
        
        EntityVar[] vars = currentlySelectedEntity.getVars();
        for (int i=0;i<currentlySelectedEntity.vars.size();i++) {
            String name = String.valueOf(vars[i].name);
            String type = String.valueOf(vars[i].type);
            String value = String.valueOf(vars[i].value);
            Object[] row = {name, type, value};
            model.addRow(row);
        }
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
        tilePreview.setImg(tilesetDropDown.getSelectedItem().toString());
        
        entityTypeDropDown.addActionListener(new EntityTypeDropDownListener ());
        entityTypeDropDown.setModel(new EntityTypeDropDown ());
        
        locationXField.getDocument().addDocumentListener(new FormattedFieldListener (0));
        locationYField.getDocument().addDocumentListener(new FormattedFieldListener (1));
        
        newVarTypeDropDown.setModel(new VarTypeDropDown ());
        editVarTypeDropDown.setModel(new VarTypeDropDown ());
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
        entityFrame = new javax.swing.JFrame();
        entityTypeLabel = new javax.swing.JLabel();
        entityTypeDropDown = new javax.swing.JComboBox();
        separator3 = new javax.swing.JSeparator();
        entityCurrentToolLabel = new javax.swing.JLabel();
        entityPlaceToolBtn = new javax.swing.JButton();
        entityEditToolBtn = new javax.swing.JButton();
        entityRemoveToolBtn = new javax.swing.JButton();
        editEntityFrame = new javax.swing.JFrame();
        entityNameLabel = new javax.swing.JLabel();
        locationLabel = new javax.swing.JLabel();
        variablesLabel = new javax.swing.JLabel();
        varsTableScroll = new javax.swing.JScrollPane();
        varsTable = new javax.swing.JTable();
        varsPanel = new javax.swing.JPanel();
        deleteVarBtn = new javax.swing.JButton();
        addVarBtn = new javax.swing.JButton();
        editVarBtn = new javax.swing.JButton();
        entityNameField = new javax.swing.JTextField();
        locationXField = new javax.swing.JFormattedTextField();
        locationYField = new javax.swing.JFormattedTextField();
        locationLabelX = new javax.swing.JLabel();
        locationLabelY = new javax.swing.JLabel();
        saveEntityNameBtn = new javax.swing.JButton();
        addVarFrame = new javax.swing.JFrame();
        newVarNameLabel = new javax.swing.JLabel();
        newVarNameField = new javax.swing.JTextField();
        newVarTypeDropDown = new javax.swing.JComboBox();
        newVarValueField = new javax.swing.JTextField();
        addVarConfBtn = new javax.swing.JButton();
        addVarCancelBtn = new javax.swing.JButton();
        newVarTypeLabel = new javax.swing.JLabel();
        newVarValueLabel = new javax.swing.JLabel();
        addVarErrorLabel = new javax.swing.JLabel();
        editVarFrame = new javax.swing.JFrame();
        editVarNameLabel = new javax.swing.JLabel();
        editVarNameField = new javax.swing.JTextField();
        editVarTypeDropDown = new javax.swing.JComboBox();
        editVarValueField = new javax.swing.JTextField();
        editVarConfBtn = new javax.swing.JButton();
        editVarCancelBtn = new javax.swing.JButton();
        editVarTypeLabel = new javax.swing.JLabel();
        editVarValueLabel = new javax.swing.JLabel();
        editVarErrorLabel = new javax.swing.JLabel();
        loadMapChooser = new javax.swing.JFileChooser();
        modeDropDown = new javax.swing.JComboBox();
        saveMapBtn = new javax.swing.JButton();
        newMapBtn = new javax.swing.JButton();
        loadMapBtn = new javax.swing.JButton();
        separator1 = new javax.swing.JSeparator();

        tilesetFrame.setTitle("Tiles");

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
            .addGap(0, 114, Short.MAX_VALUE)
        );

        tilesetPaintToolBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/editors/res/black-paint-brush-16.png"))); // NOI18N
        tilesetPaintToolBtn.setToolTipText("Paint");
        tilesetPaintToolBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tilesetPaintToolBtnActionPerformed(evt);
            }
        });

        tilesetFillToolBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/editors/res/black-paint-bucket-16.png"))); // NOI18N
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
                        .addGroup(tilesetFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(backgroundTilesetLabel)
                            .addComponent(tilesetLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tilesetFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tilesetDropDown, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(backgroundTilesetDropDown, 0, 150, Short.MAX_VALUE))))
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

        entityFrame.setTitle("Entities");

        entityTypeLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        entityTypeLabel.setText("Entity Type:");

        entityTypeDropDown.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        entityTypeDropDown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        entityCurrentToolLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        entityCurrentToolLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        entityCurrentToolLabel.setText("Current Tool: PLACE");

        entityPlaceToolBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/editors/res/black-add-property-16.png"))); // NOI18N
        entityPlaceToolBtn.setToolTipText("Paint");
        entityPlaceToolBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                entityPlaceToolBtnActionPerformed(evt);
            }
        });

        entityEditToolBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/editors/res/black-edit-property-16.png"))); // NOI18N
        entityEditToolBtn.setToolTipText("Edit");
        entityEditToolBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                entityEditToolBtnActionPerformed(evt);
            }
        });

        entityRemoveToolBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/editors/res/black-delete-property-16.png"))); // NOI18N
        entityRemoveToolBtn.setToolTipText("Delete");
        entityRemoveToolBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                entityRemoveToolBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout entityFrameLayout = new javax.swing.GroupLayout(entityFrame.getContentPane());
        entityFrame.getContentPane().setLayout(entityFrameLayout);
        entityFrameLayout.setHorizontalGroup(
            entityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(entityFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(entityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(entityCurrentToolLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(entityFrameLayout.createSequentialGroup()
                        .addComponent(entityPlaceToolBtn)
                        .addGap(18, 18, 18)
                        .addComponent(entityEditToolBtn)
                        .addGap(18, 18, 18)
                        .addComponent(entityRemoveToolBtn)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(separator3)
                    .addGroup(entityFrameLayout.createSequentialGroup()
                        .addComponent(entityTypeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(entityTypeDropDown, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        entityFrameLayout.setVerticalGroup(
            entityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(entityFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(entityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(entityTypeLabel)
                    .addComponent(entityTypeDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(separator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(entityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(entityPlaceToolBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(entityEditToolBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(entityRemoveToolBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(entityCurrentToolLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        entityNameLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        entityNameLabel.setText("Entity Name: ");

        locationLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        locationLabel.setText("Location: ");

        variablesLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        variablesLabel.setText("Variables:");

        varsTable.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        varsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Type", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        varsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        varsTable.setShowHorizontalLines(false);
        varsTableScroll.setViewportView(varsTable);

        varsPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        deleteVarBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        deleteVarBtn.setText("Delete Selected");
        deleteVarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteVarBtnActionPerformed(evt);
            }
        });

        addVarBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addVarBtn.setText("Add Variable");
        addVarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addVarBtnActionPerformed(evt);
            }
        });

        editVarBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        editVarBtn.setText("Edit Selected");
        editVarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editVarBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout varsPanelLayout = new javax.swing.GroupLayout(varsPanel);
        varsPanel.setLayout(varsPanelLayout);
        varsPanelLayout.setHorizontalGroup(
            varsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(varsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addVarBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editVarBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteVarBtn)
                .addContainerGap())
        );
        varsPanelLayout.setVerticalGroup(
            varsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(varsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(varsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addVarBtn)
                    .addComponent(deleteVarBtn)
                    .addComponent(editVarBtn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        entityNameField.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        entityNameField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        locationXField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        locationXField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        locationYField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        locationYField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        locationLabelX.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        locationLabelX.setText("X, ");

        locationLabelY.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        locationLabelY.setText("Y");

        saveEntityNameBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        saveEntityNameBtn.setText("Save Name");
        saveEntityNameBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveEntityNameBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout editEntityFrameLayout = new javax.swing.GroupLayout(editEntityFrame.getContentPane());
        editEntityFrame.getContentPane().setLayout(editEntityFrameLayout);
        editEntityFrameLayout.setHorizontalGroup(
            editEntityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editEntityFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editEntityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(varsTableScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(varsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(editEntityFrameLayout.createSequentialGroup()
                        .addGroup(editEntityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(variablesLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(locationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(entityNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(editEntityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(saveEntityNameBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(entityNameField)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editEntityFrameLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(locationXField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(locationLabelX)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(locationYField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(locationLabelY)))))
                .addContainerGap())
        );
        editEntityFrameLayout.setVerticalGroup(
            editEntityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editEntityFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editEntityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(entityNameLabel)
                    .addComponent(entityNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveEntityNameBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editEntityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(locationLabel)
                    .addComponent(locationXField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(locationYField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(locationLabelX)
                    .addComponent(locationLabelY))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(variablesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(varsTableScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(varsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        newVarNameLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        newVarNameLabel.setText("New Variable Name:");

        newVarNameField.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N

        newVarTypeDropDown.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        newVarTypeDropDown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "INTEGER" }));

        newVarValueField.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N

        addVarConfBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addVarConfBtn.setText("Add Variable");
        addVarConfBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addVarConfBtnActionPerformed(evt);
            }
        });

        addVarCancelBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addVarCancelBtn.setText("Cancel");
        addVarCancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addVarCancelBtnActionPerformed(evt);
            }
        });

        newVarTypeLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        newVarTypeLabel.setText("New Variable Type:");

        newVarValueLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        newVarValueLabel.setText("New Variable Value:");

        addVarErrorLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addVarErrorLabel.setForeground(new java.awt.Color(255, 0, 0));
        addVarErrorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        addVarErrorLabel.setText("ERROR LABEL");

        javax.swing.GroupLayout addVarFrameLayout = new javax.swing.GroupLayout(addVarFrame.getContentPane());
        addVarFrame.getContentPane().setLayout(addVarFrameLayout);
        addVarFrameLayout.setHorizontalGroup(
            addVarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addVarFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addVarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addVarConfBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addVarCancelBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, addVarFrameLayout.createSequentialGroup()
                        .addGroup(addVarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(newVarValueLabel)
                            .addComponent(newVarNameLabel)
                            .addComponent(newVarTypeLabel))
                        .addGap(18, 18, 18)
                        .addGroup(addVarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(newVarValueField)
                            .addComponent(newVarTypeDropDown, 0, 250, Short.MAX_VALUE)
                            .addComponent(newVarNameField)))
                    .addComponent(addVarErrorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        addVarFrameLayout.setVerticalGroup(
            addVarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addVarFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addVarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newVarNameLabel)
                    .addComponent(newVarNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(addVarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newVarTypeDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newVarTypeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(addVarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newVarValueField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newVarValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addComponent(addVarConfBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(addVarCancelBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addVarErrorLabel)
                .addContainerGap())
        );

        editVarNameLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        editVarNameLabel.setText("New Variable Name:");

        editVarNameField.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N

        editVarTypeDropDown.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        editVarTypeDropDown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "INTEGER" }));

        editVarValueField.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N

        editVarConfBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        editVarConfBtn.setText("Confirm Changes");
        editVarConfBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editVarConfBtnActionPerformed(evt);
            }
        });

        editVarCancelBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        editVarCancelBtn.setText("Cancel");
        editVarCancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editVarCancelBtnActionPerformed(evt);
            }
        });

        editVarTypeLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        editVarTypeLabel.setText("New Variable Type:");

        editVarValueLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        editVarValueLabel.setText("New Variable Value:");

        editVarErrorLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        editVarErrorLabel.setForeground(new java.awt.Color(255, 0, 0));
        editVarErrorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        editVarErrorLabel.setText("ERROR LABEL");

        javax.swing.GroupLayout editVarFrameLayout = new javax.swing.GroupLayout(editVarFrame.getContentPane());
        editVarFrame.getContentPane().setLayout(editVarFrameLayout);
        editVarFrameLayout.setHorizontalGroup(
            editVarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editVarFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editVarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(editVarConfBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editVarCancelBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editVarFrameLayout.createSequentialGroup()
                        .addGroup(editVarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(editVarValueLabel)
                            .addComponent(editVarNameLabel)
                            .addComponent(editVarTypeLabel))
                        .addGap(18, 18, 18)
                        .addGroup(editVarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(editVarValueField)
                            .addComponent(editVarTypeDropDown, 0, 250, Short.MAX_VALUE)
                            .addComponent(editVarNameField)))
                    .addComponent(editVarErrorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        editVarFrameLayout.setVerticalGroup(
            editVarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editVarFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editVarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editVarNameLabel)
                    .addComponent(editVarNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(editVarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editVarTypeDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editVarTypeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(editVarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editVarValueField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editVarValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addComponent(editVarConfBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(editVarCancelBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editVarErrorLabel)
                .addContainerGap())
        );

        loadMapChooser.setCurrentDirectory(new java.io.File("C:\\Program Files\\NetBeans 8.0.2\\res\\data\\maps"));
        loadMapChooser.setFileFilter(new MapFileFilter ());
        loadMapChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMapChooserActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Map Editor");

        modeDropDown.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N

        saveMapBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        saveMapBtn.setText("Save");
        saveMapBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMapBtnActionPerformed(evt);
            }
        });

        newMapBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        newMapBtn.setText("New");

        loadMapBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        loadMapBtn.setText("Load");
        loadMapBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMapBtnActionPerformed(evt);
            }
        });

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

    private void entityPlaceToolBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_entityPlaceToolBtnActionPerformed
        setEntityTool(EntityTool.PLACE);
    }//GEN-LAST:event_entityPlaceToolBtnActionPerformed

    private void entityEditToolBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_entityEditToolBtnActionPerformed
        setEntityTool(EntityTool.EDIT);
    }//GEN-LAST:event_entityEditToolBtnActionPerformed

    private void entityRemoveToolBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_entityRemoveToolBtnActionPerformed
        setEntityTool(EntityTool.DELETE);
    }//GEN-LAST:event_entityRemoveToolBtnActionPerformed

    private void deleteVarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteVarBtnActionPerformed
        currentlySelectedEntity.vars.remove(varsTable.getModel().getValueAt(varsTable.getSelectedRow(), 0).toString());
        changed = true;
        updateVars();
    }//GEN-LAST:event_deleteVarBtnActionPerformed

    private void addVarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addVarBtnActionPerformed
        newVarNameField.setText("");
        newVarValueField.setText("");
        addVarErrorLabel.setText("");

        addVarFrame.setTitle("Add New Variable");
        addVarFrame.setSize(400, 250);
        addVarFrame.setVisible(true);
        addVarFrame.setLocationRelativeTo(this);
        addVarFrame.setLocation(300, 200);
    }//GEN-LAST:event_addVarBtnActionPerformed

    private void editVarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editVarBtnActionPerformed
        oldVarName = varsTable.getModel().getValueAt(varsTable.getSelectedRow(), 0).toString();
        editVarNameField.setText(currentlySelectedEntity.vars.get(oldVarName).name);
        editVarTypeDropDown.setSelectedItem(currentlySelectedEntity.vars.get(oldVarName).type);
        editVarValueField.setText(currentlySelectedEntity.vars.get(oldVarName).value);
        editVarErrorLabel.setText("");

        editVarFrame.setTitle("Edit '"+oldVarName+"' Variable");
        editVarFrame.setSize(400, 250);
        editVarFrame.setVisible(true);
        editVarFrame.setLocationRelativeTo(this);
        editVarFrame.setLocation(300, 200);
    }//GEN-LAST:event_editVarBtnActionPerformed

    private void addVarConfBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addVarConfBtnActionPerformed
        if (newVarNameField.getText().isEmpty()) {
            addVarErrorLabel.setText("Variable must have a name!");
        } else if (currentlySelectedEntity.vars.containsKey(newVarNameField.getText())) {
            addVarErrorLabel.setText("A variable with that name already exists!");
        } else if (!EntityVar.isVarValueLegit(newVarValueField.getText(), (EntityVar.EntityVarType) newVarTypeDropDown.getSelectedItem())) {
            addVarErrorLabel.setText("The given value is not valid!");
        } else {
            currentlySelectedEntity.setVar(newVarNameField.getText(), newVarValueField.getText(), (EntityVar.EntityVarType) newVarTypeDropDown.getSelectedItem());
            addVarFrame.setVisible(false);
            changed = true;
            updateVars();
        }
    }//GEN-LAST:event_addVarConfBtnActionPerformed

    private void addVarCancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addVarCancelBtnActionPerformed
        addVarFrame.setVisible(false);
    }//GEN-LAST:event_addVarCancelBtnActionPerformed

    private void editVarConfBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editVarConfBtnActionPerformed
        if (editVarNameField.getText().isEmpty()) {
            editVarErrorLabel.setText("Variable must have a name!");
        } else if (currentlySelectedEntity.vars.containsKey(editVarNameField.getText()) && (!editVarNameField.getText().equals(oldVarName))) {
            editVarErrorLabel.setText("A variable with that name already exists!");
        } else if (!EntityVar.isVarValueLegit(editVarValueField.getText(), (EntityVar.EntityVarType) editVarTypeDropDown.getSelectedItem())) {
            editVarErrorLabel.setText("The given value is not valid!");
        } else {
            currentlySelectedEntity.vars.remove(oldVarName);
            currentlySelectedEntity.setVar(editVarNameField.getText(), editVarValueField.getText(), (EntityVar.EntityVarType) editVarTypeDropDown.getSelectedItem());
            editVarFrame.setVisible(false);
            changed = true;
            updateVars();
        }
    }//GEN-LAST:event_editVarConfBtnActionPerformed

    private void editVarCancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editVarCancelBtnActionPerformed
        editVarFrame.setVisible(false);
    }//GEN-LAST:event_editVarCancelBtnActionPerformed

    private void saveEntityNameBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveEntityNameBtnActionPerformed
        currentlySelectedEntity.name = entityNameField.getText();
    }//GEN-LAST:event_saveEntityNameBtnActionPerformed

    private void saveMapBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMapBtnActionPerformed
        save = true;
    }//GEN-LAST:event_saveMapBtnActionPerformed

    private void loadMapBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMapBtnActionPerformed
        loadMapChooser.setFileFilter(new MapFileFilter ());
        loadMapChooser.setCurrentDirectory(new File (Consts.MAP_DUMP_FOLDER));
        loadMapChooser.showOpenDialog(this);
    }//GEN-LAST:event_loadMapBtnActionPerformed

    private void loadMapChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMapChooserActionPerformed
        load = FileUtils.getMapPath(loadMapChooser.getSelectedFile());
    }//GEN-LAST:event_loadMapChooserActionPerformed

    
     
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
    private javax.swing.JButton addVarBtn;
    private javax.swing.JButton addVarCancelBtn;
    private javax.swing.JButton addVarConfBtn;
    private javax.swing.JLabel addVarErrorLabel;
    private javax.swing.JFrame addVarFrame;
    private javax.swing.JComboBox backgroundTilesetDropDown;
    private javax.swing.JLabel backgroundTilesetLabel;
    private javax.swing.JButton deleteVarBtn;
    private javax.swing.JFrame editEntityFrame;
    private javax.swing.JButton editVarBtn;
    private javax.swing.JButton editVarCancelBtn;
    private javax.swing.JButton editVarConfBtn;
    private javax.swing.JLabel editVarErrorLabel;
    private javax.swing.JFrame editVarFrame;
    private javax.swing.JTextField editVarNameField;
    private javax.swing.JLabel editVarNameLabel;
    private javax.swing.JComboBox editVarTypeDropDown;
    private javax.swing.JLabel editVarTypeLabel;
    private javax.swing.JTextField editVarValueField;
    private javax.swing.JLabel editVarValueLabel;
    private javax.swing.JLabel entityCurrentToolLabel;
    private javax.swing.JButton entityEditToolBtn;
    private javax.swing.JFrame entityFrame;
    private javax.swing.JTextField entityNameField;
    private javax.swing.JLabel entityNameLabel;
    private javax.swing.JButton entityPlaceToolBtn;
    private javax.swing.JButton entityRemoveToolBtn;
    private javax.swing.JComboBox entityTypeDropDown;
    private javax.swing.JLabel entityTypeLabel;
    private javax.swing.JButton loadMapBtn;
    private javax.swing.JFileChooser loadMapChooser;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JLabel locationLabelX;
    private javax.swing.JLabel locationLabelY;
    private javax.swing.JFormattedTextField locationXField;
    private javax.swing.JFormattedTextField locationYField;
    private javax.swing.JComboBox modeDropDown;
    private javax.swing.JButton newMapBtn;
    private javax.swing.JTextField newVarNameField;
    private javax.swing.JLabel newVarNameLabel;
    private javax.swing.JComboBox newVarTypeDropDown;
    private javax.swing.JLabel newVarTypeLabel;
    private javax.swing.JTextField newVarValueField;
    private javax.swing.JLabel newVarValueLabel;
    private javax.swing.JButton saveEntityNameBtn;
    private javax.swing.JButton saveMapBtn;
    private javax.swing.JSeparator separator1;
    private javax.swing.JSeparator separator2;
    private javax.swing.JSeparator separator3;
    private javax.swing.JLabel tilesetCurrentToolLabel;
    private javax.swing.JComboBox tilesetDropDown;
    private javax.swing.JButton tilesetFillToolBtn;
    private javax.swing.JFrame tilesetFrame;
    private javax.swing.JLabel tilesetLabel;
    private javax.swing.JButton tilesetPaintToolBtn;
    private javax.swing.JPanel tilesetPanel;
    private javax.swing.JLabel variablesLabel;
    private javax.swing.JPanel varsPanel;
    private javax.swing.JTable varsTable;
    private javax.swing.JScrollPane varsTableScroll;
    // End of variables declaration//GEN-END:variables
}
