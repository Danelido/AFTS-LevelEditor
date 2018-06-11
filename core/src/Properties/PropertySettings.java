package Properties;

import com.afts.editor.Core;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import EditorPanel.EditPanel;

public class PropertySettings {

	public final static float MAX_SIZE = 64;
	public final static float MAX_COLOR = 255;
	public final static float MAX_ROTATION = 360;
	
	private ShapeRenderer shapeRenderer;

	private Stage stage;
	private Skin skin;
		
	private Label rotationLabel;
	private Slider rotationSlider;
	
	private Label sizeLabel;
	private Slider sizeSlider;
	private float sizeLastChecked;

	// Text Fields
	private TextField size_field;
	private TextField red_field;
	private TextField green_field;
	private TextField blue_field;
	private TextField alpha_field;
	private TextField rotation_field;
	
	// Color sliders
	private Slider redSlider,blueSlider,greenSlider,alphaSlider;
	private Label redLabel, blueLabel, greenLabel, alphaLabel;
	private Vector3 colorsLastChecked_rgb = new Vector3(0,0,0);
	private float alphaLastChecked;
	
	private float rotationLastChecked;
	private Label collisionLabel;
	private CheckBox col_nonMoveable, col_moveable, col_destroy, col_nothing, col_hurtPlayer;
	private Label warningLabel;
	
	// Save and load level buttons
	private TextButton saveLevelButton;
	private TextButton loadLevelButton;
	
	private boolean readyToSaveLevel = false;
	private boolean readyToLoadLevel = false;
	
	private SavePanel savePanel;
	private LoadPanel loadPanel;
	
	public final static float propertyPanelWidth = 150.f;
	public final static float propertyPanelHeight = 300.f;
	
	private Color textColor = new Color(255.f / 255.f, 255.f / 255.f, 255.f / 255.f, 1.f);
	
	public static final String STRING_MOVEABLE = "MOVABLE";
	public static final String STRING_NON_MOVEABLE = "NON_MOVABLE";
	public static final String STRING_DESTROY = "DESTROY";
	public static final String STRING_NOTHING = "NOTHING";
	public static final String STRING_HURT_PLAYER = "HURT_PLAYER";
	
	private String onCollisionSetting = STRING_NON_MOVEABLE;
	private String onCollisionSettingLastChecked = onCollisionSetting;
	
	public PropertySettings(Stage stage, Skin skin)
	{
		this.stage = stage;
		this.skin = skin;
		this.shapeRenderer = new ShapeRenderer();
		this.rotationLastChecked = 0.f;
		this.savePanel = new SavePanel(this,stage,skin);
		this.loadPanel = new LoadPanel(this,stage,skin);
		this.setupUI();
		this.handleEvents();
	}
	
