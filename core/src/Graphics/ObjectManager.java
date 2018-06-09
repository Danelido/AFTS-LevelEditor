package Graphics;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import EditorPanel.EditPanel;
import Properties.EntityType;

public class ObjectManager {

	private enum ACTION_FLAG
	{
		WAS_ADDED,
		WAS_REMOVED,
		ROTATION_CHANGED,
		TEXTURE_CHANGED,
		ONCOLLISION_CHANGED,
		COLOR_RGB_CHANGED,
		COLOR_ALPHA_CHANGED,
		SIZE_CHANGED
	}
	
	private class Action
	{
		private Object referenceToObject;
		private Object copyOfObject;
		private ACTION_FLAG flag;
		
		public Action(Object o, ACTION_FLAG flag)
		{
			this.referenceToObject = o;
			this.copyOfObject = new Object(o);
			this.flag = flag;
		}
			
	}
	private final int MAX_ACTION_CACHED = 1000;
	private int actionListIndexPointer = 0;
	private ArrayList<Action> latestActions = new ArrayList<Action>();
	
	private ArrayList<Object> selectedObjects = new ArrayList<Object>();
	public static ArrayList<Object> objects = new ArrayList<Object>();
	private ArrayList<Object> indicateList = new ArrayList<Object>();
		
	private EditPanel settings;
	
	private boolean playerSpawnPointAdded = false;
	private int playerSpawnPointArrayLocation = -1;
	
	public ObjectManager(EditPanel settings)
	{
		this.settings = settings;
	
	}
	
	public void indicate_singular(Object object) 
	{
	
		if(this.indicateList.size() == 0){
			this.indicateList.add(object);
		}
		
		if(this.indicateList.size() > 1) {
			this.indicateList.clear();
		}
		
		if(this.indicateList.size() == 1){
			// Check if a swap is needed
			if(!this.indicateList.get(0).equals(object)){
				this.indicateList.get(0).setTo(object);
			}
		}	
		
	}
	
	public void clearIndicateList()
	{
		this.indicateList.clear();
	}
	
	// need to be manually cleared from outside
	public void indicate_multiple(Object object)
	{
		this.indicateList.add(object);
	}
	
	public void addObject(Object o)
	{
		// Somewhere in the list there is the player spawn point. Replace it
		if(this.playerSpawnPointAdded && o.getType() == EntityType.SPAWNPOINT)
		{
			ObjectManager.objects.get(this.playerSpawnPointArrayLocation).setTo(o);
			return;
		}
		else if(!this.playerSpawnPointAdded && o.getType() == EntityType.SPAWNPOINT)
		{
			this.playerSpawnPointAdded = true;
			// Actually the correct array position because the size has not yet been incremented
			this.playerSpawnPointArrayLocation = ObjectManager.objects.size(); 
		}
		
		ObjectManager.objects.add(o);
		this.addToUndoList(o, ACTION_FLAG.WAS_ADDED);
		this.selectedObjects.clear();
	}
	
	public void addAllIndicationsToMainList()
	{
		for(int i = 0; i < this.indicateList.size(); i++)
		{
			this.addObject(this.indicateList.get(i));
		}
		this.indicateList.clear();
	}
	
	public void removeObject(Object o)
	{
		if(o != null)
		{
			 if(o.getType() == EntityType.SPAWNPOINT)
			 {
				 this.playerSpawnPointAdded = false;
				 this.playerSpawnPointArrayLocation = -1;
			 }
			
			ObjectManager.objects.remove(o);
			this.addToUndoList(o, ACTION_FLAG.WAS_REMOVED);
			
			if(this.selectedObjects.size() > 0)
			{
				if(o == this.selectedObjects.get(this.selectedObjects.size()-1))
				{
					
					this.selectedObjects.remove(this.selectedObjects.size()-1);
				}
			}
		}
		
	}
	
	public void removeSelectedObject()
	{
		if(this.selectedObjects.size() > 0)
		{
			if(this.selectedObjects.get(this.selectedObjects.size()-1) != null)
			{
				this.addToUndoList(this.selectedObjects.get(this.selectedObjects.size()-1), ACTION_FLAG.WAS_REMOVED);
				ObjectManager.objects.remove(this.selectedObjects.get(this.selectedObjects.size()-1));
				this.selectedObjects.remove(this.selectedObjects.size()-1);
			}
		}
	}
	
