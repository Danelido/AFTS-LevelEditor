package Graphics;

import com.afts.editor.Core;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import EditorPanel.EditPanel;

public class GraphicState implements InputProcessor{

	private EditPanel settings;
	private OrthographicCamera camera;
	private OrthographicCamera textCamera;
	private SpriteBatch spritebatch;
	private ShapeRenderer shapeRenderer;
	private ObjectManager objectManager;
	private BitmapFont font;
	
	private TextureRegion focusTextureRegion;
	
	private UserActions userAction;
	
	public GraphicState(EditPanel settings)
	{
		this.settings = settings;
		this.camera = new OrthographicCamera(Core.WINDOW_WIDTH, Core.WINDOW_HEIGHT);
		this.camera.position.x += Core.WINDOW_WIDTH / 2.f;
		this.camera.position.y += Core.WINDOW_HEIGHT / 2.f;
		
		this.textCamera = new OrthographicCamera(Core.WINDOW_WIDTH, Core.WINDOW_HEIGHT);
		this.textCamera.position.x += Core.WINDOW_WIDTH / 2.f;
		this.textCamera.position.y += Core.WINDOW_HEIGHT / 2.f;
		
		this.spritebatch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();
		this.objectManager = new ObjectManager(this.settings);
		
		this.userAction = new UserActions(this.settings, this.objectManager, this.camera);
		
		this.font = new BitmapFont();
		
		
		this.focusTextureRegion = new TextureRegion(new Texture(Gdx.files.internal("Textures/select_mark.png")));
	}
	
	public void update()
	{
		if(!this.settings.getProperties().saveLevel() && !this.settings.getProperties().loadLevel())
		{
			this.userAction.update();
			this.objectManager.update();
			
			this.settings.getProperties().provideNewInfoDueToMassSelectOrNot(this.objectManager.isMassSelected());
			
			this.userAction.combineKeysToCreateActionState();
			
			this.camera.update();
			this.textCamera.update();
			
			if(this.objectManager.getSelectedObjects().size() > 1)
				this.settings.getObjectInfoPanel().setMassSelect(true);
			else
				this.settings.getObjectInfoPanel().setMassSelect(false);
			
			if(this.objectManager.getSelectedObjects().size() > 0)
				this.settings.getObjectInfoPanel().displayInfo(this.objectManager.getSelectedObjects().get(0));
			else
				this.settings.getObjectInfoPanel().displayInfo(null);
		}
	}
	
	public void render()
	{
		this.spritebatch.setProjectionMatrix(this.camera.combined);
		this.spritebatch.begin();
		this.objectManager.render(this.spritebatch);
		
		this.spritebatch.setProjectionMatrix(this.textCamera.combined);
		
		this.font.draw(this.spritebatch, "Number of objects: " + this.objectManager.getNrOfObjects(),
		this.textCamera.position.x, Core.WINDOW_HEIGHT / 2.f - 10.f  + this.textCamera.position.y);
		
		this.font.draw(this.spritebatch, "Camera position: [" + this.camera.position.x + ", " + this.camera.position.y + "]" ,
				this.textCamera.position.x + Core.WINDOW_WIDTH / 4.f , Core.WINDOW_HEIGHT / 2.f - 10.f  + this.textCamera.position.y);
		
		this.font.draw(this.spritebatch, "Camera zoom: " + String.format("%.0f",(this.camera.zoom * 100.f)) + " %",
				this.textCamera.position.x  + Core.WINDOW_WIDTH / 4.f, Core.WINDOW_HEIGHT / 2.f - 35.f  + this.textCamera.position.y);
		
		this.font.draw(this.spritebatch, "Last flag added: " + this.objectManager.getLatestAction()
		,this.textCamera.position.x, Core.WINDOW_HEIGHT / 2.f - 35.f + this.textCamera.position.y);
		
		if(this.userAction.getActionState() == UserActionState.PRE_MASS_ADD)
		this.font.draw(this.spritebatch, "Rotation(: " + this.userAction.getPreMassAddDegree() + ")"
		,this.textCamera.position.x, Core.WINDOW_HEIGHT / 2.f - 70.f + this.textCamera.position.y);
		
		
		this.spritebatch.setProjectionMatrix(this.camera.combined);
		this.spritebatch.setColor(Color.WHITE);
		for(int i = 0; i < this.objectManager.getSelectedObjects().size(); i++)
		{
			Object object = this.objectManager.getSelectedObjects().get(i);
			this.spritebatch.draw(this.focusTextureRegion,
					object.getPosition().x,
					object.getPosition().y,
					object.getSize() / 2.f,
					object.getSize() / 2.f,
					object.getSize(), 
					object.getSize(), 
					1.f,
					1.f,
					object.getRotation());
					
		}
		
		
		this.spritebatch.end();
		
		this.shapeRenderer.setProjectionMatrix(this.camera.combined);
		this.shapeRenderer.begin(ShapeType.Line);
		this.shapeRenderer.setColor(Color.WHITE);
		if(this.userAction.getActionState() == UserActionState.MASS_SELECT)
		{	
			
			this.shapeRenderer.rect(
					this.userAction.getMassSelectStartPosition().x,
					this.userAction.getMassSelectStartPosition().y,
					this.userAction.getMassSelectSize().x,
					this.userAction.getMassSelectSize().y);
			
		}
//		this.shapeRenderer.setColor(Color.GOLD);
//		
//		
//		for(int i = 0; i < this.objectManager.getSelectedObjects().size(); i++)
//		{
//			Object object = this.objectManager.getSelectedObjects().get(i);
//			this.shapeRenderer.rect(object.getPosition().x,
//					object.getPosition().y,
//					object.getSize(),
//					object.getSize());
//		}
//		
		this.shapeRenderer.end();
	}
	