	private void setupUI()
	{
		
		float paddingX = 10.f;
		float paddingY = 30.f;

		// Size properties
			// Size label
		this.sizeLabel = new Label("Size:", this.skin);
		this.sizeLabel.setPosition(paddingX,Core.WINDOW_HEIGHT - paddingY - 25.f);
		this.sizeLabel.setSize(25.f, 10.f);
		this.sizeLabel.setColor(this.textColor);
		this.stage.addActor(this.sizeLabel);
		
			//TextField
		this.size_field = new TextField(""+this.MAX_SIZE, this.skin);
		this.size_field.setSize(50.f,20.f);
		this.size_field.setPosition(this.sizeLabel.getX() + this.sizeLabel.getWidth() + 45.f, this.sizeLabel.getY() - this.size_field.getHeight() / 4.f);
		this.stage.addActor(this.size_field);
		
			// Slider
		this.sizeSlider = new Slider(0.f, this.MAX_SIZE, 1.f, false, this.skin);
		this.sizeSlider.setPosition(paddingX, this.sizeLabel.getY() - paddingY );
		this.sizeSlider.setWidth(100.f);
		this.sizeSlider.setValue(this.sizeSlider.getMaxValue());
		this.stage.addActor(this.sizeSlider);
		
		
		// Color properties
			// Red label
		this.redLabel = new Label("Red:", this.skin);
		this.redLabel.setPosition(paddingX, this.sizeSlider.getY() - paddingY);
		this.redLabel.setSize(25.f, 10.f);
		this.redLabel.setColor(this.textColor);
		this.stage.addActor(this.redLabel);
		
			// Red TextField
		this.red_field = new TextField(""+this.MAX_COLOR, this.skin);
		this.red_field.setSize(50.f,20.f);
		this.red_field.setPosition(this.redLabel.getX() + this.redLabel.getWidth() + 45.f, this.redLabel.getY() - this.red_field.getHeight() / 4.f);
		this.stage.addActor(this.red_field);
		
			// Red Slider
		this.redSlider = new Slider(0.f, this.MAX_COLOR, 1.f, false, this.skin);
		this.redSlider.setPosition(paddingX, this.redLabel.getY() - paddingY );
		this.redSlider.setWidth(100.f);
		this.redSlider.setValue(this.redSlider.getMaxValue());
		this.stage.addActor(this.redSlider);
		
		
			// Green label
		this.greenLabel = new Label("Green:", this.skin);
		this.greenLabel.setPosition(paddingX, this.redSlider.getY() - paddingY);
		this.greenLabel.setSize(25.f, 10.f);
		this.greenLabel.setColor(this.textColor);
		this.stage.addActor(this.greenLabel);
		
			// Green TextField
		this.green_field = new TextField(""+this.MAX_COLOR, this.skin);
		this.green_field.setSize(50.f,20.f);
		this.green_field.setPosition(this.greenLabel.getX() + this.greenLabel.getWidth() + 45.f, this.greenLabel.getY() - this.green_field.getHeight() / 4.f);
		this.stage.addActor(this.green_field);
		
			// Green Slider
		this.greenSlider = new Slider(0.f, this.MAX_COLOR, 1.f, false, this.skin);
		this.greenSlider.setPosition(paddingX, this.greenLabel.getY() - paddingY );
		this.greenSlider.setWidth(100.f);
		this.greenSlider.setValue(this.greenSlider.getMaxValue());
		this.stage.addActor(this.greenSlider);
		
			// Blue label
		this.blueLabel = new Label("Blue:", this.skin);
		this.blueLabel.setPosition(paddingX, this.greenSlider.getY() - paddingY);
		this.blueLabel.setSize(25.f, 10.f);
		this.blueLabel.setColor(this.textColor);
		this.stage.addActor(this.blueLabel);
		
			// Blue TextField
		this.blue_field = new TextField(""+this.MAX_COLOR, this.skin);
		this.blue_field.setSize(50.f,20.f);
		this.blue_field.setPosition(this.blueLabel.getX() + this.blueLabel.getWidth() + 45.f, this.blueLabel.getY() - this.blue_field.getHeight() / 4.f);
		this.stage.addActor(this.blue_field);
		
			// Blue Slider
		this.blueSlider = new Slider(0.f, this.MAX_COLOR, 1.f, false, this.skin);
		this.blueSlider.setPosition(paddingX, this.blueLabel.getY() - paddingY );
		this.blueSlider.setWidth(100.f);
		this.blueSlider.setValue(this.blueSlider.getMaxValue());
		this.stage.addActor(this.blueSlider);
		
			// Alpha label
		this.alphaLabel = new Label("Alpha:", this.skin);
		this.alphaLabel.setPosition(paddingX, this.blueSlider.getY() - paddingY);
		this.alphaLabel.setSize(25.f, 10.f);
		this.alphaLabel.setColor(this.textColor);
		this.stage.addActor(this.alphaLabel);
		
		// Alpha TextField
		this.alpha_field = new TextField(""+this.MAX_COLOR, this.skin);
		this.alpha_field.setSize(50.f,20.f);
		this.alpha_field.setPosition(this.alphaLabel.getX() + this.alphaLabel.getWidth() + 45.f, this.alphaLabel.getY() - this.alpha_field.getHeight() / 4.f);
		this.stage.addActor(this.alpha_field);
		
			// Alpha Slider
		this.alphaSlider = new Slider(0.f, this.MAX_COLOR, 1.f, false, this.skin);
		this.alphaSlider.setPosition(paddingX, this.alphaLabel.getY() - paddingY );
		this.alphaSlider.setWidth(100.f);
		this.alphaSlider.setValue(this.alphaSlider.getMaxValue());
		this.stage.addActor(this.alphaSlider);
		
		
		// Rotation properties
		this.rotationLabel = new Label("Rotation:", this.skin);
		this.rotationLabel.setPosition(paddingX, alphaSlider.getY() - paddingY);
		this.rotationLabel.setSize(25.f, 10.f);
		this.rotationLabel.setColor(this.textColor);
		this.stage.addActor(this.rotationLabel);
	
			// TextField
		this.rotation_field = new TextField(""+this.MAX_ROTATION, this.skin);
		this.rotation_field.setSize(50.f,20.f);
		this.rotation_field.setPosition(this.rotationLabel.getX() + this.rotationLabel.getWidth() + 45.f, this.rotationLabel.getY() - this.rotation_field.getHeight() / 4.f);
		this.stage.addActor(this.rotation_field);
		
			// Slider
		this.rotationSlider = new Slider(0.f, this.MAX_ROTATION, 1.f, false, this.skin);
		this.rotationSlider.setPosition(paddingX, this.rotationLabel.getY() - paddingY );
		this.rotationSlider.setWidth(100.f);
		this.rotationSlider.setValue(this.rotationSlider.getMaxValue());
		this.stage.addActor(this.rotationSlider);
		
		
		// Collision properties
		this.collisionLabel = new Label("Collision Setting", this.skin);
		this.collisionLabel.setPosition(paddingX, this.rotationSlider.getY() - paddingY);
		this.collisionLabel.setSize(25.f, 10.f);
		this.collisionLabel.setColor(this.textColor);
		this.stage.addActor(this.collisionLabel);
			
			// Solid
		this.col_nonMoveable = new CheckBox(" Non movable", this.skin);
		this.col_nonMoveable.setPosition(paddingX, this.collisionLabel.getY() - paddingY);
		this.col_nonMoveable.setZIndex(0);
		this.col_nonMoveable.setChecked(true);
		this.col_nonMoveable.setColor(this.textColor);
		this.stage.addActor(this.col_nonMoveable);
		
			// Bounce
		this.col_moveable = new CheckBox(" Movable", this.skin);
		this.col_moveable.setPosition(paddingX, this.col_nonMoveable.getY() - paddingY);
		this.col_moveable.setZIndex(1);
		this.col_moveable.setColor(this.textColor);
		this.stage.addActor(this.col_moveable);
			
			// Destroy
		this.col_destroy = new CheckBox(" Destroy", this.skin);
		this.col_destroy.setPosition(paddingX, this.col_moveable.getY() - paddingY);
		this.col_destroy.setZIndex(2);
		this.col_destroy.setColor(this.textColor);
		this.stage.addActor(this.col_destroy);
		
			// Nothing
		this.col_nothing = new CheckBox(" Nothing", this.skin);
		this.col_nothing.setPosition(paddingX, this.col_destroy.getY() - paddingY);
		this.col_nothing.setZIndex(3);
		this.col_nothing.setColor(this.textColor);
		this.stage.addActor(this.col_nothing);
		
			// Kill
		this.col_hurtPlayer = new CheckBox(" Hurt player", this.skin);
		this.col_hurtPlayer.setPosition(paddingX, this.col_nothing.getY() - paddingY);
		this.col_hurtPlayer.setZIndex(4);
		this.col_hurtPlayer.setColor(this.textColor);
		this.stage.addActor(this.col_hurtPlayer);
		
		// Load and save level buttons
		this.saveLevelButton = new TextButton("Save Level", this.skin);
		this.saveLevelButton.setWidth(100.f);
		this.saveLevelButton.setPosition(paddingX, this.col_hurtPlayer.getY() - paddingY*3);
		this.stage.addActor(this.saveLevelButton);
		
		this.loadLevelButton = new TextButton("Load Level", this.skin);
		this.loadLevelButton.setWidth(100.f);
		this.loadLevelButton.setPosition(paddingX, this.saveLevelButton.getY() - paddingY);
		this.stage.addActor(this.loadLevelButton);
		
		
		// Debatable if this should even be here
		// WarningLabel when mass selecting properties
		this.warningLabel = new Label("New changes will affect ALL selected objects!", this.skin);
		this.warningLabel.setColor(Color.ORANGE);
		this.warningLabel.setPosition((Core.WINDOW_WIDTH  + propertyPanelWidth) / 2.f - this.warningLabel.getWidth() / 2.f, Core.WINDOW_HEIGHT / 2.f + 200.f);
		this.warningLabel.setScale(2.f);
		this.warningLabel.setVisible(false);
		this.stage.addActor(this.warningLabel);
		
		
	}
	
