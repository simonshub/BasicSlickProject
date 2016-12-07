/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.maps;

import engine.game.actors.AnimatedSprite;
import engine.environment.Consts;
import engine.environment.ResMgr;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * @author Emil Simon
 */

public class Tileset extends AnimatedSprite {
    public class DirectionalTile {
        public boolean up_left, up_right, down_left, down_right;
        public int img_x, img_y;
         
        public DirectionalTile (boolean ul, boolean ur, boolean dl, boolean dr, int x, int y) {
            up_left=ul;  up_right=ur;  down_left=dl;  down_right=dr;  img_x=x;  img_y=y;
        }
        
        public boolean equals (boolean ul, boolean ur, boolean dl, boolean dr) {
            return ((ul==up_left) && (ur==up_right) && (dl==down_left) && (dr==down_right));
        }
    }
     
    public final DirectionalTile FILL = new DirectionalTile (true,true,true,true, 0,0);
    public final DirectionalTile VOID = new DirectionalTile (false,false,false,false, 3,0);
    public final DirectionalTile DIAGONAL_DL_UR = new DirectionalTile (false,true,true,false, 1,0);
    public final DirectionalTile DIAGONAL_UL_DR = new DirectionalTile (true,false,false,true, 2,0);
    public final DirectionalTile CORNER_UL = new DirectionalTile (true,false,false,false, 1,1);
    public final DirectionalTile CORNER_UR = new DirectionalTile (false,true,false,false, 0,1);
    public final DirectionalTile CORNER_DL = new DirectionalTile (false,false,true,false, 3,1);
    public final DirectionalTile CORNER_DR = new DirectionalTile (false,false,false,true, 2,1);
    public final DirectionalTile LINE_D = new DirectionalTile (false,false,true,true, 0,2);
    public final DirectionalTile LINE_U = new DirectionalTile (true,true,false,false, 2,2);
    public final DirectionalTile LINE_L = new DirectionalTile (true,false,true,false, 1,2);
    public final DirectionalTile LINE_R = new DirectionalTile (false,true,false,true, 3,2);
    public final DirectionalTile CUT_UL = new DirectionalTile (false,true,true,true, 1,3);
    public final DirectionalTile CUT_UR = new DirectionalTile (true,false,true,true, 0,3);
    public final DirectionalTile CUT_DL = new DirectionalTile (true,true,false,true, 3,3);
    public final DirectionalTile CUT_DR = new DirectionalTile (true,true,true,false, 2,3);
    
    public int id;
     
    public Tileset(String name, String spriteSheetPath, int id) throws SlickException {
        super(name, spriteSheetPath, Consts.TILESET_FRAME_WIDTH, Consts.TILESET_FRAME_HEIGHT);
        this.id = id;
    }
     
    public Image getTile (boolean ul, boolean ur, boolean dl, boolean dr) {
        if (FILL.equals(ul, ur, dl, dr)) {
            return sheet.getSubImage(FILL.img_x, FILL.img_y);
        } else if (VOID.equals(ul, ur, dl, dr)) {
            //return sheet.getSubImage(VOID.img_x, VOID.img_y);
            return null;
        } else if (DIAGONAL_DL_UR.equals(ul, ur, dl, dr)) {
            return sheet.getSubImage(DIAGONAL_DL_UR.img_x, DIAGONAL_DL_UR.img_y);
        } else if (DIAGONAL_UL_DR.equals(ul, ur, dl, dr)) {
            return sheet.getSubImage(DIAGONAL_UL_DR.img_x, DIAGONAL_UL_DR.img_y);
        } else if (CORNER_UL.equals(ul, ur, dl, dr)) {
            return sheet.getSubImage(CORNER_UL.img_x, CORNER_UL.img_y);
        } else if (CORNER_UR.equals(ul, ur, dl, dr)) {
            return sheet.getSubImage(CORNER_UR.img_x, CORNER_UR.img_y);
        } else if (CORNER_DL.equals(ul, ur, dl, dr)) {
            return sheet.getSubImage(CORNER_DL.img_x, CORNER_DL.img_y);
        } else if (CORNER_DR.equals(ul, ur, dl, dr)) {
            return sheet.getSubImage(CORNER_DR.img_x, CORNER_DR.img_y);
        } else if (LINE_D.equals(ul, ur, dl, dr)) {
            return sheet.getSubImage(LINE_D.img_x, LINE_D.img_y);
        } else if (LINE_U.equals(ul, ur, dl, dr)) {
            return sheet.getSubImage(LINE_U.img_x, LINE_U.img_y);
        } else if (LINE_L.equals(ul, ur, dl, dr)) {
            return sheet.getSubImage(LINE_L.img_x, LINE_L.img_y);
        } else if (LINE_R.equals(ul, ur, dl, dr)) {
            return sheet.getSubImage(LINE_R.img_x, LINE_R.img_y);
        } else if (CUT_UL.equals(ul, ur, dl, dr)) {
            return sheet.getSubImage(CUT_UL.img_x, CUT_UL.img_y);
        } else if (CUT_UR.equals(ul, ur, dl, dr)) {
            return sheet.getSubImage(CUT_UR.img_x, CUT_UR.img_y);
        } else if (CUT_DL.equals(ul, ur, dl, dr)) {
            return sheet.getSubImage(CUT_DL.img_x, CUT_DL.img_y);
        } else if (CUT_DR.equals(ul, ur, dl, dr)) {
            return sheet.getSubImage(CUT_DR.img_x, CUT_DR.img_y);
        }
        
        return null;
    }
}
