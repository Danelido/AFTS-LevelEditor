package EditorPanel;

import com.afts.editor.Core;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import Properties.EntityObject;
import Properties.EntityTable;
import Properties.ObjectInfoPanel;
import Properties.PropertySettings;

public class EditPanel {
	private Stage stage;
	private Skin skin;
	private PropertySettings properties;
	private EntityTable entityTable;
	private ObjectInfoPanel objectInfoPanel;
	
	private TextButton showOrHideButton;
	float totalPanelWidth;
	
	private ShapeRenderer renderer;
	private SpriteBatch batch;
	
	private boolean showPanel = true;
	private float transitionSpeed = 1000.f;
	private Vector2 cameraStartPosition = new Vector2(0,0);
	
	public static final Color PANEL_COLOR = new Color(32.f/255.f, 32.f/255.f , 32.f/255.f, 0.25f);
	
	private TextureRegion previewTextureRegion;
	
	public EditPanel() {		
		this.skin = new Skin(Gdx.files.internal("uiskin.json"));
		this.skin.getFont("default-font").getData().setScale(0.50f);
		this.stage = new Stage(new ScreenViewport());
	
		this.properties = new PropertySettings(this.stage, this.skin);
		this.entityTable = new EntityTable(this.stage, this.skin);
		
		this.renderer = new ShapeRenderer();
		this.renderer.setAutoShapeType(true);
		this.batch = new SpriteBatch();
		this.cameraStartPosition.set(this.stage.getCamera().position.x, this.stage.getCamera().position.y);
		
		this.totalPanelWidth = PropertySettings.propertyPanelWidth + this.entityTable.getSizeOfPanel().x;
		this.initializeShowOrHideButton();
	
		
		this.objectInfoPanel = new ObjectInfoPanel();
		
		this.previewTextureRegion = new TextureRegion( this.entityTable.getCurrentTextureObject().getTexture());
	}
	
	private void initializeShowOrHideButton()
	{
		this.showOrHideButton = new TextButton("Hide", this.skin);
		this.showOrHideButton.setSize(50.f,20.f);
		this.showOrHideButton.setPosition(this.totalPanelWidth + 5.f, Core.WINDOW_HEIGHT - 25.f);
		
		this.showOrHideButton.addListener(new ClickListener() {
			
			public void clicked(InputEvent inputEvent, float x, float y)
			{
				if(!EditPanel.this.properties.saveLevel() && !EditPanel.this.properties.loadLevel() ) {
				
					EditPanel.this.showPanel = !EditPanel.this.showPanel;	
					
					if(EditPanel.this.showPanel) EditPanel.this.showOrHideButton.setText("Hide");
					if(!EditPanel.this.showPanel) EditPanel.this.showOrHideButton.setText("Show");
				}
			}
		});
		
		this.stage.addActor(this.showOrHideButton);
	}
	
	private void show_hide_updater()
	{
	
		if(this.showPanel)
		{
			if(this.stage.getCamera().position.x > this.cameraStartPosition.x)
				this.stage.getCamera().position.x -= transitionSpeed * Gdx.graphics.getDeltaTime();
				
			if(this.stage.getCamera().position.x < this.cameraStartPosition.x)
				this.stage.getCamera().position.x = this.cameraStartPosition.x;
				
		}else
		{
			if(this.stage.getCamera().position.x < this.cameraStartPosition.x + totalPanelWidth)
				this.stage.getCamera().position.x += transitionSpeed * Gdx.graphics.getDeltaTime();
				
			if(this.stage.getCamera().position.x > this.cameraStartPosition.x + totalPanelWidth)
				this.stage.getCamera().position.x = this.cameraStartPosition.x + totalPanelWidth;
		}
		
	}
	
	public void draw()
	{
		
		
		this.show_hide_updater();
	
		this.properties.displayPanel();
		
		this.renderer.setProjectionMatrix(this.stage.getCamera().combined);
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		this.renderer.begin(ShapeType.Line);
		
		this.renderer.setColor(Color.GOLD);
		// Preview Bounds
		this.renderer.rect(this.entityTable.panelPosition.x + 5.f,
				this.entityTable.getDisplayCurrentTexturePosition().y - 15.f,
				this.entityTable.panelSize.x - 10.f,
				this.entityTable.getDisplayCurrentTextureSize().y + 30.f);
		
		
		if(this.entityTable.getActorThatHasScrollFocus() != null)
		{
			this.renderer.setColor(Color.WHITE);
		}else
		{
			this.renderer.setColor(Color.GRAY);
		}
		
		this.renderer.rect(this.entityTable.getTabel().getX() - 2.f,
				this.entityTable.getTabel().getY() - 4.f,
				this.entityTable.getTabel().getWidth() + 2.f,
				this.entityTable.getTabel().getHeight() + 6.f);
		
		this.entityTable.renderTextureTablePanel(this.renderer);
		
		this.objectInfoPanel.drawPanel(this.renderer);
		
		this.renderer.end();
		
		this.batch.setProjectionMatrix(this.stage.getCamera().combined);
		this.batch.begin();
		
		// Preview
		EntityObject current = this.entityTable.getCurrentTextureObject();
		if(current != null)
		{
			this.batch.setColor(new Color(
					this.properties.getColorRGB().x,
					this.properties.getColorRGB().y,
					this.properties.getColorRGB().z,
					this.properties.getAlpha()
					
					));
			
			if(previewTextureRegion.getTexture() != current.getTexture()) 
			{
				previewTextureRegion.setTexture(current.getTexture());
			}
			
			this.batch.draw(this.previewTextureRegion, 
			(this.entityTable.getDisplayCurrentTexturePosition().x - (this.properties.getSize() - entityTable.getDisplayCurrentTextureSize().x) / 2.f),
			this.entityTable.getDisplayCurrentTexturePosition().y - (this.properties.getSize() - entityTable.getDisplayCurrentTextureSize().y) / 2.f,
			this.properties.getSize() / 2.f,
			this.properties.getSize() / 2.f,
			this.properties.getSize(),
			this.properties.getSize(),
			1.f,
			1.f,
			this.properties.getRotation()
			);
			
			
			this.batch.setColor(Color.WHITE);
		}
		
		this.objectInfoPanel.drawText(this.batch);
		this.batch.end();
		this.stage.act(Gdx.graphics.getDeltaTime());
		this.stage.draw();
		
		
	}
	
	public void dispose()
	{
		this.skin.dispose();
		this.stage.dispose();
		this.entityTable.dispose();
	}

	public Stage getStage()
	{
		return this.stage;
	}
	public PropertySettings getProperties() {
		return properties;
	}
	public EntityTable getEntityTable() {
		return entityTable;
	}
	
	public ObjectInfoPanel getObjectInfoPanel(){
		return this.objectInfoPanel;
	}
	
	public void unfocusPanel()
	{
		this.stage.unfocusAll();
		this.entityTable.tableFocus(false);
	}
	
	public void show(boolean b)
	{
		this.showPanel = b;
	}
	
	public boolean isShown()
	{
		return this.showPanel;
	}
	
	public boolean mouseIsInPanelBounds(Vector2 mouse)
	{
		Vector3 pos = this.stage.getCamera().unproject(new Vector3(mouse.x, mouse.y, 0.f));
		if(pos.x < this.properties.propertyPanelWidth) return true;
		
		if(pos.x < this.totalPanelWidth && 
				pos.y > ObjectInfoPanel.panelPosition.y ) return true;
		
		return false;
	}
	
}
