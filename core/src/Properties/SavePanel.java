package Properties;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import Parsers.ParseToXML;

public class SavePanel {

	private Stage stage;
	private Skin skin;
	private Vector2 panelPosition = new Vector2(0,0);
	private Vector2 panelSize = new Vector2(0,0);
	private ParseToXML parser;
	
	private boolean visible;
	private Label label;
	private Label buildInfo;
	private TextButton save, cancel;
	private TextField fileNameTextField;
	private PropertySettings properties;
	
	public SavePanel(PropertySettings properties, Stage stage, Skin skin)
	{
		this.properties = properties;
		this.stage = stage;
		this.skin = skin;
		
		this.parser = new ParseToXML();
		
		this.panelSize.set(250.f,150.f);
		this.panelPosition.set(stage.getCamera().position.x - this.panelSize.x / 2.f, stage.getCamera().position.y - this.panelSize.y / 2.f);
		
		this.label = new Label("Filename: ", this.skin);
		this.label.setSize(50.f, 30.f);
		this.label.setColor(Color.WHITE);
		this.label.setPosition(this.panelPosition.x + 10.f, this.panelPosition.y + this.panelSize.y /2 + 25.f);
		
		
		this.fileNameTextField = new TextField("", this.skin);
		this.fileNameTextField.setWidth(this.panelSize.x - 20.f);
		this.fileNameTextField.setPosition(this.panelPosition.x + this.panelSize.x / 2.f - this.fileNameTextField.getWidth()/2.f,
				this.panelPosition.y + this.panelSize.y /2 - this.fileNameTextField.getHeight() / 2.f + 15.f);
		
		this.save = new TextButton("Save", this.skin);
		this.save.setWidth(100.f);
		this.save.setPosition(this.panelPosition.x + this.panelSize.x / 2.f - 100.f, this.panelPosition.y + this.save.getHeight() + 25.f);
		
		this.cancel = new TextButton("Cancel", this.skin);
		this.cancel.setWidth(100.f);
		this.cancel.setPosition(this.panelPosition.x + this.panelSize.x / 2.f, this.panelPosition.y + this.cancel.getHeight() + 25.f);
		
		
		this.buildInfo = new Label("Build: ", this.skin);
		this.buildInfo.setSize(50.f, 30.f);
		this.buildInfo.setColor(Color.WHITE);
		this.buildInfo.setPosition(this.panelPosition.x + 10.f, this.panelPosition.y + 0);
		
		this.stage.addActor(this.buildInfo);
		this.stage.addActor(this.label);
		this.stage.addActor(this.fileNameTextField);
		this.stage.addActor(this.save);
		this.stage.addActor(this.cancel);
		this.handeListeners();
		this.hide();
	}
	
	public void hide()
	{
		this.label.setVisible(false);
		this.buildInfo.setVisible(false);
		this.fileNameTextField.setVisible(false);
		this.save.setVisible(false);
		this.cancel.setVisible(false);
		this.visible = false;
		
		this.buildInfo.setText("Build: ");
		this.fileNameTextField.setText("");
		
	}
	
	public void show()
	{
		this.label.setVisible(true);
		this.fileNameTextField.setVisible(true);
		this.buildInfo.setVisible(true);
		this.save.setVisible(true);
		this.cancel.setVisible(true);
		this.visible = true;
	}
	
	private void handeListeners()
	{
		this.save.addListener(new ClickListener() {
			
			public void clicked(InputEvent event, float x, float y)
			{
				String buildInfo = SavePanel.this.parser.Parse(SavePanel.this.fileNameTextField.getText());
				SavePanel.this.buildInfo.setText("Buid: " + buildInfo);
			}
		});
		
		
		this.cancel.addListener(new ClickListener() {
			
			public void clicked(InputEvent event, float x, float y)
			{
				SavePanel.this.hide();
				SavePanel.this.properties.setSaveLevel(false);
			}
		});
	}
	
	public void drawPanel(ShapeRenderer renderer)
	{
		if(this.visible) {
			renderer.rect(this.panelPosition.x,
					this.panelPosition.y,
					this.panelSize.x,
					this.panelSize.y);
		}
	}
	
}
