package Graphics;

import com.afts.editor.Core;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import EditorPanel.EditPanel;
import Properties.EntityType;
import Properties.Utils;

public class UserActions {
		
	private EditPanel settings;
	private ObjectManager objects;
	private UserActionState actionState;
	private boolean validAction = false;
	
	private int key_one = -1;
	private int key_two = -1;
	private boolean mouseIsBeingDragged = false;
	
	private Vector2 mousePosition_onClick 	= new Vector2(0,0);
	private Vector2 mousePosition_onDrag 	= new Vector2(0,0);
	private Vector2 mousePosition_onRelease = new Vector2(0,0);
	private Vector2 mousePosition_onMove	= new Vector2(0,0);
	private Vector2 mousePosition_onScreen 	= new Vector2(0,0);
	
	private Vector2 massSelect_startPosition = new Vector2(0,0);
	private Vector2 massSelect_sizeOfArea = new Vector2(0,0);
	
	private Vector2 massAddStartPosition = new Vector2(0,0);
	
	private float pre_mass_add_degrees = 0.f;
	
	private OrthographicCamera camera;
	
	public UserActions(EditPanel settings, ObjectManager objects, OrthographicCamera camera)
	{
		this.settings = settings;
		this.objects = objects;
		this.camera = camera;
		
	}
	
