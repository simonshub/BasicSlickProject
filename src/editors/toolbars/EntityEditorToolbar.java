/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editors.toolbars;

import engine.game.entities.Collider;
import engine.game.entities.EntityType;
import engine.game.entities.EntityVar;
import engine.environment.ResMgr;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Emil Simon
 */
public class EntityEditorToolbar extends javax.swing.JFrame {
    public EntityType currentEntity;
    public String currentEntityName;
    public String oldVarName;
    public boolean changed=false;
    
    
    
    // Custom ComboBox (drop down list) model class
    public class EntityDropDown extends AbstractListModel implements ComboBoxModel {
        public String[] data = ResMgr.entity_lib.keySet().toArray ( new String [ResMgr.entity_lib.keySet().size()] );
        public String selection = data!=null ? data[0] : "None found!";
        
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
            selection = (String) anItem;
        }
        @Override
        public Object getSelectedItem() {
            return selection;
        }
    }
    
    // Custom ComboBox (drop down list) model class
    public class ActorDropDown extends AbstractListModel implements ComboBoxModel {
        public String[] data = ResMgr.actor_lib.keySet().toArray ( new String [ResMgr.actor_lib.keySet().size()] );
        public String selection = data!=null ? data[0] : "None found!";
        
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
            selection = (String) anItem;
        }
        @Override
        public Object getSelectedItem() {
            return selection;
        }
    }
    
    // Custom ComboBox (drop down list) model class
    public class VarTypeDropDown extends AbstractListModel implements ComboBoxModel {
        public Object[] data = EntityVar.EntityVarType.values();
        public Object selection = data!=null ? data[0] : "None found!";
        
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
    public class EntityDropDownListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (changed) {
                changed = false;
                saveEntityFrame.setSize(260, 170);
                saveEntityFrame.setVisible(true);
                saveEntityFrame.setLocation(300, 0);
            } else {
                loadEntity();
            }
        }
    }
    
    // Custom ComboBox (drop down list) listener class
    public class ActorDropDownListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!currentEntity.actor_name.equals(actorDropDown.getSelectedItem().toString())) {
                changed = true;
                currentEntity.actor_name = actorDropDown.getSelectedItem().toString();
            }
        }
    }
    
    // Custom ComboBox (drop down list) listener class
    public class ColliderDropDownListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (colliderTypeDropDown.getSelectedIndex()) {
                case 0:
                    radialColliderRadiusField.setEnabled(true);
                    boxColliderWidthField.setEnabled(false);
                    boxColliderHeightField.setEnabled(false);
                    colliderHeightSpinner.setEnabled(true);
                    floatingPillarColliderBtn.setEnabled(true);
                    currentEntity.collider.state = Collider.ColliderState.RADIAL;
                    break;
                case 1:
                    radialColliderRadiusField.setEnabled(false);
                    boxColliderWidthField.setEnabled(true);
                    boxColliderHeightField.setEnabled(true);
                    colliderHeightSpinner.setEnabled(true);
                    floatingPillarColliderBtn.setEnabled(true);
                    currentEntity.collider.state = Collider.ColliderState.BOX;
                    break;
                default:
                    radialColliderRadiusField.setEnabled(false);
                    boxColliderWidthField.setEnabled(false);
                    boxColliderHeightField.setEnabled(false);
                    colliderHeightSpinner.setEnabled(false);
                    floatingPillarColliderBtn.setEnabled(false);
                    currentEntity.collider.state = Collider.ColliderState.NONE;
                    break;
            }
        }
    }
    
    // Custom collider height layer change listener
    public class ColliderHeightListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            currentEntity.collider.height_layer = (Integer) colliderHeightSpinner.getValue();
        }
    }
    
    // Custom collider height/width/radius & origin x/y change listener
    public class FormattedFieldListener implements DocumentListener {
        private final int id;
        public FormattedFieldListener (int i) { id=i; }
        
        @Override
        public void insertUpdate(DocumentEvent e) {
            switch (id) {
                case 0: //RADIUS
                    currentEntity.collider.radius = Integer.parseInt(radialColliderRadiusField.getText());
                    break;
                case 1: //WIDTH
                    currentEntity.collider.box_width = Integer.parseInt(boxColliderWidthField.getText());
                    break;
                case 2: //HEIGHT
                    currentEntity.collider.box_height = Integer.parseInt(boxColliderHeightField.getText());
                    break;
                case 3: //ORIGIN X
                    currentEntity.originX = Integer.parseInt(originXField.getText());
                    break;
                case 4: //ORIGIN Y
                    currentEntity.originY = Integer.parseInt(originYField.getText());
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
    
    
    
    // Utility function to load up the currently selected entity type
    public final void loadEntity () {
        currentEntityName = entityDropDown.getSelectedItem().toString();
        
        if (currentEntityName.equals("None found!"))
            return;
        
        currentEntity = ResMgr.getEntityType(currentEntityName);
        actorDropDown.setSelectedItem(currentEntity.actor_name);
        
        if (currentEntity.collider.state == Collider.ColliderState.RADIAL) {
            colliderTypeDropDown.setSelectedIndex(0);
        } else if (currentEntity.collider.state == Collider.ColliderState.BOX) {
            colliderTypeDropDown.setSelectedIndex(1);
        } else {
            colliderTypeDropDown.setSelectedIndex(2);
        }
        
        colliderHeightSpinner.setValue(currentEntity.collider.height_layer);
        radialColliderRadiusField.setText(String.valueOf(currentEntity.collider.radius));
        boxColliderWidthField.setText(String.valueOf(currentEntity.collider.box_width));
        boxColliderHeightField.setText(String.valueOf(currentEntity.collider.box_height));
        floatingPillarColliderBtn.setText(currentEntity.collider.floating ? "Floating Collider" : "Pillar Collider");
        originXField.setText(String.valueOf(currentEntity.originX));
        originYField.setText(String.valueOf(currentEntity.originY));
        updateVars();
    }
    
    // Utility function for updating the frames table
    public final void updateVars () {
        DefaultTableModel model = (DefaultTableModel) varsTable.getModel();
        model.setRowCount(0);
        if (currentEntityName.equals("") || currentEntityName.equals("None found!")) { return; }
        
        EntityVar[] vars = currentEntity.getVars();
        for (int i=0;i<currentEntity.vars.size();i++) {
            String name = String.valueOf(vars[i].name);
            String type = String.valueOf(vars[i].type);
            String value = String.valueOf(vars[i].value);
            Object[] row = {name, type, value};
            model.addRow(row);
        }
    }
    
    // Utility function for setting the origin input from OUTSIDE this class!
    public void setOrigin (int x, int y) {
        this.originXField.setText(String.valueOf(x));
        this.originYField.setText(String.valueOf(y));
    }
    
    
    
    public EntityEditorToolbar() {
        initComponents();
        
        entityDropDown.addActionListener(new EntityDropDownListener ());
        entityDropDown.setModel(new EntityDropDown ());
        
        actorDropDown.addActionListener(new ActorDropDownListener ());
        actorDropDown.setModel(new ActorDropDown ());
        colliderTypeDropDown.addActionListener(new ColliderDropDownListener ());
        colliderHeightSpinner.addChangeListener(new ColliderHeightListener ());
        
        radialColliderRadiusField.getDocument().addDocumentListener(new FormattedFieldListener (0));
        boxColliderWidthField.getDocument().addDocumentListener(new FormattedFieldListener (1));
        boxColliderHeightField.getDocument().addDocumentListener(new FormattedFieldListener (2));
        originXField.getDocument().addDocumentListener(new FormattedFieldListener (3));
        originYField.getDocument().addDocumentListener(new FormattedFieldListener (4));
        
        newVarTypeDropDown.setModel(new VarTypeDropDown ());
        editVarTypeDropDown.setModel(new VarTypeDropDown ());
        
        loadEntity ();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        saveEntityFrame = new javax.swing.JDialog();
        saveEntityLabel = new javax.swing.JLabel();
        saveEntityLine1Label = new javax.swing.JLabel();
        saveEntityLine2Label = new javax.swing.JLabel();
        saveEntityConfBtn = new javax.swing.JButton();
        saveEntityCancelBtn = new javax.swing.JButton();
        resultTextFrame = new javax.swing.JFrame();
        resultTextAreaScroll = new javax.swing.JScrollPane();
        resultTextArea = new javax.swing.JTextArea();
        deleteEntityConfFrame = new javax.swing.JDialog();
        deleteEntityConfLine1Label = new javax.swing.JLabel();
        deleteEntityConfLabel = new javax.swing.JLabel();
        deleteEntityConfBtn = new javax.swing.JButton();
        deleteEntityCancelBtn = new javax.swing.JButton();
        deleteEntityConfLine2Label = new javax.swing.JLabel();
        addNewEntityFrame = new javax.swing.JDialog();
        addNewEntityNameLabel = new javax.swing.JLabel();
        addNewEntityNameField = new javax.swing.JTextField();
        addNewEntityConfBtn = new javax.swing.JButton();
        addNewEntityCancelBtn = new javax.swing.JButton();
        addNewEntityErrorLabel = new javax.swing.JLabel();
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
        entityLabel = new javax.swing.JLabel();
        entityDropDown = new javax.swing.JComboBox();
        addEntityBtn = new javax.swing.JButton();
        saveChangesBtn = new javax.swing.JButton();
        separator1 = new javax.swing.JSeparator();
        varsTableScroll = new javax.swing.JScrollPane();
        varsTable = new javax.swing.JTable();
        varsLabel = new javax.swing.JLabel();
        deleteEntityBtn = new javax.swing.JButton();
        varsPanel = new javax.swing.JPanel();
        deleteVarBtn = new javax.swing.JButton();
        addVarBtn = new javax.swing.JButton();
        editVarBtn = new javax.swing.JButton();
        actorLabel = new javax.swing.JLabel();
        actorDropDown = new javax.swing.JComboBox();
        separator2 = new javax.swing.JSeparator();
        colliderLabel = new javax.swing.JLabel();
        radialColliderPanel = new javax.swing.JPanel();
        radialColliderRadiusLabel = new javax.swing.JLabel();
        radialColliderRadiusField = new javax.swing.JFormattedTextField();
        boxColliderPanel = new javax.swing.JPanel();
        boxColliderWidthLabel = new javax.swing.JLabel();
        boxColliderWidthField = new javax.swing.JFormattedTextField();
        boxColliderHeightLabel = new javax.swing.JLabel();
        boxColliderHeightField = new javax.swing.JFormattedTextField();
        originXLabel = new javax.swing.JLabel();
        originXField = new javax.swing.JFormattedTextField();
        originYLabel = new javax.swing.JLabel();
        originYField = new javax.swing.JFormattedTextField();
        separator3 = new javax.swing.JSeparator();
        colliderHeightLabel = new javax.swing.JLabel();
        colliderHeightSpinner = new javax.swing.JSpinner();
        separator4 = new javax.swing.JSeparator();
        colliderTypeDropDown = new javax.swing.JComboBox();
        previewTextBtn = new javax.swing.JButton();
        floatingPillarColliderBtn = new javax.swing.JButton();

        saveEntityLabel.setFont(new java.awt.Font("Calibri", 0, 24)); // NOI18N
        saveEntityLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        saveEntityLabel.setText("Unsaved Changes");

        saveEntityLine1Label.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        saveEntityLine1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        saveEntityLine1Label.setText("You have made some unsaved changes to");

        saveEntityLine2Label.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        saveEntityLine2Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        saveEntityLine2Label.setText("this Entity. Would you like to save now?");

        saveEntityConfBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        saveEntityConfBtn.setText("SAVE");
        saveEntityConfBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveEntityConfBtnActionPerformed(evt);
            }
        });

        saveEntityCancelBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        saveEntityCancelBtn.setText("CONTINUE");
        saveEntityCancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveEntityCancelBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout saveEntityFrameLayout = new javax.swing.GroupLayout(saveEntityFrame.getContentPane());
        saveEntityFrame.getContentPane().setLayout(saveEntityFrameLayout);
        saveEntityFrameLayout.setHorizontalGroup(
            saveEntityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(saveEntityFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(saveEntityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saveEntityLine1Label, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addComponent(saveEntityLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(saveEntityFrameLayout.createSequentialGroup()
                        .addComponent(saveEntityConfBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(saveEntityCancelBtn))
                    .addComponent(saveEntityLine2Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        saveEntityFrameLayout.setVerticalGroup(
            saveEntityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(saveEntityFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(saveEntityLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveEntityLine1Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveEntityLine2Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(saveEntityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveEntityConfBtn)
                    .addComponent(saveEntityCancelBtn))
                .addContainerGap())
        );

        resultTextArea.setEditable(false);
        resultTextArea.setColumns(20);
        resultTextArea.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        resultTextArea.setRows(5);
        resultTextAreaScroll.setViewportView(resultTextArea);

        javax.swing.GroupLayout resultTextFrameLayout = new javax.swing.GroupLayout(resultTextFrame.getContentPane());
        resultTextFrame.getContentPane().setLayout(resultTextFrameLayout);
        resultTextFrameLayout.setHorizontalGroup(
            resultTextFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultTextFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resultTextAreaScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                .addContainerGap())
        );
        resultTextFrameLayout.setVerticalGroup(
            resultTextFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultTextFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resultTextAreaScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                .addContainerGap())
        );

        deleteEntityConfLine1Label.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        deleteEntityConfLine1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        deleteEntityConfLine1Label.setText("This will remove the selected entity type and all");

        deleteEntityConfLabel.setFont(new java.awt.Font("Calibri", 0, 24)); // NOI18N
        deleteEntityConfLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        deleteEntityConfLabel.setText("Are you sure?");

        deleteEntityConfBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        deleteEntityConfBtn.setText("YES");
        deleteEntityConfBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteEntityConfBtnActionPerformed(evt);
            }
        });

        deleteEntityCancelBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        deleteEntityCancelBtn.setText("NO");
        deleteEntityCancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteEntityCancelBtnActionPerformed(evt);
            }
        });

        deleteEntityConfLine2Label.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        deleteEntityConfLine2Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        deleteEntityConfLine2Label.setText("it's contents. This action cannot be undone.");

        javax.swing.GroupLayout deleteEntityConfFrameLayout = new javax.swing.GroupLayout(deleteEntityConfFrame.getContentPane());
        deleteEntityConfFrame.getContentPane().setLayout(deleteEntityConfFrameLayout);
        deleteEntityConfFrameLayout.setHorizontalGroup(
            deleteEntityConfFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deleteEntityConfFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(deleteEntityConfFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(deleteEntityConfLine1Label, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(deleteEntityConfLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(deleteEntityConfFrameLayout.createSequentialGroup()
                        .addComponent(deleteEntityConfBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deleteEntityCancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(deleteEntityConfLine2Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        deleteEntityConfFrameLayout.setVerticalGroup(
            deleteEntityConfFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deleteEntityConfFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(deleteEntityConfLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteEntityConfLine1Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteEntityConfLine2Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(deleteEntityConfFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteEntityConfBtn)
                    .addComponent(deleteEntityCancelBtn))
                .addContainerGap())
        );

        addNewEntityFrame.setTitle("New Actor");

        addNewEntityNameLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addNewEntityNameLabel.setText("Name: ");

        addNewEntityNameField.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addNewEntityNameField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        addNewEntityConfBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addNewEntityConfBtn.setText("Add");
        addNewEntityConfBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewEntityConfBtnActionPerformed(evt);
            }
        });

        addNewEntityCancelBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addNewEntityCancelBtn.setText("Cancel");
        addNewEntityCancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewEntityCancelBtnActionPerformed(evt);
            }
        });

        addNewEntityErrorLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addNewEntityErrorLabel.setForeground(new java.awt.Color(255, 0, 0));
        addNewEntityErrorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        addNewEntityErrorLabel.setText("SOME ERROR TEXT");

        javax.swing.GroupLayout addNewEntityFrameLayout = new javax.swing.GroupLayout(addNewEntityFrame.getContentPane());
        addNewEntityFrame.getContentPane().setLayout(addNewEntityFrameLayout);
        addNewEntityFrameLayout.setHorizontalGroup(
            addNewEntityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addNewEntityFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addNewEntityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(addNewEntityFrameLayout.createSequentialGroup()
                        .addComponent(addNewEntityNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addNewEntityNameField))
                    .addGroup(addNewEntityFrameLayout.createSequentialGroup()
                        .addComponent(addNewEntityConfBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                        .addComponent(addNewEntityCancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(addNewEntityErrorLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        addNewEntityFrameLayout.setVerticalGroup(
            addNewEntityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addNewEntityFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addNewEntityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addNewEntityNameLabel)
                    .addComponent(addNewEntityNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(addNewEntityFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addNewEntityConfBtn)
                    .addComponent(addNewEntityCancelBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addNewEntityErrorLabel))
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Entity Editor");

        entityLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        entityLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        entityLabel.setText("Current Entity Type:");

        entityDropDown.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        entityDropDown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        addEntityBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addEntityBtn.setText("Add New Entity Type");
        addEntityBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEntityBtnActionPerformed(evt);
            }
        });

        saveChangesBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        saveChangesBtn.setText("Save Changes to Current Entity Type");
        saveChangesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveChangesBtnActionPerformed(evt);
            }
        });

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
        varsTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        varsLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        varsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        varsLabel.setText("Current Entity Type Variables:");

        deleteEntityBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        deleteEntityBtn.setText("Delete Current Entity Type");
        deleteEntityBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteEntityBtnActionPerformed(evt);
            }
        });

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

        actorLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        actorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        actorLabel.setText("Actor Selector:");

        actorDropDown.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        actorDropDown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        colliderLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        colliderLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        colliderLabel.setText("Collider Settings:");

        radialColliderPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        radialColliderRadiusLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        radialColliderRadiusLabel.setText("Collider Radius:");

        radialColliderRadiusField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        radialColliderRadiusField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        radialColliderRadiusField.setEnabled(false);

        javax.swing.GroupLayout radialColliderPanelLayout = new javax.swing.GroupLayout(radialColliderPanel);
        radialColliderPanel.setLayout(radialColliderPanelLayout);
        radialColliderPanelLayout.setHorizontalGroup(
            radialColliderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radialColliderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(radialColliderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radialColliderRadiusField)
                    .addComponent(radialColliderRadiusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                .addContainerGap())
        );
        radialColliderPanelLayout.setVerticalGroup(
            radialColliderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radialColliderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(radialColliderRadiusLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radialColliderRadiusField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        boxColliderPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        boxColliderWidthLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        boxColliderWidthLabel.setText("Collider Width:");

        boxColliderWidthField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        boxColliderWidthField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        boxColliderWidthField.setEnabled(false);

        boxColliderHeightLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        boxColliderHeightLabel.setText("Collider Height:");

        boxColliderHeightField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        boxColliderHeightField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        boxColliderHeightField.setEnabled(false);

        javax.swing.GroupLayout boxColliderPanelLayout = new javax.swing.GroupLayout(boxColliderPanel);
        boxColliderPanel.setLayout(boxColliderPanelLayout);
        boxColliderPanelLayout.setHorizontalGroup(
            boxColliderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(boxColliderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(boxColliderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(boxColliderWidthField)
                    .addComponent(boxColliderWidthLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(boxColliderHeightField)
                    .addComponent(boxColliderHeightLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                .addContainerGap())
        );
        boxColliderPanelLayout.setVerticalGroup(
            boxColliderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(boxColliderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(boxColliderWidthLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(boxColliderWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(boxColliderHeightLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(boxColliderHeightField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        originXLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        originXLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        originXLabel.setText("Entity Origin X:  ");

        originXField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        originXField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        originYLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        originYLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        originYLabel.setText("Entity Origin Y:  ");

        originYField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        originYField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        colliderHeightLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        colliderHeightLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        colliderHeightLabel.setText("Collider Height Layer:  ");

        colliderHeightSpinner.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        colliderHeightSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 0, 10, 1));
        colliderHeightSpinner.setEnabled(false);

        colliderTypeDropDown.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        colliderTypeDropDown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Radial Collider", "Box Collider", "None" }));
        colliderTypeDropDown.setSelectedIndex(2);

        previewTextBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        previewTextBtn.setText("Preview .ent File Result");
        previewTextBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewTextBtnActionPerformed(evt);
            }
        });

        floatingPillarColliderBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        floatingPillarColliderBtn.setText("Floating Collider");
        floatingPillarColliderBtn.setEnabled(false);
        floatingPillarColliderBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                floatingPillarColliderBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(floatingPillarColliderBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(varsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(varsTableScroll, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(separator2)
                    .addComponent(saveChangesBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(entityLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(entityDropDown, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addEntityBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deleteEntityBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(varsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(separator1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(actorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(actorDropDown, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(colliderLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(radialColliderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(boxColliderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(separator4)
                    .addComponent(colliderTypeDropDown, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(originYLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(originXLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(originYField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(originXField)))
                    .addComponent(separator3)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(colliderHeightLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(colliderHeightSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(previewTextBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(entityLabel)
                    .addComponent(entityDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addEntityBtn)
                    .addComponent(deleteEntityBtn))
                .addGap(11, 11, 11)
                .addComponent(saveChangesBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(separator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(actorLabel)
                    .addComponent(actorDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(separator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colliderLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colliderTypeDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(boxColliderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(radialColliderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(colliderHeightLabel)
                    .addComponent(colliderHeightSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(floatingPillarColliderBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(separator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(originXLabel)
                    .addComponent(originXField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(originYLabel)
                    .addComponent(originYField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(separator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(varsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(varsTableScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(varsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(previewTextBtn)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveEntityConfBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveEntityConfBtnActionPerformed
        saveChangesBtnActionPerformed(evt);
        saveEntityFrame.setVisible(false);
        loadEntity();
    }//GEN-LAST:event_saveEntityConfBtnActionPerformed

    private void saveEntityCancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveEntityCancelBtnActionPerformed
        saveEntityFrame.setVisible(false);
        loadEntity();
    }//GEN-LAST:event_saveEntityCancelBtnActionPerformed

    private void saveChangesBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveChangesBtnActionPerformed
        System.out.println("Saving entity type "+currentEntityName+" to file "+currentEntity.entity_type_file);
        ResMgr.entity_lib.put(currentEntityName, currentEntity);
        ResMgr.getEntityType(currentEntityName).writeToFile();
        ResMgr.writeEntities();
        this.changed = false;
    }//GEN-LAST:event_saveChangesBtnActionPerformed

    private void previewTextBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewTextBtnActionPerformed
        resultTextArea.setText(currentEntity.getWritten());
        resultTextFrame.setTitle("'"+currentEntityName+".ent' contents");
        resultTextFrame.setSize(400,600);
        resultTextFrame.setLocationRelativeTo(this);
        resultTextFrame.setLocation(300, 0);
        resultTextFrame.setVisible(true);
    }//GEN-LAST:event_previewTextBtnActionPerformed

    private void deleteEntityConfBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteEntityConfBtnActionPerformed
        deleteEntityConfFrame.setVisible(false);
        ResMgr.deleteEntityType(currentEntityName);
        entityDropDown.setModel(new EntityDropDown ());
        loadEntity();
    }//GEN-LAST:event_deleteEntityConfBtnActionPerformed

    private void deleteEntityCancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteEntityCancelBtnActionPerformed
        deleteEntityConfFrame.setVisible(false);
    }//GEN-LAST:event_deleteEntityCancelBtnActionPerformed

    private void addNewEntityConfBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewEntityConfBtnActionPerformed
        String desiredName = addNewEntityNameField.getText();

        if (desiredName.isEmpty()) {
            addNewEntityErrorLabel.setText("Entity type must have a name!");
        } else if (ResMgr.hasEntityType(desiredName)) {
            addNewEntityErrorLabel.setText("Entity type already exists!");
        } else {
            try {
                addNewEntityFrame.setVisible(false);
                ResMgr.addEntityType(desiredName, new EntityType (desiredName));
                ResMgr.getEntityType(desiredName).actor_name = actorDropDown.getSelectedItem().toString();
                entityDropDown.setModel(new EntityDropDown ());
                entityDropDown.setSelectedItem(desiredName);
                loadEntity();
            } catch (IOException ex) {
                Logger.getLogger(ActorEditorToolbar.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_addNewEntityConfBtnActionPerformed

    private void addNewEntityCancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewEntityCancelBtnActionPerformed
        addNewEntityFrame.setVisible(false);
    }//GEN-LAST:event_addNewEntityCancelBtnActionPerformed

    private void floatingPillarColliderBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_floatingPillarColliderBtnActionPerformed
        currentEntity.collider.floating = !currentEntity.collider.floating;
        if (currentEntity.collider.floating) {
            floatingPillarColliderBtn.setText("Floating Collider");
        } else {
            floatingPillarColliderBtn.setText("Pillar Collider");
        }
    }//GEN-LAST:event_floatingPillarColliderBtnActionPerformed

    private void addEntityBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEntityBtnActionPerformed
        addNewEntityErrorLabel.setText("");
        addNewEntityFrame.setSize(270, 120);
        addNewEntityFrame.setVisible(true);
        addNewEntityFrame.setLocationRelativeTo(this);
        addNewEntityFrame.setLocation(300, 0);
    }//GEN-LAST:event_addEntityBtnActionPerformed

    private void deleteEntityBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteEntityBtnActionPerformed
        deleteEntityConfFrame.setSize(300, 180);
        deleteEntityConfFrame.setVisible(true);
        deleteEntityConfFrame.setLocationRelativeTo(this);
        deleteEntityConfFrame.setLocation(300, 0);
    }//GEN-LAST:event_deleteEntityBtnActionPerformed

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

    private void addVarConfBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addVarConfBtnActionPerformed
        if (newVarNameField.getText().isEmpty()) {
            addVarErrorLabel.setText("Variable must have a name!");
        } else if (currentEntity.vars.containsKey(newVarNameField.getText())) {
            addVarErrorLabel.setText("A variable with that name already exists!");
        } else if (!EntityVar.isVarValueLegit(newVarValueField.getText(), (EntityVar.EntityVarType) newVarTypeDropDown.getSelectedItem())) {
            addVarErrorLabel.setText("The given value is not valid!");
        } else {
            currentEntity.setVar(newVarNameField.getText(), newVarValueField.getText(), (EntityVar.EntityVarType) newVarTypeDropDown.getSelectedItem());
            addVarFrame.setVisible(false);
            changed = true;
            updateVars();
        }
    }//GEN-LAST:event_addVarConfBtnActionPerformed

    private void addVarCancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addVarCancelBtnActionPerformed
        addVarFrame.setVisible(false);
    }//GEN-LAST:event_addVarCancelBtnActionPerformed

    private void deleteVarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteVarBtnActionPerformed
        currentEntity.vars.remove(varsTable.getModel().getValueAt(varsTable.getSelectedRow(), 0).toString());
        changed = true;
        updateVars();
    }//GEN-LAST:event_deleteVarBtnActionPerformed

    private void editVarConfBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editVarConfBtnActionPerformed
        if (editVarNameField.getText().isEmpty()) {
            editVarErrorLabel.setText("Variable must have a name!");
        } else if (currentEntity.vars.containsKey(editVarNameField.getText()) && (!editVarNameField.getText().equals(oldVarName))) {
            editVarErrorLabel.setText("A variable with that name already exists!");
        } else if (!EntityVar.isVarValueLegit(editVarValueField.getText(), (EntityVar.EntityVarType) editVarTypeDropDown.getSelectedItem())) {
            editVarErrorLabel.setText("The given value is not valid!");
        } else {
            currentEntity.vars.remove(oldVarName);
            currentEntity.setVar(editVarNameField.getText(), editVarValueField.getText(), (EntityVar.EntityVarType) editVarTypeDropDown.getSelectedItem());
            editVarFrame.setVisible(false);
            changed = true;
            updateVars();
        }
    }//GEN-LAST:event_editVarConfBtnActionPerformed

    private void editVarCancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editVarCancelBtnActionPerformed
        editVarFrame.setVisible(false);
    }//GEN-LAST:event_editVarCancelBtnActionPerformed

    private void editVarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editVarBtnActionPerformed
        oldVarName = varsTable.getModel().getValueAt(varsTable.getSelectedRow(), 0).toString();
        editVarNameField.setText(currentEntity.vars.get(oldVarName).name);
        editVarTypeDropDown.setSelectedItem(currentEntity.vars.get(oldVarName).type);
        editVarValueField.setText(currentEntity.vars.get(oldVarName).value);
        editVarErrorLabel.setText("");
        
        editVarFrame.setTitle("Edit '"+oldVarName+"' Variable");
        editVarFrame.setSize(400, 250);
        editVarFrame.setVisible(true);
        editVarFrame.setLocationRelativeTo(this);
        editVarFrame.setLocation(300, 200);
    }//GEN-LAST:event_editVarBtnActionPerformed

    
    
    public static void main (String[] args) {
        
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ActorEditorToolbar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new EntityEditorToolbar().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox actorDropDown;
    private javax.swing.JLabel actorLabel;
    private javax.swing.JButton addEntityBtn;
    private javax.swing.JButton addNewEntityCancelBtn;
    private javax.swing.JButton addNewEntityConfBtn;
    private javax.swing.JLabel addNewEntityErrorLabel;
    private javax.swing.JDialog addNewEntityFrame;
    private javax.swing.JTextField addNewEntityNameField;
    private javax.swing.JLabel addNewEntityNameLabel;
    private javax.swing.JButton addVarBtn;
    private javax.swing.JButton addVarCancelBtn;
    private javax.swing.JButton addVarConfBtn;
    private javax.swing.JLabel addVarErrorLabel;
    private javax.swing.JFrame addVarFrame;
    private javax.swing.JFormattedTextField boxColliderHeightField;
    private javax.swing.JLabel boxColliderHeightLabel;
    private javax.swing.JPanel boxColliderPanel;
    private javax.swing.JFormattedTextField boxColliderWidthField;
    private javax.swing.JLabel boxColliderWidthLabel;
    private javax.swing.JLabel colliderHeightLabel;
    private javax.swing.JSpinner colliderHeightSpinner;
    private javax.swing.JLabel colliderLabel;
    private javax.swing.JComboBox colliderTypeDropDown;
    private javax.swing.JButton deleteEntityBtn;
    private javax.swing.JButton deleteEntityCancelBtn;
    private javax.swing.JButton deleteEntityConfBtn;
    private javax.swing.JDialog deleteEntityConfFrame;
    private javax.swing.JLabel deleteEntityConfLabel;
    private javax.swing.JLabel deleteEntityConfLine1Label;
    private javax.swing.JLabel deleteEntityConfLine2Label;
    private javax.swing.JButton deleteVarBtn;
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
    private javax.swing.JComboBox entityDropDown;
    private javax.swing.JLabel entityLabel;
    private javax.swing.JButton floatingPillarColliderBtn;
    private javax.swing.JTextField newVarNameField;
    private javax.swing.JLabel newVarNameLabel;
    private javax.swing.JComboBox newVarTypeDropDown;
    private javax.swing.JLabel newVarTypeLabel;
    private javax.swing.JTextField newVarValueField;
    private javax.swing.JLabel newVarValueLabel;
    private javax.swing.JFormattedTextField originXField;
    private javax.swing.JLabel originXLabel;
    private javax.swing.JFormattedTextField originYField;
    private javax.swing.JLabel originYLabel;
    private javax.swing.JButton previewTextBtn;
    private javax.swing.JPanel radialColliderPanel;
    private javax.swing.JFormattedTextField radialColliderRadiusField;
    private javax.swing.JLabel radialColliderRadiusLabel;
    private javax.swing.JTextArea resultTextArea;
    private javax.swing.JScrollPane resultTextAreaScroll;
    private javax.swing.JFrame resultTextFrame;
    private javax.swing.JButton saveChangesBtn;
    private javax.swing.JButton saveEntityCancelBtn;
    private javax.swing.JButton saveEntityConfBtn;
    private javax.swing.JDialog saveEntityFrame;
    private javax.swing.JLabel saveEntityLabel;
    private javax.swing.JLabel saveEntityLine1Label;
    private javax.swing.JLabel saveEntityLine2Label;
    private javax.swing.JSeparator separator1;
    private javax.swing.JSeparator separator2;
    private javax.swing.JSeparator separator3;
    private javax.swing.JSeparator separator4;
    private javax.swing.JLabel varsLabel;
    private javax.swing.JPanel varsPanel;
    private javax.swing.JTable varsTable;
    private javax.swing.JScrollPane varsTableScroll;
    // End of variables declaration//GEN-END:variables
}
