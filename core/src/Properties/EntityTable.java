package Properties;

import java.util.ArrayList;

import com.afts.editor.Core;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import EditorPanel.EditPanel;
import Parsers.ReadEntityObjectsFromFile;

public class EntityTable {

	private boolean tableActive = false;
	
	private Stage stage;
	private Skin skin;
	private Label title;
	private Label textureInfo;
	private Table scrollTable;
	private Table table;
	private int currentTextureID;
	private int textureIDLastChecked;
	private ScrollPane scroller;
	
	private ArrayList<EntityObject> blockList = new ArrayList<EntityObject>();
	private ArrayList<Label> labelList = new ArrayList<Label>();
	private EntityObject playerFinishPoint;
	private EntityObject playerSpawnPoint;
	
	private Vector2 currentTextureDisplayPosition = new Vector2();
	private Vector2 currentTextureDisplaySize = new Vector2();
	public static Vector2 panelPosition = new Vector2(0,0);
	public static Vector2 panelSize = new Vector2(0,0);
	
	
	private ReadEntityObjectsFromFile entityParser;
	
	public EntityTable(Stage stage, Skin skin)
	{
		this.stage = stage;
		this.skin = skin;
	
		this.table = new Table();
		this.panelSize.set(150.f, 275.f);
		this.panelPosition.set(PropertySettings.propertyPanelWidth, Core.WINDOW_HEIGHT - this.panelSize.y);
		
		entityParser = new ReadEntityObjectsFromFile();
		this.blockList = this.entityParser.getEntityObjectList();
		
		this.setupScrollTable();
		this.tableListener();
	}
	
	private void setupScrollTable()
	{
		
		
		float pad = 2.f;
		this.scrollTable = new Table();
		this.scrollTable.align(Align.left);
		
		Label mapLabel = new Label("Map objects", this.skin);
		mapLabel.setSize(64, 10.f);
		mapLabel.setColor(Color.ORANGE);
		mapLabel.setAlignment(Align.center);
		this.scrollTable.add(mapLabel).fill().expand();
		this.scrollTable.row();
		
		for(int i = 0; i < this.blockList.size(); i++)
		{		
			this.addButton(this.blockList.get(i), pad);
			this.scrollTable.row();
		}

		if(this.playerSpawnPoint != null) {
		this.scrollTable.row();
		Label spawnLabel = new Label("Player spawn", this.skin);
		spawnLabel.setSize(64, 10.f);
		spawnLabel.setColor(Color.ORANGE);
		spawnLabel.setAlignment(Align.left);
		this.scrollTable.add(spawnLabel).fill().expand();
		this.scrollTable.row();
		this.addButton(this.playerSpawnPoint, pad);
		}
		
		if(this.playerFinishPoint != null) {
		this.scrollTable.row();
		Label finishLabel = new Label("Player finish", this.skin);
		finishLabel.setSize(64, 10.f);
		finishLabel.setColor(Color.ORANGE);
		finishLabel.setAlignment(Align.left);
		this.scrollTable.add(finishLabel).fill().expand();
		this.scrollTable.row();
		this.addButton(this.playerFinishPoint, pad);
		}
		
		this.scroller = new ScrollPane(this.scrollTable);
		this.scroller.setScrollingDisabled(true,false);
		
		
		this.table.setSize(132, 132.f);
		this.table.add(this.scroller);
		this.table.align(Align.left);
		this.table.setPosition(this.panelPosition.x + this.panelSize.x / 2.f - this.table.getWidth() / 2.f, this.panelPosition.y + this.panelSize.y / 2.f - this.table.getHeight() / 2.f + 35.f);
			
		this.title = new Label("Entity list: ",this.skin);
		this.title.setPosition(this.table.getX(), this.table.getY() + this.table.getHeight() + 5.f);
		
		this.textureInfo = new Label("Preview: ",this.skin);
		this.textureInfo.setPosition(this.title.getX(), this.title.getY() - this.table.getHeight() - 10.f - this.textureInfo.getHeight());
		
		this.currentTextureDisplaySize.x = 64.f;
		this.currentTextureDisplaySize.y = 64.f;
		this.currentTextureDisplayPosition.x = this.panelPosition.x + this.panelSize.x /2.f - this.currentTextureDisplaySize.x / 2.f;
		this.currentTextureDisplayPosition.y = this.textureInfo.getY() - this.currentTextureDisplaySize.y - 20.f;
		
		this.stage.addActor(this.title);
		this.stage.addActor(this.table);
		this.stage.addActor(this.textureInfo);
		
		
		this.currentTextureID = this.blockList.get(0).getID();
		this.labelList.get(0).setColor(Color.WHITE);
	}
		
