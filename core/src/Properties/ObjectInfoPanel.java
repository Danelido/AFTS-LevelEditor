package Properties;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import EditorPanel.EditPanel;
import Graphics.Object;


public class ObjectInfoPanel {

	private boolean massSelect_is_active = false;
	private String infoString = "";
	private BitmapFont font;
	
	public static Vector2 panelPosition = new Vector2(0,0);
	public static Vector2 panelSize = new Vector2(0,0);
	
	
	
	public ObjectInfoPanel(){
		this.standardString();
		
		panelSize.set(EntityTable.panelSize.x, 250.f);
		panelPosition.set(EntityTable.panelPosition.x, EntityTable.panelPosition.y - this.panelSize.y);
		this.font = new BitmapFont();
	}
	
	private void standardString()
	{
		infoString = 
				"Collision Setting:" + "\n-\n" +
				"Position:"  + "\n-\n" +
				"Rotation:"  + "\n-\n" +
				"Size:"  + "\n-\n" +
				"Color:"  + "\n-\n" +
				"Type:" + "\n-"; 
	}	
	
	public void drawPanel(ShapeRenderer renderer)
	{
		renderer.setColor(EditPanel.PANEL_COLOR);
		renderer.rect(this.panelPosition.x, this.panelPosition.y,
				this.panelSize.x, this.panelSize.y);
	}
	
	public void drawText(SpriteBatch batchRenderer)
	{
			batchRenderer.setColor(Color.WHITE);
			font.draw(batchRenderer, this.infoString, 
					this.panelPosition.x + 10.f, this.panelPosition.y + this.panelSize.y - 25.f);
	}
	
	public void displayInfo(Object object)
	{
		if(this.massSelect_is_active || object == null) 
		{
			this.standardString();
			return;
		}
		
		infoString = 
				"Collision Setting:\n" 	+ object.getOnCollision() 			+ "\n" +
				"Position:\n" 			+ String.format("%.0f",object.getPosition().x) + ", " + String.format("%.0f",object.getPosition().y) 	+ "\n" +
				"Rotation:\n" 			+ object.getRotation() 				+ "\n" +
				"Size:\n" 				+ object.getSize() 					+ "\n" +
				"Color:\n" 				+ object.getColor().toString() 		+ "\n" +
				"Type:\n" 				+ object.getType().toString(); 
		
	}
	
	public void setMassSelect(boolean b)
	{
		this.massSelect_is_active = b;
	}
	
	
	
}