	public void removeFromTop()
	{
		if(ObjectManager.objects.size() > 0)
		{
			this.addToUndoList(ObjectManager.objects.get(ObjectManager.objects.size() - 1), ACTION_FLAG.WAS_REMOVED);
			ObjectManager.objects.remove(ObjectManager.objects.size() - 1);
		}
	}
	
	public Object findObject(Vector2 position)
	{
		Object o = null;
		boolean found = false;
		
		for(int i = 0; i < ObjectManager.objects.size() && found == false; i++)
		{
			Object arrayObject = ObjectManager.objects.get(i);
			
			if(position.x > arrayObject.getPosition().x && position.x <= arrayObject.getPosition().x + arrayObject.getSize())
			{
				if(position.y > arrayObject.getPosition().y && position.y <= arrayObject.getPosition().y + arrayObject.getSize())
				{
					o = arrayObject;
					found = true;
				}
			}
		}	
		return o;
	}
	
	
	public Object findObjectInSelectedList(Vector2 position)
	{
		Object o = null;
		boolean found = false;
		
		for(int i = 0; i < this.selectedObjects.size() && found == false; i++)
		{
			Object arrayObject = this.selectedObjects.get(i);
			
			if(position.x > arrayObject.getPosition().x && position.x <= arrayObject.getPosition().x + arrayObject.getSize())
			{
				if(position.y > arrayObject.getPosition().y && position.y <= arrayObject.getPosition().y + arrayObject.getSize())
				{
					o = arrayObject;
					found = true;
				}
			}
		}	
		return o;
	}
	
	public void massSelect(Vector2 position, Vector2 size)
	{
		this.selectedObjects.clear();
		for(int i = 0; i < ObjectManager.objects.size(); i++)
		{
			Object arrayObject = ObjectManager.objects.get(i);
			
			if(position.x + size.x >= arrayObject.getPosition().x && position.x <= arrayObject.getPosition().x + arrayObject.getSize())
			{
				if(position.y + size.y >= arrayObject.getPosition().y && position.y <= arrayObject.getPosition().y + arrayObject.getSize())
				{
					selectedObjects.add(arrayObject);
					this.settings.getProperties().setRotation(arrayObject.getRotation());
					this.settings.getProperties().setOnCollisionSetting(arrayObject.getOnCollision());
					this.settings.getEntityTable().setCurrentTextureID(arrayObject.getTextureIDFromTextureTable());
					this.settings.getProperties().setColor(arrayObject.getColor());
					this.settings.getProperties().setSize(arrayObject.getSize());
				}
			}
		}	
	}

	public void setSelectedObject(Object o)
	{
		this.selectedObjects.clear();
		this.selectedObjects.add(o);
	}
	
