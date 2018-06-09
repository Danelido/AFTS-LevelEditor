package Properties;

import com.badlogic.gdx.graphics.Texture;

public class EntityObject {

	private int ID;
	private String textureTableName;
	private String textureFileName;
	private Texture texture;
	private EntityType type;
	
	public EntityObject(Texture texture, String filename, String tableName, EntityType type)
	{
		this.textureTableName = tableName;
		this.textureFileName = filename;
		this.texture = texture;
		this.type = type;
	}

	public int getID() {
		return ID;
	}

	public void setID(int id)
	{
		this.ID = id;
	}
	
	public String getTextureName() {
		return textureTableName;
	}
	
	public String getTextureFileName()
	{
		return this.textureFileName;
	}

	public Texture getTexture() {
		return texture;
	}
	
	public EntityType getType()
	{
		return this.type;
	}
	
	
}