	public void renderTextureTablePanel(ShapeRenderer renderer)
	{
		renderer.setProjectionMatrix(this.stage.getCamera().combined);
		renderer.set(ShapeType.Filled);
		renderer.setColor(EditPanel.PANEL_COLOR);
		renderer.rect(this.panelPosition.x, this.panelPosition.y, this.panelSize.x, this.panelSize.y);
	}
	
	public void dispose()
	{
		for(int i = 0; i < this.blockList.size(); i++)
		{
			this.blockList.get(i).getTexture().dispose();
		}
	}
	
	private void addButton(EntityObject obj, float pad)
	{
		final Label lbl = new Label(obj.getTextureName(), this.skin);
			
		lbl.addListener(new ClickListener() {
			
			public void clicked(InputEvent event, float x, float y)
			{
				if(EntityTable.this.tableActive) {
				EntityTable.this.resetColorOfLabels();
				EntityTable.this.currentTextureID = lbl.getZIndex();
				lbl.setColor(Color.WHITE);
				}
			}
		});
	
		lbl.setColor(Color.GRAY);
		labelList.add(lbl);
		this.scrollTable.add(lbl).fill().padLeft(5.f);
		obj.setID(lbl.getZIndex());
	}
	
	private void tableListener()
	{
		this.scrollTable.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y)
			{
				EntityTable.this.tableActive = true;
			}
		});
	}
	
	private void resetColorOfLabels()
	{
		for(int i = 0; i < this.labelList.size(); i++)
		{
			this.labelList.get(i).setColor(Color.GRAY);
		}
	}
	
	private void highlightLabel(int textureID)
	{
		for(int i = 0; i < this.labelList.size(); i++)
		{
			if(this.labelList.get(i).getZIndex() == textureID)
			{
				this.labelList.get(i).setColor(Color.WHITE);
			}
		}
		
	}
	
	public ScrollPane getScrollpane()
	{
		return this.scroller;
	}
	
	public Table getTabel()
	{
		return this.table;
	}
	
	public EntityObject getCurrentTextureObject()
	{
		if(this.playerSpawnPoint != null) {
		if(this.playerSpawnPoint.getID() == this.currentTextureID)
			return playerSpawnPoint;
		}
		
		if(this.playerFinishPoint != null) {
		if(this.playerFinishPoint.getID() == this.currentTextureID)
			return playerFinishPoint;
		}
		
		for(int i = 0; i < this.blockList.size(); i++)
		{
			if(this.blockList.get(i).getID() == this.currentTextureID)
				return this.blockList.get(i);
		}
		
		return null;
	}
	
	public void setCurrentTextureID(int id)
	{
		this.currentTextureID = id;
		this.textureIDLastChecked = id;
		
		this.resetColorOfLabels();
		this.highlightLabel(id);
	}
	
	public int getCurrentTextureID()
	{
		return this.currentTextureID;
	}
	
	public Vector2 getDisplayCurrentTexturePosition()
	{
		return this.currentTextureDisplayPosition;
	}
	
	public Vector2 getDisplayCurrentTextureSize()
	{
		return this.currentTextureDisplaySize;
	}
	
	public boolean textureIDHasBeenUpdated()
	{
		if(this.textureIDLastChecked != this.currentTextureID)
		{
			this.textureIDLastChecked = this.currentTextureID;
			return true;
		}
		
		return false;
	}
	
	public Actor getActorThatHasScrollFocus() {
		return this.stage.getScrollFocus();
	}
	
	public Vector2 getSizeOfPanel()
	{
		return this.panelSize;
	}
	
	public void tableFocus(boolean b)
	{
		this.tableActive = b;
	}
}