	public void update()
	{
		if(this.selectedObjects.size() > 0)
		{
			boolean updateRotation = this.settings.getProperties().sliderHasBeenUpdated();
			boolean updateOnCollision = this.settings.getProperties().onCollisionSettingHasBeenUpdated();
			boolean updateTextureID = this.settings.getEntityTable().textureIDHasBeenUpdated();
			boolean updateColorRGB = this.settings.getProperties().colorRGBHasBeenUpdated();
			boolean updateAlpha = this.settings.getProperties().alphaHasBeenUpdated();
			boolean updateSize = this.settings.getProperties().sizeHasBeenUpdated();
			
			
			for(int i = 0; i < this.selectedObjects.size(); i++)
			{
				Object currentObj = this.selectedObjects.get(i);
				
				if(currentObj.getRotation() != this.settings.getProperties().getRotation() &&
						updateRotation)
				{
					this.addToUndoList(currentObj, ACTION_FLAG.ROTATION_CHANGED);
					currentObj.setRotation(this.settings.getProperties().getRotation());
				}
				
				if(!currentObj.getOnCollision().equalsIgnoreCase(this.settings.getProperties().getOnCollisionSetting()) && 
						updateOnCollision)
				{
					this.addToUndoList(currentObj, ACTION_FLAG.ONCOLLISION_CHANGED);
					currentObj.setOnCollision(this.settings.getProperties().getOnCollisionSetting());
				}
				
				if(currentObj.getTextureIDFromTextureTable() != this.settings.getEntityTable().getCurrentTextureID() && 
						updateTextureID)
				{
					this.addToUndoList(currentObj, ACTION_FLAG.TEXTURE_CHANGED);
					currentObj.setTextureIDFromTextureTable(this.settings.getEntityTable().getCurrentTextureID());
					currentObj.setTexture(this.settings.getEntityTable().getCurrentTextureObject().getTexture());
				}
				
				// Need to be flagged!
				if(currentObj.getSize() != this.settings.getProperties().getSize() &&
						updateSize)
				{
					this.addToUndoList(currentObj, ACTION_FLAG.SIZE_CHANGED);
					currentObj.setSize(this.settings.getProperties().getSize());
				}
				
				// RGB
				if(
					(currentObj.getColor().r != this.settings.getProperties().getColorRGB().x ||
					currentObj.getColor().g != this.settings.getProperties().getColorRGB().y ||
					currentObj.getColor().b != this.settings.getProperties().getColorRGB().z) 
					&&
				   updateColorRGB)
				{
					this.addToUndoList(currentObj, ACTION_FLAG.COLOR_RGB_CHANGED);
						currentObj.setColorRGB(this.settings.getProperties().getColorRGB().x,
							this.settings.getProperties().getColorRGB().y,
							this.settings.getProperties().getColorRGB().z);
					
					
				}
				
				// Alpha
				if(currentObj.getColor().a != this.settings.getProperties().getAlpha() &&
						updateAlpha)
				{
					this.addToUndoList(currentObj, ACTION_FLAG.COLOR_ALPHA_CHANGED);
					currentObj.setAlpha(this.settings.getProperties().getAlpha());
				}
				
			}
			
				
			
		}
		
		
	}
	
	public void render(SpriteBatch batch)
	{
		
		//There is one or more selected objects
			for(int i = 0; i < ObjectManager.objects.size(); i++)
			{
				Object arrayObject = ObjectManager.objects.get(i);

				batch.setColor(arrayObject.getColor());
				// draw
				batch.draw(arrayObject.getTextureRegion(),
						arrayObject.getPosition().x,
						arrayObject.getPosition().y,
						arrayObject.getSize() / 2.f,
						arrayObject.getSize() / 2.f,
						arrayObject.getSize(), 
						arrayObject.getSize(), 
						1.f,
						1.f,
						arrayObject.getRotation());
				batch.setColor(Color.WHITE);
			}
			
			for(int i = 0; i < this.indicateList.size(); i++)
			{
				Object arrayObject = this.indicateList.get(i);
				
				batch.setColor(new Color(arrayObject.getColor().r, arrayObject.getColor().g, arrayObject.getColor().b, 0.35f));
				// draw
				batch.draw(arrayObject.getTextureRegion(),
						arrayObject.getPosition().x,
						arrayObject.getPosition().y,
						arrayObject.getSize() / 2.f,
						arrayObject.getSize() / 2.f,
						arrayObject.getSize(), 
						arrayObject.getSize(), 
						1.f,
						1.f,
						arrayObject.getRotation());
				batch.setColor(Color.WHITE);
				
			}
			
	}
	
	private void addToUndoList(Object o, ACTION_FLAG flag)
	{
		if(this.actionListIndexPointer == this.MAX_ACTION_CACHED)
		{
			this.actionListIndexPointer = 0;
		}
		// If its the same object from last time that is being changed just update the values instead of 
		// adding it again
		if(this.actionListIndexPointer > 0)
		{
			Object latestRef = null;
			boolean sameFlag = false;
			
			for(int i = 0; i < this.latestActions.size() && sameFlag == false; i++)
			{
				if(o == this.latestActions.get(i).referenceToObject)
				{	
					if(flag == this.latestActions.get(i).flag)
					{
						latestRef = o;
						sameFlag = true;	
					}
				}
			}	
			
			
			if(latestRef != null)
			{
				if(sameFlag == true)
				{
					return;
				}
			}	
		}
		
		this.latestActions.add(this.actionListIndexPointer++,
				new Action(o, flag));
		

	}
	