	private void handleEvents()
	{
		
		this.size_field.addListener(new InputListener() {
            @Override
            public boolean keyTyped (InputEvent event, char character) {
            	boolean valid = true;
    
            	if(character == '\r')
            	{
		    		String text = PropertySettings.this.size_field.getText();
		    		int value = 0;
		    		
		    		try {
		    			 value = (int)Float.parseFloat(text);
		    			 if(value > PropertySettings.this.MAX_SIZE || value < 0) valid = false;
		    		}
		    		catch(NumberFormatException e){		    	
		    			valid = false;
		    		}
		    		
		    		
		    		if(valid)
		    		{
		    			PropertySettings.this.sizeSlider.setValue(value);
		    		}else
		    		{
		    			PropertySettings.this.sizeSlider.setValue(PropertySettings.this.MAX_SIZE);
		    		}
		            	
	            }
            	
            	return true;
	          }
            
		});
		
		
		this.red_field.addListener(new InputListener() {
            @Override
            public boolean keyTyped (InputEvent event, char character) {
            	boolean valid = true;
    
            	if(character == '\r')
            	{
		    		String text = PropertySettings.this.red_field.getText();
		    		int value = 0;
		    		
		    		try {
		    			 value = (int)Float.parseFloat(text);
		    			 if(value > PropertySettings.this.MAX_COLOR || value < 0) valid = false;
		    		}
		    		catch(NumberFormatException e){		    	
		    			valid = false;
		    		}
		    		
		    		
		    		if(valid)
		    		{
		    			PropertySettings.this.redSlider.setValue(value);
		    		}else
		    		{
		    			PropertySettings.this.redSlider.setValue(PropertySettings.this.MAX_COLOR);
		    		}
		            	
		            	
	            }
            	
            	return true;
	          }
            
		});
		
		
		this.green_field.addListener(new InputListener() {
            @Override
            public boolean keyTyped (InputEvent event, char character) {
            	boolean valid = true;
    
            	if(character == '\r')
            	{
		    		String text = PropertySettings.this.green_field.getText();
		    		int value = 0;
		    		
		    		try {
		    			 value = (int)Float.parseFloat(text);
		    			 if(value > PropertySettings.this.MAX_COLOR || value < 0) valid = false;
		    		}
		    		catch(NumberFormatException e){		    	
		    			valid = false;
		    		}
		    		
		    		
		    		if(valid)
		    		{
		    			PropertySettings.this.greenSlider.setValue(value);
		    		}else
		    		{
		    			PropertySettings.this.greenSlider.setValue(PropertySettings.this.MAX_COLOR);
		    		}
		            	
		            	
	            }
            	
            	return true;
	          }
            
		});
		
		
		
		this.blue_field.addListener(new InputListener() {
            @Override
            public boolean keyTyped (InputEvent event, char character) {
            	boolean valid = true;
    
            	if(character == '\r')
            	{
		    		String text = PropertySettings.this.blue_field.getText();
		    		int value = 0;
		    		
		    		try {
		    			 value = (int)Float.parseFloat(text);
		    			 if(value > PropertySettings.this.MAX_COLOR || value < 0) valid = false;
		    		}
		    		catch(NumberFormatException e){		    	
		    			valid = false;
		    		}
		    		
		    		
		    		if(valid)
		    		{
		    			PropertySettings.this.blueSlider.setValue(value);
		    		}else
		    		{
		    			PropertySettings.this.blueSlider.setValue(PropertySettings.this.MAX_COLOR);
		    		}
		            	
		            	
	            }
            	
            	return true;
	          }
            
		});
		
		
		this.alpha_field.addListener(new InputListener() {
            @Override
            public boolean keyTyped (InputEvent event, char character) {
            	boolean valid = true;
    
            	if(character == '\r')
            	{
		    		String text = PropertySettings.this.alpha_field.getText();
		    		int value = 0;
		    		
		    		try {
		    			 value = (int)Float.parseFloat(text);
		    			 if(value > PropertySettings.this.MAX_COLOR || value < 0) valid = false;
		    		}
		    		catch(NumberFormatException e){		    	
		    			valid = false;
		    		}
		    		
		    		
		    		if(valid)
		    		{
		    			PropertySettings.this.alphaSlider.setValue(value);
		    		}else
		    		{
		    			PropertySettings.this.alphaSlider.setValue(PropertySettings.this.MAX_COLOR);
		    		}
		            	
		            	
	            }
            	
            	return true;
	          }
            
		});
		
		
		this.rotation_field.addListener(new InputListener() {
            @Override
            public boolean keyTyped (InputEvent event, char character) {
            	boolean valid = true;
    
            	if(character == '\r')
            	{
		    		String text = PropertySettings.this.rotation_field.getText();
		    		int value = 0;
		    		
		    		try {
		    			 value = (int)Float.parseFloat(text);
		    			 if(value > PropertySettings.this.MAX_ROTATION || value < 0) valid = false;
		    		}
		    		catch(NumberFormatException e){		    	
		    			valid = false;
		    		}
		    		
		    		
		    		if(valid)
		    		{
		    			PropertySettings.this.rotationSlider.setValue(value);
		    		}else
		    		{
		    			PropertySettings.this.rotationSlider.setValue(PropertySettings.this.MAX_ROTATION);
		    		}
		            	
		            	
	            }
            	
            	return true;
	          }
            
		});
		
		this.sizeSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PropertySettings.this.size_field.setText(""+PropertySettings.this.sizeSlider.getValue());
			}
		});
		
		
		this.redSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PropertySettings.this.red_field.setText(""+PropertySettings.this.redSlider.getValue());
			}
		});
		
		
		this.greenSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PropertySettings.this.green_field.setText(""+PropertySettings.this.greenSlider.getValue());
			}
		});
		
		
		this.blueSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PropertySettings.this.blue_field.setText(""+PropertySettings.this.blueSlider.getValue());
			}
		});
		
		
		this.alphaSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PropertySettings.this.alpha_field.setText(""+PropertySettings.this.alphaSlider.getValue());
			}
		});
		
		
		this.rotationSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PropertySettings.this.rotation_field.setText(""+PropertySettings.this.rotationSlider.getValue());
			}
		});
		
		this.col_nonMoveable.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(PropertySettings.this.col_nonMoveable.isChecked())
				{
					PropertySettings.this.inactivateOtherCheckboxes(PropertySettings.this.col_nonMoveable.getZIndex());
					PropertySettings.this.onCollisionSetting = PropertySettings.this.STRING_NON_MOVEABLE;
				}
			}
			
		});
		
		this.col_moveable.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(PropertySettings.this.col_moveable.isChecked())
				{
					PropertySettings.this.inactivateOtherCheckboxes(PropertySettings.this.col_moveable.getZIndex());
					PropertySettings.this.onCollisionSetting = PropertySettings.this.STRING_MOVEABLE;
				}
			}
			
		});
		
		
		this.col_destroy.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(PropertySettings.this.col_destroy.isChecked())
				{
					PropertySettings.this.inactivateOtherCheckboxes(PropertySettings.this.col_destroy.getZIndex());
					PropertySettings.this.onCollisionSetting = PropertySettings.this.STRING_DESTROY;
				}
			}
			
		});
		
		
		this.col_nothing.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(PropertySettings.this.col_nothing.isChecked())
				{
					PropertySettings.this.inactivateOtherCheckboxes(PropertySettings.this.col_nothing.getZIndex());
					PropertySettings.this.onCollisionSetting = PropertySettings.this.STRING_NOTHING;
				}
			}
			
		});
		
		this.col_hurtPlayer.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(PropertySettings.this.col_hurtPlayer.isChecked())
				{
					PropertySettings.this.inactivateOtherCheckboxes(PropertySettings.this.col_hurtPlayer.getZIndex());
					PropertySettings.this.onCollisionSetting = PropertySettings.this.STRING_HURT_PLAYER;
				}
			}
			
		});
		
		
		this.saveLevelButton.addListener(new ClickListener() {
			
			public void clicked(InputEvent event, float x, float y)
			 {
				if(PropertySettings.this.readyToLoadLevel) PropertySettings.this.readyToLoadLevel = false;
				PropertySettings.this.readyToSaveLevel = true;
				PropertySettings.this.savePanel.show();
			 }
			
			
		});
		
		this.loadLevelButton.addListener(new ClickListener() {
			
			public void clicked(InputEvent event, float x, float y)
			 {
				if(PropertySettings.this.readyToSaveLevel) PropertySettings.this.readyToSaveLevel = false;
				PropertySettings.this.readyToLoadLevel = true;
				PropertySettings.this.loadPanel.show();
			 }
			
			
		});
		
	}
	
	private void inactivateOtherCheckboxes(int z)
	{
		if(this.col_nonMoveable.getZIndex() != z)
			this.col_nonMoveable.setChecked(false);
		
		if(this.col_moveable.getZIndex() != z)
			this.col_moveable.setChecked(false);
		
		if(this.col_destroy.getZIndex() != z)
			this.col_destroy.setChecked(false);
		
		if(this.col_nothing.getZIndex() != z)
			this.col_nothing.setChecked(false);

		if(this.col_hurtPlayer.getZIndex() != z)
			this.col_hurtPlayer.setChecked(false);
	}
	
	public void displayPanel()
	{	
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		this.shapeRenderer.setProjectionMatrix(this.stage.getCamera().combined);
		this.shapeRenderer.begin(ShapeType.Filled);
		this.shapeRenderer.setColor(EditPanel.PANEL_COLOR);
		this.shapeRenderer.rect(0.f, 0.f, this.propertyPanelWidth, Core.WINDOW_HEIGHT);
		this.savePanel.drawPanel(this.shapeRenderer);
		this.loadPanel.drawPanel(this.shapeRenderer);
		this.shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	private void activateTheCorrectOnCollisionBox(String setting)
	{
		this.inactivateOtherCheckboxes(-1);
		
		if(setting.equalsIgnoreCase(this.STRING_NON_MOVEABLE))
		{
			this.col_nonMoveable.setChecked(true);
		}
		
		if(setting.equalsIgnoreCase(this.STRING_MOVEABLE))
		{
			this.col_moveable.setChecked(true);
		}
		
		if(setting.equalsIgnoreCase(this.STRING_DESTROY))
		{
			this.col_destroy.setChecked(true);
		}
		
		if(setting.equalsIgnoreCase(this.STRING_NOTHING))
		{
			this.col_nothing.setChecked(true);
		}
		
		if(setting.equalsIgnoreCase(this.STRING_HURT_PLAYER))
		{
			this.col_hurtPlayer.setChecked(true);
		}
	}
	
	public float getRotation()
	{
		return this.rotationSlider.getValue();
	}
	
	public float getSize()
	{
		return this.sizeSlider.getValue();
	}
	
	public Vector3 getColorRGB(){
		return new Vector3(this.redSlider.getValue() / 255.f,
				this.greenSlider.getValue() / 255.f,
				this.blueSlider.getValue() / 255.f);
	}
	
	public float getAlpha() {
		return this.alphaSlider.getValue() / 255.f;
	}
	public String getOnCollisionSetting()
	{
		return this.onCollisionSetting;
	}
	
	public void setRotation(float rotation)
	{
		this.rotationSlider.setValue(rotation);
		this.rotationLastChecked = rotation;
	}
	// King lil g - same ones
	public void setColor(Color color)
	{
		this.redSlider.setValue(color.r * 255.f);
		this.colorsLastChecked_rgb.x = color.r * 255.f;
		
		this.greenSlider.setValue(color.g * 255.f);
		this.colorsLastChecked_rgb.y = color.g * 255.f;
		
		this.blueSlider.setValue(color.b * 255.f);
		this.colorsLastChecked_rgb.z = color.b * 255.f;
		
		this.alphaSlider.setValue(color.a * 255.f);
		this.alphaLastChecked = color.a * 255.f;
	}
	
	public void setSize(float size)
	{
		this.sizeSlider.setValue(size);
		this.sizeLastChecked = size;
	}
	
	
	public void setOnCollisionSetting(String setting)
	{
		this.onCollisionSetting = setting;
		this.activateTheCorrectOnCollisionBox(setting);
		this.onCollisionSettingLastChecked = setting;
	}
	
	public boolean sliderHasBeenUpdated()
	{
		if(this.rotationLastChecked != this.rotationSlider.getValue())
		{
			this.rotationLastChecked = this.rotationSlider.getValue();
			return true;
		}
		
		return false;
	}
	
	public boolean sizeHasBeenUpdated()
	{
		if(this.sizeLastChecked != this.sizeSlider.getValue())
		{
			this.sizeLastChecked = this.sizeSlider.getValue();
			return true;
		}
		
		return false;
	}
	
	public boolean colorRGBHasBeenUpdated()
	{
		boolean status = false;
		
		if(this.colorsLastChecked_rgb.x != this.redSlider.getValue())
		{
			this.colorsLastChecked_rgb.x = this.redSlider.getValue();
			status = true;
		}
		
		if(this.colorsLastChecked_rgb.y != this.greenSlider.getValue())
		{
			this.colorsLastChecked_rgb.y = this.greenSlider.getValue();
			status = true;
		}
		
		if(this.colorsLastChecked_rgb.z != this.blueSlider.getValue())
		{
			this.colorsLastChecked_rgb.z = this.blueSlider.getValue();
			status = true;
		}
		
		return status;
	}
	
	public boolean alphaHasBeenUpdated()
	{
		if(this.alphaLastChecked != this.alphaSlider.getValue())
		{
			this.alphaLastChecked = this.alphaSlider.getValue();
			return true;
		}
		
		return false;
	}
	
	public void provideNewInfoDueToMassSelectOrNot(boolean isMassSelected)
	{
		this.warningLabel.setVisible(isMassSelected);
	}
	
	public boolean onCollisionSettingHasBeenUpdated()
	{
		if(!this.onCollisionSettingLastChecked.equalsIgnoreCase(this.onCollisionSetting))
		{
			this.onCollisionSettingLastChecked = this.onCollisionSetting;
			return true;
		}
		return false;
	}
	
	public boolean saveLevel()
	{
		return this.readyToSaveLevel;
	}
	
	public boolean loadLevel()
	{
		return this.readyToLoadLevel;
	}
	
	public void setSaveLevel(boolean b)
	{
		this.readyToSaveLevel = b;
	}
	
	public void setLoadLevel(boolean b)
	{
		this.readyToLoadLevel = b;
	}
}
