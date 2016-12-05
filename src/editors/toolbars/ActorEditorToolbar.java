/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editors.toolbars;

import engine.game.actors.Actor;
import engine.game.actors.AnimFrame;
import engine.environment.ResMgr;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Emil Simon
 */
public class ActorEditorToolbar extends javax.swing.JFrame {
     
    public String currentActorName;
    public String currentAnim;
    public Actor currentActor;
    
    public boolean gridlines=false;
    public boolean previewAnim = false;
    public boolean changed=false;
    
    
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
    public class SpriteDropDown extends AbstractListModel implements ComboBoxModel {
        public String[] data = ResMgr.sprite_lib.keySet().toArray ( new String [ResMgr.sprite_lib.keySet().size()] );
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
    public class AnimDropDown extends AbstractListModel implements ComboBoxModel {
        public String[] data = (ResMgr.hasActor(currentActorName) && (!ResMgr.getActor(currentActorName).anims.isEmpty())) ? ResMgr.getActor(currentActorName).anims.keySet().toArray (new String [ResMgr.getActor(currentActorName).anims.keySet().size()] ) : new String [] { "None found !" };
        public String selection = data[0];
        
        @Override
        public int getSize() {
            if (data == null)
                return 0;
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
    
    
    
    // Custom ComboBox (drop down list) listener class
    public class ActorDropDownListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (changed) {
                changed = false;
                saveActorFrame.setSize(260, 170);
                saveActorFrame.setVisible(true);
                saveActorFrame.setLocation(300, 0);
            } else {
                loadActor();
            }
        }
    }
    