	public void dispose()
	{
		this.spritebatch.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		
		if(!this.settings.getProperties().saveLevel() && !this.settings.getProperties().loadLevel())
		{
			if(!this.userAction.isKeyOneValid())
				this.userAction.setKey_one(keycode);
			else
				this.userAction.setKey_two(keycode);
			
			this.userAction.handleNonUpdateDependentActions();
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(!this.settings.getProperties().saveLevel() && !this.settings.getProperties().loadLevel())
		{
			this.userAction.handleNonUpdateDependentActions();
			this.userAction.disableCorrespondingKey(keycode);
		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		if(!this.settings.getProperties().saveLevel() && !this.settings.getProperties().loadLevel())
		{
			Vector3 pos = this.camera.unproject(new Vector3(screenX,screenY,0.f));
			this.userAction.setMouse_OnScreen(new Vector2(screenX,screenY));
			this.userAction.setKey_two(button);
			this.userAction.setMouse_onClick(new Vector2(pos.x,pos.y));
			this.userAction.handleNonUpdateDependentActions();
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(!this.settings.getProperties().saveLevel() && !this.settings.getProperties().loadLevel())
		{
			Vector3 pos = this.camera.unproject(new Vector3(screenX,screenY,0.f));
			this.userAction.setMouse_OnScreen(new Vector2(screenX,screenY));
			this.userAction.setMouse_onRelease(new Vector2(pos.x,pos.y));
			this.userAction.handleNonUpdateDependentActions();
			this.userAction.setKey_two(-1);
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(!this.settings.getProperties().saveLevel() && !this.settings.getProperties().loadLevel())
		{
			Vector3 pos = this.camera.unproject(new Vector3(screenX,screenY,0.f));
			this.userAction.setMouse_OnScreen(new Vector2(screenX,screenY));
			this.userAction.userStartedToDrag(true);
			this.userAction.setMouse_onDrag(new Vector2(pos.x,pos.y));
			this.userAction.handleUpdateDependentActions();
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(!this.settings.getProperties().saveLevel() && !this.settings.getProperties().loadLevel())
		{
			Vector3 pos = this.camera.unproject(new Vector3(screenX,screenY,0.f));
			this.userAction.setMouse_OnScreen(new Vector2(screenX,screenY));
			this.userAction.setMouse_onMove(new Vector2(pos.x,pos.y));
			
			if(!this.settings.mouseIsInPanelBounds(new Vector2(screenX,screenY)))
			{
				this.settings.unfocusPanel();
				return true;
			}
		}
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		if(!this.settings.getProperties().saveLevel() && !this.settings.getProperties().loadLevel())
		{
			userAction.setScroll(amount);
		}
		return true;
	}
	
	
}