	public void combineKeysToCreateActionState()
	{
		this.validAction = false;
		
		if(this.key_one == -1 && this.key_two == -1)
		{
				this.actionState = UserActionState.NOTHING;
				this.reset();
				return;
		}
	
		// LEFT CTRL
		if(this.key_one == Input.Keys.CONTROL_LEFT)
		{
			// LEFT MOUSE
			if(this.key_two == Input.Buttons.LEFT)
			{	
				this.actionState = UserActionState.MASS_SELECT;
				this.validAction = true;
			}
			// Z
			else if(this.key_two == Input.Keys.Z)
			{
				
				this.actionState = UserActionState.REVERT_LAST_CHANGES;
				this.validAction = true;	
			}
			else if(this.key_two == -1)
			{
				this.actionState = UserActionState.PRE_MASS_SELECT;
				this.validAction = true;
			}
		}
		

		// LEFT SHIFT & LEFT MOUSE BUTTON
		else if(this.key_one == Input.Keys.SHIFT_LEFT)
		{
			if(this.key_two == Input.Buttons.LEFT)
			{
					this.actionState = UserActionState.MASS_ADD;
					this.validAction = true;
			}else
			{
				if(this.actionState != UserActionState.PRE_MASS_ADD ) {
				Vector3 pos = this.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0.f));
				this.massAddStartPosition.set(pos.x, pos.y);
				}
				
				this.actionState = UserActionState.PRE_MASS_ADD;
				this.validAction = true;
			}
		}
		
		
		// NO KEY ONE & LEFT MOUSE BUTTON OR RIGHT MOUSE BUTTON
		else if(this.key_one == -1)
		{
			if(this.key_two == Input.Buttons.LEFT)
			{
				this.actionState = UserActionState.LEFT_MOUSE_STATE;
				this.validAction = true;
			}	
			else if(this.key_two == Input.Buttons.RIGHT)
			{
				this.actionState = UserActionState.RIGHT_MOUSE_STATE;
				this.validAction = true;
			}
		}
		
		// BACKSPACE & NO KEY TWO
		else if(this.key_one == Input.Keys.BACKSPACE)
		{
			if(this.key_two == -1)
			{
				this.actionState = UserActionState.REMOVE_SELECTED_OBJECTS;
				this.validAction = true;
			}
		}
		
		// ESCAPE & NO KEY TWO
		else if(this.key_one == Input.Keys.ESCAPE)
		{
			if(this.key_two == -1) 
			{
				this.actionState = UserActionState.CLEAR_SELECTED;
				this.validAction = true;
			}
		}
			
		
		// If no valid actions then reset
		if(!this.validAction)
		{
			this.reset();
		}
	}

	public void update()
	{
		if(this.settings.mouseIsInPanelBounds(this.mousePosition_onScreen) && this.settings.isShown())
		{
			this.actionState = UserActionState.OUT_OF_BOUNDS;
		}else
		{
			if(this.actionState == UserActionState.OUT_OF_BOUNDS)
			{
				this.actionState = UserActionState.NOTHING;
			}
		}
		
		if(this.actionState == UserActionState.NOTHING && this.objects.getSelectedObjects().size() == 0)
		{
			
		
			Object temp = new Object(
					this.settings.getEntityTable().getCurrentTextureObject().getTexture(),
					this.settings.getEntityTable().getCurrentTextureObject().getTextureFileName()
					,this.settings.getEntityTable().getCurrentTextureObject().getType());
			
			float rotation = this.settings.getProperties().getRotation();
			int textureID = this.settings.getEntityTable().getCurrentTextureID();
			
			temp.setSize(this.settings.getProperties().getSize());
			temp.setPosition(this.getPositionBasedOnMode(temp));
			temp.setRotation(rotation);
			temp.setTextureIDFromTextureTable(textureID);
			temp.setColorRGB(this.settings.getProperties().getColorRGB().x,
					this.settings.getProperties().getColorRGB().y,
					this.settings.getProperties().getColorRGB().z);
			
			this.objects.indicate_singular(temp);
		}else
		{
			if(this.actionState != UserActionState.PRE_MASS_ADD && this.actionState != UserActionState.MASS_ADD && this.objects.getSizeOfIndicateList() > 0) 
			{
				this.objects.clearIndicateList();
			}
		}
		
		
		if(this.actionState == UserActionState.PRE_MASS_ADD)
		{
			this.action_pre_mass_add();
		}
		if(this.actionState == UserActionState.MASS_ADD)
		{
			this.action_mass_add();
		}
		
	}
	
	public void handleNonUpdateDependentActions()
	{
		if(this.settings.mouseIsInPanelBounds(this.mousePosition_onScreen) && this.settings.isShown())
		{
			this.actionState = UserActionState.OUT_OF_BOUNDS;
			return;
		}
		
		if(this.actionState == UserActionState.LEFT_MOUSE_STATE && !this.mouseIsBeingDragged) {
			this.action_select_or_add_object();
		}
		else if(this.actionState == UserActionState.MASS_SELECT)
		{
			this.massSelect_startPosition = this.mousePosition_onClick.cpy();
			this.action_mass_select_on_release();
		}
		else if(this.actionState == UserActionState.REMOVE_SELECTED_OBJECTS)
		{
			this.action_remove_selected_objects();
		}
		else if(this.actionState == UserActionState.CLEAR_SELECTED)
		{
			this.action_clear_selected_objects();
		}
		else if(this.actionState == UserActionState.REVERT_LAST_CHANGES)
		{
			this.action_revert_last_changes();
		}
	}
	
	public void handleUpdateDependentActions()
	{
		if(this.settings.mouseIsInPanelBounds(this.mousePosition_onScreen) && this.settings.isShown())
		{
			return;
		}
		
		if(this.actionState == UserActionState.MASS_SELECT) 
		{
			this.action_mass_select_on_drag();
		}
		else if(this.actionState == UserActionState.LEFT_MOUSE_STATE)
		{
			if(!this.action_drag_object())
			{
				this.action_move_camera();
			}
		}
		else if(this.actionState == UserActionState.RIGHT_MOUSE_STATE)
		{
			this.action_move_camera();
		}
	}
	
	private void addObject(Vector2 position)
	{
		
		float rotation = this.settings.getProperties().getRotation();
		String onCollision = this.settings.getProperties().getOnCollisionSetting();
		int textureID = this.settings.getEntityTable().getCurrentTextureID();
		EntityType type = this.settings.getEntityTable().getCurrentTextureObject().getType();
		
		Object temp = new Object(
				this.settings.getEntityTable().getCurrentTextureObject().getTexture(),
				this.settings.getEntityTable().getCurrentTextureObject().getTextureFileName()
				,type);
		
		temp.setSize(this.settings.getProperties().getSize());
		temp.setPosition(this.getPositionBasedOnMode(temp));
		temp.setRotation(rotation);
		temp.setOnCollision(onCollision);
		temp.setTextureIDFromTextureTable(textureID);
		temp.setColorRGB(this.settings.getProperties().getColorRGB().x,
				this.settings.getProperties().getColorRGB().y,
				this.settings.getProperties().getColorRGB().z);
	
		this.objects.addObject(temp);
	}
	
	private Vector2 getPositionBasedOnMode(Object obj)
	{
		
		if(this.settings.isFreeMode())
			return new Vector2(this.mousePosition_onMove.x - obj.getSize() / 2.f, this.mousePosition_onMove.y - obj.getSize() / 2.f);
		else
		{
			int cellPositionX = 0;
			int cellPositionY = 0;
			
			if(this.mousePosition_onMove.x < 0){
				cellPositionX = (int)(this.mousePosition_onMove.x - (int)obj.getSize()) / (int)obj.getSize();
			}else{
				cellPositionX = (int)this.mousePosition_onMove.x / (int)obj.getSize();
			}
			
			if(this.mousePosition_onMove.y < 0){
				cellPositionY = (int)(this.mousePosition_onMove.y - (int)obj.getSize()) / (int)obj.getSize();
			}else{
				cellPositionY = (int)this.mousePosition_onMove.y / (int)obj.getSize();
			}
			
		
			Vector2 position = new Vector2(0,0);
			position.x = cellPositionX * (int)obj.getSize();
			position.y = cellPositionY * (int)obj.getSize();
			
			return position;
		}
	}
	
	
	private void reset()
	{
		this.actionState = UserActionState.NOTHING;
		this.massSelect_startPosition.set(0,0);
		this.massSelect_sizeOfArea.set(0,0);
		this.mouseIsBeingDragged = false;
	}
	
	
	
	// Put these into a seperate class later!
	//------------------------------------------------------------------------------------------------------------------------------------------
	private void action_select_or_add_object()
	{
		Object temp = this.objects.findObject(this.mousePosition_onRelease); 
		
		// There is none where you clicked so if you already have one
		// selected then (deselect?) the selected ones. 
		// Otherwise just add a new object
		if(temp == null)
		{
			if(this.objects.isAnyObjectSelected())
			{
				this.objects.clearCurrentSelectedObject();
			
			}else
			{				
				this.addObject(this.mousePosition_onRelease);
			}
		}
		
		// There is an object where you just clicked so select that one
		if(temp != null)
		{
			this.settings.getProperties().setRotation(temp.getRotation());
			this.settings.getProperties().setOnCollisionSetting(temp.getOnCollision());
			this.settings.getProperties().setSize(temp.getSize());
			this.settings.getProperties().setColor(temp.getColor());
			this.settings.getEntityTable().setCurrentTextureID(temp.getTextureIDFromTextureTable());
			this.objects.setSelectedObject(temp);
		}	
	}
	
	private void action_move_camera()
	{
		float xChange = this.mousePosition_onDrag.x - this.mousePosition_onClick.x;
		float yChange = this.mousePosition_onDrag.y - this.mousePosition_onClick.y;
	
		this.camera.position.x -= xChange;
		this.camera.position.y -= yChange;
	}
	
	
	
	private boolean action_drag_object()
	{
		if(this.objects.isAnyObjectSelected())
		{
			boolean isAnySelectedObjectBeingPressed = false;
			
			for(int i = 0; i < this.objects.getSelectedObjects().size() && isAnySelectedObjectBeingPressed == false; i++)
			{
				Object cur = this.objects.getSelectedObjects().get(i);
				
				if(this.objects.findObjectInSelectedList(this.mousePosition_onClick) == cur)
				{
					isAnySelectedObjectBeingPressed = true;
				}
			}
			
			if(isAnySelectedObjectBeingPressed)
			{
				for(int i = 0; i < this.objects.getSelectedObjects().size(); i++)
				{
					Object cur = this.objects.getSelectedObjects().get(i);
					cur.setPosition(cur.getPosition().cpy().add(this.mousePosition_onDrag.x - this.mousePosition_onClick.x, mousePosition_onDrag.y - this.mousePosition_onClick.y));
				}
				this.mousePosition_onClick.set(this.mousePosition_onDrag);
			}
			return true;
		}
			
		return false;
	}
	
	private void action_mass_select_on_release()
	{
		if(this.massSelect_sizeOfArea.y < 0)
		{
			this.massSelect_sizeOfArea.y *= -1f;
			this.massSelect_startPosition.y -= this.massSelect_sizeOfArea.y;
			
		}
		
		if(this.massSelect_sizeOfArea.x < 0)
		{
			this.massSelect_sizeOfArea.x *= -1f;
			this.massSelect_startPosition.x -= this.massSelect_sizeOfArea.x;
			
		}
		this.objects.massSelect(this.massSelect_startPosition, this.massSelect_sizeOfArea);
	}
	
	private void action_mass_select_on_drag()
	{
		this.massSelect_sizeOfArea.set(this.mousePosition_onDrag.x - this.mousePosition_onClick.x , 
				 this.mousePosition_onDrag.y - mousePosition_onClick.y);
	}
	
	
	private void action_remove_selected_objects()
	{
		for(int i = 0; i < this.objects.getSelectedObjects().size(); i++)
		{
			Object cur = this.objects.getSelectedObjects().get(i);
			this.objects.removeObject(cur);
		}
		this.objects.clearCurrentSelectedObject();
		
	}
	
	private void action_pre_mass_add()
	{
		if(this.settings.isFreeMode()) 
		{
			this.preMassIndicate_FreeMode();
		}
		else if(this.settings.isGridMode())
		{
			this.preMassIndicate_gridMode();
		}
			
		
	}
	
	private void action_mass_add()
	{
		this.objects.addAllIndicationsToMainList();
	}
	
	private void action_clear_selected_objects()
	{
		this.objects.clearCurrentSelectedObject();
	}
	
	private void action_revert_last_changes()
	{
		this.objects.revertLatestActionMade();
		this.objects.clearCurrentSelectedObject();
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------
	
	private void preMassIndicate_FreeMode()
	{
		float distance = this.massAddStartPosition.dst(this.mousePosition_onMove.cpy());
		
		if(distance > 1.f && this.settings.getProperties().getSize() >= 1) {
			
			int numberOfObjects = 0;
			if(distance < this.settings.getProperties().getSize())
			{
				numberOfObjects = 1;
			}else {
				 numberOfObjects = (int)(distance + this.settings.getProperties().getSize() / 2.f) / ((int)this.settings.getProperties().getSize());
				 numberOfObjects += 1;
			}
			
			this.objects.clearIndicateList();
	
			

	        	this.pre_mass_add_degrees = (float)Math.atan2(
	                this.massAddStartPosition.y + this.settings.getProperties().getSize() / 2.f - mousePosition_onMove.y -  this.settings.getProperties().getSize() / 2.f,
	                this.massAddStartPosition.x + this.settings.getProperties().getSize() / 2.f - mousePosition_onMove.x -  this.settings.getProperties().getSize() / 2.f

	        ) * 180.f / (float)Math.PI;

	        	this.pre_mass_add_degrees += 180.f;
		
			
			for(int i = 0; i < numberOfObjects; i++)
			{
				Object temp = new Object(
						this.settings.getEntityTable().getCurrentTextureObject().getTexture(),
						this.settings.getEntityTable().getCurrentTextureObject().getTextureFileName()
						,this.settings.getEntityTable().getCurrentTextureObject().getType());
				
				float rotation = this.settings.getProperties().getRotation();
				int textureID = this.settings.getEntityTable().getCurrentTextureID();
				
				temp.setSize(this.settings.getProperties().getSize());
				
				float xDir = (this.massAddStartPosition.x - mousePosition_onMove.x) / distance;
				float yDir = (this.massAddStartPosition.y - mousePosition_onMove.y) / distance;
				
				
				
				float positionX = 
						 (this.massAddStartPosition.x - (temp.getSize() * xDir) * i);
				float positionY = 
						 (this.massAddStartPosition.y - (temp.getSize() * yDir) * i);
				
				temp.setPosition(new Vector2(positionX - (temp.getSize() / 2.f), positionY - (temp.getSize() / 2.f)));
				temp.setRotation(rotation);
				temp.setTextureIDFromTextureTable(textureID);
				temp.setOnCollision(this.settings.getProperties().getOnCollisionSetting());
				temp.setColorRGB(this.settings.getProperties().getColorRGB().x,
						this.settings.getProperties().getColorRGB().y,
						this.settings.getProperties().getColorRGB().z);
				
				this.objects.indicate_multiple(temp);
			}
		}
	}
	
	/* 
	 * 	TODO:
	 *  The indication jumps 1 cell to the right or/and up when x or/and y is negative
	 *  
	 */
	private void preMassIndicate_gridMode()
	{
		
		int distanceX = (int)this.massAddStartPosition.x - (int)this.mousePosition_onMove.x;
		int distanceY = (int)this.massAddStartPosition.y - (int)this.mousePosition_onMove.y;
		
		int startCellX = (int)this.massAddStartPosition.x / (int)this.settings.getProperties().getSize();
		
		int signX = Utils.getSign(distanceX);
		int signY = Utils.getSign(distanceY);
		
		int cellsX = (distanceX + signX * (int)this.settings.getProperties().getSize()/2) / (int)this.settings.getProperties().getSize();
		int cellsY = (distanceY + signY * (int)this.settings.getProperties().getSize()/2) / (int)this.settings.getProperties().getSize();
		
		cellsX = Math.abs(cellsX) + 1;
		cellsY = Math.abs(cellsY);
		
		Object endObject = null;
		
		this.objects.clearIndicateList();
			
		for(int i = 0; i < cellsX; i++)
		{
			Object temp = new Object(
					this.settings.getEntityTable().getCurrentTextureObject().getTexture(),
					this.settings.getEntityTable().getCurrentTextureObject().getTextureFileName()
					,this.settings.getEntityTable().getCurrentTextureObject().getType());
			
			float rotation = this.settings.getProperties().getRotation();
			int textureID = this.settings.getEntityTable().getCurrentTextureID();
			
			temp.setSize(this.settings.getProperties().getSize());
			
		
			float positionX = startCellX * (int)temp.getSize();
			positionX -= temp.getSize() * signX * (i);
			float positionY = ((int)this.massAddStartPosition.y / (int)temp.getSize());
			positionY = positionY * (int)temp.getSize();
			
			temp.setPosition(new Vector2(positionX, positionY));
			temp.setRotation(rotation);
			temp.setTextureIDFromTextureTable(textureID);
			temp.setOnCollision(this.settings.getProperties().getOnCollisionSetting());
			temp.setColorRGB(this.settings.getProperties().getColorRGB().x,
					this.settings.getProperties().getColorRGB().y,
					this.settings.getProperties().getColorRGB().z);
			
			this.objects.indicate_multiple(temp);
			
			endObject = temp;
		}
		
		for(int i = 0; i < cellsY; i++)
		{
			Object temp = new Object(
					this.settings.getEntityTable().getCurrentTextureObject().getTexture(),
					this.settings.getEntityTable().getCurrentTextureObject().getTextureFileName()
					,this.settings.getEntityTable().getCurrentTextureObject().getType());
			
			float rotation = this.settings.getProperties().getRotation();
			int textureID = this.settings.getEntityTable().getCurrentTextureID();
			
			temp.setSize(this.settings.getProperties().getSize());
			
			float positionX = endObject.getPosition().x;
			float positionY = endObject.getPosition().y - (signY * temp.getSize());
			positionY -= temp.getSize() * signY * i;
			
			temp.setPosition(new Vector2(positionX, positionY));
			temp.setRotation(rotation);
			temp.setTextureIDFromTextureTable(textureID);
			temp.setOnCollision(this.settings.getProperties().getOnCollisionSetting());
			temp.setColorRGB(this.settings.getProperties().getColorRGB().x,
					this.settings.getProperties().getColorRGB().y,
					this.settings.getProperties().getColorRGB().z);
			
			this.objects.indicate_multiple(temp);
			
		}	
		
	}
	
	public void setScroll(int amount)
	{
		if(this.settings.mouseIsInPanelBounds(this.mousePosition_onScreen) && this.settings.isShown()){
			return;
		}
		
		if(amount != 0) 
		{
			float sign = amount * -1;
			
			if(this.actionState == UserActionState.NOTHING)
			{
				this.camera.zoom -= 0.1* sign;
			}
		}
		
		if(this.camera.zoom < 0.25f) this.camera.zoom = 0.25f;
		if(this.camera.zoom > 3.f) this.camera.zoom = 3.f;	
	}
	
	public void setKey_one(int key_one) {
		this.key_one = key_one;
	}

	public void setKey_two(int key_two) {
		this.key_two = key_two;
	}

	public void disableCorrespondingKey(int keycode)
	{
		if(this.key_one == keycode) this.key_one = -1;
		if(this.key_two == keycode) this.key_two = -1;
	}
	
	public void userStartedToDrag(boolean b)
	{
		mouseIsBeingDragged = b;
	}
	
	public void setMouse_onClick(Vector2 mousePosition)
	{
		this.mousePosition_onClick = mousePosition;
	}
	
	public void setMouse_onDrag(Vector2 mousePosition)
	{
		this.mousePosition_onDrag = mousePosition;
	}
	
	public void setMouse_onRelease(Vector2 mousePosition)
	{
		this.mousePosition_onRelease = mousePosition;
	}
	
	public void setMouse_OnScreen(Vector2 mousePosition)
	{
		this.mousePosition_onScreen = mousePosition;
	}
	
	public void setMouse_onMove(Vector2 mousePosition) 
	{
		this.mousePosition_onMove = mousePosition;
	}
	
	public UserActionState getActionState()
	{
		return this.actionState;
	}
	
	public Vector2 getMassSelectStartPosition()
	{
		return this.mousePosition_onClick;
	}
	
	public Vector2 getMassSelectSize()
	{
		return this.massSelect_sizeOfArea;
	}
	
	public boolean isKeyOneValid()
	{
		return this.key_one != -1;
	}
	
	public boolean isKeyTwoValid()
	{
		return this.key_two != -1;
	}
	
	public float getPreMassAddDegree()
	{
		return this.pre_mass_add_degrees;
	}
}