    // Custom ComboBox (drop down list) listener class
    public class SpriteDropDownListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            changed = (!currentActor.sheet.equals(spriteDropDown.getSelectedItem().toString()));
            currentActor.sheet = spriteDropDown.getSelectedItem().toString();
            dimensionField.setText( ResMgr.getAnimatedSprite(currentActor.sheet).dimX + " x " + ResMgr.getAnimatedSprite(currentActor.sheet).dimY );
        }
    }
    
    // Custom ComboBox (drop down list) listener class
    public class AnimDropDownListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (animationDropDown.getSelectedIndex()!=-1) {
                currentAnim = animationDropDown.getSelectedItem().toString();
            } else {
                currentAnim = "";
            }
            updateFrames();
        }
    }
    
    // Custom ComboBox (drop down list) listener class
    public class DefaultAnimDropDownListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (defaultAnimationDropDown.getSelectedIndex()!=-1) {
                currentActor.default_anim = defaultAnimationDropDown.getSelectedItem().toString();
            }
        }
    }
    
    
    
    // Utility function for selection within the actual game container
    public void setSelectedFrame (int x, int y) {
        this.frameXField.setText(String.valueOf(x));
        this.frameYField.setText(String.valueOf(y));
    }
    
    // Utility function for updating the frames table
    public final void updateFrames () {
        DefaultTableModel model = (DefaultTableModel) framesTable.getModel();
        model.setRowCount(0);
        if (currentAnim.equals("")) { return; }
        
        AnimFrame[] frames = currentActor.getAnimFrames(animationDropDown.getSelectedItem().toString());
        for (int i=0;i<frames.length;i++) {
            String dur = String.valueOf(frames[i].dur);
            String x = String.valueOf(frames[i].x);
            String y = String.valueOf(frames[i].y);
            Object[] row = {dur, x, y};
            model.addRow(row);
        }
    }
    
    // Utility function for updating all lists to display actor info correctly
    public void loadActor () {
        if (currentActorName.equals("None found!"))
            return;
        
        currentActorName = actorDropDown.getSelectedItem().toString();
        currentActor = ResMgr.getActor(currentActorName);
        animationDropDown.setModel(new AnimDropDown ());
        animationDropDown.setSelectedItem(currentActor.default_anim);
        defaultAnimationDropDown.setModel(new AnimDropDown ());
        defaultAnimationDropDown.setSelectedItem(currentActor.default_anim);
        spriteDropDown.setSelectedItem(currentActor.sheet);
        updateFrames();
    }
    
    
    
    public ActorEditorToolbar() {
        initComponents();
        
        actorDropDown.addActionListener(new ActorDropDownListener ());
        actorDropDown.setModel(new ActorDropDown ());
        
        currentActorName = actorDropDown.getSelectedItem().toString();
        currentActor = ResMgr.getActor(currentActorName);
        
        animationDropDown.addActionListener(new AnimDropDownListener ());
        animationDropDown.setModel(new AnimDropDown ());
        animationDropDown.setSelectedItem(currentActor.default_anim);
        defaultAnimationDropDown.addActionListener(new DefaultAnimDropDownListener ());
        defaultAnimationDropDown.setModel(new AnimDropDown ());
        defaultAnimationDropDown.setSelectedItem(currentActor.default_anim);
        currentAnim = defaultAnimationDropDown.getSelectedItem().toString();
        updateFrames();
        
        spriteDropDown.addActionListener(new SpriteDropDownListener ());
        spriteDropDown.setModel(new SpriteDropDown ());
        spriteDropDown.setSelectedItem(currentActor.sheet);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        resultTextFrame = new javax.swing.JFrame();
        resultTextAreaScroll = new javax.swing.JScrollPane();
        resultTextArea = new javax.swing.JTextArea();
        addNewActorFrame = new javax.swing.JDialog();
        addNewActorNameLabel = new javax.swing.JLabel();
        addNewActorNameField = new javax.swing.JTextField();
        addNewActorConfBtn = new javax.swing.JButton();
        addNewActorCancelBtn = new javax.swing.JButton();
        addNewActorErrorLabel = new javax.swing.JLabel();
        deleteActorConfFrame = new javax.swing.JDialog();
        deleteActorConfLine1Label = new javax.swing.JLabel();
        deleteActorConfLabel = new javax.swing.JLabel();
        deleteActorConfBtn = new javax.swing.JButton();
        deleteActorCancelBtn = new javax.swing.JButton();
        deleteActorConfLine2Label = new javax.swing.JLabel();
        saveActorFrame = new javax.swing.JDialog();
        saveActorLabel = new javax.swing.JLabel();
        saveActorLine1Label = new javax.swing.JLabel();
        saveActorLine2Label = new javax.swing.JLabel();
        saveActorConfBtn = new javax.swing.JButton();
        saveActorCancelBtn = new javax.swing.JButton();
        addNewAnimFrame = new javax.swing.JDialog();
        addNewAnimNameLabel = new javax.swing.JLabel();
        addNewAnimNameField = new javax.swing.JTextField();
        addNewAnimConfBtn = new javax.swing.JButton();
        addNewAnimCancelBtn = new javax.swing.JButton();
        addNewAnimErrorLabel = new javax.swing.JLabel();
        deleteAnimConfFrame = new javax.swing.JDialog();
        deleteAnimConfLine1Label = new javax.swing.JLabel();
        deleteAnimConfLabel = new javax.swing.JLabel();
        deleteAnimConfBtn = new javax.swing.JButton();
        deleteAnimCancelBtn = new javax.swing.JButton();
        deleteAnimConfLine2Label = new javax.swing.JLabel();
        actorDropDown = new javax.swing.JComboBox();
        actorLabel = new javax.swing.JLabel();
        separator1 = new javax.swing.JSeparator();
        currentAnimationLabel = new javax.swing.JLabel();
        animationDropDown = new javax.swing.JComboBox();
        framesTableScroll = new javax.swing.JScrollPane();
        framesTable = new javax.swing.JTable();
        framesPanel = new javax.swing.JPanel();
        addFrameBtn = new javax.swing.JButton();
        frameYField = new javax.swing.JFormattedTextField();
        frameYLabel = new javax.swing.JLabel();
        frameXLabel = new javax.swing.JLabel();
        frameXField = new javax.swing.JFormattedTextField();
        removeFrameBtn = new javax.swing.JButton();
        frameDurationLabel = new javax.swing.JLabel();
        frameDurationField = new javax.swing.JFormattedTextField();
        addAnimationBtn = new javax.swing.JButton();
        separator3 = new javax.swing.JSeparator();
        addActorBtn = new javax.swing.JButton();
        deleteActorBtn = new javax.swing.JButton();
        deleteAnimationBtn = new javax.swing.JButton();
        separator4 = new javax.swing.JSeparator();
        gridlinesLabel = new javax.swing.JLabel();
        gridlinesBtn = new javax.swing.JToggleButton();
        spriteFrameLabel = new javax.swing.JLabel();
        dimensionField = new javax.swing.JTextField();
        saveChangesBtn = new javax.swing.JButton();
        spriteDropDown = new javax.swing.JComboBox();
        spriteLabel = new javax.swing.JLabel();
        separator2 = new javax.swing.JSeparator();
        prevOrSpriteBtn = new javax.swing.JToggleButton();
        previewTextBtn = new javax.swing.JButton();
        defaultAnimationLabel = new javax.swing.JLabel();
        defaultAnimationDropDown = new javax.swing.JComboBox();

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

        addNewActorFrame.setTitle("New Actor");

        addNewActorNameLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addNewActorNameLabel.setText("Name: ");

        addNewActorNameField.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addNewActorNameField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        addNewActorConfBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addNewActorConfBtn.setText("Add");
        addNewActorConfBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewActorConfBtnActionPerformed(evt);
            }
        });

        addNewActorCancelBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addNewActorCancelBtn.setText("Cancel");
        addNewActorCancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewActorCancelBtnActionPerformed(evt);
            }
        });

        addNewActorErrorLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addNewActorErrorLabel.setForeground(new java.awt.Color(255, 0, 0));
        addNewActorErrorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        addNewActorErrorLabel.setText("SOME ERROR TEXT");

        javax.swing.GroupLayout addNewActorFrameLayout = new javax.swing.GroupLayout(addNewActorFrame.getContentPane());
        addNewActorFrame.getContentPane().setLayout(addNewActorFrameLayout);
        addNewActorFrameLayout.setHorizontalGroup(
            addNewActorFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addNewActorFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addNewActorFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(addNewActorFrameLayout.createSequentialGroup()
                        .addComponent(addNewActorNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addNewActorNameField))
                    .addGroup(addNewActorFrameLayout.createSequentialGroup()
                        .addComponent(addNewActorConfBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                        .addComponent(addNewActorCancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(addNewActorErrorLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        addNewActorFrameLayout.setVerticalGroup(
            addNewActorFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addNewActorFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addNewActorFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addNewActorNameLabel)
                    .addComponent(addNewActorNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(addNewActorFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addNewActorConfBtn)
                    .addComponent(addNewActorCancelBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addNewActorErrorLabel))
        );

        deleteActorConfLine1Label.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        deleteActorConfLine1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        deleteActorConfLine1Label.setText("This will remove the selected actor completely.");

        deleteActorConfLabel.setFont(new java.awt.Font("Calibri", 0, 24)); // NOI18N
        deleteActorConfLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        deleteActorConfLabel.setText("Are you sure?");

        deleteActorConfBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        deleteActorConfBtn.setText("YES");
        deleteActorConfBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActorConfBtnActionPerformed(evt);
            }
        });

        deleteActorCancelBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        deleteActorCancelBtn.setText("NO");
        deleteActorCancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActorCancelBtnActionPerformed(evt);
            }
        });

        deleteActorConfLine2Label.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        deleteActorConfLine2Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        deleteActorConfLine2Label.setText(" This action cannot be undone.");

        javax.swing.GroupLayout deleteActorConfFrameLayout = new javax.swing.GroupLayout(deleteActorConfFrame.getContentPane());
        deleteActorConfFrame.getContentPane().setLayout(deleteActorConfFrameLayout);
        deleteActorConfFrameLayout.setHorizontalGroup(
            deleteActorConfFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deleteActorConfFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(deleteActorConfFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(deleteActorConfLine1Label, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(deleteActorConfLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(deleteActorConfFrameLayout.createSequentialGroup()
                        .addComponent(deleteActorConfBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deleteActorCancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(deleteActorConfLine2Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        deleteActorConfFrameLayout.setVerticalGroup(
            deleteActorConfFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deleteActorConfFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(deleteActorConfLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteActorConfLine1Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteActorConfLine2Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(deleteActorConfFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteActorConfBtn)
                    .addComponent(deleteActorCancelBtn))
                .addContainerGap())
        );

        saveActorLabel.setFont(new java.awt.Font("Calibri", 0, 24)); // NOI18N
        saveActorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        saveActorLabel.setText("Unsaved Changes");

        saveActorLine1Label.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        saveActorLine1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        saveActorLine1Label.setText("You have made some unsaved changes to");

        saveActorLine2Label.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        saveActorLine2Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        saveActorLine2Label.setText("this Actor. Would you like to save now?");

        saveActorConfBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        saveActorConfBtn.setText("SAVE");
        saveActorConfBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActorConfBtnActionPerformed(evt);
            }
        });

        saveActorCancelBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        saveActorCancelBtn.setText("CONTINUE");
        saveActorCancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActorCancelBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout saveActorFrameLayout = new javax.swing.GroupLayout(saveActorFrame.getContentPane());
        saveActorFrame.getContentPane().setLayout(saveActorFrameLayout);
        saveActorFrameLayout.setHorizontalGroup(
            saveActorFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(saveActorFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(saveActorFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saveActorLine1Label, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addComponent(saveActorLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(saveActorFrameLayout.createSequentialGroup()
                        .addComponent(saveActorConfBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(saveActorCancelBtn))
                    .addComponent(saveActorLine2Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        saveActorFrameLayout.setVerticalGroup(
            saveActorFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(saveActorFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(saveActorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveActorLine1Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveActorLine2Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(saveActorFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveActorConfBtn)
                    .addComponent(saveActorCancelBtn))
                .addContainerGap())
        );

        addNewAnimNameLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addNewAnimNameLabel.setText("Name: ");

        addNewAnimNameField.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addNewAnimNameField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        addNewAnimConfBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addNewAnimConfBtn.setText("Add");
        addNewAnimConfBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewAnimConfBtnActionPerformed(evt);
            }
        });

        addNewAnimCancelBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addNewAnimCancelBtn.setText("Cancel");
        addNewAnimCancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewAnimCancelBtnActionPerformed(evt);
            }
        });

        addNewAnimErrorLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addNewAnimErrorLabel.setForeground(new java.awt.Color(255, 0, 0));
        addNewAnimErrorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        addNewAnimErrorLabel.setText("SOME ERROR TEXT");

        javax.swing.GroupLayout addNewAnimFrameLayout = new javax.swing.GroupLayout(addNewAnimFrame.getContentPane());
        addNewAnimFrame.getContentPane().setLayout(addNewAnimFrameLayout);
        addNewAnimFrameLayout.setHorizontalGroup(
            addNewAnimFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addNewAnimFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addNewAnimFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(addNewAnimFrameLayout.createSequentialGroup()
                        .addComponent(addNewAnimNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addNewAnimNameField))
                    .addGroup(addNewAnimFrameLayout.createSequentialGroup()
                        .addComponent(addNewAnimConfBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                        .addComponent(addNewAnimCancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(addNewAnimErrorLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        addNewAnimFrameLayout.setVerticalGroup(
            addNewAnimFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addNewAnimFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addNewAnimFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addNewAnimNameLabel)
                    .addComponent(addNewAnimNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(addNewAnimFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addNewAnimConfBtn)
                    .addComponent(addNewAnimCancelBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addNewAnimErrorLabel))
        );

        deleteAnimConfLine1Label.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        deleteAnimConfLine1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        deleteAnimConfLine1Label.setText("This will remove the selected animation and all");

        deleteAnimConfLabel.setFont(new java.awt.Font("Calibri", 0, 24)); // NOI18N
        deleteAnimConfLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        deleteAnimConfLabel.setText("Are you sure?");

        deleteAnimConfBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        deleteAnimConfBtn.setText("YES");
        deleteAnimConfBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAnimConfBtnActionPerformed(evt);
            }
        });

        deleteAnimCancelBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        deleteAnimCancelBtn.setText("NO");
        deleteAnimCancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAnimCancelBtnActionPerformed(evt);
            }
        });

        deleteAnimConfLine2Label.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        deleteAnimConfLine2Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        deleteAnimConfLine2Label.setText("added frame info. This action cannot be undone.");

        javax.swing.GroupLayout deleteAnimConfFrameLayout = new javax.swing.GroupLayout(deleteAnimConfFrame.getContentPane());
        deleteAnimConfFrame.getContentPane().setLayout(deleteAnimConfFrameLayout);
        deleteAnimConfFrameLayout.setHorizontalGroup(
            deleteAnimConfFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deleteAnimConfFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(deleteAnimConfFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(deleteAnimConfLine1Label, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(deleteAnimConfLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(deleteAnimConfFrameLayout.createSequentialGroup()
                        .addComponent(deleteAnimConfBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deleteAnimCancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(deleteAnimConfLine2Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        deleteAnimConfFrameLayout.setVerticalGroup(
            deleteAnimConfFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deleteAnimConfFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(deleteAnimConfLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteAnimConfLine1Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteAnimConfLine2Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(deleteAnimConfFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteAnimConfBtn)
                    .addComponent(deleteAnimCancelBtn))
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Actor Editor");

        actorDropDown.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        actorDropDown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        actorDropDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actorDropDownActionPerformed(evt);
            }
        });

        actorLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        actorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        actorLabel.setText("Current Actor:");

        currentAnimationLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        currentAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        currentAnimationLabel.setText("Current Animation:");

        animationDropDown.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        animationDropDown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        framesTable.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        framesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Dur", "X", "Y"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
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
        framesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        framesTable.setShowHorizontalLines(false);
        framesTable.getTableHeader().setReorderingAllowed(false);
        framesTableScroll.setViewportView(framesTable);
        framesTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        framesPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        framesPanel.setAutoscrolls(true);

        addFrameBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addFrameBtn.setText("Add Frame");
        addFrameBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFrameBtnActionPerformed(evt);
            }
        });

        frameYField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        frameYField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        frameYField.setText("0");

        frameYLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        frameYLabel.setText("Frame Y:");

        frameXLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        frameXLabel.setText("Frame X:");

        frameXField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        frameXField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        frameXField.setText("0");

        removeFrameBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        removeFrameBtn.setText("Remove Frame");
        removeFrameBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFrameBtnActionPerformed(evt);
            }
        });

        frameDurationLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        frameDurationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        frameDurationLabel.setText("Frame Duration:");

        frameDurationField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        frameDurationField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        frameDurationField.setText("200");

        javax.swing.GroupLayout framesPanelLayout = new javax.swing.GroupLayout(framesPanel);
        framesPanel.setLayout(framesPanelLayout);
        framesPanelLayout.setHorizontalGroup(
            framesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(framesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(framesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(removeFrameBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(frameDurationField)
                    .addComponent(frameDurationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, framesPanelLayout.createSequentialGroup()
                        .addGap(0, 10, Short.MAX_VALUE)
                        .addGroup(framesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(frameYLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(frameXLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(framesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(frameYField)
                            .addComponent(frameXField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(addFrameBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        framesPanelLayout.setVerticalGroup(
            framesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(framesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(framesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(frameXField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(frameXLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(framesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(frameYField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(frameYLabel))
                .addGap(18, 18, 18)
                .addComponent(frameDurationLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(frameDurationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addFrameBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeFrameBtn)
                .addContainerGap())
        );

        addAnimationBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addAnimationBtn.setText("Add New Animation");
        addAnimationBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAnimationBtnActionPerformed(evt);
            }
        });

        addActorBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        addActorBtn.setText("Add New Actor");
        addActorBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActorBtnActionPerformed(evt);
            }
        });

        deleteActorBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        deleteActorBtn.setText("Delete Current Actor");
        deleteActorBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActorBtnActionPerformed(evt);
            }
        });

        deleteAnimationBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        deleteAnimationBtn.setText("Delete Animation");
        deleteAnimationBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAnimationBtnActionPerformed(evt);
            }
        });

        gridlinesLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        gridlinesLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        gridlinesLabel.setText("Draw Gridlines: ");

        gridlinesBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        gridlinesBtn.setText("No");
        gridlinesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gridlinesBtnActionPerformed(evt);
            }
        });

        spriteFrameLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        spriteFrameLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        spriteFrameLabel.setText("SpriteSheet Frame Dimensions: ");

        dimensionField.setEditable(false);
        dimensionField.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        dimensionField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        dimensionField.setText("32 x 32");

        saveChangesBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        saveChangesBtn.setText("Save Changes to Current Actor");
        saveChangesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveChangesBtnActionPerformed(evt);
            }
        });

        spriteDropDown.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        spriteDropDown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        spriteDropDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spriteDropDownActionPerformed(evt);
            }
        });

        spriteLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        spriteLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        spriteLabel.setText("Using Sprite:");

        prevOrSpriteBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        prevOrSpriteBtn.setText("Frame View");
        prevOrSpriteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevOrSpriteBtnActionPerformed(evt);
            }
        });

        previewTextBtn.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        previewTextBtn.setText("Preview .act File Result");
        previewTextBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewTextBtnActionPerformed(evt);
            }
        });

        defaultAnimationLabel.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        defaultAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        defaultAnimationLabel.setText("Default Animation:");

        defaultAnimationDropDown.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        defaultAnimationDropDown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(separator1)
                    .addComponent(separator3)
                    .addComponent(separator4)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(framesTableScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(framesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(spriteFrameLabel)
                            .addComponent(gridlinesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dimensionField)
                            .addComponent(gridlinesBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addAnimationBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deleteAnimationBtn))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(currentAnimationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(animationDropDown, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(saveChangesBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(actorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(actorDropDown, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addActorBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deleteActorBtn))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(spriteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spriteDropDown, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(separator2)
                    .addComponent(prevOrSpriteBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(previewTextBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(defaultAnimationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(defaultAnimationDropDown, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(actorDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(actorLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addActorBtn)
                    .addComponent(deleteActorBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveChangesBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(separator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spriteDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spriteLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(defaultAnimationDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(defaultAnimationLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(separator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(animationDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(currentAnimationLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addAnimationBtn)
                    .addComponent(deleteAnimationBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(separator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(framesTableScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(framesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(separator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gridlinesLabel)
                    .addComponent(gridlinesBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spriteFrameLabel)
                    .addComponent(dimensionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(previewTextBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(prevOrSpriteBtn)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void actorDropDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actorDropDownActionPerformed
        
    }//GEN-LAST:event_actorDropDownActionPerformed

    private void gridlinesBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gridlinesBtnActionPerformed
        gridlines = !gridlines;
        gridlinesBtn.setText(gridlines ? "Yes" : "No");
    }//GEN-LAST:event_gridlinesBtnActionPerformed

    private void previewTextBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewTextBtnActionPerformed
        resultTextArea.setText(currentActor.getWritten());
        resultTextFrame.setTitle("'"+currentActorName+".act' contents");
        resultTextFrame.setSize(400,600);
        resultTextFrame.setLocationRelativeTo(this);
        resultTextFrame.setLocation(300, 0);
        resultTextFrame.setVisible(true);
    }//GEN-LAST:event_previewTextBtnActionPerformed

    private void spriteDropDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spriteDropDownActionPerformed
        
    }//GEN-LAST:event_spriteDropDownActionPerformed

    private void prevOrSpriteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevOrSpriteBtnActionPerformed
        previewAnim = !previewAnim;
        prevOrSpriteBtn.setText(previewAnim ? "Animation Preview" : "Frame View");
    }//GEN-LAST:event_prevOrSpriteBtnActionPerformed

    private void removeFrameBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFrameBtnActionPerformed
        if (framesTable.getSelectedRow() != -1) {
            changed = true;
            currentActor.removeFrame(currentAnim, framesTable.getSelectedRow());
            updateFrames();
        }
    }//GEN-LAST:event_removeFrameBtnActionPerformed

    private void addFrameBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFrameBtnActionPerformed
        if (!frameDurationField.getText().isEmpty() && !frameXField.getText().isEmpty() && !frameYField.getText().isEmpty()) {
            changed = true;
            currentActor.addFrame(currentAnim, Integer.parseInt(frameDurationField.getText()),
                    Integer.parseInt(frameXField.getText()), Integer.parseInt(frameYField.getText()));
            updateFrames();
        }
    }//GEN-LAST:event_addFrameBtnActionPerformed

    private void saveChangesBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveChangesBtnActionPerformed
        ResMgr.actor_lib.put(currentActorName, currentActor);
        ResMgr.getActor(currentActorName).writeToFile();
        ResMgr.writeActors();
        this.changed = false;
    }//GEN-LAST:event_saveChangesBtnActionPerformed

    private void deleteActorBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActorBtnActionPerformed
        deleteActorConfFrame.setSize(300, 180);
        deleteActorConfFrame.setVisible(true);
        deleteActorConfFrame.setLocationRelativeTo(this);
        deleteActorConfFrame.setLocation(300, 0);
    }//GEN-LAST:event_deleteActorBtnActionPerformed

    private void addNewActorCancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewActorCancelBtnActionPerformed
        addNewActorFrame.setVisible(false);
    }//GEN-LAST:event_addNewActorCancelBtnActionPerformed

    private void addNewActorConfBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewActorConfBtnActionPerformed
        String desiredName = addNewActorNameField.getText();
        
        if (desiredName.isEmpty()) {
            addNewActorErrorLabel.setText("Actor must have a name!");
        } else if (ResMgr.hasActor(desiredName)) {
            addNewActorErrorLabel.setText("Actor already exists!");
        } else {
            try {
                addNewActorFrame.setVisible(false);
                ResMgr.addActor(desiredName, new Actor (desiredName));
                ResMgr.getActor(desiredName).sheet = spriteDropDown.getSelectedItem().toString();
                actorDropDown.setModel(new ActorDropDown ());
                actorDropDown.setSelectedItem(desiredName);
                loadActor();
            } catch (IOException ex) {
                Logger.getLogger(ActorEditorToolbar.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_addNewActorConfBtnActionPerformed

    private void addActorBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActorBtnActionPerformed
        addNewActorErrorLabel.setText("");
        addNewActorFrame.setSize(270, 120);
        addNewActorFrame.setVisible(true);
        addNewActorFrame.setLocationRelativeTo(this);
        addNewActorFrame.setLocation(300, 0);
    }//GEN-LAST:event_addActorBtnActionPerformed

    private void deleteActorCancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActorCancelBtnActionPerformed
        deleteActorConfFrame.setVisible(false);
    }//GEN-LAST:event_deleteActorCancelBtnActionPerformed

    private void deleteActorConfBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActorConfBtnActionPerformed
        deleteActorConfFrame.setVisible(false);
        ResMgr.deleteActor(currentActorName);
        actorDropDown.setModel(new ActorDropDown ());
        actorDropDown.setSelectedIndex(0);
    }//GEN-LAST:event_deleteActorConfBtnActionPerformed

    private void saveActorConfBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActorConfBtnActionPerformed
        saveChangesBtnActionPerformed(evt);
        saveActorFrame.setVisible(false);
        loadActor();
    }//GEN-LAST:event_saveActorConfBtnActionPerformed

    private void saveActorCancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActorCancelBtnActionPerformed
        saveActorFrame.setVisible(false);
        loadActor();
    }//GEN-LAST:event_saveActorCancelBtnActionPerformed

    private void addAnimationBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAnimationBtnActionPerformed
        addNewAnimErrorLabel.setText("");
        addNewAnimFrame.setSize(270, 120);
        addNewAnimFrame.setVisible(true);
        addNewAnimFrame.setLocationRelativeTo(this);
        addNewAnimFrame.setLocation(300, 0);
    }//GEN-LAST:event_addAnimationBtnActionPerformed

    private void deleteAnimationBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAnimationBtnActionPerformed
        deleteAnimConfFrame.setVisible(true);
        deleteAnimConfFrame.setSize(300, 180);
        deleteAnimConfFrame.setVisible(true);
        deleteAnimConfFrame.setLocationRelativeTo(this);
        deleteAnimConfFrame.setLocation(300, 0);
    }//GEN-LAST:event_deleteAnimationBtnActionPerformed

    private void addNewAnimConfBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewAnimConfBtnActionPerformed
        changed = true;
        String desiredName = addNewAnimNameField.getText();
        
        if (desiredName.isEmpty()) {
            addNewAnimErrorLabel.setText("Animation must have a name!");
        } else if (currentActor.anims.containsKey(desiredName)) {
            addNewAnimErrorLabel.setText("Animation already exists!");
        } else {
            addNewAnimFrame.setVisible(false);
            currentActor.addAnim(desiredName);
            loadActor();
            animationDropDown.setSelectedItem(desiredName);
        }
    }//GEN-LAST:event_addNewAnimConfBtnActionPerformed

    private void addNewAnimCancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewAnimCancelBtnActionPerformed
        addNewAnimFrame.setVisible(false);
    }//GEN-LAST:event_addNewAnimCancelBtnActionPerformed

    private void deleteAnimConfBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAnimConfBtnActionPerformed
        changed = true;
        deleteAnimConfFrame.setVisible(false);
        currentActor.removeAnim(currentAnim);
        animationDropDown.setModel(new AnimDropDown ());
        loadActor();
    }//GEN-LAST:event_deleteAnimConfBtnActionPerformed

    private void deleteAnimCancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAnimCancelBtnActionPerformed
        deleteAnimConfFrame.setVisible(false);
    }//GEN-LAST:event_deleteAnimCancelBtnActionPerformed

     
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
            new ActorEditorToolbar().setVisible(true);
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox actorDropDown;
    private javax.swing.JLabel actorLabel;
    private javax.swing.JButton addActorBtn;
    private javax.swing.JButton addAnimationBtn;
    private javax.swing.JButton addFrameBtn;
    private javax.swing.JButton addNewActorCancelBtn;
    private javax.swing.JButton addNewActorConfBtn;
    private javax.swing.JLabel addNewActorErrorLabel;
    private javax.swing.JDialog addNewActorFrame;
    private javax.swing.JTextField addNewActorNameField;
    private javax.swing.JLabel addNewActorNameLabel;
    private javax.swing.JButton addNewAnimCancelBtn;
    private javax.swing.JButton addNewAnimConfBtn;
    private javax.swing.JLabel addNewAnimErrorLabel;
    private javax.swing.JDialog addNewAnimFrame;
    private javax.swing.JTextField addNewAnimNameField;
    private javax.swing.JLabel addNewAnimNameLabel;
    private javax.swing.JComboBox animationDropDown;
    private javax.swing.JLabel currentAnimationLabel;
    private javax.swing.JComboBox defaultAnimationDropDown;
    private javax.swing.JLabel defaultAnimationLabel;
    private javax.swing.JButton deleteActorBtn;
    private javax.swing.JButton deleteActorCancelBtn;
    private javax.swing.JButton deleteActorConfBtn;
    private javax.swing.JDialog deleteActorConfFrame;
    private javax.swing.JLabel deleteActorConfLabel;
    private javax.swing.JLabel deleteActorConfLine1Label;
    private javax.swing.JLabel deleteActorConfLine2Label;
    private javax.swing.JButton deleteAnimCancelBtn;
    private javax.swing.JButton deleteAnimConfBtn;
    private javax.swing.JDialog deleteAnimConfFrame;
    private javax.swing.JLabel deleteAnimConfLabel;
    private javax.swing.JLabel deleteAnimConfLine1Label;
    private javax.swing.JLabel deleteAnimConfLine2Label;
    private javax.swing.JButton deleteAnimationBtn;
    private javax.swing.JTextField dimensionField;
    private javax.swing.JFormattedTextField frameDurationField;
    private javax.swing.JLabel frameDurationLabel;
    private javax.swing.JFormattedTextField frameXField;
    private javax.swing.JLabel frameXLabel;
    private javax.swing.JFormattedTextField frameYField;
    private javax.swing.JLabel frameYLabel;
    private javax.swing.JPanel framesPanel;
    private javax.swing.JTable framesTable;
    private javax.swing.JScrollPane framesTableScroll;
    private javax.swing.JToggleButton gridlinesBtn;
    private javax.swing.JLabel gridlinesLabel;
    private javax.swing.JToggleButton prevOrSpriteBtn;
    private javax.swing.JButton previewTextBtn;
    private javax.swing.JButton removeFrameBtn;
    private javax.swing.JTextArea resultTextArea;
    private javax.swing.JScrollPane resultTextAreaScroll;
    private javax.swing.JFrame resultTextFrame;
    private javax.swing.JButton saveActorCancelBtn;
    private javax.swing.JButton saveActorConfBtn;
    private javax.swing.JDialog saveActorFrame;
    private javax.swing.JLabel saveActorLabel;
    private javax.swing.JLabel saveActorLine1Label;
    private javax.swing.JLabel saveActorLine2Label;
    private javax.swing.JButton saveChangesBtn;
    private javax.swing.JSeparator separator1;
    private javax.swing.JSeparator separator2;
    private javax.swing.JSeparator separator3;
    private javax.swing.JSeparator separator4;
    private javax.swing.JComboBox spriteDropDown;
    private javax.swing.JLabel spriteFrameLabel;
    private javax.swing.JLabel spriteLabel;
    // End of variables declaration//GEN-END:variables
}