	public void revertLatestActionMade()
	{
		if(this.latestActions.size() > 0)
		{
			if(this.actionListIndexPointer == 0)
				this.actionListIndexPointer = this.MAX_ACTION_CACHED;
			
			if(this.actionListIndexPointer > 0)
			{
				this.revertAction(this.latestActions.get(this.actionListIndexPointer-1));
			}
		}
		
	}
	
	public void clearCurrentSelectedObject()
	{
		this.selectedObjects.clear();
	}
	
	public Object getLatestObjectInList()
	{
		if(ObjectManager.objects.size() > 0)
			return ObjectManager.objects.get(ObjectManager.objects.size() - 1);
		
		return null;
	}
	
	public boolean isAnyObjectSelected()
	{
		return this.selectedObjects.size() > 0;
	}
	
	public int getNrOfObjects()
	{
		return ObjectManager.objects.size();
	}
	
	public boolean isAnyObjects()
	{
		return ObjectManager.objects.size() != 0;
	}
	
	public boolean isMassSelected()
	{
		return this.selectedObjects.size() > 1;
	}
	
	public ArrayList<Object> getSelectedObjects()
	{
		return this.selectedObjects;
	}
	
	public int getSizeOfIndicateList()
	{
		return this.indicateList.size();
	}
	
	private void revertAction(Action action)
	{
		if(action.flag == ACTION_FLAG.WAS_REMOVED)
		{
			ObjectManager.objects.add(action.referenceToObject);
			this.latestActions.remove(--this.actionListIndexPointer);
		}
		else
		if(action.flag == ACTION_FLAG.WAS_ADDED)
		{
			ObjectManager.objects.remove(action.referenceToObject);
			this.latestActions.remove(--this.actionListIndexPointer);
		}
		else
		if(action.flag == ACTION_FLAG.ROTATION_CHANGED)
		{
			int index = ObjectManager.objects.indexOf(action.referenceToObject);
			ObjectManager.objects.get(index).setRotation(action.copyOfObject.getRotation());
			this.latestActions.remove(--this.actionListIndexPointer);
		}
		else
		if(action.flag == ACTION_FLAG.ONCOLLISION_CHANGED)
		{
			int index = ObjectManager.objects.indexOf(action.referenceToObject);
			ObjectManager.objects.get(index).setOnCollision(action.copyOfObject.getOnCollision());
			this.latestActions.remove(--this.actionListIndexPointer);
		}
		else
		if(action.flag == ACTION_FLAG.TEXTURE_CHANGED)
		{
			int index = ObjectManager.objects.indexOf(action.referenceToObject);
			int textureID = action.copyOfObject.getTextureIDFromTextureTable();
			this.settings.getEntityTable().setCurrentTextureID(textureID);
			ObjectManager.objects.get(index).setTextureIDFromTextureTable(textureID);
			ObjectManager.objects.get(index).setTexture(this.settings.getEntityTable().getCurrentTextureObject().getTexture());
			
			this.latestActions.remove(--this.actionListIndexPointer);
		}
		else
		if(action.flag == ACTION_FLAG.SIZE_CHANGED)
		{
			int index = ObjectManager.objects.indexOf(action.referenceToObject);
			ObjectManager.objects.get(index).setSize(action.copyOfObject.getSize());
			this.latestActions.remove(--this.actionListIndexPointer);
		}
		else
		if(action.flag == ACTION_FLAG.COLOR_RGB_CHANGED)
		{
			int index = ObjectManager.objects.indexOf(action.referenceToObject);
			ObjectManager.objects.get(index).setColorRGB(action.copyOfObject.getColor().r, action.copyOfObject.getColor().g, action.copyOfObject.getColor().b);
			this.latestActions.remove(--this.actionListIndexPointer);
		}
		else
		if(action.flag == ACTION_FLAG.COLOR_ALPHA_CHANGED)
		{
			int index = ObjectManager.objects.indexOf(action.referenceToObject);
			ObjectManager.objects.get(index).setAlpha(action.copyOfObject.getColor().a);
			this.latestActions.remove(--this.actionListIndexPointer);
		}
	}
	
	public String getLatestAction()
	{
		if(this.actionListIndexPointer > 0)
			return this.latestActions.get(this.actionListIndexPointer-1).flag.toString();
		else
			return "Null";
	}
	
	
	
}
