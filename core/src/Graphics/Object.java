package Graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import Properties.EntityType;

public class Object {

	private String onCollision;
	private Vector2 position;
	private float rotation;
	private float size;
	private Color color;
	private String fileName;
	private TextureRegion region;
	private EntityType type; // To determine if the object that is about to be created is a unique one, e.g player spawn point. There can only be one
	private int textureIDFromTextureTable;
	
	public Object(Texture texture, String fileName, EntityType type)
	{
		this.onCollision = "solid";
		this.rotation = 0.f;
		this.size = 64.f;
		this.region = new TextureRegion(texture);
		this.fileName = fileName;
		this.position = new Vector2(0.f,0.f);
		this.textureIDFromTextureTable = 0;
		this.type = type;
		this.color = new Color(Color.WHITE);
	}


	public Object(Object other) 
	{
		this.setTo(other);
	}


	public int getTextureIDFromTextureTable() {
		return textureIDFromTextureTable;
	}


	public void setTextureIDFromTextureTable(int textureIDFromTextureTable) {
		this.textureIDFromTextureTable = textureIDFromTextureTable;
	}


	public Vector2 getPosition() {
		return position;
	}


	public void setPosition(Vector2 position) {
		this.position = position;
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public String getOnCollision() {
		return onCollision;
	}


	public void setOnCollision(String onCollision) {
		this.onCollision = onCollision;
	}


	public float getRotation() {
		return rotation;
	}


	public void setRotation(float rotation) {
		this.rotation = rotation;
	}


	public float getSize() {
		return size;
	}

	public Color getColor(){
		return this.color;
	}
	
	public void setSize(float size) {
		this.size = size;
	}

	public TextureRegion getTextureRegion() {
		return this.region;
	}


	public void setTexture(Texture texture) {
		this.region.setTexture(texture);
	}

	public void setType(EntityType type){
		this.type = type;
	}
	
	public EntityType getType() {
		if(this.type == null) return EntityType.__error__;
		return this.type;
	}
	
	public void setColorRGB(float r, float g, float b)
	{
		this.color.r = r;
		this.color.g = g;
		this.color.b = b;
		
	}
	
	public void setAlpha(float a)
	{
		this.color.a = a;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((onCollision == null) ? 0 : onCollision.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + Float.floatToIntBits(rotation);
		result = prime * result + Float.floatToIntBits(size);
		result = prime * result + textureIDFromTextureTable;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}


	@Override
	public boolean equals(java.lang.Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Object other = (Object) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (onCollision == null) {
			if (other.onCollision != null)
				return false;
		} else if (!onCollision.equals(other.onCollision))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (Float.floatToIntBits(rotation) != Float.floatToIntBits(other.rotation))
			return false;
		if (Float.floatToIntBits(size) != Float.floatToIntBits(other.size))
			return false;
		if (textureIDFromTextureTable != other.textureIDFromTextureTable)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	public void setTo(Object other)
	{
		this.onCollision = other.onCollision;
		this.rotation = other.rotation;
		this.size = other.size;
		this.region = other.region;
		this.fileName = other.fileName;
		this.position = other.position;
		this.textureIDFromTextureTable = other.textureIDFromTextureTable;
		this.type = other.type;
		this.color = new Color(other.color);
	}
	
}
